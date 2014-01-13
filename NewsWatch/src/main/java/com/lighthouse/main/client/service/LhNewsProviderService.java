package com.lighthouse.main.client.service;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.login.user.client.domain.LhUser;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsProviderService;
import com.newscenter.client.tags.TagItem;

/**
 * 
 * @author prachi
 *
 */
@RemoteServiceRelativePath("lhNewsProviderService")
public interface LhNewsProviderService extends NewsProviderService{
	
	public NewsItemList getAndPage(GroupPageCriteria criteria, int newsmode);
	
	public NewsItemList getPage(GroupPageCriteria criteria, int newsmode);
    
	public NewsItemList getAllNewsforTag(TagItem tagitem, GroupPageCriteria criteria);
	
	public  NewsItemList getGroupedNewsList(GroupPageCriteria criteria,int newsmode);
	
	LhUser getUser(int uid,int nid);
}
