package com.common.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.login.client.UserInformation;

public class SessionInformation 
{
	public static ArrayList arrayUserInfo;
	
	
	public void getUserInformation()
	{
		CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		ServiceDefTarget target = (ServiceDefTarget)service;
		String moduleRelativeUrl = GWT.getModuleBaseURL()+"common";
		target.setServiceEntryPoint(moduleRelativeUrl);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}

			public void onSuccess(Object result) {
				try{
					
					ArrayList array = (ArrayList)result;
					setArrayUserInfo(array);
					UserInformation userInfo = (UserInformation)array.get(0);
				
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in removeFromSession()");
				}

			}
		};
		service.getIndustryNameFromSession(callback);
	}


	public static ArrayList getArrayUserInfo() {
		return arrayUserInfo;
	}


	public static void setArrayUserInfo(ArrayList arrayUserInfo) {
		SessionInformation.arrayUserInfo = arrayUserInfo;
	}
}
