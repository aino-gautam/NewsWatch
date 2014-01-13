package com.lighthouse.newsletter.server;

import java.sql.Connection;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletConfig;

import com.lighthouse.group.client.domain.Group;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.newsletter.client.domain.NewsletterGroupSelectionConfig;
import com.lighthouse.newsletter.client.domain.NewsletterInformation;
import com.lighthouse.newsletter.client.domain.UserNewsletterAlertConfig;
import com.newscenter.server.categorydb.ItemProviderServiceImpl;
import com.newscenter.server.db.DBHelper;


/**
 * 
 * @author pritam@ensarm.com
 *
 */

/**
 * This class is used to create and update the newsletter alert configuration as well as
 * used to maintain the reference of user with their newscenter and their respective alerts.
 */
public class UserNewsletterAlertConfigHelper extends DBHelper{
	
     Logger logger=Logger.getLogger(UserNewsletterAlertConfigHelper.class.getName());
	
     /**
      * This method will be called when a user tries to create a newsletter alert (via 3 step wizard)
      * @param config(domain class object which contains alert details.)
      * @return true if newsletter alert created successfully.
      *         false if newsletter alert creation has a problems.
      */
	public boolean createUserNewsletterAlert(UserNewsletterAlertConfig config,NewsletterGroupSelectionConfig groupConfig){
		try{
			String name=config.getAlertName();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper :: creating alert :: "+name+"  :: createUserNewsletterAlert()   ] ");
			Connection tempConnection=getConnection();			
			ArrayList<UserNewsletterAlertConfig> alertList=getAllUserAlertList(groupConfig.getUserId(), groupConfig.getNewscenterId());
			if(alertList != null){
				for(UserNewsletterAlertConfig alertConfig:alertList){
					if(alertConfig.getAlertName().equals(name))
						return false;
				}
			}
			
			String query="insert into usernewsletteralert(alertName,frequency,preferenceDevice,isSingle,timeOfDelivery,letterFormat,timeZone) values('"+config.getAlertName()+"','"+config.getFrequency()+"','"+config.getPrefDevice()+"','"+config.getIsSingle()+"','"+config.getTimeOfDelivery()+"','"+config.getLetterFormat()+"','"+config.getTimeZone()+"')";
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper :: createUserNewsletterAlert() insert query:: "+query+"  ] ");
			Statement stmt=tempConnection.createStatement();
			stmt.execute(query,Statement.RETURN_GENERATED_KEYS);

			ResultSet rs = stmt.getGeneratedKeys();
			while(rs.next()){
				int alertId = rs.getInt(1);
				groupConfig.setAlertId(alertId);
			}
			rs.close();
			stmt.close();
			createNewsletterGroupSelection(groupConfig);
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper :: created alert :: createUserNewsletterAlert()   ] ");
			return true;
		}catch(Exception ex){
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- Unable to create alert :: createUserNewsletterAlert()   ] " +
					""+ex.getMessage());
			return false;
		}
	}
	
	/**
     * This method will be called when a user tries to create a newsletter alert (via 3 step wizard)
     * @param config(domain class object which contains alert details.)
     * @return true if newsletter alert created successfully.
     *         false if newsletter alert creation has a problems.
     */
	public UserNewsletterAlertConfig createUserNewsletterAlert(UserNewsletterAlertConfig config,ArrayList<NewsletterGroupSelectionConfig> groupSelectionsList,int userId,int newscenterId){
		try{
			String name=config.getAlertName();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper :: creating alert :: "+name+"  :: createUserNewsletterAlert(config, groupSelectionList, userId, newscenterId)   ] ");
			Connection tempConnection=getConnection();	
			ArrayList<UserNewsletterAlertConfig> alertList=getAllUserAlertList(userId, newscenterId);
			if(alertList != null){
				for(UserNewsletterAlertConfig alertConfig:alertList){
					if(alertConfig.getAlertName().equals(name))
						return null;
				}
			}
			String query="insert into usernewsletteralert(alertName,frequency,preferenceDevice,isSingle,letterFormat,timeZone) values('"+config.getAlertName()+"','"+config.getFrequency()+"','"+config.getPrefDevice()+"','"+config.getIsSingle()+"','"+config.getLetterFormat()+"','"+config.getTimeZone()+"')";
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper :: createUserNewsletterAlert(config, groupSelectionList, userId, newscenterId) insert query:: "+query+"  ] ");
			Statement stmt=tempConnection.createStatement();
			stmt.execute(query,Statement.RETURN_GENERATED_KEYS);
			
			ResultSet rs = stmt.getGeneratedKeys();
			int alertId = 0;
			while(rs.next()){
				alertId = rs.getInt(1);
				config.setAlertId(alertId);
			}
			rs.close();
			stmt.close();
			
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper :: createUserNewsletterAlert(config, groupSelectionList, userId, newscenterId) :: creating NewsletterGroupSelection  ] ");
			if(groupSelectionsList!=null){
				Iterator iter = groupSelectionsList.iterator();
				while(iter.hasNext()){
					NewsletterGroupSelectionConfig groupConfig = (NewsletterGroupSelectionConfig) iter.next();
					groupConfig.setAlertId(alertId);
					groupConfig.setUserId(userId);
					createNewsletterGroupSelection(groupConfig);
				}
			}else{
				NewsletterGroupSelectionConfig groupConfig = new NewsletterGroupSelectionConfig();
				groupConfig.setAlertId(alertId);
				groupConfig.setUserId(userId);
				groupConfig.setNewscenterId(newscenterId);
				createNewsletterGroupSelection(groupConfig);
			}
			
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper :: created alert :: createUserNewsletterAlert(config, groupSelectionList, userId, newscenterId)   ] ");
			return config;
	}catch(Exception ex){
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- Unable to create alert :: createUserNewsletterAlert(config, groupSelectionList, userId, newscenterId)   ] " +
					""+ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}
	
	public boolean updateUserNewsletterAlert(UserNewsletterAlertConfig config,ArrayList<NewsletterGroupSelectionConfig> groupSelectionsList){
		try{
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- updating alert :: updateUserNewsletterAlert(config, groupSelectionsList )   ] ");
			Connection tempConnection=getConnection();
			String query="update usernewsletteralert set alertName='"+config.getAlertName()+"',frequency='"+config.getFrequency()+"',preferenceDevice='"+config.getPrefDevice()+"',isSingle="+config.getIsSingle()+" where aId="+config.getAlertId();
			Statement state=tempConnection.createStatement();
			state.executeUpdate(query);
			state.close();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- updated alert :: updateUserNewsletterAlert(config, groupSelectionsList )   ] ");
			
			if(groupSelectionsList.size()>0){
				logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper:: updateUserNewsletterAlert(config, groupSelectionsList ) :: updating newsletter group selections  ] ");
				boolean isDeleted = deleteNewsLetterGroupSelectionsForAlert(config.getAlertId());
				if(isDeleted){
					Iterator iter = groupSelectionsList.iterator();
					while(iter.hasNext()){
						NewsletterGroupSelectionConfig groupConfig = (NewsletterGroupSelectionConfig) iter.next();
						groupConfig.setAlertId(config.getAlertId());
						createNewsletterGroupSelection(groupConfig);
					}
				}else
					return false;
			}
			return true;
		}catch(Exception ex){
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- Unable to update alert :: updateUserNewsletterAlert(config, groupSelectionsList )   ] "+ex.getMessage());
			return false;
		}
	}
	
	/**
	 * This method is used to update the alert configuration (via 3 step wizard)
	 * @param frequency(i.e newsletter frequency Daily/Weekly)
	 * @param preference(i.e preference device selected by user
	 * @param alertId(id of the alert which has to be updated
	 * @return true if newsletter alert updated successfully
	 *         false if newsletter alert updation goes unsuccessful.
	 */
	public boolean updateUserNewsletterAlert(String frequency,String preference,int alertId){
		try{
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- updating alert :: updateUserNewsletterAlert()   ] ");
			Connection tempConnection=getConnection();
			String query="update usernewsletteralert set frequency='"+frequency+"',preferenceDevice='"+preference+"' where aId="+alertId;
			Statement state=tempConnection.createStatement();
			state.executeUpdate(query);
			state.close();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- updated alert :: updateUserNewsletterAlert()   ] ");
			return true;
		}catch(Exception ex){
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- Unable to update alert :: updateUserNewsletterAlert()   ] "+ex.getMessage());
			return false;
		}
	}
	
	/**
	 * This method is used to keep information about which group selected by which user 
	 * @param config(i.e. object of the newslettergroupselectionconfig object
	 * @return true if creation of user group selection done successfully.
	 *         false if creation of user group selection goes unsuccessful.
	 */
	public boolean createNewsletterGroupSelection(NewsletterGroupSelectionConfig config) {
		try{
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- inserting group selections :: createNewsletterGroupSelection()   ]");
			String query="insert into newslettergroupselection(alertId,userId,newscenterId,groupId) values("+config.getAlertId()+","+config.getUserId()+","+config.getNewscenterId()+","+config.getGroupId()+")";
			Statement state=getConnection().createStatement();
			state.executeUpdate(query);
			state.close();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- inserted group selections :: createNewsletterGroupSelection()   ]");
			return true;
		}catch(Exception ex){
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- Unable to insert group selections :: createNewsletterGroupSelection()   ] "+ex.getMessage());
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * This method is used for updation of user group selection.
	 * @param ncid(ie. id of newscenter)
	 * @param grpId(ie. id of the group)
	 * @param id(ie. id of the newslettergroupselection table) 
	 * @return true if updation is successful about group selection
	 *         false if updation is unsuccessful
	 */
	public boolean updateNewsletterGroupSelection(int ncid,int grpId,int Id) {
		try{
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- updating group selection :: updateNewsletterGroupSelection()   ] ");
			Connection tempConnection=getConnection();
			String query="update newslettergroupselection set newscenterId="+ncid+",groupId="+grpId+" where Id="+Id;
			Statement state=tempConnection.createStatement();
			state.executeUpdate(query);
			state.close();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- updated group selection :: updateNewsletterGroupSelection()   ] ");
			return true;
		}catch(Exception ex){
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- Unable to update group selection :: updateNewsletterGroupSelection()   ] "+ex.getMessage());
		}
		return false;
	}
	
	/**
	 * deletes all newsletter group selections for an alert
	 * @param alertId of the alert for which selections are to be deleted
	 * @return true if successfully deleted, else false
	 */
	public boolean deleteNewsLetterGroupSelectionsForAlert(int alertId){
		try{
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- deleting group selection :: deleteNewsLetterGroupSelectionsForAlert()   ] ");
			String query="delete from newslettergroupselection where alertId="+alertId;
			Statement stmt=getConnection().createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- deleted group selection :: deleteNewsLetterGroupSelectionsForAlert()   ] ");
			return true;
		}catch(Exception ex){
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- Unable to delete group selection :: deleteNewsLetterGroupSelectionsForAlert()   ] "+ex.getMessage());
		}
		return false;
	}
	
	
 	/**
	 * This method is used to get all alerts 
	 * @param userId(ie. id of the user whose associated alerts want to be fetched
	 * @return list (ie. list of alerts )
	 */
	public ArrayList<UserNewsletterAlertConfig> getAllUserAlertList(int userId, int ncid){
		try{
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- fetching all user alerts for USER :: "+userId+" and NCID::"+ncid+" ::: getAllUserAlertList()   ] ");
			ArrayList<UserNewsletterAlertConfig> resultList=new ArrayList<UserNewsletterAlertConfig>();
			Connection conn = getConnection();
			String query="select distinct u.* from usernewsletteralert u,newslettergroupselection n where u.aid=n.alertId and n.newscenterId="+ncid+" and n.userId="+userId;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				int alertId = rs.getInt("aId");
				String alertName=rs.getString("alertName");
				String frequency=rs.getString("frequency");
				String prefDevice=rs.getString("preferenceDevice");
				int isSingle = rs.getInt("isSingle");
				String letterFormat=rs.getString("letterFormat");
				String timeZone=rs.getString("timeZone");
				
				UserNewsletterAlertConfig config=new UserNewsletterAlertConfig();
				config.setAlertId(alertId);
				config.setAlertName(alertName);
				config.setFrequency(frequency);
				config.setPrefDevice(prefDevice);
				config.setIsSingle(isSingle);
				config.setLetterFormat(letterFormat);
				config.setTimeZone(timeZone);
				
				logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---fetching groups for ALERTID: "+alertId+" ---- getAllUserAlertList()   ] ");
				ArrayList<Group> alertGroupList = new ArrayList<Group>();
				String query1 = "select distinct g.* from `group` g, newslettergroupselection n where n.groupId = g.groupId and n.alertId="+alertId;
				Statement stmt1 = getConnection().createStatement();
				ResultSet rs1 = stmt1.executeQuery(query1);
				while(rs1.next()){
					Group group = new Group();
					group.setGroupId(rs1.getInt("groupId"));
					group.setGroupName(rs1.getString("groupName"));
					group.setIsMandatory(rs1.getInt("isMandatory"));
					group.setGroupParentId(rs1.getInt("groupParentId"));
					group.setNewsCenterId(rs1.getInt("newsCenterId"));
					group.setUserId(rs1.getInt("userId"));
					group.setNewsFilterMode(rs1.getInt("newsFilterMode"));
					alertGroupList.add(group);
				}
				logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---fetched groups: "+alertGroupList.size()+" for ALERTID: "+alertId+" ---- getAllUserAlertList()   ] ");
				rs1.close();
				stmt1.close();
				config.setAlertGroupList(alertGroupList);
				resultList.add(config);
			}
			rs.close();
			stmt .close();
			return resultList;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- Unable to get all user alerts :: getAllUserAlertList()  :: returning NULL ] "+ex.getMessage());
			return null;
		}
	}
	
	public UserNewsletterAlertConfig getIsSingleAlertForUser(int userId, int ncid){
		try{
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- fetching single user alert for USER ::"+userId+" and NCID"+ncid+" getIsSingleAlertForUser()   ] ");
			UserNewsletterAlertConfig config=null;
			Connection conn = getConnection();
			String query="select distinct u.* from usernewsletteralert u,newslettergroupselection n where u.isSingle=1 and n.newscenterId="+ncid+" and n.userId="+userId+" and u.aid=n.alertId";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				int alertId = rs.getInt("aId");
				String alertName=rs.getString("alertName");
				String frequency=rs.getString("frequency");
				//String prefDevice=rs.getString("preferenceDevice");
				int isSingle = rs.getInt("isSingle");
				String letterFormat=rs.getString("letterFormat");
				String timeZone=rs.getString("timeZone");
				
				config=new UserNewsletterAlertConfig();
				config.setAlertId(alertId);
				config.setAlertName(alertName);
				config.setFrequency(frequency);
				//config.setPrefDevice(prefDevice);
				config.setIsSingle(isSingle);
				config.setLetterFormat(letterFormat);
				config.setTimeZone(timeZone);
			}
			rs.close();
			stmt .close();
			return config;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- Unable to get single user alert for USER ::"+userId+" and NCID"+ncid+"  :: getIsSingleAlertForUser() ::: returning NULL  ] "+ex.getMessage());
			return null;
		}
	}
	/**
	 * This method is used to delete the alerts 
	 * @param alertId
	 * @return true if alert deleted successfully
	 *         false if alert deletion failed
	 */
	
	public boolean deleteAlert(int alertId){
		try{
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- deleting alert :: "+alertId+" deleteAlert()   ] ");
			Connection tempConnection=getConnection();
			String query="delete from usernewsletteralert where aId="+alertId+"";
			Statement state=tempConnection.createStatement();
			state.executeUpdate(query);
			state.close();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- deleted alert :: deleteAlert()   ] ");
			return true;
		}
		catch(Exception ex){
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- Unable to delete alert :: "+alertId+" deleteAlert()   ] "+ex);
			return false;
		}
		
		
	}
	
	
	public UserNewsletterAlertConfig getAlert(int id) throws SQLException {
		logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---getAlert() :: "+id+" ::: initiated ] ");
		UserNewsletterAlertConfig config = null;
		Connection conn = getConnection();
		try {

			String query = "SELECT * FROM usernewsletteralert u where u.aId="+id;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);

			while (rs.next()) {
				int alertId = rs.getInt("aId");
				String alertName = rs.getString("alertName");
				String frequency = rs.getString("frequency");
				int isSingle = rs.getInt("isSingle");
				String letterFormat = rs.getString("letterFormat");
				String timeZone = rs.getString("timeZone");
				
				ArrayList<Group> alertGroupList = new ArrayList<Group>();
				String query1 = "select g.* from `group` g, newslettergroupselection n where n.groupId = g.groupId and n.alertId="+id;
				Statement stmt1 = conn.createStatement();
				ResultSet rs1 = stmt1.executeQuery(query1);
				while(rs1.next()){
					Group group = new Group();
					group.setGroupId(rs1.getInt("groupId"));
					group.setGroupName(rs1.getString("groupName"));
					group.setIsMandatory(rs1.getInt("isMandatory"));
					group.setGroupParentId(rs1.getInt("groupParentId"));
					group.setNewsCenterId(rs1.getInt("newsCenterId"));
					group.setUserId(rs1.getInt("userId"));
					group.setNewsFilterMode(rs1.getInt("newsFilterMode"));
					alertGroupList.add(group);
				}
				config = new UserNewsletterAlertConfig();
				config.setAlertId(alertId);
				config.setAlertName(alertName);
				config.setFrequency(frequency);
				config.setIsSingle(isSingle);
				config.setLetterFormat(letterFormat);
				config.setTimeZone(timeZone);
				config.setAlertGroupList(alertGroupList);
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---getAlert() :: "+id+" EXCEPTION!!!] ");
			e.printStackTrace();
		} finally {
			conn.close();
		}
		logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---getAlert() :: "+id+" ::: completed] ");
		return config;
	}
	
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
				 lhUserPermission.setUserId(userId);
				 lhUserPermission.setNewsCenterId((userSelectedNewsCenterID));
			}
			rs.close();
			stmt.close();
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return lhUserPermission;
	}
	
	public NewsletterInformation getNewsItemForAlert(int alertId,int userId,int ncid,ServletConfig servletConfig){
		logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---getNewsItemForAlert() :: "+alertId+" and USER::"+userId+" and NCID:: "+ncid+" ] ");
		
		LhNewsletterHelper lhNewsletterHelper=new LhNewsletterHelper();
		UserNewsletterAlertConfig alertConfig=null;
		NewsletterInformation newsletterInformation = null;
		try{
			LhUserPermission lhUserPermission =  getUserPermission(userId, ncid);
			
			ItemProviderServiceImpl itemprovider = new ItemProviderServiceImpl();
			itemprovider.init(servletConfig);
		//	LhNewsletterHelper newsletterHelper=new LhNewsletterHelper();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---getNewsItemForAlert() fetching last delivery time ] ");
			Timestamp datetime=lhNewsletterHelper.getLastNewsLetterDelivery(ncid);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String today=sdf.format(new Date());
			String newsLetterDate=sdf.format(datetime); 
			
			if(newsLetterDate.equals(today)){
				 long oneDay = 1 * 24 * 60 * 60 * 1000;
			   	datetime.setTime(datetime.getTime()-oneDay);
			}
			
			alertConfig=getAlert(alertId);
			
			if(alertConfig != null){
				boolean isWeekly = false;
				if(alertConfig.getFrequency().equalsIgnoreCase("Weekly"))
					isWeekly = true;
				
				if(alertConfig.getIsSingle()==1){// check for merge alert..
					logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---getNewsItemForAlert() Merged Alert ] ");
					ArrayList<UserNewsletterAlertConfig> alertList =lhNewsletterHelper.getUserAlerts(userId, ncid);
					HashMap<UserNewsletterAlertConfig, NewsletterInformation> alertNewsMap = lhNewsletterHelper.fetchMergedAlertNews(alertConfig, alertList, lhUserPermission,datetime);
					for(NewsletterInformation newsInfo : alertNewsMap.values()){
						newsletterInformation = newsInfo;
					}
				}
				else{
					logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---getNewsItemForAlert() Individual Alert ] ");
					newsletterInformation = lhNewsletterHelper.getIndividualAlertNews(alertConfig,ncid, lhUserPermission, datetime, isWeekly);
				}
			}
		}
		catch (Exception e) {
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---getNewsItemForAlert() EXCEPTION!!!] ");
			e.printStackTrace();
		}
		finally{
			lhNewsletterHelper.closeConnection();
		}
		return newsletterInformation; 
	}

	public int createAlert(UserNewsletterAlertConfig config, int userId, int ncid) {
		logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---createAlert() initiated ] ");
		int alertId = -1;
		try{
			ArrayList<UserNewsletterAlertConfig> alertList=getAllUserAlertList(userId, ncid);
			for(UserNewsletterAlertConfig alertConfig:alertList){
				if(alertConfig.getAlertName().equals(config.getAlertName()))
					return 0;
			}
			
		Connection tempConnection=getConnection();	
		String query="insert into usernewsletteralert values(null,'"+config.getAlertName()+"','"+config.getFrequency()+"',null,0,null,null,null)";
		Statement stmt=tempConnection.createStatement();
		stmt.execute(query,Statement.RETURN_GENERATED_KEYS);
		ResultSet rs = stmt.getGeneratedKeys();
		while(rs.next()){
			alertId = rs.getInt(1);
		}
		rs.close();
		stmt.close();
		}catch (Exception e) {
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---createAlert() EXCEPTION!!!! ] ");
			e.printStackTrace();
		}
		
		logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---createAlert() completed ] ");
		return alertId;
	}

	public int getDafaultGroup(Integer ncid) {
		logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---getDeafultGroup() initiated ] ");
		int groupId = -1;
		try{
			Connection tempConnection=getConnection();
			String query="SELECT groupId FROM `group` g where isDefaultGroup=1 and newsCenterId="+ncid;
			Statement stmt=tempConnection.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next()){
				groupId = rs.getInt(1);
			}
			rs.close();
			stmt.close();
		}catch (Exception e) {
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---getDeafultGroup() EXCEPTION!!!! ] ");
			e.printStackTrace();
		}
		logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---getDeafultGroup() completed :: groupId ::"+ groupId +" ] ");
		return groupId;
		
	}

	public boolean accociateNewsLetterGroupSelection(Integer userId, Integer ncid,int alertId) {
		logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---accociateNewsLetterGroupSelection() initiated for :: alertid ::"+ alertId +" ] ");
		try{
			int groupId=getDafaultGroup(ncid);
			Connection tempConnection=getConnection();
			String query="insert into newslettergroupselection values(null,"+alertId+","+userId+","+ncid+","+groupId+");";
			Statement stmt=tempConnection.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---accociateNewsLetterGroupSelection() completed for :: alertid ::"+ alertId +" ] ");
			return true;
		}catch (Exception e) {
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---accociateNewsLetterGroupSelection() EXCEPTION!!!  ] ");
			e.printStackTrace();
		}
		logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper ---accociateNewsLetterGroupSelection() completed for :: alertid ::"+ alertId +" ] ");
		return false;
	}
	
	public UserNewsletterAlertConfig getMergedAlertForUser(int userId, int ncid){
		try{
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- fetching merged alert for USER ::+"+userId+" and for NCID ::"+ncid+" getMergedAlertForUser()  initiated ] ");
			UserNewsletterAlertConfig config=null;
			Connection conn = getConnection();
			String query="select distinct u.* from usernewsletteralert u,newslettergroupselection n where u.isSingle=1 and n.newscenterId="+ncid+" and n.userId="+userId+" and u.aid=n.alertId";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			while(rs.next()){
				int alertId = rs.getInt("aId");
				String alertName=rs.getString("alertName");
				String frequency=rs.getString("frequency");
				//String prefDevice=rs.getString("preferenceDevice");
				int isSingle = rs.getInt("isSingle");
				String letterFormat=rs.getString("letterFormat");
				String timeZone=rs.getString("timeZone");
				
				config=new UserNewsletterAlertConfig();
				config.setAlertId(alertId);
				config.setAlertName(alertName);
				config.setFrequency(frequency);
				//config.setPrefDevice(prefDevice);
				config.setIsSingle(isSingle);
				config.setLetterFormat(letterFormat);
				config.setTimeZone(timeZone);
			}
			rs.close();
			stmt .close();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- fetching merged alert for USER ::+"+userId+" and for NCID ::"+ncid+" getMergedAlertForUser()  completed ] ");
			return config;
		}catch(Exception ex){
			ex.printStackTrace();
			logger.log(Level.INFO,"[UserNewsletterAlertConfigHelper --- fetching merged alert for USER ::+"+userId+" and for NCID ::"+ncid+" getMergedAlertForUser() EXCEPTION!!! "+ex.getMessage());
			return null;
		}
	}
	
}
