package com.trial.client;

import java.io.Serializable;

public class TrialUserInformation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int salesUserId = 0;
	private String firstname = null;
	private String lastname = null;
	private String email = null;
	private int catalystName = 0;
	private boolean emailAlert;
	private boolean isActive;
	private long phoneno = 0;
	
	public int getSalesUserId() {
		return salesUserId;
	}
	public void setSalesUserId(int salesUserId) {
		this.salesUserId = salesUserId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getCatalystName() {
		return catalystName;
	}
	public void setCatalystName(int catalystName) {
		this.catalystName = catalystName;
	}
	public boolean isEmailAlert() {
		return emailAlert;
	}
	public void setEmailAlert(boolean emailAlert) {
		this.emailAlert = emailAlert;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public long getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(long phoneno) {
		this.phoneno = phoneno;
	}
}
