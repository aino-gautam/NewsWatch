package com.admin.client;


import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.DOM;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.user.client.ui.CheckBox;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.common.client.ValidationException;
import com.common.client.Validators;

public class AdminRegistration extends Composite implements ClickHandler,
               MouseMoveHandler,ChangeHandler{

	protected TextBox firstnameTextbox = new TextBox();
	protected TextBox lastnameTextBox = new TextBox();
	protected TextBox companynmTextBox = new TextBox();
	protected TextBox titleTextBox = new TextBox();
	protected TextBox phnoTextBox = new TextBox();
	private PasswordTextBox password = new PasswordTextBox();
	protected PasswordTextBox Repassword = new PasswordTextBox();
	protected TextBox emailTextbox =new TextBox();
	protected TextBox industryName =new TextBox();
	private FlexTable flex = new FlexTable();
	private FlexCellFormatter formatter = flex.getFlexCellFormatter();
	protected TextBox durationTextbox = new TextBox();
	private DecoratorPanel decorator = new DecoratorPanel();
	private DockPanel dock = new DockPanel();
	private HorizontalPanel hpbuttonPanel = new HorizontalPanel();
	private HorizontalPanel hpaneldecorator = new HorizontalPanel();
	RootPanel panel;
	private String industryname="";
	private Button savebtn = new Button("Save");
	private Button clearbtn = new Button("Clear");

	public Validators valid = null;
	private Label errorfirst = new Label();
	private Label errorlast = new Label();
	private Label errorcompany = new Label();
	private Label errortitle = new Label();
	private Label errorphno = new Label();
	private Label erroremail = new Label();
	private CheckBox isAdminCheckbox = new CheckBox();
	int validationcount = 0;
	public boolean flag = true;
	protected RadioButton rbadmin = new RadioButton("user","Administrator");
	protected RadioButton rbsalesexecutive = new RadioButton("user","Sales Executive");
	protected RadioButton rbuser = new RadioButton("user","Subscriber");
	protected AdminRegistrationInformation adminregInfo = new AdminRegistrationInformation();
	public String userCreated;
	
	public AdminRegistration(){
		getIndustryNameFromSession();
		addInFlex();
		getStyleNames();

		
	   //firstnameTextbox.addChangeListener(this);
		firstnameTextbox.addChangeHandler(this);
		
		//lastnameTextBox.addChangeListener(this);
		lastnameTextBox.addChangeHandler(this);
		
		//companynmTextBox.addChangeListener(this);
		companynmTextBox.addChangeHandler(this);
		
		//titleTextBox.addChangeListener(this);
		titleTextBox.addChangeHandler(this);
		
		//.addChangeListener(this);
		phnoTextBox.addChangeHandler(this);
		
		//emailTextbox.addChangeListener(this);
		emailTextbox.addChangeHandler(this);
		
		//savebtn.addClickListener(this);
		savebtn.addClickHandler(this);
		
		//clearbtn.addClickListener(this);
		clearbtn.addClickHandler(this); 
		
		savebtn.setStylePrimaryName("buttonOkAdmin");
		clearbtn.setStylePrimaryName("buttonOkAdmin");

		industryName.setEnabled(false);
		decorator.add(flex);
		hpaneldecorator.add(decorator);
		dock.setSpacing(7);

		hpbuttonPanel.setSpacing(7);
		hpbuttonPanel.add(savebtn);
		hpbuttonPanel.add(clearbtn);

		dock.add(hpaneldecorator, DockPanel.CENTER);
		dock.add(hpbuttonPanel, DockPanel.SOUTH);

		errorfirst.setStylePrimaryName("errorLabels");
		errorlast.setStylePrimaryName("errorLabels");
		errorcompany.setStylePrimaryName("errorLabels");
		errortitle.setStylePrimaryName("errorLabels");
		errorphno.setStylePrimaryName("errorLabels");
		erroremail.setStylePrimaryName("errorLabels");
		rbadmin.setStylePrimaryName("labelTitle");
		rbsalesexecutive.setStylePrimaryName("labelTitle");
		rbuser.setStylePrimaryName("labelTitle");
		initWidget(dock);
	}

	public void getIndustryNameFromSession()
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
					String Industry[] = (String[])result;
					String name = Industry[0];
					int industryid = Integer.parseInt(Industry[2]);
					String userSelectedIndustryName = Industry[3];
					setIndustryname(userSelectedIndustryName);
					if(industryName.getText().equals("")){
					 industryName.setText(getIndustryname());
					}
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryNameFromSession()");
				}
			}
		};
		service.getIndustryNameFromSession(callback);
	}

	public Label createLabel(String text){
		Label label = new Label(text);
		label.setStylePrimaryName("labelTitle");
		return label;
	}

	public void onModuleLoad() {
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}

	public void addInFlex(){
		flex.setWidget(0, 0, createLabelsmall("All Feilds marked (*) are mandatory"));
		formatter.setColSpan(0, 0, 3);
		flex.setWidget(1, 2, errorfirst);
		flex.setWidget(2,0,createLabel("First Name *"));
		flex.setWidget(3, 2, errorlast);
		flex.setWidget(4,0, createLabel("Last Name *"));
		flex.setWidget(5, 2, errorcompany);
		flex.setWidget(6,0, createLabel("Company Name *"));
		flex.setWidget(7, 2, errortitle);
		flex.setWidget(8,0, createLabel("Title"));
		flex.setWidget(9, 2, errorphno);
		flex.setWidget(10,0, createLabel("Phone Number *"));
		flex.setWidget(11, 2, erroremail);
		flex.setWidget(12,0,createLabel("email *"));
		flex.setWidget(13,0,createLabel("Password *"));
		flex.setWidget(14,0,createLabel("Re-Password *"));
		flex.setWidget(15,0,createLabel("Industry"));
		flex.setWidget(16,0,createLabel("Duration *"));
		flex.setWidget(2,2, firstnameTextbox);
		flex.setWidget(4,2, lastnameTextBox);
		flex.setWidget(6,2, companynmTextBox);
		flex.setWidget(8,2, titleTextBox);
		flex.setWidget(10,2, phnoTextBox);
		flex.setWidget(12,2, emailTextbox);
		flex.setWidget(13,2, password);
		flex.setWidget(14,2, Repassword);
		flex.setWidget(15,2, industryName); // "Hearing Aid"
		flex.setWidget(16,2, durationTextbox);
		flex.setCellSpacing(6);
		
		HorizontalPanel hproles = new HorizontalPanel();
		hproles.add(rbadmin);
		hproles.add(rbsalesexecutive);
		hproles.add(rbuser);
		DOM.setStyleAttribute(rbsalesexecutive.getElement(), "marginLeft","8px");
		DOM.setStyleAttribute(rbuser.getElement(), "marginLeft","8px");
		
		flex.setWidget(18, 0, hproles);
		formatter.setColSpan(18, 0, 3);
		/*flex.setWidget(18, 0, createLabel("is Admin"));
		flex.setWidget(18, 2, isAdminCheckbox);*/
	}

	private Label createLabelsmall(String text){
		Label label = new Label(text);
		label.setStylePrimaryName("ThirdLoginTitle");
		return label;
	}

	public void getStyleNames()
	{
		firstnameTextbox.setStylePrimaryName("textboxRegistration");
		lastnameTextBox.setStylePrimaryName("textboxRegistration");
		companynmTextBox.setStylePrimaryName("textboxRegistration");
		titleTextBox.setStylePrimaryName("textboxRegistration");
		phnoTextBox.setStylePrimaryName("textboxRegistration");

		emailTextbox.setStylePrimaryName("textboxRegistration");
		password.setStylePrimaryName("textboxRegistration");
		Repassword.setStylePrimaryName("textboxRegistration");
		industryName.setStylePrimaryName("textboxRegistration");
		durationTextbox.setStylePrimaryName("textboxRegistration");
	}

	public void clearFields(){
		firstnameTextbox.setText("");
		lastnameTextBox.setText("");
		companynmTextBox.setText("");
		titleTextBox.setText("");
		phnoTextBox.setText("");
		password.setText("");
		Repassword.setText("");
		emailTextbox.setText("");
		durationTextbox.setText("");
		isAdminCheckbox.setChecked(false);
	}
	
	/*public void onClick(Widget sender) {
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Clear")){
				clearFields();
			}
			else if(button.getText().equals("Save")){
				
				boolean validate=validation(); 
				if(validate){
					String name = firstnameTextbox.getText();
					String lastname = lastnameTextBox.getText();
					String companynm = companynmTextBox.getText();
					String title = titleTextBox.getText();
					String phno = phnoTextBox.getText();

					String password = Repassword.getText();
					String email = emailTextbox.getText();
					String industry = industryName.getText();
					String durationStr = durationTextbox.getText();
					
					//boolean check = isAdminCheckbox.isChecked();
					if(validate(durationStr) && validate(phno)){
						if(check)
							adminregInfo.setIsAdmin(1);
						else
							adminregInfo.setIsAdmin(0);
						
						if(rbadmin.getValue())
							adminregInfo.setIsAdmin(1);
						else if(rbsalesexecutive.getValue())
							adminregInfo.setIsAdmin(2);
						else if(rbuser.getValue())
							adminregInfo.setIsAdmin(0);
						
						int duration = Integer.parseInt(durationStr);
						long phoneno = Long.parseLong(phno);
						adminregInfo.setName(name);
						adminregInfo.setLastname(lastname);
						adminregInfo.setCompanynm(companynm);
						adminregInfo.setTitle(title);
						adminregInfo.setPhoneno(phoneno);
						adminregInfo.setPassword(password);
						adminregInfo.setEmail(email);
						adminregInfo.setIndustry(industry); // industry
						adminregInfo.setDuration(duration);
						setUserInfoAdminRegistration(adminregInfo);
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter valid entries");
						popup.setPopupPosition(600, 300);
						popup.show();
						button.setEnabled(true);
					}
				}
			}
		}

		else if(sender instanceof Label)
		{
			Label label = (Label)sender;
			if(label.getText().equals("logout")){
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);

				String urlClient = GWT.getModuleBaseURL();
				String[]  url = new String[5];
				url = urlClient.split("/");
				String urlPort = url[0]+"//"+url[2];
				Window.open(urlPort, "_self", "");
			}
			else if(label.getText().equals("Back"))
			{
				AdminPage admin = new AdminPage();
				admin.onModuleLoad();
			}
		}
	}*/

	public boolean validate(String text)
	{
		if(text.matches("[0-9][0-9]*"))
		{
			return true;
		}
		else
			return false;
	}

	public boolean validation(){
		String email = emailTextbox.getText();
		String passwordStr = password.getText();
		String repassStr = Repassword.getText();
		
		if((firstnameTextbox.getText().equals("") ||industryName.getText().equals("") || Repassword.getText().equals("") || password.getText().equals("") || emailTextbox.getText().equals("") || durationTextbox.getText().equals("")) || lastnameTextBox.getText().equals("") || (phnoTextBox.getText().equals("")) || (companynmTextBox.getText().equals("")))
		{
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter required fields");
			popup.setPopupPosition(600, 300);
			popup.show();
			return false;
		}
		
		if(!email.matches(("[\\w-+]+(?:\\.[\\w-+]+)*@(?:[\\w-]+\\.)+[a-zA-Z]{2,7}")))
		{
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Invalid E-mail ID");
			popup.setPopupPosition(600, 300);
			popup.show();
			return false;
		}
		if(validationcount < 5){
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Invalid data.Please enter valid data");
			popup.setPopupPosition(600, 300);
			popup.show();
			return false;
		}
		if(!(passwordStr.trim().equals(repassStr.trim())))
		{
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Please Enter correct password");
			popup.setPopupPosition(600, 300);
			popup.show();
			return false;
		}
		else{
			if(passwordStr.length()<6){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Password should be minimum 6 characters long");
				popup.setPopupPosition(600, 300);
				popup.show();
				return false;
			}
			else if(passwordStr.length() > 16){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Password should not exceed 16 characters");
				popup.setPopupPosition(600, 300);
				popup.show();
				return false;
			}
		}
		
		return true;
	}
	
	
	public void setUserInfoAdminRegistration(AdminRegistrationInformation adminregInfo)
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
					userCreated = (String)result;
					if(userCreated.equals("true")){
						clearFields();
						PopUpForForgotPassword popup = new PopUpForForgotPassword("The following user has been registered");
						popup.setPopupPosition(600, 300);
						popup.show();
					}
					else if(userCreated.equals("false")){
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Duplicate email entry Please enter another email");
						popup.setPopupPosition(600, 300);
						popup.show();
					}
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryName()");
				}
			}
		};
		service.setUserInfoAdminRegistration(adminregInfo, callback);
	}

	public void onMouseDown(Widget arg0, int arg1, int arg2) {
		
	}
	
	public void onMouseEnter(Widget arg0) {
	
	}
	
	public void onMouseLeave(Widget arg0) {

	}

	public void onMouseMove(Widget arg0, int arg1, int arg2) {

	}

	public void onMouseUp(Widget arg0, int arg1, int arg2) {

	}

	/*public void onChange(Widget sender) {
		
		Validators validatorClass=new Validators();
		if(sender instanceof TextBox){
			if(((TextBox)sender).equals(firstnameTextbox)){
				try {
					errorfirst.setText("");
					validatorClass.firstnameValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					validationcount++;
					flag = true;
				} 
				catch (ValidationException e){
					errorfirst.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(lastnameTextBox)){
				try {
					errorlast.setText("");
					new Validators().lastnameValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					validationcount++;
					flag = true;
				} 
				catch (ValidationException e){
					errorlast.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(companynmTextBox)){
				try {
					errorcompany.setText("");
					new Validators().companyValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					validationcount++;
					flag = true;
				} 
				catch (ValidationException e){
					errorcompany.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(titleTextBox)){
				try {
					errortitle.setText("");
					new Validators().titleValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					flag = true;
				} 
				catch (ValidationException e){
					errortitle.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(phnoTextBox)){
				try {
					errorphno.setText("");
					new Validators().phonenoValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					validationcount++;
					flag = true;
				} 
				catch (ValidationException e){
					errorphno.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(emailTextbox)){
				try{
					erroremail.setText("");
					new Validators().emailValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					validationcount++;
					flag = true;
				} 
				catch (ValidationException e){
					erroremail.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
		}
	}*/

	public String getIndustryname() {
		return industryname;
	}

	public void setIndustryname(String industryname) {
		this.industryname = industryname;
	}

	public TextBox getFirstnameTextbox() {
		return firstnameTextbox;
	}

	public void setFirstnameTextbox(TextBox firstnameTextbox) {
		this.firstnameTextbox = firstnameTextbox;
	}

	public TextBox getLastnameTextBox() {
		return lastnameTextBox;
	}

	public void setLastnameTextBox(TextBox lastnameTextBox) {
		this.lastnameTextBox = lastnameTextBox;
	}

	public TextBox getEmailTextbox() {
		return emailTextbox;
	}

	public void setEmailTextbox(TextBox emailTextbox) {
		this.emailTextbox = emailTextbox;
	}

	public RadioButton getRbuser() {
		return rbuser;
	}

	public void setRbuser(RadioButton rbuser) {
		this.rbuser = rbuser;
	}

	public TextBox getIndustryName() {
		return industryName;
	}

	public void setIndustryName(TextBox industryName) {
		this.industryName = industryName;
	}

	public int getValidationcount() {
		return validationcount;
	}

	public void setValidationcount(int validationcount) {
		this.validationcount = validationcount;
	}

	public AdminRegistrationInformation getAdminregInfo() {
		return adminregInfo;
	}

	public void setAdminregInfo(AdminRegistrationInformation adminregInfo) {
		this.adminregInfo = adminregInfo;
	}

	public String getUserCreated() {
		return userCreated;
	}

	public void setUserCreated(String userCreated) {
		this.userCreated = userCreated;
	}

	public PasswordTextBox getPassword() {
		return password;
	}

	public void setPassword(PasswordTextBox password) {
		this.password = password;
	}

	public PasswordTextBox getRepassword() {
		return Repassword;
	}

	public void setRepassword(PasswordTextBox repassword) {
		Repassword = repassword;
	}

	public RadioButton getRbadmin() {
		return rbadmin;
	}

	public void setRbadmin(RadioButton rbadmin) {
		this.rbadmin = rbadmin;
	}

	public RadioButton getRbsalesexecutive() {
		return rbsalesexecutive;
	}

	public void setRbsalesexecutive(RadioButton rbsalesexecutive) {
		this.rbsalesexecutive = rbsalesexecutive;
	}

	

	@Override
	public void onClick(ClickEvent event) {
		
		Widget sender = (Widget)event.getSource();
		
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Clear")){
				clearFields();
			}
			else if(button.getText().equals("Save")){
				
				boolean validate=validation(); 
				if(validate){
					String name = firstnameTextbox.getText();
					String lastname = lastnameTextBox.getText();
					String companynm = companynmTextBox.getText();
					String title = titleTextBox.getText();
					String phno = phnoTextBox.getText();

					String password = Repassword.getText();
					String email = emailTextbox.getText();
					String industry = industryName.getText();
					String durationStr = durationTextbox.getText();
					
					//boolean check = isAdminCheckbox.isChecked();
					if(validate(durationStr) && validate(phno)){
						/*if(check)
							adminregInfo.setIsAdmin(1);
						else
							adminregInfo.setIsAdmin(0);*/
						
						if(rbadmin.getValue())
							adminregInfo.setIsAdmin(1);
						else if(rbsalesexecutive.getValue())
							adminregInfo.setIsAdmin(2);
						else if(rbuser.getValue())
							adminregInfo.setIsAdmin(0);
						
						int duration = Integer.parseInt(durationStr);
						long phoneno = Long.parseLong(phno);
						adminregInfo.setName(name);
						adminregInfo.setLastname(lastname);
						adminregInfo.setCompanynm(companynm);
						adminregInfo.setTitle(title);
						adminregInfo.setPhoneno(phoneno);
						adminregInfo.setPassword(password);
						adminregInfo.setEmail(email);
						adminregInfo.setIndustry(industry); // industry
						adminregInfo.setDuration(duration);
						setUserInfoAdminRegistration(adminregInfo);
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter valid entries");
						popup.setPopupPosition(600, 300);
						popup.show();
						button.setEnabled(true);
					}
				}
			}
		}
				
			
		
	}

	@Override
	public void onChange(ChangeEvent event) {
		
	    Widget sender = (Widget)event.getSource(); 
		Validators validatorClass=new Validators();
		if(sender instanceof TextBox){
			if(((TextBox)sender).equals(firstnameTextbox)){
				try {
					errorfirst.setText("");
					validatorClass.firstnameValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					validationcount++;
					flag = true;
				} 
				catch (ValidationException e){
					errorfirst.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(lastnameTextBox)){
				try {
					errorlast.setText("");
					new Validators().lastnameValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					validationcount++;
					flag = true;
				} 
				catch (ValidationException e){
					errorlast.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(companynmTextBox)){
				try {
					errorcompany.setText("");
					new Validators().companyValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					validationcount++;
					flag = true;
				} 
				catch (ValidationException e){
					errorcompany.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(titleTextBox)){
				try {
					errortitle.setText("");
					new Validators().titleValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					flag = true;
				} 
				catch (ValidationException e){
					errortitle.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(phnoTextBox)){
				try {
					errorphno.setText("");
					new Validators().phonenoValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					validationcount++;
					flag = true;
				} 
				catch (ValidationException e){
					errorphno.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(emailTextbox)){
				try{
					erroremail.setText("");
					new Validators().emailValidator(((TextBox)sender).getText());
					savebtn.setEnabled(true);
					validationcount++;
					flag = true;
				} 
				catch (ValidationException e){
					erroremail.setText(e.getDisplayMessage());
					savebtn.setEnabled(false);
					flag = false;
				}
			}
		}
		
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		
	}

	

}