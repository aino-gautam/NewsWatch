package com.lighthouse.report.server;

import java.io.DataInputStream;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.lighthouse.report.server.helper.ReportsHelper;

public class ReportsDownloadServlet extends HttpServlet {
	Logger logger = Logger.getLogger(ReportsDownloadServlet.class.getName());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			String str = req.getParameter("reportId");
			logger.log(Level.INFO, "In doGet() method : reportid : "+str);
			int reportId = Integer.parseInt(str);
			ReportsHelper helper = new ReportsHelper();

			ArrayList<Object> downloadFileInfo = helper
					.getFileToDownload(reportId);

			InputStream dwnloadFis = (InputStream) downloadFileInfo.get(0);
			String format = (String) downloadFileInfo.get(1);
			// String filename = "NewsReports";
	/*		StringTokenizer st = new StringTokenizer(mimetype, "/");
			String applicationName = st.nextToken();
			String extension = st.nextToken();

			if (extension.matches("pdf")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.pdf" + "\"");
			} else if (extension.matches("vnd.ms-powerpoint")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.ppt" + "\"");
			} else if (extension.matches("gif")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.gif" + "\"");
			} else if (extension.matches("vnd.ms-excel")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.xls" + "\"");
			} else if (extension.matches("msword")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.doc" + "\"");
			} else if (extension.matches("html")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.html" + "\"");
			} else {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport"+extension + "\"");
			}
*/
			if (format.matches("pdf")) {
				resp.setContentType("application/pdf");
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			} else if (format.matches("ppt") || format.matches("pptx")) {
				resp.setContentType("application/vnd.ms-powerpoint");
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			} else if (format.matches("gif") || format.matches("jpg")
					|| format.matches("jpeg")) {
				resp.setContentType("image/" + format);
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			} else if (format.matches("xls")) {
				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			} else if (format.matches("doc") || format.matches("docx")) {
				resp.setContentType("application/msword");
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			} else if(format.matches("unknown")){
				resp.setContentType("application/octet-stream");
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport" + "\"");
			}
			else {
				resp.setContentType("application/" + format);
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			}
		/*	try{
				
				//RequestDispatcher dispatcher = req.getRequestDispatcher("http://lighthouse/src/com/lighthouse/report/server/Close.html");
				RequestDispatcher dispatcher = req.getRequestDispatcher("http://www.facebook.com");
			    
				dispatcher.forward(req, resp);
				}
				catch(Exception e){
					e.printStackTrace();
				}*/
			int length = 0;
			ServletOutputStream op = resp.getOutputStream();

		/*	resp.setContentType((mimetype != null) ? mimetype
					: "application/octet-stream");
*/
			resp.setContentLength(dwnloadFis.available());
			/*
			 * resp.setHeader("Content-Disposition", "attachment; filename=\"" +
			 * filename + "\"");
			 */
			byte[] bbuf = new byte[dwnloadFis.available()];
			DataInputStream in = new DataInputStream(dwnloadFis);

			while ((in != null) && ((length = in.read(bbuf)) != -1)) {
				op.write(bbuf, 0, length);

			}
			
			in.close();

			op.flush();
			op.close();

		} catch (Exception e) {
			logger.log(Level.INFO, "In doGet() method : Error : "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "In doGet() method : task completed");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		int reportId = 0;
		logger.log(Level.INFO, "In doPost() method");
		try {
			
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			ArrayList items = null;

			items = (ArrayList) upload.parseRequest(req);
			Iterator iter = items.iterator();
			
		

			while (iter.hasNext()) {
				DiskFileItem item = (DiskFileItem) iter.next();
				if (item.isFormField()) {
			//		String reportIdStr = new String(item.getString().getBytes("ISO-8859-1"),"utf-8");
					String reportIdStr = item.getString("UTF-8");
					String str =new String(item.getString().getBytes("UTF-8"));
					String fieldName = item.getFieldName();
					if(fieldName.matches("reportId")){
						String reportIdStr1 = item.getString("UTF-8");
						reportId = Integer.parseInt(reportIdStr1);
					}
				}
			}

			ReportsHelper helper = new ReportsHelper();

			ArrayList<Object> downloadFileInfo = helper
					.getFileToDownload(reportId);

			InputStream dwnloadFis = (InputStream) downloadFileInfo.get(0);
			String format = (String) downloadFileInfo.get(1);
			// String filename = "NewsReports";
	/*		StringTokenizer st = new StringTokenizer(mimetype, "/");
			String applicationName = st.nextToken();
			String extension = st.nextToken();

			if (extension.matches("pdf")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.pdf" + "\"");
			} else if (extension.matches("vnd.ms-powerpoint")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.ppt" + "\"");
			} else if (extension.matches("gif")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.gif" + "\"");
			} else if (extension.matches("vnd.ms-excel")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.xls" + "\"");
			} else if (extension.matches("msword")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.doc" + "\"");
			} else if (extension.matches("html")) {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport.html" + "\"");
			} else {
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport"+extension + "\"");
			}
*/
			if (format.matches("pdf")) {
				resp.setContentType("application/pdf");
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			} else if (format.matches("ppt") || format.matches("pptx")) {
				resp.setContentType("application/vnd.ms-powerpoint");
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			} else if (format.matches("gif") || format.matches("jpg")
					|| format.matches("jpeg")) {
				resp.setContentType("image/" + format);
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			} else if (format.matches("xls")) {
				resp.setContentType("application/vnd.ms-excel");
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			} else if (format.matches("doc") || format.matches("docx")) {
				resp.setContentType("application/msword");
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			} else if(format.matches("unknown")){
				resp.setContentType("application/octet-stream");
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport" + "\"");
			}
			else {
				resp.setContentType("application/" + format);
				resp.setHeader("Content-disposition", "attachment; filename=\""
						+ "NewsReport." + format + "\"");
			}
			
			int length = 0;
			ServletOutputStream op = resp.getOutputStream();

		/*	resp.setContentType((mimetype != null) ? mimetype
					: "application/octet-stream");
*/
			resp.setContentLength(dwnloadFis.available());
			/*
			 * resp.setHeader("Content-Disposition", "attachment; filename=\"" +
			 * filename + "\"");
			 */
			byte[] bbuf = new byte[dwnloadFis.available()];
			DataInputStream in = new DataInputStream(dwnloadFis);

			while ((in != null) && ((length = in.read(bbuf)) != -1)) {
				op.write(bbuf, 0, length);

			}

			in.close();

			op.flush();
			op.close();

		} catch (Exception e) {
			logger.log(Level.INFO, "In doPost() method: error : "+e);
			e.printStackTrace();
		}
		logger.log(Level.INFO, "In doPost() method: task completed");
	}

}
