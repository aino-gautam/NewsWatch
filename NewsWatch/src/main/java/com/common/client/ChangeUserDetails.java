package com.common.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class ChangeUserDetails 
{
	String username;
	String userpassword;
	String useremail;
	int clientwidthforpopup;
	int clientheightforpopup;
	public ChangeUserDetails(String password,String email)
	{
		userpassword = password;
		useremail= email;
		int clientwidth = Window.getClientWidth();
		int clientHeight = Window.getClientHeight();
		clientwidthforpopup = clientwidth/2;
		clientheightforpopup = clientHeight/2-150;
	}
	public void changeuserdetails(String userpassword,String useremail)
	{
		CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		ServiceDefTarget target = (ServiceDefTarget)service;
		String moduleRelativeUrl = GWT.getModuleBaseURL()+"common";
		target.setServiceEntryPoint(moduleRelativeUrl);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				PopUpForForgotPassword popup = new PopUpForForgotPassword("There is some problem in updating please try again");
				popup.setPopupPosition(600, 300);
				popup.show();
			}

			public void onSuccess(Object result) {
				try{
					
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Your password has been updated","redirectLonza","changedetails");
					popup.setPopupPosition(clientwidthforpopup, clientheightforpopup);
					popup.show();	
					
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in changeuserdetails()");
				}

			}
		};
		service.changeuserdetails(userpassword,useremail,callback);
	}
}
