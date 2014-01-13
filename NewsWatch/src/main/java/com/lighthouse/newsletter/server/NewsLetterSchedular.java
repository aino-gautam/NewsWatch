package com.lighthouse.newsletter.server;
/**
 * author sachin@ensarm.com
 */
import java.util.Date;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.time.DateUtils;

public class NewsLetterSchedular {

	private Timer timer;
	Logger logger=Logger.getLogger(NewsLetterSchedular.class.getName());
	public Timer getTimer() {
		return timer;
	}

		
	public Boolean handleScheduling(int ncid, HttpServletRequest req){
		
		try{
			timer=new Timer();
			TimerTask task=manageDelivery(ncid,req);
			if(task!=null){
			timer.schedule(task, 0,180000); // 3 Min Interval
			return true;
			}
			return false;
		}catch (Exception e) {
			e.printStackTrace();
			
		}
		return false;
	}

	private TimerTask manageDelivery(final int ncid,final HttpServletRequest req) throws SQLException {
		
		LhNewsletterHelper helper=new LhNewsletterHelper();
		Date lastTimeOfDelivery=helper.getLastNewsLetterDelivery(ncid); 
		Date today =new Date();
		
		Boolean isSameDay=DateUtils.isSameDay(today, lastTimeOfDelivery);
		
		if(!isSameDay){
		
		StringBuffer reqURL=req.getRequestURL();
		String reqHost=(String)reqURL.subSequence(0,reqURL.length()-(req.getServletPath().length()));
		final String url1=reqHost+"/sendNewsLetter";
		final Time timeOfDelivery=helper.getNewsLetterTimeOfDelivery(ncid);
		helper.closeConnection();
		
		logger.log(Level.INFO,"NewsLetter URL:"+url1);
		
		TimerTask task=new TimerTask() {
			
			@Override
			public void run() {
			
				logger.log(Level.INFO," --  Running Schedular for NewsCenter-- :"+ncid);
				try{
				
				Date currentDate = new Date();
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				String date =dateFormat.format(currentDate);
				String []dateString=date.split(" ");
				String []timeString=dateString[1].split(":");
				int hh=Integer.parseInt(timeString[0]);
				int mm=Integer.parseInt(timeString[1]);
				int ss=Integer.parseInt(timeString[0]);
				Time now=new Time(hh, mm, ss);
				
				Long timeDiff = now.getTime()-timeOfDelivery.getTime()  ;
				
				logger.log(Level.INFO," ---- Time Difference -----:"+timeDiff);
				
				
				if(timeDiff>(-180000)&&timeDiff<180000){ //3 MIN
					try {
						synchronized (timer) { 
							
							
							GetMethod get = new  GetMethod(url1);
							get.setDoAuthentication(true);
							HttpClient httpClient=new HttpClient();
							httpClient.executeMethod(get);
							get.releaseConnection();
							logger.log(Level.INFO,"[-- NewsLetter Send --] ");
							LhNewsletterHelper helper=new LhNewsletterHelper();
							helper.saveNewsletterDelivery(ncid, date);
							helper.closeConnection();
							timer.wait(82800000); //23 HR
						}	
						
					} catch (Exception e) {
						e.printStackTrace();
					} 
				}
				}//
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		task.run();
		return task;
	}
		return null;	
	}
	
}
