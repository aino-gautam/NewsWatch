package com.lighthouse.login.server;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.newscenter.server.db.DBHelper;

/**
 * 
 * @author prachi@ensarm.com
 * 
 */
public class CreateNewSilo extends HttpServlet {

	Logger logger = Logger.getLogger(CreateNewSilo.class.getName());
	private ServletContext context;
	private StringBuilder stringBuilder = new StringBuilder();

	Connection connection = null;
	public static final String DBCONNECTIONURL = "ConnectionUrl";
	public static final String DRIVERCLASSNAME = "driverClass";
	public static final String USERNAME = "UserName";
	public static final String PASSWORD = "Password";
	static final long serialVersionUID = 1L;

	public static final String TOMCATPATH = "tomcatpath";
	String tomcaturl;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			logger.log(Level.INFO,
					"########## IN CREATE NEW SILO SERVLET  init() ##########");
			
			String connectionUrl = config.getInitParameter(DBCONNECTIONURL);
			String driverClassName = config.getInitParameter(DRIVERCLASSNAME);
			String username = config.getInitParameter(USERNAME);
			String password = config.getInitParameter(PASSWORD);

			ServletContext context = getServletContext();
			context.setAttribute(DBCONNECTIONURL, connectionUrl);
			context.setAttribute(DRIVERCLASSNAME, driverClassName);
			context.setAttribute(USERNAME, username);
			context.setAttribute(PASSWORD, password);

			String realPath = getServletContext().getRealPath("");
			context.setAttribute(TOMCATPATH, realPath);
			
			
			/**
			 * This piece of code is use to intialize the static variables of
			 * the DBHelper
			 */
			DBHelper.driverClassName = driverClassName;

			Driver drv = (Driver) Class.forName(DBHelper.driverClassName)
					.newInstance();
			DriverManager.registerDriver(drv);

			DBHelper.connectionUrl = connectionUrl;
			DBHelper.username = username;
			DBHelper.password = password;
			DBHelper.tomcatpath = realPath;

		} catch (Exception e) {
			System.out
					.println("exception could not retrieve connection strings");
			logger.log(
					Level.INFO,
					"CREATE NEW SILO SERVLET  init() - exception could not retrieve connection strings");
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			logger.log(Level.INFO,
					"[-- In CreateNewSilo Servlet -- initiated ]");
			String str = null;
			context = getServletContext();
			
			tomcaturl = context.getInitParameter("tomcaturl");
					
			
			String currentFile = req.getContextPath();
			URL currentURL = new URL(req.getScheme(), req.getServerName(), req.getServerPort(), currentFile);
			logger.log(Level.INFO, "Complete Url" + currentURL);

			String siloName = req.getParameter("siloname");
			logger.log(Level.INFO, "[-- In CreateNewSilo Servlet -- siloName:: " + siloName+ " ]");
			if(checkSiloExist(siloName)){
				str = printResult((Boolean) null, currentURL);
			}
			else{
				String adminNames = req.getParameter("adminlist");
				logger.log(Level.INFO, "[-- In CreateNewSilo Servlet -- admins :: "
						+ adminNames + " ]");
				String listOfAdmins = getAdminListFromForm(adminNames); // this will return the userid's of the admins
				ArrayList<Integer> userAdminList = getAdminsUserId(listOfAdmins);
				boolean result = addSilo(siloName, userAdminList);
	
				str = printResult(result, currentURL);
			}
			resp.setContentType("text/html");
			resp.getOutputStream().print(str);
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(
					Level.SEVERE,
					"CreateNewSilo ::: Error in doGet  :: doGet()"
							+ e.getMessage());
		}
	}

	public boolean checkSiloExist(String newSiloName){
		boolean result = false;
		try{

			DBHelper dbHelper = new DBHelper();
			Connection conn = dbHelper.getConnection();
			String industryNameQuery ="select name from industryenum";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(industryNameQuery);
			while (rs.next()) {
				if(rs.getString("name").equalsIgnoreCase(newSiloName))
					return true;
			}	
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.SEVERE,"CreateNewSilo ::: Error in checkSiloExist  :: checkSiloExist()"+ e.getMessage());
		}
		return result;
	}
	/**
	 * The entries in the tables to create a silo are done here.
	 * 
	 * @param siloName
	 * @param userAdminList
	 * @throws SQLException
	 */
	public boolean addSilo(String siloName, ArrayList<Integer> userAdminList)
			throws SQLException {

		DBHelper dbHelper = new DBHelper();
		Connection conn = dbHelper.getConnection();
		Statement queryTagStmt = null;
		Statement queryUserStmt = null;
		Statement queryNewsLetterStmt = null;
		Statement queryIndustryEnumStmt = null;
		Statement queryNewsStmt = null;
		boolean flag = false;
		try {
			
			conn.setAutoCommit(false);
			
			// insert into industryEnum
			String queryIndustry = "insert into industryenum(Name,Description,CreationDate,emailTemplate,newsLetterHeaderTemplate,newsLetterFooterTemplate) values('"
					+ siloName
					+ "','"+tomcaturl+"',null,null,null,null)";
			queryIndustryEnumStmt = conn.createStatement();
			queryIndustryEnumStmt.execute(queryIndustry,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = queryIndustryEnumStmt.getGeneratedKeys();
			int industryId = 0;
			if (rs != null) {
				while (rs.next()) {
					industryId = rs.getInt(1);
				}
			}
			rs.close();
			
			String siloLink = tomcaturl+"lhlogin.html?NCID="+industryId+"&ncName="+siloName;
			String updateIndustryDescription = "update industryenum set Description = '"+siloLink+"' where IndustryEnumId="+industryId;
			queryIndustryEnumStmt.execute(updateIndustryDescription);
			queryIndustryEnumStmt.close();
			
			// insert into newsCenter
			String queryNewsCenter = "insert into newscenter(Name,Description,IndustryEnumId,allowComment,numMandGrps,numCustGrps) values('"
					+ siloName
					+ "','This is related newsCenter','"
					+ industryId + "',1,3,3) ";
			queryNewsStmt = conn.createStatement();
			queryNewsStmt.execute(queryNewsCenter,Statement.RETURN_GENERATED_KEYS);
			ResultSet rs1 = queryNewsStmt.getGeneratedKeys();
			int newsCenterId = 0;
			if (rs1 != null) {
				while (rs1.next()) {
					newsCenterId = rs1.getInt(1);
				}
			}
			rs1.close();
			queryNewsStmt.close();
			
			// insert into tagitem
			queryTagStmt = conn.createStatement();
			for (Integer userId : userAdminList) {
				int usrId = userAdminList.get(0);
				String queryTagitem = "insert into tagitem(Name,ParentId,IndustryId,UserId,isPrimary) values('"
						+ siloName
						+ "',null,'"
						+ industryId
						+ "','"
						+ usrId
						+ "',0)";
				queryTagStmt.executeUpdate(queryTagitem);
				break;
			}
			queryTagStmt.close();
			
			// insert in userPermission
			queryUserStmt = conn.createStatement();
			int i = 0;
			for (Integer userId : userAdminList) {
				int usrId = userAdminList.get(i);
				i++;
				String queryUserPermission = "insert into user_permission(userId,newsCenterId,groups) values('"
						+ usrId + "','" + newsCenterId + "',1)";
				queryUserStmt.executeUpdate(queryUserPermission);
			}
			queryUserStmt.close();
			
			// insert in newsLetterDelivery
			Date todaysDate = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String formattedDate = formatter.format(todaysDate);
			System.out.println("Date -" + formattedDate);

			String queryNewsLetterDelivery = "insert into newsletterdelivery(delivery,newsCenterId) values('"
					+ formattedDate + "','" + newsCenterId + "')";
			queryNewsLetterStmt = conn.createStatement();
			queryNewsLetterStmt.executeUpdate(queryNewsLetterDelivery);
			queryNewsLetterStmt.close();
			
			// insert in newsLetterDesign
			saveDefaultNewsLetterTemlateForSilo(newsCenterId, conn);
			conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					logger.log(Level.INFO,
							"lhLoginHelper ::: Transaction is being rolled back ::addSilo"
									+ e.getMessage());
					conn.rollback();
				} catch (SQLException excep) {
					excep.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (conn != null) {
				try {
					logger.log(Level.INFO,
							"lhLoginHelper ::: Transaction is being rolled back ::addSilo"
									+ e.getMessage());
					conn.rollback();
				} catch (SQLException excep) {
					excep.printStackTrace();
				}
			}
		} finally {
			if (queryIndustryEnumStmt != null) {
				queryIndustryEnumStmt.close();
			}
			if (queryNewsStmt != null) {
				queryNewsStmt.close();
			}
			if (queryTagStmt != null) {
				queryTagStmt.close();
			}
			if (queryUserStmt != null) {
				queryUserStmt.close();
			}
			if (queryNewsLetterStmt != null) {
				queryNewsLetterStmt.close();
			}

			conn.setAutoCommit(true);
			flag = true;
		}
		conn.close();
		return flag;
	}

	public boolean saveDefaultNewsLetterTemlateForSilo(int newscenterId,
			Connection conn) throws Exception {
		String headerXml = getHeaderConfigString();
		String contentXml = getContentConfigString();
		String footerXml = getFooterConfigString();
		String outlineXml = getOutlineConfigString();
		try {
			String queryNewsLetterDelivery = "insert into newsletter_design(ncid,headerXml,contentXml,footerXml,outlineXml) values("
					+ newscenterId
					+ ",'"
					+ headerXml
					+ "','"
					+ contentXml
					+ "','" + footerXml + "','" + outlineXml + "')";
			Statement queryNewsLetter = conn.createStatement();
			queryNewsLetter.executeUpdate(queryNewsLetterDelivery);
			queryNewsLetter.close();
			return true;
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	private String getOutlineConfigString() {
		final String outlineConfigString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<Design>"+
					"<table>"+
						"<width>100%</width>"+
						"<backgroundColor>#ffffff</backgroundColor>"+
						"<backgroundImage></backgroundImage>"+
						"<UseUploadedImg>false</UseUploadedImg>"+
						"<backgroundImageUrl></backgroundImageUrl>"+
					"</table>"+
				"</Design>";
		return outlineConfigString;
	}

	private String getFooterConfigString() {
		final String footerConfigString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<Design>"+
					"<table>"+
						"<width>700</width>"+
						"<cellSpacing>0</cellSpacing>"+
						"<cellPadding>0</cellPadding>"+
						"<backgroundColor>#ffffff</backgroundColor>"+
					"</table>"+
					"<firstRow>"+
						"<padding>22px 40px 0 40px</padding>"+
						"<border>5px solid #C2D22D</border>"+
						"<align>center</align>"+
						"<valign>top</valign>"+
						"<width>700</width>" +
					"</firstRow>" +
					"<nameAndemail>" +
						"<Font>" +
							"<color>#a1a1a1</color>" +
							"<family>Georgia</family>" +
							"<size>12px</size>" +
							"<weight>normal</weight>" +
							"<style>normal</style>" +
						"</Font>" +
						"<TextAlignment>center</TextAlignment>" +
						"<NameToBeDisplayed>Hans Urlik</NameToBeDisplayed>"+
						"<EmailToBeDisplayed>hus@marketscape.net</EmailToBeDisplayed>"+
					"</nameAndemail>" +
					"<unsubscribe>" +
						"<enableOrNot>yes</enableOrNot>" +
						"<Font>" +
							"<color>#a1a1a1</color>" +
							"<family>Lucida Grande</family>" +
							"<size>12px</size>" +
							"<weight>normal</weight>" +
							"<style>normal</style>" +
						"</Font>" +
						"<dispalyText>Unsubscribe Instantly</dispalyText>" +
						"<linkUrl>http://intranet.nzcorp.net/ServicesAndTools/Science/library/Pages/Alerts%20and%20media.aspx</linkUrl>" +
						"<TextAlignment>center</TextAlignment>" +
					"</unsubscribe>"+
					"<poweredBy>" +
						"<enableOrNot>yes</enableOrNot>" +
						"<Font>" +
							"<color>#333333</color>" +
							"<family>Lucida Grande</family>" +
							"<size>12px</size>" +
							"<weight>normal</weight>" +
							"<style>normal</style>" +
						"</Font>" +
						"<dispalyText>MarketScape A/S</dispalyText>" +
						"<linkUrl>http://www.marketscape.net</linkUrl>" +
						"<TextAlignment>center</TextAlignment>" +
					"</poweredBy>" +
				"</Design>";
		return footerConfigString;
	}

	private String getContentConfigString() {
		final String contentConfigString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				" <Design>" +
					"<headline>" +
						"<font>" +
							"<color>#766436</color>" +
							"<family>Lucida Grande</family>"+
							"<size>18px</size>" +
							"<weight>normal</weight>" +
							"<style>normal</style>" +
						"</font>" +
						"<padding>0px</padding>" +
					"</headline>" +
					"<newstitle>" +
						"<font>" +
							"<color>#00B2CD</color>	" +
							"<family>Lucida Grande</family>" +
							"<size>18pt</size>" +
							"<weight>normal</weight>" +
							"<style>normal</style>" +
						"</font>" +
						"<padding>3px</padding>" +
					"</newstitle>" +
					"<abstract>" +
						"<font>" +
							"<color>#333333</color>" +
							"<family>Lucida Grande</family>" +
							"<size>12pt</size>" +
							"<weight>normal</weight>" +
							"<style>normal</style>" +
						"</font>" +
						"<padding>3px</padding>" +
					"</abstract>" +
					"<publishedDate>" +
						"<font>" +
							"<color>rgb(110,110,110)</color>" +
							"<family>Lucida Grande</family>" +
							"<size>8pt</size>" +
							"<weight>normal</weight>" +
							"<style>normal</style>" +
						"</font>" +
						"<enableOrNot>yes</enableOrNot>" +
					"</publishedDate>" +
					"<source>" +
						"<font>" +
							"<color>rgb(110,110,110)</color>" +
							"<family>Lucida Grande</family>" +
							"<size>8pt</size>" +
							"<weight>normal</weight>" +
							"<style>normal</style>" +
						"</font>" +
						"<enableOrNot>yes</enableOrNot>" +
					"</source>" +
					"<tags>" +
						"<font>" +
							"<color>red</color>" +
							"<family>Arial</family>" +
							"<size>8pt</size>" +
							"<weight>normal</weight>" +
							"<style>normal</style>" +
						"</font>" +
						"<enableOrNot>yes</enableOrNot>" +
					"</tags>" +
					"<table>" +
						"<width>700</width>" +
						"<cellSpacing>0</cellSpacing>" +
						"<cellPadding>0</cellPadding>" +
						"<backgroundColor>#ffffff</backgroundColor>" +
					"</table>" +
					"<firstColumn>" +
						"<padding>22px 10px 22px 40px</padding>" +
						"<border>5px solid #C2D22D</border>" +
						"<align>left</align>" +
						"<valign>top</valign>" +
						"<width>500</width>" +
					"</firstColumn>" +
					"<secondColumn>" +
						"<padding>22px 40px 0px 0px</padding>" +
						"<border>5px solid #C2D22D</border>" +
						"<align>left</align>" +
						"<valign>top</valign>" +
						"<width>200</width>" +
					"</secondColumn>" +
					"<pulse>" +
						"<backgroundColor>#EFEFEF</backgroundColor>" +
						"<headerColor>#766436</headerColor>" +
						"<itemColor>#00b2cd</itemColor>" +
						"<headerHorizontalRulerColor>2px inset #BECBEA</headerHorizontalRulerColor>" +
					"</pulse>" +
					"<favorites>" +
						"<backgroundColor>#FFFECE</backgroundColor>" +
						"<headerColor>#766436</headerColor>" +
						"<itemColor>#00b2cd</itemColor>" +
						"<headerHorizontalRulerColor>2px inset #BECBEA</headerHorizontalRulerColor>" +
					"</favorites>" +
					"<report>" +
						"<backgroundColor>#EFEFEF</backgroundColor>" +
						"<headerColor>#766436</headerColor>" +
						"<itemColor>#00b2cd</itemColor>" +
						"<headerHorizontalRulerColor>2px inset #BECBEA</headerHorizontalRulerColor>" +
					"</report>" +
				"</Design>";
		return contentConfigString;
	}

	private String getHeaderConfigString() {
		final String headerConfigString = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>" +
				"<Design>" +
					"<AlertName>" +
						"<Font>" +
							"<color>#766436</color>" +
							"<family>Georgia</family>" +
							"<size>30px</size>" +
							"<weight>500</weight>" +
							"<style>normal</style>" +
						"</Font>" +
						"<TextAlignment>left</TextAlignment>" +
					"</AlertName>" +
					"<Logo>" +
						"<Alignment>right</Alignment>" +
						"<LogoImage>http://marketscape.facility.dir.dk/Lighthouse/images/nz/NZLogo.jpg</LogoImage>" +
						"<TargetUrl>http://www.novozymes.com/</TargetUrl>" +
						"<UseUploadedImg>false</UseUploadedImg>" +
						"<LogoImageUploadUrl> </LogoImageUploadUrl>" +
						"<AlternativeText>Novozymes-Media-Watch-Logo</AlternativeText>" +
					"</Logo>" +
					"<Date>" +
						"<Font>" +
							"<color>#bfbfbf</color>" +
							"<family>Georgia</family>" +
							"<size>12px</size>" +
							"<weight>normal</weight>" +
							"<style>italic</style>" +
						"</Font>" +
						"<TextAlignment>center</TextAlignment>" +
					"</Date>" +
					"<BaseTable>" +
						"<Width>700</Width>" +
						"<CellSpacing>0</CellSpacing>" +
						"<CellPadding>0</CellPadding>" +
						"<BackgroundColor>#ffffff</BackgroundColor>" +
					"</BaseTable>" +
				"</Design>";
		return headerConfigString;
	}

	/**
	 * This method fetches the userId of all the admins specified for the
	 * newSilo.
	 * 
	 * @param listOfAdmins
	 * @return
	 */
	public ArrayList<Integer> getAdminsUserId(String listOfAdmins) {
		ArrayList<Integer> admins = new ArrayList<Integer>();
		DBHelper dbHelper = new DBHelper();
		try {
			String adminsList = "select userId, isAdmin from user where email in("
					+ listOfAdmins + ") order by userId asc";
			Statement stmt = dbHelper.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(adminsList);
			while (rs.next()) {
				Integer userId = rs.getInt("userId");
				int isAdmin = rs.getInt("isAdmin");
				if(isAdmin == 1)
					admins.add(userId);
			}
			rs.close();
			stmt.close();
			dbHelper.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(Level.INFO,
					"lhLoginHelper ::: Unable to fetch the userId :: getAdminsUserId()"
							+ e.getMessage());
		}
		return admins;
	}

	private String printResult(Boolean result, URL currentURL) {
		stringBuilder = new StringBuilder();
		String redirectURL = currentURL + "/maintainence";
		logger.log(Level.INFO,
				" CreateNewSilo :: printResult() :: Redirect URL is :"
						+ redirectURL);
		if(result == null){
			stringBuilder.append("<html>");
			stringBuilder.append("<table>");
			stringBuilder
					.append("<tr>"
							+ "<td style=\"font-weight:bold;font-size:11pt;font-stretch:extra-expanded\">Silo name exists. Please enter a unique silo name.</td>"
							+ "<td><a href='" + redirectURL
							+ "'> Back to Silo Listing </a></td>" + "</tr>");
			stringBuilder.append("</table>");
			stringBuilder.append("</html>");
		}
		else if (result == true) {
			stringBuilder.append("<html>");
			stringBuilder.append("<table>");
			stringBuilder
					.append("<tr>"
							+ "<td style=\"font-weight:bold;font-size:11pt;font-stretch:extra-expanded\">The newsCenter has been created succesfully.</td>"
							+ "<td><a href='" + redirectURL
							+ "'> Back to Silo Listing </a></td>" + "</tr>");
			stringBuilder.append("</table>");
			stringBuilder.append("</html>");
		} else if(result == false) {
			stringBuilder.append("<html>");
			stringBuilder.append("<table>");
			stringBuilder
					.append("<tr>"
							+ "<td style=\"font-weight:bold;font-size:11pt;font-stretch:extra-expanded\">The newsCenter could not be created succesfully.</td>"
							+ "<td><a href='" + redirectURL
							+ "'> Back to Silo Listing </a></td>" + "</tr>");
			stringBuilder.append("</table>");
			stringBuilder.append("</html>");
		}
		return stringBuilder.toString();
		
	}

	/**
	 * This method will return a list of admins with singlequotes inserted in
	 * them
	 * 
	 * @param adminNames
	 * @return
	 */
	private String getAdminListFromForm(String adminNames) {
		ArrayList<String> admins = new ArrayList<String>();
		ArrayList<String> adminList = new ArrayList<String>();
		StringBuffer buf = new StringBuffer();
		final String SINGLE_QUOTE = "\'";
		final String COMMA = ",";

		try {
			StringTokenizer st = new StringTokenizer(adminNames, ",");
			while (st.hasMoreTokens()) {
				admins.add(st.nextToken());

			}
			for (String s : admins) {
				buf.append(SINGLE_QUOTE);
				buf.append(s);
				buf.append(SINGLE_QUOTE);
				buf.append(COMMA);
			}
			int lastIdx = buf.length() - 1;
			String ret = buf.substring(0, lastIdx);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			logger.log(
					Level.INFO,
					"CreateNewSilo ::: Unable to fetch the admins from form  :: getAdminListFromForm()"
							+ e.getMessage());
			return null;
		}
	}
}
