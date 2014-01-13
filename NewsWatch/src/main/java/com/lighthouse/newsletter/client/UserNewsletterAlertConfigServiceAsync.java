package com.lighthouse.newsletter.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lighthouse.newsletter.client.domain.NewsletterGroupSelectionConfig;
import com.lighthouse.newsletter.client.domain.UserNewsletterAlertConfig;


public interface UserNewsletterAlertConfigServiceAsync {

	void createUserNewsletterAlert(UserNewsletterAlertConfig config,
			NewsletterGroupSelectionConfig groupSelection, AsyncCallback<Boolean> callback);


	void updateUserNewsletterAlert(String frequency, String preference,
			int alertId, AsyncCallback<Boolean> callback);

	void deleteAlert(int alertId, AsyncCallback<Boolean> callback);

	void createNewsletterGroupSelection(int alertId,
			NewsletterGroupSelectionConfig config,
			AsyncCallback<Boolean> callback);

	void updateNewsletterGroupSelection(int ncid, int grpId, int nlgsId,
			AsyncCallback<Boolean> callback);

	void getAllUserAlertList(int userId, int ncid,
			AsyncCallback<ArrayList<UserNewsletterAlertConfig>> callback);


	void createUserNewsletterAlert(UserNewsletterAlertConfig config,
			ArrayList<NewsletterGroupSelectionConfig> groupSelectionsList,
			int userId, int newscenterId,
			AsyncCallback<UserNewsletterAlertConfig> callback);


	void updateUserNewsletterAlert(UserNewsletterAlertConfig config,ArrayList<NewsletterGroupSelectionConfig> groupSelectionsList,
			AsyncCallback<Boolean> callback);


	void generateRssLinkForAlert(int alertId, AsyncCallback<String> callback);


	void saveNewsletterTemplate(String newsletterHeader, String newsletterFooter, int newscenterid,AsyncCallback<Void> callback);
	
	void getNewsletterTemplate(int newscenterid,AsyncCallback<ArrayList<String>> callback);

	void saveNewsletterDelivery(int newscenterid, String text,AsyncCallback<Boolean> callback);

	void createAlertAndAssociateDefaultGroup(UserNewsletterAlertConfig config, AsyncCallback<Boolean> callback);



	

}
