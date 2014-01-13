package com.admin.client;

import java.io.Serializable;

public class UserAdminInformation implements Serializable
{
		int userId = 0;
		String name = "";
		String password = "";
		String email = "";
		String usernameMail ="";
		String passwordMail ="";
		String subjectMail="";
		String bodyMail = "";
		String senderMail = "";
		String recipientsMail = "";
		int duration = 0;

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

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
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

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public int getDuration() {
			return duration;
		}

		public void setDuration(int duration) {
			this.duration = duration;
		}
}