package com.newscenter.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.login.client.UserInformation;
import com.login.server.db.AllocateResources;

public class ItemViewServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private Connection conn = null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
	
				ServletContext context=getServletContext();
				String connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
				String driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
				String username =(String)context.getAttribute(AllocateResources.USERNAME);
				String password =(String)context.getAttribute(AllocateResources.PASSWORD);
				getServletContext().log("Returning connection for ItemViewServlet");
				Driver drv =(Driver)Class.forName(driverClassName).newInstance();
				DriverManager.registerDriver(drv);
				Connection conn=DriverManager.getConnection(connectionUrl,username,password);
					
					
			UserInformation userinformation = (UserInformation)req.getSession().getAttribute("userInfo");
			String newsitemIdStr = req.getParameter("newsItemId");
			int newitemid = Integer.parseInt(newsitemIdStr);
		
			 Statement stmt = conn.createStatement();
			 int userid = userinformation.getUserId();
			 int newscenterid = userinformation.getUserSelectedIndustryID();
			 DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
			 	 String query = "insert into useritemaccessstats(userId,newscenterId,newsitemId,timeOfAccess,NewscatalystItemCount,NewsletterItemcount) " +
			 		        "values("+userid+","+newscenterid+","+newitemid+",'"+df.format(new Date())+"',1,0)";
			 
			 	stmt.executeUpdate(query);
			 	 getServletContext().log(query);
		
			 stmt.close();
			
			 conn.close();
			 getServletContext().log("Connection closed for ItemViewServlet");
			}
			 catch(Exception e){
				e.printStackTrace();
				getServletContext().log(""+e.getLocalizedMessage());
			 }
			 
		/*	 PrintWriter pw =resp.getWriter();
			 pw.println("<html>");
			 pw.println("<body onload=\"self.close();\">");
			 pw.println("</body>");
			 pw.println("</html>");
			 pw.close();*/
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
		//getConnection();
			ServletContext context=getServletContext();
			String connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
			String driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
			String username =(String)context.getAttribute(AllocateResources.USERNAME);
			String password =(String)context.getAttribute(AllocateResources.PASSWORD);
			getServletContext().log("Returning connection for ItemViewServlet");
			Driver drv =(Driver)Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(drv);
			Connection conn=DriverManager.getConnection(connectionUrl,username,password);
				
				
		UserInformation userinformation = (UserInformation)req.getSession().getAttribute("userInfo");
		int newitemid = 0;
		int groupId = 0;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		 ServletFileUpload upload = new ServletFileUpload(factory);
		 ArrayList items = null;
		 
				items = (ArrayList) upload.parseRequest(req);
				Iterator iter = items.iterator();
				while(iter.hasNext()){
					DiskFileItem item = (DiskFileItem)iter.next();
					if(item.isFormField()){
						String fieldName = item.getFieldName();
						newitemid = Integer.parseInt(item.getString());
						String grpfieldName = item.getFieldName();
						groupId = Integer.parseInt(item.getString());
					}
				}
		 
		 Statement stmt = conn.createStatement();
		 int userid = userinformation.getUserId();
		 int newscenterid = userinformation.getUserSelectedIndustryID();
		 DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 /*String query = "select * from useritemaccessstats where newscenterId = "+newscenterid+" and userId = "+userid+" and newsitemId = "+newitemid+"";
		 ResultSet rs = stmt.executeQuery(query);
		 if(rs.next()){
			 int count = rs.getInt("NewscatalystItemCount");
			 query = "update useritemaccessstats set NewscatalystItemcount = "+(count+1)+" ,timeOfAccess = '"+df.format(new Date())+"' where newscenterId = "+newscenterid+" and userId = "+userid+" and newsitemId = "+newitemid+"";
			 getServletContext().log(query);
			 stmt.executeUpdate(query);
		 }
		 else{*/
		 	 String query = "insert into useritemaccessstats(userId,newscenterId,newsitemId,timeOfAccess,NewscatalystItemCount,NewsletterItemcount) " +
		 		        "values("+userid+","+newscenterid+","+newitemid+",'"+df.format(new Date())+"',1,0)";
		 	/*ResultSet rs = null;*/
		 	stmt.executeUpdate(query);
		 	 getServletContext().log(query);
		     //stmt.executeUpdate(query);
		 //}
		 stmt.close();
		 //rs.close();
		 conn.close();
		 getServletContext().log("Connection closed for ItemViewServlet");
		}
		 catch(Exception e){
			e.printStackTrace();
			getServletContext().log(""+e.getLocalizedMessage());
		 }
		
	}
	
	/*private Connection getConnection(){
		try {
			ServletContext context=getServletContext();
			String connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
			String driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
			String username =(String)context.getAttribute(AllocateResources.USERNAME);
			String password =(String)context.getAttribute(AllocateResources.PASSWORD);
			if(conn==null){
				getServletContext().log("Returning connection for Newsletter Servlet");
				Driver drv =(Driver)Class.forName(driverClassName).newInstance();
				DriverManager.registerDriver(drv);
				conn=DriverManager.getConnection(connectionUrl,username,password);
				return conn;
			}
			else{
				getServletContext().log("Returning existing connection for Newsletter Servlet");
				return conn;
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
			getServletContext().log(ex.getLocalizedMessage());
		}
		return null ;
	}
	
	private void closeConnection(){
		try{
			if(conn!=null){
				getServletContext().log("Closing connection for Newsletter Servlet");
				conn.close();
			}
			}
		catch(Exception ex){
			ex.printStackTrace();
			getServletContext().log(ex.getLocalizedMessage());
		}
	}*/

}
