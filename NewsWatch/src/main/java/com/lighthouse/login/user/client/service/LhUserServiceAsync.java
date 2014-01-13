package com.lighthouse.login.user.client.service;



import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.login.client.UserInformation;

public interface LhUserServiceAsync {

	void saveUserPermissions(LhUserPermission permission,
			AsyncCallback<Boolean> callback);

	void getUserPermissions(int newscenterid, String email,
			AsyncCallback<LhUserPermission> callback);

	
	void getEntityList(int newscenterid,AsyncCallback<ArrayList> callback);
	
	void forgotPasswordRetrieve(UserInformation userinfo,AsyncCallback  callback);
	
	void sendMailForForgotPassword(UserInformation userinfo,AsyncCallback  callback);
}
