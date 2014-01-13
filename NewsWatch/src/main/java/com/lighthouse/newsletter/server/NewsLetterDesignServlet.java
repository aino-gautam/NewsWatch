package com.lighthouse.newsletter.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.login.client.UserInformation;

public class NewsLetterDesignServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(NewsLetterDesignServlet.class.getName());
	String type;
	int newsCenterId;
	String xml = null;
	InputStream inputstream ;
	int filelength;
	String tomcatPath;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.log(Level.INFO," [----  In NewsLetterDesignServlet      ----] :");

		HttpSession session = req.getSession(true);
		UserInformation userInfo = (UserInformation) session.getAttribute("userInfo");
		newsCenterId = userInfo.getUserSelectedNewsCenterID();
		tomcatPath = (String) session.getServletContext().getAttribute("tomcatpath");
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		try {
			List items = upload.parseRequest(req);
			Iterator it = items.iterator();
			LhNewsletterHelper helper = new LhNewsletterHelper();
			while (it.hasNext()) {
				FileItem item = (FileItem) it.next();
				if (item.isFormField())
					processFormField(item);
				else
					processUploadedFile(item);
			}
			if (type.equals("header")){
				String url = getLogoUrl();
				updateXmlwithlogoImgUrl(xml,url,type);
				helper.saveNewsLetterHeaderConfig(newsCenterId, xml);
				helper.closeConnection();
			}
			else if (type.equals("outline")){
				String url = getLogoUrl();
				updateXmlwithlogoImgUrl(xml,url,type);
				helper.saveNewsletterOutlineConfig(newsCenterId, xml);
				helper.closeConnection();
			}
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
	}

	private String getLogoUrl() {
		LhNewsletterHelper helper = new LhNewsletterHelper();
		String imgUrl = null;
		if(type.equals("header"))
			imgUrl = helper.getImageURL(newsCenterId, "logo",tomcatPath, true);
		else
			imgUrl = helper.getImageURL(newsCenterId, "bgImg",tomcatPath, true);
		helper.closeConnection();
		return imgUrl;
	}

	public void processFormField(FileItem item) {
		try {
			String name = item.getFieldName();
			if (name.equals("xml"))
				xml = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
			if (name.equals("type"))
				type = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void processUploadedFile(FileItem item) throws IOException {
		inputstream = item.getInputStream();
		filelength = (int) item.getSize();
		LhNewsletterHelper helper = new LhNewsletterHelper();
		helper.saveNewsletterLogoImage(newsCenterId,inputstream,filelength,type);
		if (type.equalsIgnoreCase("siloLogo")){
			String imgurl = helper.getImageURL(newsCenterId, "siloLogo",tomcatPath, false);
			getServletContext().setAttribute("siloLogo", imgurl);
		}else if(type.equalsIgnoreCase("header"))
			helper.getImageURL(newsCenterId, "logo",tomcatPath, false);
		else
			helper.getImageURL(newsCenterId, "bgImg",tomcatPath, false);
		
		helper.closeConnection();
	}

	private void updateXmlwithlogoImgUrl(String xml2, String url, String type2) {
		if (type2.equals("header")){
			HeaderDesignConfiguration headerConfig = new HeaderDesignConfiguration(xml2);
			headerConfig.setLogoImageUploadUrl(url);
			xml = headerConfig.getDomAsString();
		}
		else if (type2.equals("outline")){
			OutlineDesignConfiguration config = new OutlineDesignConfiguration(xml2);
			config.setTableBackgroundImageUrl(url);
			xml = config.getDomAsString();
		}
	}
}
