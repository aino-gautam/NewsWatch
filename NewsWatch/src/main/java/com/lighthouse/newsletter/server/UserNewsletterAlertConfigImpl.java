package com.lighthouse.newsletter.server;

import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.lighthouse.newsletter.client.UserNewsletterAlertConfigService;
import com.lighthouse.newsletter.client.domain.NewsletterGroupSelectionConfig;
import com.lighthouse.newsletter.client.domain.UserNewsletterAlertConfig;
import com.lighthouse.newsletter.client.exception.NewsletterconfigException;
import com.lighthouse.utils.server.EncrypterDecrypter;
import com.login.client.UserInformation;


/**
 * 
 * @author pritam@ensarm.com
 *
 */


/**
 * This is impl class of usernewsletterconfigImpl 
 */
public class UserNewsletterAlertConfigImpl extends RemoteServiceServlet implements UserNewsletterAlertConfigService {

	private static final long serialVersionUID = -6163927202487310879L;
	Logger logger=Logger.getLogger(UserNewsletterAlertConfigImpl.class.getName());
	
	
	
	/** This method is used to create configuration for newsletter alerts
	 */
	@Override
	public boolean createUserNewsletterAlert(UserNewsletterAlertConfig config,NewsletterGroupSelectionConfig groupConfig)throws NewsletterconfigException {
		try{
			UserNewsletterAlertConfigHelper alertHelper=new UserNewsletterAlertConfigHelper();
			boolean isCreated=alertHelper.createUserNewsletterAlert(config,groupConfig);
			alertHelper.closeConnection();
			return isCreated;
		}
		catch(Exception ex){
			logger.log(Level.INFO,"couldn't create alert details"+ex);
			return false;
		}
	}

	@Override
	public UserNewsletterAlertConfig createUserNewsletterAlert(UserNewsletterAlertConfig config, ArrayList<NewsletterGroupSelectionConfig> groupSelectionsList,int userId,int newscenterId) {
		try{
			UserNewsletterAlertConfigHelper alertHelper=new UserNewsletterAlertConfigHelper();
			HttpServletRequest req=this.getThreadLocalRequest();
			HttpSession session=req.getSession(false);
			//if(userId==-1&&newscenterId==-1){
				UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			
				userId = userInformation.getUserId();
				newscenterId=userInformation.getUserSelectedIndustryID();
			//}
			config=alertHelper.createUserNewsletterAlert(config,groupSelectionsList,userId,newscenterId);
			alertHelper.closeConnection();
			return config;
		}
		catch(Exception ex){
			logger.log(Level.INFO,"couldn't create alert details"+ex);
			return config;
		}
	}

	@Override
	public boolean updateUserNewsletterAlert(UserNewsletterAlertConfig config, ArrayList<NewsletterGroupSelectionConfig> groupSelectionsList) {
		try{
			UserNewsletterAlertConfigHelper alertHelper=new UserNewsletterAlertConfigHelper();
			boolean isUpdated=alertHelper.updateUserNewsletterAlert(config,groupSelectionsList);
			alertHelper.closeConnection();
			return isUpdated;
		}
		catch(Exception ex){
			logger.log(Level.INFO,"couldn't update alert details"+ex);
			return false;
		}
	}
	
	
	/**
	 * This method used to update the configuration of newsletter alert
	 */
	@Override
	public boolean updateUserNewsletterAlert(String frequency, String preference,int alertId)throws NewsletterconfigException {
		
		try{
			UserNewsletterAlertConfigHelper alertHelper=new UserNewsletterAlertConfigHelper();
			boolean isUpdated=alertHelper.updateUserNewsletterAlert(frequency, preference,alertId);
			alertHelper.closeConnection();
			return isUpdated;
		}
		catch(Exception ex){
			logger.log(Level.INFO,"couldn't update alert details"+ex);
			return false;
		}
	}

	
	/**
	 * This method is used to keep the information about which alert is associated with which user,newscenter etc...
	 * 
	 */
	@Override
	public boolean createNewsletterGroupSelection(int alertId,NewsletterGroupSelectionConfig config) throws NewsletterconfigException {
		try{
			UserNewsletterAlertConfigHelper alertHelper=new UserNewsletterAlertConfigHelper();
			config.setAlertId(alertId);
			boolean isCreated=alertHelper.createNewsletterGroupSelection(config);
			alertHelper.closeConnection();
			return isCreated;
		}
		catch(Exception ex){
			logger.log(Level.INFO,"couldn't insert details"+ex);
			return false;
		}
	}

	/**
	 * This method is used to update the information about which alert is associated with which user,newscenter etc... 
	 */
	@Override
	public boolean updateNewsletterGroupSelection(int ncid, int grpId,int nlgsId) throws NewsletterconfigException{
		try{
			UserNewsletterAlertConfigHelper alertHelper=new UserNewsletterAlertConfigHelper();
			boolean isUpdated=alertHelper.updateNewsletterGroupSelection(ncid, grpId,nlgsId);
			alertHelper.closeConnection();
			return isUpdated;
		   }
		catch(Exception ex){
			logger.log(Level.INFO,"couldn't update group selection details"+ex);
			return false;
		}
		
	}

	
	/**
	 * This overrided method is used to get all alerts related to particular user
	 */
	@Override
	public ArrayList<UserNewsletterAlertConfig> getAllUserAlertList(int userId, int ncid) throws NewsletterconfigException {
		try{
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			UserInformation user = (UserInformation)session.getAttribute("userInfo");
			if(userId == -1)
				userId = user.getUserId();
			if(ncid == -1)
				ncid = user.getUserSelectedNewsCenterID();
			
			UserNewsletterAlertConfigHelper alertHelper=new UserNewsletterAlertConfigHelper();
			ArrayList<UserNewsletterAlertConfig> list=alertHelper.getAllUserAlertList(userId, ncid);
			alertHelper.closeConnection();
			return list;
		}
		catch(Exception ex){
			logger.log(Level.INFO,"couldn't get alert details"+ex);
			return null;
		}
		
	}

	
	/**
	 * This overrided method is used to delete the alert 
	 */
	@Override
	public boolean deleteAlert(int alertId) throws NewsletterconfigException {
		try{
			UserNewsletterAlertConfigHelper alertHelper=new UserNewsletterAlertConfigHelper();
			boolean isDeleted=alertHelper.deleteAlert(alertId);
			alertHelper.closeConnection();
			return isDeleted;
		}
		catch(Exception ex){
			logger.log(Level.INFO,"couldn't delete alert details"+ex);
			return false;
		}
		
	}

	@Override
	public String generateRssLinkForAlert(int alertId) {
	
		String encryptedUrlQuery=null;
		try {
				EncrypterDecrypter encrypterDecrypter = EncrypterDecrypter.getInstance();
				HttpServletRequest req = this.getThreadLocalRequest();
				HttpSession session = req.getSession(false);
				UserInformation user = (UserInformation) session.getAttribute("userInfo");
				Integer userId = user.getUserId();
				Integer ncid = user.getUserSelectedNewsCenterID();
				String urlQuery="user="+userId+"&nc="+ncid+"&alert="+alertId;
				encryptedUrlQuery=encrypterDecrypter.getUrlEncodedString(urlQuery);
				encryptedUrlQuery=encrypterDecrypter.getEncryptedString(encryptedUrlQuery);
				
			} catch (Exception e) {
			e.printStackTrace();
		}

		return encryptedUrlQuery;
	}
	
	
	public void saveNewsletterTemplate(String newsletterHeader, String newsletterFooter, int newscenterid) {
		LhNewsletterHelper helper=new LhNewsletterHelper();
		helper.saveNewsletterTemplate(newsletterHeader,newsletterFooter,newscenterid);
		helper.closeConnection();
	}

	

	@Override
	public boolean saveNewsletterDelivery(int newscenterid, String time) {
		// commented to disable automated delivery time update.
		
		LhNewsletterHelper helper=new LhNewsletterHelper();
		boolean value=helper.saveNewsletterDelivery(newscenterid,time);
		if(value){
			try{
			HttpServletRequest req= this.getThreadLocalRequest();
			StringBuffer reqURL=req.getRequestURL();
			String reqHost=(String)reqURL.subSequence(0,reqURL.length()-(req.getServletPath().length()));
			final String url=reqHost+"/scheduleNewsLetter?ncid="+newscenterid;
			
			logger.log(Level.INFO,"In saveNewsletterDelivery -> URL:"+url);
			
			GetMethod get = new  GetMethod(url);
			get.setDoAuthentication(true);
			HttpClient httpClient=new HttpClient();
			httpClient.executeMethod(get);
			get.releaseConnection();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		helper.closeConnection();
		return value;
		
		
	}

	@Override
	public ArrayList<String> getNewsletterTemplate(int newscenterid) {
		LhNewsletterHelper helper=new LhNewsletterHelper();
		ArrayList<String> value = helper.getNewsletterTemplate(newscenterid);
		helper.closeConnection();
		return value;
		
	}

	@Override
	public boolean createAlertAndAssociateDefaultGroup(UserNewsletterAlertConfig config) {
		
		try{
			UserNewsletterAlertConfigHelper alertHelper=new UserNewsletterAlertConfigHelper();
			
			HttpServletRequest req = this.getThreadLocalRequest();
			HttpSession session = req.getSession(false);
			UserInformation user = (UserInformation) session.getAttribute("userInfo");
			Integer userId = user.getUserId();
			Integer ncid = user.getUserSelectedNewsCenterID();
			int alertId=alertHelper.createAlert(config, userId, ncid);
			if(alertId==0){
				alertHelper.closeConnection();
				return false;
			}
			boolean result=alertHelper.accociateNewsLetterGroupSelection(userId,ncid,alertId);
			alertHelper.closeConnection();
			return result;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}

	


	
}
