package org.teiid.tools.vdbmanager.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.teiid.adminapi.Admin;
import org.teiid.adminapi.AdminException;
import org.teiid.adminapi.Model;
import org.teiid.adminapi.PropertyDefinition;
import org.teiid.adminapi.Translator;
import org.teiid.adminapi.VDB;
import org.teiid.adminapi.VDB.Status;
import org.teiid.adminapi.impl.ModelMetaData;
import org.teiid.adminapi.impl.VDBMetaData;
import org.teiid.adminapi.impl.VDBMetadataParser;
import org.teiid.tools.vdbmanager.client.DataItem;
import org.teiid.tools.vdbmanager.client.PropertyObj;
import org.teiid.tools.vdbmanager.client.TeiidMgrService;
import org.teiid.tools.vdbmanager.client.TeiidServiceException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TeiidMgrServiceImpl extends RemoteServiceServlet implements
TeiidMgrService {

	private static final String DDL_KEY = "Views-DDL";
	private static final String LOCALHOST = "localhost";
	private static final String CLASSNAME_KEY = "class-name";
    private static final String CONN_FACTORY_CLASS_KEY = "managedconnectionfactory-class"; 
	
	ConnectionFactory connectionFactory;
	Admin admin = null;

	/*
	 * (non-Javadoc)
	 * @see org.teiid.webapp.client.TeiidService#isRunningOnOpenShift( )
	 */
	public Boolean isRunningOnOpenShift( ) {
		String openShiftAppName = System.getenv("OPENSHIFT_APP_NAME");
		if(openShiftAppName!=null && openShiftAppName.trim().length()>0) {
			return new Boolean(true);
		}
		return new Boolean(false);	
	}

	/*
	 * (non-Javadoc)
	 * @see org.teiid.webapp.client.TeiidService#initApplication(int, java.lang.String, java.lang.String, java.lang.String)
	 */
	public List<String> initApplication(int serverPort, String userName, String password) throws TeiidServiceException {
		String serverHost = LOCALHOST;
		
		// First priority is use OpenShift - if running on OpenShift.
		// Try both the JBOSSEAP and JBOSSAS system vars
		String serverIP = System.getenv("OPENSHIFT_JBOSSEAP_IP");
		if(serverIP==null || serverIP.trim().isEmpty()) {
			serverIP = System.getenv("OPENSHIFT_JBOSSAS_IP");
		}
		if(serverIP==null || serverIP.trim().isEmpty()) {
			// Lookup the server ip address for the server this is running on.
			try {
				serverIP=InetAddress.getLocalHost().getHostAddress();
			} catch (Exception e) {
				// Nothing here
			}
		}
				
		
		// If the server Address was found, override the default 'localhost'
		if(serverIP!=null) {
			serverHost = serverIP;
		}
		// Init the Admin API
		try {
			initAdminApi(serverHost,serverPort,userName,password);
		} catch (Exception e) {
			throw new TeiidServiceException(e.getMessage());
		}
		
		return getDynamicVDBNames();
	}

	/*
	 * Init the Admin API.
	 * @param serverHost the server host name
	 * @param serverPort the admin port
	 * @param userName the user name
	 * @param password the user password
	 */
	private void initAdminApi(String serverHost, int serverPort, String userName, String password) throws Exception {
		this.admin = org.teiid.tools.vdbmanager.server.ConnectionFactory.getInstance().initAdminApi(serverHost,serverPort,userName,password);
	}
	
	/*
	 * Get the current list of Dynamic VDBs
	 * @return the list of Dynamic VDBs
	 */
	public List<String> getDynamicVDBNames() throws TeiidServiceException {
		List<String> vdbNames = new ArrayList<String>();
		if(this.admin==null) return vdbNames;
		
		// Get list of VDBS - get vdbName VDB (if already deployed)
		Collection<? extends VDB> vdbs = null;
		try {
			vdbs = this.admin.getVDBs();
		} catch (AdminException e) {
		    throw new TeiidServiceException(e.getMessage());
		}
		
		// Dont show the Preview VDBs
		for(VDB vdb : vdbs) {
			VDBMetaData vdbMeta = (VDBMetaData)vdb;
			String vdbName = vdbMeta.getName();
			if(vdbName!=null && !vdbName.startsWith("PREVIEW_")) {
				vdbNames.add(vdbName);
			}
		}

		Collections.sort(vdbNames);
		
		return vdbNames;
	}
	
	/*
	 * Get the current list of Templates
	 * @return the list of Templates
	 */
	public List<String> getDataSourceTemplates() throws TeiidServiceException {
		List<String> templateNames = new ArrayList<String>();
		if(this.admin==null) return templateNames;
		
		// Get list of DataSource Template Names
		Collection<String> dsTemplates = null;
		try {
			dsTemplates = (Collection<String>) this.admin.getDataSourceTemplateNames();
		} catch (AdminException e) {
			throw new TeiidServiceException(e.getMessage());
		}
		
		// Filter out un-wanted names
		for(String template : dsTemplates) {
			if(template!=null && !template.endsWith(".war")) {
				templateNames.add(template);
			}
		}

		// Sort the list
		Collections.sort(templateNames);

		return templateNames;
	}
	
	/*
	 * Get a Map of the current Templates to their PropertyObjects
	 * @return the Map of dsTemplate to List of PropertyObjects
	 */
	public Map<String,List<PropertyObj>> getDSPropertyObjMap() throws TeiidServiceException {
		// Define Map to hold the results
		Map<String,List<PropertyObj>> resultMap = new HashMap<String,List<PropertyObj>>();
		
		// Get all DataSource Template names
		List<String> templateNames = getDataSourceTemplates();
		
		// For each DataSource, get the properties then populate the resultMap. 
		for(String template: templateNames) {
			List<PropertyObj> propDefns = getPropertyDefns(template);
			resultMap.put(template, propDefns);
		}
		
		return resultMap;
	}
	
	
	/*
	 * Get the current list of Translator names
	 * @return the list of Translator names
	 */
	public List<String> getTranslatorNames() throws TeiidServiceException {
		List<String> transNames = new ArrayList<String>();
		if(this.admin==null) return transNames;
		
		// Get list of DataSource Template Names
		Collection<? extends Translator> translators = null;
		try {
			translators = this.admin.getTranslators();
		} catch (AdminException e) {
			throw new TeiidServiceException(e.getMessage());
		}
		
		// Filter out un-wanted names
		for(Translator translator : translators) {
			if(translator!=null) {
				transNames.add(translator.getName());
			}
		}

		// Sort the list
		Collections.sort(transNames);

		return transNames;
	}

	/*
	 * Create the Dynamic VDB - if it is not already deployed
	 * @param vdbName name of the VDB to create
	 * @return the new List of VDB names
	 */
	public List<String> createVDB(String vdbName) throws TeiidServiceException {
		List<String> vdbNames = new ArrayList<String>();
		
 		if(this.admin==null) return vdbNames;
 		
 		try {
 			// Get list of VDBS - get vdbName VDB (if already deployed)
 			Collection<? extends VDB> vdbs = this.admin.getVDBs();

 			VDBMetaData workVDBMetaData = null;
 			for(VDB vdb : vdbs) {
 				// Bug workaround (the name may be null).  Only one VDB, so use - if it exists...
 				if(vdb.getName()==null || vdb.getName().equalsIgnoreCase(vdbName)) {
 					workVDBMetaData = (VDBMetaData)vdb;
 					workVDBMetaData.setName(vdbName);
 				}
 			}

 			// Only deploy the VDB if it was not found
 			if(workVDBMetaData==null) {
 				// Deployment name for vdb must end in '-vdb.xml'
 				String deploymentName = vdbName+"-vdb.xml";

 				// Deploy the VDB
 				String deployString = createNewDeployment(vdbName,1);
 				this.admin.deploy(deploymentName,new ByteArrayInputStream(deployString.getBytes("UTF-8")));

 				waitForVDBLoad(this.admin, vdbName, 1, 120);

 				// Add the VDB Source.  If it exists, it is deleted first - then re-added.
 				addVDBSource(vdbName);
 			}
 		} catch (Exception e) {
 			throw new TeiidServiceException(e.getMessage());
 		}
		// Return the new list of VDB names
		return getDynamicVDBNames();
	}
	
//	private VDBMetaData createNewVDB(String vdbName, int vdbVersion) {
//		VDBMetaData vdbMetaData = new VDBMetaData();
//		vdbMetaData.setName(vdbName);
//		vdbMetaData.setVersion(vdbVersion);
//		return vdbMetaData;
//	}

	/*
	 * Delete the Dynamic VDB - undeploy it, then delete the source
	 * @param vdbName name of the VDB to delete
	 * @return the new List of VDB names
	 */
	public List<String> deleteVDB(String vdbName) throws TeiidServiceException {
		// Deployment name for dynamic vdb ends in '-vdb.xml'
		String vdbDeployName = vdbName+"-vdb.xml";
		
		try {
			// Undeploy the working VDB
			admin.undeploy(vdbDeployName);

			// Delete the VDB Source
			deleteSource(vdbName);
		} catch (Exception e) {
			throw new TeiidServiceException(e.getMessage());
		}
		// Return the new list of VDB names
		return getDynamicVDBNames();
	}
	
	/*
	 * Create a new, blank deployment for the provided vdbName and version
	 * @param vdbName name of the VDB
	 * @param vdbVersion the VDB version
	 * @return the VDB deployment string
	 */
	private String createNewDeployment(String vdbName, int vdbVersion) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
		sb.append("<vdb name=\""+vdbName+"\" version=\""+vdbVersion+"\">");
		sb.append("<description>A Dynamic VDB</description>");
        sb.append("<property name=\"UseConnectorMetadata\" value=\"true\" />");
        sb.append("<property name=\"{http://teiid.org/rest}auto-generate\" value=\"true\" />");
        sb.append("<property name=\"{http://teiid.org/rest}security-type\" value=\"none\" />");
        sb.append("<translator name=\"rest\" type=\"ws\" description=\"Rest override\">");
        sb.append("<property name=\"DefaultBinding\" value=\"HTTP\"/>");
        sb.append("<property name=\"DefaultServiceMode\" value=\"MESSAGE\"/>");
        sb.append("</translator>");
		sb.append("</vdb>");
		return sb.toString();
	}

	/*
	 * Helper method - waits for the VDB to finish loading
	 * @param admin the admin api instance
	 * @param vdbName the name of the VDB
	 * @param vdbVersion the VDB version
	 * @param timeoutInSecs time to wait before timeout
	 * @return 'true' if vdb found and is out of 'Loading' status, 'false' otherwise.
	 */
	
	private boolean waitForVDBLoad(Admin admin, String vdbName, int vdbVersion, int timeoutInSecs) {
		long waitUntil = System.currentTimeMillis() + timeoutInSecs*1000;
		if (timeoutInSecs < 0) {
			waitUntil = Long.MAX_VALUE;
		}
		boolean first = true;
		do {
			// Pause 5 sec before subsequent attempts
			if (!first) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					break;
				}
			} else {
				first = false;
			}
			// Get the VDB using admin API
			VDBMetaData vdbMetaData = null;
			try {
				vdbMetaData = (VDBMetaData)admin.getVDB(vdbName, vdbVersion);
			} catch (AdminException e) {
			}
			// Determine if VDB is loading, or whether to wait
			if(vdbMetaData!=null) {
				Status vdbStatus = vdbMetaData.getStatus();
				// return if no models in VDB, or VDB has errors (done loading)
				if(vdbMetaData.getModels().isEmpty() || vdbStatus==Status.FAILED || vdbStatus==Status.REMOVED || vdbStatus==Status.ACTIVE) {
					return true;
				}
				// If the VDB Status is LOADING, but a validity error was found - return
				if(vdbStatus==Status.LOADING && !vdbMetaData.getValidityErrors().isEmpty()) {
					return true;
				}
			}
		} while (System.currentTimeMillis() < waitUntil);
		return false;
	}

	/*
	 * Get ModelInfo for the models in the specified VDB
	 * @param vdbName name of the VDB
	 * @return the List of ModelInfo data
	 */
	public List<List<DataItem>> getVDBModelInfo(String vdbName) throws TeiidServiceException {
		List<List<DataItem>> rowList = new ArrayList<List<DataItem>>();
		
		// Get list of VDBS - get the named VDB
		Collection<? extends VDB> vdbs = null;
		try {
			vdbs = admin.getVDBs();
		} catch (AdminException e) {
			throw new TeiidServiceException(e.getMessage());
		}
		VDBMetaData workVDBMetaData = null;
		for(VDB vdb : vdbs) {
			VDBMetaData vdbMeta = (VDBMetaData)vdb;
			if(vdbMeta.getName()!=null && vdbMeta.getName().equalsIgnoreCase(vdbName)) {
				workVDBMetaData = vdbMeta;
			}
		}

		// VDB Status Item
		DataItem vdbStatusItem = null;
		
		// Add a model to the vdb, then re-deploy it.
		if(workVDBMetaData!=null) {
			// First row of result is VDB Status
			VDB.Status status = workVDBMetaData.getStatus();
			// Change FAILED or REMOVED status to INACTIVE
			String vdbStatus = status.toString();
			if(vdbStatus!=null && (vdbStatus.equalsIgnoreCase("FAILED")||vdbStatus.equalsIgnoreCase("REMOVED"))) {
				vdbStatus="INACTIVE";
			}
			List<DataItem> vdbStatusInfo = new ArrayList<DataItem>();
			vdbStatusItem = new DataItem(vdbStatus,"string");
			vdbStatusInfo.add(vdbStatusItem);
			rowList.add(vdbStatusInfo);

			// Second row of result is Model Header Info
			List<DataItem> headerInfo = new ArrayList<DataItem>();
			headerInfo.add(new DataItem("Model Name","string"));
			headerInfo.add(new DataItem("Model Type","string"));
			headerInfo.add(new DataItem("Translator","string"));
			headerInfo.add(new DataItem("JNDI source","string"));
			headerInfo.add(new DataItem("Status","string"));
			rowList.add(headerInfo);

			// Subsequent Rows - one row for each model
			List<Model> models = workVDBMetaData.getModels();
			
			for(Model model: models) {
				ModelMetaData modelMeta = (ModelMetaData)model;
				String modelName = modelMeta.getName();
				String modelType = modelMeta.getModelType().toString();
				String jndiName = null;
				String translatorName = null;
				String modelStatus = null;

				// Virtual Model, use placeholders for jndiName and translatorName
				if(modelType.equals(Model.Type.VIRTUAL.toString())) {
					jndiName = "-----";
					translatorName = "teiid";
					// Physical Model, get source info 
				} else {
					List<String> sourceNames = modelMeta.getSourceNames();
					for(String sourceName: sourceNames) {
						jndiName = modelMeta.getSourceConnectionJndiName(sourceName);
						translatorName = modelMeta.getSourceTranslatorName(sourceName);
					}
				}
				
				
				List<String> errors = modelMeta.getValidityErrors();
				if(errors.size()==0) {
					modelStatus = "ACTIVE";
				} else {
					// There may be multiple errors - process the list...
					boolean connectionError = false;
					boolean validationError = false;
					boolean isLoading = false;
					// Iterate Errors and set status flags
					for(String error: errors) {
						if(error.indexOf("TEIID11009")!=-1 || error.indexOf("TEIID60000")!=-1 || error.indexOf("TEIID31097")!=-1) {
							connectionError=true;
						} else if(error.indexOf("TEIID31080")!=-1 || error.indexOf("TEIID31071")!=-1) {
							validationError=true;
						} else if(error.indexOf("TEIID50029")!=-1) {
							isLoading=true;
						}
					}
					// --------------------------------------------------
					// Set model status string according to errors found
					// --------------------------------------------------
					// Connection Error.  Reset the VDB overall status, as it may say loading
					if(connectionError) {
						modelStatus = "INACTIVE: Data Source connection failed...";
						if(vdbStatusItem!=null && "LOADING".equalsIgnoreCase(vdbStatusItem.getData())) {
							vdbStatusItem.setData("INACTIVE");
						}
					// Validation Error with View SQL
					} else if(validationError) {
						modelStatus = "INACTIVE: Validation Error with SQL";
					// Loading in progress
					} else if(isLoading) {
						modelStatus = "INACTIVE: Metadata loading in progress...";
					// Unknown - use generic message
					} else {
						modelStatus = "INACTIVE: unknown source issue";
					}
				}
				List<DataItem> modelInfo = new ArrayList<DataItem>();
				modelInfo.add(new DataItem(modelName,"string"));
				modelInfo.add(new DataItem(modelType,"string"));
				modelInfo.add(new DataItem(translatorName,"string"));
				modelInfo.add(new DataItem(jndiName,"string"));
				modelInfo.add(new DataItem(modelStatus,"string"));
				rowList.add(modelInfo);
			}
		}
		
		return rowList;
	}
	
	/*
	 * Removes the models from the supplied VDB deployment - if they exist.  Redeploys the VDB after
	 * models are removed.
	 * @param vdbName name of the VDB
	 * @param removeModelNameList the list of model names to remove
	 * @return the List of ModelInfo data
	 */
	public List<List<DataItem>> removeModels(String vdbName, List<String> removeModelNameList) throws TeiidServiceException {		
		// Get list of VDBS - get the named VDB
		Collection<? extends VDB> vdbs = null;
		
		try {
			vdbs = admin.getVDBs();
			VDBMetaData workVDBMetaData = null;
			for(VDB vdb : vdbs) {
				if(vdb.getName()==null || vdb.getName().equalsIgnoreCase(vdbName)) {
					workVDBMetaData = (VDBMetaData)vdb;
					workVDBMetaData.setName(vdbName);
				}
			}

			VDBMetaData newVDB = null;

			// Re-configure the VDB Models, remove the supplied list if present
			if(workVDBMetaData!=null) {
				List<Model> currentModels = workVDBMetaData.getModels();
				List<ModelMetaData> newModels = new ArrayList<ModelMetaData>();
				List<ModelMetaData> removeModels = new ArrayList<ModelMetaData>();
				for(Model model: currentModels) {
					String currentName = model.getName();
					// Keep the model - unless its in the remove list
					if(!removeModelNameList.contains(currentName)) {
						newModels.add((ModelMetaData)model);
					} else {
						removeModels.add((ModelMetaData)model);
					}
				}
				// Remove Data Sources related to the models being removed
				for(ModelMetaData model: removeModels) {
					List<String> srcsToDelete = model.getSourceNames();
					for(String src: srcsToDelete) {
						deleteSource(src);
					}
				}
				// Now reset the Models on the VDB to those being kept
				newVDB = new VDBMetaData();
				newVDB.setName(vdbName);
				newVDB.setModels(newModels);
			}

			// Re-Deploy the VDB
			redeployVDB(vdbName, newVDB);
		} catch (Exception e) {
			throw new TeiidServiceException(e.getMessage());
		}
		// Return the Model Info
		return getVDBModelInfo(vdbName);
	}
	
	/*
	 * Adds the models to the supplied VDB deployment.  Redeploys the VDB after the models are added.
	 * @param vdbName name of the VDB
	 * @param sourceName the name of the source to add
	 * @param templateName the name of the template for the source
	 * @param translatorName the name of the translator for the source
	 * @param sourcePropMap the map of property values for the specified source
	 * @return the List of ModelInfo data
	 */
	public String addSourceAndModel(String vdbName, 
			                        String sourceName, String templateName, String translatorName, 
			                        Map<String,String> sourcePropMap ) throws TeiidServiceException {

		try {
			// Create the dataSource
			addSource(sourceName,templateName,sourcePropMap);

			// Add Model to the VDB - and Re-Deploy it.
			addSourceModelToVDB(vdbName, sourceName, translatorName);
		} catch (Exception e) {
			throw new TeiidServiceException(e.getMessage());
		}
		return "Success";
	}
	
	/*
	 * Adds the view model to the supplied VDB deployment.  Redeploys the VDB after the model is added.
	 * @param vdbName name of the VDB
	 * @param viewModelName the name of the view to add
	 * @param sourcePropMap the map of property values for the specified source.  This must include 
	 * the DDL property-value for the view model.
	 * @return the List of ModelInfo data
	 */
	public String addViewModel(String vdbName, String viewModelName, 
			                        Map<String,String> sourcePropMap ) throws TeiidServiceException {

		// Get the DDL String
		String ddlString = sourcePropMap.get(DDL_KEY);
		
		// Add View Model to the VDB - and Re-Deploy it.
		try {
			addViewModelToVDB(vdbName, viewModelName, ddlString);
		} catch (Exception e) {
			throw new TeiidServiceException(e.getMessage());
		}
		
		return "Success";
	}
	
	public List<String> getPropertyNames(String templateName) throws TeiidServiceException {
		List<String> propNames = new ArrayList<String>();
		if(this.admin!=null && templateName!=null && !templateName.trim().isEmpty()) {
			Collection<? extends PropertyDefinition> propDefnList = null;
			try {
				propDefnList = this.admin.getTemplatePropertyDefinitions(templateName);
			} catch (AdminException e) {
				throw new TeiidServiceException(e.getMessage());
			}
			for(PropertyDefinition propDefn: propDefnList) {
				if(propDefn.isRequired()) {
					String name = propDefn.getName();
					propNames.add(name);
				}
			}
		}
		return propNames;
	}
	
	public List<PropertyObj> getPropertyDefns(String templateName) throws TeiidServiceException {
		List<PropertyObj> propDefns = new ArrayList<PropertyObj>();
		if(this.admin!=null && templateName!=null && !templateName.trim().isEmpty()) {
			Collection<? extends PropertyDefinition> propDefnList = null;
			try {
				propDefnList = this.admin.getTemplatePropertyDefinitions(templateName);
			} catch (AdminException e) {
				throw new TeiidServiceException("["+templateName+"] "+e.getMessage());
			}
			String managedConnFactoryClass = getManagedConnectionFactoryClassDefault(propDefnList);
			
			for(PropertyDefinition propDefn: propDefnList) {
				PropertyObj pDefn = new PropertyObj();
                // ------------------------
				// Set PropertyObj fields
                // ------------------------
				// Name
				String name = propDefn.getName();
				pDefn.setName(name);
				// DisplayName
				String displayName = propDefn.getDisplayName();
				pDefn.setDisplayName(displayName);
				// isModifiable
				boolean isModifiable = propDefn.isModifiable();
				pDefn.setModifiable(isModifiable);
				// isRequired
				boolean isRequired = propDefn.isRequired();
				pDefn.setRequired(isRequired);
				// defaultValue
				Object defaultValue = propDefn.getDefaultValue();
				if(defaultValue!=null) {
					pDefn.setValue(defaultValue.toString());
					pDefn.setDefault(defaultValue.toString());
				}
				
	            // Copy the 'managedconnectionfactory-class' default value into the 'class-name' default value
	            if(name.equals(CLASSNAME_KEY)) {
	            	pDefn.setValue(managedConnFactoryClass);
	            	pDefn.setRequired(true);
	            }
                // ------------------------
				// Add PropertyObj to List
                // ------------------------
				propDefns.add(pDefn);
			}
		}
		return propDefns;
	}
	
    /*
     * Get the default value for the Managed ConnectionFactory class
     * @param propDefns the collection of property definitions
     * @return default value of the ManagedConnectionFactory, null if not found.
     */
    private String getManagedConnectionFactoryClassDefault (Collection<? extends PropertyDefinition> propDefns) {
        String resultValue = null;
        for(PropertyDefinition pDefn : propDefns) {
            if(pDefn.getName().equalsIgnoreCase(CONN_FACTORY_CLASS_KEY)) {
                resultValue=(String)pDefn.getDefaultValue();
                break;
            }
        }
        return resultValue;
    }

    /*
	 * Create the specified VDB "teiid-local" source on the server.  If it already exists, delete it first.
	 * @param vdbName the name of the VDB for the connection
	 */
	private void addVDBSource(String vdbName) throws Exception {
		if(this.admin!=null) {
			// Define Datasource properties to expose VDB as a source
			Map<String,String> propMap = new HashMap<String,String>();
			propMap.put("connection-url","jdbc:teiid:"+vdbName+";useJDBC4ColumnNameAndLabelSemantics=false");
			propMap.put("user-name","user");
			propMap.put("password","user");
			
			// Create the datasource (deletes first, if it already exists)
			String vdbSourceName = vdbName;
			addSource(vdbSourceName, "teiid-local", propMap );
		}
	}

	/*
	 * Create the specified source on the server.  If it already exists, delete it first - then redeploy
	 * @param sourceName the name of the source to add
	 * @param templateName the name of the template for the source
	 * @param sourcePropMap the map of property values for the specified source
	 */
	private void addSource(String sourceName, String templateName, Map<String,String> sourcePropMap) throws Exception {
		if(this.admin!=null) {
			// If 'sourceName' already exists - delete it first...
			deleteSource(sourceName);

			// Get properties for the source
			Properties sourceProps = getPropsFromMap(sourcePropMap);

			// Create the specified datasource
			admin.createDataSource(sourceName,templateName,sourceProps);
		}
	}
	
	/*
	 * Delete the specified source on the server. 
	 * @param sourceName the name of the source to add
	 */
	private void deleteSource(String sourceName) throws Exception {
		if(this.admin!=null) {
			// Get list of DataSource Names.  If 'sourceName' is found, delete it...
			Collection<String> dsNames = admin.getDataSourceNames();
			Iterator<String> iter = dsNames.iterator();
			while(iter.hasNext()) {
				String name = iter.next();
				if(name!=null && name.trim().equalsIgnoreCase(sourceName)) {
					admin.deleteDataSource(name.trim());
				}
			}
		}
	}

	/*
	 * Convert the Map of property key-value pairs to Properties object
	 * @param propMap the Map of property key-value pairs
	 * @return the corresponding Properties object
	 */
	private Properties getPropsFromMap(Map<String,String> propMap) {
		Properties sourceProps = new Properties();
		Iterator<String> keyIter = propMap.keySet().iterator();
		while(keyIter.hasNext()) {
			String key = keyIter.next();
			String value = propMap.get(key);
			sourceProps.setProperty(key, value);
		}
		return sourceProps;
	}
	
	/*
	 * Add a Model to the VDB for the specified source and translator type.
	 * The VDB is then re-deployed 
	 * @param vdbName name of the VDB
	 * @param sourceName the name of the source to add
	 * @param translatorName the name of the translator for the source
	 */
	private void addSourceModelToVDB(String vdbName, String sourceName, String translatorName) throws Exception {
		if(admin!=null) {
			// Get the VDB
			Collection<? extends VDB> vdbs = admin.getVDBs();
			VDBMetaData workVDBMetaData = null;
			for(VDB vdb : vdbs) {
				if(vdb.getName()==null || vdb.getName().equalsIgnoreCase(vdbName)) {
					workVDBMetaData = (VDBMetaData)vdb;
					workVDBMetaData.setName(vdbName);
				}
			}

			// Add a model to the vdb, then re-deploy it.
			if(workVDBMetaData!=null) {
				ModelMetaData modelMetaData = new ModelMetaData();
				modelMetaData.addSourceMapping(sourceName, translatorName, "java:/"+sourceName);
				modelMetaData.setName(sourceName+"Model");

				workVDBMetaData.addModel(modelMetaData);

				// Re-Deploy the VDB
				redeployVDB(vdbName, workVDBMetaData);
			}
		}
	}
	
	/*
	 * Add a View Model to the VDB for the specified viewName.
	 * The VDB is then re-deployed 
	 * @param vdbName name of the VDB
	 * @param viewModelName the name of the viewModel to add
	 * @param ddlString the DDL string to use for the view model
	 */
	private void addViewModelToVDB(String vdbName, String viewModelName, String ddlString) throws Exception {
		if(admin!=null) {
			// Get the VDB
			Collection<? extends VDB> vdbs = admin.getVDBs();
			VDBMetaData workVDBMetaData = null;
			for(VDB vdb : vdbs) {
				if(vdb.getName()==null || vdb.getName().equalsIgnoreCase(vdbName)) {
					workVDBMetaData = (VDBMetaData)vdb;
					workVDBMetaData.setName(vdbName);
				}
			}

			// Add a model to the vdb, then re-deploy it.
			if(workVDBMetaData!=null) {
				ModelMetaData modelMetaData = new ModelMetaData();
				modelMetaData.setName(viewModelName);
				modelMetaData.setModelType(Model.Type.VIRTUAL);
				modelMetaData.setSchemaSourceType("DDL");
				if(ddlString==null) {
					System.out.println("DDL Text for "+viewModelName+" is NULL!");
				}
				modelMetaData.setSchemaText(ddlString);

				workVDBMetaData.addModel(modelMetaData);

				// Re-Deploy the VDB
				redeployVDB(vdbName, workVDBMetaData);
			}
		}
	}
	
	/*
	 * Undeploy the current deployed VDB, re-deploy the supplied VDBMetadata, then define
	 * the VDB as a Source
	 * @param vdbName name of the VDB
	 * @param vdb the VDBMetaData object
	 */
	private void redeployVDB(String vdbName, VDBMetaData vdb) throws Exception {
		// redeploy the VDB using the supplied deployment name
		if(admin!=null && vdb!=null) {
			
			// Before attempting to redeploy the VDB, 
			// Make sure that any Model Validity errors are cleared.
			VDBMetaData deploymentVDB = removeValidationErrors(vdb);
					
			// For debug purposes
			//save("/home/userdir/testVDBs/dynamic-vdb.xml",deploymentVDB);
			
			// output using VDBMetadataParser
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			VDBMetadataParser.marshell(deploymentVDB, out);
			
			// Deployment name for vdb must end in '-vdb.xml'
			String vdbDeployName = vdbName+"-vdb.xml";
			// Undeploy the working VDB
			admin.undeploy(vdbDeployName);

			// Deploy the updated VDB
			admin.deploy(vdbDeployName, new ByteArrayInputStream(out.toByteArray()));
			
			// Wait for VDB to finish loading
			waitForVDBLoad(this.admin, vdbName, 1, 120);

			// Add the VDB as a source.  If it already exists, it is deleted first then recreated.
			// Re-create is required to clear the connection pool.
			addVDBSource(vdbName);
		}
	}
	
	private VDBMetaData removeValidationErrors(VDBMetaData vdb) {
		// If no VDB errors, its safe to deploy
		if(!vdb.hasErrors()) {
			return vdb;
		}
		
		// If it has errors, clone the models out errors
		if(vdb.hasErrors()) {
			// Get current models
			List<Model> models = vdb.getModels();
			
			// New list for scrubbed models
			List<ModelMetaData> scrubbedModels = new ArrayList<ModelMetaData>();
			
			// Go thru the current models
			for(Model model: models) {
				List<ModelMetaData.Message> validationErrors = ((ModelMetaData)model).getMessages(false);
				if(validationErrors.isEmpty()) {
					scrubbedModels.add((ModelMetaData)model);
				} else {
					ModelMetaData modelMeta = copyWithoutErrors((ModelMetaData)model);
					scrubbedModels.add(modelMeta);
				}
			}
			
			// Reset the models on the VDB
			vdb.setModels(scrubbedModels);
		}
		return vdb;
	}
	
	private ModelMetaData copyWithoutErrors(ModelMetaData model) {
		ModelMetaData resultModel = new ModelMetaData();
		resultModel.setName(model.getName());
		resultModel.setSourceMappings(model.getSourceMappings());
		resultModel.setModelType(model.getModelType());
		resultModel.setSchemaSourceType(model.getSchemaSourceType());
		resultModel.setSchemaText(model.getSchemaText());
		return resultModel;
	}
	
    public boolean save(String filePath, VDBMetaData vdb) throws Exception {
        File outFile = new File(filePath);
        FileOutputStream outStream = new FileOutputStream(outFile);
        VDBMetadataParser.marshell(vdb, outStream);
        return true;
    }
	
}
