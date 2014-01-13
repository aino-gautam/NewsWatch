package com.lighthouse.admin.server;

import java.util.ArrayList;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.admin.server.MailClient;

public class LHMailClient extends MailClient {

	public LHMailClient(String user, String password, String port, String host) {
		super(user, password, port, host);
	}

	public void sendMessage(String from, ArrayList<String> listOfAdmin, String subject, String content)	throws MessagingException{
		System.out.println("SENDING message from " + from + " to " + listOfAdmin);
	    MimeMessage msg = new MimeMessage(session);
	    Address[] fromaddresses =  InternetAddress.parse(from);
	    msg.addFrom(fromaddresses);
	    for(int i =0; i<listOfAdmin.size();i++){
	    	msg.addRecipients(Message.RecipientType.TO, listOfAdmin.get(i));
	    }
	    msg.addRecipients(Message.RecipientType.BCC, "pict@ensarm.com");
	    msg.setSubject(subject);
	    msg.setContent(content,"text/html; charset=UTF-8");
	    Transport.send(msg);
	    System.out.println("Message sent to --> "+listOfAdmin);
    }
}
