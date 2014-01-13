package com.lighthouse.report.server;




import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.report.client.domain.ReportItemList;
import com.lighthouse.report.client.service.ReportsService;
import com.lighthouse.report.server.helper.ReportsHelper;
import com.login.client.UserInformation;
import com.newscenter.client.news.NewsItemList;

public class ReportsServiceImpl extends RemoteServiceServlet implements ReportsService{

	@Override
	public ReportItemList getAllReports() {
		ReportItemList reportItemList = new ReportItemList();
		
		ReportsHelper helper = new ReportsHelper();
		reportItemList = helper.getAllReportItem();
		helper.closeConnection();
		return reportItemList;
		
		
	}

	@Override
	public void reportDownload(int reportId) {
		
	}

	@Override
	public NewsItemList getReport(int reportId) {

		ReportsHelper helper = new ReportsHelper();
		
		NewsItemList itemlist = helper.getReportItem(reportId);
		helper.closeConnection();
		return itemlist;
	}

	@Override
	public ReportItemList getAllReports(Group group, int newsmode,
			GroupPageCriteria criteria) {
	ReportItemList reportItemList = new ReportItemList();
		
		ReportsHelper helper = new ReportsHelper();
		Long userId = (long) getUserInfoFromSession().getUserId();
		reportItemList = helper.getAllReportItem(group, newsmode, criteria,userId);
		
		helper.closeConnection();
		return reportItemList;
	}

	private UserInformation getUserInfoFromSession(){
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(false);
		
		
		UserInformation userInformation = (UserInformation) session
				.getAttribute("userInfo");
		
		return userInformation;
	}

}
