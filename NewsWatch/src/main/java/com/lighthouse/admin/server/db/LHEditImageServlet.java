package com.lighthouse.admin.server.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.admin.server.db.EditImageService;
import com.admin.server.db.UserHelperAdmin;
import com.lighthouse.admin.client.domain.LHNewsItemsAdminInformation;
import com.lighthouse.search.server.helper.SetupIndexHelper;
import com.lighthouse.search.server.helper.SingleRecordIndexOperations;
import com.login.client.UserInformation;
import com.login.server.db.AllocateResources;
import com.mysql.jdbc.Blob;
import com.newscenter.client.tags.TagItem;

public class LHEditImageServlet extends EditImageService {

	String dirPath;
	String connectionUrl;
	String driverClassName;
	String username;
	String password;
	LHNewsItemsAdminInformation newsinfo = new LHNewsItemsAdminInformation();
	InputStream inputstream;
	String tomcatpath;
	byte[] b3;
	Blob b = null;
	LHAdminHelper helper;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
	
	//	super.init(config);
		
		super.init(config);
		ServletContext context=getServletContext();
		connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		username =(String)context.getAttribute(AllocateResources.USERNAME);
		password =(String)context.getAttribute(AllocateResources.PASSWORD);
		tomcatpath =(String)context.getAttribute(AllocateResources.TOMCATPATH);
		
		dirPath = config.getServletContext().getInitParameter("DirPath");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		super.doGet(req, res);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		//super.doPost(req, res);
		
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

		helper= new LHAdminHelper(req, res);
		helper.setB(b3);
		helper.editNewsItemFields(newsinfo,inputstream);
		

		HttpSession session = req.getSession(true);
		UserInformation userInfo = (UserInformation) session.getAttribute("userInfo");
		String industryName = userInfo.getIndustryNewsCenterName();
	//	String dirPathIndustryName = dirPath+industryName;
		
		//SetupIndexHelper searchHelper = new SetupIndexHelper();
//		LHNewsItemsAdminInformation convertedNewsInfo = convertNewsInfoAdmin(newsinfo, industryName);
	//	searchHelper.updateRecordFromIndex(convertedNewsInfo, dirPathIndustryName);
		updateRecordFromIndex(industryName);
		helper.closeConnection();
	}

	private void updateRecordFromIndex(String industryName){
		try{
			String dirPathIndustryName = dirPath+industryName;
			LHNewsItemsAdminInformation convertedNewsInfo = convertNewsInfoAdmin(newsinfo, industryName);
			SingleRecordIndexOperations updateRecordThread = new SingleRecordIndexOperations();
			updateRecordThread.setDaemon(true);
			updateRecordThread.setDirPath(dirPathIndustryName);
			updateRecordThread.setOperation("updateRecord");
			updateRecordThread.setNewsItem(convertedNewsInfo);
			
			updateRecordThread.start();
		}catch(Exception ex){
		
			ex.printStackTrace();
		}
	}
	
	@Override
	public void processFormField(FileItem item) {
	
		//super.processFormField(item);
		
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
			    	if(!author.equalsIgnoreCase(""))
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
			    			if(!arraylist.contains(tagname[i]));
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

	@Override
	public void processUploadedFile(FileItem item) throws IOException {
	
	//	super.processUploadedFile(item);
		
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
		tempNewsInfo.setAuthor(originalNewsInfo.getAuthor());
		
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
