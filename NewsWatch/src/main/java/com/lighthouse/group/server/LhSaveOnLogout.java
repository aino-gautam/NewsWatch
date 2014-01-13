package com.lighthouse.group.server;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lighthouse.group.client.GroupCategoryMap;
import com.lighthouse.group.client.domain.Group;

import com.newscenter.client.tags.TagItem;
import com.newscenter.server.db.DBHelper;

public class LhSaveOnLogout extends Thread implements Runnable {

	private Connection conn;
	private int userId;
	private int ncid;
	private List<Group> groupsList;
	private DBHelper dbhelper;
	Logger logger = Logger.getLogger(LhSaveOnLogout.class.getName());
	
	public LhSaveOnLogout(Connection con){
		conn = con;
	}

	public LhSaveOnLogout(){
		
	}
	
	public void run() {
		dbhelper = new DBHelper();
		conn = (Connection) dbhelper.getConnection();
		try{
			if(groupsList != null){
				for(Group group : groupsList){
					GroupCategoryMap gcmap = group.getGroupCategoryMap();
					logger.log(Level.INFO,"[LhSaveOnLogout ---- selected tags: "+gcmap.getSelectedTags().size()+" for group: "+group.getGroupName()+" ]");
					int groupId = group.getGroupId();
					ArrayList dirtyList = gcmap.getDirtyTags();
					logger.log(Level.INFO,"[LhSaveOnLogout ---- dirty tags: "+dirtyList.size()+" for group: "+group.getGroupName()+" ]");
					Iterator iter = dirtyList.iterator();
					
						while(iter.hasNext()){
							TagItem tag = (TagItem)iter.next();
							int tagid = tag.getTagId();
							
							Statement stmt = conn.createStatement();
							if(tag.isSelected()){
								String query1 = "select * from usergrouptagselection where TagItemId=" +tagid+ " and UserId=" +userId+ " and NewsCenterId=" +ncid+ " and groupId="+groupId;
								ResultSet rs1 = stmt.executeQuery(query1);
								if(!rs1.next()){					
									String query = "insert into usergrouptagselection(UserId,TagItemId,NewsCenterId,groupId) values(" +userId+ "," +tagid+ "," +ncid+ ","+groupId+ ")";
									Statement stmt1 = conn.createStatement();
									stmt1.executeUpdate(query);
									stmt1.close();
								}
								rs1.close();
							}
							else{
								String query = "delete from usergrouptagselection where TagItemId=" +tagid+ " and UserId=" +userId+ " and NewsCenterId=" +ncid+ " and groupId="+groupId;
								Statement stmt1 = conn.createStatement();
								stmt1.executeUpdate(query);
								stmt1.close();
							}
							stmt.close();
						}
						
					}
				conn.close();
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		Thread.currentThread().interrupt();
	
	}
	
	public void setUserId(int id){
		userId = id;
	}
	public void setNCID(int id){
		ncid = id;
	}
	
	public void setGroupsList(List<Group> groupsList) {
		this.groupsList = groupsList;
	}
}
