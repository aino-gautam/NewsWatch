package com.common.client;

import java.io.Serializable;
import java.util.Date;

public class UserLoginStats implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int userid;
	private int industryenumid;
	private Date timeOfLogin;
	private String username;
	private String newscatalystname;
	private String email;
	private String role;
	
	public UserLoginStats(){
		
	}

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

	public int getIndustryenumid() {
		return industryenumid;
	}

	public void setIndustryenumid(int industryenumid) {
		this.industryenumid = industryenumid;
	}

	public Date getTimeOfLogin() {
		return timeOfLogin;
	}

	public void setTimeOfLogin(Date timeOfOperation) {
		this.timeOfLogin = timeOfOperation;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
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

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

}
