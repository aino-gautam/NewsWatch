package com.newscenter.client;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class ANDnOR {

	Selenium selenium;
	
	public ANDnOR(){
		
	}
	
	public void openBrowser(){
		selenium = new DefaultSelenium("localhost",4444,"*firefox","http://localhost/newscenter/index.html");
		selenium.start();
		selenium.open("http://localhost/newscenter/index.html");
		selenium.click("link=Offshore Wind");
		selenium.waitForPageToLoad("30000");
		selenium.setSpeed("900");
		selenium.type("//input[@type='text']", "nairutee@ensarm.com");
		selenium.type("//input[@type='password']", "offshorewind");
		selenium.click("//div/img");
		selenium.mouseOver("//div/img");
		selenium.mouseDown("//div/img");
		selenium.mouseUp("//div/img");
		selenium.click("//tr[3]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		selenium.click("//tr[7]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		selenium.click("//tr[5]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		selenium.click("//td[3]/table/tbody/tr[3]/td/table/tbody/tr[8]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		if(selenium.isTextPresent("Total: 10 stories"))
			System.out.println("For AND feature 10 stories found");
		selenium.click("//td[5]/div");
		selenium.click("//div/div/table/tbody/tr[2]/td/div");
		selenium.click("//input[@type='checkbox']");
		selenium.click("//button[@type='button']");
		if(selenium.isTextPresent("Total: 207 stories"))
			System.out.println("For OR feature 207 stories found");
		selenium.click("//td[5]/div");
		selenium.click("//div/div/table/tbody/tr[2]/td/div");
		selenium.click("//input[@type='checkbox']");
		selenium.click("//button[@type='button']");
		if(selenium.isTextPresent("Total: 10 stories")){
			System.out.println("For AND feature 10 stories found");
		}
		System.out.println("click onto manage link");
		selenium.click("link=Manage");
		System.out.println("Inside manage link. click onto newscenter link and see if we get 10 stories");
		selenium.click("link=newscenter");
		if(selenium.isTextPresent("Total: 10 stories")){
		    System.out.println("For AND feature 10 stories found");
		}
		System.out.println("Now logout");
		selenium.click("link=logout");
		selenium.waitForPageToLoad("30000");
		System.out.println("After login in back");
		selenium.type("//input[@type='text']", "nairutee@ensarm.com");
		selenium.type("//input[@type='password']", "offshorewind");
		selenium.click("//div/img");
		selenium.mouseOver("//div/img");
		selenium.mouseDown("//div/img");
		selenium.mouseUp("//div/img");
		System.out.println("Check if same 10 stories we r getting or nt");
		 if(selenium.isTextPresent("Total: 10 stories"))
			 System.out.println("For AND feature 10 stories found");
		selenium.click("//td[5]/div");
		selenium.click("//div/div/table/tbody/tr[2]/td/div");
		selenium.click("//input[@type='checkbox']");
		selenium.click("//button[@type='button']");	
		selenium.close();
		selenium.stop();
	}
	
	public static void main(String[] args) {
		ANDnOR andnor = new ANDnOR();
		andnor.openBrowser();
	}

}
