package com.login.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.login.client.UserInformation;

public interface UserInformationServiceAsync {
	void saveUserRecord(UserInformation userinfo ,AsyncCallback  callback);

	void validateUser(UserInformation userinfo,AsyncCallback  callback);

	void forgotPasswordRetrieve(UserInformation userinfo,AsyncCallback  callback);

	void sendMailForForgotPassword(UserInformation userinfo,AsyncCallback  callback);

	void updateRecord(UserInformation userinfo,String colName,String email,AsyncCallback callback);
	
	void setValuesToSession(int ncid,String ncName,AsyncCallback callback);
	
	void checkUserLogin(AsyncCallback callback);

	void emailapprovalCheck(String email, AsyncCallback callback);
}