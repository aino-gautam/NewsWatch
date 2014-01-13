package com.common.client;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.login.client.UserInformation;

public class ManageHeader extends Composite 
{
	//private Image headerImage = new Image("blue_header.JPG");
	private Header hearingAidHeader = new Header();
	private HorizontalPanel headerBar = new HorizontalPanel();
	private HeaderLonza lonzaHeader = new HeaderLonza(true);
	public static String userIndustryName;
	public static int userIndustryId;
	private static UserInformation userinformation;
	public ManageHeader()
	{
		getIndustryNameFromSession();
		getUserInformationFromSession();
		initWidget(headerBar);
	}

	private void getUserInformationFromSession() {
		CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		ServiceDefTarget target = (ServiceDefTarget)service;
		String moduleRelativeUrl = GWT.getModuleBaseURL()+"common";
		target.setServiceEntryPoint(moduleRelativeUrl);
		AsyncCallback callback = new AsyncCallback(){

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Object arg0) {
				ArrayList list = (ArrayList)arg0;
				userinformation = (UserInformation)list.get(0);
			}
			
		};
		service.getUserInformationObject(callback);
		
	}

	public void getIndustryNameFromSession()
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
					String Industry[] = (String[])result;
					
					int industryid = Integer.parseInt(Industry[2]);
					String userSelectedIndustryName = Industry[3];
					setUserIndustryName(userSelectedIndustryName);
					setUserIndustryId(industryid);
					if(industryid == 1)
					{
					//headerImage.setStylePrimaryName("headerImage");
					headerBar.add(hearingAidHeader);
					headerBar.setStylePrimaryName("headerBar");
					
					}
					else if(industryid == 3)
					{
						headerBar.setStylePrimaryName("headerBar");
						headerBar.clear();
						headerBar.add(lonzaHeader);
						//initWidget(headerBar);
					}
					
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in getIndustryNameFromSession()");
				}

			}
		};
		service.getIndustryNameFromSession(callback);
		
	}

	public static String getUserIndustryName() {
		return userIndustryName;
	}

	public static void setUserIndustryName(String userIndustryName) {
		ManageHeader.userIndustryName = userIndustryName;
	}

	public static int getUserIndustryId() {
		return userIndustryId;
	}

	public static void setUserIndustryId(int userIndustryId) {
		ManageHeader.userIndustryId = userIndustryId;
	}

	public static UserInformation getUserinformation() {
		return userinformation;
	}

	public static void setUserinformation(UserInformation userinformation) {
		ManageHeader.userinformation = userinformation;
	}
	
}
