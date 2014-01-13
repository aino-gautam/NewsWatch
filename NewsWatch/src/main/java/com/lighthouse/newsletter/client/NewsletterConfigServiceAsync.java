package com.lighthouse.newsletter.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface NewsletterConfigServiceAsync {

	void saveNewsletterHeaderConfig(String xml,
			AsyncCallback<Boolean> callback);

	void getNewsletterHeaderConfig(AsyncCallback<String> callback);

	void saveNewsletterFooterConfig(String xml, AsyncCallback<Boolean> callback);

	void getNewsletterFooterConfig(AsyncCallback<String> callback);

	void saveNewsletterOutlineConfig(String xml, AsyncCallback<Boolean> callback);

	void getNewsletterOutlineConfig(AsyncCallback<String> callback);

	void getNewsletterContentConfig(AsyncCallback<String> callback);

	void saveNewsletterContentConfig(String xml, AsyncCallback<Boolean> callback);

	void getImageUrl(int industryId, String string, boolean checkIfFileExists,
			AsyncCallback<String> asyncCallback);

}
