package lighthouse.group.server.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.junit.Test;

import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.server.helper.GroupHelper;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.categorydb.CategoryHelper;

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
		/*GroupCategoryMap grpMap = grpHelper.getGroupCategoryMap(0);
		
		if (!grpMap.isEmpty())
			assertTrue("The map is not empty", true);
		else
			assertFalse("The map is empty because there is no valid user",
					false);
		try {
			GroupHelper groupHelper = new GroupHelper(1, 2, 1);
			GroupCategoryMap groupMap = groupHelper.getGroupCategoryMap(1);

			int grpSize = groupMap.size();
			for (Object grpObj : groupMap.keySet()) {
				int groupId = (Integer) grpObj;
				Group group = (Group) groupMap.get(grpObj);
				HashMap categMap = group.getGroupCategoryMap();
				for (Object catObj : categMap.keySet()) {
					int categoryid = (Integer) catObj;
					CategoryItem categoryitem = (CategoryItem) categMap.get(catObj);
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
	}*/
	
	
	@Test
	public void getUserGroupsList()
	{
	  GroupHelper grpHelper = new GroupHelper();
	  List<Group> groupList = grpHelper.getGroupList(1,1);
	  if(!groupList.isEmpty())
		  assertTrue("The list is not empty", true);
		  else
		assertFalse("The list is empty because there are no groups",false);  
	  try{
		  GroupHelper groupHelper = new GroupHelper();
		  List<Group> grpList = groupHelper.getGroupList(158, 4);
		  int groupListSize = grpList.size();
		  if(groupListSize == 5 )
			  assertTrue("The user has 5 groups",true);
		  else
			  assertFalse("Some groups are missing",false );
		  
	  }
	  catch(Exception e){
		  logger.log(Level.INFO,"The groupList is empty");
	  }
	  
	  
	}
	
	@Test
	public void getGroupCategoryMapForMandatoryGroup (){
		try{
		Group group = new Group();
		group.setGroupId(1);
		group.setNewsCenterId(5);
		group.setUserId(158);
		group.setIsMandatory(1);
		CategoryHelper catHelper = new CategoryHelper(5,158);
		CategoryMap cmap = catHelper.getCategories();
		GroupHelper grpHelper = new GroupHelper();
		GroupCategoryMap gmap = grpHelper.getGroupCategoryMap(group, cmap);
		if(!gmap.isEmpty())
			assertTrue("The map is not empty",true);
			else
			assertFalse("The map is empty", false);	
		int grpCatSize = gmap.size();
		
		for(Object obj: gmap.keySet()){
			int grpId = (Integer) obj;
			CategoryMap catMap = (CategoryMap)gmap.get(obj);
			int size = catMap.size();
			if(size==6)
				assertTrue("The categories expected are right",true);
			else
				assertFalse("The categories expected are not right",false);
			    CategoryItem catItem = (CategoryItem) catMap.get(obj);
			    HashMap tagmap = catItem.getItemMap();
			    TagItem tagItem = (TagItem)tagmap.get(251);
			    boolean bool = tagItem.isSelected();
			    if(bool==true)
			    	assertTrue("The user has selected the tag",true);
			    else
			    	assertFalse("The tag is not selected",false);
		}
			/*HashMap categMap = grp.getGroupCategoryMap().getCategoryMap();
			for(Object catobj : categMap.keySet()){
			int categoryid =(Integer) catobj;
			CategoryItem categoryitem = (CategoryItem)categMap.get(obj);
			HashMap tagmap = categoryitem.getItemMap();
				for(Object tagobj:tagmap.keySet())
				{
					TagItem tagitem = (TagItem)tagmap.get(tagobj);
					int id = tagitem.getTagId();
					String tagname = tagitem.getTagName();
					boolean bool = tagitem.isSelected();
					if((id == 251)&&(bool==true))
						assertTrue("The user has selected the tag",true);
				}
				int catSize = categMap.size();
				if (catSize == 2)
					assertTrue("The map contains 2 categories", true);
				else
					assertFalse("Some categories are missing", false);
		 }*/
			
		
		}	//end of try
		catch(Exception e ){
			logger.log(Level.SEVERE,"GroupCategory map test failed"+e.getMessage());
		}
		
		
		
	}	
	
	@Test
	public void getGroupCategoryMapForCustomGroup(){
		try{
		Group grp = new Group();
		grp.setGroupId(14);
		grp.setIsMandatory(0);
		grp.setNewsCenterId(5);
		grp.setUserId(158);
		CategoryHelper catHelper = new CategoryHelper(5,158);
		CategoryMap cmap = catHelper.getCategories();
		GroupHelper grpHelper = new GroupHelper();
		GroupCategoryMap gmap = grpHelper.getGroupCategoryMap(grp, cmap);
		if(!gmap.isEmpty())
			assertTrue("The map is not empty",true);
			else
			assertFalse("The map is empty", false);	
		int grpCatSize = gmap.size();
		
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
