package com.newscenter.server;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.mysql.jdbc.Connection;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.db.DBHelper;

public class SaveOnLogout extends Thread implements Runnable {

	private Connection conn;
	private int userId;
	private int ncid;
	private CategoryMap cmap;
	private DBHelper dbhelper;
	
	public SaveOnLogout(Connection con){
		conn = con;
	}

	public SaveOnLogout(){
		
	}
	
	public void run() {
		dbhelper = new DBHelper();
		conn = (Connection) dbhelper.getConnection();
		
		ArrayList dirtyList = cmap.getDirtyTags();
		Iterator iter = dirtyList.iterator();
		try{
			while(iter.hasNext()){
				TagItem tag = (TagItem)iter.next();
				int tagid = tag.getTagId();
				
				Statement stmt = conn.createStatement();
				if(tag.isSelected()){
					String query1 = "select * from usertagselection where TagItemId=" +tagid+ " and UserId=" +userId+ " and NewsCenterId=" +ncid;
					ResultSet rs1 = stmt.executeQuery(query1);
					if(!rs1.next()){					
						String query = "insert into usertagselection(UserId,TagItemId,NewsCenterId) values(" +userId+ "," +tagid+ "," +ncid+ ")";
						Statement stmt1 = conn.createStatement();
						stmt1.executeUpdate(query);
						stmt1.close();
					}
					rs1.close();
				}
				else{
					String query = "delete from usertagselection where TagItemId=" +tagid+ " and UserId=" +userId+ " and NewsCenterId=" +ncid;
					Statement stmt1 = conn.createStatement();
					stmt1.executeUpdate(query);
					stmt1.close();
				}
				stmt.close();
			}
			conn.close();
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

	public void setSavingMap(CategoryMap map){
		this.cmap = map;
	}
}
