package com.lighthouse.search.client.service;

import java.util.HashMap;
import java.util.HashSet;


import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.lighthouse.search.client.domain.DateRange;
import com.lighthouse.search.client.domain.SearchResultList;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsItemList;

public interface SearchServiceAsync {

	void getSearchResultList(String keyword, String tag, DateRange range,
			AsyncCallback<SearchResultList> callback);

	void getSearchSuggestList(String keyword, DateRange range,
			AsyncCallback<HashSet<String>> callback);

	void getSearchSuggestList(Request req, AsyncCallback<Response> callback);

	void getPage(PageCriteria criteria, int newsmode,
			AsyncCallback<SearchResultList> callback);

	void getTagCloud(String keyword, DateRange range,
			AsyncCallback<HashMap<String, Long>> callback);


}
