package com.login.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.login.client.UserInformation;

public interface UserInformationService extends RemoteService{
	public String saveUserRecord(UserInformation userinfo);

	public ArrayList validateUser(UserInformation userinfo);

	public String forgotPasswordRetrieve(UserInformation userinfo);

	public boolean sendMailForForgotPassword(UserInformation userinfo);

	public String updateRecord(UserInformation userinfo, String colName, String email);

	public void setValuesToSession(int ncId,String ncName);

	public ArrayList checkUserLogin();

	public int emailapprovalCheck(String email);
}