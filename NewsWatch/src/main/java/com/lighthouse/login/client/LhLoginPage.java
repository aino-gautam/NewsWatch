package com.lighthouse.login.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.appUtils.client.PopupWidget;
import com.appUtils.client.ValidationException;
import com.appUtils.client.Validators;
import com.appUtils.client.exception.AuthorizationException;
import com.appUtils.client.service.AppUtilsService;
import com.appUtils.client.service.AppUtilsServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.lighthouse.login.client.service.LhLoginService;
import com.lighthouse.login.client.service.LhLoginServiceAsync;
import com.lighthouse.login.user.client.LHForgotPassword;
import com.lighthouse.login.user.client.LhUserRegistration;
import com.lighthouse.login.user.client.domain.LhUser;

public class LhLoginPage extends Composite implements EntryPoint, ClickHandler, ValueChangeHandler, KeyPressHandler,ChangeHandler{
	private TextBox emailtxtBox = new TextBox();
	 private PasswordTextBox password = new PasswordTextBox();
	 private FlexTable flexTable = new FlexTable();
	 private VerticalPanel vPanel = new VerticalPanel();
	 private VerticalPanel verticalPanelAlign = new VerticalPanel();
	 private FlexCellFormatter formatter;
	 private HorizontalPanel subscribehPanel = new HorizontalPanel();
	 private HorizontalPanel rememberhPanel = new HorizontalPanel();
	 private VerticalPanel mainBasePanel = new VerticalPanel();
	 private VerticalPanel linkPanel = new VerticalPanel();
	 private static final int COOKIE_TIMEOUT = 1000 * 60 * 60 * 24 * 365;
	 public static int userSelectedindustryID; 
	 private CheckBox chkBox = new CheckBox();
	 private String email = "";
	 private String value = "";
	 String userSelectedIndustryName;
	 RootPanel panel;
	 static int usernewsCenterId;
	 static String userNewsCenterName = "";
	 private VerticalPanel baseVPanel = new VerticalPanel();
	 private Label emailerrorlbl = new Label();
	 private Label passworderrorlbl = new Label();
	 //private Label invaliduserlbl = new Label();
	 //private Label blanklbl = new Label();
	 //private Label awaitingapprovalerrorlbl = new Label();
	 public boolean flag = true;
	 public static final String INIT_STATE = "startpage";
	 public static final String CHANGEPASSWORD ="changepassword";
	 public static final String REGISTRATION = "userregistration";
	 private Image imLogo;
	 private Image imlogin = new Image("images/login.gif");
	 private PushButton btpush = new PushButton(imlogin);
	 private DockPanel dockPanel = new DockPanel();
	 private String keyVal="";
	 private String keyCombination="18171669";
	 public LhLoginPage(){
		 	
		  String ncid =Window.Location.getParameter("NCID");  
		  String ncName =Window.Location.getParameter("ncName"); 
		  
		  int userncid = Integer.parseInt(ncid);
		  setUsernewsCenterId(userncid);
		  setUserNewsCenterName(ncName);
		  userSelectedindustryID = getUsernewsCenterId();
		  userSelectedIndustryName = getUserNewsCenterName();
		  initWidget(mainBasePanel);
		  isValidApp();
		  
		  //isUserLoggedIn();
			 
	 }
	 
	 public void createUI(){
		// awaitingapprovalerrorlbl.setStylePrimaryName("errorLabels");
		  emailerrorlbl.setStylePrimaryName("errorLabels");
		  passworderrorlbl.setStylePrimaryName("errorLabels");
		 // invaliduserlbl.setStylePrimaryName("errorLabels");
		 // blanklbl.setStylePrimaryName("errorLabels");

		 // awaitingapprovalerrorlbl.setVisible(false);
		  emailtxtBox.addKeyPressHandler(this);
		    
		  password.addKeyPressHandler(this);
		  emailtxtBox.setWidth("290px");
		  password.setWidth("150px");

		  String usercookie = Cookies.getCookie("email");
		  String pwdcookie = Cookies.getCookie("password");

		  if(usercookie!=null && pwdcookie!=null){
			  emailtxtBox.setText(usercookie);
			  password.setText(pwdcookie);
			  chkBox.setValue(true);
		  }
		  
		  btpush.setStylePrimaryName("loginButton");
		  chkBox.addClickHandler(this);
		  btpush.addClickHandler(this);
		  
		  emailtxtBox.addChangeHandler(this);
		  
		  password.addChangeHandler(this);
		  rememberhPanel.add(chkBox);
		  rememberhPanel.setCellVerticalAlignment(chkBox, HasVerticalAlignment.ALIGN_TOP);
		  rememberhPanel.add(createLabel("Remember me"));
		  rememberhPanel.setSpacing(2);
		  
		  subscribehPanel.add(createLabelBold("Not a user?"));
		  subscribehPanel.add(createHyperlink("Subscribe Now", "Subscription"));
		  subscribehPanel.setSpacing(5);
		 
		  formatter = flexTable.getFlexCellFormatter();
		  getClientLogoImage(usernewsCenterId);
		  formatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
//		  flexTable.setWidget(1, 1, blanklbl);
//		  flexTable.setWidget(2, 1, invaliduserlbl);
		  flexTable.setWidget(1, 1, emailerrorlbl);
//		  formatter.setColSpan(4, 1, 2);
//		  flexTable.setWidget(4, 1, awaitingapprovalerrorlbl);
		  flexTable.setWidget(2, 0, createLabel("User e-mail"));
		  formatter.setColSpan(2, 1, 2);
		  flexTable.setWidget(2, 1, emailtxtBox);
		  flexTable.setWidget(3, 1, passworderrorlbl);
		  flexTable.setWidget(4, 0, createLabel("Password"));
		  flexTable.setWidget(4, 1, password);
		  formatter.setWordWrap(4, 2, false);
		  flexTable.setWidget(4, 2, createHyperlink("I forgot my password", "forgotpass"));
		  flexTable.setWidget(5, 1, rememberhPanel);
		  formatter.setAlignment(5, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		  flexTable.setWidget(6, 1, btpush);
		  formatter.setColSpan(8, 1, 2);
		  flexTable.setWidget(8, 1, subscribehPanel);
		 /* flexTable.setCellPadding(3);*/
		  flexTable.setCellSpacing(3);
		  
		  vPanel.add(flexTable);
		  vPanel.setCellHorizontalAlignment(subscribehPanel, HasHorizontalAlignment.ALIGN_CENTER);
		  vPanel.setSpacing(10);

		  verticalPanelAlign.add(vPanel);
		  verticalPanelAlign.setStylePrimaryName("dockpanelLogin");
		  dockPanel.add(verticalPanelAlign,DockPanel.CENTER);
		  dockPanel.setCellHorizontalAlignment(verticalPanelAlign, HasHorizontalAlignment.ALIGN_CENTER);

		  linkPanel.add(dockPanel);
		  baseVPanel.add(linkPanel);
		  baseVPanel.setCellHorizontalAlignment(linkPanel, HasHorizontalAlignment.ALIGN_CENTER);
		  baseVPanel.setCellVerticalAlignment(linkPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		  baseVPanel.setStylePrimaryName("containerPanel");

		  mainBasePanel.add(baseVPanel);
	
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
		  
		  
		  mainBasePanel.setCellVerticalAlignment(baseVPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		  mainBasePanel.setStylePrimaryName("loginbasePanel");
		  History.addValueChangeHandler(this);
		  
	 }
	 
	 private void getClientLogoImage(int newsCenterId) {
		 LhLoginServiceAsync service = (LhLoginServiceAsync)GWT.create(LhLoginService.class);
		 service.getClientLogoImage(newsCenterId,new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				imLogo = new Image("images/marketscapeLogoNewscatalyst.png");
				formatter.setColSpan(0, 0, 3);
				flexTable.setWidget(0, 0,imLogo);
			}
			@Override
			public void onSuccess(String result) {
				if(result!=null){
					imLogo = new Image(result);
					//imLogo.setStylePrimaryName("siloLogo");
				/*	HorizontalPanel logoImageContainer = new HorizontalPanel();
					imLogo.setStylePrimaryName("siloLogo");
					logoImageContainer.setStylePrimaryName("siloLogoImage");
					logoImageContainer.add(imLogo);*/
				
					imLogo.setVisible(false);
					imLogo.addLoadHandler(new LoadHandler() {
						
						@Override
						public void onLoad(LoadEvent event) {
							  Element element = (Element) event.getRelativeElement();
					            if (element == imLogo.getElement()) {
					                int originalHeight = imLogo.getOffsetHeight();
					                int originalWidth = imLogo.getOffsetWidth();
					                if (originalHeight > originalWidth) {
					                	imLogo.setStylePrimaryName("siloLogoHeight");
					                } else {
					                	imLogo.setStylePrimaryName("siloLogoWidth");
					                }
					                imLogo.setVisible(true);
					            }
						}
					});
					
					
					formatter.setColSpan(0, 0, 3);
					flexTable.setWidget(0, 0,imLogo);
				}
				else{
					imLogo = new Image("images/marketscapeLogoNewscatalyst.png");
					//TODO : need to check this for small image icon
					imLogo.addLoadHandler(new LoadHandler() {
						
						@Override
						public void onLoad(LoadEvent event) {
							  Element element = (Element) event.getRelativeElement();
					            if (element == imLogo.getElement()) {
					                int originalHeight = imLogo.getOffsetHeight();
					                int originalWidth = imLogo.getOffsetWidth();
					                if (originalHeight > originalWidth) {
					                	imLogo.setStylePrimaryName("siloLogoHeight");
					                } else {
					                	imLogo.setStylePrimaryName("siloLogoWidth");
					                }
					                imLogo.setVisible(true);
					            }
						}
					});
					formatter.setColSpan(0, 0, 3);
					flexTable.setWidget(0, 0,imLogo);
				}
			}
		 });
	}

	/**
	  * checks if a user is logged in or not. If logged in then redirects appropriately
	  */
	 public void isUserLoggedIn(){
		 LhLoginServiceAsync service = (LhLoginServiceAsync)GWT.create(LhLoginService.class);
		 service.checkUserLogin(new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				createUI();
			}

			@Override
			public void onSuccess(Object result) {
				if(result != null){
					ArrayList arrayList = (ArrayList)result;
					Iterator iter = arrayList.iterator();
					while(iter.hasNext()){
						LhUser user = (LhUser)iter.next();
			        	checkUserAccess(user);
					}
				}else
					createUI();
			}
		});
		 
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
							isUserLoggedIn();
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
	 
	 /**
	  * checks if a user is logged in or not
	  */
	/* public void checkUserLoginFromSession(){
		  UserInformationServiceAsync service = (UserInformationServiceAsync)GWT.create(UserInformationService.class);
		  ServiceDefTarget endpoint = (ServiceDefTarget) service;
		  String moduleRelativeURL = GWT.getModuleBaseURL() + "UserInformation";
		  endpoint.setServiceEntryPoint(moduleRelativeURL);
		  service.checkUserLogin(new AsyncCallback(){
			  
			  public void onFailure(Throwable caught) {
				  caught.printStackTrace();
			  }
			  
			  public void onSuccess(Object result){
				   int industryId= -1;
				   String industryName="";
				   ArrayList arrayList = (ArrayList)result;
				   if(arrayList!=null){
					   industryId = (Integer)arrayList.get(0);
					   industryName = (String)arrayList.get(1);
		
					   if((industryId == getUsernewsCenterId())
							   &&(industryName.equals(getUserNewsCenterName()) )){
						   if(industryId!=3){
							   NewsCenterMain main = NewsCenterMain.getInstance();
							   String token = History.getToken();
							   if(token.equals("adminpage")){
								   main.onHistoryChanged(token);
							   }
							   else{
								   main.onModuleLoad();
								   main.initialize();
							   }
						   }
						   else if(industryId==3){
							   LonzaMainCollection lonzaMain = new LonzaMainCollection();
							   lonzaMain.onModuleLoad();
						   }
				     }
				     else{
				    	 onModuleLoad();
				     }
				   }
				   else{
				    onModuleLoad();
				   }
		  }});
		  
		 }*/
		 
		 public void onModuleLoad() {
		   panel = RootPanel.get(/*"loginContainer"*/);
		   panel.clear();
		   panel.add(this); 
		 }
		 
		 /** 
		  * creates a label widget
		  * @param text - the text to be displayed on the label
		  * @return Label
		  */
		 public Label createLabel(String text) {
		  Label label = new Label(text);
		  label.setStylePrimaryName("LoginLabels");
		  return label;
		 }

		 /**
		  * createa a label with bold (<b>) style
		  * @param text- the text to be displayed on the label
		  * @return Label
		  */
		 public Label createLabelBold(String text) {
		  Label label = new Label(text);
		  label.setStylePrimaryName("boldLabel");
		  return label;
		 }

		 /**
		  * creates a hyperlink
		  * @param text - the text to be displayed on the label
		  * @param link - the text to be displayed as the history token
		  * @return
		  */
		 public Hyperlink createHyperlink(String text,String link)
		 {
			  Hyperlink hylink = new Hyperlink(text,link);
			  hylink.addClickHandler(this);
			  hylink.addStyleDependentName("hyperlinkStyle");
			  return hylink;
		 }

		 /**
			 * checks what the user can access in the application
			 */
			public void checkUserAccess(LhUser loggedInUser){
				if(loggedInUser.getIsApproved() == 1){ // Step 1: check whether approved user or not
					if(loggedInUser.getIsAdmin() == 1){ // Step 2: if admin
						 if(getUsernewsCenterId()!= 3){
							 String urlQueryString = Window.Location.getQueryString();
							 String url = GWT.getHostPageBaseURL() + "LhMain.html"+urlQueryString;
							 Window.open(url, "_self", null);
				         }else if(getUsernewsCenterId() == 3){
				             /*LonzaMainCollection lonzaMain = new LonzaMainCollection();
				             lonzaMain.onModuleLoad();*/
				             //Window.open("http://localhost:8888/com.lonzaNewscenter.lonzaNewscenter/lonzaNewscenter.html", "_self", ""); 
				         }
					}else if(loggedInUser.getIsAdmin() == 2){ //if sales executive
						String url = GWT.getHostPageBaseURL() + "trial.html";
						redirect(url);
					}else{
			            int userIndustryId =loggedInUser.getUserSelectedIndustryID();
			            int durationLeft = loggedInUser.getDurationLeft();
			            if((getUsernewsCenterId() == 3)&&(userIndustryId == 3)){
				             /*LonzaMainCollection lonzaMain = new LonzaMainCollection();
				             lonzaMain.onModuleLoad();*/
				             //Window.open("http://localhost:8888/com.lonzaNewscenter.lonzaNewscenter/lonzaNewscenter.html", "_self", ""); 
			            }
			            else if(getUsernewsCenterId() == userIndustryId){
				             if(durationLeft > 0){
				            	String urlQueryString = Window.Location.getQueryString();
								String url = GWT.getHostPageBaseURL() + "LhMain.html"+urlQueryString;
							    redirect(url);
				             }
				             else{
				            	 emailerrorlbl.setText("Your subscription for this newscatalyst has expired. \n Please contact the administrator for further assistance");
				            	 emailerrorlbl.setVisible(true);
				            	 password.setText("");
						         emailtxtBox.setText("");
						         removeUserFromSession();
				             }
			            }
			            else{
			            	emailerrorlbl.setText("You are not subscribed to this NewsCatalyst");
			            	emailerrorlbl.setVisible(true);
					        password.setText("");
					        emailtxtBox.setText("");
					        removeUserFromSession();
			            }
					}
				}else{ // user not approved yet
			           //awaitingapprovalerrorlbl.setText("Your subscription is awaiting approval.You will \nbe intimated through email as soon as it is approved");
			           //awaitingapprovalerrorlbl.setVisible(true);
					emailerrorlbl.setText("Your subscription is awaiting approval.You will \nbe intimated through email as soon as it is approved");
					emailerrorlbl.setVisible(true);
					password.setText("");
			        emailtxtBox.setText("");
			        removeUserFromSession();
			    }
	}
			
	/**
	 * removes user from session
	 */
	private void removeUserFromSession(){
		 LhLoginServiceAsync service = (LhLoginServiceAsync)GWT.create(LhLoginService.class);
		 service.removeUserFromSession(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				emailerrorlbl.setVisible(false);
			}

			@Override
			public void onSuccess(Void result) {
				emailerrorlbl.setVisible(false);
			}
		});
	}
	
	public static native void redirect(String url)/*-{
  	    $wnd.location = url;
  	}-*/;
		  
	/**
	 * validates the email and password data
	 * @return
	 */
	public boolean validation() {
		 String passwordStr = password.getText();
		 String email = emailtxtBox.getText();
		 Validators validator = new Validators();
		 if(passwordStr.equals("") || email.equals("")){
			 try{
				 validator.blankfield();
				 emailerrorlbl.setText("");
			 }catch(ValidationException e){
				 emailerrorlbl.setText(e.getDisplayMessage());
				 return false;
			 }
		  }
		  return true;
	}

	public void onKeyDown(Widget arg0, char arg1, int arg2) {

	}
	
	public void onKeyUp(Widget arg0, char arg1, int arg2) {
		
	}

	public void onChange(Widget sender){
		Validators validator = new Validators();
		 if(sender instanceof TextBox){
			 TextBox textbox = (TextBox)sender;
			 if(textbox.equals(emailtxtBox)){
				 try {
				     validator.emailValidator(emailtxtBox.getText());
				     emailerrorlbl.setText("");
//				     emailerrorlbl.setText("");
//				     blanklbl.setText("");
				 }catch (ValidationException e){
				     emailerrorlbl.setText(e.getDisplayMessage());
				     flag = false;
				 }
			 }else if(textbox.equals(password)){
			    try{
				     validator.passwordValidator(password.getText());
				     passworderrorlbl.setText("");
				     emailerrorlbl.setText("");
			    }catch (ValidationException e){
				     passworderrorlbl.setText(e.getDisplayMessage());
				     flag = false;
			    }
			 }
		  }
	}

	@Deprecated
	public void onHistoryChanged(String historytoken){
		  if(historytoken.equals("forgotpass")){
			  LHForgotPassword forgot = new LHForgotPassword();
		   dockPanel.clear();
		   dockPanel.add(forgot, DockPanel.CENTER);
		  }else if(historytoken.equals("Subscription")){
		   LhUserRegistration userReg = new LhUserRegistration();
		   dockPanel.clear();
		   dockPanel.add(userReg, DockPanel.CENTER);
		  }else if ((historytoken.equals("startpage"))||historytoken.length()==0){
		   dockPanel.clear();
		   dockPanel.add(verticalPanelAlign, DockPanel.CENTER);
		  }
	}

	public void onSubmit(FormSubmitEvent arg0) {
		  
	}

	public void onSubmitComplete(FormSubmitCompleteEvent arg0) {
		  
	}

	@Override
	public void onKeyPress(KeyPressEvent event) {
		String email = emailtxtBox.getText();
		String pwd = password.getText();
				
		if(email != null && pwd != null){
			if(event.getNativeEvent().getKeyCode()== KeyCodes.KEY_ENTER)
			   validate();
		}	
	}

	@Override
	public void onChange(ChangeEvent event) {
		Widget sender = (Widget) event.getSource(); 
		Validators validator = new Validators();
		if(sender instanceof TextBox){
			TextBox textbox = (TextBox)sender;
			if(textbox.equals(emailtxtBox)){
			    try {
				     validator.emailValidator(emailtxtBox.getText());
				     emailerrorlbl.setText("");
//				     invaliduserlbl.setText("");
//				     blanklbl.setText("");
			    } catch (ValidationException e){
				     emailerrorlbl.setText(e.getDisplayMessage());
				     flag = false;
			    }
			 }else if(textbox.equals(password)){
			    try{
				     validator.passwordValidator(password.getText());
				     passworderrorlbl.setText("");
				     emailerrorlbl.setText("");
			    }catch (ValidationException e){
				     passworderrorlbl.setText(e.getDisplayMessage());
				     flag = false;
			    }
			 }
		}
	}

	public static int getUserSelectedindustryID() {
		return userSelectedindustryID;
	}

	public static void setUserSelectedindustryID(int userSelectedindustryID) {
		LhLoginPage.userSelectedindustryID = userSelectedindustryID;
	}

	public static int getUsernewsCenterId() {
		return usernewsCenterId;
	}

	public static void setUsernewsCenterId(int usernewsCenterId) {
		LhLoginPage.usernewsCenterId = usernewsCenterId;
	}

	public static String getUserNewsCenterName() {
		return userNewsCenterName;
	}

	public static void setUserNewsCenterName(String userNewsCenterName) {
		LhLoginPage.userNewsCenterName = userNewsCenterName;
	}

	@Override
	public void onValueChange(ValueChangeEvent event) {
		String historytoken = History.getToken();
		if(historytoken.equals("forgotpass")){
			   LHForgotPassword forgot = new LHForgotPassword();
			   dockPanel.clear();
			   dockPanel.add(forgot, DockPanel.CENTER);
		}else if(historytoken.equals("Subscription")){
			   LhUserRegistration userReg = new LhUserRegistration();
			   dockPanel.clear();
			   dockPanel.add(userReg, DockPanel.CENTER);
		}else if ((historytoken.equals("startpage"))||historytoken.length()==0){
			   dockPanel.clear();
			   dockPanel.add(verticalPanelAlign, DockPanel.CENTER);
		}
		
	}
	
	/**
	 * validates the user 
	 */
	private void validate(){
		boolean bool = validation();
		if(bool){
			LhUser userInformation = new LhUser();
		    userInformation.setEmail(emailtxtBox.getText().trim());
		    userInformation.setPassword(password.getText().trim());
		   
		    userInformation.setUserSelectedIndustryID(getUsernewsCenterId());
		    userInformation.setIndustryNewsCenterName(getUserNewsCenterName());
		     
		    LhLoginServiceAsync service = (LhLoginServiceAsync)GWT.create(LhLoginService.class);

		    service.validateUser(userInformation, new AsyncCallback() {

				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}

				@Override
				public void onSuccess(Object result) {
					ArrayList valid =(ArrayList) result;
			        if(valid.size() != 0){
				        if(valid.get(0).equals("true")){
				        	LhUser user = (LhUser)valid.get(1);
				        	checkUserAccess(user);
				        }else{
				        	emailerrorlbl.setText("Your email and password does not match");
					         password.setText("");
					         emailtxtBox.setText("");
				        }
			        }else{
			        	emailerrorlbl.setText("You are not registered for this NewsCatalyst");
			        	emailerrorlbl.setVisible(true);
				         password.setText("");
				         emailtxtBox.setText("");
				         removeUserFromSession();
				    }
				}
			});
		}
	}
		
	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() instanceof PushButton){
			PushButton btn = (PushButton)event.getSource();
			validate();
		}else if(event.getSource() instanceof CheckBox){
			CheckBox checkBox = (CheckBox)event.getSource();
			if(checkBox.getValue()){
				    email = emailtxtBox.getText();
				    value = password.getText();
				    Date expires = new Date((new Date()).getTime() + COOKIE_TIMEOUT);
				    Cookies.setCookie("email", email, expires);
				    Cookies.setCookie("password", value, expires);
			    
			 }else if(!checkBox.getValue()){
				    Cookies.removeCookie("email");
				    Cookies.removeCookie("password");
				    emailtxtBox.setText("");
				    password.setText("");
			 }
		}
		
	}
}