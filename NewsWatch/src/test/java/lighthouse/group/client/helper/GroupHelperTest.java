package lighthouse.group.client.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.server.helper.GroupHelper;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.TagItem;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * 
 * @author prachi@ensarm.com
 * 
 * 
 */

public class GroupHelperTest {
	Logger logger = Logger.getLogger(GroupHelperTest.class.getName());

	@Test
	public void testGroupHelper() {
		GroupHelper grpHelper = new GroupHelper();
		int userId = grpHelper.getUserId();
		int grpId = grpHelper.getGroupId();
		int ncid = grpHelper.getNcid();
		if ((ncid == -1) && (userId == -1) && (grpId == -1))
			assertTrue("The values are right", true);
		else
			assertFalse("The values are not right", false);
		try {
			GroupHelper groupHelper = new GroupHelper(1, 2, 1);
			int usrId = groupHelper.getUserId();
			int groupId = groupHelper.getGroupId();
			int newscenterid = groupHelper.getNcid();
			if ((newscenterid == 1) && (usrId == 2) && (groupId == 1))
				assertTrue("The values are right", true);
			else
				assertFalse("The values are not right", false);

		} catch (Exception e) {
			logger.log(Level.INFO, "Exception GroupHelper constructor failed");
		}
	}

	/*@Test
	public void testgetGroupCategoriesMap() {
		GroupHelper grpHelper = new GroupHelper();
		GroupCategoryMap grpMap = grpHelper.getGroupCategoriesMap();
		if (!grpMap.isEmpty())
			assertTrue("The map is not empty", true);
		else
			assertFalse("The map is empty because there is no valid user",
					false);
		try {
			GroupHelper groupHelper = new GroupHelper(1, 2, 1);
			GroupCategoryMap groupMap = groupHelper.getGroupCategoriesMap();

			int grpSize = groupMap.size();
			for (Object grpObj : groupMap.keySet()) {
				int groupId = (Integer) grpObj;
				Group group = (Group) groupMap.get(grpObj);
				HashMap categMap = group.getCatMap();
				for (Object catObj : categMap.keySet()) {
					int categoryid = (Integer) catObj;
					CategoryItem categoryitem = (CategoryItem) categMap
							.get(catObj);
					HashMap tagmap = categoryitem.getItemMap();
					for (Object tagobj : tagmap.keySet()) {
						TagItem tagitem = (TagItem) tagmap.get(tagobj);
						int id = tagitem.getTagId();
						String tagname = tagitem.getTagName();
					}
					int catSize = categMap.size();
					if (catSize == 2)
						assertTrue("The map contains 2 categories", true);
					else
						assertFalse("Some categories are missing", false);

				}

			}

		}

		catch (Exception e) {
			logger.log(Level.SEVERE, "Test to fetch the groupCategoryMap failed");
		}
	}
*/
}
