package com.lighthouse.main.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.login.user.client.domain.LhUser;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsProviderServiceAsync;
import com.newscenter.client.tags.TagItem;

/**
 * 
 * @author prachi
 *
 */
public interface LhNewsProviderServiceAsync extends NewsProviderServiceAsync{
    
	
	
	void getPage(GroupPageCriteria criteria, int newsmode,AsyncCallback<NewsItemList> callback);

	void getAndPage(GroupPageCriteria criteria, int newsmode,AsyncCallback<NewsItemList> callback);

	void getAllNewsforTag(TagItem tagitem, GroupPageCriteria criteria,AsyncCallback<NewsItemList> callback);

	void getGroupedNewsList(GroupPageCriteria criteria, int newsmode,
			AsyncCallback<NewsItemList> callback);

	void getUser(int uid, int nid, AsyncCallback<LhUser> callback);
}
