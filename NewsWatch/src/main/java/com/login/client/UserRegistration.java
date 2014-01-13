package com.login.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import com.appUtils.client.PopUpForForgotPassword;

import com.login.client.UserInformation;
import com.login.client.UserInformationService;
import com.login.client.UserInformationServiceAsync;

public class UserRegistration extends Composite implements ClickHandler,ChangeHandler{

	protected TextBox firstnametxtBox;
	protected TextBox lastnametxtBox;
	protected TextBox companyNmtxtBox;
	protected TextBox titletxtBox;
	protected TextBox phnotxtBox;
	protected PasswordTextBox pwdtxtBox;
	protected PasswordTextBox confirmpwdtxtBox;
	protected TextBox mailtxtBox;
	private FlexTable flexregistration;
	private Button submitButton;
	private Button clearButton;
	private HorizontalPanel buttonPanel;
	private VerticalPanel registrationPanel;
	private DecoratorPanel decoratorPanel;
	private DockPanel registrationdock;
	private FlexCellFormatter formatter;
	private VerticalPanel container = new VerticalPanel();
	protected int userSelectedindustryID;
	protected long value;
	private Image imLogo = new Image("images/marketscapeLogoNewscatalyst.png");
	private Validators valid = null;
	private Label errorfirst = new Label();
	private Label errorlast = new Label();
	private Label errorcompany = new Label();
	private Label errortitle = new Label();
	private Label errorphno = new Label();
	private Label erroremail = new Label();
	private Image imsubmit = new Image("images/submit.gif");
	private Image imclear = new Image("images/clear.gif");
	protected PushButton btpushSubmit = new PushButton(imsubmit,this);
	protected PushButton btpushClear = new PushButton(imclear,this);
	private int validationcount = 0;
	public boolean flag = true;

	public UserRegistration(){
		userSelectedindustryID =LoginPage.getUsernewsCenterId();
		firstnametxtBox = new TextBox();
		//firstnametxtBox.addChangeListener(this);
		firstnametxtBox.addChangeHandler(this);
		lastnametxtBox = new TextBox();
		//lastnametxtBox.addChangeListener(this);
		lastnametxtBox.addChangeHandler(this);
		companyNmtxtBox = new TextBox();
		//companyNmtxtBox.addChangeListener(this);
		companyNmtxtBox.addChangeHandler(this);
		titletxtBox = new TextBox();
		//titletxtBox.addChangeListener(this);
		titletxtBox.addChangeHandler(this);
		phnotxtBox = new TextBox();
		//phnotxtBox.addChangeListener(this);
		phnotxtBox.addChangeHandler(this);
		pwdtxtBox = new PasswordTextBox();
		confirmpwdtxtBox = new PasswordTextBox();
		mailtxtBox = new TextBox();
		//mailtxtBox.addChangeListener(this);
		mailtxtBox.addChangeHandler(this);
		flexregistration = new FlexTable();
		submitButton = new Button("Submit");
		clearButton = new Button("Clear");
		buttonPanel = new HorizontalPanel();
		registrationPanel = new VerticalPanel();
		decoratorPanel = new DecoratorPanel();
		
		btpushClear.setStylePrimaryName("loginButton");
		btpushSubmit.setStylePrimaryName("loginButton");
		buttonPanel.add(btpushSubmit);
		buttonPanel.add(btpushClear);
		buttonPanel.setSpacing(8);
		registrationdock = new DockPanel();
		valid = new Validators();
		errorLabelInitialize();

		registrationPanel.add(flexregistration);
		registrationPanel.setStylePrimaryName("containerPanel");

		/*clearButton.addClickListener(this);
		submitButton.addClickListener(this);*/
		clearButton.addClickHandler(this);
		submitButton.addClickHandler(this);
		
		
		submitButton.setWidth("70px");
		clearButton.setWidth("70px");
		registrationPanel.setSpacing(10);
		decoratorPanel.add(registrationPanel);

		container.add(registrationPanel);
		container.setStylePrimaryName("registration");
		registrationdock.add(container, DockPanel.CENTER);
		flexInitialize();
		initWidget(registrationdock);
	}

	public void errorLabelInitialize(){
		errorfirst.setStylePrimaryName("errorLabels");
		errorlast.setStylePrimaryName("errorLabels");
		errorcompany.setStylePrimaryName("errorLabels");
		errortitle.setStylePrimaryName("errorLabels");
		errorphno.setStylePrimaryName("errorLabels");
		erroremail.setStylePrimaryName("errorLabels");

		firstnametxtBox.setWidth("150px");
		lastnametxtBox.setWidth("150px");
		companyNmtxtBox.setWidth("150px");
		titletxtBox.setWidth("150px");
		phnotxtBox.setWidth("150px");
		pwdtxtBox.setWidth("150px");
		confirmpwdtxtBox.setWidth("150px");
		mailtxtBox.setWidth("150px");
		buttonPanel.setSpacing(15);
		buttonPanel.setStylePrimaryName("registerButtonPanel");
	}

	public void flexInitialize(){
		formatter = flexregistration.getFlexCellFormatter();

		formatter.setColSpan(0, 0, 2);
		flexregistration.setWidget(0, 0,imLogo);
		formatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		
		flexregistration.setWidget(1, 0, createLabelBold("New User Registration"));
		formatter.setColSpan(1, 0, 2);
		flexregistration.setWidget(2, 0, createLabel("(Please enter following details)"));
		formatter.setColSpan(2, 0, 2);
		flexregistration.setWidget(3, 0, createLabelsmall("All Fields marked (*) are mandatory"));
		formatter.setColSpan(3, 0, 4);

		flexregistration.setWidget(4, 1, errorfirst);
		flexregistration.setWidget(5, 0, createLabel("First Name *"));
		flexregistration.setWidget(5, 1, firstnametxtBox);

		flexregistration.setWidget(6, 1, errorlast);
		flexregistration.setWidget(7, 0, createLabel("Last Name *"));
		flexregistration.setWidget(7, 1, lastnametxtBox);

		flexregistration.setWidget(8, 1, errorcompany);
		flexregistration.setWidget(9, 0, createLabel("Company Name *"));
		flexregistration.setWidget(9, 1, companyNmtxtBox);

		flexregistration.setWidget(10, 1, errortitle);
		flexregistration.setWidget(11, 0, createLabel("Title"));
		flexregistration.setWidget(11, 1, titletxtBox);

		flexregistration.setWidget(12, 1, errorphno);
		flexregistration.setWidget(13, 0, createLabel("Phone Number *"));
		flexregistration.setWidget(13, 1, phnotxtBox);

		flexregistration.setWidget(14, 1, erroremail);
		flexregistration.setWidget(15, 0, createLabel("E-mail *"));
		flexregistration.setWidget(15, 1, mailtxtBox);

		flexregistration.setWidget(16, 0, createLabel("Password *"));
		flexregistration.setWidget(16, 1, pwdtxtBox);
		flexregistration.setWidget(17, 0, createLabel("Confirm Password *"));
		flexregistration.setWidget(17, 1, confirmpwdtxtBox);
		formatter.setHorizontalAlignment(18, 0, HasHorizontalAlignment.ALIGN_CENTER);
		flexregistration.setWidget(18, 0, btpushSubmit);
		formatter.setHorizontalAlignment(18, 1, HasHorizontalAlignment.ALIGN_CENTER);
		flexregistration.setWidget(18, 1, btpushClear);
		flexregistration.setCellPadding(5);
	}

	private Label createLabel(String text) {
		Label label = new Label(text);
		label.setStylePrimaryName("LoginLabels");
		return label;
	}

	private Label createLabelBold(String text) {
		Label label = new Label(text);
		label.setStylePrimaryName("LoginMainLabel");
		return label;
	}

	private Label createLabelsmall(String text){
		Label label = new Label(text);
		label.setStylePrimaryName("ThirdLoginTitle");
		return label;
	}

	/*public void onClick(Widget sender) {
		if(sender instanceof PushButton){
			PushButton btn = (PushButton)sender;
			if(btn == btpushClear){
				firstnametxtBox.setText("");
				lastnametxtBox.setText("");
				companyNmtxtBox.setText("");
				titletxtBox.setText("");
				phnotxtBox.setText("");
				pwdtxtBox.setText("");
				confirmpwdtxtBox.setText("");
				mailtxtBox.setText("");
			}
			else if(btn == btpushSubmit){
				boolean validate=validating();
				if(validate){
					UserInformation userInformation = new UserInformation();
					userInformation.setFirstname(firstnametxtBox.getText());
					userInformation.setLastname(lastnametxtBox.getText());
					userInformation.setCompanyname(companyNmtxtBox.getText());
					userInformation.setTitle(titletxtBox.getText());
					String text = phnotxtBox.getText();
					if(!text.equals("")){
						value=Long.parseLong(text);
						userInformation.setPhoneno(value);
					}
					userInformation.setEmail(mailtxtBox.getText());
					userInformation.setPassword(pwdtxtBox.getText().trim());
					userInformation.setUserSelectedIndustryID(userSelectedindustryID);

					UserInformationServiceAsync service = (UserInformationServiceAsync)GWT.create(UserInformationService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) service;
					String moduleRelativeURL = GWT.getModuleBaseURL() + "UserInformation";
					endpoint.setServiceEntryPoint(moduleRelativeURL);

					AsyncCallback callback = new AsyncCallback(){

						public void onFailure(Throwable caught) {
							Window.alert("callback failed");
							caught.printStackTrace();
						}

						public void onSuccess(Object result){
							String str= (String)result;
							if(str.equals("Failed")){
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Duplicate email entry Please enter another Email id");
								popup.setPopupPosition(600, 300);
								popup.show();
								return;
							}
							else{
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Congratulations! Your account has been successfully created", "redirecttoLogin", "userregistration");
								popup.setPopupPosition(600, 300);
								popup.show();
							}
						}
					};
					service.saveUserRecord(userInformation, callback);
				}
			}
		}
	}*/

	public boolean validating(){
		String passwordStr = pwdtxtBox.getText();
		String repassStr = confirmpwdtxtBox.getText();

		if((mailtxtBox.getText().equals("")) || (companyNmtxtBox.getText().equals("")) || (firstnametxtBox.getText().equals("")) || (lastnametxtBox.getText().equals("")) || (phnotxtBox.getText().equals(""))){
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Incomplete data. Please enter all required fields");
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
		if(!(passwordStr.trim().equals(repassStr.trim()))){
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter a correct password");
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

	/*public void onChange(Widget sender) {
		Validators validatorClass = new Validators();
		if(sender instanceof TextBox){
			if(((TextBox)sender).equals(firstnametxtBox)){
				try {
					errorfirst.setText("");
					validatorClass.firstnameValidator(((TextBox)sender).getText());
					submitButton.setEnabled(true);
					validationcount++;
				} 
				catch (ValidationException e){
					errorfirst.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(lastnametxtBox)){
				try {
					errorlast.setText("");
					validatorClass.lastnameValidator(((TextBox)sender).getText());//new Validators().lastnameValidator
					submitButton.setEnabled(true);
					validationcount++;
				} 
				catch (ValidationException e){
					errorlast.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(companyNmtxtBox)){
				try {
					errorcompany.setText("");
					validatorClass.companyValidator(((TextBox)sender).getText());
					submitButton.setEnabled(true);
					validationcount++;
				} 
				catch (ValidationException e){
					errorcompany.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(titletxtBox)){
				try {
					errortitle.setText("");
					validatorClass.titleValidator(((TextBox)sender).getText());
					submitButton.setEnabled(true);
				} 
				catch (ValidationException e){
					errortitle.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(phnotxtBox)){
				try {
					errorphno.setText("");
					validatorClass.phonenoValidator(((TextBox)sender).getText());
					submitButton.setEnabled(true);
					validationcount++;
				} 
				catch (ValidationException e){
					errorphno.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(mailtxtBox)){
				try {
					erroremail.setText("");
					validatorClass.emailValidator(((TextBox)sender).getText());
					submitButton.setEnabled(true);
					validationcount++;
				} 
				catch (ValidationException e){
					erroremail.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
		}
	}*/

	@Override
	public void onClick(ClickEvent event) {
		Widget sender=(Widget)event.getSource();
		if(sender instanceof PushButton){
			PushButton btn = (PushButton)sender;
			if(btn == btpushClear){
				firstnametxtBox.setText("");
				lastnametxtBox.setText("");
				companyNmtxtBox.setText("");
				titletxtBox.setText("");
				phnotxtBox.setText("");
				pwdtxtBox.setText("");
				confirmpwdtxtBox.setText("");
				mailtxtBox.setText("");
			}
			else if(btn == btpushSubmit){
				boolean validate=validating();
				if(validate){
					UserInformation userInformation = new UserInformation();
					userInformation.setFirstname(firstnametxtBox.getText());
					userInformation.setLastname(lastnametxtBox.getText());
					userInformation.setCompanyname(companyNmtxtBox.getText());
					userInformation.setTitle(titletxtBox.getText());
					String text = phnotxtBox.getText();
					if(!text.equals("")){
						value=Long.parseLong(text);
						userInformation.setPhoneno(value);
					}
					userInformation.setEmail(mailtxtBox.getText());
					userInformation.setPassword(pwdtxtBox.getText().trim());
					userInformation.setUserSelectedIndustryID(userSelectedindustryID);

					UserInformationServiceAsync service = (UserInformationServiceAsync)GWT.create(UserInformationService.class);
					ServiceDefTarget endpoint = (ServiceDefTarget) service;
					String moduleRelativeURL = GWT.getModuleBaseURL() + "UserInformation";
					endpoint.setServiceEntryPoint(moduleRelativeURL);

					AsyncCallback callback = new AsyncCallback(){

						public void onFailure(Throwable caught) {
							Window.alert("callback failed");
							caught.printStackTrace();
						}

						public void onSuccess(Object result){
							String str= (String)result;
							if(str.equals("Failed")){
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Duplicate email entry Please enter another Email id");
								popup.setPopupPosition(600, 300);
								popup.show();
								return;
							}
							else{
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Congratulations! Your account has been successfully created", "redirecttoLogin", "userregistration");
								popup.setPopupPosition(600, 300);
								popup.show();
							}
						}
					};
					service.saveUserRecord(userInformation, callback);
				}
			}
		}
		
	}

	@Override
	public void onChange(ChangeEvent event) {
		Widget sender=(Widget)event.getSource();
		Validators validatorClass = new Validators();
		if(sender instanceof TextBox){
			if(((TextBox)sender).equals(firstnametxtBox)){
				try {
					errorfirst.setText("");
					validatorClass.firstnameValidator(((TextBox)sender).getText());
					submitButton.setEnabled(true);
					validationcount++;
				} 
				catch (ValidationException e){
					errorfirst.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(lastnametxtBox)){
				try {
					errorlast.setText("");
					validatorClass.lastnameValidator(((TextBox)sender).getText());//new Validators().lastnameValidator
					submitButton.setEnabled(true);
					validationcount++;
				} 
				catch (ValidationException e){
					errorlast.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(companyNmtxtBox)){
				try {
					errorcompany.setText("");
					validatorClass.companyValidator(((TextBox)sender).getText());
					submitButton.setEnabled(true);
					validationcount++;
				} 
				catch (ValidationException e){
					errorcompany.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(titletxtBox)){
				try {
					errortitle.setText("");
					validatorClass.titleValidator(((TextBox)sender).getText());
					submitButton.setEnabled(true);
				} 
				catch (ValidationException e){
					errortitle.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(phnotxtBox)){
				try {
					errorphno.setText("");
					validatorClass.phonenoValidator(((TextBox)sender).getText());
					submitButton.setEnabled(true);
					validationcount++;
				} 
				catch (ValidationException e){
					errorphno.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
			else if(((TextBox)sender).equals(mailtxtBox)){
				try {
					erroremail.setText("");
					validatorClass.emailValidator(((TextBox)sender).getText());
					submitButton.setEnabled(true);
					validationcount++;
				} 
				catch (ValidationException e){
					erroremail.setText(e.getDisplayMessage());
					submitButton.setEnabled(false);
					flag = false;
				}
			}
		}
		
	}
}