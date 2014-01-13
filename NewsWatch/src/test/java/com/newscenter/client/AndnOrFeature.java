package com.newscenter.client;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class AndnOrFeature {

	Selenium selenium;
	String startDisplayText;
	String speed;
	String text;
	String mode;
	
	public AndnOrFeature(){
		
	}
	
	public static void main(String[] args) {
		AndnOrFeature feature = new AndnOrFeature();
		try {
			feature.openBrowser();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void openBrowser() throws Exception{
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
		for (int second = 0;; second++) {
			if (second >= 60) 
				System.out.println("timeout");
			try { 
				if (selenium.isTextPresent("Updating news..."))
					System.out.println("Time taken : "+second+ " secs");
					break; 
				} catch (Exception e) {}
			Thread.sleep(1000);
		}
		text = selenium.getText("//tr[3]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		System.out.println("Retrived data on click of "+text);
		
		selenium.click("//tr[7]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		for (int second = 0;; second++) {
			if (second >= 60) System.out.println("timeout");
			try { if (selenium.isTextPresent("Updating news..."))
				System.out.println("Time taken : "+second+ "secs");
				break; 
				} catch (Exception e) {}
			Thread.sleep(1000);
		}
		text = selenium.getText("//tr[7]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		System.out.println("Retrived data on click of "+text);
		
		selenium.click("//tr[5]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		for (int second = 0;; second++) {
			if (second >= 60) System.out.println("timeout");
			try { if (selenium.isTextPresent("Updating news..."))
				System.out.println("Time taken : "+second+ "secs");
				break; 
				} catch (Exception e) {}
			Thread.sleep(1000);
		}
		text = selenium.getText("//tr[5]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		System.out.println("Retrived data on click of "+text);
		
		selenium.click("//td[3]/table/tbody/tr[3]/td/table/tbody/tr[8]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		for (int second = 0;; second++) {
			if (second >= 60) System.out.println("timeout");
			try { if (selenium.isTextPresent("Updating news..."))
				System.out.println("Time taken : "+second+ "secs");
				break; 
				} catch (Exception e) {}
			Thread.sleep(1000);
		}
		text = selenium.getText("//td[3]/table/tbody/tr[3]/td/table/tbody/tr[8]/td/table/tbody/tr/td/table/tbody/tr/td[2]/div");
		System.out.println("Retrived data on click of "+text);
		
		startDisplayText = selenium.getText("//td/table/tbody/tr[1]/td/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr/td/div");
		System.out.println("Stories: "+startDisplayText);
		
		selenium.click("//td[5]/div");
		selenium.click("//div/div/table/tbody/tr[2]/td/div");
		mode=selenium.getTable("//div/div/table.1.0");
		selenium.click("//input[@type='checkbox']");
		System.out.println("Mode: "+mode);
		selenium.click("//button[@type='button']");
		startDisplayText = selenium.getText("//td/table/tbody/tr[1]/td/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr/td/div");
		System.out.println("Stories: "+startDisplayText);
		
		selenium.click("//td[5]/div");
		selenium.click("//div/div/table/tbody/tr[2]/td/div");
		mode=selenium.getTable("//div/div/table.1.0");
		selenium.click("//input[@type='checkbox']");
		System.out.println("Mode: "+mode);
		selenium.click("//button[@type='button']");
		startDisplayText = selenium.getText("//td/table/tbody/tr[1]/td/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr/td/div");
		System.out.println("Stories: "+startDisplayText);
		
		System.out.println("click onto manage link");
		selenium.click("link=Manage");
		System.out.println("Inside manage link. click onto newscenter link and see if we get 10 stories");
		selenium.click("link=newscenter");
		startDisplayText = selenium.getText("//td/table/tbody/tr[1]/td/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr/td/div");
		System.out.println("Stories: "+startDisplayText);
		
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
		startDisplayText = selenium.getText("//td/table/tbody/tr[1]/td/table/tbody/tr[1]/td/table/tbody/tr/td/table/tbody/tr/td/div");
		System.out.println("Stories: "+startDisplayText);
		
		selenium.close();
		selenium.stop();
	}
	

}
