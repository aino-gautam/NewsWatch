package com.lighthouse.report.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.report.client.domain.ReportItemList;
import com.newscenter.client.news.NewsItemList;

public interface ReportsServiceAsync {

	void getAllReports(AsyncCallback<ReportItemList> callback);

	void reportDownload(int reportId, AsyncCallback<Void> callback);

	void getReport(int reportId, AsyncCallback<NewsItemList> callback);

	void getAllReports(Group group, int newsmode, GroupPageCriteria criteria,
			AsyncCallback<ReportItemList> callback);

}
