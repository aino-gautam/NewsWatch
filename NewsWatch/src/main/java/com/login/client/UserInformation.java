package com.login.client;

import java.io.Serializable;


public class UserInformation implements Serializable
{
		String firstname = "";
		String lastname = "";
		String companyname = "";
		String title = "";
		String password = "";
		String email = "";
		String usernameMail ="";
		String passwordMail ="";
		String subjectMail="";
		String bodyMail = "";
		String senderMail = "";
		String recipientsMail = "";
		long phoneno = 0;
		int durationLeft = 0;
		int isAdmin = 0;
		int isApproved = 0;
		int userSelectedIndustryID;
		int userSelectedNewsCenterID;
		int userId;
		String industryNewsCenterName="";
		int period;
		int newsFilterMode;
		int isSubscribed;
		String userSelectedNewsCenterURL="";
		int userCreatedBy;
		String signature;
		
		public String getUserSelectedNewsCenterURL() {
			return userSelectedNewsCenterURL;
		}

		public void setUserSelectedNewsCenterURL(String userSelectedNewsCenterURL) {
			this.userSelectedNewsCenterURL = userSelectedNewsCenterURL;
		}

		public int getIsSubscribed() {
			return isSubscribed;
		}

		public void setIsSubscribed(int isSubscribed) {
			this.isSubscribed = isSubscribed;
		}

		public int getPeriod() {
			return period;
		}

		public void setPeriod(int period) {
			this.period = period;
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

		public String getUsernameMail() {
			return usernameMail;
		}

		public void setUsernameMail(String usernameMail) {
			this.usernameMail = usernameMail;
		}

		public String getPasswordMail() {
			return passwordMail;
		}

		public void setPasswordMail(String passwordMail) {
			this.passwordMail = passwordMail;
		}

		public String getSubjectMail() {
			return subjectMail;
		}

		public void setSubjectMail(String subjectMail) {
			this.subjectMail = subjectMail;
		}

		public String getBodyMail() {
			return bodyMail;
		}

		public void setBodyMail(String bodyMail) {
			this.bodyMail = bodyMail;
		}

		public String getSenderMail() {
			return senderMail;
		}

		public void setSenderMail(String senderMail) {
			this.senderMail = senderMail;
		}

		public String getRecipientsMail() {
			return recipientsMail;
		}

		public void setRecipientsMail(String recipientsMail) {
			this.recipientsMail = recipientsMail;
		}

		public int getAdmin(){
			return isAdmin;
		}

		public int getUserSelectedIndustryID() {
			return userSelectedIndustryID;
		}

		public void setUserSelectedIndustryID(int userSelectedIndustryID) {
			this.userSelectedIndustryID = userSelectedIndustryID;
		}

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public int getUserSelectedNewsCenterID() {
			return userSelectedNewsCenterID;
		}

		public void setUserSelectedNewsCenterID(int userSelectedNewsCenterID) {
			this.userSelectedNewsCenterID = userSelectedNewsCenterID;
		}

		public String getLastname() {
			return lastname;
		}

		public void setLastname(String lastname) {
			this.lastname = lastname;
		}

		public String getFirstname() {
			return firstname;
		}

		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}

		public String getCompanyname() {
			return companyname;
		}

		public void setCompanyname(String companyname) {
			this.companyname = companyname;
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

		public int getDurationLeft() {
			return durationLeft;
		}

		public void setDurationLeft(int durationLeft) {
			this.durationLeft = durationLeft;
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

		public void setApproved(int isApproved) {
			this.isApproved = isApproved;
		}
		
		public String getIndustryNewsCenterName() {
			return industryNewsCenterName;
		}

		public void setIndustryNewsCenterName(String industryNewsCenterName) {
			this.industryNewsCenterName = industryNewsCenterName;
		}

		public int getNewsFilterMode() {
			return newsFilterMode;
		}

		public void setNewsFilterMode(int newsFilterMode) {
			this.newsFilterMode = newsFilterMode;
		}

		public int getUserCreatedBy() {
			return userCreatedBy;
		}

		public void setUserCreatedBy(int userCreatedBy) {
			this.userCreatedBy = userCreatedBy;
		}

		public String getSignature() {
			return signature;
		}

		public void setSignature(String signature) {
			this.signature = signature;
		}
}