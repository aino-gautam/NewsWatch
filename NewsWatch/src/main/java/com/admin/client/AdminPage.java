package com.admin.client;





import com.common.client.Footer;

import com.common.client.LogoutPage;
import com.common.client.ManageHeader;
import com.common.client.Statistics;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;


import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Anchor;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;

import com.google.gwt.user.client.ui.RootPanel;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.lonzaNewscenter.client.LonzaMainCollection;
import com.lonzaNewscenter.client.LonzaNewsCenterPage;
import com.newscenter.client.NewsCenterMain;
import com.trial.client.ExistingTrialAccounts;
import com.trial.client.TrialAccountSetup;

public class AdminPage extends Composite implements  EntryPoint,ClickHandler,MouseMoveHandler,
ChangeHandler,SelectionHandler
{
	String name = "";
	private Label labelWelcome = new Label();
	private HorizontalPanel hlabelpanel = new HorizontalPanel();
	private Label newscenterLabel = new Label("newscenter");
	//private Hyperlink newscenterLink = new Hyperlink("newscenter","mainpage");
	private Anchor newscenterLinkAnchor = new Anchor("newscenter","mainpage");
	
	private Label logoutLabel = new Label();
	private HorizontalPanel hpanelLabel = new HorizontalPanel();
	private VerticalPanel vpanel = new VerticalPanel();
	private ManageHeader header = new ManageHeader();
	private HorizontalPanel hpanel = new HorizontalPanel();
	private VerticalPanel contanerPanel = new VerticalPanel();
	private Footer footer = new Footer();
	public static final String INIT_STATE="initstate";
	//public Hyperlink link = new Hyperlink("lo","Login");
	
	RootPanel panel;
	public static String industryName="";
	public static int industryId;
	private DecoratedTabPanel decoratedTab = new DecoratedTabPanel();
	private ListBox selectionListbox = new ListBox();
	private ListBox tagncategorySelectionlist = new ListBox();
	private Label selectionLabel = new Label("Please select any of the following ");
	private HorizontalPanel selectionHpanel = new HorizontalPanel();
	private VerticalPanel contanerVpanel = new VerticalPanel();
	private VerticalPanel containerVtagPanel = new VerticalPanel();
	private ListBox selectionListManage = new ListBox();
	private Label selectionLabelManage = new Label("Please select any of the following ");
	private Label selectionTagManage = new Label("Please select any of the following ");
	private HorizontalPanel selectionHpanelManage = new HorizontalPanel();
	private VerticalPanel contanerVpanelManage = new VerticalPanel();
	private VerticalPanel containerEmailPanel = new VerticalPanel();
	private HorizontalPanel tagHSelection = new HorizontalPanel();
	private String adminemail = "";
	private Statistics statistics;
	private EmailTemplate emailtemplate;
	private VerticalPanel vpStatistics = new VerticalPanel();
	
	public static int userSelectedIndustryId;
	String userIndustryName="";
	int userIndustryId = 0;
	
	NewUserApproval newuser;
	ManageUsers manageuser = new  ManageUsers();
	AdminRegistration adminReg = new AdminRegistration();
	ApprovedUsers approvedUsers = new ApprovedUsers();

	DeleteNewsItems delnewsitems ;
	EditNewsItems editnewsitems ;
	ManageNewsItems managenews ;

	ManageTags manageTags ;
	ManageCategories manageCategories;// = new ManageCategories();
	int clientWidth;
	int clientHeight;

	public AdminPage(){
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		validateUser();
		initWidget(contanerPanel);
	}

	public AdminPage(String email){
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		adminemail = email;
		validateUser();
		initWidget(contanerPanel);
	}

	public void validateUser()
	{
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}

			public void onSuccess(Object result) {
				try{
					String valid = result.toString();
					if(valid.equals("true"))
					{
						getIndustryNameFromSession();
						addAllContents();
					}
					else
					{
						String urlClient = GWT.getModuleBaseURL();
						String[]  url = new String[5];
						url = urlClient.split("/");
						String urlPort = url[0]+"//"+url[2];
						String urlDirection = urlPort+"/";
						//String urlOpen = urlDirection+"NewsCenter/index.html";
						String urlOpen = urlDirection+"com.login.login/index.html";
						
						Window.open(urlPort, "_self", "");	
					}

				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in getUserInfo()");
				}
			}
		};
		service.validateUser(callback);
	}
	
	public void addAllContents()
	{
		userIndustryName = ManageHeader.getUserIndustryName();
		userIndustryId = ManageHeader.getUserIndustryId();
		int userid = ManageHeader.getUserinformation().getUserId();
		setUserSelectedIndustryId(userIndustryId);
		newuser = new NewUserApproval(userIndustryId, adminemail);
		editnewsitems = new EditNewsItems(userIndustryName,userIndustryId);
		managenews = new ManageNewsItems(userIndustryName,userIndustryId);
		manageTags = new ManageTags(userIndustryName,userIndustryId);
		manageCategories = new ManageCategories(userIndustryName,userIndustryId);
		statistics = new Statistics(userIndustryId);
		emailtemplate = new EmailTemplate(userIndustryId,userid);
		System.out.println("The user industry is  "+userIndustryName+userIndustryId);
		hpanel.add(header);
		contanerPanel.setWidth(clientWidth+"px");
		contanerPanel.setHeight(clientHeight+"px");
		hpanel.setStylePrimaryName("hpanelAdmin");

		vpanel.setSpacing(30);
		vpanel.setWidth(clientWidth+"px");
		labelWelcome.setStylePrimaryName("labelHeader");
		newscenterLabel.setStylePrimaryName("labelnewscenter");
		logoutLabel.setStylePrimaryName("logoutlabel");
		selectionLabelManage.setStylePrimaryName("labelSelection");
		selectionLabel.setStylePrimaryName("labelSelection");
		selectionTagManage.setStylePrimaryName("labelSelection");
		
		//selectionListbox.addChangeListener(this);
		selectionListbox.addChangeHandler(this);
		selectionListbox.addItem("To be approved");
		selectionListbox.addItem("Delete");
		selectionListbox.addItem("Add User");
		selectionListbox.addItem("Approved Users");
		selectionListbox.addItem("Setup trial account");
		selectionListbox.addItem("Existing trial accounts");

		//tagncategorySelectionlist.addChangeListener(this);
		tagncategorySelectionlist.addChangeHandler(this);
		tagncategorySelectionlist.addItem("Manage Tags");
		tagncategorySelectionlist.addItem("Manage Categories");

		//selectionListManage.addChangeListener(this);
		selectionListManage.addChangeHandler(this);
		selectionListManage.addItem("Add NewsItems");
		selectionListManage.addItem("Delete NewsItems");
		selectionListManage.addItem("Edit NewsItems");

		HTML homeText = new HTML("Click one the tabs to see more content.");
		
		decoratedTab.setStylePrimaryName("decoratorTab");
		//decoratedTab.addTabListener(this);
		decoratedTab.addSelectionHandler(this);
		
		decoratedTab.add(contanerVpanel, "User");
		decoratedTab.add(containerVtagPanel, "Manage Tags & Categories");
		decoratedTab.add(contanerVpanelManage,"Manage NewsItems");
		decoratedTab.add(containerEmailPanel,"Manage Email Template");
		decoratedTab.add(vpStatistics,"Statistics");
		
		decoratedTab.selectTab(0);

		logoutLabel.setText("Logout");
		//newscenterLinkAnchor.addClickListener(this);
		newscenterLinkAnchor.addClickHandler(this);
		
		//newscenterLabel.addClickListener(this);
		newscenterLabel.addClickHandler(this);
		
		//logoutLabel.addClickListener(this);
		logoutLabel.addClickHandler(this);
		hlabelpanel.add(newscenterLinkAnchor);
		hlabelpanel.add(logoutLabel);
		hlabelpanel.setSpacing(5);

		vpanel.add(labelWelcome);
		vpanel.add(hlabelpanel);
		vpanel.setCellHorizontalAlignment(hlabelpanel, HasHorizontalAlignment.ALIGN_RIGHT);
		vpanel.add(hpanelLabel);
		vpanel.add(decoratedTab);
		vpanel.setCellHorizontalAlignment(decoratedTab, HasHorizontalAlignment.ALIGN_CENTER);

		contanerPanel.add(hpanel);
		contanerPanel.setCellHorizontalAlignment(hpanel, HasHorizontalAlignment.ALIGN_CENTER);
		contanerPanel.add(vpanel);
		contanerPanel.setCellHorizontalAlignment(vpanel, HasHorizontalAlignment.ALIGN_CENTER);
		contanerPanel.add(footer);
	}

	public void onModuleLoad(){
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}
	
	public Image createImage(String url)
	{
		Image image = new Image(url);
		return image;
	}
	
	public Label createLabel(String text)
	{
		Label label = new Label(text);
		//label.addClickListener(this);
		label.addClickHandler(this);
		//label.addMouseListener(this);
		label.addMouseMoveHandler(this);
		return label;
	}
	public void getIndustryNameFromSession(){
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}

			public void onSuccess(Object result) {
				try{
					String Industry[] = (String[])result;
					name = Industry[0];
					int industryid = Integer.parseInt(Industry[2]);
					String userSelectedIndustryName = Industry[3];
					setIndustryId(industryid);
					labelWelcome.setText("Welcome Administrator to "+ userSelectedIndustryName);
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in getIndustryNameFromSession()");
				}
			}
		};
		service.getIndustryNameFromSession(callback);
	}

	/*public void onClick(Widget sender)
	{
		if(sender  instanceof Label) 
		{
			Label label = (Label)sender;
			if(label.getText().equals("New User Aproval"))
			{	
				newuser.onModuleLoad();
			}
			if(label.getText().equals("Manage User"))
			{
				ManageUsers manageuser  = new ManageUsers();
				manageuser.onModuleLoad();
			}
			if(label.getText().equals("Manage Tags"))
			{
				manageTags.onModuleLoad();
			}

			if(label.getText().equals("Manage Newsitems"))
			{
				managenews.onModuleLoad();
			}
			if(label.getText().equals("Delete Newsitems"))
			{
				delnewsitems.onModuleLoad();
			}
			if(label.getText().equals("Edit Newsitems"))
			{
				editnewsitems.onModuleLoad();
			}
			if(label.getText().equals("Manage Categories")){
				manageCategories.onModuleLoad();
			}

			if(label.getText().equals("Lonza Newscenter")){
				LonzaNewsCenterPage lonza = new LonzaNewsCenterPage();
				lonza.onModuleLoad();
			}
			if(label.getText().equals("Logout")){
				NewsCenterMain.logoutclick = 1;
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);
				String urlClient = GWT.getModuleBaseURL();
				String[]  url = new String[5];
				url = urlClient.split("/");
				String urlPort = url[0]+"//"+url[2];
				Window.open(urlPort, "_self", "");
			}
			else if(label.getText().equals("newscenter"))
			    {
					if(getUserSelectedIndustryId()==1)
				   {
					History.back();
					NewsCenterMain main = new NewsCenterMain();
					main.onModuleLoad();
					main.initialize();
				  }
				 else if(getUserSelectedIndustryId()==3)
				 {
					LonzaMainCollection lonzaMain = new LonzaMainCollection();
					lonzaMain.onModuleLoad();
				}
			}
		}
	}*/

	public void onMouseDown(Widget arg0, int arg1, int arg2) 
	{
	}

	/*public void onMouseEnter(Widget sender) {
		if(sender instanceof Label )
		{
			sender.setStylePrimaryName("hover");
		}	
	}*/

	/*public void onMouseLeave(Widget sender) {
		if(sender instanceof Label )
		{
			sender.setStylePrimaryName("labelAdmin");
		}
	}*/

	public void onMouseMove(Widget arg0, int arg1, int arg2) {	
		
	}

	public void onMouseUp(Widget arg0, int arg1, int arg2) {
	
	}
	
	
	public static String getIndustryName() {
		return industryName;
	}
	
	public static void setIndustryName(String industryName) {
		AdminPage.industryName = industryName;
	}
	
	public static int getIndustryId() {
		return industryId;
	}
	
	public static void setIndustryId(int industryId) {
		AdminPage.industryId = industryId;
	}
	
	
	
	/*public void onTabSelected(SourcesTabEvents arg0, int arg1) {
		DecoratedTabPanel decoratorTab = (DecoratedTabPanel)arg0;
		String tabtext = decoratorTab.getTabBar().getTabHTML(arg1);
		
		if(tabtext.equals("User")){
			contanerVpanel.clear();
			containerVtagPanel.clear();
			contanerVpanelManage.clear();
			vpStatistics.clear();
			containerEmailPanel.clear();
			selectionHpanel.setSpacing(7);
			selectionHpanel.add(selectionLabel);
			selectionHpanel.add(selectionListbox);
			selectionListbox.setSelectedIndex(0);
			contanerVpanel.add(selectionHpanel);
			contanerVpanel.add(newuser);
		}
		else if(tabtext.equals("Manage Tags & Categories")){
			contanerVpanel.clear();
			containerVtagPanel.clear();
			contanerVpanelManage.clear();
			vpStatistics.clear();
			containerEmailPanel.clear();
			tagHSelection.setSpacing(7);
			tagHSelection.add(selectionTagManage);
			tagHSelection.add(tagncategorySelectionlist);
			tagncategorySelectionlist.setSelectedIndex(0);
			containerVtagPanel.add(tagHSelection);
			containerVtagPanel.add(manageTags);
		}
		else if(tabtext.equals("Manage NewsItems")){
			contanerVpanel.clear();
			containerVtagPanel.clear();
			contanerVpanelManage.clear();
			vpStatistics.clear();
			containerEmailPanel.clear();
			selectionHpanelManage.setSpacing(7);
			selectionHpanelManage.add(selectionLabelManage);
			selectionHpanelManage.add(selectionListManage);
			selectionListManage.setSelectedIndex(0);
			contanerVpanelManage.add(selectionHpanelManage);
			contanerVpanelManage.add(managenews);
		}
		else if(tabtext.equals("Statistics")){
			contanerVpanel.clear();
			containerVtagPanel.clear();
			contanerVpanelManage.clear();
			vpStatistics.clear();
			containerEmailPanel.clear();
			vpStatistics.add(statistics);
		}
		else if(tabtext.equals("Manage Email Template")){
			contanerVpanel.clear();
			containerVtagPanel.clear();
			contanerVpanelManage.clear();
			vpStatistics.clear();
			containerEmailPanel.clear();
			containerEmailPanel.add(emailtemplate);
		}
	}*/
	
	/*public void onChange(Widget sender) 
	{
		if(sender instanceof ListBox)
		{
			ListBox listbox =(ListBox)sender;
			int index = listbox.getSelectedIndex();
			String name = listbox.getItemText(index);
			if(name.equals("Delete"))
			{
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				ManageUsers mag = new ManageUsers();
				contanerVpanel.add(mag);
			}
			if(name.equals("To be approved")){
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				NewUserApproval newuser = new NewUserApproval(getUserSelectedIndustryId(), adminemail);
				contanerVpanel.add(newuser);
				
			}
			if(name.equals("Add User"))
			{
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				contanerVpanel.add(adminReg);
			}

			if(name.equals("Approved Users"))
			{
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				ApprovedUsers appuser = new ApprovedUsers();
				contanerVpanel.add(appuser);
			}
			
			if(name.equals("Setup trial account"))
			{
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				TrialAccountSetup trialaccountsetup = new TrialAccountSetup();
				trialaccountsetup.setUserinfo(ManageHeader.getUserinformation());
				trialaccountsetup.createUI();
				contanerVpanel.add(trialaccountsetup);
			}
			
			if(name.equals("Existing trial accounts"))
			{
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				ExistingTrialAccounts existingtrialaccount = new ExistingTrialAccounts();
				existingtrialaccount.getTrialAccounts();
				contanerVpanel.add(existingtrialaccount);
			}
			
			if(name.equals("Delete NewsItems")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				delnewsitems = new DeleteNewsItems(userIndustryName,userIndustryId);
				delnewsitems.getAllFieldOfNewsItems();
				contanerVpanelManage.add(delnewsitems);
			}
			if(name.equals("Edit NewsItems")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				contanerVpanelManage.add(editnewsitems);
			}
			if(name.equals("Add NewsItems")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				contanerVpanelManage.add(managenews);
			}

			if(name.equals("Manage Tags")){
				containerVtagPanel.clear();
				containerVtagPanel.add(tagHSelection);
				containerVtagPanel.add(manageTags);
			}
			if(name.equals("Manage Categories")){
				containerVtagPanel.clear();
				containerVtagPanel.add(tagHSelection);
				containerVtagPanel.add(manageCategories);
			}
		}
	}*/
	
	public static int getUserSelectedIndustryId() {
		return userSelectedIndustryId;
	}
	
	public static void setUserSelectedIndustryId(int userSelectedIndustryId) {
		AdminPage.userSelectedIndustryId = userSelectedIndustryId;
	}

	@Override
	public void onClick(ClickEvent event) {
		
		Widget sender = (Widget) event.getSource();
		if(sender  instanceof Label) 
		{
			Label label = (Label)sender;
			if(label.getText().equals("New User Aproval"))
			{	
				newuser.onModuleLoad();
			}
			if(label.getText().equals("Manage User"))
			{
				ManageUsers manageuser  = new ManageUsers();
				manageuser.onModuleLoad();
			}
			if(label.getText().equals("Manage Tags"))
			{
				manageTags.onModuleLoad();
			}

			if(label.getText().equals("Manage Newsitems"))
			{
				managenews.onModuleLoad();
			}
			if(label.getText().equals("Delete Newsitems"))
			{
				delnewsitems.onModuleLoad();
			}
			if(label.getText().equals("Edit Newsitems"))
			{
				editnewsitems.onModuleLoad();
			}
			if(label.getText().equals("Manage Categories")){
				manageCategories.onModuleLoad();
			}

			if(label.getText().equals("Lonza Newscenter")){
				LonzaNewsCenterPage lonza = new LonzaNewsCenterPage();
				lonza.onModuleLoad();
			}
			if(label.getText().equals("Logout")){
				NewsCenterMain.logoutclick = 1;
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);
				String urlClient = GWT.getModuleBaseURL();
				String[]  url = new String[5];
				url = urlClient.split("/");
				String urlPort = url[0]+"//"+url[2];
				Window.open(urlPort, "_self", "");
			}
			else if(label.getText().equals("newscenter"))
			    {
					if(getUserSelectedIndustryId()==1)
				   {
					History.back();
					/*NewsCenterMain main = new NewsCenterMain();
					main.onModuleLoad();
					main.initialize();*/
				  }
				 else if(getUserSelectedIndustryId()==3)
				 {
					LonzaMainCollection lonzaMain = new LonzaMainCollection();
					lonzaMain.onModuleLoad();
				}
			}
		}
	
	    		
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		
		
		
	}

	
	@Override
	public void onChange(ChangeEvent event) {
		
		Widget sender = (Widget)event.getSource();
		

		if(sender instanceof ListBox)
		{
			ListBox listbox =(ListBox)sender;
			int index = listbox.getSelectedIndex();
			String name = listbox.getItemText(index);
			if(name.equals("Delete"))
			{
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				ManageUsers mag = new ManageUsers();
				contanerVpanel.add(mag);
			}
			if(name.equals("To be approved")){
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				NewUserApproval newuser = new NewUserApproval(getUserSelectedIndustryId(), adminemail);
				contanerVpanel.add(newuser);
				
			}
			if(name.equals("Add User"))
			{
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				contanerVpanel.add(adminReg);
			}

			if(name.equals("Approved Users"))
			{
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				ApprovedUsers appuser = new ApprovedUsers();
				contanerVpanel.add(appuser);
			}
			
			if(name.equals("Setup trial account"))
			{
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				TrialAccountSetup trialaccountsetup = new TrialAccountSetup();
				trialaccountsetup.setUserinfo(ManageHeader.getUserinformation());
				trialaccountsetup.createUI();
				contanerVpanel.add(trialaccountsetup);
			}
			
			if(name.equals("Existing trial accounts"))
			{
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				ExistingTrialAccounts existingtrialaccount = new ExistingTrialAccounts();
				existingtrialaccount.getTrialAccounts();
				contanerVpanel.add(existingtrialaccount);
			}
			
			if(name.equals("Delete NewsItems")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				delnewsitems = new DeleteNewsItems(userIndustryName,userIndustryId);
				delnewsitems.getAllFieldOfNewsItems();
				contanerVpanelManage.add(delnewsitems);
			}
			if(name.equals("Edit NewsItems")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				contanerVpanelManage.add(editnewsitems);
			}
			if(name.equals("Add NewsItems")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				contanerVpanelManage.add(managenews);
			}

			if(name.equals("Manage Tags")){
				containerVtagPanel.clear();
				containerVtagPanel.add(tagHSelection);
				containerVtagPanel.add(manageTags);
			}
			if(name.equals("Manage Categories")){
				containerVtagPanel.clear();
				containerVtagPanel.add(tagHSelection);
				containerVtagPanel.add(manageCategories);
			}
		}
		
		
				
	}

	

	

	@Override
	public void onSelection(SelectionEvent event) {
		     Widget argo = (Widget) event.getSource();
		     String  tabtext =event.getSelectedItem().toString(); 
		     
		     
		 	if(tabtext.equals("User")){
		 		System.out.println("String-------"+tabtext);
				contanerVpanel.clear();
				containerVtagPanel.clear();
				contanerVpanelManage.clear();
				vpStatistics.clear();
				containerEmailPanel.clear();
				selectionHpanel.setSpacing(7);
				selectionHpanel.add(selectionLabel);
				selectionHpanel.add(selectionListbox);
				selectionListbox.setSelectedIndex(0);
				contanerVpanel.add(selectionHpanel);
				contanerVpanel.add(newuser);
			}
			else if(tabtext.equals("Manage Tags & Categories")){
				System.out.println("String-------"+tabtext);
				contanerVpanel.clear();
				containerVtagPanel.clear();
				contanerVpanelManage.clear();
				vpStatistics.clear();
				containerEmailPanel.clear();
				tagHSelection.setSpacing(7);
				tagHSelection.add(selectionTagManage);
				tagHSelection.add(tagncategorySelectionlist);
				tagncategorySelectionlist.setSelectedIndex(0);
				containerVtagPanel.add(tagHSelection);
				containerVtagPanel.add(manageTags);
			}
			else if(tabtext.equals("Manage NewsItems")){
				System.out.println("String-------"+tabtext);
				contanerVpanel.clear();
				containerVtagPanel.clear();
				contanerVpanelManage.clear();
				vpStatistics.clear();
				containerEmailPanel.clear();
				selectionHpanelManage.setSpacing(7);
				selectionHpanelManage.add(selectionLabelManage);
				selectionHpanelManage.add(selectionListManage);
				selectionListManage.setSelectedIndex(0);
				contanerVpanelManage.add(selectionHpanelManage);
				contanerVpanelManage.add(managenews);
			}
			else if(tabtext.equals("Statistics")){
				System.out.println("String-------"+tabtext);
				contanerVpanel.clear();
				containerVtagPanel.clear();
				contanerVpanelManage.clear();
				vpStatistics.clear();
				containerEmailPanel.clear();
				vpStatistics.add(statistics);
			}
			else if(tabtext.equals("Manage Email Template")){
				System.out.println("String-------"+tabtext);
				contanerVpanel.clear();
				containerVtagPanel.clear();
				contanerVpanelManage.clear();
				vpStatistics.clear();
				containerEmailPanel.clear();
				containerEmailPanel.add(emailtemplate);
			}
		}

	
	
  
	

	

	

	/*@Override
	public void onMouseEnter(Widget sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseLeave(Widget sender) {
		// TODO Auto-generated method stub
		
	}*/
	
}