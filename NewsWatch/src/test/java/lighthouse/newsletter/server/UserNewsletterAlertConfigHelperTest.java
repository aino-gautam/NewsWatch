package lighthouse.newsletter.server;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.lighthouse.newsletter.client.domain.NewsletterGroupSelectionConfig;
import com.lighthouse.newsletter.client.domain.UserNewsletterAlertConfig;
import com.lighthouse.newsletter.server.UserNewsletterAlertConfigHelper;

/**
 * @author pritam@ensarm.com
 *
 */
public class UserNewsletterAlertConfigHelperTest {

	UserNewsletterAlertConfig config=new UserNewsletterAlertConfig();
	NewsletterGroupSelectionConfig groupConfig=new NewsletterGroupSelectionConfig();
	UserNewsletterAlertConfigHelper helper=new UserNewsletterAlertConfigHelper();
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
		
		Date date=new Date();
		java.sql.Date time=new java.sql.Date(date.getTime());
		config.setAlertName("Express2");
		config.setFrequency("Weekly");
		config.setLetterFormat("HTML");
		config.setPrefDevice("PDA");
		config.setIsSingle(1);
		config.setTimeOfDelivery(time);
		config.setTimeZone("GMT");	
		groupConfig.setAlertId(20);
		groupConfig.setNewscenterId(2);
		groupConfig.setGroupId(1);
		groupConfig.setUserId(158);
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.lighthouse.newsletter.server.UserNewsletterAlertConfigHelper#createNewsletterAlert(boolean, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date)}.
	 */
	@Test
	public void testCreateUserNewsletterAlert() {
		ArrayList<UserNewsletterAlertConfig> beforeResult=helper.getAllUserAlertList(158,5);
		boolean expected=helper.createUserNewsletterAlert(config,groupConfig);
		//boolean result=helper.createNewsletterGroupSelection(groupConfig);
		ArrayList<UserNewsletterAlertConfig> afterResult=helper.getAllUserAlertList(158,5);
		System.out.println(expected);
		assertTrue(expected);
		assertTrue(beforeResult.size()!=afterResult.size());
	}

	/**
	 * Test method for {@link com.lighthouse.newsletter.server.UserNewsletterAlertConfigHelper#updateNewsletterAlert(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUpdateUserNewsletterAlert() {
		boolean expected=helper.updateUserNewsletterAlert("daily","mail",5);
		System.out.println(expected);
		assertTrue(expected);
	}
	
	/**
	 * Test method for {@link com.lighthouse.newsletter.server.UserNewsletterAlertConfigHelper#createUserGroupSelection(int, int, int, int)}.
	 */
	@Test
	public void testCreateNewsletterGroupSelection() {
		boolean expected=helper.createNewsletterGroupSelection(groupConfig);
		System.out.println(expected);
		assertTrue(expected);
	}

	/**
	 * Test method for {@link com.lighthouse.newsletter.server.UserNewsletterAlertConfigHelper#updateUserGroupSelection(int, int)}.
	 */
	@Test
	public void testUpdateNewsletterGroupSelection() {
		boolean expected=helper.updateNewsletterGroupSelection(2,2,6);
		System.out.println(expected);
		assertTrue(expected);
	}

	/**
	 * Test method for {@link com.lighthouse.newsletter.server.UserNewsletterAlertConfigHelper#getAllAlertList(int)}.
	 */
	@Test
	public void testGetAllUserAlertList() {
		ArrayList<UserNewsletterAlertConfig> expectedList=helper.getAllUserAlertList(158,5);
		assertNotNull(expectedList);
		assertTrue(expectedList.size()!=3);
	
	}
	
	/**
	 * Test method for {@link com.lighthouse.newsletter.server.UserNewsletterAlertConfigHelper#deleteParticularAlert(int)}.
	 */
		@Test
		public void testDeleteAlert() {
			ArrayList<UserNewsletterAlertConfig> beforeResult=helper.getAllUserAlertList(158,5);
			boolean expected=helper.deleteAlert(4);
			ArrayList<UserNewsletterAlertConfig> afterResult=helper.getAllUserAlertList(158,5);
			System.out.println(expected);
			assertTrue(expected);
			assertTrue(beforeResult.size()!=afterResult.size());
	}
}
