package com.lighthouse.login.user.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.admin.client.UserAdminInformation;
import com.admin.server.MailClient;
import com.lighthouse.admin.server.LHMailClient;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.login.client.UserInformation;

import com.newscenter.server.db.DBHelper;

public class LhUserHelper extends DBHelper{

	private String subject = "";
	private String body = "";
	private String smtpusername;
	private String smtppassword;
	private String smtphost;
	private String smtpport;
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getSmtpusername() {
		return smtpusername;
	}

	public void setSmtpusername(String smtpusername) {
		this.smtpusername = smtpusername;
	}

	public String getSmtppassword() {
		return smtppassword;
	}

	public void setSmtppassword(String smtppassword) {
		this.smtppassword = smtppassword;
	}

	public String getSmtphost() {
		return smtphost;
	}

	public void setSmtphost(String smtphost) {
		this.smtphost = smtphost;
	}

	public String getSmtpport() {
		return smtpport;
	}

	public void setSmtpport(String smtpport) {
		this.smtpport = smtpport;
	}

	public boolean saveUserPermissions(LhUserPermission permission) {
		boolean value = false;
		PreparedStatement prestmt = null;
		try {
			Connection conn = (Connection) getConnection();
			String query = "update user_permission set mailAlert=?,groups=?,reports=?,comments=?,views=?,search=?,primaryHeadline=?,rss=?,share=?,pulse=?,favoriteGroup=? where newsCenterId ="
					+permission.getNewsCenterId()+" and userId="+permission.getUserId();
			prestmt = (PreparedStatement) conn.prepareStatement(query);
			prestmt.setInt(1, permission.isMailAlertPermitted());
			prestmt.setInt(2, permission.isGroupsPermitted());
			prestmt.setInt(3, permission.isReportsPermitted());
			prestmt.setInt(4, permission.isCommentsPermitted());
			prestmt.setInt(5, permission.isViewsPermitted());
			prestmt.setInt(6, permission.isSearchPermitted());
			prestmt.setInt(7, permission.isPrimaryHeadLinePermitted());
			prestmt.setInt(8, permission.isRssPermitted());
		    prestmt.setInt(9, permission.isSharePermitted());
		    prestmt.setInt(10, permission.isPulsePermitted());
		    prestmt.setInt(11, permission.isFavoriteGroupPermitted());
			prestmt.executeUpdate();
			prestmt.close();
			value = true;
		} catch (Exception e) {
			value = false;
			e.printStackTrace();
		}
		prestmt = null;
		return value;
	}
	
	public LhUserPermission getUserPermissions(int newscenterid,String email) {
		int userId=getUser(email,newscenterid);
		LhUserPermission lhUserPermission = new LhUserPermission();
		String query;
		int cnt=0;
		try {
			Connection conn = (Connection) getConnection();
			query = "select mailAlert,groups,reports,comments,views,search,primaryHeadline,rss,share,pulse,favoriteGroup from user_permission where userId="
					+ userId + " and newsCenterId=" + newscenterid;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				cnt++;
			lhUserPermission.setMailAlertPermitted(rs.getInt("mailAlert"));
			lhUserPermission.setGroupsPermitted(rs.getInt("groups"));
			lhUserPermission.setReportsPermitted(rs.getInt("reports"));
			lhUserPermission.setCommentsPermitted(rs.getInt("comments"));
			lhUserPermission.setViewsPermitted(rs.getInt("views"));
			lhUserPermission.setSearchPermitted(rs.getInt("search"));
			lhUserPermission.setPrimaryHeadLinePermitted(rs.getInt("primaryHeadline"));
			lhUserPermission.setRssermitted(rs.getInt("rss"));
			lhUserPermission.setSharePermitted(rs.getInt("share"));
			lhUserPermission.setPulsePermitted(rs.getInt("pulse"));
			lhUserPermission.setFavoriteGroupPermitted(rs.getInt("favoriteGroup"));
			lhUserPermission.setUserId(userId);
			lhUserPermission.setNewsCenterId(newscenterid);
			
			}
			if(cnt==0)
			{
				lhUserPermission=null;
			}
			rs.close();
			stmt.close();
		} catch (Exception ex) {

			ex.printStackTrace();
		}

		return lhUserPermission;
	}
	
	
	public int getUser(String emailId, int newscenterid)
	{
		String query;
		int userId = 0;
		try {

			Connection conn = (Connection) getConnection();
			query = "select UserId,isadmin from user where email='"+emailId +"'";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
		
			while(rs.next()){
				userId=rs.getInt("UserId");
				if(rs.getString("isadmin").contains("1")){
					String query1 = "select u.UserId, u.isAdmin, up.* from user u, user_permission up where email=\""+emailId+"\" and u.userid = up.userid and up.newscenterid="+newscenterid;
					Statement stmt1 = conn.createStatement();
					ResultSet rs1 = stmt1.executeQuery(query1);
					if(!rs1.last()){
						String query2 = "insert into user_permission values(null,"+userId+","+newscenterid+",0,0,0,0,0,0,0,0,0,0,0)";
						stmt1.executeUpdate(query2);
					}
					rs1.close();
					stmt1.close();
				}
				
			}
			
			rs.close();
			stmt.close();
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return userId;

		
	}

	public ArrayList getEntityList(int newscenterid, int isAdmin) {
		ArrayList list =new ArrayList();
		Connection conn = (Connection) getConnection();
		try {
			String queryAllUsers = "select email from user where IndustryEnumId="+newscenterid+" and isAdmin=0";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(queryAllUsers);
			while(rs.next()){
				list.add(rs.getString("email"));
				}
			
			rs.close();
			stmt.close();
		  	
			String queryAdminUsers = "select email from user where isAdmin=1";  
			Statement st = conn.createStatement();
			ResultSet rs1 = st.executeQuery(queryAdminUsers);
			while(rs1.next()){
				list.add(rs1.getString("email"));
				}
			rs1.close();
			st.close();
		} catch (Exception ex) {

			ex.printStackTrace();
		}
         return list;
		
	}
	
	public boolean sendMailForApproval(UserAdminInformation userinfo){
		subject=userinfo.getSubjectMail();
		body=userinfo.getBodyMail();
		try{
			MailClient testClient = new MailClient(getSmtpusername(), getSmtppassword(), getSmtpport(), getSmtphost());
			testClient.sendMessage("no_reply@newscatalyst.com", userinfo.getRecipientsMail(), subject, body);
			return true;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
	
	public String forgotPasswordRetrieve(UserInformation userinfo){
		try{
			Connection conn = (Connection) getConnection();
			String passwordOfUser = "";
			String query = "select password from user where email = '"+userinfo.getEmail()+"'";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				passwordOfUser = rs.getString("password");
				userinfo.setPassword(passwordOfUser);
				System.out.println("The forgotten password of the user is "+passwordOfUser);
			}
			rs.close();
			stmt.close();
			return passwordOfUser;
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Error in retriving the password from db");
			return null;
		}
	}
	
	public boolean sendMailForForgotpassword(UserInformation userinfo) {
		subject=userinfo.getSubjectMail();
		body=userinfo.getBodyMail();
		try{
			MailClient testClient = new MailClient(getSmtpusername(), getSmtppassword(), getSmtpport(), getSmtphost());
			testClient.sendMessage("no_reply@newscatalyst.com", userinfo.getRecipientsMail(), subject, body);
			return true;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}

	public void deleteUser(HashMap hashmap){
		try{
			Connection conn = (Connection) getConnection();
			for(Object obj:hashmap.keySet()){
				int id = (Integer)obj;
				Statement stmt=conn.createStatement();
				String query = "delete from user where userId = "+id;
				stmt.executeUpdate(query);
				stmt.close();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void sendMailForAccessNewsItem(UserInformation userInfo) {
		subject=userInfo.getSubjectMail();
		body=userInfo.getBodyMail();
		ArrayList<String> listOfAdmin = new ArrayList<String>();
		try {
			Connection conn = (Connection) getConnection();
			String query = "SELECT email FROM user where IndustryEnumId="+userInfo.getUserSelectedIndustryID()+" and isAdmin=1";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				listOfAdmin.add(rs.getString("email"));
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try{
			LHMailClient testClient = new LHMailClient(getSmtpusername(), getSmtppassword(), getSmtpport(), getSmtphost());
			testClient.sendMessage("no_reply@newscatalyst.com", listOfAdmin, subject, body);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
    
	/**
	 * This method sends the mail to list of admins in the industry if a dead link is reported. 
	 * @param userInformation
	 */
	public void sendMailForReportDeadlink(UserInformation userInformation) {
		subject=userInformation.getSubjectMail();
		body=userInformation.getBodyMail();
		ArrayList<String> listOfAdmin = new ArrayList<String>();
				
		try {
			Connection conn = (Connection) getConnection();
			String query = "SELECT email FROM user where IndustryEnumId="+userInformation.getUserSelectedIndustryID()+" and isAdmin=1";
			Statement stmt=conn.createStatement();
			ResultSet rs =stmt.executeQuery(query);
			while(rs.next()){
				listOfAdmin.add(rs.getString("email"));
			
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try{
			LHMailClient testClient = new LHMailClient(getSmtpusername(), getSmtppassword(), getSmtpport(), getSmtphost());
			testClient.sendMessage("no_reply@newscatalyst.com", listOfAdmin, subject, body);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
}
