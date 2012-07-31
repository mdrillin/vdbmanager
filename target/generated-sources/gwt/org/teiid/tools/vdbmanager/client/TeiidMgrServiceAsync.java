package org.teiid.tools.vdbmanager.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface TeiidMgrServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void isRunningOnOpenShift( AsyncCallback<java.lang.Boolean> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void initApplication( int serverPort, java.lang.String userName, java.lang.String password, AsyncCallback<java.util.List<java.lang.String>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void getDynamicVDBNames( AsyncCallback<java.util.List<java.lang.String>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void getDataSourceTemplates( AsyncCallback<java.util.List<java.lang.String>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void getPropertyNames( java.lang.String templateName, AsyncCallback<java.util.List<java.lang.String>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void getPropertyDefns( java.lang.String templateName, AsyncCallback<java.util.List<org.teiid.tools.vdbmanager.client.PropertyObj>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void getTranslatorNames( AsyncCallback<java.util.List<java.lang.String>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void createVDB( java.lang.String vdbName, AsyncCallback<java.util.List<java.lang.String>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void deleteVDB( java.lang.String vdbName, AsyncCallback<java.util.List<java.lang.String>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void getVDBModelInfo( java.lang.String vdbName, AsyncCallback<java.util.List<java.util.List<org.teiid.tools.vdbmanager.client.DataItem>>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void removeModels( java.lang.String vdbName, java.util.List<java.lang.String> removeModelNameList, AsyncCallback<java.util.List<java.util.List<org.teiid.tools.vdbmanager.client.DataItem>>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void addSourceAndModel( java.lang.String vdbName, java.lang.String sourceName, java.lang.String templateName, java.lang.String translatorName, java.util.Map<java.lang.String,java.lang.String> propsMap, AsyncCallback<java.lang.String> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see org.teiid.tools.vdbmanager.client.TeiidMgrService
     */
    void addViewModel( java.lang.String vdbName, java.lang.String viewModelName, java.util.Map<java.lang.String,java.lang.String> propsMap, AsyncCallback<java.lang.String> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static TeiidMgrServiceAsync instance;

        public static final TeiidMgrServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (TeiidMgrServiceAsync) GWT.create( TeiidMgrService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "teiid" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
