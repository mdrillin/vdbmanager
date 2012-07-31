package org.teiid.tools.vdbmanager.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.teiid.tools.vdbmanager.client.events.PropertyChangedEvent;
import org.teiid.tools.vdbmanager.client.events.PropertyChangedEventHandler;
import org.teiid.tools.vdbmanager.client.events.SourcesChangedEvent;
import org.teiid.tools.vdbmanager.client.events.VDBRedeployEvent;

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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/*
 * This class encapsulates the Create/Edit Source Dialog
 */
public class CreateEditSourceDialog {

	private static final String REMOVE_MODEL_ERROR = "An error occurred while "
		+ "attempting to remove model(s). ";
	private static final String ADD_MODEL_ERROR = "An error occurred while "
		+ "attempting to add model(s). ";
	private static final String INIT_DSNAMES_ERROR = "An error occurred while "
		+ "attempting to init the datasource names. ";
	private static final String INIT_TRANSLATOR_NAMES_ERROR = "An error occurred while "
		+ "attempting to init the translator names. ";
	private static final String INIT_DEFAULT_PROPS_ERROR = "An error occurred while "
		+ "attempting to get template properties. ";
	
	private static final String VIEW_MODEL = "ViewModel"; //$NON-NLS-1$
	private static final String DDL_NAME = "Views-DDL";
	private static final String DDL_DISPLAY_NAME = "View DDL";

	// Source Type and Name Controls
	private Label sourceNameLabel = new Label("Name: ");
	private TextBox sourceNameTextBox = new TextBox();

	private Label sourceTypeLabel = new Label("Source: ");
	private ListBox sourceTypeListBox = new ListBox();
	private Label sourceTypeEditLabel = new Label();

	// Translator controls
	private Label translatorLabel = new Label("Translator: ");
	private TextBox translatorTextBox = new TextBox();

	private Label sourceNameRedeployLabel = new Label();
	private Label sourcePropertyStatusLabel = new Label();

	// Dialog Controls
	private DialogBox addSourceDialogBox = new DialogBox();
	private Button addSourceDialogOKButton = new Button("OK");
	private Button addSourceDialogCloseButton = new Button("Cancel");

	// Error Dialog Controls
	private DialogBox errorDialogBox = new DialogBox();
	private Button errorDialogCloseButton = new Button("Cancel");
	private HTML serverResponseLabel = new HTML();

	// Source Properties Table
	//private FlexTable sourcePropertiesTable = new FlexTable();
	private PropertyTable sourcePropsTable;
	
	// Flag for Add vs Edit state
	private boolean addingSource = true;
	private List<String> currentSourceNames = new ArrayList<String>();
	
	private List<String> availableTranslators = new ArrayList<String>();
	
	// Maintain latest type and property list for each named source
	private Map<String,Map<String,String>> savedNamePropertyMap; 
    private Map<String,String> savedNameTranslatorMap;
    
	// EventBus and TeiidMgrService
	private SimpleEventBus eventBus;
	private final TeiidMgrServiceAsync teiidMgrService;
	
	// Flag allows different handling when running on OpenShift
	private boolean isRunningOnOpenShift = false;

	public CreateEditSourceDialog(SimpleEventBus eventBus, 
			                      Map<String,Map<String,String>> savedNamePropertyMap,
			                      Map<String,String> savedNameTranslatorMap,
			                      boolean isRunningOnOpenShift,
			                      TeiidMgrServiceAsync teiidMgrService) {
		this.eventBus=eventBus;
		this.savedNamePropertyMap = savedNamePropertyMap;
		this.savedNameTranslatorMap = savedNameTranslatorMap;
		this.isRunningOnOpenShift=isRunningOnOpenShift;
		this.teiidMgrService=teiidMgrService;
		this.sourcePropsTable = new PropertyTable(this.eventBus);
		initErrorDialog();
	    // Listen for PropertyChangedEvent from properties table.
		this.eventBus.addHandler(PropertyChangedEvent.TYPE, new PropertyChangedEventHandler() {
			public void onEvent(PropertyChangedEvent event) {
	        	setAddSourceDialogOKButtonEnablement();
			}
		});
	}
	
	public void showDialogForAdd(String vdbName,List<String> currentSourceNames, Widget relativeToWidget) {
		addingSource=true;
		this.currentSourceNames.addAll(currentSourceNames);
		
		// Populate the translator list
		populateTranslatorList();
		
		// Creates the components / panel / handlers
		init(vdbName,null,null);
				
		// Final Step - Populates the available Sources, fires selection event
		populateSourceTypeListBox();

		// Show the Dialog
		addSourceDialogBox.showRelativeTo(relativeToWidget);
	}
	
	public void showDialogForEdit(String vdbName, String editSourceName, String editSourceType, Widget relativeToWidget) {
		addingSource=false;

		// Populate the translator list
		populateTranslatorList();
		
		// Creates the components / panel / handlers
		init(vdbName,editSourceName,editSourceType);
		
		populateUsingSaved(editSourceName,editSourceType);
		
		// Show the Dialog
		addSourceDialogBox.showRelativeTo(relativeToWidget);
	}
	
	private void init(final String vdbName, String editSourceName, String editSourceType) {
		// Title selection is based on new / redeploy flag
		String dialogTitle = "Re-Deploy an Existing DataSource";
		if(addingSource) {
			dialogTitle = "Add a New DataSource";
		}
		// Create the popup AddSource DialogBox
		addSourceDialogBox.setText(dialogTitle);
		addSourceDialogBox.setAnimationEnabled(true);
		// We can set the id of a widget by accessing its Element
		addSourceDialogOKButton.getElement().setId("okButton");
		addSourceDialogCloseButton.getElement().setId("closeButton");
		
		// Dialog Box - Panel Content
		VerticalPanel vPanel = new VerticalPanel();
		vPanel.addStyleName("addSourceDialogPanel");
		
		// ------------------------
		// Title Label
		// ------------------------
		Label titleLabel = new Label("Enter properties for the source: ");
		titleLabel.addStyleName("labelTextBold");
		titleLabel.addStyleName("bottomPadding10");
		vPanel.add(titleLabel);

		// ------------------------
		// Status Label
		// ------------------------
		sourcePropertyStatusLabel.setText("");
		sourcePropertyStatusLabel.addStyleName("labelTextItalics");
		sourcePropertyStatusLabel.addStyleName("paddingBottom10Left20");
		vPanel.add(sourcePropertyStatusLabel);

		// ------------------------
		// Source Name controls
		// ------------------------
		HorizontalPanel sourceNamePanel = new HorizontalPanel();
		sourceNameLabel.addStyleName("labelTextBold");
		sourceNameLabel.addStyleName("rightPadding5");
		sourceNamePanel.add(sourceNameLabel);
		// New Source, the user can type in a name
		if(addingSource) {
			sourceNameTextBox.setText("");
			sourceNamePanel.add(sourceNameTextBox);
		// Re-deploy Source, the name cannot be changed
		} else {
			String sourceName = editSourceName;
			sourceNameRedeployLabel.setText(sourceName);
			sourceNamePanel.add(sourceNameRedeployLabel);
		}
		sourceNamePanel.addStyleName("bottomPadding10");
		vPanel.add(sourceNamePanel);
		
		// ------------------------
		// Source Type controls
		// ------------------------
		HorizontalPanel sourceTypePanel = new HorizontalPanel();
		sourceTypeLabel.addStyleName("labelTextBold");
		sourceTypeLabel.addStyleName("rightPadding5");
		sourceTypePanel.add(sourceTypeLabel);
		// New Source, add the SourceType ListBox
		if(addingSource) {
			sourceTypePanel.add(sourceTypeListBox);
		// Re-deploy Source, the type cannot be changed
		} else {
			String selectedType = editSourceType;
			sourceTypeEditLabel.setText(selectedType);
			sourceTypePanel.add(sourceTypeEditLabel);
		}
		sourceTypePanel.addStyleName("bottomPadding10");
		vPanel.add(sourceTypePanel);
		
		// --------------------------
		// Translator Name controls
		// --------------------------
		HorizontalPanel translatorNamePanel = new HorizontalPanel();
		// Add TranslatorName Label to Panel
		translatorLabel.addStyleName("labelTextBold");
		translatorLabel.addStyleName("rightPadding5");
		translatorNamePanel.add(translatorLabel);
		// Add Translator TextBox to Panel
		translatorNamePanel.add(translatorTextBox);
		translatorNamePanel.addStyleName("bottomPadding10");
		vPanel.add(translatorNamePanel);
		
		// --------------------------
		// Source Properties Table
		// --------------------------
		sourcePropsTable.addStyleName("sourcePropertiesTable");
		vPanel.add(sourcePropsTable);
		
		// ------------------------------
		// Required Property Note Panel
		// ------------------------------
		HorizontalPanel propertyNotePanel = new HorizontalPanel();
		Label propertyNoteLabel = new Label("* - denotes required property");
		propertyNoteLabel.addStyleName("labelTextItalics");
		propertyNoteLabel.addStyleName("rightPadding5");
		propertyNotePanel.add(propertyNoteLabel);
		propertyNotePanel.addStyleName("bottomPadding10");
		vPanel.add(propertyNotePanel);
		
		// --------------------------
		// Buttons Panel
		// --------------------------
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
		addSourceDialogOKButton.addStyleName("rightPadding5");
		buttonPanel.add(addSourceDialogCloseButton);
		buttonPanel.add(addSourceDialogOKButton);
		vPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		vPanel.add(buttonPanel);
		
		addSourceDialogBox.setHeight("200px");
		addSourceDialogBox.setWidth("500px");

		// Add the Completed Panel to the Dialog
		addSourceDialogBox.setWidget(vPanel);
		
		// ---------------------------------------
		// Handlers for Widgets 
		// ---------------------------------------

		// Change Listener for Source Type ListBox
		sourceTypeListBox.addChangeHandler(new ChangeHandler()
		{
			// Changing the Type selection will re-populate property table with defaults for that type
			public void onChange(ChangeEvent event)
			{
				String selectedType = getDialogSourceType();				
				setSourcePropertiesTableWithDefaults(selectedType);
				
				// Get translator for the source - if possible
				String translator = DataSourceHelper.getDefaultTranslator(selectedType, availableTranslators);
				translatorTextBox.setText(translator);
//				// Select translator in list box if found
//				if(translator!=null) {
//					int nItems = translatorListBox.getItemCount();
//					for(int i=0; i<nItems; i++) {
//						String itemText = translatorListBox.getItemText(i);
//						if(translator.equals(itemText)) {
//							translatorListBox.setSelectedIndex(i);
//							break;
//						}
//					}
//				}
			}
		});
		
		// Change Listener for Source Name TextBox - does property validation
		sourceNameTextBox.addKeyUpHandler(new KeyUpHandler() {
	        @Override
	        public void onKeyUp(KeyUpEvent event) {
	        	setAddSourceDialogOKButtonEnablement();
	        }
	    });
		
		// Click Handler for DialogBox Close Button
		addSourceDialogCloseButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				addSourceDialogBox.hide();
			}
		});
		
		// Click Handler for DialogBox OK Button
		addSourceDialogOKButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				// Get source name and type from the dialog widgets
				String selectedType = getDialogSourceType();
				String sourceName = getDialogSourceName();
				String translatorName = getDialogTranslatorName();

				List<PropertyObj> tableProps = getSourcePropsFromTable();
				
				Map<String,String> propsForCreate = getPropsForCreate(tableProps);
				// Save the table property entries (for redeploy)
				updateSavedPropertyMap(sourceName,propsForCreate,translatorName);
								
				if(selectedType.equalsIgnoreCase(VIEW_MODEL)) {
					// For view Model, add 'Model' suffix here
					addViewModel(vdbName,sourceName,propsForCreate);
				} else {
					// Add / Re-deploy the Model
					addSourceModel(vdbName,sourceName,selectedType,translatorName,propsForCreate);
				}

				addSourceDialogBox.hide();
			}
		});

		setAddSourceDialogOKButtonEnablement();
	}
	
	/*
	 * Convert the entire List of PropertyObj from the Table, into a Map which will be submitted to the Admin API
	 * @param propertyObjs the List of PropertyObjs
	 * @return the Map for submittal to the Admin APIAdd any mising properties
	 */
	private Map<String,String> getPropsForCreate(List<PropertyObj> propertyObjs) {
		Map<String,String> resultMap = new HashMap<String,String>();
		
		for(PropertyObj propertyObj: propertyObjs) {
			String propName = propertyObj.getName();
			String value = propertyObj.getValue();
			// Property is Required - add it to the Map
			if(propertyObj.isRequired()) {
				resultMap.put(propName, value);
			} else if(value!=null && !value.trim().isEmpty()) {
				resultMap.put(propName, value);
			}
		}
		return resultMap;
	}
	
	/*
	 * Populate the Properties table with defaults for the supplied dataSourceType
	 * @param dataSourceType the DataSource Type
	 */
	private void setSourcePropertiesTableWithDefaults(final String dataSourceType) {
		// Set up the callback object.
		AsyncCallback<List<PropertyObj>> callback = new AsyncCallback<List<PropertyObj>>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				showErrorDialog("Init Default Properties Table Error", INIT_DEFAULT_PROPS_ERROR+caught.getMessage());
			}

			// On Success - Populate the ListBox
			public void onSuccess(List<PropertyObj> propertyObjs) {
				// Verify properties.  Adds anything missing, plus sets some default values
				verifyServerProperties(dataSourceType, propertyObjs);
				populateSourcePropertiesTable(propertyObjs);
			}
		};
        
		// Special case for VIEW_MODEL - bypasses the server call.
		if(dataSourceType.equalsIgnoreCase(VIEW_MODEL)) {
			List<PropertyObj> propObjs = new ArrayList<PropertyObj>();
			// Verify properties.  For VIEW_MODEL this adds the DDL property.
			verifyServerProperties(dataSourceType, propObjs);
			populateSourcePropertiesTable(propObjs);
		} else {
			teiidMgrService.getPropertyDefns(dataSourceType,callback);	
		}
	}
	
	/*
	 * Populate the Properties table for the supplied DataSource type.  Use the provided saved property values as starting point.
	 * @param dataSourceType the DataSource Type
	 */
	private void populateUsingSaved(final String dataSourceName, final String dataSourceType) {
		// Set up the callback object.
		AsyncCallback<List<PropertyObj>> callback = new AsyncCallback<List<PropertyObj>>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				showErrorDialog("Init Default Properties Table Error", INIT_DEFAULT_PROPS_ERROR+caught.getMessage());
			}

			// On Success - Populate the ListBox
			public void onSuccess(List<PropertyObj> propertyObjs) {
				// Get saved translator.  If null, use the default if possible.
				String translator = savedNameTranslatorMap.get(dataSourceName);
				if(translator==null) {
					translator = DataSourceHelper.getDefaultTranslator(dataSourceType, availableTranslators);
				}
				translatorTextBox.setText(translator);

				Map<String,String> savedPropValues = savedNamePropertyMap.get(dataSourceName);
				verifyServerProperties(dataSourceType, propertyObjs);
				populateSourcePropertiesTable(propertyObjs,savedPropValues);
			}
		};


		// Special case for VIEW_MODEL - bypasses the server call.
		if(dataSourceType.equalsIgnoreCase(VIEW_MODEL)) {
			// Get saved translator.  If null, use the default if possible.
			String translator = savedNameTranslatorMap.get(dataSourceName);
			if(translator==null) {
				translator = DataSourceHelper.getDefaultTranslator(dataSourceType, availableTranslators);
			}
			translatorTextBox.setText(translator);

			Map<String,String> savedPropValues = savedNamePropertyMap.get(dataSourceName);
			
			List<PropertyObj> propObjs = new ArrayList<PropertyObj>();
			// Verify properties.  For VIEW_MODEL this adds the DDL property.
			verifyServerProperties(dataSourceType, propObjs);
			populateSourcePropertiesTable(propObjs,savedPropValues);
		} else {
			teiidMgrService.getPropertyDefns(dataSourceType,callback);	
		}
	}
	
	private void verifyServerProperties(String dataSourceType, List<PropertyObj> propertyObjs) {
		// ------------------------------
		// Add any mising properties
		// ------------------------------
		DataSourceHelper.addMissingProperties(dataSourceType, propertyObjs);
		
		// --------------------------------------------
		// This sets 'default' values on properties
		//   - URL template 
		//   - username placeholder
		// --------------------------------------------
		DataSourceHelper.setPropertyDefaults(dataSourceType, propertyObjs);
	}
	
	/*
	 * Init the List of DataSource Template Names
	 * @param vdbName the name of the VDB
	 * @param sourceName the source name
	 * @param templateName the template name
	 * @param translatorName the translator name
	 * @param propsMap the property Map of name-value pairs
	 */
	private void populateSourceTypeListBox( ) {
		// Set up the callback object.
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				showErrorDialog("Init DataSource Names Error", INIT_DSNAMES_ERROR+caught.getMessage());
			}

			// On Success - Populate the ListBox
			public void onSuccess(List<String> typeNames) {
				
				// passes in 'runningOnOpenShift' flag to filter available sources on OpenShift
				List<String> allowedTypes = DataSourceHelper.filterAllowedTypes(typeNames,isRunningOnOpenShift);
				
				// Make sure clear first
				sourceTypeListBox.clear();
				
				// Repopulate the ListBox.  The actual names are converted to more user-friendly display names
				int i = 0;
				for(String typeName: allowedTypes) {
					String displayName = DataSourceHelper.convertTemplateNameToDisplayName(typeName);
					sourceTypeListBox.insertItem(displayName, i);
					i++;
				}
				sourceTypeListBox.insertItem(VIEW_MODEL, i);

				// Initialize by setting the selection to the first item.
				sourceTypeListBox.setSelectedIndex(0);
				DomEvent.fireNativeEvent(Document.get().createChangeEvent(),sourceTypeListBox);				
			}
		};

		teiidMgrService.getDataSourceTemplates(callback);	
	}
	
	/*
	 * Init the List of Translator Names
	 * @param vdbName the name of the VDB
	 * @param sourceName the source name
	 * @param templateName the template name
	 * @param translatorName the translator name
	 * @param propsMap the property Map of name-value pairs
	 */
	private void populateTranslatorList( ) {
		// Set up the callback object.
		AsyncCallback<List<String>> callback = new AsyncCallback<List<String>>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				showErrorDialog("Init Translator Names Error", INIT_TRANSLATOR_NAMES_ERROR+caught.getMessage());
			}

			// On Success - Populate the ListBox
			public void onSuccess(List<String> translatorNames) {
				// Make sure clear first
				availableTranslators.clear();
				
				// Repopulate the ListBox
				int i = 0;
				for(String translatorName: translatorNames) {
					availableTranslators.add(translatorName);
					i++;
				}
			}
		};

		teiidMgrService.getTranslatorNames(callback);	
	}

	private void fireSourcesChanged() {
		this.eventBus.fireEvent(new SourcesChangedEvent());
	}
	
	private void fireVDBRedeploy() {
		this.eventBus.fireEvent(new VDBRedeployEvent());
	}
	
	  /*
	 * Determine the OK Button enablement on the Add Source Dialog.  The Add button
	 * will only enable if the properties are filled out.
	 */
	private void setAddSourceDialogOKButtonEnablement( ) {
		boolean addSourceEnabled = false;
		
		// Validate the entered properties
		if(validateSourceProperties()) {
			addSourceEnabled = true;
		}
		
		// Disable if properties are invalid
		addSourceDialogOKButton.setEnabled(addSourceEnabled);
	}
	
	/*
	 * Validate the entered properties and return status.  The status message label is also updated.
	 * @return the property validation status.  'true' if properties are valid, 'false' otherwise.
	 */
	private boolean validateSourceProperties( ) {
		boolean statusOK = true;
		String statusStr = "OK";
		
		// Validate the entered name
		String name = getDialogSourceName();
		if(name==null || name.trim().length()==0) {
			statusStr = "Please enter a name for the DataSource";
			statusOK = false;
		}
		
		// If new source, check entered name against existing names
		if(statusOK && addingSource) {
			if(currentSourceNames.contains(name)) {
				statusStr = "A DataSource with this name already exists";
				statusOK = false;
			}
		}
		
		// Validate the Property Table
		if(statusOK) {
			statusStr = this.sourcePropsTable.getStatus();
			if(!statusStr.equals("OK")) {
				statusOK = false;
			}
		}
		
		// Update the status label
		
		if(!statusStr.equals("OK")) {
			sourcePropertyStatusLabel.setText(statusStr);
		} else {
			sourcePropertyStatusLabel.setText("Click OK to accept");
		}
		
		return statusOK;
	}

	/*
	 * Add a source Model to the specified VDB
	 * @param vdbName the name of the VDB
	 * @param sourceName the source name
	 * @param templateName the template name
	 * @param translatorName the translator name
	 * @param propsMap the property Map of name-value pairs
	 */
	private void addSourceModel(final String vdbName, String sourceName, String templateName,
			                    String translatorName, Map propsMap) {
		// Set up the callback object.
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				showErrorDialog("Add Model Error", ADD_MODEL_ERROR+caught.getMessage());
			}

			// On Success - Populate the ListBox
			public void onSuccess(String result) {
				fireSourcesChanged();
			}
		};
		
		fireVDBRedeploy();
		
		teiidMgrService.addSourceAndModel(vdbName, sourceName, templateName, translatorName, propsMap, callback);		
	}

	/*
	 * Add a View Model
	 * @param vdbName the name of the VDB being modified
	 * @param viewModelName the name of the view model to create
	 * @param propsMap the map of properties for the view
	 */
	public void addViewModel(final String vdbName, String viewModelName, Map propsMap) {
		// Set up the callback object.
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			// On Failure - show Error Dialog
			public void onFailure(Throwable caught) {
				showErrorDialog("Add Model Error", ADD_MODEL_ERROR+caught.getMessage());
			}

			// On Success - Populate the ListBox
			public void onSuccess(String result) {
				fireSourcesChanged();
			}
		};
		
		fireVDBRedeploy();

		teiidMgrService.addViewModel(vdbName, viewModelName, propsMap, callback);		
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
	 * Get the List of property objects from the Source Properties table.
	 * @return the List of source property objects
	 */
	private List<PropertyObj> getSourcePropsFromTable() {
		return this.sourcePropsTable.getProperties();
	}

	/*
	 * Get the selected Source Type from the Dialog selection.
	 * @return the current selected SourceType
	 */
	private String getDialogSourceType( ) {
		String selectedType = null;
		// For new source - use the ListBox selection
		if(addingSource) {
			int selectedIndex = sourceTypeListBox.getSelectedIndex();
			String selectedDisplayType = sourceTypeListBox.getValue(selectedIndex);
			// Converts the display name to actual name
			selectedType = DataSourceHelper.convertTemplateDisplayNameToName(selectedDisplayType);
	    // For re-deploy - use the Label widget
		} else {
			String selectedDisplayType = sourceTypeEditLabel.getText();
			// Converts the display name to actual name
			selectedType = DataSourceHelper.convertTemplateDisplayNameToName(selectedDisplayType);
		}
		return selectedType;
	}
	
	/*
	 * Get the Source Name from the Source Name TextBox
	 * @return the Source Name TextBox entry
	 */
	private String getDialogSourceName( ) {
		String sourceName = null;
		// For new source - use the TextBox entry
		if(addingSource) {
			sourceName = sourceNameTextBox.getText();
	    // For re-deploy - use the Label widget
		} else {
			sourceName = sourceNameRedeployLabel.getText();
		}
		return sourceName;
	}
	
	/*
	 * Get the Translator Name from the Translator TextBox
	 * @return the Translator Name TextBox entry
	 */
	private String getDialogTranslatorName( ) {
		return translatorTextBox.getText();
	}
		
	/*
	 * Populate the Source Properties Table
	 * @param propertyObjs the List of PropertyObjs
	 */
	private void populateSourcePropertiesTable(List<PropertyObj> propertyObjs) {
		this.sourcePropsTable.setProperties(propertyObjs);
	}
	
	/*
	 * Populate the Source Properties Table.  First override any values with the provided saved Map values
	 * @param propertyObjs the List of PropertyObjs
	 * @param savedPropValues the Map of saved name-value pairs, used to override values on PropertyObjs if found.
	 */
	private void populateSourcePropertiesTable(List<PropertyObj> propertyObjs,Map<String,String> savedPropValues) {
		if(savedPropValues!=null && !savedPropValues.isEmpty()) {
			// Iterate PropertyObjs, look for matching savedPropValues
			for(PropertyObj propObj : propertyObjs) {
				String propObjName = propObj.getName();
				// look for saved property.  If found, use the saved property value to update the PropertyObj
				if(propObjName!=null) {
					String savedValue = savedPropValues.get(propObjName);
					propObj.setValue(savedValue);
				}
			}
		}
		populateSourcePropertiesTable(propertyObjs);
	}
	
	/*
	 * Update the name-property Map using the supplied sourceName and properties.  Update the name-translator Map with the
	 * supplied translatorName.  Purpose is to maintain last submitted properties and translator for a source when possible, for re-deploy.
	 * @param sourceName the name of the source being updated
	 * @param propsMap the Map of name-value pairs for the properties
	 * @param translator the translator to save to the saved translator map.
	 */
	private void updateSavedPropertyMap(String sourceName, Map<String,String> propsMap, String translator) {
		savedNamePropertyMap.put(sourceName,propsMap);
		savedNameTranslatorMap.put(sourceName,translator);
	}
	
}
