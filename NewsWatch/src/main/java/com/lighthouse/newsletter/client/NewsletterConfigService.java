package com.lighthouse.newsletter.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;


@RemoteServiceRelativePath("newsletterConfigService")
public interface NewsletterConfigService extends RemoteService {
	
	boolean saveNewsletterHeaderConfig(String xml);

	public String getNewsletterHeaderConfig();

	boolean saveNewsletterFooterConfig(String xml);

	String getNewsletterFooterConfig();

	boolean saveNewsletterOutlineConfig(String xml);

	String getNewsletterOutlineConfig();

	String getNewsletterContentConfig();

	boolean saveNewsletterContentConfig(String xml);

	String getImageUrl(int industryId, String string, boolean checkIfFileExists);





}
