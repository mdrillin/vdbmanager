package org.teiid.tools.vdbmanager.client;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teiid.tools.vdbmanager.client.events.SourcesChangedEvent;
import org.teiid.tools.vdbmanager.client.events.SourcesChangedEventHandler;
import org.teiid.tools.vdbmanager.client.events.VDBRedeployEvent;
import org.teiid.tools.vdbmanager.client.events.VDBRedeployEventHandler;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class vdbmanager implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String INIT_APP_ERROR = "An error occurred while "
		+ "attempting to initialize the application ";
	private static final String GET_VDB_ERROR = "An error occurred while "
		+ "attempting to retrieve the VDB list. ";
	private static final String CREATE_VDB_ERROR = "An error occurred while "
		+ "attempting to create the VDB. ";
	private static final String DELETE_VDB_ERROR = "An error occurred while "
		+ "attempting to delete the VDB. ";
	private static final String REMOVE_MODEL_ERROR = "An error occurred while "
		+ "attempting to remove model(s). ";
	private static final String REFRESH_TABLE_ERROR = "An error occurred while "
		+ "attempting to refresh the sources table. ";

	// 'No VDBs' ListBox entry
	private static final String NO_VDBS = "None Available";
	
	// 'RunningOnOpenShift' flag
    private boolean isRunningOnOpenShift = false;

	/**
	 * Create a remote service proxy to talk to the server-side Teiid service.
	 */
	private final TeiidMgrServiceAsync teiidMgrService = GWT.create(TeiidMgrService.class);

	//private final Messages messages = GWT.create(Messages.class);

	// Title for the Main Page
	private Label titleLabel = new Label("Teiid Dynamic VDB Manager");
    // Link for Examples Panel
	private HTMLPanel examplesLinkPanel = new HTMLPanel(getExamplesLinkHtml());
	
	// VDB Selection and Management controls
	private Label vdbSelectionLabel = new Label("Selected VDB: ");
	private ListBox vdbSelectionListBox = new ListBox();
	private Button newVDBButton = new Button("New...");
	private Button deleteVDBButton = new Button("Delete");
	private Button refreshVDBButton = new Button("Refresh VDB");
	private Button apiLoginButton = new Button("Login to API");
	
	// VDB Sources Table and Table Header
	private Label vdbSourcesTableLabel = new Label();
	private Label vdbStatusLabel = new Label();
	private FlexTable vdbSourcesTable = new FlexTable();

    // Add, ReDeploy and Delete Sources Buttons
	private Button deleteSourceButton = new Button("Delete Source");
	private Button addSourceDialogButton = new Button("Add...");
	private Button editSourceDialogButton = new Button("Edit...");
	
	// New VDB Dialog Controls
	private DialogBox appLoginDialogBox = new DialogBox();
	private Button appLoginDialogOKButton = new Button("OK");
	private Button appLoginDialogCloseButton = new Button("Cancel");
	private Label appLoginStatusLabel = new Label();
	private TextBox adminPortTextBox = new TextBox();
	private TextBox adminUsernameTextBox = new TextBox();
	private PasswordTextBox adminPasswordTextBox = new PasswordTextBox();

	// New VDB Dialog Controls
	private DialogBox newVDBDialogBox = new DialogBox();
	private Button newVDBDialogOKButton = new Button("OK");
	private Button newVDBDialogCloseButton = new Button("Cancel");
	private Label newVDBStatusLabel = new Label();
	private TextBox newVDBNameTextBox = new TextBox();

	// Delete VDB Dialog Controls
	private DialogBox deleteVDBDialogBox = new DialogBox();
	private Button deleteVDBDialogOKButton = new Button("OK");
	private Button deleteVDBDialogCloseButton = new Button("Cancel");

	// Delete Source Dialog Controls
	private DialogBox deleteSourceDialogBox = new DialogBox();
	private Button deleteSourceDialogOKButton = new Button("OK");
	private Button deleteSourceDialogCloseButton = new Button("Cancel");

	// Error Dialog Controls
	private DialogBox errorDialogBox = new DialogBox();
	private Button errorDialogCloseButton = new Button("Cancel");
	private HTML serverResponseLabel = new HTML();
	
	// Maintain list of VDB Names - for checking on create
	private List<String> currentVDBNames = new ArrayList<String>();
	
	// Event Bus for communication between components
	private final SimpleEventBus eventBus = new SimpleEventBus();
	// Maps to save Properties and Translator when sources are added.
	private Map<String,Map<String,String>> savedNamePropertyMap = new HashMap<String,Map<String,String>>(); 
    private Map<String,String> savedNameTranslatorMap = new HashMap<String,String>();
    
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		// Makes server call to see if it's openShift
		determineIfOpenShift();
		
	}

	/*
	 * Init the UI components.  This is executed in the onSuccess of determineIfOpenShift - we want
	 * that server call to return before setting up the components...
	 */
	private void initUI() {
		// ==============================================
		// Initialize connection and VDB deployment Info
		// ==============================================
		int adminPort = 9999;
		String userName = "admin";
		String password = "admin";

		// ---------------------------------------
		// Main Page Widgets
		// ---------------------------------------

		// -------------------------
		// Application Title Label
		// -------------------------
		titleLabel.addStyleName("applicationTitle");
		RootPanel.get("titleLabelContainer").add(titleLabel);
		
		// ----------------------------------
		// VDB Management Controls
		// ----------------------------------
		// Change Listener for VDB Selection ListBox
		vdbSelectionListBox.addChangeHandler(new ChangeHandler()
		{
			// Changing the VDB selection will re-populate the VDB Sources Table
			public void onChange(ChangeEvent event)
			{
				// Refresh the VDB Sources Table
				String vdbName = getSelectedVDBName();
				refreshVDBSourcesTable(vdbName);
				
				// Set the VDB Mgmt Button enabled states
				setVDBMgmtButtonEnablements();
			}
		});

		vdbSelectionLabel.addStyleName("vdbSelectionLabel");
		RootPanel.get("vdbSelectionLabelContainer").add(vdbSelectionLabel);
		RootPanel.get("vdbSelectionListBoxContainer").add(vdbSelectionListBox);

		// Add a handler for New VDB Button
		newVDBButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showNewVDBDialog();
			}
		});
		// Add a handler for Delete VDB Button
		deleteVDBButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showDeleteVDBDialog(getSelectedVDBName());
			}
		});

		RootPanel.get("newVDBButtonContainer").add(newVDBButton);
		RootPanel.get("deleteVDBButtonContainer").add(deleteVDBButton);
		
		// ----------------------------------
		// VDB Source Table Title and Status
		// ----------------------------------
		vdbSourcesTableLabel.addStyleName("vdbSourceTableTitle");
		vdbStatusLabel.addStyleName("vdbSourceTableTitle");
		RootPanel.get("vdbSourcesLabelContainer").add(vdbSourcesTableLabel);
		RootPanel.get("vdbStatusLabelContainer").add(vdbStatusLabel);
		
		// -------------------------
		// Refresh Sources Button
		// -------------------------
		refreshVDBButton.setEnabled(true);
		// Handler for refresh button
		refreshVDBButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				refreshVDBSourcesTable(getSelectedVDBName());
			}
		});
		// -------------------------
		// API Login Button
		// -------------------------
		apiLoginButton.setEnabled(true);
		// Handler for refresh button
		apiLoginButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showAppLoginDialog();
			}
		});
		RootPanel.get("refreshButtonContainer").add(refreshVDBButton);
		// Only include ApiLogin Button when not on OpenShift
		if(!isRunningOnOpenShift) {
			RootPanel.get("apiLoginButtonContainer").add(apiLoginButton);
		}
		RootPanel.get("examplesLinkContainer").add(examplesLinkPanel);

		// --------------------
		// VDB Sources Table
		// --------------------
		vdbSourcesTable.addStyleName("vdbSourcesTable");
		RootPanel.get("vdbSourcesTableContainer").add(vdbSourcesTable);

		// ----------------------------------------------
		// VDB Source Buttons - Delete, Add, Re-Deploy
		// ----------------------------------------------
		
		// Initial state of Delete Button is disabled.  It will enable if at least one source row is selected.
		deleteSourceButton.setEnabled(false);
		// Add a handler to close the DialogBox
		deleteSourceButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				showDeleteSourceDialog();
			}
		});
		
		// Initial state of Add Button is enabled.  It will remain enabled.
		addSourceDialogButton.setEnabled(true);
		// Add a handler to close the DialogBox
		addSourceDialogButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Init the Create Source Dialog
				CreateEditSourceDialog createDialog = new CreateEditSourceDialog(eventBus,savedNamePropertyMap,savedNameTranslatorMap,isRunningOnOpenShift,teiidMgrService);
				// Show Add Source Dialog
				createDialog.showDialogForAdd(getSelectedVDBName(), getVDBTableSourceNames(false), examplesLinkPanel);
			}
		});
				
		// Initial state of Re-deploy Button is disabled.  It will enable if a source row is selected.
		editSourceDialogButton.setEnabled(false);
		// Add a handler to close the DialogBox
		editSourceDialogButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Init the Create Source DialogshowErrorDialog
				CreateEditSourceDialog createDialog = new CreateEditSourceDialog(eventBus,savedNamePropertyMap,savedNameTranslatorMap,isRunningOnOpenShift,teiidMgrService);
				// Show Edit Source Dialog
				String sourceName = getSourceNameForFirstSelectedRow();
				String sourceType = getSourceTypeForFirstSelectedRow();
				createDialog.showDialogForEdit(getSelectedVDBName(), sourceName, sourceType, examplesLinkPanel);
			}
		});
		
		RootPanel.get("deleteSourcesButtonContainer").add(deleteSourceButton);
		RootPanel.get("addSourceDialogButtonContainer").add(addSourceDialogButton);
		RootPanel.get("redeploySourceDialogButtonContainer").add(editSourceDialogButton);
		
		// ---------------------------------------
		// Widgets for Delete Source Dialog
		// ---------------------------------------

		// Click Handler for DialogBox Close Button
		deleteSourceDialogCloseButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				deleteSourceDialogBox.hide();
			}
		});
		
		// Click Handler for DialogBox OK Button
		deleteSourceDialogOKButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				List<String> checkedSrcs = new ArrayList<String>();
	            for (int i = 1, n = vdbSourcesTable.getRowCount(); i < n; i++) {
	                CheckBox box = (CheckBox) vdbSourcesTable.getWidget(i, 0);
                    String modelName = vdbSourcesTable.getText(i, 1);
                    if(box.getValue()) {
                    	checkedSrcs.add(modelName);
                    }
	            }
				deleteSelectedSources(getSelectedVDBName(),checkedSrcs);

				deleteSourceDialogBox.hide();
			}
		});

		// ---------------------------------------
		// Widgets for App Login Dialog
		// ---------------------------------------

		// Change Listener for New VDB Name TextBox - does property validation
		adminPortTextBox.addKeyUpHandler(new KeyUpHandler() {
	        @Override
	        public void onKeyUp(KeyUpEvent event) {
	        	//setNewVDBDialogOKButtonEnablement();
	        }
	    });
		
		// Click Handler for DialogBox Close Button
		appLoginDialogCloseButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				appLoginDialogBox.hide();
				apiLoginButton.setEnabled(true);
			}
		});
		
		// Click Handler for DialogBox OK Button
		appLoginDialogOKButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Get admin info from the dialog widgets
				String adminPort = adminPortTextBox.getText();
				String adminUser = adminUsernameTextBox.getText();
				String adminPass = adminPasswordTextBox.getText();
			    // Init Application 
				// - connects to AdminAPI
				// - Populates the VDB ListBox
				initApplication(Integer.parseInt(adminPort), adminUser, adminPass);

				appLoginDialogBox.hide();
			}
		});
		
		// ---------------------------------------
		// Widgets for New VDB Dialog
		// ---------------------------------------

		// Change Listener for New VDB Name TextBox - does property validation
		newVDBNameTextBox.addKeyUpHandler(new KeyUpHandler() {
	        @Override
	        public void onKeyUp(KeyUpEvent event) {
	        	setNewVDBDialogOKButtonEnablement();
	        }
	    });
		
		// Click Handler for DialogBox Close Button
		newVDBDialogCloseButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				newVDBDialogBox.hide();
			}
		});
		
		// Click Handler for DialogBox OK Button
		newVDBDialogOKButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Get VDB name from the dialog widgets
				String vdbName = newVDBNameTextBox.getText();
				createVDB(vdbName);
				newVDBDialogBox.hide();
			}
		});
		
		// ---------------------------------------
		// Widgets for Delete VDB Dialog
		// ---------------------------------------

		// Click Handler for DialogBox Close Button
		deleteVDBDialogCloseButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				deleteVDBDialogBox.hide();
			}
		});
		
		// Click Handler for DialogBox OK Button
		deleteVDBDialogOKButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String vdbName = getSelectedVDBName();
				if(vdbName!=null && !vdbName.equalsIgnoreCase(NO_VDBS)) {
					deleteVDB(vdbName);
				}
				deleteVDBDialogBox.hide();
			}
		});

		// Init Error Dialog for later use.
		initErrorDialog();
		
		// Show Login if not on OpenShift
		if(!isRunningOnOpenShift) {
			showAppLoginDialog();
		// OpenShift assumes port 9999, admin, admin
		} else {
			initApplication(adminPort, userName, password);
		}
		
	    // Listen for SourcesChangedEvent from Add Source Dialog, so we can update the sources table.
		eventBus.addHandler(SourcesChangedEvent.TYPE, new SourcesChangedEventHandler() {
			public void onEvent(SourcesChangedEvent event) {
				refreshVDBSourcesTable(getSelectedVDBName());
			}
		});
		
	    // Listen for VDBRedeployEvent from Add Source Dialog, so we can update the UI
		eventBus.addHandler(VDBRedeployEvent.TYPE, new VDBRedeployEventHandler() {
			public void onEvent(VDBRedeployEvent event) {
				setUIStatusForVDBRedeploy();
			}
		});
		
	}
	
	/*
	 * HTML for link to the Source Examples Page
	 * @return HTML for example page link
	 */
	private String getExamplesLinkHtml() {
		return "<p>Go to: <a href='https://community.jboss.org/docs/DOC-18405' target='_blank'>Teiid Source Examples</a></p>";
	}

	/*
	 * Get the selected VDB from the VDB ListBox
	 * @return the currently selected VDB name
	 */
	private String getSelectedVDBName() {
		int selectedIndex = this.vdbSelectionListBox.getSelectedIndex();
		return this.vdbSelectionListBox.getValue(selectedIndex);
	}
	
	/*
	 * Return the list of SourceNames from the VDB Sources table.
	 * @param onlySelected if 'true', only the selected source names are returned.  if 'false', all source names are returned.
	 * @return the list of VDB Model Names
	 */
	private List<String> getVDBTableSourceNames(boolean onlySelected) {
		List<String> sourceNames = new ArrayList<String>();
        for (int i = 1, n = vdbSourcesTable.getRowCount(); i < n; i++) {
            CheckBox box = (CheckBox) vdbSourcesTable.getWidget(i, 0);
            String modelName = vdbSourcesTable.getText(i, 1);
            String sourceName = getSourceNameForModel(modelName);
            if(!onlySelected) {
            	sourceNames.add(sourceName);
            } else if(box.getValue()) {
            	sourceNames.add(sourceName);
            }
        }
        return sourceNames;
	}
	
	/*
	 * Get the VDB Source Name for the first selected row in the VDB Sources Table.
	 * @return the DataSource Name, null if nothing is selected
	 */
	private String getSourceNameForFirstSelectedRow() {
		String modelName = null;
		String modelType = null;
        for (int i = 1, n = vdbSourcesTable.getRowCount(); i < n; i++) {
            CheckBox box = (CheckBox) vdbSourcesTable.getWidget(i, 0);
            if(box.getValue()) {
            	modelName = vdbSourcesTable.getText(i,1);
            	modelType = vdbSourcesTable.getText(i,2);
            	break;
            }
        }
        // Virtual Models, use modelName as is
        if(modelType!=null && modelType.equalsIgnoreCase("VIRTUAL")) {
        	return modelName;
        }
    	return getSourceNameForModel(modelName);
	}
	
	/*
	 * Get the source name from the provided model name.  The model name is derived from the
	 * source name - 'Model' added as a suffix.
	 * @param modelName the model name
	 * @return the source name
	 */
	private String getSourceNameForModel(String modelName) {
		// Source name is derived from the modelname
		String sourceName = null;
		if(modelName!=null && modelName.endsWith("Model")) {
			sourceName=modelName.substring(0, modelName.length()-5);
		}	
		return sourceName;
	}
	
	/*
	 * Get the VDB Source type name for the first selected row in the VDB Sources Table.
	 * This is based on the translator name since type is not explicitly available.
	 * @return the DataSource translator name, null if nothing is selected
	 */
	private String getSourceTypeForFirstSelectedRow() {
		String translatorName = null;
        for (int i = 1, n = vdbSourcesTable.getRowCount(); i < n; i++) {
            CheckBox box = (CheckBox) vdbSourcesTable.getWidget(i, 0);
            if(box.getValue()) {
            	translatorName = vdbSourcesTable.getText(i,3);
            	break;
            }
        }
        
        // Get source type which corresponds to translator
		return DataSourceHelper.getSourceTypeForTranslator(translatorName);
	}
	
	/*
	 * Set the VDB Mgmt Button Enablement States.  This includes the NewVDB, DeleteVDB and
	 * RefreshVDB buttons
	 */
	private void setVDBMgmtButtonEnablements() {
		// New VDB Button is always enabled
		newVDBButton.setEnabled(true);
		
		// Delete and Refresh Buttons - disabled if NO_VDBS
		deleteVDBButton.setEnabled(true);
		refreshVDBButton.setEnabled(true);
		String selectedVDB = getSelectedVDBName();
		if(selectedVDB==null || selectedVDB.equalsIgnoreCase(NO_VDBS)) {
			deleteVDBButton.setEnabled(false);
			refreshVDBButton.setEnabled(false);
		} 
	}
	
	/*
	 * Set the Source Mgmt Button Enablement States.  This includes the DeleteSource, AddSource, RedeploySource
	 * Buttons.
	 */
	private void setSourceMgmtButtonEnablements() {
		// Get the list of selected table rows
		List<String> selectedModels = getVDBTableSourceNames(true);

		// DeleteSourceButton - If anything is checked, the button is enabled
		if(selectedModels.size()>0) {
			deleteSourceButton.setEnabled(true);
		} else {
			deleteSourceButton.setEnabled(false);
		}
		
		// RedeploySourceButton - If one row is checked, the button is enabled
		if(selectedModels.size()==1) {
			editSourceDialogButton.setEnabled(true);
		} else {
			editSourceDialogButton.setEnabled(false);
		}
		
		String selectedVDB = getSelectedVDBName();
		if(selectedVDB==null || selectedVDB.equalsIgnoreCase(NO_VDBS)) {
			addSourceDialogButton.setEnabled(false);
		} else {
			addSourceDialogButton.setEnabled(true);
		}
		
	}
	
	/*
	 * Determine the OK Button enablement on the New VDB Dialog.  The OK button
	 * will only enable if the Name is valid and is not a duplicate.
	 */
	private void setNewVDBDialogOKButtonEnablement( ) {
		boolean okButtonEnabled = false;
		
		// Validate the entered VDB Name
		if(validateVDBName()) {
			okButtonEnabled = true;
		}
		
		// Set enabled state of OK button - disables for invalid or duplicate name
		newVDBDialogOKButton.setEnabled(okButtonEnabled);
	}
	
	/*
	 * Validate the VDB Name for a new VDB.  Checks for valid characters and checks against current list for duplicates.
	 * @return 'true' if the entered name is OK, 'false' if not OK.
	 */
	private boolean validateVDBName() {
		boolean statusOK = true;
		String statusStr = "OK";
		
		// Validate the entered VDB name
		String newVDBName = newVDBNameTextBox.getText();
		if(newVDBName==null || newVDBName.trim().length()==0) {
			statusStr = "Please enter a name for the VDB";
			statusOK = false;
		}
		
		// Check entered name against existing names
		if(statusOK) {
			if(this.currentVDBNames.contains(newVDBName)) {
				statusStr = "A VDB with this name already exists";
				statusOK = false;
			}
		}
		
		// Check for valid characters
		if(statusOK) {
			boolean allValidChars = true;
			String str = newVDBName.trim();
			for (int i = 0; i < str.length(); i++){
			    char c = str.charAt(i);
			    if( (!Character.isLetterOrDigit(c) && c!='-' && c!='_') || c==' ') {
			    	allValidChars = false;
			    	break;
			    }
			}
			if(!allValidChars) {
				statusStr = "The VDB name contains invalid character(s)";
				statusOK = false;
			}
		}
		
		// Update the status label
		
		if(!statusStr.equals("OK")) {
			newVDBStatusLabel.setText(statusStr);
		} else {
			newVDBStatusLabel.setText("Click OK to accept");
		}
		
		return statusOK;
	}

	/*
	 * Initialized the application.
	 * @param serverPort the server management port number
	 * @param userName the username
	 * @param password the password
	 */
	private void initApplication(int serverPort, String userName, String password) {
		// Set up the callback object.
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				showErrorDialog("Application Init Error", INIT_APP_ERROR+":<br/><br/>"+caught.getMessage()+"<br/>");
				apiLoginButton.setEnabled(true);
			}

			// On Success - Populate the ListBox with Datasource Names
			public void onSuccess(List<String> vdbNames) {
				populateVDBListBox(vdbNames,null);
				apiLoginButton.setEnabled(true);
			}
		};

		// Make the Remote Server call to init the ListBox
		teiidMgrService.initApplication(serverPort, userName, password, callback);
	}
	
	/*
	 * Makes server call to determine if OpenShift
	 */
	private void determineIfOpenShift() {
		// Set up the callback object.
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				isRunningOnOpenShift=false;
				initUI();
			}

			// On Success - Populate the ListBox with Datasource Names
			public void onSuccess(Boolean isOpenShift) {
				isRunningOnOpenShift=isOpenShift.booleanValue();
				initUI();
			}
		};

		// Make the Remote Server call to init the ListBox
		teiidMgrService.isRunningOnOpenShift(callback);
	}
		
	/*
	 * Populates the available VDBs ListBox
	 * @vdbList the list of VDB Names to re-populate the ListBox
	 * @vdbToSelect the name of the VDB to select initially
	 */
	private void populateVDBListBox(List<String> vdbList, String vdbToSelect) {
		// Refresh list of current VDB names
		currentVDBNames.clear();
		currentVDBNames.addAll(vdbList);
		
		// If nothing in the list, add a placeholder NO_VDBS
		if(vdbList.isEmpty()) {
			vdbList.add(NO_VDBS);
		}
		
		// Clear current list
		vdbSelectionListBox.clear();
		
		int selectionIndx = 0;
		
		// Repopulate the list box
		int i = 0;
		for(String vdbName: vdbList) {
			vdbSelectionListBox.insertItem(vdbName, i);
			if(vdbName!=null && vdbName.equalsIgnoreCase(vdbToSelect)) selectionIndx = i;
			i++;
		}
		vdbSelectionListBox.setSelectedIndex(selectionIndx);
		DomEvent.fireNativeEvent(Document.get().createChangeEvent(),vdbSelectionListBox);				
	}

	/*
	 * Clear the VDB Sources Table, then re-populate it using the supplied rows
	 * @param vdbName the VDB name
	 * @param rowData the List of Row info for re-populating the VDB Sources table
	 */
	private void populateSourcesTable(String vdbName, List<List<DataItem>> rowData) {
		// Clear Previous Results
		clearSourcesTable();

		// Check for NO_VDBS
		if(NO_VDBS.equalsIgnoreCase(vdbName)) {
		    vdbSourcesTableLabel.setText("No Dynamic VDBs are available");
		    vdbStatusLabel.setText("");
		}
		
		// First Row is VDB Status.  Use it in the Table Title
		if(rowData.size()>0) {
			List<DataItem> vdbStatusRow = rowData.get(0);
			DataItem data = (DataItem)vdbStatusRow.get(0);
			vdbSourcesTableLabel.setText("Sources for '"+vdbName+"' -- Status: ");
			String vdbStatus = data.getData();
			// If no models in the VDB, make sure INACTIVE status
			if(rowData.size()==2) {
				vdbStatus = "INACTIVE";
			}
			vdbStatusLabel.setText(vdbStatus);
			if(vdbStatus!=null) {
				if(vdbStatus.trim().equalsIgnoreCase("active")) {
					vdbStatusLabel.removeStyleName("vdbStatusInactive");
					vdbStatusLabel.addStyleName("vdbStatusActive");
				} else {
					vdbStatusLabel.removeStyleName("vdbStatusActive");
					vdbStatusLabel.addStyleName("vdbStatusInactive");
				}
			}
		} else {
			vdbSourcesTable.setText(0,0,"Selected");
			vdbSourcesTable.setText(0,1,"Model Name");
			vdbSourcesTable.setText(0,2,"Model Type");
			vdbSourcesTable.setText(0,3,"Translator");
			vdbSourcesTable.setText(0,4,"JNDI Source");
			vdbSourcesTable.setText(0,5,"Status");
			vdbSourcesTable.getRowFormatter().addStyleName(0, "vdbSourcesTableHeader");
		}
		
		int iRow = 0;
		for(List<DataItem> row: rowData) {
		    // Data Row 0 is VDB Status - skip
			if(iRow==0) {
				iRow++;
				continue;
			}
			
			// Data Row 1 is header Row
			int nCols = row.size();
			if(iRow==1) {
				vdbSourcesTable.setText(iRow-1,0,"Selected");
				vdbSourcesTable.getCellFormatter().addStyleName(iRow-1, 0, "vdbSourcesTableCell");
			} else {
				// First Column is a remove checkbox
				CheckBox rowCheckbox = new CheckBox("");
				rowCheckbox.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						setSourceMgmtButtonEnablements();
					}
				});
				vdbSourcesTable.setWidget(iRow-1, 0, rowCheckbox);
			}
			
			// Columns 2+ contain the returned data
			for(int i=0; i<nCols; i++) {
				DataItem data = (DataItem)row.get(i);
				vdbSourcesTable.setText(iRow-1,i+1,data.getData());
				vdbSourcesTable.getCellFormatter().addStyleName(iRow-1, i+1, "vdbSourcesTableCell");
			}
			
			// Header Row Style
			if(iRow==1) {
				vdbSourcesTable.getRowFormatter().addStyleName(iRow-1, "vdbSourcesTableHeader");
		    // Even Row Style
			} else {
				boolean isEven = (iRow % 2 == 0);
				if(isEven) {
					vdbSourcesTable.getRowFormatter().addStyleName(iRow-1, "vdbSourcesTableEvenRow");
				} else {
					vdbSourcesTable.getRowFormatter().addStyleName(iRow-1, "vdbSourcesTableOddRow");
				}
			}
			iRow++;
		}
		
	}
	
	/*
	 * Deletes the List of sources in the specified VDB
	 * @param vdbName the name of the VDB
	 * @param deleteSrcs the list of sources to delete
	 */
	private void deleteSelectedSources(final String vdbName, List<String> deleteSrcs) {
		// Set up the callback object.
		AsyncCallback<List<List<DataItem>>> callback = new AsyncCallback<List<List<DataItem>>>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				showErrorDialog("Remove Model Error", REMOVE_MODEL_ERROR+caught.getMessage());
				setSourceMgmtButtonEnablements();
			}

			// On Success - Populate the ListBox
			public void onSuccess(List<List<DataItem>> result) {
				populateSourcesTable(vdbName,result);
				setSourceMgmtButtonEnablements();
			}
		};

		// change the UI - the VDB will re-deploy - may take awhile.
		setUIStatusForVDBRedeploy();
		
		teiidMgrService.removeModels(vdbName, deleteSrcs, callback);		
	}
		
	/*
	 * Changes UI status for actions that cause a VDB re-deploy.  Re-deploys
	 * may take a while while metadata is reloading...
	 */
	private void setUIStatusForVDBRedeploy() {
		vdbStatusLabel.removeStyleName("vdbStatusActive");
		vdbStatusLabel.addStyleName("vdbStatusInactive");
		vdbStatusLabel.setText("Re-loading - Please Wait...");
		clearSourcesTable();
	}
	
	/*
	 * Changes UI status for actions that cause a VDB re-deploy.  Re-deploys
	 * may take a while while metadata is reloading...
	 */
	private void setUIStatusForReInit() {
		this.newVDBButton.setEnabled(false);
		this.deleteVDBButton.setEnabled(false);
		this.refreshVDBButton.setEnabled(false);
		this.apiLoginButton.setEnabled(false);
		this.addSourceDialogButton.setEnabled(false);
		this.vdbSelectionLabel.setText("Selected VDB: ");
		this.vdbStatusLabel.setText("");
		
		clearSourcesTable();
		
	    this.vdbSelectionListBox.clear();
	}
	
	/*
	 * Create a VDB
	 * @param vdbName the name of the VDB to create
	 */
	private void createVDB(final String vdbName) {
		// Set up the callback object.
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				showErrorDialog("CreateVDB Error", CREATE_VDB_ERROR+caught.getMessage());
				setSourceMgmtButtonEnablements();
			}

			// On Success - no action.  List Box updates after call.
			public void onSuccess(List<String> vdbNames) {
				populateVDBListBox(vdbNames,vdbName);
			}
		};

		// Creates the VDB
		teiidMgrService.createVDB(vdbName, callback);
	}
	
	/* 
	 * Delete VDB.
	 * @param vdbName the name of the VDB to delete.
	 */
	private void deleteVDB(final String vdbName) {
		// Set up the callback object.
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				showErrorDialog("DeleteVDB Error", DELETE_VDB_ERROR+caught.getMessage());
				setSourceMgmtButtonEnablements();
			}

			// On Success - no action.  List Box updates after call.
			public void onSuccess(List<String> vdbNames) {
				populateVDBListBox(vdbNames,null);
			}
		};

		// Delete the VDB
		teiidMgrService.deleteVDB(vdbName, callback);
	}

	/*
	 * Refresh the VDB source table
	 * @param vdbName the name of the VDB to refresh
	 */
	private void refreshVDBSourcesTable(final String vdbName) {
		// Set up the callback object.
		AsyncCallback<List<List<DataItem>>> callback = new AsyncCallback<List<List<DataItem>>>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				showErrorDialog("Refresh VDB Sources Error", REFRESH_TABLE_ERROR+caught.getMessage());
				setSourceMgmtButtonEnablements();
			}

			// On Success - Populate the ListBox
			public void onSuccess(List<List<DataItem>> result) {
				populateSourcesTable(vdbName,result);
				setSourceMgmtButtonEnablements();
			}
		};

		if(vdbName!=null && vdbName.equalsIgnoreCase(NO_VDBS)) {
			populateSourcesTable(vdbName,new ArrayList<List<DataItem>>());
			setSourceMgmtButtonEnablements();
		} else {
			teiidMgrService.getVDBModelInfo(vdbName, callback);	
		}
	}
	
	/*
	 * Clear the VDB Sources Table 
	 */
	private void clearSourcesTable( ) {
		vdbSourcesTable.removeAllRows();
	}
	
	/*
	 * Init the Dialog for Error Display
	 */
	private void initErrorDialog() {
		// Create the popup Error DialogBox
		errorDialogBox.setText("Error Dialog");
		errorDialogBox.setAnimationEnabled(true);
		// We can set the id of a widget by accessing its Element
		errorDialogCloseButton.getElement().setId("closeButton");
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("errorDialogPanel");
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(errorDialogCloseButton);
		errorDialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		errorDialogCloseButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				errorDialogBox.hide();
			}
		});
	}
	
	/*
	 * Show the Dialog for Error Display
	 */
	private void showErrorDialog(String title, String msg) {
		// Dialog Title
		errorDialogBox.setText(title);
		serverResponseLabel.addStyleName("serverResponseLabelError");
		// Dialog Text
		serverResponseLabel.setHTML(msg);
		errorDialogBox.center();
		errorDialogCloseButton.setFocus(true);
	}
	
	/*
	 * Init the Dialog for Deleting a Source
	 */
	private void initDeleteSourceDialog( ) {
		// Create the popup DialogBox
		deleteSourceDialogBox.setText("Delete Source(s)");
		deleteSourceDialogBox.setAnimationEnabled(true);
		
		// Dialog Box - Panel Content
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.addStyleName("deleteSourceDialogPanel");
		
		// Message
		Label titleLabel = new Label(" Click 'OK' to delete the Source(s) ");
		titleLabel.addStyleName("labelTextBold");
		titleLabel.addStyleName("bottomPadding10");
		vPanel.add(titleLabel);
		
		// Buttons Panel
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		deleteSourceDialogOKButton.addStyleName("rightPadding5");
		buttonPanel.add(deleteSourceDialogCloseButton);
		buttonPanel.add(deleteSourceDialogOKButton);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		vPanel.add(buttonPanel);
		
		// Add the Completed Panel to the Dialog
		deleteSourceDialogBox.setWidget(vPanel);
	}
	
	/*
	 * Show the Confirm Dialog for Deleting Source(s)
	 */
	private void showDeleteSourceDialog( ) {
		initDeleteSourceDialog();
		
		deleteSourceDialogBox.showRelativeTo(examplesLinkPanel);
		deleteSourceDialogCloseButton.setFocus(true);
	}

	/*
	 * Init the Dialog for Creating a new VDB
	 */
	private void initAppLoginDialog( ) {
		// Create the popup Error DialogBox
		appLoginDialogBox.setText("Enter Admin Login info");
		appLoginDialogBox.setAnimationEnabled(true);
		
		// Dialog Box - Panel Content
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.addStyleName("appLoginDialogPanel");
		
		// Message
		Label titleLabel = new Label("Enter Login Info: ");
		titleLabel.addStyleName("labelTextBold");
		titleLabel.addStyleName("bottomPadding10");
		vPanel.add(titleLabel);

		// Status Label
		appLoginStatusLabel.setText("");
		appLoginStatusLabel.addStyleName("labelTextItalics");
		appLoginStatusLabel.addStyleName("paddingBottom10Left20");
		vPanel.add(appLoginStatusLabel);
				
		// Admin Port widgets
		HorizontalPanel adminPortPanel = new HorizontalPanel();
		Label adminPortLabel = new Label("Port: ");
		adminPortLabel.addStyleName("labelTextBold");
		adminPortLabel.addStyleName("rightPadding5");
		
		adminPortTextBox.setText("9999");
		adminPortPanel.add(adminPortLabel);
		adminPortPanel.add(adminPortTextBox);
		adminPortPanel.addStyleName("bottomPadding10");
		vPanel.add(adminPortPanel);
		
		// Admin Username widgets
		HorizontalPanel adminUsernamePanel = new HorizontalPanel();
		Label adminUsernameLabel = new Label("Username: ");
		adminUsernameLabel.addStyleName("labelTextBold");
		adminUsernameLabel.addStyleName("rightPadding5");
		
		adminUsernameTextBox.setText("admin");
		adminUsernamePanel.add(adminUsernameLabel);
		adminUsernamePanel.add(adminUsernameTextBox);
		adminUsernamePanel.addStyleName("bottomPadding10");
		vPanel.add(adminUsernamePanel);
		
		// Admin Password widgets
		HorizontalPanel adminPasswordPanel = new HorizontalPanel();
		Label adminPasswordLabel = new Label("Password: ");
		adminPasswordLabel.addStyleName("labelTextBold");
		adminPasswordLabel.addStyleName("rightPadding5");
		
		adminPasswordTextBox.setText("admin");
		adminPasswordPanel.add(adminPasswordLabel);
		adminPasswordPanel.add(adminPasswordTextBox);
		adminPasswordPanel.addStyleName("bottomPadding10");
		vPanel.add(adminPasswordPanel);

		// Buttons Panel
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		appLoginDialogOKButton.addStyleName("rightPadding5");
		buttonPanel.add(appLoginDialogCloseButton);
		buttonPanel.add(appLoginDialogOKButton);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		vPanel.add(buttonPanel);
		
		// Add the Completed Panel to the Dialog
		appLoginDialogBox.setWidget(vPanel);
		
		//setAppLoginDialogOKButtonEnablement();
	}
	
	/*
	 * Show the Dialog for Login
	 */
	private void showAppLoginDialog( ) {
		setUIStatusForReInit();
		
		initAppLoginDialog( );
		
		appLoginDialogBox.showRelativeTo(apiLoginButton);
		appLoginDialogCloseButton.setFocus(true);
	}

	/*
	 * Init the Dialog for Creating a new VDB
	 */
	private void initNewVDBDialog( ) {
		// Create the popup Error DialogBox
		newVDBDialogBox.setText("Create a New Dynamic VDB");
		newVDBDialogBox.setAnimationEnabled(true);
		
		// Dialog Box - Panel Content
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.addStyleName("newVDBDialogPanel");
		
		// Message
		Label titleLabel = new Label("Enter a name for the new VDB: ");
		titleLabel.addStyleName("labelTextBold");
		titleLabel.addStyleName("bottomPadding10");
		vPanel.add(titleLabel);

		// Status Label
		newVDBStatusLabel.setText("");
		newVDBStatusLabel.addStyleName("labelTextItalics");
		newVDBStatusLabel.addStyleName("paddingBottom10Left20");
		vPanel.add(newVDBStatusLabel);
				
		// Source Name widgets
		HorizontalPanel vdbNamePanel = new HorizontalPanel();
		Label newVDBNameLabel = new Label("Name: ");
		newVDBNameLabel.addStyleName("labelTextBold");
		newVDBNameLabel.addStyleName("rightPadding5");
		
		newVDBNameTextBox.setText("");
		vdbNamePanel.add(newVDBNameLabel);
		vdbNamePanel.add(newVDBNameTextBox);
		vdbNamePanel.addStyleName("bottomPadding10");
		vPanel.add(vdbNamePanel);
		
		// Buttons Panel
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		newVDBDialogOKButton.addStyleName("rightPadding5");
		buttonPanel.add(newVDBDialogCloseButton);
		buttonPanel.add(newVDBDialogOKButton);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		vPanel.add(buttonPanel);
		
		// Add the Completed Panel to the Dialog
		newVDBDialogBox.setWidget(vPanel);
		
		setNewVDBDialogOKButtonEnablement();
	}
	
	/*
	 * Show the Dialog for Creating a New VDB
	 */
	private void showNewVDBDialog( ) {
		initNewVDBDialog( );
		
		newVDBDialogBox.showRelativeTo(newVDBButton);
		newVDBDialogCloseButton.setFocus(true);
	}
	
	/*
	 * Init the Dialog for Deleting a VDB
	 * @param vdbName the VDB name to delete.
	 */
	private void initDeleteVDBDialog(final String vdbName) {
		// Create the popup Error DialogBox
		deleteVDBDialogBox.setText("Delete a Dynamic VDB");
		deleteVDBDialogBox.setAnimationEnabled(true);
		
		// Dialog Box - Panel Content
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.addStyleName("deleteVDBDialogPanel");
		
		// Message
		Label titleLabel = new Label(" Click 'OK' to delete VDB: '"+vdbName+"'");
		titleLabel.addStyleName("labelTextBold");
		titleLabel.addStyleName("bottomPadding10");
		vPanel.add(titleLabel);
		
		// Buttons Panel
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		deleteVDBDialogOKButton.addStyleName("rightPadding5");
		buttonPanel.add(deleteVDBDialogCloseButton);
		buttonPanel.add(deleteVDBDialogOKButton);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		vPanel.add(buttonPanel);
		
		// Add the Completed Panel to the Dialog
		deleteVDBDialogBox.setWidget(vPanel);
	}
	
	/*
	 * Show the Dialog for Deleting the VDB
	 * @param vdbName the VDB name to delete.
	 */
	private void showDeleteVDBDialog(final String vdbName) {
		initDeleteVDBDialog(vdbName);
		
		deleteVDBDialogBox.showRelativeTo(deleteVDBButton);
		deleteVDBDialogCloseButton.setFocus(true);
	}

}
