package com.lighthouse.report.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lighthouse.report.server.helper.ReportsHelper;

public class ReportDisplayServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		int reportId = Integer.parseInt(req.getParameter("reportid"));
		ReportsHelper helper = new ReportsHelper();

		ArrayList<Object> downloadFileInfo = helper.getFileToDownload(reportId);

		InputStream dwnloadFis = (InputStream) downloadFileInfo.get(0);
		String mimetype = (String) downloadFileInfo.get(1);

		InputStream inp = dwnloadFis;

		streamBinaryData(inp, mimetype, resp, reportId);
	}

	private void streamBinaryData(InputStream is, String format,
			HttpServletResponse resp, int reportId) {

		ServletOutputStream outstr = null;
		String ErrorStr = null;

		try {

			// if (format.contains("text/html")) {
			if (format.matches("html") || format.matches("txt")) {

				resp.setContentType("text/html");
				if (is != null) {
					InputStreamReader isr = new InputStreamReader(is);
					BufferedReader reader = new BufferedReader(isr);
					PrintWriter pw = resp.getWriter();
					pw.println("<html><head><title>Report Display</title></head><body bgcolor='white'></body></html>");
					pw.println("<a href =/com.lighthouse.report.report/DownloadReport?reportId="
							+ reportId
							+ " ><img src=/images/downloadimgbig.png /></a>");

					String text = "";

					while ((text = reader.readLine()) != null) {
						pw.println("<h2><i><b>" + text + "</b></i></b><br>");
					}

				}

			} else {
				outstr = resp.getOutputStream();

				/*
				 * StringTokenizer st = new StringTokenizer(format, "/"); String
				 * applicationName = st.nextToken(); String extension =
				 * st.nextToken(); resp.setContentType(format);
				 * 
				 * if (extension.matches("pdf")) {
				 * resp.setHeader("Content-disposition", "inline; filename=\"" +
				 * "NewsReport.pdf" + "\""); } else if
				 * (extension.matches("vnd.ms-powerpoint")) {
				 * resp.setHeader("Content-disposition", "inline; filename=\"" +
				 * "NewsReport.ppt" + "\""); } else if
				 * (extension.matches("gif")) {
				 * resp.setHeader("Content-disposition", "inline; filename=\"" +
				 * "NewsReport.gif" + "\""); } else if
				 * (extension.matches("vnd.ms-excel")) {
				 * resp.setHeader("Content-disposition", "inline; filename=\"" +
				 * "NewsReport.xls" + "\""); } else if
				 * (extension.matches("msword")) {
				 * resp.setHeader("Content-disposition", "inline; filename=\"" +
				 * "NewsReport.doc" + "\""); }else{
				 * resp.setHeader("Content-disposition", "inline; filename=\"" +
				 * "NewsReport."+extension + "\""); }
				 */

				if (format.matches("pdf")) {
					resp.setContentType("application/pdf");
					resp.setHeader("Content-disposition", "inline; filename=\""
							+ "NewsReport." + format + "\"");
				} else if (format.matches("ppt") || format.matches("pptx")) {
					resp.setContentType("application/vnd.ms-powerpoint");
					resp.setHeader("Content-disposition", "inline; filename=\""
							+ "NewsReport." + format + "\"");
				} else if (format.matches("gif") || format.matches("jpg")
						|| format.matches("jpeg")) {
					resp.setContentType("image/gif" );
					resp.setHeader("Content-disposition", "inline; filename=\""
							+ "NewsReport.gif"  + "\"");
				} else if (format.matches("xls")) {
					resp.setContentType("application/vnd.ms-excel");
					resp.setHeader("Content-disposition", "inline; filename=\""
							+ "NewsReport." + format + "\"");
				} else if (format.matches("doc") || format.matches("docx")) {
					resp.setContentType("application/msword");
					resp.setHeader("Content-disposition", "inline; filename=\""
							+ "NewsReport." + format + "\"");
				}	else if(format.matches("unknown")){
					resp.setContentType("application/octet-stream");
					resp.setHeader("Content-disposition", "attachment; filename=\""
							+ "NewsReport" + "\"");
				} 
				else {
					resp.setContentType("application/" + format);
					resp.setHeader("Content-disposition", "inline; filename=\""
							+ "NewsReport." + format + "\"");
				}

				BufferedInputStream bis = null;

				BufferedOutputStream bos = null;

				try {

					int length = is.available();

					resp.setContentLength(length);

					InputStream in = is;

					bis = new BufferedInputStream(in);
					bos = new BufferedOutputStream(outstr);
					byte[] buff = new byte[length];
					int bytesRead;
					while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {

						bos.write(buff, 0, bytesRead);
					}

				} catch (Exception e) {
					e.printStackTrace();
					ErrorStr = "Error Streaming the Data";
					outstr.print(ErrorStr);
				} finally {

					if (bis != null) {
						bis.close();
					}
					if (bos != null) {
						bos.close();
					}
					if (outstr != null) {
						outstr.flush();
						outstr.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * @Override protected void doPost(HttpServletRequest req,
	 * HttpServletResponse resp) throws ServletException, IOException { // TODO
	 * Auto-generated method stub // super.doPost(req, resp);
	 * 
	 * int reportId = Integer.parseInt(req.getParameter("reportid"));
	 * ReportsHelper helper = new ReportsHelper();
	 * 
	 * ArrayList<Object> downloadFileInfo = helper.getFileToDownload(reportId);
	 * 
	 * InputStream dwnloadFis = (InputStream) downloadFileInfo.get(0); String
	 * mimetype = (String) downloadFileInfo.get(1);
	 * 
	 * ServletOutputStream out = resp.getOutputStream();
	 * 
	 * resp.setContentType("application/pdf");
	 * resp.setHeader("Content-disposition", "attachment; filename=" +
	 * "Example.pdf");
	 * 
	 * String fileURL = "file:///tmp/example.pdf";
	 * 
	 * URL url = new URL(fileURL);
	 * 
	 * BufferedInputStream bis = new BufferedInputStream(url.openStream());
	 * BufferedOutputStream bos = new BufferedOutputStream(out); byte[] buff =
	 * new byte[2048]; int bytesRead; // Simple read/write loop. while (-1 !=
	 * (bytesRead = bis.read(buff, 0, buff.length))) { bos.write(buff, 0,
	 * bytesRead); }
	 * 
	 * bos.close(); out.close(); }
	 */
}
