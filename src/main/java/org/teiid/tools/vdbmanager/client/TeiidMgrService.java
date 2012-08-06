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

	  Boolean isRunningOnOpenShift() throws Exception;
	  
	  List<String> initApplication(int serverPort, String userName, String password) throws Exception;
	  
	  List<String> getDynamicVDBNames() throws Exception;

	  List<String> getDataSourceTemplates() throws Exception;
		
	  Map<String,List<PropertyObj>> getDSPropertyObjMap() throws Exception;

	  List<String> getPropertyNames(String templateName) throws Exception;
	  
	  List<PropertyObj> getPropertyDefns(String templateName) throws Exception;
	  
	  List<String> getTranslatorNames() throws Exception;

	  List<String> createVDB(String vdbName) throws Exception;

	  List<String> deleteVDB(String vdbName) throws Exception;
	  
	  List<List<DataItem>> getVDBModelInfo(String vdbName) throws Exception;

	  List<List<DataItem>> removeModels(String vdbName, List<String> removeModelNameList) throws Exception;		

	  String addSourceAndModel(String vdbName, String sourceName, String templateName, String translatorName, Map<String,String> propsMap ) throws Exception;

	  String addViewModel(String vdbName, String viewModelName, Map<String,String> propsMap ) throws Exception;
	    
}
