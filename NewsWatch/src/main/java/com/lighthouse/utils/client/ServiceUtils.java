package com.lighthouse.utils.client;


import com.google.gwt.core.client.GWT;
/**
 * Service utility class
 * @author nairutee
 *
 */
public class ServiceUtils {

	private static ServiceUtils serviceUtil;

	private ServiceUtils(){
		
	}
	
	/**
	 * gets the singleton instance of the ServiceUtils
	 * @return
	 */
	public static ServiceUtils getInstance(){
		if(serviceUtil == null)
			serviceUtil = new ServiceUtils();
		
		return serviceUtil;
	}
}
