package com.lighthouse.login.client.service;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.login.client.UserInformationService;

@RemoteServiceRelativePath("lhlogin")
public interface LhLoginService extends UserInformationService{
	
	public void removeUserFromSession();

	String getClientLogoImage(int newsCenterId);

}
