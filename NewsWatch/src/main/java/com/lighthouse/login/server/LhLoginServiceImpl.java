package com.lighthouse.login.server;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lighthouse.login.client.service.LhLoginService;
import com.lighthouse.login.server.helper.lhLoginHelper;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.newsletter.server.LhNewsletterHelper;
import com.login.client.UserInformation;
import com.login.server.UserInformationServiceImpl;

public class LhLoginServiceImpl extends UserInformationServiceImpl implements LhLoginService{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(LhLoginServiceImpl.class.getName());
	ServletContext context;
	@Override
	public ArrayList validateUser(UserInformation userinfo){
		logger.log(Level.INFO, "LhLoginServiceImpl --- validateUser() initiated");
		ArrayList list = new ArrayList();
		try {
			HttpServletRequest req = this.getThreadLocalRequest();
			lhLoginHelper helper = new lhLoginHelper();
			ArrayList arraylist = helper.validateUser((LhUser)userinfo);
			HttpSession session = req.getSession(false);
			if(session == null)
				session = req.getSession(true);
			session.setAttribute("userInfo",userinfo);
			helper.closeConnection();
			logger.log(Level.INFO, "LhLoginServiceImpl --- validateUser() completed");
			return arraylist;
		}
		catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Error in validateUser IN UserInformatonServiceImpl");
			logger.log(Level.INFO, "LhLoginServiceImpl --- validateUser() exception");
			list.add("false");
			return list;
		}
	}

	@Override
	public ArrayList checkUserLogin() 
	{
		try
		{
			logger.log(Level.INFO, "LhLoginServiceImpl --- checkUserLogin() initiated");
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			ArrayList arraylist = new ArrayList();
			
			if(session!=null){
				logger.log(Level.INFO, "LhLoginServiceImpl --- checkUserLogin() session not null");
				LhUser lhUser  = (LhUser)session.getAttribute("userInfo");
				if(lhUser!=null){
					logger.log(Level.INFO, "LhLoginServiceImpl --- checkUserLogin() user not null");
					arraylist.add(lhUser);
					logger.log(Level.INFO, "LhLoginServiceImpl --- checkUserLogin() completed");
					return arraylist;
				}
				else{
					logger.log(Level.INFO, "LhLoginServiceImpl --- checkUserLogin() user null -- returning null");
					return null;
				}
			}
			else{
				logger.log(Level.INFO, "LhLoginServiceImpl --- checkUserLogin() session null -- returning null");
				return null;
			}
		}
		catch(Exception ex){
			logger.log(Level.INFO, "LhLoginServiceImpl --- checkUserLogin() Exception!! " +ex.getMessage());
			ex.printStackTrace();
			return null;
		}
		
	}

	
	private void doRedirect()  {

		try{
		HttpServletResponse resp = this.getThreadLocalResponse();
		
		HttpServletRequest req = this.getThreadLocalRequest();
		
		RequestDispatcher dispatcher = req.getRequestDispatcher("http://www.facebook.com");
	    dispatcher.forward(req, resp);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	
	}

	@Override
	public void removeUserFromSession() {
		try{
			HttpServletResponse res = this.getThreadLocalResponse();
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			if(session!=null)
			{
				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
				session.removeAttribute("userInfo");
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

	}

	@Override
	public String getClientLogoImage(int newsCenterId) {
		LhNewsletterHelper helper = new LhNewsletterHelper();
		context = getServletContext();
		String imgurl = helper.getImageURL(newsCenterId, "siloLogo",context.getAttribute("tomcatpath").toString(), true);
		context.setAttribute("siloLogo", imgurl);
		helper.closeConnection();
		return imgurl;
	}
	
	

}