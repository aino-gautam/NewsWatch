package com.admin.client;

import java.io.Serializable;

public class AdminRegistrationInformation implements Serializable
{
	int userid;
	String name="";
	String lastname = "";
	String companynm = "";
	String title = "";
	long phoneno = 0;
	String password="";
	String email="";
	String industry="";
	int duration;
	int durationLeft;
	int isAdmin;
	int isApproved;
	boolean isExtension;
    int industryid = 0; 
	
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public int getDurationLeft() {
		return durationLeft;
	}
	public void setDurationLeft(int durationLeft) {
		this.durationLeft = durationLeft;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getCompanynm() {
		return companynm;
	}
	public void setCompanynm(String companynm) {
		this.companynm = companynm;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getPhoneno() {
		return phoneno;
	}
	public void setPhoneno(long phoneno) {
		this.phoneno = phoneno;
	}
	public int getIsAdmin() {
		return isAdmin;
	}
	public void setIsAdmin(int isAdmin) {
		this.isAdmin = isAdmin;
	}
	public int getIsApproved() {
		return isApproved;
	}
	public void setIsApproved(int isApproved) {
		this.isApproved = isApproved;
	}
	public boolean isExtension() {
		return isExtension;
	}
	public void setExtension(boolean isExtension) {
		this.isExtension = isExtension;
	}
	public int getIndustryid() {
		return industryid;
	}
	public void setIndustryid(int industryid) {
		this.industryid = industryid;
	}
	
}
