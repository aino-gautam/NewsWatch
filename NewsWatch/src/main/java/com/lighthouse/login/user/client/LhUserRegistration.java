package com.lighthouse.login.user.client;

import com.appUtils.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;
import com.login.client.UserInformation;
import com.login.client.UserInformationService;
import com.login.client.UserInformationServiceAsync;
import com.login.client.UserRegistration;

public class LhUserRegistration extends UserRegistration {

	public LhUserRegistration(){
		super();
		userSelectedindustryID = Integer.parseInt(Window.Location.getParameter("NCID"));
	}
	
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
			if(btn == btpushSubmit){
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
								PopUpForAfterRegister popup = new PopUpForAfterRegister("Congratulations! Your account has been successfully created", "redirecttoLogin", "userregistration");
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
}
