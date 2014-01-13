package com.admin.server.db;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.admin.client.TagItemInformation;
import com.google.gwt.user.client.Window;
import com.login.client.UserInformation;
import com.login.server.db.AllocateResources;
import com.mysql.jdbc.Connection;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class FileUploadServlet extends HttpServlet{

	Integer length;
	String curDir = System.getProperty("user.dir");
	String f = curDir.replace("\\","/");
	String file = f+ "/src/input.csv";

	Connection conn = null;
	String connectionUrl;
	String driverClassName;
	String username;
	String password;
	String tomcatpath;
	int industryid;
	String newscentername;
	
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
			System.out.println("Reached->doGet");
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		try{
			System.out.println("Reached->doPost");
			ServletContext context = getServletContext();
			connectionUrl = (String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
			driverClassName = (String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
			username = (String)context.getAttribute(AllocateResources.USERNAME);
			password = (String)context.getAttribute(AllocateResources.PASSWORD);
			tomcatpath = (String)context.getAttribute(AllocateResources.TOMCATPATH);

			Driver drv =(Driver)Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(drv);
			conn = (Connection)DriverManager.getConnection(connectionUrl,username,password);
			
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			
			List fileList = upload.parseRequest(req);
			InputStream uploadedFileStream = null;
			String uploadedFileName = null; // name of file on user's computer
			Iterator iter = fileList.iterator();
			while(iter.hasNext()){
				FileItem fi = (FileItem)iter.next();
				if (fi.isFormField()){
					String key = fi.getFieldName();
					String val = fi.getString();
					System.out.println("Form parameter " + key + "=" + val);
				}
				else{
					if (fi.getSize() < 1){
						throw new Exception("No file was uplaoded");
					}
					uploadedFileName = fi.getName();
					uploadedFileStream = fi.getInputStream();	
					}
			}
			
			Workbook workbook = Workbook.getWorkbook(uploadedFileStream);
			TagItemInformation tag;
			HttpSession session = req.getSession(false);
			UserInformation userInformation  = (UserInformation)session.getAttribute("userInfo");
			setIndustryid(userInformation.getUserSelectedIndustryID());
			setNewscentername(userInformation.getIndustryNewsCenterName());
			ArrayList tagsList;
			
			for (int sheet = 0; sheet < workbook.getNumberOfSheets(); sheet++){
				tagsList = new ArrayList();
				Sheet s = workbook.getSheet(sheet);
				if(s.getRow(0).length > 0){
					Cell topcell = s.getCell(0, 0);
					String categoryName = topcell.getContents();
					int col = 0;
					for(int row = 1; row<s.getRows(); row++){
						
						Cell rowcell = s.getCell(col, row);
						String tagName = rowcell.getContents();
						if(!tagName.equals("")){
							if(tagName.matches("[a-zA-Z][a-zA-Z0-9 ' ']*")){
								tag = new TagItemInformation();
								tag.setIndustryId(industryid);
								tag.setTagName(tagName);
								tagsList.add(tag);
							}
						}
					}
					saveTags(tagsList,categoryName,userInformation.getUserId());
				}
			}

		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public boolean saveTags(ArrayList list, String category, int userid){
		boolean bool = false;
		try{
			int parentId = 0;
			String query = "select TagItemId from tagitem where Name='"+category+"' and IndustryId="+getIndustryid();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()){
				parentId = rs.getInt("TagItemId");
			}
			else{
				int categoryparentId=0;
				String query3 = "select TagItemId from tagitem where Name='"+getNewscentername()+"'";
				stmt = conn.createStatement();
				ResultSet rs3 = stmt.executeQuery(query3);
				while(rs3.next()){
					categoryparentId = rs3.getInt("TagItemId");
				}
				rs3.close();
				String query1 = "insert into tagitem(Name,ParentId,IndustryId,UserId) values('"+category+"',"+categoryparentId+","+getIndustryid()+","+userid+")";
				stmt = conn.createStatement();
				stmt.executeUpdate(query1);
				
				String query2 = "select TagItemId from tagitem where Name='"+category+"'";
				stmt = conn.createStatement();
				ResultSet rs2 = stmt.executeQuery(query2);
				while(rs2.next()){
					parentId = rs2.getInt("TagItemId");
				}
				rs2.close();
			}
			
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				TagItemInformation tagitem = (TagItemInformation)iter.next();
				
				String query1 = "select * from tagitem where Name='"+tagitem.getTagName()+"' and ParentId="+parentId+" and IndustryId="+tagitem.getIndustryId();
				stmt = conn.createStatement();
				ResultSet rs1 = stmt.executeQuery(query1);
				if(rs1.next() != true){
					String query2 = "insert into tagitem(Name,ParentId,IndustryId,UserId) values('"+tagitem.getTagName()+"',"+parentId+","+tagitem.getIndustryId()+","+userid+")";
					stmt = conn.createStatement();
					stmt.executeUpdate(query2);
				}
				rs1.close();
			}
			bool = true;
			rs.close();
			stmt.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
			bool = false;
		}
		return bool;
	}

	public int getIndustryid() {
		return industryid;
	}

	public void setIndustryid(int industryid) {
		this.industryid = industryid;
	}

	public String getNewscentername() {
		return newscentername;
	}

	public void setNewscentername(String newscentername) {
		this.newscentername = newscentername;
	}
}