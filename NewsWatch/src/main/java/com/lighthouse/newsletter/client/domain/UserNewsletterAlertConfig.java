package com.lighthouse.newsletter.client.domain;




import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.lighthouse.group.client.domain.Group;

/**
 * Domain class for usernewsletteralert
 * @author pritam
 *
 */



public class UserNewsletterAlertConfig implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6780717861651397818L;
	private int alertId;
	private String alertName;
	private String frequency;
	private String prefDevice;
	private int isSingle;
	private String letterFormat;
	private String timeZone;
	private Date timeOfDelivery;
	private ArrayList<Group> alertGroupList;

	
	public UserNewsletterAlertConfig(){
		
	}
	
	public int getIsSingle() {
		return isSingle;
	}
	
	public void setIsSingle(int isSingle) {
		this.isSingle = isSingle;
	}
	
	public Date getTimeOfDelivery() {
		return timeOfDelivery;
	}
	
	public void setTimeOfDelivery(Date timeOfDelivery) {
		this.timeOfDelivery = timeOfDelivery;
	}

	public int getAlertId() {
		return alertId;
	}
	
	public void setAlertId(int alertId) {
		this.alertId = alertId;
	}
	
	public String getAlertName() {
		return alertName;
	}
	
	public void setAlertName(String alertName) {
		this.alertName = alertName;
	}
	
	public String getFrequency() {
		return frequency;
	}
	
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}
	
	public String getPrefDevice() {
		return prefDevice;
	}
	
	public void setPrefDevice(String prefDevice) {
		this.prefDevice = prefDevice;
	}

	public String getLetterFormat() {
		return letterFormat;
	}
	
	public void setLetterFormat(String letterFormat) {
		this.letterFormat = letterFormat;
	}
	
	public String getTimeZone() {
		return timeZone;
	}
	
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public ArrayList<Group> getAlertGroupList() {
		return alertGroupList;
	}

	public void setAlertGroupList(ArrayList<Group> alertGroupList) {
		this.alertGroupList = alertGroupList;
	}
	
	

}
