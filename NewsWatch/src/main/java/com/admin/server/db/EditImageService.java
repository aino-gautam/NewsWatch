package com.admin.server.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import com.admin.client.NewsItemsAdminInformation;
import com.login.server.db.AllocateResources;
import com.mysql.jdbc.Blob;

public class EditImageService extends HttpServlet implements Servlet{
	String connectionUrl;
	String driverClassName;
	String username;
	String password;
	NewsItemsAdminInformation newsinfo = new NewsItemsAdminInformation();
	InputStream inputstream;
	String tomcatpath;
	byte[] b3;
	Blob b = null;

	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		ServletContext context=getServletContext();
		connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(AllocateResources.USERNAME);
		password =(String)context.getAttribute(AllocateResources.PASSWORD);
		tomcatpath =(String)context.getAttribute(AllocateResources.TOMCATPATH);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{
		res.setContentType("text/html");
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
		}catch (FileUploadException e){	}

		UserHelperAdmin helper = new UserHelperAdmin(req, res, connectionUrl, driverClassName, username, password, tomcatpath);
		helper.setB(b3);
		helper.setInputstream(inputstream);
		helper.editNewsItemFields(newsinfo);
		helper.closeConnection();
	}

	public void processFormField(FileItem item){
		try{
		if (item.isFormField()) {
		    String name = item.getFieldName();

		    if(name.equals("industryTextBox")){
		    	String industryname = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
		    	newsinfo.setIndustryName(industryname);
		    }
		    if(name.equals("userSelectedTags")){
			    String value = item.getString();
			    System.out.println("the val"+value);
		    }
		    if(name.equals("newstitleTextBox")){
		    	String title = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
		    	newsinfo.setNewsTitle(title);
		    }
		    if(name.equals("abstractNewsTextArea")){
		    	String abstrct = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
		    	newsinfo.setAbstractNews(abstrct);
		    }
		    if(name.equals("contentTextArea")){
		    	String content = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
		    	newsinfo.setContent(content);
		    }
		    if(name.equals("urlTextBox")){
		    	String url = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
		    	newsinfo.setUrl(url);
		    }
		    if(name.equals("sourceTextBox")){
		    	String source = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
		    	newsinfo.setSource(source);
		    }
		    if(name.equals("dateTextBox")){
		    	//String date = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
		    	Date date = Date.valueOf(item.getString());
		    	newsinfo.setDate(date);	    	
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
		    	newsinfo.setArrayTagList(arraylist);
		    	System.out.println("the valuess"+item.getString());
		    }
		    if(name.equals("NewsCenterID")){
		    	String str = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
		    	int id = Integer.parseInt(str);
		    	newsinfo.setNewsItemId(id);
		    }
		}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void processUploadedFile(FileItem item) throws IOException{
		if (!item.isFormField()) {
			try{
				String fieldName = item.getFieldName();
			    String fileName = item.getName();
			    String contentType = item.getContentType();
			    long sizeInBytes = item.getSize();
			    inputstream = item.getInputStream();
			    
			    int filelength = (int) item.getSize();
			    if(filelength != 0){
			    	newsinfo.setFilelength(filelength);
			    }
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
}