package com.lighthouse.main.client;

import com.appUtils.client.service.AppUtilsService;
import com.appUtils.client.service.AppUtilsServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

import com.lighthouse.login.user.client.domain.LhUser;

public class LhLogoutPage {
	
	LhUser lhUser;
	
	public LhLogoutPage(LhUser lhUser){
		this.lhUser = lhUser;
	}
	
	public void removeFromSession(int admin)
	{
		final int isadmin = admin;
		//CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		AppUtilsServiceAsync service = (AppUtilsServiceAsync)GWT.create(AppUtilsService.class);
		ServiceDefTarget target = (ServiceDefTarget)service;
		String moduleRelativeUrl = GWT.getModuleBaseURL()+"appUtils";
		target.setServiceEntryPoint(moduleRelativeUrl);
		service.removeFromSession(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}

			@Override
			public void onSuccess(Void result) {
				try{
					System.out.println("Successfull in removin the session");
					redirect(isadmin);
					//Window.open("http://localhost:8888/com.login.login/login.html", "_self", "");
					}catch(Exception ex)
					{
						ex.printStackTrace();
						System.out.println("problem in removeFromSession()");
					}
			}
		});
	}
	
	public void removeFromSession()
	{
		//CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		AppUtilsServiceAsync service = (AppUtilsServiceAsync)GWT.create(AppUtilsService.class);
		ServiceDefTarget target = (ServiceDefTarget)service;
		String moduleRelativeUrl = GWT.getModuleBaseURL()+"appUtils";
		target.setServiceEntryPoint(moduleRelativeUrl);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}

			public void onSuccess(Object result) {
				try{
				System.out.println("Successfull in removin the session");
				//Window.open("http://localhost:8888/com.login.login/login.html", "_self", "");
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in removeFromSession()");
				}

			}
		};
		service.removeFromSession(callback);
	}
	
	public void redirect(int isadmin){
		//NewsCenterMain.logoutclick = 0 ;
		String urlClient = GWT.getModuleBaseURL();
			
			String[] url = new String[5];
			url = urlClient.split("/");
			String urlPort = url[0]+"//"+url[2]+"/"+url[3];
			String urlDirection = urlPort+"/";
			int ncid = lhUser.getUserSelectedNewsCenterID();
			String ncname = lhUser.getIndustryNewsCenterName(); //LoginPage.getUserNewsCenterName().replace(" ", "%20");
			String urlOpen = ""; 
			if(isadmin == 1 ||isadmin == 2){
		        //urlOpen = urlDirection+"NewsCenter/login.html?NCID="+ncid+"&ncName="+ncname;
				urlOpen = urlDirection+"lhlogin.html?NCID="+ncid+"&ncName="+ncname;
				//urlOpen = GWT.getHostPageBaseURL()+"lhlogin.html?gwt.codesvr=127.0.0.1:9997&NCID="+ncid+"&ncName="+ncname;
			}
			else
				urlOpen = "http://www.aalundnetsearch.com";
		
			System.out.println(urlOpen);
			Window.Location.replace(urlOpen);
		
	}
	
	
	public void redirectOnSessionClose(){
		//CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		AppUtilsServiceAsync service = (AppUtilsServiceAsync)GWT.create(AppUtilsService.class);
		ServiceDefTarget target = (ServiceDefTarget)service;
		String moduleRelativeUrl = GWT.getModuleBaseURL()+"appUtils";
		target.setServiceEntryPoint(moduleRelativeUrl);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}

			public void onSuccess(Object result) {
				try{
				System.out.println("Successfull in removin the session");
				String urlClient = GWT.getModuleBaseURL();
				String[] url = new String[5];
				url = urlClient.split("/");
				String urlPort = url[0]+"//"+url[2]+"/"+url[3];
				
				String urlDirection = urlPort+"/";
				int ncid = lhUser.getUserSelectedNewsCenterID();
				String ncname = lhUser.getIndustryNewsCenterName(); //LoginPage.getUserNewsCenterName().replace(" ", "%20");
				String urlOpen = "";
			    //urlOpen = urlDirection+"NewsCenter/login.html?NCID="+ncid+"&ncName="+ncname;
			    urlOpen = urlDirection+"lhlogin.html?NCID="+ncid+"&ncName="+ncname;
				Window.open(urlOpen, "_self", "");
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in removeFromSession()");
				}

			}
		};
		service.removeFromSession(callback);
	}
}
