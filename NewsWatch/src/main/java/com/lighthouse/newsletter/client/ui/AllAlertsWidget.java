package com.lighthouse.newsletter.client.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.appUtils.client.ProgressIndicatorWidget;
import com.appUtils.client.exception.FeatureNotSupportedException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.newsletter.client.UserNewsletterAlertConfigService;
import com.lighthouse.newsletter.client.UserNewsletterAlertConfigServiceAsync;
import com.lighthouse.newsletter.client.domain.UserNewsletterAlertConfig;

/**
 * page to display all user alerts
 * @author nairutee & pritam
 *
 */
public class AllAlertsWidget extends Composite implements ClickHandler{

	private VerticalPanel baseVp;
	private ArrayList<UserNewsletterAlertConfig> useralertsList;
	private FlexTable alertsFlex;
	public Anchor alertNameAnchor;
	private HashMap<Anchor, UserNewsletterAlertConfig> alertLabelMap;
	private AlertsConfigurationWidget parentWidget;
	public CreateAlertsWidget createAlert=new CreateAlertsWidget(parentWidget);
	private UserNewsletterAlertConfigServiceAsync alertService = GWT.create(UserNewsletterAlertConfigService.class);
	private UserNewsletterAlertConfig userAlert;
	private UserNewsletterAlertConfig mergedAlert;
	private boolean isInitialized = false;
	private TextBox alertTextBox=new TextBox();
	private ListBox frequencyBox;
	private FlexTable flex;
	private Label errorLabel = new Label();
	private VerticalPanel vpHolder;
	private Label notificationLabel = new Label();
	private LhUserPermission lhUserPermission;
	private ProgressIndicatorWidget loader = new ProgressIndicatorWidget(true);
	
	/**
	 * Constructor
	 * @param parentWidget AlertsConfigurationWidget
	 */
	public AllAlertsWidget(AlertsConfigurationWidget  parentWidget, LhUserPermission lhUserPermission){
		baseVp = new VerticalPanel();
		this.lhUserPermission = lhUserPermission;
		this.parentWidget = parentWidget;
		baseVp.setWidth("100%");
		baseVp.setHeight("99%");
		DOM.setStyleAttribute(baseVp.getElement(), "padding", "5px");
		initWidget(baseVp);
	}
	
	/**
	 * initializes the widget. Fetches a list of all alerts for the user
	 */
	public void initialize(){
		
		alertService.getAllUserAlertList(-1, -1, new AsyncCallback<ArrayList<UserNewsletterAlertConfig>>() {
			
			@Override
			public void onSuccess(ArrayList<UserNewsletterAlertConfig> result) {
				isInitialized = true;
				setUseralertsList(result);
				createUI();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}
	
	/**
	 * creates the ui of the page
	 */
	public void createUI(){
		baseVp.clear();
		vpHolder = new VerticalPanel();
		vpHolder.setWidth("100%");
		vpHolder.setSpacing(5);
		boolean hasMergedAlert = false;
		
		alertsFlex = new FlexTable();
		if(useralertsList == null || useralertsList.size() == 0){
			Label createAlertLabel = new Label("You currently have no alerts defined.");
			Anchor createAlertAnchor = new Anchor("Create an alert");
			createAlertAnchor.addClickHandler(this);
			alertsFlex.setWidget(1, 0, createAlertLabel);
			alertsFlex.setWidget(1, 1, createAlertAnchor);
		}else{
			alertLabelMap = new HashMap<Anchor, UserNewsletterAlertConfig>();
			int row = 0;
			for(UserNewsletterAlertConfig userAlertConfig : useralertsList){
				if(userAlertConfig.getIsSingle() == 0){
					alertsFlex.setWidget(row, 0, createUserAlertComponent(userAlertConfig));
					row+=2;
				}else{
					hasMergedAlert = true;
					mergedAlert = userAlertConfig;
				}
			}
		}
		
		
		vpHolder.add(createHeader("List of all user configured alerts", "Click on an alert to edit / delete it"));

		vpHolder.add(loader);
		createConsolidatedMailOptionPanel(hasMergedAlert);
		createPreferencesComponent();
		getRssPanel();
		vpHolder.add(alertsFlex);
		baseVp.add(vpHolder);
		alertsFlex.setStylePrimaryName("alertList");
	}
	
	/**
	 * creates the Rss feed options panel
	 */
	private void getRssPanel(){
		try {
			AlertsRssWidget alertsRssWidget = new AlertsRssWidget(lhUserPermission, useralertsList);
			vpHolder.add(alertsRssWidget);
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	private HorizontalPanel createUserAlertComponent(UserNewsletterAlertConfig userAlertConfig){
		alertNameAnchor = new Anchor(userAlertConfig.getAlertName());
		alertNameAnchor.addClickHandler(this);
		alertLabelMap.put(alertNameAnchor, userAlertConfig);
		HorizontalPanel hpAlert = new HorizontalPanel();
		
		StringBuilder htmlText = new StringBuilder();
		int count = 0;
		int size = userAlertConfig.getAlertGroupList().size();
		if(size>0){
			htmlText.append(" [ ");
			for(Group group : userAlertConfig.getAlertGroupList()){
				if(count < size-1)
					htmlText.append(group.getGroupName() + ", ");
				else
					htmlText.append(group.getGroupName() +" ]");
				
				count++;
			}
		}else{
			if(userAlertConfig.getIsSingle() == 1)
				htmlText.append(" [ Merged alert ]");
		}
		
		HTML grpNamesHTML = new HTML();
		grpNamesHTML.setHTML("<p style=\" font-family:lucida grande,tahoma,verdana,arial,sans-serif; font-size:8pt; color:rgb(102,102,102); text-indent:3px; text-align:left; \">"+htmlText.toString()+"</p>");
		
		hpAlert.add(alertNameAnchor);
		hpAlert.add(grpNamesHTML);
		hpAlert.setCellVerticalAlignment(alertNameAnchor, HasVerticalAlignment.ALIGN_MIDDLE);
		hpAlert.setCellVerticalAlignment(grpNamesHTML, HasVerticalAlignment.ALIGN_TOP);
		
		return hpAlert;
		
	}
	
	/**
	 * creates a header panel
	 * @param headerText - the header text of the panel
	 * @param subHeaderText - the subheader text of the panel
	 * @return HorizontalPanel
	 */
	private VerticalPanel createHeader(String headerText, String subHeaderText){
		VerticalPanel headerPanel = new VerticalPanel();
		headerPanel.setWidth("100%");
		Label headerLabel = new Label(headerText);
		Label subheaderLabel = new Label(subHeaderText);
		headerLabel.setStylePrimaryName("headerText");
		subheaderLabel.setStylePrimaryName("suggestionText");
		headerPanel.add(headerLabel);
		headerPanel.add(subheaderLabel);
		headerPanel.setStylePrimaryName("headerPanel");
		return headerPanel;
	}
	
	private void createPreferencesComponent(){
		flex = new FlexTable();
		Label frequencyName=new Label("Frequency of alerts:");
		frequencyName.setStylePrimaryName("label");
		frequencyBox=new ListBox();
		frequencyBox.addItem("Daily");
		frequencyBox.addItem("Weekly");
			
		Label alertName=new Label("Alert name:");
		alertName.setStylePrimaryName("label");
		alertTextBox=new TextBox();
			 
		flex.getFlexCellFormatter().setColSpan(2, 0, 2);
		flex.setWidget(5,0,frequencyName);
		flex.setWidget(5,1,frequencyBox);
		flex.setWidget(10,0,alertName);
		flex.setWidget(10,1,alertTextBox);
		
		vpHolder.add(flex);
		flex.setVisible(false);
		
	}
	
	private void createConsolidatedMailOptionPanel(final boolean hasMergedAlert){
		final HorizontalPanel horizontalPanel = new HorizontalPanel();
		Label label = new Label("Send me a single combined mail for all alerts listed below");
		label.setStylePrimaryName("instructionLabel");
		
		final Button updateButton = new Button("Update");
		updateButton.setVisible(false);
		final CheckBox checkBox = new CheckBox();
		if(getUseralertsList().size()<=0){
			checkBox.setEnabled(false);
		}
		if(hasMergedAlert)
			checkBox.setValue(true, false);
		
		checkBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				CheckBox cb = (CheckBox) event.getSource();
				if(cb.getValue()){
					if(getUseralertsList().size()>0)
					{
					updateButton.setVisible(true);
					flex.setVisible(true);
					}
				}
				else{
					if(getUseralertsList().size()>0){
					flex.setVisible(false);
					updateButton.setVisible(true);
					}
				}
			}
		});
		updateButton.addStyleName("loginButton");
		updateButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(checkBox.getValue()){
					if(hasMergedAlert){
						notificationLabel.setText("You already have a merged alert defined.");
						notificationLabel.setVisible(true);
						return;
					}
					loader.setLoadingMessage("Creating alert...");
					loader.enable();
					if(notificationLabel.isVisible())
						notificationLabel.setVisible(false);
					if(errorLabel.isVisible())
						errorLabel.setVisible(false);
					
				    String alertName=alertTextBox.getText();
				    if(alertName.equals("")){
						errorLabel.setText("Please Enter the Alert Name");
						errorLabel.setVisible(true);
						loader.disable();
						return;
					}
					String frequency=frequencyBox.getItemText(frequencyBox.getSelectedIndex());
					userAlert=new UserNewsletterAlertConfig();
					userAlert.setAlertName(alertName);
					userAlert.setFrequency(frequency);
					userAlert.setIsSingle(1);
					alertService.createUserNewsletterAlert(userAlert,null,-1,-1, new AsyncCallback<UserNewsletterAlertConfig>() {
						
						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
							notificationLabel.setText("Unable to create alert");
							notificationLabel.setVisible(true);
							loader.disable();
						}

						@Override
						public void onSuccess(UserNewsletterAlertConfig result) {
							loader.disable();
							if(result == null){
								errorLabel.setText("Alert name already exists");
								errorLabel.setVisible(true);
							}else{
								notificationLabel.setText("Alert successfully created");
								notificationLabel.setVisible(true);
								if(result.getIsSingle()==1)
									mergedAlert=result;
								clear();
								flex.setVisible(false);
								updateButton.setVisible(false);
							}
							
						}
					});
				}else{
					loader.setLoadingMessage("Deleting alert...");
					loader.enable();
					int alertId=mergedAlert.getAlertId();
					UserNewsletterAlertConfigServiceAsync async=GWT.create(UserNewsletterAlertConfigService.class);
					async.deleteAlert(alertId, new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							caught.printStackTrace();
							notificationLabel.setText("Unable to delete alert");
							notificationLabel.setVisible(true);	
							loader.disable();
						}

						@Override
						public void onSuccess(Boolean result) {
							loader.disable();
							notificationLabel.setText("Alert successfully deleted");
							notificationLabel.setVisible(true);
							updateButton.setVisible(false);
						}
					});
				}
	
			}
		});
		
		updateButton.setVisible(false);
		horizontalPanel.add(checkBox);
		horizontalPanel.add(label);
		
		horizontalPanel.add(updateButton);
		horizontalPanel.setStylePrimaryName("alertPanel");
		vpHolder.add(horizontalPanel);
	}

	public ArrayList<UserNewsletterAlertConfig> getUseralertsList() {
		return useralertsList;
	}

	public void setUseralertsList(
			ArrayList<UserNewsletterAlertConfig> useralertsList) {
		this.useralertsList = useralertsList;
	}

	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() instanceof Anchor){
			Anchor anchor = (Anchor)event.getSource();
			if(anchor.getText().equals("Create an alert")){
				parentWidget.toggleTab(0);
			}else{
				UserNewsletterAlertConfig userAlertConfig = alertLabelMap.get(anchor);
				parentWidget.getCreateAlertWidget().setUserAlert(userAlertConfig);
				parentWidget.toggleTab(0);
			}
		}
		
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}
	
		
	private void clear(){
		alertTextBox.setText("");
		errorLabel.setVisible(false);
		notificationLabel.setVisible(false);
		
	}
	
	public UserNewsletterAlertConfig getUserAlert() {
		return userAlert;
	}

	public void setUserAlert(UserNewsletterAlertConfig userAlert) {
		this.userAlert = userAlert;
	}
}


