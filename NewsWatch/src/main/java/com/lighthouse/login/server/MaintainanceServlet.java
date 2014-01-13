package com.lighthouse.login.server;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lighthouse.login.server.helper.lhLoginHelper;
import com.lighthouse.newsletter.server.AlertRssServlet;
import com.login.server.db.AllocateResources;

/**
 * This class will load the list of newsCenters from the database and create the index page dynamicallly.
 * @author prachi@ensarm.com
 * 
 *
 */
public class MaintainanceServlet extends HttpServlet{
    
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ServletContext context;
	private StringBuilder stringBuilder;
	HashMap<Integer, String> newsCatalyst;
	Logger logger = Logger.getLogger(MaintainanceServlet.class.getName());
	
   	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try{
			logger.log(Level.INFO, "[-- In MaintainanceServlet -- initiated ]");
			
			context=getServletContext();
			String currentFile = req.getContextPath();
			URL currentURL = new URL(req.getScheme(),req.getServerName(), req.getServerPort(), currentFile);
			logger.log(Level.INFO, "Complete Url" +currentURL);
			
		    String connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
			String driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
			String username =(String)context.getAttribute(AllocateResources.USERNAME);
			String password =(String)context.getAttribute(AllocateResources.PASSWORD);
			
			Driver drv =(Driver)Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(drv);
			Connection conn=DriverManager.getConnection(connectionUrl,username,password);
			
			logger.log(Level.INFO, "[-- In MaintainanceServlet -- fetching all existing silos]");
			String query = "select IndustryEnumId,Name from industryenum ORDER BY CreationDate";
			Statement stmt = conn.createStatement();
			ResultSet rs1 = stmt.executeQuery(query);
			newsCatalyst = new HashMap<Integer, String>();
			while(rs1.next()){
				int ncid = rs1.getInt("IndustryEnumId");
				String newsCenterName = rs1.getString("Name");
				newsCatalyst.put(ncid, newsCenterName);
				
			}
			rs1.close();
			stmt.close();
			conn.close();
			String str = createSiloPage(newsCatalyst,currentURL);
			resp.setContentType("text/html");
			resp.getOutputStream().print(str);
			logger.log(Level.INFO, "[-- In MaintainanceServlet -- completed]");
		}
		catch(Exception e){
			e.printStackTrace();
			logger.log(Level.INFO, "[-- In MaintainanceServlet -- EXCEPTION!!!! ::: ]"+e.getMessage());
		}
		
	}

   	private void createLayout() {
		stringBuilder.append("<html>" +
					"<body BGCOLOR='#E5ECF3'>" +
						"<center>" +
							"<h1>Welcome to Lighthouse</h1>" +
						"</center>" +
						"<table align=\"center\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\">" + // main table
							"<tr>"+ // first row
								"<td style=\"align:left; vertical-align:top\">" + // start column1 for silo listing
									"<div class=\"SiloList\">" +
										"<table width=\"100%\">" +
											"<tr>" +
												"<td>" +
													"<h3 style=\"text-align:center\">Following are the existing Silos</h3>" +
												"</td>" +
											"</tr>"); 
	}
		
   	/**
	  * This method will create the index page dynamically by taking the silos from the database
	  * @param newsCatalyst
	  * @param currentURL 
	  */
   	public String createSiloPage(HashMap<Integer, String> newsCatalyst, URL currentURL) {
		stringBuilder = new StringBuilder();
		createLayout();
			
		for (Iterator it = newsCatalyst.entrySet().iterator(); it.hasNext();){
			Map.Entry entry = (Map.Entry) it.next();
			Integer newsCenterId = (Integer) entry.getKey();
			String newsCenterName = (String) entry.getValue();
			stringBuilder.append("<tr>" +
									"<td style=\"text-align:center\">" +
										"<a href = 'lhlogin.html?NCID="+newsCenterId+"&ncName="+newsCenterName+"' >"+newsCenterName+"</a>" +
									"</td>" +
								 "</tr>");
		}
		endLayout(currentURL);
		return stringBuilder.toString();
	}

	private void endLayout(URL currentURL) {
		logger.log(Level.INFO , "Complete URL is :" +currentURL);
		
		stringBuilder.append("</table>" +
							"</div> " +
							"</td>"); // end of column 1
			
		stringBuilder.append("<td style=\"align:right; vertical-align:top\">" + // start of column2 for silo creation form
								"<div class=\"CreateSilo\">" +
									"<table width=\"100%\">" +
										"<tr>" +
											"<td>" +
												"<h3>Create New Silo</h3>" +
											"</td>" +
										"</tr>" +
										"<tr>" +
											"<td>" +
												"<form method=\"get\" action=\""+currentURL+"/newSiloCreation\">Silo Name:  <input type=\"text\" size=\"25\" name=\"siloname\" />" +
														"<br />" +
														"<br /> Admin List:  <input type=\"text\" size=\"25\" name=\"adminlist\" />" +
														"<br />" +
														"<br /><input type=\"submit\" value=\"Create new silo\" />" +
												"</form>" +
											"</td>" +
										"</tr>" +
									"</table>" +
								"</div>" +
							"</td>" + // end of column2 
						"</tr>" + // end of row 1
					"</table>" +
				"</body>" +
			"</html>");
	}
}

