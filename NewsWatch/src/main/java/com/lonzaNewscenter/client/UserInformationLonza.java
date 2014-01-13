package com.lonzaNewscenter.client;

import java.io.Serializable;

public class UserInformationLonza implements Serializable{
	int lonzanewscenterid = 0;
	int userid = 0;
	int newscenterid = 0;
	String path = "";

	public int getLonzanewscenterid() {
		return lonzanewscenterid;
	}
	public void setLonzanewscenterid(int lonzanewscenterid) {
		this.lonzanewscenterid = lonzanewscenterid;
	}
	public int getUserid() {
		return userid;
	}
	public void setUserid(int userid) {
		this.userid = userid;
	}
	public int getNewscenterid() {
		return newscenterid;
	}
	public void setNewscenterid(int newscenterid) {
		this.newscenterid = newscenterid;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
}