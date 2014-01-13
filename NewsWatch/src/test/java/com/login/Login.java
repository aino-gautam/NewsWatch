package com.login;

import com.thoughtworks.selenium.SeleneseTestCase;

public class Login extends SeleneseTestCase {
	public void setUp() throws Exception {
		setUp("http://localhost/newscenter/index.html", "*chrome");
	}
	public void testLogin() throws Exception {
		selenium.open("http://localhost/newscenter/index.html");
		assertEquals("", selenium.getTitle());
		selenium.click("link=Solar PV");
		selenium.setSpeed("700");
		selenium.waitForPageToLoad("30000");
		assertEquals("Welcome to Aalund NetSearch NewsCatalyst", selenium.getTitle());
		selenium.type("//input[@type='text']", "revin@en.com");
		selenium.type("//input[@type='password']", "revinrevin");
		selenium.click("//div/img");
		
		
		
		/*browser.click("//tr[5]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		browser.click("//tr[6]/td/table/tbody/tr/td/table/tbody/tr/td[1]/img");
		browser.click("//td[7]/table/tbody/tr[3]/td/table/tbody/tr[5]/td/table/tbody/tr/td/table/tbody/tr/td[1]/img");
		browser.click("//td[7]/table/tbody/tr[3]/td/table/tbody/tr[6]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		browser.click("//td[5]/div");
		browser.click("//div/div/table/tbody/tr[2]/td/div");
		browser.click("//input[@type='checkbox']");
		browser.click("//button[@type='button']");
		browser.click("//td[5]/div");
		browser.click("//div/div/table/tbody/tr[2]/td/div");
		browser.click("//input[@type='checkbox']");
		browser.click("//button[@type='button']");*/
		//browser.click("link=Manage");
		//browser.click("link=newscenter");
		
		/*browser.click("//td[5]/div");
		browser.click("//div/div/table/tbody/tr[2]/td/div");
		browser.click("//input[@type='checkbox']");
		browser.click("//button[@type='button']");
		browser.click("link=Manage");*/
		/*if(browser.getTitle().equals("Aalund NetSearch")){
			System.out.println("Username: "+username+" not registered");
			browser.close();
		}
		else{
			System.out.println("Username: "+username+" registered");
		}
		boolean boolManageLink = browser.isTextPresent("Manage");
		if(boolManageLink){
			browser.click("link=Manage");
		}*/
	}
}
