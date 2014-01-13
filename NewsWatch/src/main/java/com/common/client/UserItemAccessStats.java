package com.common.client;

import java.io.Serializable;
import java.util.Date;

public class UserItemAccessStats implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private String userid;
	private String firstname;
	private String lastname;
	private String newsitemtitle;
	private int newitemid;
	private int newscatalystitemcount;
	private int newsletteritemcount;
	private Date newsitemdate;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
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
	public String getNewsitemtitle() {
		return newsitemtitle;
	}
	public void setNewsitemtitle(String newsitemtitle) {
		this.newsitemtitle = newsitemtitle;
	}
	public int getNewitemid() {
		return newitemid;
	}
	public void setNewitemid(int newitemid) {
		this.newitemid = newitemid;
	}
	public int getNewscatalystitemcount() {
		return newscatalystitemcount;
	}
	public void setNewscatalystitemcount(int newscatalystitemcount) {
		this.newscatalystitemcount = newscatalystitemcount;
	}
	public int getNewsletteritemcount() {
		return newsletteritemcount;
	}
	public void setNewsletteritemcount(int newsletteritemcount) {
		this.newsletteritemcount = newsletteritemcount;
	}
	public Date getNewsitemdate() {
		return newsitemdate;
	}
	public void setNewsitemdate(Date newsitemdate) {
		this.newsitemdate = newsitemdate;
	}
	
	
}
