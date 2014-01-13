package com.lighthouse.report.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.report.client.domain.ReportItemList;
import com.newscenter.client.news.NewsItemList;

@RemoteServiceRelativePath("reportservicetoken")
public interface ReportsService extends RemoteService{
	
	public ReportItemList getAllReports();

	public void reportDownload(int reportId);
	
	public NewsItemList getReport(int reportId);
	
	public ReportItemList getAllReports(Group group,int newsmode,GroupPageCriteria criteria);
}
