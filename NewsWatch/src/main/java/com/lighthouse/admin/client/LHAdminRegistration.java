package com.lighthouse.admin.client;

import com.admin.client.AdminRegistration;
import com.admin.client.AdminRegistrationInformation;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.admin.client.service.LHAdminInformationService;
import com.lighthouse.admin.client.service.LHAdminInformationServiceAsync;

public class LHAdminRegistration extends AdminRegistration {

	public LHAdminRegistration(){
		super();
	}
	
	public void onClick(ClickEvent event) {
		Widget sender = (Widget)event.getSource();
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Save")){
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
					
					if(validate(durationStr) && validate(phno)){
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
		public void setUserInfoAdminRegistration(AdminRegistrationInformation adminregInfo){
			LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
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
	}
