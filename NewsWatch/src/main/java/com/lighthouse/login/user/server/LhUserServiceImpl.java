package com.lighthouse.login.user.server;

import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.login.user.client.service.LhUserService;
import com.login.client.UserInformation;
import com.login.server.db.UserHelper;


public class LhUserServiceImpl extends RemoteServiceServlet implements LhUserService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public boolean saveUserPermissions(LhUserPermission permission) {
		LhUserHelper helper=new LhUserHelper();
		boolean val=helper.saveUserPermissions(permission);
		HttpServletRequest req = this.getThreadLocalRequest();
		if(val){
			HttpSession session = req.getSession(false);
			LhUser lhUser  = (LhUser)session.getAttribute("userInfo");
			lhUser.setUserPermission(permission);
		}
		helper.closeConnection();
		return val;
	}

	@Override
	public LhUserPermission getUserPermissions(int newscenterid, String email) {
		LhUserHelper helper=new LhUserHelper();
		LhUserPermission permision=helper.getUserPermissions(newscenterid, email);
		helper.closeConnection();
		return permision;
	}

	@Override
	public ArrayList getEntityList(int newscenterid) {
		
		HttpServletRequest req = this.getThreadLocalRequest();
		HttpSession session = req.getSession(false);
		LhUser userInformation  = (LhUser)session.getAttribute("userInfo");
	    int isAdmin = userInformation.getAdmin();
		LhUserHelper helper=new LhUserHelper();
		ArrayList list=helper.getEntityList(newscenterid,isAdmin);
		helper.closeConnection();
		return list;
	}
	
	@Override
	public boolean sendMailForForgotPassword(UserInformation userinfo) {
		HttpServletRequest  req = this.getThreadLocalRequest();
		ServletContext context = getServletContext();
		LhUserHelper helper = new LhUserHelper();
		helper.setSmtpusername(context.getInitParameter("smtpusername"));
		helper.setSmtppassword(context.getInitParameter("smtppassword"));
		helper.setSmtphost(context.getInitParameter("smtphost"));
		helper.setSmtpport(context.getInitParameter("smtpport"));
		boolean val = helper.sendMailForForgotpassword(userinfo);
		return val;
	}

	@Override
	public String forgotPasswordRetrieve(UserInformation userinfo) {
		try {
			LhUserHelper helper = new LhUserHelper();
			String str = helper.forgotPasswordRetrieve(userinfo);
			helper.closeConnection();
			return str;
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
			return null;
		}
	}
	
}
