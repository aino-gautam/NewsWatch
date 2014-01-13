package com.lighthouse.newspage.client;

import java.util.ArrayList;

import com.appUtils.client.NotificationPopup;
import com.appUtils.client.NotificationPopup.NotificationType;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.lighthouse.login.client.service.LhLoginService;
import com.lighthouse.login.client.service.LhLoginServiceAsync;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.client.service.LhNewsProviderService;
import com.lighthouse.main.client.service.LhNewsProviderServiceAsync;

/**
 * 
 * @author kiran@ensarm.com This class for NewsItemHome entryPoint
 * 
 */
public class NewsItemHome extends Composite implements EntryPoint,SubmitCompleteHandler {
	
	
	NotificationPopup ntp;
	//LhUser user=null;
	@Override
	public void onModuleLoad() {
			
		String lockedNews= Window.Location.getParameter("lockednid");
		String uid= Window.Location.getParameter("uid");
		String nid= Window.Location.getParameter("NCID");
		String display= Window.Location.getParameter("display");
		
		if(uid!=null && display!=null){
			LhNewsProviderServiceAsync newsProviderService = GWT.create(LhNewsProviderService.class);
			newsProviderService.getUser(Integer.parseInt(uid), Integer.parseInt(nid), new AsyncCallback<LhUser>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(LhUser user) {
					String display= Window.Location.getParameter("display");
					if(display!=null){
						if(display.equals("true")){
							initialize(user);
						}
					}else{
					String urlQueryString = Window.Location.getQueryString();
					String url = GWT.getHostPageBaseURL() + "lhlogin.html"+urlQueryString;
					Window.open(url, "_self", null);
					}
					
				}
			});
		}
	
		else if(lockedNews != null){
			int lockednid = Integer.parseInt(lockedNews);
			int userid= Integer.parseInt(uid);
			requestLockedNewsAccess(lockednid,userid);
		}else{
			LhNewsProviderServiceAsync newsProviderService = GWT.create(LhNewsProviderService.class);
			newsProviderService.getadmininformation(new AsyncCallback() {
	
				@Override
				public void onFailure(Throwable caught) {
					System.out.println("User information could not be fetched:: onModuleLoad() NewsItemHome");
					caught.printStackTrace();
				}
	
				@Override
				public void onSuccess(Object result) {
					if(result == null){
						String urlQueryString = Window.Location.getQueryString();
						String url = GWT.getHostPageBaseURL() + "lhlogin.html"+urlQueryString;
						Window.open(url, "_self", null);
					}else{
						ArrayList list = (ArrayList)result;
						LhUser user = (LhUser)list.get(0);
						if(user == null){
							String urlQueryString = Window.Location.getQueryString();
							String url = GWT.getHostPageBaseURL() + "lhlogin.html"+urlQueryString;
							Window.open(url, "_self", null);
						}else{
							initialize(user);
						}
					}
					
				}
				
			});
		}
	}  
  	
	private void requestLockedNewsAccess(final Integer lockednid,final Integer userid) {
		String message =" The item clicked by you is marked as locked by admin. Please click on button below to request permission.";
		String btnText = "Request access";
		
		 ntp= new NotificationPopup(message, btnText, NotificationType.CUSTOMNOTIFICATION, new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				String url = GWT.getHostPageBaseURL()+"requestLockedItem?newsId="+lockednid+"&uid="+userid;
				RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
				try {
					Request request = requestBuilder.sendRequest(null,new RequestCallback() {

						@Override
						public void onResponseReceived(Request request,	Response response) {
							ntp.hide();
							NotificationPopup popup=new NotificationPopup("Your request is sent.. ", NotificationType.WINDOWCLOSENOTIFICATION);
							popup.createUI();
							popup.setPopupPosition(400, 200);
							popup.show();

						}

						@Override
						public void onError(Request request,
								Throwable exception) {
							// TODO Auto-generated method stub
							
						}
      
					 						
					
			  });
				} catch (RequestException e) {
					
					e.printStackTrace();
				}
			}
		});
		ntp.createUI();
		ntp.center();
		ntp.setStylePrimaryName("searchPopup");
		ntp.show();
		
	}

	private void initialize(LhUser user){
		int nId = Integer.parseInt(Window.Location.getParameter("nid"));

		RootPanel.get().clear();

		NewsPageManager newsPageManager = new NewsPageManager(user);
		newsPageManager.loadNewsItemContent(nId);
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		// TODO Auto-generated method stub
		
	}

	
}
