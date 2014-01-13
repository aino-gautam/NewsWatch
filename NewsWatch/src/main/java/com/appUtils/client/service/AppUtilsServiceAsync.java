package com.appUtils.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AppUtilsServiceAsync {

	void removeFromSession(AsyncCallback<Void> callback);

	void isValidAppAccess(AsyncCallback<Boolean> callback);

}
