package com.lighthouse.newsletter.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.lighthouse.newsletter.client.domain.NewsletterGroupSelectionConfig;
import com.lighthouse.newsletter.client.domain.UserNewsletterAlertConfig;
import com.newscenter.client.news.NewsItems;


@RemoteServiceRelativePath("userAlertService")
public interface UserNewsletterAlertConfigService extends RemoteService {

	boolean createUserNewsletterAlert(UserNewsletterAlertConfig config,
			NewsletterGroupSelectionConfig groupSelection);
			
	UserNewsletterAlertConfig createUserNewsletterAlert(UserNewsletterAlertConfig config,
			ArrayList<NewsletterGroupSelectionConfig> groupSelectionsList,
			int userId, int newscenterId);
	
	boolean updateUserNewsletterAlert(String frequency, String preference,
			int alertId);

	boolean updateUserNewsletterAlert(UserNewsletterAlertConfig config, ArrayList<NewsletterGroupSelectionConfig> groupSelectionsList);
	
	boolean deleteAlert(int alertId); 


	boolean createNewsletterGroupSelection(int alertId,NewsletterGroupSelectionConfig config);


	boolean updateNewsletterGroupSelection(int ncid, int grpId, int nlgsId);

	ArrayList<UserNewsletterAlertConfig> getAllUserAlertList(int userId, int ncid);

	String generateRssLinkForAlert(int alertId); 
	
	void saveNewsletterTemplate(String newsletterHeader,String newsletterFooter, int newscenterid);

	public boolean saveNewsletterDelivery(int newscenterid, String text);

	public ArrayList<String> getNewsletterTemplate( int newscenterid);

	boolean createAlertAndAssociateDefaultGroup(UserNewsletterAlertConfig config);
	
}
