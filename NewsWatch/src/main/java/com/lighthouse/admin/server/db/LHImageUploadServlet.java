package com.lighthouse.admin.server.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.admin.client.NewsItemsAdminInformation;
import com.admin.server.db.ImageUploadService;
import com.admin.server.db.UserHelperAdmin;
import com.lighthouse.admin.client.domain.LHNewsItemsAdminInformation;
import com.lighthouse.group.server.LhSaveOnLogout;
import com.lighthouse.search.server.helper.SetupIndexHelper;
import com.lighthouse.search.server.helper.SingleRecordIndexOperations;
import com.login.client.UserInformation;
import com.login.server.db.AllocateResources;
import com.newscenter.client.tags.TagItem;


public class LHImageUploadServlet extends ImageUploadService{

	String connectionUrl;
	String driverClassName;
	String username;
	String password;
	LHNewsItemsAdminInformation newsinfo = new LHNewsItemsAdminInformation();
	InputStream inputstream;
	String tomcatpath;
	String dirPath;
	LHAdminHelper helper;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ServletContext context=getServletContext();
		connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(AllocateResources.USERNAME); 
		password =(String)context.getAttribute(AllocateResources.PASSWORD);
		tomcatpath =(String)context.getAttribute(AllocateResources.TOMCATPATH);
		
		dirPath = config.getServletContext().getInitParameter("DirPath");
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		System.out.println("It is in doget method...");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html"); 
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(req);
		    Iterator it = items.iterator();
		    while (it.hasNext()) {
		    	FileItem item = (FileItem) it.next();
		        if (!item.isFormField())
		        	processUploadedFile(item);
		        else
		        	processFormField(item);
		          
		    }
		}
		catch (FileUploadException e){
			e.printStackTrace();
		}
		
		helper = new LHAdminHelper(req, resp);
		helper.setAllNewsItemFields(newsinfo,inputstream);
		
		HttpSession session = req.getSession(true);
		UserInformation userInfo = (UserInformation) session.getAttribute("userInfo");
		
		String industryName = userInfo.getIndustryNewsCenterName();
	//	String dirPathIndustryName = dirPath+industryName;
		
		//SetupIndexHelper searchHelper = new SetupIndexHelper();
		//LHNewsItemsAdminInformation convertedNewsInfo = convertNewsInfoAdmin(newsinfo, industryName);
		
		addRecordToIndex(industryName);
		helper.closeConnection();
	}
	
	private void addRecordToIndex(String industryName){
		try{			
			String dirPathIndustryName = dirPath+industryName;
			LHNewsItemsAdminInformation convertedNewsInfo = convertNewsInfoAdmin(newsinfo, industryName);
			
			SingleRecordIndexOperations addThread = new SingleRecordIndexOperations();
			addThread.setDaemon(true);
			addThread.setDirPath(dirPathIndustryName);
			addThread.setOperation("addRecord");
			addThread.setNewsItem(convertedNewsInfo);
			
			addThread.start();
		}catch(Exception ex){
		
			ex.printStackTrace();
		}
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
		    if(name.equals("authorTextBox")){
		    	String author = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
		    	newsinfo.setAuthor(author);
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
		    	System.out.println("the values"+item.getString());
		    }
		    if(name.equals("isLocked")){
		    	String isLocked = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
		    	newsinfo.setIsLocked(Integer.parseInt(isLocked));
		    }
		    if(name.equals("priorityListBox")){
			    String value = item.getString();
			    System.out.println("the val"+value);
			    newsinfo.setNewsPriority(value);
		    }
		}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void processUploadedFile(FileItem item) throws IOException
	{
		if (!item.isFormField()) {
		    String fieldName = item.getFieldName();
		    String fileName = item.getName();
		    String contentType = item.getContentType();
		    long sizeInBytes = item.getSize();
		    inputstream = item.getInputStream();
		    int filelength = (int) item.getSize();
		    newsinfo.setFilelength(filelength);
		}
	}
	
	
	private LHNewsItemsAdminInformation convertNewsInfoAdmin(LHNewsItemsAdminInformation originalNewsInfo, String industryName){
		LHNewsItemsAdminInformation tempNewsInfo = new LHNewsItemsAdminInformation();
		
		tempNewsInfo.setAbstractNews(originalNewsInfo.getAbstractNews());
		tempNewsInfo.setContent(originalNewsInfo.getContent());
		tempNewsInfo.setDate(originalNewsInfo.getDate());
		tempNewsInfo.setIndustryName(originalNewsInfo.getIndustryName());
		tempNewsInfo.setNewsItemId(originalNewsInfo.getNewsItemId());
		tempNewsInfo.setNewsTitle(originalNewsInfo.getNewsTitle());
		tempNewsInfo.setSource(originalNewsInfo.getSource());
		tempNewsInfo.setTagName(originalNewsInfo.getTagName());
		tempNewsInfo.setUrl(originalNewsInfo.getUrl());
		
		ArrayList tempList = originalNewsInfo.getArrayTagList();
		
		ArrayList<TagItem> tagItemList = new ArrayList<TagItem>();
		
		for(int i=0;i<tempList.size();i++){
			String tagName = (String) tempList.get(i);
			int tagItemId = helper.getTagId(industryName, tagName);
			TagItem item = new TagItem();
			item.setTagId(tagItemId);
			item.setTagName(tagName);
			tagItemList.add(item);
		}
		
		tempNewsInfo.setArrayTagList(tagItemList);
		tempNewsInfo.setIsLocked(originalNewsInfo.getIsLocked());
		tempNewsInfo.setNewsPriority(originalNewsInfo.getNewsPriority());
		return tempNewsInfo;
	}
}
