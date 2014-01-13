package com.newscenter.client;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class EntireTest {

	Selenium selenium;
	
	public EntireTest(){
		
	}
	
	public void openBrowser(){
		selenium = new DefaultSelenium("localhost",
	            4444, "*firefox", "http://localhost/newscenter/index.html");
		selenium.start();
		selenium.open("http://localhost/newscenter/index.html");
		selenium.click("link=Offshore Wind");
		selenium.setSpeed("700");
		if(selenium.getTitle().equals("Welcome to Aalund NetSearch NewsCatalyst"))
			System.out.println("Match found");
		selenium.type("//input[@type='text']", "nairutee@ensarm.com");
		selenium.type("//input[@type='password']", "offshorewind");
		selenium.click("//div/img");
		selenium.mouseOver("//div/img");
		selenium.mouseDown("//div/img");
		selenium.mouseUp("//div/img");
		selenium.click("//tr[3]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		selenium.click("//tr[7]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		selenium.click("//td[3]/table/tbody/tr[3]/td/table/tbody/tr[8]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		if(selenium.isTextPresent("Total: 119 stories"))
			System.out.println("Match found");
		selenium.click("//td[5]/div");
		selenium.click("//div/div/table/tbody/tr[2]/td/div");
		if(selenium.isTextPresent("AND"))
			System.out.println("Match found");
		selenium.click("//input[@type='checkbox']");
		selenium.click("//button[@type='button']");
		if(selenium.isTextPresent("No news to display"))
			System.out.println("Match found");
		selenium.click("//td[5]/div");
		selenium.click("//div/div/table/tbody/tr[2]/td/div");
		if(selenium.isTextPresent("OR"))
			System.out.println("Match found");
		selenium.click("//input[@type='checkbox']");
		selenium.click("//button[@type='button']");
		if(selenium.isTextPresent("Manage"))
			System.out.println("Match found");
		selenium.click("link=Manage");
		if(selenium.isTextPresent("Welcome Administrator to Offshore Wind"))
			System.out.println("Match found");
		if(selenium.isTextPresent("newscenter"))
			System.out.println("Match found");
		selenium.click("link=newscenter");
		if(selenium.isTextPresent("Total: 119 stories"))
			System.out.println("Match found");
	}
	public static void main(String[] args) {
		EntireTest test = new EntireTest();
		test.openBrowser();

	}

}
