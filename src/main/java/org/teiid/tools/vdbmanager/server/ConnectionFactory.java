package org.teiid.tools.vdbmanager.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminFactory;

/**
 */
public class ConnectionFactory {
	
	private static final String WRAPPER_DS = "org.jboss.resource.adapter.jdbc.WrapperDataSource"; //$NON-NLS-1$
	private static final String WRAPPER_DS_NAMEPREFIX = "java:"; //$NON-NLS-1$
	private static final String WRAPPER_DS_AS7 = "org.jboss.jca.adapters.jdbc.WrapperDataSource"; //$NON-NLS-1$
	private static final String WRAPPER_DS_NAMEPREFIX_AS7 = "java:/"; //$NON-NLS-1$
    private static final String JDBC_PREFIX = "java:/";
    private static final String TEIID_DRIVER_PREFIX = "teiid";
    
	private static ConnectionFactory sInstance = null;
	
	private Map<String,DataSource> mDatasources = new TreeMap<String,DataSource>();
	private Map<String,String> mDatasourceSchemas = new TreeMap<String,String>();

	/*
	 * InitDataSources.   Initializes the lists of all Datasources and Datasource Schemas
	 */
	private void initDataSources(Context context, String prefix) throws Exception {
        Context jdbcContext = (Context) context.lookup(prefix);
        NamingEnumeration<javax.naming.NameClassPair> ne  = jdbcContext.list("");
         while (ne.hasMoreElements()) {
             javax.naming.NameClassPair o = (javax.naming.NameClassPair) ne.nextElement();
             Object bindingObject = null;
             
             if (o.getClassName().equals(WRAPPER_DS)) {
                 bindingObject = context.lookup(WRAPPER_DS_NAMEPREFIX + o.getName());
             } else if(o.getClassName().equals(WRAPPER_DS_AS7)) {
                 bindingObject = context.lookup(WRAPPER_DS_NAMEPREFIX_AS7 + o.getName());
             }
             if(bindingObject!=null && bindingObject instanceof DataSource) {
            	 // Put DataSource into datasource Map
                 String key = prefix.concat(o.getName());
                 mDatasources.put(key, (DataSource)bindingObject);
                 
                 // Put Schema into schema Map
                 String schema = null;
                 try {
                   schema = (String) context.lookup("java:comp/env/schema/" + key);
                 } catch (NamingException e) {
                	 
                 }
         		 mDatasourceSchemas.put(key, schema);
             }
         }
        
	}

	/*
	 * Constructor
	 */
	private ConnectionFactory () throws Exception {
		InitialContext initialContext = new InitialContext();
		initDataSources (initialContext, JDBC_PREFIX);
	}
	
	public static ConnectionFactory getInstance() throws Exception {
		if (sInstance == null)  {
			synchronized (ConnectionFactory.class) {
				if (sInstance == null)  {
					sInstance = new ConnectionFactory();
				}
			}
		}
		return sInstance;
	}
	
	/*
	 * Initialize the Admin API
	 */
	public Admin initAdminApi (String serverHost, int serverPort, String userName, String password) throws Exception {
		Admin admin = null;
		try {
			admin = AdminFactory.getInstance().createAdmin(serverHost, serverPort, userName, password.toCharArray());
		} catch (Exception e) {
			throw new Exception(e.getMessage());
		}
		if(admin==null) {
			StringBuffer sb = new StringBuffer("Unable to establish Admin API connection.  Please check the supplied credentials: \n");
			sb.append("\n [Host]: "+serverHost);
			sb.append("\n [Port]: "+serverPort);
			
			throw new Exception(sb.toString());
		}
		return admin;
	}
	
	/*
	 * Get Connection for the specified DataSource Name
	 */
	public Connection getConnection (String datasourceName) throws SQLException {
		Connection connection = null;
		DataSource dataSource = (DataSource) mDatasources.get(datasourceName);
		if(dataSource!=null) {
			connection = dataSource.getConnection();
		}
		return connection;
	}
	
	/*
	 * Get Datasource Schema for the specified DataSource Name
	 */
	public String getDataSourceSchema (String datasourceName) throws SQLException {
		return (String) mDatasourceSchemas.get(datasourceName);
	}
	
	/*
	 * Get List of all available Datasource Names
	 */
	public String[] getAllDataSourceNames(boolean teiidOnly) {
		try {
			InitialContext initialContext = new InitialContext();
			initDataSources (initialContext, JDBC_PREFIX);
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> resultList = new ArrayList<String>();
		
		Set<String> dsNames = mDatasources.keySet();
		Iterator<String> nameIter = dsNames.iterator();
		while(nameIter.hasNext()) {
			String dsName = nameIter.next();
			DataSource ds = mDatasources.get(dsName);
			if(!teiidOnly) {
				resultList.add(dsName);
			} else if(isTeiidSource(ds)) {
				resultList.add(dsName);
			}
		}
		String[] resArray = new String[resultList.size()];
		int i=0;
		for(String name: resultList) {
			resArray[i] = name;
			i++;
		}
		return resArray;
	}
	
	public boolean isTeiidSource(DataSource dataSource) {
		boolean isVdb = false;
		Connection conn = null;
		if(dataSource!=null) {
			try {
				conn = dataSource.getConnection();
				if(conn!=null) {
					String driverName = conn.getMetaData().getDriverName();
					if(driverName!=null && driverName.trim().toLowerCase().startsWith(TEIID_DRIVER_PREFIX)) {
						isVdb = true;
					}
				}
			} catch (SQLException e) {

			} finally {
				if(conn!=null) {
					try {
						conn.close();
					} catch (SQLException e) {

					}
				}
			}
		}
		return isVdb;
	}

}
