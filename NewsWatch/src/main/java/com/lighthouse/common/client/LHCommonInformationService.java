package com.lighthouse.common.client;

import com.common.client.CommonInformationService;
import com.common.client.PageResult;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.newscenter.client.criteria.PageCriteria;

@RemoteServiceRelativePath("lhCommonInformationService")

public interface LHCommonInformationService extends CommonInformationService {

	PageResult getNewsLetterAccessStats(PageCriteria crt, int userid,
			int industryid);

	PageResult getSortedNewsLetterAccessStats(PageCriteria crt, int userid,
			int industryid, String columnname, String mode);

	PageResult getSearchNewsLetterAccessStatistics(PageCriteria crt,
			int userid, int industryId, String columnName, String searchString);
}
