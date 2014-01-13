package com.login.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.login.server.db.AllocateResources;
import com.login.server.db.UserHelper;

public class NewsCenterSelector extends HttpServlet {

public  String ncid = "NCID";
public String ncname = "ncName";

protected void doGet(HttpServletRequest req, HttpServletResponse resp)
throws ServletException, IOException 
{

				String str = req.getParameter(ncid);
				String strName = req.getParameter(ncname);
				System.out.println("in newscenter selector" +str+strName);
				// int ncid = Integer.parseInt(str);
				String driverClassName;
				String connectionUrl;
				String username;
				String password;
				
				ServletContext context = getServletContext();
				connectionUrl = (String) context.getAttribute(AllocateResources.DBCONNECTIONURL);
				driverClassName = (String) context.getAttribute(AllocateResources.DRIVERCLASSNAME);
				username = (String) context.getAttribute(AllocateResources.USERNAME);
				password = (String) context.getAttribute(AllocateResources.PASSWORD);
				
				//UserHelper helper = new UserHelper();
				//String nclink = helper.getTheLink(str);//.getNewscenterLink(str);
				//System.out.println("the selected link is>>>>>>>>>>>>>.."+nclink);
				HttpSession session = req.getSession(false);
				session.setAttribute(ncid, str);
				session.setAttribute(ncname,strName);
				//resp.getWriter().print("NewsCenter Selector Servlet invoked");
				//resp.getWriter().close();
				 String nclink = "http://localhost:8888/com.login.login/login.html";
				resp.sendRedirect(nclink);
	}
}
