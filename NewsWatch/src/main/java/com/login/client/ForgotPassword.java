package com.login.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import com.login.client.UserInformation;
import com.login.client.UserInformationService;
import com.login.client.UserInformationServiceAsync;

import com.appUtils.client.PopUpForForgotPassword;

public class ForgotPassword extends Composite implements ClickHandler{

	private FlexTable forgotpwdflex;
	private FlexCellFormatter formatter;
	protected TextBox emailtxtBox;
	public Button okButton;
	private HorizontalPanel forgotpwdhpanel;
	private VerticalPanel basevpanel;
	private DecoratorPanel decoratorPanel;
	private VerticalPanel container = new VerticalPanel();
	private DockPanel dock;
	public String passwordOfUser;
	public int isapproval = 0;
	private Image imLogo = new Image("images/marketscapeLogoNewscatalyst.png");
	private Image imok = new Image("images/ok.gif");
	protected PushButton btpushOk = new PushButton(imok,this);
	
	public ForgotPassword(){
		forgotpwdflex = new FlexTable();
		formatter = forgotpwdflex.getFlexCellFormatter();
		emailtxtBox = new TextBox();
		emailtxtBox.setWidth("250px");
		okButton = new Button("Ok");
		forgotpwdhpanel = new HorizontalPanel();
		basevpanel = new VerticalPanel();
		decoratorPanel = new DecoratorPanel();
		dock = new DockPanel();

		forgotpwdflex.setCellPadding(5);
		
		formatter.setColSpan(0, 0, 2);
		forgotpwdflex.setWidget(0, 0,imLogo);
		formatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		
		forgotpwdflex.setWidget(1, 0, createLabelBold("Forgot Password?"));
		formatter.setColSpan(1, 0, 2);
		forgotpwdflex.setWidget(2, 0, createLabel("Please enter your e-mail address"));
		formatter.setColSpan(2, 0, 2);
		forgotpwdflex.setWidget(3, 0, createLabel("e-mail"));
		forgotpwdflex.setWidget(3, 1, emailtxtBox);
		formatter.setColSpan(4, 0, 2);
		forgotpwdflex.setWidget(4, 0, btpushOk);
		formatter.setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);
		btpushOk.setStylePrimaryName("loginButton");
		basevpanel.add(forgotpwdflex);
		basevpanel.setSpacing(10);
		decoratorPanel.add(basevpanel);
		container.add(basevpanel);
		container.setStylePrimaryName("forgotpwd");
		dock.add(container, DockPanel.CENTER);
		initWidget(dock);
	}

	public void emailCheck(String email){
		UserInformationServiceAsync service = (UserInformationServiceAsync)GWT.create(UserInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserInformation";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object arg0) {
				isapproval = (Integer)arg0;
			}
		};
		service.emailapprovalCheck(email, callback);
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
	
	/*public void onClick(Widget sender) {
		PushButton button = (PushButton)sender;
		if(button == btpushOk){
			boolean bool = Validation();
			String email = emailtxtBox.getText();
			emailCheck(email);

			if(bool){
			UserInformation userInformation = new UserInformation();
			userInformation.setEmail(emailtxtBox.getText());

			UserInformationServiceAsync service = (UserInformationServiceAsync)GWT.create(UserInformationService.class);
			ServiceDefTarget endpoint = (ServiceDefTarget) service;
			String moduleRelativeURL = GWT.getModuleBaseURL() + "UserInformation";
			endpoint.setServiceEntryPoint(moduleRelativeURL);

			AsyncCallback callback = new AsyncCallback(){

				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}

				public void onSuccess(Object result) {
					try{
					passwordOfUser = (String)result;
					if(!passwordOfUser.equals(""))
					{
					System.out.println("The password in ForgotPasswordLogin() is ...."+passwordOfUser);

					//This is to send the mail to the get password
					UserInformation userInformationforMail = new UserInformation();
					userInformationforMail.setBodyMail("Your password to the system is " +passwordOfUser+ "");
					userInformationforMail.setPasswordMail("");
					userInformationforMail.setRecipientsMail(emailtxtBox.getText());
					userInformationforMail.setSenderMail("no_reply@newscatalyst.com");
					userInformationforMail.setSubjectMail("NewsCatalyst - forgotten password");

					UserInformationServiceAsync serviceforMail = (UserInformationServiceAsync)GWT.create(UserInformationService.class);
					ServiceDefTarget endpointforSentMail = (ServiceDefTarget) serviceforMail;
					String moduleRelativeURLforSentMail = GWT.getModuleBaseURL() + "MailForPassword";
					endpointforSentMail.setServiceEntryPoint(moduleRelativeURLforSentMail);

					AsyncCallback callbackforSentMail = new AsyncCallback(){

						public void onFailure(Throwable caught) {
							caught.printStackTrace();
						}

						public void onSuccess(Object result) {
							try{
								Boolean valid =(Boolean) result;
								if(valid == false){
									if(isapproval == 1){
										PopUpForForgotPassword popupforgot = new PopUpForForgotPassword("A password has been sent to your respective email " ,emailtxtBox.getText(),"redirectToLogin",true);
										popupforgot.setPopupPosition(600, 350);
										popupforgot.show();
									}
									else{
										PopUpForForgotPassword popupforgot = new PopUpForForgotPassword("Your account is awaiting approval. Please contact Administrator for details");
										popupforgot.setPopupPosition(600, 350);
										popupforgot.show();
									}
								}
								else{
									PopUpForForgotPassword popupforgot = new PopUpForForgotPassword("Please check your e-mail,it does't exist: ",emailtxtBox.getText());
									popupforgot.setPopupPosition(600, 350);
									popupforgot.show();
								}
							}
							catch(Exception ex){
								System.out.println("exception in on success");
								ex.printStackTrace();
							}
						}
					};
					serviceforMail.sendMailForForgotPassword(userInformationforMail,callbackforSentMail);
					//end of sending mail function
					okButton.setEnabled(false);
					}
					else
					{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("User does not exist. If you have not registered\nplease click on the Registration link to register yourself");
						popup.setPopupPosition(600, 300);
						popup.show();
					}
			
					}
					catch(Exception ex){
						System.out.println("exception in on success");
						ex.printStackTrace();
					}
				}
			};
			service.forgotPasswordRetrieve(userInformation, callback);
			}
		}	
	}*/

protected boolean Validation() {
		String email = emailtxtBox.getText();
		if(!email.matches("[\\w-+]+(?:\\.[\\w-+]+)*@(?:[\\w-]+\\.)+[a-zA-Z]{2,7}") )
		{
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter a valid e-mail address");
			popup.setPopupPosition(600, 300);
			popup.show();
			return false;
		}
		else
		{
			return true;
		}
	}

@Override
public void onClick(ClickEvent event) {
	Widget sender=(Widget)event.getSource();
	PushButton button = (PushButton)sender;
	if(button == btpushOk){
		boolean bool = Validation();
		String email = emailtxtBox.getText();
		emailCheck(email);

		if(bool){
		UserInformation userInformation = new UserInformation();
		userInformation.setEmail(emailtxtBox.getText());

		UserInformationServiceAsync service = (UserInformationServiceAsync)GWT.create(UserInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "UserInformation";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				try{
				passwordOfUser = (String)result;
				if(!passwordOfUser.equals(""))
				{
				System.out.println("The password in ForgotPasswordLogin() is ...."+passwordOfUser);

				//This is to send the mail to the get password
				UserInformation userInformationforMail = new UserInformation();
				userInformationforMail.setBodyMail("Your password to the system is " +passwordOfUser+ "");
				userInformationforMail.setPasswordMail("");
				userInformationforMail.setRecipientsMail(emailtxtBox.getText());
				userInformationforMail.setSenderMail("no_reply@newscatalyst.com");
				userInformationforMail.setSubjectMail("NewsCatalyst - forgotten password");

				UserInformationServiceAsync serviceforMail = (UserInformationServiceAsync)GWT.create(UserInformationService.class);
				ServiceDefTarget endpointforSentMail = (ServiceDefTarget) serviceforMail;
				String moduleRelativeURLforSentMail = GWT.getModuleBaseURL() + "MailForPassword";
				endpointforSentMail.setServiceEntryPoint(moduleRelativeURLforSentMail);

				AsyncCallback callbackforSentMail = new AsyncCallback(){

					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}

					public void onSuccess(Object result) {
						try{
							Boolean valid =(Boolean) result;
							if(valid == false){
								if(isapproval == 1){
									PopUpForForgotPassword popupforgot = new PopUpForForgotPassword("A password has been sent to your respective email " ,emailtxtBox.getText(),"redirectToLogin",true);
									popupforgot.setPopupPosition(600, 350);
									popupforgot.show();
								}
								else{
									PopUpForForgotPassword popupforgot = new PopUpForForgotPassword("Your account is awaiting approval. Please contact Administrator for details");
									popupforgot.setPopupPosition(600, 350);
									popupforgot.show();
								}
							}
							else{
								PopUpForForgotPassword popupforgot = new PopUpForForgotPassword("Please check your e-mail,it does't exist: ",emailtxtBox.getText());
								popupforgot.setPopupPosition(600, 350);
								popupforgot.show();
							}
						}
						catch(Exception ex){
							System.out.println("exception in on success");
							ex.printStackTrace();
						}
					}
				};
				serviceforMail.sendMailForForgotPassword(userInformationforMail,callbackforSentMail);
				//end of sending mail function
				okButton.setEnabled(false);
				}
				else
				{
					PopUpForForgotPassword popup = new PopUpForForgotPassword("User does not exist. If you have not registered\nplease click on the Registration link to register yourself");
					popup.setPopupPosition(600, 300);
					popup.show();
				}
		
				}
				catch(Exception ex){
					System.out.println("exception in on success");
					ex.printStackTrace();
				}
			}
		};
		service.forgotPasswordRetrieve(userInformation, callback);
		}
	}	
	
}
}