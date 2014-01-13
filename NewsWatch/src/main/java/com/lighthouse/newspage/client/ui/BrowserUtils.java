package com.lighthouse.newspage.client.ui;

/**
 * 
 * @author kiran@ensarm.com
 * This class for closing the browser tab.
 *
 */
public class BrowserUtils {
	
	 private BrowserUtils() {
	        
	    }
	    
	    native  public static void close()/*-{  $wnd.close();}-*/;

}
