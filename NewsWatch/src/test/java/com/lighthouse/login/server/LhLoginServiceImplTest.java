package com.lighthouse.login.server;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.login.client.UserInformation;

public class LhLoginServiceImplTest {

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
	public void testValidateUser() {
		LhLoginServiceImpl loginTest = new LhLoginServiceImpl();
		UserInformation userInfo = new UserInformation();
		userInfo.setEmail("nairutee@ensarm.com");
		userInfo.setPassword("ennc09internal");
		ArrayList tempList = loginTest.validateUser(userInfo);
		assertTrue(!tempList.isEmpty());
	}

}
