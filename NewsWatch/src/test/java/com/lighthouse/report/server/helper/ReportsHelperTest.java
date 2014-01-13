/**
 * 
 */
package com.lighthouse.report.server.helper;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Date;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.server.helper.GroupHelper;
import com.lighthouse.report.client.domain.ReportItem;
import com.lighthouse.report.client.domain.ReportItemList;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.server.categorydb.CategoryHelper;
import com.newscenter.server.db.DBHelper;
import com.newscenter.server.exception.CategoryHelperException;

/**
 * @author root
 * 
 */
public class ReportsHelperTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String driverClassName = "com.mysql.jdbc.Driver";
		String connectionUrl = "jdbc:mysql://10.10.10.125:3306/lighthouse?noAccessToProcedureBodies=true";
		String username = "root";
		String password = "mysql";
		
		DBHelper.driverClassName = driverClassName;
		
		Driver drv =(Driver)Class.forName(DBHelper.driverClassName).newInstance();
	  	DriverManager.registerDriver(drv); 
	
	  	DBHelper.connectionUrl = connectionUrl;
		DBHelper.username = username;
		DBHelper.password = password;
	
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
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#getAllReportItem()}
	 * .
	 */
	@Test
	public void testGetAllReportItem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#getReportItem(int)}
	 * .
	 */
	@Test
	public void testGetReportItem() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#getFileToDownload(int)}
	 * .
	 */
	@Test
	public void testGetFileToDownload() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#setAllNewsreportsFields(com.lighthouse.report.client.domain.ReportItem, java.io.InputStream, javax.servlet.http.HttpServletRequest)}
	 * .
	 */
	@Test
	public void testSetAllNewsreportsFields() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#setTagIdAndReportId(com.lighthouse.report.client.domain.ReportItem, javax.servlet.http.HttpServletRequest)}
	 * .
	 */
	@Test
	public void testSetTagIdAndReportId() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#getMaxValue(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetMaxValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#getTagId(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetTagId() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#saveNewTags(com.admin.client.TagItemInformation, java.lang.String, boolean, com.login.client.UserInformation)}
	 * .
	 */
	@Test
	public void testSaveNewTags() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#editNewsReportFields(com.lighthouse.report.client.domain.ReportItem, java.io.InputStream)}
	 * .
	 */
	@Test
	public void testEditNewsReportFields() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#deleteSelectedReport(java.util.HashMap)}
	 * .
	 */
	@Test
	public void testDeleteSelectedReport() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#getAllReportItems(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetAllReportItems() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#getReportItemId(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetReportItemId() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.lighthouse.report.server.helper.ReportsHelper#getAllReportItem(com.lighthouse.group.client.domain.Group, int, com.lighthouse.group.client.criteria.GroupPageCriteria, java.lang.Long)}
	 * .
	 * @throws CategoryHelperException 
	 */
/*	@Test
	public void testGetAllReportItemGroupIntGroupPageCriteriaLongForOrCriteria()  {
	try{
		Group group = new Group();
		group.setGroupId(20);
		group.setNewsCenterId(2);
		group.setUserId(1);
		group.setIsMandatory(0);
		CategoryHelper catHelper = new CategoryHelper(2,1);
		CategoryMap cmap = catHelper.getCategories();
		
		int newsmode=0;
		GroupPageCriteria criteria = new GroupPageCriteria();
		criteria.setStartIndex(0);
		criteria.setPageSize(15);
		
		Long userid = 158L;
		
		
		GroupHelper grpHelper = new GroupHelper();
		GroupCategoryMap gmap = grpHelper.getGroupCategoryMap(group, cmap);
		if(!gmap.isEmpty()){
			assertTrue("The map is not empty",true);
			group.setGroupCategoryMap(gmap);
		}else
			assertFalse("The map is empty", false);	
		
		ReportsHelper reportHelper = new ReportsHelper();
		 Date now = new Date();
         long starttimestamp = now.getTime();
        System.out.println("Start: " +starttimestamp);
		ReportItemList reportList =	reportHelper.getAllReportItem(group, newsmode, criteria, userid);
		now = new Date();
	    long endtimestamp = now.getTime();
        System.out.println("End: " +endtimestamp);
        System.out.println("time taken: in miiliseconds " +(endtimestamp-starttimestamp));
        System.out.println("time taken: " +(endtimestamp-starttimestamp)/1000);
    	
		assertNotNull(reportList);
		
	}
	catch(Exception e){
		e.printStackTrace();
	}
	}
*/
	@Test
	public void testGetAllReportItemGroupIntGroupPageCriteriaLongForAndCriteria()  {
	try{
		Group group = new Group();
		group.setGroupId(42);
		group.setNewsCenterId(5);
		group.setUserId(158);
		group.setIsMandatory(0);
		CategoryHelper catHelper = new CategoryHelper(5, 158);
		CategoryMap cmap = catHelper.getCategories();
		
		int newsmode=1;
		GroupPageCriteria criteria = new GroupPageCriteria();
		criteria.setStartIndex(0);
		criteria.setPageSize(15);
		
		Long userid = 158L;
		
		
		GroupHelper grpHelper = new GroupHelper();
		GroupCategoryMap gmap = grpHelper.getGroupCategoryMap(group, cmap);
		if(!gmap.isEmpty()){
			assertTrue("The map is not empty",true);
			group.setGroupCategoryMap(gmap);
		}else
			assertFalse("The map is empty", false);	
		
		ReportsHelper reportHelper = new ReportsHelper();
		 Date now = new Date();
         long starttimestamp = now.getTime();
        System.out.println("Start: " +starttimestamp);
		ReportItemList reportList =	reportHelper.getAllReportItem(group, newsmode, criteria, userid);
		now = new Date();
	    long endtimestamp = now.getTime();
	    for(ReportItem item : reportList){
	    	 System.out.println(item.getNewsTitle()); 	
	    }
        System.out.println("End: " +endtimestamp);
        System.out.println("time taken: in miiliseconds " +(endtimestamp-starttimestamp));
        System.out.println("time taken: " +(endtimestamp-starttimestamp)/1000);
    	
		assertNotNull(reportList);
		
	}
	catch(Exception e){
		e.printStackTrace();
	}
	}
}
