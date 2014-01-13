package com.lighthouse.statistics.client.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.statistics.client.domain.NewsStatistics;
import com.newscenter.client.news.NewsItems;

public interface StatisticsServiceAsync {

	void getNewsStatisticsData(Group group,
			AsyncCallback<NewsStatistics> callback);

	void getRefreshNewsStatisticsData(Group group, int newsmode,
			AsyncCallback<NewsStatistics> callback);

	void getFavoriteItems(Group group, boolean refreshed,
			AsyncCallback<List<NewsItems>> callback);

	

}
