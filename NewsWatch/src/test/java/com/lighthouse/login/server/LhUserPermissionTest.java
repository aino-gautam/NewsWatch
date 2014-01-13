package com.lighthouse.login.server;


import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.login.user.server.LhUserHelper;
import com.newscenter.server.db.DBHelper;



public class LhUserPermissionTest {
    @Test
	public void testUserPermission() {
		DBHelper helpme = new DBHelper(
				"jdbc:mysql://10.10.10.125:3306/lighthouse",
				"com.mysql.jdbc.Driver", "root", "mysql", "");
		LhUserPermission lhUserPermission=new LhUserPermission();
		lhUserPermission.setUserId(158);
		lhUserPermission.setNewsCenterId(5);
		lhUserPermission.setMailAlertPermitted(1);
		lhUserPermission.setGroupsPermitted(1);
		lhUserPermission.setReportsPermitted(0);
		lhUserPermission.setCommentsPermitted(0);
		lhUserPermission.setViewsPermitted(0);
		lhUserPermission.setSearchPermitted(0);
		lhUserPermission.setPrimaryHeadLinePermitted(0);
		lhUserPermission.setRssermitted(0);
		lhUserPermission.setSharePermitted(0);
		lhUserPermission.setPulsePermitted(1);
		
		LhUserHelper helper=new LhUserHelper();
		boolean val=helper.saveUserPermissions(lhUserPermission);
		assertTrue(val);
		
	}
	
	@Test
	public void testGetUserPermission() {
		DBHelper helpme = new DBHelper(
				"jdbc:mysql://10.10.10.125:3306/lighthouse",
				"com.mysql.jdbc.Driver", "root", "mysql", "");
				
		LhUserHelper helper=new LhUserHelper();
		
		LhUserPermission permission=helper.getUserPermissions(5,"nairutee@ensarm.com");
		
		
	}
	
	
	
}
