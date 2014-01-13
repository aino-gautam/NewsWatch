package com.lighthouse.login.user.client;

import com.appUtils.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.login.user.client.service.LhUserService;
import com.lighthouse.login.user.client.service.LhUserServiceAsync;
import com.login.client.ForgotPassword;
import com.login.client.UserInformation;

public class LHForgotPassword extends ForgotPassword {

	private StringBuilder stringBuilder;
	public LHForgotPassword(){
		super();
	}
	
	
	public void onClick(ClickEvent event) {
		Widget sender=(Widget)event.getSource();
		PushButton button = (PushButton)sender;
		final UserInformation userInformation ;
		if(button == btpushOk){
			boolean bool = Validation();
			final String email = emailtxtBox.getText();
			emailCheck(email);

			if(bool){
				userInformation = new UserInformation();
				userInformation.setEmail(emailtxtBox.getText());
	
				LhUserServiceAsync service = (LhUserServiceAsync)GWT.create(LhUserService.class);
				AsyncCallback callback = new AsyncCallback(){
	
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
	
					public void onSuccess(Object result) {
						try{
							passwordOfUser = (String)result;
							if(!passwordOfUser.equals("")){
								System.out.println("The password in ForgotPasswordLogin() is ...."+passwordOfUser);
								//This is to send the mail to the get password
								UserInformation userInformationforMail = new UserInformation();
								stringBuilder = new StringBuilder();
							    
								//userInformationforMail.setBodyMail("Your password to the system is " +passwordOfUser+ "");
								
								formattedBodyMail(email,passwordOfUser);
								userInformationforMail.setBodyMail(stringBuilder.toString());
								userInformationforMail.setPasswordMail("");
								userInformationforMail.setRecipientsMail(emailtxtBox.getText());
								userInformationforMail.setSenderMail("no_reply@newscatalyst.com");
								userInformationforMail.setSubjectMail("NewsCatalyst - forgotten password");
			
								LhUserServiceAsync serviceforMail = (LhUserServiceAsync)GWT.create(LhUserService.class);
								AsyncCallback callbackforSentMail = new AsyncCallback(){
			
									public void onFailure(Throwable caught) {
										caught.printStackTrace();
									}
			
									public void onSuccess(Object result) {
										try{
											Boolean valid =(Boolean) result;
											if(valid){
												if(isapproval == 1){
													PopUpForAfterRegister popupforgot = new PopUpForAfterRegister("A password has been sent to your respective email " ,"redirecttoLogin", "userregistration");
													popupforgot.setPopupPosition(600, 350);
													popupforgot.show();
												}
												else{
													PopUpForAfterRegister popupforgot = new PopUpForAfterRegister("Your account is awaiting approval. Please contact Administrator for details","redirecttoLogin", "userregistration");
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
							else{
								PopUpForForgotPassword popup = new PopUpForForgotPassword("User does not exist. If you have not registered\nplease click on the Registration link to register yourself");
								popup.setPopupPosition(600, 300);
								popup.show();
							}
				
						}catch(Exception ex){
							System.out.println("exception in on success");
							ex.printStackTrace();
						}
					}

					
				};
				service.forgotPasswordRetrieve(userInformation, callback);
			}
		}
	}
	
	private void formattedBodyMail(String email, String passwordOfUser) {
	   	stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:10pt;color:black;text-align:left;padding-left:3px\">Hello "
				+ email + ",<br></td></tr><br>");
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:10pt;color:black;text-align:left;padding-left:3px\"> "
				+ "Your password for the system is "+ passwordOfUser + ".</td></tr><br>");
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:10pt;color:black;text-align:left;padding-left:3px\"> "
				+ "Regards,<br>Admin</td></tr><br>");						
	}
}
