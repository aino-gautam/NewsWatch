package com.lighthouse.feed.client.ui.base;

import java.util.ArrayList;

import com.appUtils.client.exception.AuthorizationException;
import com.appUtils.client.service.AppUtilsService;
import com.appUtils.client.service.AppUtilsServiceAsync;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.lighthouse.feed.client.FeedEventBus;
import com.lighthouse.feed.client.ui.base.IBaseMainView.IBaseMainPresenter;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.client.service.LhNewsProviderService;
import com.lighthouse.main.client.service.LhNewsProviderServiceAsync;
import com.mvp4g.client.annotation.Presenter;
import com.mvp4g.client.presenter.BasePresenter;
/**
 * 
 * @author kiran@ensarm.com
 *
 */
@Presenter(view = BaseMainView.class)
public class BaseMainPresenter extends BasePresenter<BaseMainView, FeedEventBus> implements IBaseMainPresenter{

	public void onSetBody( IsWidget body ) {
		view.setBody( body );
	}
	
	public void onStart() {
		LhNewsProviderServiceAsync newsProviderService = GWT.create(LhNewsProviderService.class);
		newsProviderService.getadmininformation(new AsyncCallback() {
			
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("User information could not be fetched:: initialize() LhMain");
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
				final LhUser user = (LhUser)list.get(0);
				//clientLogo = (String) list.get(1);
				if(user == null){
					String urlQueryString = Window.Location.getQueryString();
					String url = GWT.getHostPageBaseURL() + "lhlogin.html"+urlQueryString;
					Window.open(url, "_self", null);
				}else{
					
					AppUtilsServiceAsync service = (AppUtilsServiceAsync)GWT.create(AppUtilsService.class);
					 service.isValidAppAccess(new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
								if (caught instanceof AuthorizationException) {
										PopupPanel popupPanel=new PopupPanel(false, false);
										popupPanel.setSize("100%", "100%");
										popupPanel.center();
										
										AuthorizationException aex=(AuthorizationException)caught;
											if(aex.getAuthExceptionType()==AuthorizationException.SERVICENOTACTIVATED){
													popupPanel.setWidget(new Label("You are not authorised to access this page."));
													popupPanel.show();
												
											}
											if(aex.getAuthExceptionType()==AuthorizationException.SERVICEEXPIRED){
												popupPanel.setWidget(new Label("This service is currently not activated."));
												popupPanel.show();
											
										}
									}
							
						}

						@Override
						public void onSuccess(Boolean result) {
								if(result){
									view.setLhUser(user);
									view.createUI();
								}else{
									PopupPanel popupPanel=new PopupPanel(false, false);
									popupPanel.setWidget(new Label("You are not authorised to access this page."));
									popupPanel.setSize("100%", "100%");
									popupPanel.center();
									popupPanel.show();
								}
							}
					});
					
					
				}
			}
		}
	});
	}
}
