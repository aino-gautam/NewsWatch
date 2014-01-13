package com.login.server;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

class GJMailAuthenticator extends javax.mail.Authenticator{
	  protected PasswordAuthentication getPasswordAuthentication()
	   {
	    return new PasswordAuthentication("prachi@ensarm.com", "blacksmith6919");
	    }
	
	}

