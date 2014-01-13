package com.lighthouse.search.server.helper;

import static org.junit.Assert.*;




import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.Test;
import com.lighthouse.search.client.domain.DateRange;
import com.lighthouse.search.client.domain.SearchResultList;
import com.lighthouse.search.exception.SearchException;

/** @author mahesh@ensarm.com */
public class SearchHelperTest {


	@Test
	public void testGetSearchResultListHelper() throws SearchException {

		SearchHelper helper = new SearchHelper();
		SearchResultList resultList = new SearchResultList();
		/*resultList= helper
				.getSearchResultListHelper("Siemens", null,
						"/Projects/helios/lighthouse/NewsCenter/war/searchindexes/Hearing Aid");
		assertTrue(!resultList.isEmpty());
		resultList.clear();

		resultList = helper
				.getSearchResultListHelper("Rodsand ll:siemens", null,
						"/lighthouse/searchindexes/Solar PV");
		assertTrue(!resultList.isEmpty());
		resultList.clear();

/*		resultList = helper
				.getSearchResultListHelper("Siem*", null,
						"/Projects/helios/lighthouse/NewsCenter/war/searchindexes/Hearing Aid");
		assertTrue(!resultList.isEmpty());
		resultList.clear();

		java.util.Date date = new java.util.Date();
		Date toDate = new Date(date.getTime());

		Date startDate = new Date(date.getTime());
		DateRange range = new DateRange(startDate, toDate);
		resultList = helper
				.getSearchResultListHelper(null, range,
						"/Projects/helios/lighthouse/NewsCenter/war/searchindexes/Hearing Aid");
		assertTrue(resultList.isEmpty());
		resultList.clear();
*/	}
/*
	@Test
	public void testGetAllSearchedByUser() {

		SearchHelper helper = new SearchHelper();
		ArrayList mostSearched = helper.getAllSearchedByUser(158);
		assertTrue(!mostSearched.isEmpty());

	}

	@Test
	public void testAddToSearchHistory() {
		int count = 2;
		String itemSearched = "Solar Thermal";
		int userid = 158;
		java.util.Date date = new java.util.Date();
		Date commdate = new Date(date.getTime());
		int industryId = 4;

		SearchHelper helper = new SearchHelper();
		boolean result = helper.addToSearchHistory(commdate, count,
				itemSearched, userid, industryId);
		assertTrue(result == true);
	}

	@Test
	public void testDeleteByUserId() {
		int userid = 158;
		SearchHelper helper = new SearchHelper();
		boolean result = helper.deleteByUserId(userid);
		assertTrue(result == true);
	}

	@Test
	public void testUpdateCount() {
		int userid = 158;
		String itemSearched = "Solar Thermal";
		SearchHelper helper = new SearchHelper();

		java.util.Date date = new java.util.Date();
		Date commdate = new Date(date.getTime());

		boolean result = helper.updateCount(userid, commdate, itemSearched);
		assertTrue(result == true);
	}
*/
	@Test
	public void testGetTagCloudList(){
	/*	String itemSearched = "Siemens";
		SearchHelper helper = new SearchHelper();
		
		HashMap<String, Long> tempMap = helper.getTagCloudList(itemSearched);
        Iterator it = tempMap.keySet().iterator();  
        while(it.hasNext()){  
            String key = (String) it.next();  
            Long i = tempMap.get(key);        
                        //To display the information  
                           System.out.println("TagName: " +key);
                           System.out.println("Value:  "+Long.toString(i));
                           System.out.println();
        }  
		assertTrue(!tempMap.isEmpty());
	*/	
	}
	
}
