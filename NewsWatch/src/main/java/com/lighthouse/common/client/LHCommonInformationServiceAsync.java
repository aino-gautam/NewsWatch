package com.lighthouse.common.client;

import com.common.client.CommonInformationServiceAsync;
import com.common.client.PageResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.newscenter.client.criteria.PageCriteria;

public interface LHCommonInformationServiceAsync extends
		CommonInformationServiceAsync {
	void getNewsLetterAccessStats(PageCriteria crt, int userid, int industryid,
			AsyncCallback<PageResult> callback);

	void getSortedNewsLetterAccessStats(PageCriteria crt, int userid,
			int industryid, String columnname, String mode,
			AsyncCallback<PageResult> callback);

	void getSearchNewsLetterAccessStatistics(PageCriteria crt, int userid,
			int industryId, String columnName, String searchString,
			AsyncCallback<PageResult> callback);

}
