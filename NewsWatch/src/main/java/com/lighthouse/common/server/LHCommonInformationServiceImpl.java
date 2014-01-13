package com.lighthouse.common.server;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.common.client.PageResult;
import com.common.server.CommonInformationServiceImpl;
import com.lighthouse.common.client.LHCommonInformationService;
import com.lighthouse.common.server.db.LHUserHelperForCommon;
import com.newscenter.client.criteria.PageCriteria;

public class LHCommonInformationServiceImpl extends
		CommonInformationServiceImpl implements LHCommonInformationService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public PageResult getNewsLetterAccessStats(PageCriteria crt, int userid,
			int industryid) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		LHUserHelperForCommon helper = new LHUserHelperForCommon(req, res,connectionUrl, driverClassName, username, password);
		PageResult pageresult = helper.getNewsLetterAccessStats(crt, userid,industryid);
		helper.closeConnection();
		return pageresult;
	}

	@Override
	public PageResult getSortedNewsLetterAccessStats(PageCriteria crt,
			int userid, int industryid, String columnname, String mode) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		LHUserHelperForCommon helper = new LHUserHelperForCommon(req, res,connectionUrl, driverClassName, username, password);
		PageResult pageresult = helper.getSortedNewsLetterAccessStats(crt,userid, industryid, columnname, mode);
		helper.closeConnection();
		return pageresult;
	}

	@Override
	public PageResult getSearchNewsLetterAccessStatistics(PageCriteria crt,
			int userid, int industryId, String columnName, String searchString) {
		HttpServletResponse res = this.getThreadLocalResponse();
		HttpServletRequest req = this.getThreadLocalRequest();
		LHUserHelperForCommon helper = new LHUserHelperForCommon(req,res,connectionUrl,driverClassName,username,password);
		PageResult list = helper.getSearchNewsLetterAccessStatistics(crt,userid,industryId,columnName,searchString);
		helper.closeConnection();
		return list;
	}
}
