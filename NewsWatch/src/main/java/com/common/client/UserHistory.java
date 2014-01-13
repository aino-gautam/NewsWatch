package com.common.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class UserHistory implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userid;
	private String message;
	private String operation;
	private Date operationDate;
    private int duration;
    private Date approvalDate;
    private boolean isSubscribed;
    private int newsletterperiod;
    private Date extendedDate;
    private int extendedDuration;
    private int userCreatedBy;
    private String userCreatedByName;
    private String operationPerformedBy;
    private String firstname;
    private String lastname;
    private String email;
    private String newscatalystname;
    private ArrayList<UserItemAccessStats> userItemAccessStats;
    private ArrayList<String> userLoginStats;
    
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
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public Date getOperationDate() {
		return operationDate;
	}
	public void setOperationDate(Date operationDate) {
		this.operationDate = operationDate;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public Date getApprovalDate() {
		return approvalDate;
	}
	public void setApprovalDate(Date approvalDate) {
		this.approvalDate = approvalDate;
	}
	public boolean isSubscribed() {
		return isSubscribed;
	}
	public void setSubscribed(boolean isSubscribed) {
		this.isSubscribed = isSubscribed;
	}
	public int getNewsletterperiod() {
		return newsletterperiod;
	}
	public void setNewsletterperiod(int newsletterperiod) {
		this.newsletterperiod = newsletterperiod;
	}
	public Date getExtendedDate() {
		return extendedDate;
	}
	public void setExtendedDate(Date extendedDate) {
		this.extendedDate = extendedDate;
	}
	public int getExtendedDuration() {
		return extendedDuration;
	}
	public void setExtendedDuration(int extendedDuration) {
		this.extendedDuration = extendedDuration;
	}
	public int getUserCreatedBy() {
		return userCreatedBy;
	}
	public void setUserCreatedBy(int userCreatedBy) {
		this.userCreatedBy = userCreatedBy;
	}
	public String getUserCreatedByName() {
		return userCreatedByName;
	}
	public void setUserCreatedByName(String userCreatedByName) {
		this.userCreatedByName = userCreatedByName;
	}
	public String getOperationPerformedBy() {
		return operationPerformedBy;
	}
	public void setOperationPerformedBy(String operationPerformedBy) {
		this.operationPerformedBy = operationPerformedBy;
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
	public String getNewscatalystname() {
		return newscatalystname;
	}
	public void setNewscatalystname(String newscatalystname) {
		this.newscatalystname = newscatalystname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public ArrayList<UserItemAccessStats> getUserItemAccessStats() {
		return userItemAccessStats;
	}
	public void setUserItemAccessStats(ArrayList<UserItemAccessStats> userItemAccessStats) {
		this.userItemAccessStats = userItemAccessStats;
	}
	public ArrayList<String> getUserLoginStats() {
		return userLoginStats;
	}
	public void setUserLoginStats(ArrayList<String> userLoginStats) {
		this.userLoginStats = userLoginStats;
	}
}