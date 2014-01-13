package com.trial.client;

import java.io.Serializable;
import java.util.Date;

public class TrialAccount implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int id;
	private int userid;
	private String firstname;
	private String lastname;
	private String username;
	private String useremail;
	private long phoneno = 0;
	private Date startdate;
	private Date enddate;
	private int newscenterid;
	private String newscatalystName;
	private boolean isActive;
	private boolean emailAlert;
	private int createdBy;
	private String salesExecutiveName;
	private Date createdOn;
	private int durationLeft;
	private int duration;
	boolean isExtension;
	private String password;
	private String signature;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public Date getStartdate() {
		return startdate;
	}
	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}
	public Date getEnddate() {
		return enddate;
	}
	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}
	public int getNewscenterid() {
		return newscenterid;
	}
	public void setNewscenterid(int newscenterid) {
		this.newscenterid = newscenterid;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
	public boolean isEmailAlert() {
		return emailAlert;
	}
	public void setEmailAlert(boolean emailAlert) {
		this.emailAlert = emailAlert;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getSalesExecutiveName() {
		return salesExecutiveName;
	}
	public void setSalesExecutiveName(String salesExecutiveName) {
		this.salesExecutiveName = salesExecutiveName;
	}
	public String getNewscatalystName() {
		return newscatalystName;
	}
	public void setNewscatalystName(String newscatalystName) {
		this.newscatalystName = newscatalystName;
	}
	public String getUseremail() {
		return useremail;
	}
	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}
	public int getDurationLeft() {
		return durationLeft;
	}
	public void setDurationLeft(int durationLeft) {
		this.durationLeft = durationLeft;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public boolean isExtension() {
		return isExtension;
	}
	public void setExtension(boolean isExtension) {
		this.isExtension = isExtension;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
}
