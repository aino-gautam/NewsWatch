package com.lighthouse.admin.client;


import com.admin.client.AdminInformationService;
import com.admin.client.AdminInformationServiceAsync;
import com.admin.client.ApprovedUsers;
import com.admin.client.EmailTemplate;
import com.admin.client.ManageUsers;
import com.appUtils.client.PopupWidget;
import com.appUtils.client.exception.AuthorizationException;
import com.appUtils.client.service.AppUtilsService;
import com.appUtils.client.service.AppUtilsServiceAsync;
import com.common.client.Footer;
import com.common.client.ManageHeader;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
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
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.admin.client.LhNewUserApproval;
import com.lighthouse.admin.client.service.LHAdminInformationService;
import com.lighthouse.admin.client.service.LHAdminInformationServiceAsync;
import com.lighthouse.common.client.LHStatistics;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.client.LhLogoutPage;
import com.lighthouse.main.client.LhMain;
import com.lonzaNewscenter.client.LonzaMainCollection;
import com.lonzaNewscenter.client.LonzaNewsCenterPage;
import com.trial.client.ExistingTrialAccounts;
import com.trial.client.TrialAccountSetup;

public class LHadmin extends Composite implements  EntryPoint,ClickHandler,MouseMoveHandler,
ChangeHandler,SelectionHandler {

	String name = "";
	private Label labelWelcome = new Label();
	private HorizontalPanel hlabelpanel = new HorizontalPanel();
	private Label newscenterLabel = new Label("newscenter");
	private Anchor newscenterLinkAnchor = new Anchor("newscenter"/*,"mainpage"*/);
	private Anchor previewFeedLinkAnchor = new Anchor("Review feed"/*,"mainpage"*/);
	
	private Label logoutLabel = new Label();
	private HorizontalPanel hpanelLabel = new HorizontalPanel();
	private VerticalPanel vpanel = new VerticalPanel();
	private VerticalPanel contanerPanel = new VerticalPanel();
	private Footer footer = new Footer();
	public static final String INIT_STATE="initstate";
	
	RootPanel panel;
	public static String industryName="";
	public static int industryId;
	private DecoratedTabPanel decoratedTab = new DecoratedTabPanel();
	private ListBox userSelectionListbox = new ListBox();
	private ListBox tagncategorySelectionlist = new ListBox();
	private Label selectionLabel = new Label("Please select any of the following ");
	private HorizontalPanel selectionHpanel = new HorizontalPanel();
	private VerticalPanel contanerVpanel = new VerticalPanel();
	private VerticalPanel containerVtagPanel = new VerticalPanel();
	private VerticalPanel containerVGroupPanel = new VerticalPanel(); 
	private ListBox newsItemSelectionList = new ListBox();
	private Label selectionLabelManage = new Label("Please select any of the following ");
	private Label selectionTagManage = new Label("Please select any of the following ");
	private HorizontalPanel selectionHpanelManage = new HorizontalPanel();
	private VerticalPanel contanerVpanelManage = new VerticalPanel();
	private VerticalPanel containerEmailPanel = new VerticalPanel();
	private VerticalPanel containerSiloPanel = new VerticalPanel();
	private VerticalPanel containerNewsletterPanel = new VerticalPanel();
	private HorizontalPanel tagHSelection = new HorizontalPanel();
	private String adminemail = "";
	private LHStatistics statistics;
	private EmailTemplate emailtemplate;
	private ManageSilo manageSilo;
	private ManageNewsletter newletterTemplate;
	private VerticalPanel vpStatistics = new VerticalPanel();
    private String keyVal="";
    private String keyCombination="18171669";
	
	public static int userSelectedIndustryId;
	String userIndustryName="";
	int userIndustryId = 0;
	int userid =0;
	LhNewUserApproval newuser;
	
	ManageUsers manageuser = new  ManageUsers();
	LHAdminRegistration adminReg = new LHAdminRegistration();
	ApprovedUsers approvedUsers = new ApprovedUsers();
	EditLhUserPermission userPermission;
	LHDeleteNewsItems delnewsitems ;
	LHEditNewsItems editnewsitems ;
	LHManageNewsItems managenews ;
	DeleteNewsReport delnewsreport ;
	EditNewsReport editnewsreport ;
	AddNewsReport addnewsreport;
	SyncNewsFeed syncNewsFeed;
	
	public LhUser userInformation;
	LHManageTags manageTags ;
	LHManageCategories manageCategories;
	LhManageGroups manageGroups;
	public void calculateWidth(){
		int width = Window.getClientWidth()-50;
		contanerPanel.setPixelSize(width, 0);
	}
	public LHadmin(){
		validateUser();
		calculateWidth();
		initWidget(contanerPanel);
	}

	public LHadmin(String email){
		adminemail = email;
		validateUser();
		calculateWidth();
		initWidget(contanerPanel);
	}

	 public boolean isValidApp() {
			try{
			
			   	 AppUtilsServiceAsync service = (AppUtilsServiceAsync)GWT.create(AppUtilsService.class);
				 service.isValidAppAccess(new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
							if (caught instanceof AuthorizationException) {
									PopupPanel popupPanel=new PopupPanel(false, false);
									popupPanel.setSize("100%", "100%");
									popupPanel.center();
									
									AuthorizationException aex=(AuthorizationException)caught;
										if(aex.getAuthExceptionType()==AuthorizationException.SERVICENOTACTIVATED){
												popupPanel.setWidget(new Label("You are not authorised to access this page."));
												popupPanel.show();
											
										}
										if(aex.getAuthExceptionType()==AuthorizationException.SERVICEEXPIRED){
											popupPanel.setWidget(new Label("This service is currently not activated."));
											popupPanel.show();
										
									}
								}
						
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result)
							addAllContents();
						else{
							PopupPanel popupPanel=new PopupPanel(false, false);
							popupPanel.setWidget(new Label("You are not authorised to access this page."));
							popupPanel.setSize("100%", "100%");
							popupPanel.center();
							popupPanel.show();
						}
						}
				});
				
			}catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	
	public void validateUser(){
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}

			public void onSuccess(Object result) {
				try{
					if(result!=null){
						userInformation = (LhUser) result;
						userIndustryId = userInformation.getUserSelectedIndustryID();
						userIndustryName = userInformation.getIndustryNewsCenterName();
						userid = userInformation.getUserId();
						adminemail = userInformation.getEmail();
						setIndustryId(userIndustryId);
						labelWelcome.setText("Welcome Administrator to "+ userIndustryName);
						isValidApp();
					}
					else{
						String urlQueryString = Window.Location.getQueryString();
						String url = GWT.getHostPageBaseURL() + "lhlogin.html"+urlQueryString;
						Window.open(url, "_self", null);
					}

				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getUserInfo()");
				}
			}
		};
		service.validateUserInfo(callback);
	}
	
	public void addAllContents(){
		
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				calculateWidth();
			}
		});
		
		  RootPanel.get().addDomHandler(new KeyDownHandler() {
				
				@Override
				public void onKeyDown(KeyDownEvent event) {
					int key = event.getNativeEvent().getKeyCode();
					keyVal=keyVal+key;
								
					if(!keyCombination.startsWith(keyVal))
						keyVal="";
				
					if(keyVal.equalsIgnoreCase(keyCombination)){
						if(event.isControlKeyDown()&&event.isAltKeyDown()&&event.isShiftKeyDown()&&key==69){
							PopupWidget popupWidget=new PopupWidget();
							//popupWidget.setMessage("This site is developed by");
							keyVal="";
						}else{
							keyVal="";
						}
					}							
				}
			},KeyDownEvent.getType());
		
		setUserSelectedIndustryId(userIndustryId);
		newuser = new LhNewUserApproval(userIndustryId, adminemail);
		editnewsitems = new LHEditNewsItems(userIndustryName,userIndustryId);
		managenews = new LHManageNewsItems(userIndustryName,userIndustryId);
		manageTags = new LHManageTags(userIndustryName,userIndustryId);
		addnewsreport = new AddNewsReport(userIndustryName, userIndustryId);
		editnewsreport = new EditNewsReport(userIndustryName, userIndustryId);
		delnewsreport = new DeleteNewsReport(userIndustryName, userIndustryId);
		manageCategories = new LHManageCategories(userIndustryName,userIndustryId);
		statistics = new LHStatistics(userIndustryId);
		emailtemplate = new EmailTemplate(userIndustryId,userid);
		manageSilo = new ManageSilo(userIndustryId, userid);
		newletterTemplate=new ManageNewsletter(userIndustryId, userid);
		manageGroups = new LhManageGroups(userIndustryName,userIndustryId); 
		syncNewsFeed= new SyncNewsFeed();
		
		System.out.println("The user industry is  "+userIndustryName+userIndustryId);

		labelWelcome.setStylePrimaryName("labelHeader");
		newscenterLabel.setStylePrimaryName("labelnewscenter");
		logoutLabel.setStylePrimaryName("logoutlabel");
		selectionLabelManage.setStylePrimaryName("labelSelection");
		selectionLabel.setStylePrimaryName("labelSelection");
		selectionTagManage.setStylePrimaryName("labelSelection");
		
		//selectionListbox.addChangeListener(this);
		userSelectionListbox.addChangeHandler(this);
		userSelectionListbox.addItem("To be approved");
		userSelectionListbox.addItem("Delete");
		userSelectionListbox.addItem("Add User");
		userSelectionListbox.addItem("Approved Users");
		userSelectionListbox.addItem("Setup trial account");
		userSelectionListbox.addItem("Existing trial accounts");
		userSelectionListbox.addItem("User Permissions");

		//tagncategorySelectionlist.addChangeListener(this);
		tagncategorySelectionlist.addChangeHandler(this);
		tagncategorySelectionlist.addItem("Manage Tags");
		tagncategorySelectionlist.addItem("Manage Categories");
		

		//selectionListManage.addChangeListener(this);
		newsItemSelectionList.addChangeHandler(this);
		newsItemSelectionList.addItem("Add NewsItems");
		newsItemSelectionList.addItem("Delete NewsItems");
		newsItemSelectionList.addItem("Edit NewsItems");
		newsItemSelectionList.addItem("Add NewsReport");
		newsItemSelectionList.addItem("Delete NewsReport");
		newsItemSelectionList.addItem("Edit NewsReport");
	//	newsItemSelectionList.addItem("Synchronize Feed");
		

		HTML homeText = new HTML("Click one the tabs to see more content.");
		
		decoratedTab.setStylePrimaryName("decoratorTab");
		decoratedTab.addSelectionHandler(this);
		
		decoratedTab.add(contanerVpanel, "User");
		decoratedTab.add(containerVGroupPanel, "Manage Groups");
		decoratedTab.add(containerVtagPanel, "Manage Tags & Categories");
		decoratedTab.add(contanerVpanelManage,"Manage NewsItems & NewsReports");
		decoratedTab.add(containerEmailPanel,"Manage Email Template");
		decoratedTab.add(containerSiloPanel,"Manage Silo");
		decoratedTab.add(containerNewsletterPanel,"Manage Newsletter");
		decoratedTab.add(vpStatistics,"Statistics");
		
		
		decoratedTab.selectTab(0);

		logoutLabel.setText("Logout");
		newscenterLinkAnchor.addClickHandler(this);
		previewFeedLinkAnchor.addClickHandler(this);
		newscenterLabel.addClickHandler(this);
		logoutLabel.addClickHandler(this);
		
		hlabelpanel.setWidth("100%");
		hlabelpanel.add(labelWelcome);
		hlabelpanel.setCellHorizontalAlignment(labelWelcome, HasHorizontalAlignment.ALIGN_LEFT);
		hlabelpanel.setCellVerticalAlignment(labelWelcome, HasVerticalAlignment.ALIGN_MIDDLE);
		
		HorizontalPanel hplogoutAnchor = new HorizontalPanel();
		hplogoutAnchor.add(previewFeedLinkAnchor);
		hplogoutAnchor.add(newscenterLinkAnchor);
		hplogoutAnchor.add(logoutLabel);
		hplogoutAnchor.setSpacing(5);
		hlabelpanel.add(hplogoutAnchor);
		hlabelpanel.setCellHorizontalAlignment(hplogoutAnchor, HasHorizontalAlignment.ALIGN_RIGHT);
		hlabelpanel.setCellVerticalAlignment(hplogoutAnchor, HasVerticalAlignment.ALIGN_BOTTOM);
		hlabelpanel.setStylePrimaryName("hlabelPanel");
		//vpanel.add(labelWelcome);
		vpanel.add(hlabelpanel);
		vpanel.setCellHorizontalAlignment(hlabelpanel, HasHorizontalAlignment.ALIGN_RIGHT);
		/*vpanel.setCellVerticalAlignment(labelWelcome, HasVerticalAlignment.ALIGN_MIDDLE);
		vpanel.setCellVerticalAlignment(hlabelpanel, HasVerticalAlignment.ALIGN_TOP);*/
		vpanel.add(hpanelLabel);
		vpanel.add(decoratedTab);
		vpanel.setCellHorizontalAlignment(decoratedTab, HasHorizontalAlignment.ALIGN_CENTER);

		/*contanerPanel.add(hpanel);
		contanerPanel.setCellHorizontalAlignment(hpanel, HasHorizontalAlignment.ALIGN_CENTER);*/
		contanerPanel.add(vpanel);
		contanerPanel.setCellHorizontalAlignment(vpanel, HasHorizontalAlignment.ALIGN_CENTER);
		contanerPanel.add(footer);
	}

	@Override
	public void onModuleLoad() {
		panel = RootPanel.get();
		panel.clear();
		panel.add(this,20,0);
	}
	
	public Image createImage(String url){
		Image image = new Image(url);
		return image;
	}
	
	public Label createLabel(String text){
		Label label = new Label(text);
		label.addClickHandler(this);
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
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryNameFromSession()");
				}
			}
		};
		service.getIndustryNameFromSession(callback);
	}

	public void onMouseDown(Widget arg0, int arg1, int arg2){
	}
	public void onMouseMove(Widget arg0, int arg1, int arg2){	
	}
	public void onMouseUp(Widget arg0, int arg1, int arg2) {
	
	}
	public static String getIndustryName() {
		return industryName;
	}
	
	public static void setIndustryName(String industryName) {
		LHadmin.industryName = industryName;
	}
	
	public static int getIndustryId() {
		return industryId;
	}
	
	public static void setIndustryId(int industryId) {
		LHadmin.industryId = industryId;
	}
	
	public static int getUserSelectedIndustryId() {
		return userSelectedIndustryId;
	}
	
	public static void setUserSelectedIndustryId(int userSelectedIndustryId) {
		LHadmin.userSelectedIndustryId = userSelectedIndustryId;
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		if(sender  instanceof Label){
			Label label = (Label)sender;
			if(label.getText().equals("New User Aproval")){	
				newuser.onModuleLoad();
			}
			if(label.getText().equals("User Permissions")){	
				userPermission.onModuleLoad();
			}
			if(label.getText().equals("Manage User")){
				ManageUsers manageuser  = new ManageUsers();
				manageuser.onModuleLoad();
			}
			if(label.getText().equals("Manage Tags")){
				manageTags.onModuleLoad();
			}
			if(label.getText().equals("Manage Newsitems")){
				managenews.onModuleLoad();
			}
			if(label.getText().equals("Delete Newsitems")){
				delnewsitems.onModuleLoad();
			}
			if(label.getText().equals("Delete NewsReport")){
				delnewsreport.onModuleLoad();
			}
			if(label.getText().equals("Edit NewsReport")){
				editnewsreport.onModuleLoad();
			}
			if(label.getText().equals("Edit Newsitems")){
				editnewsitems.onModuleLoad();
			}
			if(label.getText().equals("Manage Categories")){
				manageCategories.onModuleLoad();
			}
			if(label.getText().equals("Synchronize Feed")){
				syncNewsFeed.onModuleLoad();
			}
            if(label.getText().equals("Manage Groups")){
            	//manageGroups.onModuleLoad();
            }
			
			if(label.getText().equals("Lonza Newscenter")){
				LonzaNewsCenterPage lonza = new LonzaNewsCenterPage();
				lonza.onModuleLoad();
			}
			if(label.getText().equals("Logout")){
				/*NewsCenterMain.logoutclick = 1;
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);
				String urlClient = GWT.getModuleBaseURL();
				String[]  url = new String[5];
				url = urlClient.split("/");
				String urlPort = url[0]+"//"+url[2];
				Window.open(urlPort, "_self", "");*/
				if (LhMain.logoutclick == 0) {
					LhMain.logoutclick = 1;
					saveUserSelection();
				}
			}
		}else if(sender instanceof Anchor){
			Anchor link = (Anchor) sender;
			if(link.getText().equals("newscenter")){
				if(getUserSelectedIndustryId()==3){
					LonzaMainCollection lonzaMain = new LonzaMainCollection();
					lonzaMain.onModuleLoad();
				}
				else{
					History.back();
					String urlQueryString = Window.Location.getQueryString();
					String url = GWT.getHostPageBaseURL() + "LhMain.html"+urlQueryString;
					Window.open(url, "_self", null);
					 
					/*History.back();
				    String url = GWT.getHostPageBaseURL() + "LhMain.html?gwt.codesvr=127.0.0.1:9997";
					//String url = GWT.getHostPageBaseURL() + "LhMain.html";
					Window.open(url, "_self", null);*/
				  } 
				  
			}
			if(link.getText().equals("Review feed")){
				
					String urlQueryString = Window.Location.getQueryString();
					String url = GWT.getHostPageBaseURL() + "ReviewFeed.html"+urlQueryString;
					Window.open(url, "_self", null);
				
				}
		}
	}
	
	public void saveUserSelection(){
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onSuccess(Object result) {
				System.out.println("User Selection Saved...");
				LhLogoutPage logout = new LhLogoutPage(userInformation);
				logout.removeFromSession(1);
			}
			
		};
		service.saveUserGroupItemsSelections(callback);
	}


	@Override
	public void onMouseMove(MouseMoveEvent event) {
	}
	
	@Override
	public void onChange(ChangeEvent event) {
		
		Widget sender = (Widget)event.getSource();
		if(sender instanceof ListBox){
			ListBox listbox =(ListBox)sender;
			int index = listbox.getSelectedIndex();
			String name = listbox.getItemText(index);
			if(name.equals("Delete")){
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				LHManageUsers mag = new LHManageUsers();
				contanerVpanel.add(mag);
			}
			if(name.equals("To be approved")){
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				newuser = new LhNewUserApproval(getUserSelectedIndustryId(), adminemail);
				contanerVpanel.add(newuser);
				
			}
			if(name.equals("Add User")){
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				contanerVpanel.add(adminReg);
			}
			if(name.equals("User Permissions")){
				userPermission=new EditLhUserPermission(userIndustryId);
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				contanerVpanel.add(userPermission);
			}

			if(name.equals("Approved Users")){
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				ApprovedUsers appuser = new ApprovedUsers();
				contanerVpanel.add(appuser);
			}
			
			if(name.equals("Setup trial account")){
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				TrialAccountSetup trialaccountsetup = new TrialAccountSetup();
				trialaccountsetup.setUserinfo(ManageHeader.getUserinformation());
				trialaccountsetup.createUI();
				contanerVpanel.add(trialaccountsetup);
			}
			
			if(name.equals("Existing trial accounts")){
				contanerVpanel.clear();
				contanerVpanel.add(selectionHpanel);
				ExistingTrialAccounts existingtrialaccount = new ExistingTrialAccounts();
				existingtrialaccount.getTrialAccounts();
				contanerVpanel.add(existingtrialaccount);
			}
			
			if(name.equals("Delete NewsItems")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				delnewsitems = new LHDeleteNewsItems(userIndustryName,userIndustryId);
				delnewsitems.getAllFieldOfNewsItems();
				contanerVpanelManage.add(delnewsitems);
			}
			if(name.equals("Edit NewsItems")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				editnewsitems = new LHEditNewsItems(userIndustryName,userIndustryId);
				contanerVpanelManage.add(editnewsitems);
			}
			if(name.equals("Add NewsItems")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				contanerVpanelManage.add(managenews);
			}
			if(name.equals("Delete NewsReport")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				delnewsreport = new DeleteNewsReport(userIndustryName,userIndustryId);
				contanerVpanelManage.add(delnewsreport);
			}
			if(name.equals("Edit NewsReport")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				editnewsreport = new EditNewsReport(userIndustryName, userIndustryId);
				contanerVpanelManage.add(editnewsreport);
			}
			if(name.equals("Add NewsReport")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				contanerVpanelManage.add(addnewsreport);
			}
			if(name.equals("Manage Tags")){
				containerVtagPanel.clear();
				containerVtagPanel.add(tagHSelection);
				manageTags.clearCatList();
				containerVtagPanel.add(manageTags);
			}
			if(name.equals("Manage Categories")){
				containerVtagPanel.clear();
				containerVtagPanel.add(tagHSelection);
				containerVtagPanel.add(manageCategories);
			}
			if(name.equals("Synchronize Feed")){
				contanerVpanelManage.clear();
				contanerVpanelManage.add(selectionHpanelManage);
				contanerVpanelManage.add(syncNewsFeed);
			}
		}
	}

	@Override
	public void onSelection(SelectionEvent event) {
		
		     TabPanel tabPanel = (TabPanel) event.getSource();
		     int  index = (Integer) event.getSelectedItem();
		     String tabtext = tabPanel.getTabBar().getTabHTML(index);
		     
		 	if(tabtext.equals("User")){
		 		System.out.println("String-------"+tabtext);
				contanerVpanel.clear();
				containerVtagPanel.clear();
				contanerVpanelManage.clear();
				vpStatistics.clear();
				containerEmailPanel.clear();
				containerSiloPanel.clear();
				containerNewsletterPanel.clear();
				selectionHpanel.setSpacing(7);
				selectionHpanel.add(selectionLabel);
				selectionHpanel.add(userSelectionListbox);
				userSelectionListbox.setSelectedIndex(0);
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
				containerSiloPanel.clear();
				containerNewsletterPanel.clear();
				tagHSelection.setSpacing(7);
				tagHSelection.add(selectionTagManage);
				tagHSelection.add(tagncategorySelectionlist);
				tagncategorySelectionlist.setSelectedIndex(0);
				containerVtagPanel.add(tagHSelection);
				containerVtagPanel.add(manageTags);
			}
			else if(tabtext.equals("Manage NewsItems & NewsReports")){
				System.out.println("String-------"+tabtext);
				contanerVpanel.clear();
				containerVtagPanel.clear();
				contanerVpanelManage.clear();
				vpStatistics.clear();
				containerEmailPanel.clear();
				containerSiloPanel.clear();
				containerNewsletterPanel.clear();
				selectionHpanelManage.setSpacing(7);
				selectionHpanelManage.add(selectionLabelManage);
				selectionHpanelManage.add(newsItemSelectionList);
				newsItemSelectionList.setSelectedIndex(0);
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
				containerSiloPanel.clear();
				containerNewsletterPanel.clear();
				vpStatistics.add(statistics);
			}
			else if(tabtext.equals("Manage Email Template")){
				System.out.println("String-------"+tabtext);
				contanerVpanel.clear();
				containerVtagPanel.clear();
				contanerVpanelManage.clear();
				vpStatistics.clear();
				containerEmailPanel.clear();
				containerSiloPanel.clear();
				containerNewsletterPanel.clear();
				
				containerEmailPanel.add(emailtemplate);
			}
			else if(tabtext.equals("Manage Silo")){
				System.out.println("String-------"+tabtext);
				contanerVpanel.clear();
				containerVtagPanel.clear();
				contanerVpanelManage.clear();
				vpStatistics.clear();
				containerEmailPanel.clear();
				containerSiloPanel.clear();
				containerNewsletterPanel.clear();
				manageSilo.initialize();
				containerSiloPanel.add(manageSilo);
			}
			else if(tabtext.equals("Manage Groups")){
				System.out.println("String-------"+tabtext);
				contanerVpanel.clear();
				containerVtagPanel.clear();
				contanerVpanelManage.clear();
				vpStatistics.clear();
				containerEmailPanel.clear();
				containerSiloPanel.clear();
				containerNewsletterPanel.clear();
				containerVGroupPanel.clear();
				
				manageGroups.initialise();
				containerVGroupPanel.add(manageGroups);
				
			
			}
			else if(tabtext.equals("Manage Newsletter")){
				System.out.println("String-------"+tabtext);
				contanerVpanel.clear();
				containerVtagPanel.clear();
				contanerVpanelManage.clear();
				vpStatistics.clear();
				containerEmailPanel.clear();
				containerSiloPanel.clear();
				containerNewsletterPanel.clear();
				newletterTemplate.createUI();
				containerNewsletterPanel.add(newletterTemplate);
			}
		}

}