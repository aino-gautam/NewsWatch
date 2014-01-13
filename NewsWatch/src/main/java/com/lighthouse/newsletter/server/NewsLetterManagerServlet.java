package com.lighthouse.newsletter.server;

/**
 * author sachin@ensarm.com
 */
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NewsLetterManagerServlet extends HttpServlet {
	
	Logger logger=Logger.getLogger(NewsLetterManagerServlet.class.getName());
	HashMap<Integer, NewsLetterSchedular> NcSchedularMap=null;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try{
			logger.log(Level.INFO," [----  In NewsLetterManagerServlet      ----] :");
			NcSchedularMap=(HashMap<Integer, NewsLetterSchedular>) this.getServletContext().getAttribute("NcSchedularMap");
			
			if( NcSchedularMap==null){
			
				NcSchedularMap=new HashMap<Integer, NewsLetterSchedular>();	
				LhNewsletterHelper newsletterHelper =new LhNewsletterHelper();
				ArrayList<Integer> newsCenterList=newsletterHelper.getNewsCenterList();
				
				for(Integer ncid:newsCenterList){
					
					NewsLetterSchedular schedular=new NewsLetterSchedular();
					NcSchedularMap.put(ncid,schedular);
					schedular.handleScheduling(ncid, req);
					
				}
				this.getServletContext().setAttribute("NcSchedularMap", NcSchedularMap);
				newsletterHelper.closeConnection();
			}
			else{
				int ncid=Integer.parseInt(req.getParameter("ncid"));
				NcSchedularMap=(HashMap<Integer, NewsLetterSchedular>) this.getServletContext().getAttribute("NcSchedularMap");
				NewsLetterSchedular letterSchedular=NcSchedularMap.get(ncid);
				letterSchedular.getTimer().cancel();
				letterSchedular.handleScheduling(ncid, req);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

}
