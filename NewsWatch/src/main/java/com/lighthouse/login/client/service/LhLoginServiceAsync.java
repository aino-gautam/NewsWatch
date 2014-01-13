package com.lighthouse.login.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.login.client.UserInformationServiceAsync;

public interface LhLoginServiceAsync extends UserInformationServiceAsync {

	void removeUserFromSession(AsyncCallback<Void> callback);

	void getClientLogoImage(int newsCenterId,AsyncCallback<String> asyncCallback);


}
