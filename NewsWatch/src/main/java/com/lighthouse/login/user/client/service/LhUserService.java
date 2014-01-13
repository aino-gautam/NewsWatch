package com.lighthouse.login.user.client.service;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.login.client.UserInformation;

@RemoteServiceRelativePath("lhuserservice")
public interface LhUserService extends RemoteService{

	public boolean saveUserPermissions(LhUserPermission permission);
	
	public LhUserPermission getUserPermissions(int newscenterid, String email);

	public ArrayList getEntityList(int newscenterid);
	
	public String forgotPasswordRetrieve(UserInformation userinfo);
	
	public boolean sendMailForForgotPassword(UserInformation userinfo);
	
	}
