/**
 * 
 */
package com.lighthouse.search.server.helper;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.apache.lucene.document.Field;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.admin.client.NewsItemsAdminInformation;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.db.DBHelper;

/**
 * @author mahesh@ensarm.com
 * 
 */
public class SetupIndexHelperTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.search.server.helper.MasterSetup#masterIndexing(java.sql.Connection)}
	 * .
	 */
/*	@Test
	public void testMasterIndexing() {
		DBHelper helpme = new DBHelper(
				"jdbc:mysql://10.10.10.125:3306/lighthouse",
				"com.mysql.jdbc.Driver", "root", "mysql", "");
		SetupIndexHelper helper = new SetupIndexHelper();
		helper.indexSetup("/lighthouse/indexes/");
	}
*/

	@Test
	public void testAddRecordToIndex(){
		SetupIndexHelper helper = new SetupIndexHelper();
		NewsItemsAdminInformation newsitem = new NewsItemsAdminInformation();
		
		newsitem.setAbstractNews("testing for indexing");
		newsitem.setContent("testing for indexing");
		newsitem.setTagName("test indexing");
		newsitem.setNewsItemId(500000000);
		newsitem.setDate(new Date());
		newsitem.setUrl("www.test.com");
		newsitem.setNewsTitle("testing for indexing");
		newsitem.setSource("mahesh");
		ArrayList tenplist = new ArrayList();
		TagItem item = new TagItem();
		item.setTagId(60000);
		item.setTagName("mahesh testing");
		tenplist.add(item);
		newsitem.setArrayTagList(tenplist);
		

//		helper.addRecordToIndex(newsitem, "/lighthouse/searchindexes/Solar PV");		
	}

	@Test
	public void testdeleteRecordfromIndex(){
		SetupIndexHelper helper = new SetupIndexHelper();
		//helper.deleteRecordFromIndex(500000000,"/lighthouse/searchindexes/Solar PV");		
		
	}
}
