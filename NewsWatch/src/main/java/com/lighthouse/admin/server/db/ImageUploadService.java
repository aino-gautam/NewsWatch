package com.lighthouse.admin.server.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.lighthouse.report.client.domain.ReportItem;
import com.lighthouse.report.server.helper.ReportsHelper;
import com.login.server.db.AllocateResources;

public class ImageUploadService extends HttpServlet implements Servlet {

	String connectionUrl;
	String driverClassName;
	String username;
	String password;
	ReportItem reportItem = new ReportItem();
	InputStream inputstream;
	String tomcatpath;
	ServletContext context;
	HttpServletRequest request;
	HttpServletResponse response;
	boolean result=false;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		context=getServletContext();
		connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(AllocateResources.USERNAME); 
		password =(String)context.getAttribute(AllocateResources.PASSWORD);
		tomcatpath =(String)context.getAttribute(AllocateResources.TOMCATPATH);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("It is in doget method...");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		request=req;
		response=resp;
		resp.setContentType("text/html"); 
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);

		      try {
		        List items = upload.parseRequest(req);
		        Iterator it = items.iterator();
		        while (it.hasNext()) {
		          FileItem item = (FileItem) it.next();
		          
		          if (!item.isFormField()){
		        	  processUploadedFile(item);
		          }
		          else{
		        	  processFormField(item);
		          }
		        }
		      }
		      catch (FileUploadException e){
		      }
		ReportsHelper helper = new ReportsHelper();
		if(result)
			helper.setAllNewsreportsFields(reportItem, inputstream,request);
		helper.closeConnection();
	}
	
	public void processFormField(FileItem item){
		try{
			if (item.isFormField()) {
			    String name = item.getFieldName();
			    	    
			    if(name.equals("titleTextBox")){
			    	String title = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
			    	reportItem.setNewsTitle(title);
			    }
			    
			    if(name.equals("dateTextBox")){
			    	String title = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
			    	reportItem.setNewsDate(title);
			    }
			    
			    if(name.equals("abstractTextArea")){
			    	String abstrct = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
			    	reportItem.setAbstractNews(abstrct);
			    }
			    
			    if(name.equals("sourceTextBox")){
			    	String source = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
			    	reportItem.setReportLink(source);
			    }
			    
			    if(name.equals("lifeSpanDateTextBox")){
			    	String lifeSpan= new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
			    	reportItem.setReportLifeSpan(lifeSpan);
			    }
			    
			    if(name.equals("userSelectedTags")){
				    String value = item.getString();
				    System.out.println("the val"+value);
			    }
	
			    if(name.equals("contentTextArea")){
			    	String content = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
			    	reportItem.setNewsContent(content);
			    }
			    
			    if(name.equals("urlTextBox")){
			    	String url = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
			    	reportItem.setUrl(url);
			    }
			    
			    if(name.equals("userSelectedTagTextbox")){
			    	ArrayList arraylist = new ArrayList();
			    	String tags = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
			    	String[] tagname = new String[tags.length()];
			    	tagname = tags.split(";");
			    	for(int i=0;i<tagname.length;i++){
			    		if(!tagname[i].isEmpty()){
			    			arraylist.add(tagname[i]);
			    		}
			    	}
			    	reportItem.setAssociatedTagList(arraylist);
			    }
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void processUploadedFile(FileItem item) throws IOException{
		if (!item.isFormField()) {
			try {
				String contentType = item.getContentType();
				String type =null;
				inputstream = item.getInputStream();
				Long fileSize = item.getSize();
				if(fileSize>10485760){
					response.getWriter().write("Failed");
					result = false;
				}
				else
					result=true;
				String filename =  item.getName();
				StringTokenizer token = new StringTokenizer(filename,".");
				String fname = token.nextToken();
				if(token.hasMoreTokens())
					type = token.nextToken();
				else
					type = "unknown";
				reportItem.setReportMimeType(type);
			} catch (Exception e) {
				response.getWriter().write("Failed");
				result = false;
				e.printStackTrace();
			}
		}
	}

}
