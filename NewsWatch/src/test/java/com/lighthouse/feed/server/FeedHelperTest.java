package com.lighthouse.feed.server;

import static org.junit.Assert.*;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.junit.BeforeClass;
import org.junit.Test;

import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.db.DBHelper;

public class FeedHelperTest {

	public static FeedHelper feedHelper=null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
		DBHelper helper = new DBHelper(
				"jdbc:mysql://10.10.10.125:3306/lighthouse",
				"com.mysql.jdbc.Driver", "root", "mysql", "");
		FeedManager.CONFIGFILENAME="war/config/catalystConfig.xml";
		feedHelper=new FeedHelper(null);
	}

	//@Test
	public void testParseFeed() {
		fail("Not yet implemented");
	}

	//@Test
	public void testSaveNewsFeed() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTagItemId() {
		Long id=feedHelper.getTagItemId("Europe", 1);
		assertNotNull(id);
		assert(id==256);
	}

	@Test
	public void testAssociateNewsTagItem() {
		feedHelper.associateNewsTagItem(399L, 262L, 1);
	}
	

	@Test
	public void testRemoveNewsTagItemAssociation() {
		boolean result=feedHelper.removeNewsTagItemAssociation(399L, 262L, 1);
		assertTrue(result);
	}

	@Test
	public void testGetNewsItemforFeedSource() throws SQLException {
		String url="http://newswatch-mc.com/applications/monitor-feed/?seed=dbde1eed000881aea4595a7c85836318";
		NewsItemList newsList=feedHelper.getNewsItemforFeedSource(url, "EnFeed2", 1);
		assert(newsList.size()>0);
	}

	@Test
	public void testGetEditorialLastDeliveryTime() throws SQLException {
		Timestamp deliveryTime=feedHelper.getLastNewsLetterDelivery(1);
		assertNotNull(deliveryTime);
		assert(deliveryTime instanceof java.sql.Timestamp);
	}

//	@Test
	public void testSaveFeedStatistics() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIndustryFeedItems() {
		NewsItemList newsList=feedHelper.getIndustryFeedItems(1);
		assert(newsList.size()>0);
	}

	@Test
	public void testIsPrimaryTagPresent() {
		String tagArray[]={"Bayer AG","Biocon,Biogen","Chr. Hansen","Clariant","English","North America","Corporate Information"};
		//boolean result=feedHelper.isPrimaryTagPresent(tagArray, 1);
		//assertTrue(result);
	}

	@Test
	public void testMarkAsTopFeed() {
		TagItem result=feedHelper.markAsTopFeed(399L, 1, true);
		assertNotNull(result);
	}


	@Test
	public void testGetIndustryTopFeedNews() {
		
		NewsItemList newsList=feedHelper.getIndustryTopFeedNews(1);
		assert(newsList.size()>0);
	}

	//@Test
	public void testDeleteNewsFeed() {
		
		boolean n=feedHelper.deleteNewsFeed(21L);
		assertTrue(n);
		
	}
}
