package com.lighthouse.search.server;

import java.io.IOException;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lighthouse.search.server.helper.SetupIndexHelper;

/**
 * @author mahesh@ensarm.com This servlet will be called during war deployment
 *         and will do the indexing for lucene search
 */
public class SearchIndexSetupServlet extends HttpServlet {
	Connection conn;

	public static String INDEXDIRPATH = null;
	public static String USERNAME = null;
	public static String PASSWORD = null;

	Logger logger = Logger.getLogger(SearchIndexSetupServlet.class.getName());
	
	/**
	 * This method will get the the path where the directory is to be created
	 * and gets the username and password for checking the user who should
	 * perform the indexing
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		logger.log(Level.INFO, "[SearchIndexSetupServlet --- init() initiated]");
		super.init(config);
		try {
			INDEXDIRPATH = config.getInitParameter("IndexDirPath");
			USERNAME = config.getInitParameter("IndexSetupUsername");
			PASSWORD = config.getInitParameter("IndexSetupPassword");
			SetupIndexHelper helper = new SetupIndexHelper();
			helper.indexSetup(INDEXDIRPATH);
			helper.closeConnection();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[SearchIndexSetupServlet --- init() completed]");
	}

	private void getDisplay(HttpServletResponse resp) throws IOException {
		logger.log(Level.INFO, "[SearchIndexSetupServlet --- getDisplay() initiated]");
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>JVM Memory Statistics</title>");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1> JVM Memory Statistics </h1>");
		out.println("Maximum Heap Memory (in MB): ");
		out.println("<br>");
		out.println("Total Heap Memory (in MB): ");
		out.println("<br>");
		out.println("</body>");
		out.println("</html>");
		out.close();
		logger.log(Level.INFO, "[SearchIndexSetupServlet --- getDisplay() completed]");
	}

	/**
	 * This method will the check the user credientials for performing indexing
	 * 
	 * @param - String username String password
	 * @return - boolean result- true if correct or else false
	 * */
	private boolean checkUserCredientials(String emailId, String userPassword) {
		try {
			String sql = "select email,password from user where isAdmin=1;";

			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				if (emailId.equalsIgnoreCase(rs.getString("email"))
						&& userPassword.equalsIgnoreCase(rs
								.getString("password"))) {
					conn.close();
					return true;
				}
			}
		} catch (Exception e) {
			try {
				conn.close();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.log(Level.INFO, "[SearchIndexSetupServlet --- doGet() calling SetupIndexHelper]");
		SetupIndexHelper helper = new SetupIndexHelper();
		helper.indexSetup(INDEXDIRPATH);
		logger.log(Level.INFO, "[SearchIndexSetupServlet --- doGet() called Complete index done....]");
		helper.closeConnection();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.log(Level.INFO, "[SearchIndexSetupServlet --- doPost() initiated]");
		super.doPost(req, resp);
		getDisplay(resp);
		String uname = req.getParameter("username");
		String password = req.getParameter("password");
		uname = "mahesh";
		password = "erom";
		if (checkUserCredientials(uname, password)) {
			logger.log(Level.INFO, "[SearchIndexSetupServlet --- doPost() calling SetupIndexHelper]");
			SetupIndexHelper helper = new SetupIndexHelper();
			helper.indexSetup(INDEXDIRPATH);
			helper.closeConnection();
		} else {
			
		}

	}

}
