package com.lighthouse.search.client.service;


import java.util.HashMap;
import java.util.HashSet;




import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.lighthouse.search.client.domain.DateRange;
import com.lighthouse.search.client.domain.SearchResultList;
import com.lighthouse.search.exception.DbException;
import com.lighthouse.search.exception.SearchException;
import com.lighthouse.search.exception.SearchHelperException;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsItemList;



/**@author mahesh@ensarm.com*/
@RemoteServiceRelativePath("searchtoken")
public interface SearchService extends RemoteService{
	
	/**This method would return the suggestion list depending upon the appropriate inputs
	 * @param : String keyword- string for which suggestion is to be provided.
	 * 			DateRange object - dates for which suggestion is to be provided.
	 * @return : HashSet<String> object : containing the suggestion list
	 * */
	public HashSet<String>  getSearchSuggestList(String keyword ,DateRange range) throws SearchException, SearchHelperException, DbException; 

	/**This method would return the dynamic suggestion list depending upon the appropriate inputs
	 * @param : String keyword- string for which suggestion is to be provided.
	 * 			DateRange object - dates for which suggestion is to be provided.
	 * @return : Suggestion.Response : response containing dynamic suggestion list
	 * */
	public SuggestOracle.Response  getSearchSuggestList(SuggestOracle.Request req) throws SearchException, SearchHelperException, DbException; 
	

	/**This method would return the search result list depending upon the appropriate inputs
	 * @param : String keyword- string for which search result list is to be provided.
	 * 			DateRange object - dates for which search result list is to be provided.
	 * @return : SearchResultList object : containing the search result list
	 * */
	public SearchResultList getSearchResultList(String keyword, String tag ,DateRange range) throws SearchException;


	public SearchResultList getPage(PageCriteria criteria, int newsmode);
	
	
	public HashMap<String, Long> getTagCloud(String keyword,DateRange range);

	
	
}
