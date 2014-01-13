package com.lighthouse.login.server.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lighthouse.group.server.helper.GroupHelper;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.newscenter.server.db.DBHelper;

public class lhLoginHelper extends DBHelper {

	private StringBuilder stringBuilder;
	Logger logger = Logger.getLogger(lhLoginHelper.class.getName());
	
	
	/**
	 * validates the user trying to login
	 * @param userinfo UserInformation object
	 * @return
	 */
	public ArrayList validateUser(LhUser userinfo)
	{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ArrayList list = new ArrayList();
		try
		{
			String query1 = "select distinct (U.UserId),U.FirstName,U.LastName,U.isAdmin,U.isApproved,U.Signature,NC.NewsCenterId,US.isSubscribed,US.NewsFilterMode from user as U,newscenter as NC,usersubscription US where U.email = '"+userinfo.getEmail()+"' and U.password = '"+userinfo.getPassword()+"' and NC.IndustryEnumId = "+userinfo.getUserSelectedIndustryID()+" and U.UserId = US.UserId";
			Statement stmt = getConnection().createStatement();
			ResultSet rs1 = stmt.executeQuery(query1);
			while(rs1.next()){
				if(rs1.getInt("isAdmin") == 1 || rs1.getInt("isAdmin") == 2){ // if user is an admin or sales executive
					list.add("true");
					
					//HttpSession mySes = request.getSession(true); 
					int userId = rs1.getInt("UserId");
					int ncid = rs1.getInt("NewsCenterId");
					userinfo.setUserId(userId);
					userinfo.setUserSelectedNewsCenterID(ncid);
					userinfo.setUserSelectedIndustryID(userinfo.getUserSelectedIndustryID());
					userinfo.setIsAdmin(rs1.getInt("isAdmin"));
					userinfo.setApproved(rs1.getInt("isApproved"));
					userinfo.setIsSubscribed(rs1.getInt("isSubscribed"));
					userinfo.setNewsFilterMode(rs1.getInt("NewsFilterMode"));
					userinfo.setSignature(rs1.getString("Signature"));
					userinfo.setFirstname(rs1.getString("FirstName"));
					userinfo.setLastname(rs1.getString("LastName"));
					LhUserPermission userPermission = getUserPermission(userId, ncid);
					userinfo.setUserPermission(userPermission);
					
					list.add(userinfo);
					
					//mySes.setAttribute(UserHelper.USER_INFO,userinfo);
					Date date = new Date();
					
					stmt = getConnection().createStatement();
					query1 = "insert into loginStatistics(userId,industryEnumId,timeOfLogin) values("+userinfo.getUserId()+","+userinfo.getUserSelectedIndustryID()+",'"+df.format(date)+"')";
					stmt.executeUpdate(query1);
					System.out.println("User log: - UserEmail = "+userinfo.getEmail()+" NewsCenterID = "+userinfo.getUserSelectedIndustryID()+" Login Time = "+date);
				}
				else{
				String query = "select U.*,NC.Name,NC.NewsCenterId,US.Duration,US.DurationLeft,US.isSubscribed,US.NewsFilterMode from newscenter as NC,user as U,usersubscription as US where U.email = '"+userinfo.getEmail()+"' and U.password = '"+userinfo.getPassword()+"' and NC.IndustryEnumId = "+userinfo.getUserSelectedIndustryID()+" and U.IndustryEnumId = NC.IndustryEnumId and U.UserId = US.UserId";
				stmt = getConnection().createStatement();
				//HttpSession mySes = request.getSession(true); 
				ResultSet rs = stmt.executeQuery(query);
					if(rs.next())
					{   // for normal user (subscriber)
						list.add("true");
						
						int userId = rs1.getInt("UserId");
						int ncid = rs1.getInt("NewsCenterId");
						userinfo.setUserId(userId);
						userinfo.setUserSelectedNewsCenterID(ncid);
						userinfo.setUserSelectedIndustryID(userinfo.getUserSelectedIndustryID());
						userinfo.setIsAdmin(rs.getInt("isAdmin"));
						userinfo.setApproved(rs.getInt("isApproved"));
						userinfo.setDurationLeft(rs.getInt("DurationLeft"));
						userinfo.setIsSubscribed(rs1.getInt("isSubscribed"));
						userinfo.setNewsFilterMode(rs1.getInt("NewsFilterMode"));
						userinfo.setSignature(rs.getString("Signature"));
						userinfo.setFirstname(rs1.getString("FirstName"));
						userinfo.setLastname(rs1.getString("LastName"));
						LhUserPermission userPermission = getUserPermission(userId, ncid);
						userinfo.setUserPermission(userPermission);
						
						list.add(userinfo);
						
						//mySes.setAttribute(UserHelper.USER_INFO,userinfo);	
						Date date = new Date();
						stmt = getConnection().createStatement();
						query1 = "insert into loginStatistics(userId,industryEnumId,timeOfLogin) values("+userinfo.getUserId()+","+userinfo.getUserSelectedIndustryID()+",'"+df.format(date)+"')";
						stmt.executeUpdate(query1);
						System.out.println("User log: - UserEmail = "+userinfo.getEmail()+" NewsCenterID = "+userinfo.getUserSelectedNewsCenterID()+" Login Time = "+date);
					    
					}
					else
					{ // user not found (email / pass does not match)
						list.add("false");
						
					}
					rs.close();
				}
			}
			rs1.close();
			stmt.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			System.out.println("Error in validateUser IN UserInformatonServiceImpl");
			list.add("false");
		    return list;
		}
		return list;
	}
	

	/**
	 * populates the user permission object
	 * @param userId
	 * @param userSelectedNewsCenterID
	 * @return
	 * @throws SQLException
	 */
	private LhUserPermission getUserPermission(int userId,int userSelectedNewsCenterID) throws SQLException {
		LhUserPermission lhUserPermission=null;
		try{
			String query= "SELECT * FROM user_permission where userId = "+userId+" and newsCenterId = "+userSelectedNewsCenterID;
			Statement stmt=getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				 lhUserPermission=new LhUserPermission();
				 lhUserPermission.setId(Integer.parseInt(rs.getString("id")));
				 lhUserPermission.setMailAlertPermitted((Integer.parseInt(rs.getString("mailAlert"))));
				 lhUserPermission.setGroupsPermitted(Integer.parseInt(rs.getString("groups")));
				 lhUserPermission.setReportsPermitted((Integer.parseInt(rs.getString("reports"))));
				 lhUserPermission.setCommentsPermitted((Integer.parseInt(rs.getString("comments"))));
				 lhUserPermission.setViewsPermitted((Integer.parseInt(rs.getString("views"))));
				 lhUserPermission.setSearchPermitted((Integer.parseInt(rs.getString("search"))));
				 lhUserPermission.setPrimaryHeadLinePermitted((Integer.parseInt(rs.getString("primaryHeadline"))));
				 lhUserPermission.setRssermitted((Integer.parseInt(rs.getString("rss"))));
				 lhUserPermission.setSharePermitted((Integer.parseInt(rs.getString("share"))));
				 lhUserPermission.setPulsePermitted((Integer.parseInt(rs.getString("pulse"))));
				 lhUserPermission.setFavoriteGroupPermitted((Integer.parseInt(rs.getString("favoriteGroup"))));
				 lhUserPermission.setId(userId);
				 lhUserPermission.setNewsCenterId((userSelectedNewsCenterID));
			}
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return lhUserPermission;
	}
}
