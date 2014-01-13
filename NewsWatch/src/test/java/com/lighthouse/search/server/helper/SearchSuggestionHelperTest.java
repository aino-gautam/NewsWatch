package com.lighthouse.search.server.helper;

import static org.junit.Assert.*;




import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lighthouse.search.exception.DbException;
import com.lighthouse.search.exception.SearchHelperException;
import com.newscenter.server.db.DBHelper;

/**@author mahesh@ensarm.com*/
public class SearchSuggestionHelperTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetSearchSuggestListHelper() throws SearchHelperException, DbException {
		DBHelper helpme = new DBHelper(
				"jdbc:mysql://10.10.10.125:3306/lighthouse",
				"com.mysql.jdbc.Driver", "root", "mysql", "");
		SearchSuggestionHelper helper = new SearchSuggestionHelper();
		java.util.HashSet<String> templist = helper.getSearchSuggestListHelper("Solar", null);
		assertTrue(!templist.isEmpty());
	}

}
