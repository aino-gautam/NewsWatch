package com.lighthouse.search.server;

import java.util.HashMap;
import java.util.HashSet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.lighthouse.search.client.domain.DateRange;
import com.lighthouse.search.client.domain.SearchResultList;
import com.lighthouse.search.client.service.SearchService;
import com.lighthouse.search.exception.DbException;
import com.lighthouse.search.exception.SearchException;
import com.lighthouse.search.exception.SearchHelperException;
import com.login.client.UserInformation;

import com.lighthouse.search.server.helper.SearchHelper;
import com.lighthouse.search.server.helper.SearchSuggestionHelper;
import com.newscenter.client.criteria.PageCriteria;


/**
 * @author mahesh@ensarm.com This class serve as the impl class for the
 *         SearchService interfaces and all async calls for search related
 *         tasks.
 * */
public class SearchServiceImpl extends RemoteServiceServlet implements
		SearchService {

	String dirPath = null;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		dirPath = config.getInitParameter("IndexDirPath");
	}

	@Override
	public HashSet<String> getSearchSuggestList(String keyword, DateRange range)
			throws SearchHelperException, DbException {
		HashSet<String> suggestionList = new HashSet<String>();
		
		SearchSuggestionHelper suggestionHelper = new SearchSuggestionHelper();
		suggestionList = suggestionHelper.getSearchSuggestListHelper(keyword,
				range);
		suggestionHelper.closeConnection();
		return suggestionList;
	}

	@Override
	public SearchResultList getSearchResultList(String keyword,String tag, DateRange range)
			throws SearchException {
		SearchResultList resultList = new SearchResultList();
		//String lightHouseName = getLightHouseName();
		//String lightHouseDir = dirPath+lightHouseName;
		
		//String lightHouseDir = "/lighthouse/searchindexes/Hearing Aid";
		//String lightHouseDir = "\\lighthouse\\searchindexes\\Hearinng Aid";
		
		UserInformation userinfo = getUserInfoFromSession();
		
		String lightHouseName = userinfo.getIndustryNewsCenterName();
		String lightHouseDir = dirPath + lightHouseName;
	//	 String lightHouseDir = "/lighthouse/searchindexes/Hearing Aid";
		SearchHelper helper = new SearchHelper();
		resultList = helper.getSearchResultListHelper(keyword,tag, range,
				lightHouseDir);
		if(keyword!=null){
			if(resultList.size()!=0){
				helper.addToSearchHistory(null, resultList.size(), keyword, userinfo.getUserId(), userinfo.getUserSelectedIndustryID());
			}
			else if(resultList.size()==0){
				helper.deleteByUserId(keyword);
			}
		}
		
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(false);
		session.setAttribute("searchResultList", resultList);

	//	HashMap<String, Long> tagCloudMap = helper.getTagCloudMap(resultList,keyword,range,lightHouseDir);
		PageCriteria pageCriteria = new PageCriteria();
		pageCriteria.setPageSize(15);
		pageCriteria.setStartIndex(0);
		SearchResultList tempResultList = helper.getPage(resultList,
				pageCriteria, 0);

//		tempResultList.setTagCloud(tagCloudMap);
	//	tempResultList.setTagCloud(helper.getTagCloudList(keyword,lightHouseDir));
		helper.closeConnection();
		return tempResultList;
	}

	/**
	 * This method will give the industry name from the session
	 * 
	 * @return : String lighthouse - lighthouse name from the session
	 
	private String getLightHouseName() {
		String lightHouseName = null;

		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(true);
		UserInformation userInformation = (UserInformation) session
				.getAttribute("userInfo");
		lightHouseName = userInformation.getIndustryNewsCenterName();
		return lightHouseName;
	}
	*/
	private UserInformation getUserInfoFromSession(){
		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(false);
		
		
		UserInformation userInformation = (UserInformation) session
				.getAttribute("userInfo");
		
		return userInformation;
	}

	@Override
	public Response getSearchSuggestList(Request req) throws SearchException,
			SearchHelperException, DbException {
		UserInformation userinfo = getUserInfoFromSession();
		int lightHouseId = userinfo.getUserSelectedIndustryID();
		
		
		SearchSuggestionHelper suggestionHelper = new SearchSuggestionHelper();
		SuggestOracle.Response resp = new SuggestOracle.Response();
		resp = suggestionHelper.getSuggectionResopnse(req,lightHouseId);
		
		suggestionHelper.closeConnection();
		return resp;
	}

	@Override
	public SearchResultList getPage(PageCriteria criteria, int newsmode) {

		HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(true);
		SearchResultList originalResultList = (SearchResultList) session
				.getAttribute("searchResultList");
		SearchHelper helper = new SearchHelper();
		SearchResultList resultList = helper.getPage(originalResultList,
				criteria, newsmode);
		helper.closeConnection();
		return resultList;

	}
	
	public HashMap<String, Long> getTagCloud(String keyword,DateRange range){
	
		UserInformation userinfo = getUserInfoFromSession();
		String lightHouseName = userinfo.getIndustryNewsCenterName();
		String lightHouseDir = dirPath + lightHouseName;
	/*	HttpServletRequest request = this.getThreadLocalRequest();
		HttpSession session = request.getSession(false);
		SearchResultList resultList = (SearchResultList) session.getAttribute("searchResultList");
		if(resultList.isEmpty()){*/
		//SearchResultList resultList = helper.getSearchResultListHelper(keyword, range, lightHouseDir);
		//}
	//	SearchResultList resultList = helper.getSearchResultListHelper(keyword, range, lightHouseDir);
	//	SearchResultList resultList = helper.getTagCloudResultList(keyword, range, lightHouseDir);
		SearchHelper helper = new SearchHelper();
		HashMap<String, Long> tagCloudMap = helper.getTagCloudMapNew(keyword,range,lightHouseDir);
		helper.closeConnection();
		return tagCloudMap;
	}

}
