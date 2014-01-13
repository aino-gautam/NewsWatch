package com.appUtils.client.service;

import com.appUtils.client.exception.AuthorizationException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("appUtilService")
public interface AppUtilsService extends RemoteService {

	public void removeFromSession();
	boolean isValidAppAccess() throws AuthorizationException;
}
