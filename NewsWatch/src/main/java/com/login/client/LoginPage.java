package com.login.client;

import java.util.ArrayList;
import java.util.Date;

import com.appUtils.client.Header;
//import com.common.client.LogoutPage;
import com.appUtils.client.PopUpForForgotPassword;
import com.appUtils.client.ValidationException;
import com.appUtils.client.Validators;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
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
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

//import com.lonzaNewscenter.client.LonzaMainCollection;
//import com.newscenter.client.NewsCenterMain;

public class LoginPage extends Composite implements EntryPoint, ClickListener, HistoryListener, KeyPressHandler,ChangeHandler
{

 private TextBox emailtxtBox = new TextBox();
 private PasswordTextBox password = new PasswordTextBox();
 private FlexTable flexTable = new FlexTable();
 private VerticalPanel vPanel = new VerticalPanel();
 private VerticalPanel verticalPanelAlign = new VerticalPanel();
 private FlexCellFormatter formatter;
 private Hyperlink forgotpwdlnk = new Hyperlink();
 private Hyperlink registerlnk = new Hyperlink();
 private HorizontalPanel subscribehPanel = new HorizontalPanel();
 private HorizontalPanel rememberhPanel = new HorizontalPanel();
 private VerticalPanel mainBasePanel = new VerticalPanel();
 private VerticalPanel linkPanel = new VerticalPanel();
 private static final int COOKIE_TIMEOUT = 1000 * 60 * 60 * 24 * 365;
 int userSelectedindustryID; 
 private CheckBox chkBox = new CheckBox();
 private String email = "";
 private String value = "";
 String userSelectedIndustryName;
 RootPanel panel;
 static int usernewsCenterId;
 static String userNewsCenterName = "";
 private VerticalPanel baseVPanel = new VerticalPanel();
 private VerticalPanel headerVPanel = new VerticalPanel();
 private Header headerBar = new Header();
 private Label emailerrorlbl = new Label();
 private Label passworderrorlbl = new Label();
 private Label invaliduserlbl = new Label();
 private Label blanklbl = new Label();
 private Label awaitingapprovalerrorlbl = new Label();
 public boolean flag = true;
 public static final String INIT_STATE = "startpage";
 public static final String CHANGEPASSWORD ="changepassword";
 public static final String REGISTRATION = "userregistration";
 private Image imLogo = new Image("images/marketscapeLogoNewscatalyst.png");
 private Image imlogin = new Image("images/login.gif");
 private PushButton btpush = new PushButton(imlogin,this);
 private DockPanel dockPanel = new DockPanel();

 public LoginPage(){

  String ncid =Window.Location.getParameter("NCID"); 
 // System.out.printf("value:",ncid);
  
  String ncName =Window.Location.getParameter("ncName"); 
  
  int userncid = Integer.parseInt(ncid);
  setUsernewsCenterId(userncid);
  setUserNewsCenterName(ncName);
  
  
  checkUserLoginFromSession();
  
  setValuesFormSession();
  userSelectedindustryID = getUsernewsCenterId();
  userSelectedIndustryName = getUserNewsCenterName();
  
  headerVPanel.add(headerBar);
  headerVPanel.setStylePrimaryName("containerPanel");

  awaitingapprovalerrorlbl.setStylePrimaryName("errorLabels");
  emailerrorlbl.setStylePrimaryName("errorLabels");
  passworderrorlbl.setStylePrimaryName("errorLabels");
  invaliduserlbl.setStylePrimaryName("errorLabels");
  blanklbl.setStylePrimaryName("errorLabels");

  awaitingapprovalerrorlbl.setVisible(false);
 // emailtxtBox.addKeyboardListener(this);
  emailtxtBox.addKeyPressHandler(this);
    
  //password.addKeyboardListener(this);
  password.addKeyPressHandler(this);
  emailtxtBox.setWidth("290px");
  password.setWidth("150px");

  String usercookie = Cookies.getCookie("email");
  String pwdcookie = Cookies.getCookie("password");

  if(usercookie!=null && pwdcookie!=null){
   emailtxtBox.setText(usercookie);
   password.setText(pwdcookie);
   chkBox.setChecked(true);
   
   
  }
  
  btpush.setStylePrimaryName("loginButton");
  chkBox.addClickListener(this);
  
  
  registerlnk.addClickListener(this);
  forgotpwdlnk.addClickListener(this);
  
  //emailtxtBox.addChangeListener(this);
  emailtxtBox.addChangeHandler(this);
  
  //password.addChangeListener(this);
  password.addChangeHandler(this);
  rememberhPanel.add(chkBox);
  rememberhPanel.setCellVerticalAlignment(chkBox, HasVerticalAlignment.ALIGN_TOP);
  rememberhPanel.add(createLabel("Remember me"));
  rememberhPanel.setSpacing(2);
  
  subscribehPanel.add(createLabelBold("Not a user?"));
  subscribehPanel.add(createHyperlink("Subscribe Now", "Subscription"));
  subscribehPanel.setSpacing(5);
 
  formatter = flexTable.getFlexCellFormatter();
  
  formatter.setColSpan(0, 0, 3);
  flexTable.setWidget(0, 0,imLogo);
  formatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
  flexTable.setWidget(1, 1, blanklbl);
  flexTable.setWidget(2, 1, invaliduserlbl);
  flexTable.setWidget(3, 1, emailerrorlbl);
  formatter.setColSpan(4, 1, 2);
  flexTable.setWidget(4, 1, awaitingapprovalerrorlbl);
  flexTable.setWidget(5, 0, createLabel("User e-mail"));
  formatter.setColSpan(5, 1, 2);
  flexTable.setWidget(5, 1, emailtxtBox);
  flexTable.setWidget(6, 1, passworderrorlbl);
  flexTable.setWidget(7, 0, createLabel("Password"));
  flexTable.setWidget(7, 1, password);
  formatter.setWordWrap(7, 2, false);
  flexTable.setWidget(7, 2, createHyperlink("I forgot my password", "forgotpass"));
  flexTable.setWidget(8, 1, rememberhPanel);
  formatter.setAlignment(8, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
  flexTable.setWidget(9, 1, btpush);
  formatter.setColSpan(13, 1, 2);
  flexTable.setWidget(13, 1, subscribehPanel);
  flexTable.setCellPadding(5);

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
  mainBasePanel.setCellVerticalAlignment(baseVPanel, HasVerticalAlignment.ALIGN_MIDDLE);
  mainBasePanel.setStylePrimaryName("loginbasePanel");
  initHistorySupport();
  initWidget(mainBasePanel);
 }

 public void checkUserLoginFromSession(){
  UserInformationServiceAsync service = (UserInformationServiceAsync)GWT.create(UserInformationService.class);
  ServiceDefTarget endpoint = (ServiceDefTarget) service;
  String moduleRelativeURL = GWT.getModuleBaseURL() + "UserInformation";
  endpoint.setServiceEntryPoint(moduleRelativeURL);
  AsyncCallback callback = new AsyncCallback(){
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

    if((industryId == getUsernewsCenterId())&&(industryName.equals(getUserNewsCenterName()) )){
     if(industryId!=3){
      /*NewsCenterMain main = NewsCenterMain.getInstance();
      String token = History.getToken();
      if(token.equals("adminpage")){
       main.onHistoryChanged(token);
      }
      else{
      main.onModuleLoad();
      main.initialize();
      }*/
     }
     else if(industryId==3){
     /* LonzaMainCollection lonzaMain = new LonzaMainCollection();
      lonzaMain.onModuleLoad();*/
     }
    }
    else{
     onModuleLoad();
    }
   }
   else{
    onModuleLoad();
   }
  }};
  service.checkUserLogin( callback);
 }

 public void setValuesFormSession()
 {
  UserInformationServiceAsync service = (UserInformationServiceAsync)GWT.create(UserInformationService.class);
  ServiceDefTarget endpoint = (ServiceDefTarget) service;
  String moduleRelativeURL = GWT.getModuleBaseURL() + "UserInformation";
  endpoint.setServiceEntryPoint(moduleRelativeURL);
  AsyncCallback callback = new AsyncCallback(){
   public void onFailure(Throwable caught) {
    caught.printStackTrace();
   }
   public void onSuccess(Object result)
  {
    
  }};
  service.setValuesToSession(getUsernewsCenterId(),getUserNewsCenterName(),callback);

 }

 
 public void onModuleLoad() {
   panel = RootPanel.get(/*"loginContainer"*/);
   panel.clear();
   panel.add(this); 
 }
 
 public Label createLabel(String text) {
  Label label = new Label(text);
  label.setStylePrimaryName("LoginLabels");
  return label;
 }

 public Label createLabelBold(String text) {
  Label label = new Label(text);
  label.setStylePrimaryName("boldLabel");
  return label;
 }

 public Hyperlink createHyperlink(String text,String link)
 {
  Hyperlink hylink = new Hyperlink(text,link);
  hylink.addClickListener(this);
  hylink.addStyleDependentName("hyperlinkStyle");
  return hylink;
 }

 public void onClick(Widget sender) {
  if(sender instanceof PushButton){
   PushButton btn = (PushButton)sender;
    boolean bool = validation();
    if(bool){
     UserInformation userInformation = new UserInformation();
     userInformation.setEmail(emailtxtBox.getText().trim());
     userInformation.setPassword(password.getText().trim());
     
     userInformation.setUserSelectedIndustryID(getUsernewsCenterId());
     userInformation.setIndustryNewsCenterName(getUserNewsCenterName());

     UserInformationServiceAsync service = (UserInformationServiceAsync)GWT.create(UserInformationService.class);
     ServiceDefTarget endpoint = (ServiceDefTarget) service;
     String moduleRelativeURL = GWT.getModuleBaseURL() + "UserInformation";
     endpoint.setServiceEntryPoint(moduleRelativeURL);

     AsyncCallback callback = new AsyncCallback(){
      public void onFailure(Throwable caught) {
       caught.printStackTrace();
      }
      public void onSuccess(Object result){
       try{
        
        ArrayList valid =(ArrayList) result;
        if(valid.size() != 0){
        if(valid.get(0).equals("true")){
          if((Integer)valid.get(2) == 1){ // checks whether approved user or not
           if((Integer)valid.get(1) == 1){ // checks whether admin or not
            if(getUsernewsCenterId()!= 3){
             //History.newItem("mainpage");
            /* NewsCenterMain main =  NewsCenterMain.getInstance();
             main.setSubscriptionMode((Integer)valid.get(3));
             main.setNewsFilterMode((Integer)valid.get(4));
             main.setUserSelectedIndustryId(userSelectedindustryID);
             main.onModuleLoad(); */
                //main.initialize();
             /*RootPanel.get().clear();
             RootPanel.get().add(main,90,0);*/
            }
            else if(getUsernewsCenterId() == 3)
            {
             /*LonzaMainCollection lonzaMain = new LonzaMainCollection();
             lonzaMain.onModuleLoad();*/
             //Window.open("http://localhost:8888/com.lonzaNewscenter.lonzaNewscenter/lonzaNewscenter.html", "_self", ""); 
            }
             }
             else if((Integer)valid.get(1) == 2){ //for sales executive
              /*TrialEntryPoint trialentrypoint = new TrialEntryPoint();
              trialentrypoint.setNewsCenterId((Integer)valid.get(3));
              trialentrypoint.initialize();
              trialentrypoint.setSubscriptionMode((Integer)valid.get(3));
              trialentrypoint.setNewsFilterMode((Integer)valid.get(4));*/
             // trialentrypoint.onModuleLoad();
             }
             else{
            int userIndustryId =(Integer) valid.get(3);
            int durationLeft = (Integer)valid.get(4);
            if((getUsernewsCenterId() == 3)&&(userIndustryId == 3))
            {
            /* LonzaMainCollection lonzaMain = new LonzaMainCollection();
             lonzaMain.onModuleLoad();*/
             //Window.open("http://localhost:8888/com.lonzaNewscenter.lonzaNewscenter/lonzaNewscenter.html", "_self", ""); 
            }
            else if(getUsernewsCenterId() == userIndustryId)
            {
             if(durationLeft > 0){
              //History.newItem("mainpage");
              /*NewsCenterMain main = NewsCenterMain.getInstance();
              main.setSubscriptionMode((Integer)valid.get(5));
                 main.setNewsFilterMode((Integer)valid.get(6));
              main.onModuleLoad(); */
              //main.initialize();
              /*RootPanel.get().clear();
              RootPanel.get().add(main,90,0);*/
             }
             else{
              PopUpForForgotPassword popup = new PopUpForForgotPassword("Your subscription for this newscatalyst has expired. \n Please contact the administrator for further assistance","redirectToIndexPage","Login");
              popup.setPopupPosition(600, 300);
              popup.show();
              /*LogoutPage logout = new LogoutPage();
              logout.removeFromSession(0);*/
             }
            }
            else{
             PopUpForForgotPassword popup = new PopUpForForgotPassword("You are not Subscribed  for this NewsCatalyst","redirectToIndexPage","Login");
             popup.setPopupPosition(600, 300);
             popup.show();
             /*LogoutPage logout = new LogoutPage();
             logout.removeFromSession(0);*/
            }
           }
          }
          else{
           awaitingapprovalerrorlbl.setText("Your subscription is awaiting approval.You will \nbe intimated through email as soon as it is approved");
           awaitingapprovalerrorlbl.setVisible(true);
           /*LogoutPage logout = new LogoutPage();
           logout.removeFromSession(0);*/
          }
        }
        else
        {
         invaliduserlbl.setText("Your email and password does not match");
         password.setText("");
         emailtxtBox.setText("");
        }
        }
        else{
         PopUpForForgotPassword popup = new PopUpForForgotPassword("You are not registered for this NewsCatalyst","redirectToIndexPage","Login");
         popup.setPopupPosition(600, 300);
         popup.show();
         /*LogoutPage logout = new LogoutPage();
         logout.removeFromSession(0);*/
        }
       }catch(Exception e){
        e.printStackTrace();
       }
      }
     };
     service.validateUser(userInformation, callback);
    }
  }
  else if(sender instanceof CheckBox){
   CheckBox checkBox = (CheckBox)sender;
   if(checkBox.isChecked()){
    email = emailtxtBox.getText();
    value = password.getText();
    Date expires = new Date((new Date()).getTime() + COOKIE_TIMEOUT);
    Cookies.setCookie("email", email, expires);
    Cookies.setCookie("password", value, expires);
    
   }
   else if(checkBox.isChecked()==false)
   {
    Cookies.removeCookie("email");
    Cookies.removeCookie("password");
    emailtxtBox.setText("");
    password.setText("");
   }
  }
 }

 public boolean validation() {
  String passwordStr = password.getText();
  String email = emailtxtBox.getText();
  Validators validator = new Validators();
  if(passwordStr.equals("") || email.equals(""))
  {
   try{
    validator.blankfield();
    blanklbl.setText("");
    }
    catch(ValidationException e)
    {
     blanklbl.setText(e.getDisplayMessage());
     return false;
    }
  }
  return true;
 }

 public void onKeyDown(Widget arg0, char arg1, int arg2) {

 }

/* public void onKeyPress(Widget arg0, char key, int arg2) {
  String email = emailtxtBox.getText();
  String pwd = password.getText();
  if(email != null && pwd != null){
   if(key == KeyboardListener.KEY_ENTER){
    this.onClick(btpush);
   }
  }
 }*/
 public void onKeyUp(Widget arg0, char arg1, int arg2) {
 }

 public static int getUsernewsCenterId() {
  return usernewsCenterId;
 }

 public static void setUsernewsCenterId(int usernewsCenterId) {
  LoginPage.usernewsCenterId = usernewsCenterId;
 }

 public static String getUserNewsCenterName() {
  return userNewsCenterName;
 }

 public static void setUserNewsCenterName(String userNewsCenterName) {
  LoginPage.userNewsCenterName = userNewsCenterName;
 }
 
 /*public void onChange(Widget sender) 
 {
  Validators validator = new Validators();
  if(sender instanceof TextBox)
  {
   TextBox textbox = (TextBox)sender;
   if(textbox.equals(emailtxtBox))
   {
    try 
    {
     validator.emailValidator(emailtxtBox.getText());
     emailerrorlbl.setText("");
     invaliduserlbl.setText("");
     blanklbl.setText("");
    } 
    catch (ValidationException e)
    {
     emailerrorlbl.setText(e.getDisplayMessage());
     flag = false;
    }
   }
   else if(textbox.equals(password))
   {
    try
    {
     validator.passwordValidator(password.getText());
     passworderrorlbl.setText("");
     blanklbl.setText("");
    }
    catch (ValidationException e)
    {
     passworderrorlbl.setText(e.getDisplayMessage());
     flag = false;
    }
   }
  }
 }*/

 public void initHistorySupport(){
  History.addHistoryListener(this);
  String token = History.getToken();
  if (token.length() == 0) {
   onHistoryChanged(INIT_STATE);
  }
  else {
   onHistoryChanged(token);
  }
 }

 public void onHistoryChanged(String historytoken){
  if(historytoken.equals("forgotpass")){
   ForgotPassword forgot = new ForgotPassword();
   dockPanel.clear();
   dockPanel.add(forgot, DockPanel.CENTER);
  }
  else if(historytoken.equals("Subscription")){
   UserRegistration userReg = new UserRegistration();
   dockPanel.clear();
   dockPanel.add(userReg, DockPanel.CENTER);
  }
  else if ((historytoken.equals("startpage"))||historytoken.length()==0){
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
	  char key = event.getCharCode(); 
	  if(email != null && pwd != null){
	   if(key == KeyCodes.KEY_ENTER){
	    this.onClick(btpush);
	   }
	  }
	
}

@Override
public void onChange(ChangeEvent event) {
	Widget sender = (Widget) event.getSource(); 
	  Validators validator = new Validators();
	  if(sender instanceof TextBox)
	  {
	   TextBox textbox = (TextBox)sender;
	   if(textbox.equals(emailtxtBox))
	   {
	    try 
	    {
	     validator.emailValidator(emailtxtBox.getText());
	     emailerrorlbl.setText("");
	     invaliduserlbl.setText("");
	     blanklbl.setText("");
	    } 
	    catch (ValidationException e)
	    {
	     emailerrorlbl.setText(e.getDisplayMessage());
	     flag = false;
	    }
	   }
	   else if(textbox.equals(password))
	   {
	    try
	    {
	     validator.passwordValidator(password.getText());
	     passworderrorlbl.setText("");
	     blanklbl.setText("");
	    }
	    catch (ValidationException e)
	    {
	     passworderrorlbl.setText(e.getDisplayMessage());
	     flag = false;
	    }
	   }
	  }
}
  
}