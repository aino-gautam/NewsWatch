package com.login;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.BasicConfigurator;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class LoginPage {

	Selenium browser;
	public LoginPage(){
		
	}
	
	public static void main(String[] args) {
		LoginPage login = new LoginPage();
		login.testUserLoginDataSet();
	}

	public int openBrowser(String username,String password){
		try{
			int value = 0;
			BufferedWriter output = null;
			browser = new DefaultSelenium("localhost",
		            4444, "*firefox", "http://localhost/newscenter/index.html");
			browser.start();
			browser.setSpeed("700");
			browser.open("http://localhost/newscenter/index.html");
			browser.click("link=Solar PV");
			
			browser.type("//input[@type='text']", username);
			browser.type("//input[@type='password']", password);
			browser.click("//div/img");
			browser.mouseOver("//div/img");
			browser.mouseDown("//div/img");
			browser.mouseUp("//div/img");
			boolean bool = browser.isTextPresent("Manage");
			if(bool){
				value = 1;
				char c = (char)value; 
		    	File file = new File("//usr//local//selenium_csv_file//userlogin.txt");
		    	FileWriter writer = new FileWriter(file,true);
		    	output = new BufferedWriter(writer);
		    	output.append(c);
		    	output.flush();
		    	output.close();
				return value;
			}
			else{
				char c = (char)value;
		    	File file = new File("//usr//local//selenium_csv_file//userlogin.txt");
		    	FileWriter writer = new FileWriter(file,true);
		    	output = new BufferedWriter(writer);
		    	output.write(value);
		    	output.flush();
		    	output.close();
				return value;
			}
			   
			
		/*	testUserClickDataSet();
			browser.click("//td[5]/div");
			browser.click("//div/div/table/tbody/tr[2]/td/div");
			browser.click("//input[@type='checkbox']");
			browser.click("//button[@type='button']");*/
			
	}catch(Exception ex){
		System.out.println("Exception ..."+ ex);
		}
		return 0;
	}
	
	public void testUserLoginDataSet(){
		BasicConfigurator.configure();
		
		try{
			CSVReader reader = new CSVReader(new FileReader("//usr//local//selenium_csv_file//user.csv"));
			ArrayList list = new ArrayList();
			String [] nextLine;
		    while ((nextLine = reader.readNext()) != null) {
		    	String newsitems = nextLine[0];
		    	if(newsitems.equals("")){
		    		break;
		    	}
		    	int value = openBrowser(nextLine[0], nextLine[1]);
		    	
		    		    	
		    }
		    browser.close();
		    browser.stop();
		  
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void testUserClickDataSet(){
		BasicConfigurator.configure();
		ArrayList list = new ArrayList();
		try{
			CSVReader reader = new CSVReader(new FileReader("//usr//local//selenium_csv_file//userclicks.csv"));
			
			String [] nextLine;
		    
		    while ((nextLine = reader.readNext()) != null) {
		    	String newsitems = nextLine[0];
		    	if(newsitems.equals("")){
		    		break;
		    	}
		    	for(int i=0;i< nextLine.length;i++){
		    		list.add(nextLine[i]);
		    	}
		    	setSelection(list);
		    }
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	private void setSelection(ArrayList list) {
		Iterator itr = list.iterator();
		while(itr.hasNext()){
			browser.click(itr.next().toString());
		}
	}
}
