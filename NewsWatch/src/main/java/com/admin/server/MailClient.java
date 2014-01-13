package com.admin.server;

import java.io.*;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

public class MailClient extends Authenticator{
	public static final int SHOW_MESSAGES = 1;
	public static final int CLEAR_MESSAGES = 2;
	public static final int SHOW_AND_CLEAR =
		SHOW_MESSAGES + CLEAR_MESSAGES;
	protected String from;
	protected Session session;
	protected PasswordAuthentication authentication;
	public MailClient(String user, String password, String port, String host)
	{
		this(user, password, port, host, true);
	}

	public MailClient(String user, String password, String port, String host, boolean debug){
		from = user + '@' + host;
	    authentication = new PasswordAuthentication(user, password);
	    Properties props = new Properties();
	    props.put("mail.user", user);
	    props.put("mail.host", host);
	    props.put("mail.debug", debug ? "true" : "false");
	    props.put("mail.store.protocol", "pop3");
	    props.put("mail.transport.protocol", "smtp");
	    props.put("mail.smtp.port", port);
	      session = Session.getInstance(props, this);
	}

	public MailClient(){
		
	}
	public PasswordAuthentication getPasswordAuthentication()
	{
		return authentication;
	}

	public void sendMessage(String from, String to, String subject, String content)	throws MessagingException
    {
		/*
		 * Use the default MailClient() constructor and uncomment the following lines while using gmail stmp 
		 */
		/*Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.auth", "true");
		props.put("mail.debug", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");
		
		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator(){
			protected PasswordAuthentication getPasswordAuthentication()
			{ 
				return new PasswordAuthentication("no_reply@ensarm.com","en_noreply");	
			}
		});
		session.setDebug(true);*/
		
		System.out.println("SENDING message from " + from + " to " + to);
	    MimeMessage msg = new MimeMessage(session);
	    Address[] fromaddresses =  InternetAddress.parse(from);
	    msg.addFrom(fromaddresses);
	    msg.addRecipients(Message.RecipientType.TO, to);
	    msg.addRecipients(Message.RecipientType.BCC, "pict@ensarm.com");
	    
	    msg.setSubject(subject);
	    msg.setContent(content,"text/html; charset=UTF-8");
	    Transport.send(msg);
	    System.out.println("Message sent to --> "+to);
    }

	public void checkInbox(int mode) throws MessagingException, IOException
	{
	    if (mode == 0) return;
	    boolean show = (mode & SHOW_MESSAGES) > 0;
	    boolean clear = (mode & CLEAR_MESSAGES) > 0;
	    String action =
	      (show ? "Show" : "") +
	      (show && clear ? " and " : "") +
	      (clear ? "Clear" : "");
	    System.out.println(action + " INBOX for " + from);
	    Store store = session.getStore();
	    store.connect();
	    Folder root = store.getDefaultFolder();
	    Folder inbox = root.getFolder("inbox");
	    inbox.open(Folder.READ_WRITE);
	    Message[] msgs = inbox.getMessages();
	    if (msgs.length == 0 && show)
	    {
	      System.out.println("No messages in inbox");
	    }
	    for (int i = 0; i < msgs.length; i++)
	    {
	    	MimeMessage msg = (MimeMessage)msgs[i];
	    	if (show)
	    	{
		        System.out.println("    From: " + msg.getFrom()[0]);
		        System.out.println(" Subject: " + msg.getSubject());
		        System.out.println(" Content: " + msg.getContent());
	    	}
	    	if (clear)
	    	{
	    		msg.setFlag(Flags.Flag.DELETED, true);
	    	}
	    }
	    inbox.close(true);
	    store.close();
	    System.out.println();
	}
}