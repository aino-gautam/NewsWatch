package com.lighthouse.newsletter.server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lighthouse.newsletter.client.domain.NewsLetterStats;
import com.lighthouse.utils.server.EncrypterDecrypter;

public class NewsLetterStatsServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Logger logger = Logger.getLogger(NewsLetterStatsServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: doGet() initated]");
		String stringToDecrypt = req.getParameter("imgId");
		logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: Encrypted String = "+stringToDecrypt);
		try {
			String decryptedString = EncrypterDecrypter.getInstance()
					.getDecryptedString(stringToDecrypt);
			logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: Dencrypted String = "+decryptedString);
			String[] values = decryptedString.split("\\*\\*");
			
			LhNewsletterHelper helper = new LhNewsletterHelper();
			Long ncid = Long.parseLong(values[0]);
			logger.log(Level.INFO, "[ NewsLetterStatsServlet :::NCID = "+ncid);
			Long userid = Long.parseLong(values[1]);
			logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: userid = "+userid);
			String newsSent = values[2];
			if(newsSent.contains(".0")){
				newsSent = newsSent.replace(".0", "");
			}
			logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: newsSent = "+newsSent);
			Date newsAccessed = new Date();
			logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: newsAccessed = "+newsAccessed);
			
			NewsLetterStats stats = new NewsLetterStats(ncid, userid, newsSent,
					newsAccessed);
		
			boolean val = helper.saveNewsletterStats(stats);
			logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: NewsletterStats Saved = "+val);
			if (val) {
				// get image data in bytes and throw it on the browser...
				String realPath = getRealPath();
				logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: the real path for creating image= "+realPath);
				File f2 = new File(realPath + "images/blank.gif");
				InputStream is = new FileInputStream(f2);
				String fileName = "statsimag";
				String mimeType = "image/jpg";
				try {
					int length = 0;
					logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: the image creation started");
					ServletOutputStream op = resp.getOutputStream();
					resp.setHeader("Content-Disposition", "inline; filename=\""
							+ fileName + "\"");
					byte[] bbuf = new byte[is.available()];
					DataInputStream in = new DataInputStream(is);
					while ((in != null) && ((length = in.read(bbuf)) != -1)) {
						op.write(bbuf, 0, length);
					}
					logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: the image created successfully");
					in.close();
					op.flush();
					op.close();
				} catch (Exception e) {
					logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: Error while creating the stats table = "+e);
					e.printStackTrace();
				}
			} else {
				// provide default image
			}
			helper.closeConnection();
		} catch (Exception e) {
			logger.log(Level.INFO, "[ NewsLetterStatsServlet ::: Error while creating the stats table = "+e);
			e.printStackTrace();
			// provide default image
		}
	}
	
	private byte[] getImage(Long ncid) {
		return "".getBytes();
	}

	public String getRealPath() {
		String realpath = getServletContext().getRealPath(
				java.io.File.separator);
		Character ch = realpath.charAt(realpath.length() - 1);
		String lastCharfromRealpath = ch.toString();
		String seperator = java.io.File.separator;
		if (!lastCharfromRealpath.equals(seperator)) {
			realpath = realpath.concat(seperator);
		}
		return realpath;
	}
}
