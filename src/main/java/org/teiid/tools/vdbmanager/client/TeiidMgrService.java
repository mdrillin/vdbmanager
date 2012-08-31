package org.teiid.tools.vdbmanager.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("teiid")
public interface TeiidMgrService extends RemoteService {

	  Boolean isRunningOnOpenShift();
	  
	  List<String> initApplication(int serverPort, String userName, String password) throws TeiidServiceException;
	  
	  List<String> getDynamicVDBNames() throws TeiidServiceException;

	  List<String> getDataSourceTemplates() throws TeiidServiceException;
		
	  Map<String,List<PropertyObj>> getDSPropertyObjMap() throws TeiidServiceException;

	  List<String> getPropertyNames(String templateName) throws TeiidServiceException;
	  
	  List<PropertyObj> getPropertyDefns(String templateName) throws TeiidServiceException;
	  
	  List<String> getTranslatorNames() throws TeiidServiceException;

	  List<String> createVDB(String vdbName) throws TeiidServiceException;

	  List<String> deleteVDB(String vdbName) throws TeiidServiceException;
	  
	  List<List<DataItem>> getVDBModelInfo(String vdbName) throws TeiidServiceException;

	  List<List<DataItem>> removeModels(String vdbName, List<String> removeModelNameList) throws TeiidServiceException;		

	  String addSourceAndModel(String vdbName, String sourceName, String templateName, String translatorName, Map<String,String> propsMap ) throws TeiidServiceException;

	  String addViewModel(String vdbName, String viewModelName, Map<String,String> propsMap ) throws TeiidServiceException;
	    
}
