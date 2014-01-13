package com.newscenter.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gwt.user.client.Window;
import com.lighthouse.main.server.AccessAlertLockedNewsItemServlet;
import com.login.server.db.AllocateResources;


public class EmailItemViewServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Logger logger = Logger.getLogger(EmailItemViewServlet.class.getName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		requestProcess(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		requestProcess(req, resp);
	}

	public void requestProcess(HttpServletRequest req, HttpServletResponse resp){
		
		String info = req.getParameter("info");
		String[] infosplti = info.split("\\*\\*");
		int userid = Integer.parseInt(infosplti[0]);
		int newscenterid = Integer.parseInt(infosplti[1]);
		int newsitemid = Integer.parseInt(infosplti[2]);
		int isLocked = Integer.parseInt(infosplti[3]); 
		int isReport = Integer.parseInt(infosplti[4]); 
		String url = infosplti[5];
		
		try{
			ServletContext context=getServletContext();
			context.log("In EmailItemViewServlet");
			String connectionUrl = (String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
			String driverClassName = (String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
			String username = (String)context.getAttribute(AllocateResources.USERNAME);
			String password = (String)context.getAttribute(AllocateResources.PASSWORD);
			System.out.println("Returning connection for Newsletter Servlet");
			Driver drv =(Driver)Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(drv);
			Connection conn=DriverManager.getConnection(connectionUrl,username,password);
			
			if(isReport==1){
				StringBuffer reqURL=req.getRequestURL();
				logger.log(Level.INFO, "[In EmailItemViewServlet...reqURL : ] "+reqURL );
				String reqHost=(String)reqURL.subSequence(0,reqURL.length()-(req.getServletPath().length()));
			    String reqUrl=reqHost+"/com.lighthouse.main.lhmain/DownloadReport?reportId="+newsitemid;
				Statement stmt = conn.createStatement();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String query = "insert into useritemaccessstats(userId,newscenterId,newsitemId,timeOfAccess,NewscatalystItemCount,NewsletterItemcount) " +
			        "values("+userid+","+newscenterid+","+newsitemid+",'"+df.format(new Date())+"',0,1)";
			 	stmt.executeUpdate(query);
			 	getServletContext().log(query);
			  
			    stmt.close();
			    conn.close();
				logger.log(Level.INFO, "[In EmailItemViewServlet...Redirect : ] "+reqUrl );
				resp.sendRedirect(reqUrl);
				return;
			}
			
			if(isLocked==0){ 
				
	    		Statement stmt = conn.createStatement();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String query = "insert into useritemaccessstats(userId,newscenterId,newsitemId,timeOfAccess,NewscatalystItemCount,NewsletterItemcount) " +
			        "values("+userid+","+newscenterid+","+newsitemid+",'"+df.format(new Date())+"',0,1)";
			 	stmt.executeUpdate(query);
			 	getServletContext().log(query);
			  
			    stmt.close();
			    conn.close();
				getServletContext().log("EmailItemViewServlet connection closed");
				resp.sendRedirect(url);	
			}
			else{
				StringBuffer reqURL=req.getRequestURL();
				logger.log(Level.INFO, "[In EmailItemViewServlet...reqURL : ] "+reqURL );
				String reqHost=(String)reqURL.subSequence(0,reqURL.length()-(req.getServletPath().length()));
			//	String reqUrl=reqHost+"/newshome.html?gwt.codesvr=127.0.0.1:9997&lockednid="+newsitemid+"&uid="+userid;
				String reqUrl=reqHost+"/newshome.html?lockednid="+newsitemid+"&uid="+userid;
				logger.log(Level.INFO, "[In EmailItemViewServlet...Redirect : ] "+reqUrl );
				resp.sendRedirect(reqUrl);
				
			}
		}catch(Exception e){
			e.printStackTrace();
			getServletContext().log("EmailItemViewServlet excpetion " +e.getMessage());
		 }
	}
	
}
