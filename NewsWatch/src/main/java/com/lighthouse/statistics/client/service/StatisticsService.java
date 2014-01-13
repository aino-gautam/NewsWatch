package com.lighthouse.statistics.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.statistics.client.domain.NewsStatistics;
import com.newscenter.client.news.NewsItems;

@RemoteServiceRelativePath("statisticsService") 
public interface StatisticsService extends RemoteService{
	
	NewsStatistics getNewsStatisticsData(Group group);
	
	NewsStatistics getRefreshNewsStatisticsData(Group group,int newsmode);
	
	public List<NewsItems> getFavoriteItems(Group group, boolean refreshed);

}
