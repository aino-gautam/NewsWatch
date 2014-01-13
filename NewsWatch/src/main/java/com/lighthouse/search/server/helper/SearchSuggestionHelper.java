package com.lighthouse.search.server.helper;

import java.sql.Connection;



import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Request;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.lighthouse.search.client.domain.DateRange;
import com.lighthouse.search.exception.DbException;
import com.lighthouse.search.exception.SearchHelperException;

import com.lighthouse.search.client.ui.ItemSuggestion;
import com.newscenter.server.db.DBHelper;

/**
 * @author mahesh@ensarm.com*/
public class SearchSuggestionHelper extends DBHelper {
	
	Logger logger = Logger.getLogger(SearchSuggestionHelper.class.getName());
	
	/**This method will returns the suggestion list depending the appropriate inputs provided.
	 * @param ; String keyword - keyword for which suggestion list is to be provided
	 * DateRange object - the object for which the suggestion list is to be provided
	 * 
	 * @return : HashSet<String> object - it contains the suggestion list to be returned.*/
	public HashSet<String> getSearchSuggestListHelper(String keyword,
			DateRange range) throws SearchHelperException, DbException {
		logger.log(Level.INFO, "[SearchSuggestionHelper --- getSearchSuggestListHelper() initiated]");
		HashSet<String> suggestedList = new HashSet<String>();

		if (keyword != null && range != null) {
			suggestedList = getTimeBoundTagSearchSuggestion(keyword, range);
			return suggestedList;
		}

		else if (range != null && keyword == null) {
			suggestedList = getTimeBoundSearchSuggestions(range);
			return suggestedList;
		}

		else if (keyword != null && range == null) {

			if (keyword.contains(":")) {
				suggestedList = getTagSearchSuggestions(keyword);
				return suggestedList;
			}

			else {
				suggestedList = getPlainSearchSuggestions(keyword);
				return suggestedList;
			}
		}
		logger.log(Level.INFO, "[SearchSuggestionHelper --- getSearchSuggestListHelper() completed]");
		return suggestedList;
	}
	
	/**This method will returns the suggestion list depending the appropriate inputs provided.
	 * @param ; String keyword - keyword for which suggestion list is to be provided
	 * DateRange object - the object for which the suggestion list is to be provided
	 * 
	 * @return :  Suggestion.Response : response containing dynamic suggestion list.*/
	public Response getSuggectionResopnse(Request req, int lightHouseId) {
		logger.log(Level.INFO, "[SearchSuggestionHelper --- getSuggectionResopnse() initiated] : industryId");
		 SuggestOracle.Response resp = new SuggestOracle.Response();
	     List suggestions = new ArrayList();
	     Connection con = getConnection();
		 Statement smt;
			try {
				smt = con.createStatement();
				String val = req.getQuery();
				
				ResultSet rs = smt.executeQuery("SELECT DISTINCT itemSearched FROM searchHistory WHERE itemSearched LIKE '%"+val+"%' and searchCount>0 and industryId="+lightHouseId);
				
				//ResultSet rs = smt.executeQuery("SELECT itemSearched FROM searchHistory WHERE timeOfAccess= (Select max(timeOfAccess) from searchHistory where itemSearched LIKE '%"+val+"%' and searchCount>0 )");
				
				while (rs.next()) {
					if(suggestions.size()!=7){
					suggestions.add(new ItemSuggestion(rs.getString("itemSearched")));
				}
				}
				rs.close();
				smt.close();

			} catch (Exception e) {
			   	logger.log(Level.INFO, "[SearchSuggestionHelper --- getSuggectionResopnse() error] trace: "+e);
				e.printStackTrace();
			}

	       resp.setSuggestions(suggestions);
	       logger.log(Level.INFO, "[SearchSuggestionHelper --- getSuggectionResopnse() completed] : industryId");
	       return resp;	
	       }
	

	/**This method will returns the suggestion list depending the appropriate inputs provided.
	 * @param ; String keyword - keyword for which suggestion list is to be provided
	 * DateRange object - the object for which the suggestion list is to be provided
	 * 
	 * @return : HashSet<String> object - it contains the suggestion list to be returned.*/
	private HashSet<String> getTimeBoundTagSearchSuggestion(String keyword,
			DateRange range) {
		HashSet<String> suggestedList = new HashSet<String>();
		return suggestedList;
	}

	/**This method will returns the suggestion list depending the appropriate inputs provided.
	 * @param ; String keyword - keyword for which suggestion list is to be provided 
	 * @return : HashSet<String> object - it contains the suggestion list to be returned.*/
	private HashSet<String> getPlainSearchSuggestions(String keyword){
		logger.log(Level.INFO, "[SearchSuggestionHelper --- getPlainSearchSuggestions() initiated] : keyword"+keyword);
		HashSet<String> suggestedList = new HashSet<String>();
		Connection conn= getConnection();
		try{
		String sql = "SELECT itemSearched FROM searchHistory WHERE itemSearched LIKE '%"+keyword+"%'";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			suggestedList.add(rs.getString("itemSearched"));
		}
		rs.close();
		stmt.close();
		}
		catch (Exception e) {
			logger.log(Level.INFO, "[SearchSuggestionHelper --- getPlainSearchSuggestions() Error] : Trace"+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[SearchSuggestionHelper --- getPlainSearchSuggestions() Completed] : keyword"+keyword);
		return suggestedList;

	}
	/**This method will returns the suggestion list depending the appropriate inputs provided.
	 * @param : String keyword - keyword for which suggestion list is to be provided
	 * @return : HashSet<String> object - it contains the suggestion list to be returned.*/
	private HashSet<String> getTagSearchSuggestions(String keyword) {
		logger.log(Level.INFO, "[SearchSuggestionHelper --- getTagSearchSuggestions() initiated] : keyword"+keyword);
		HashSet<String> suggestedList = new HashSet<String>();
		Connection conn= getConnection();
		try{
		String sql = "SELECT itemSearched FROM searchHistory WHERE itemSearched LIKE '%"+keyword+"%'";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {
			suggestedList.add(rs.getString("itemSearched"));
		}
		rs.close();
		stmt.close();
		}
		catch (Exception e) {
			logger.log(Level.INFO, "[SearchSuggestionHelper --- getPlainSearchSuggestions() Error] : Trace"+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "[SearchSuggestionHelper --- getPlainSearchSuggestions() Completed] : keyword"+keyword);
		return suggestedList;
	}

	/**This method will returns the suggestion list depending the appropriate inputs provided.
	 * @param : DateRange object - the object for which the suggestion list is to be provided
	 * @return : HashSet<String> object - it contains the suggestion list to be returned.*/
	private HashSet<String> getTimeBoundSearchSuggestions(DateRange range) {
		HashSet<String> suggestedList = new HashSet<String>();
		return suggestedList;

	}
}
