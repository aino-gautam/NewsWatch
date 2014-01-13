package com.newscenter.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.login.server.db.AllocateResources;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

public class RSSFeedServlet extends HttpServlet {
	private static final String MIME_TYPE = "application/xml; charset=UTF-8";
	private static final long serialVersionUID = 1L;
	private String newscatalystname;
	private String url;
	private static final String COULD_NOT_GENERATE_FEED_ERROR = "Could not generate feed";
	private static DateFormat DATE_PARSER = new SimpleDateFormat("EEEE dd MMMM yyyy");
	private Connection conn = null;
       
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try{
			ServletContext context=getServletContext();
			String connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
			String driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
			String username =(String)context.getAttribute(AllocateResources.USERNAME);
			String password =(String)context.getAttribute(AllocateResources.PASSWORD);
			getServletContext().log("Returning connection for RSSFeedServlet");
			Driver drv =(Driver)Class.forName(driverClassName).newInstance();
			DriverManager.registerDriver(drv);
			conn=DriverManager.getConnection(connectionUrl,username,password);
			Statement stmt = conn.createStatement();
			SyndFeed feed = null;	
		    String ncname = req.getParameter("NCname");
			String email = req.getParameter("e");
			int ncid = 0;
			String query;
			if(ncname.equalsIgnoreCase("solarpv")){
				ncid = 5;
				newscatalystname="Solar PV";
				url = "http://solarpv.newscatalyst.com";
			}
			if(ncname.equalsIgnoreCase("offshorewind")){
				ncid = 4;
				newscatalystname="Offshore Wind";
				url = "http://offshorewind.newscatalyst.com";
			}
			if(ncname.equalsIgnoreCase("greencar")){
				ncid = 6;
				newscatalystname="Green Car";
				url = "http://greencar.newscatalyst.com";
			}
			if(ncname.equalsIgnoreCase("solarthermal")){
				ncid = 7;
				newscatalystname="Solar Thermal";
				url = "http://solarthermal.newscatalyst.com";
			}
			if(ncname.equalsIgnoreCase("oncology")){
				ncid = 8;
				newscatalystname="Oncology";
				url = "http://oncology.newscatalyst.com";
			}
			if(ncname.equalsIgnoreCase("wastemanagement")){
				ncid = 9;
				newscatalystname="Wastemanagement";
				url = "http://beta.newscatalyst.com";
			}	
			if(ncname.equalsIgnoreCase("alpha")){
				ncid = 10;
				newscatalystname="Alpha";
				url = "http://alpha.newscatalyst.com";
			}		
			if(ncname.equalsIgnoreCase("gamma")){
				ncid = 11;
				newscatalystname="Gamma";
				url = "http://gamma.newscatalyst.com";
			}	
			
			query = "select u.isAdmin,us.isSubscribed,u.IndustryEnumId,i.Name from user as u, usersubscription as us, industryenum as i where u.email = '"+email+"' and u.UserId = us.UserId and u.IndustryEnumId = i.IndustryEnumId and i.IndustryEnumId = us.NewsCenterId";
			ResultSet rs = stmt.executeQuery(query);
			if(rs.next()){
				int isadmin = rs.getInt("isAdmin");
				boolean issubscribed = rs.getBoolean("isSubscribed");
				int industryenumid = rs.getInt("IndustryEnumId");
				String newscatalystname = rs.getString("Name");
				String msg = null;
				
				if(issubscribed){
				  	
					if(isadmin == 1){ //admin
							feed = getFeed(req, ncid, this.newscatalystname);
					
					}
		            
					if(isadmin == 0 || isadmin == 3 || isadmin == 2){ //normal user or sales executive or trial
					   if(ncid == industryenumid){
						   feed = getFeed(req, ncid, newscatalystname);
					   }
					   else{
						   msg = COULD_NOT_GENERATE_FEED_ERROR+". You are not subscribed to "+this.newscatalystname.toLowerCase()+" newscatalyst.";
						   createerrormesg(resp, ncname, msg);
					   }
					}
					
					if(msg == null){
					String feedType = "atom_0.3";
		            feed.setFeedType(feedType);

		            resp.setContentType(MIME_TYPE);
		            SyndFeedOutput output = new SyndFeedOutput();
		            output.output(feed,resp.getWriter());
					}
				}
				else{
					msg = COULD_NOT_GENERATE_FEED_ERROR+". User not subscribed to "+this.newscatalystname.toLowerCase()+" newscatalyst.";
		            createerrormesg(resp, ncname, msg);
				}
			}
			else{
				 createerrormesg(resp, ncname, COULD_NOT_GENERATE_FEED_ERROR+". Please enter correct details.");
			}
			conn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
				
	public String stripNonValidXMLCharacters(String in) {
        StringBuffer out = new StringBuffer();
        char current;

        if (in == null || ("".equals(in))) return "";
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i);
            if ((current == 0x9) ||
                (current == 0xA) ||
                (current == 0xD) ||
                ((current >= 0x20) && (current <= 0xD7FF)) ||
                ((current >= 0xE000) && (current <= 0xFFFD)) ||
                ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }    
		
	 protected SyndFeed getFeed(HttpServletRequest req,int ncid,String ncname) throws IOException,FeedException, Exception {
	    	String newsTitle;
			String url;
			String newsAbstract;
			Date publisheddate;
			String source;
	    		    	
	        SyndFeed feed = new SyndFeedImpl();
            feed.setTitle(ncname+" NewsCatalyst");
	        feed.setLink(this.url);
	        feed.setDescription("This feed has been created for "+ncname.toLowerCase()+" newscatalyst");
	       /* SyndImage syndImage = new SyndImageImpl();
	        syndImage.setLink("http://122.169.111.248:8090/NewsCenter/images/favicon.ico");
	        syndImage.setUrl("http://122.169.111.248:8090/NewsCenter/images/favicon.ico");
	        feed.setImage(syndImage);*/
	        List entries = new ArrayList();
	        SyndEntry entry;
	        SyndContent description;
	        SyndFeed syncfeed;
	        
	        try{
	        Statement stmt = conn.createStatement();
	        String query = "SELECT distinct n.Title,n.Abstract,n.URL,n.ItemDate,n.Source from newsitem as n, newstagitem as nt where n.NewsItemId = nt.NewsItemId and nt.IndustryEnumId = "+ncid+" order by n.ItemDate desc";
	        ResultSet rs = stmt.executeQuery(query);
	        while(rs.next()){
			       newsTitle = new String(rs.getString("Title").getBytes("utf-8"),"utf-8");
			       url = rs.getString("url");
			       newsAbstract = stripNonValidXMLCharacters(new String(rs.getString("Abstract").getBytes("utf-8"),"utf-8"));
			       publisheddate = rs.getDate("ItemDate");
			       source = rs.getString("Source");
			       if(source.equals("")){
			    	   source = "-";
			       }
			       entry = new SyndEntryImpl();
			       entry.setTitle(newsTitle);
			       entry.setLink(url);
			       entry.setPublishedDate(null);
			        
			       description = new SyndContentImpl();
			       description.setType("text/html");
			       description.setValue(newsAbstract+"<br><b><font color=\"#666\">   Source:</font></b> <i>"+source+"</i>&nbsp; <b><font color=\"#666\">Published Date:</font></b> "+DATE_PARSER.format(publisheddate));
			       entry.setDescription(description);
			       entries.add(entry);
	        }
	        }
	        catch(Exception e){
	        	e.printStackTrace();
	        }
	        
	        feed.setEntries(entries);
          
	        return feed;
	    }
	 
	 private void createerrormesg(HttpServletResponse resp, String ncname, String mesg){
		 try{
			 resp.setContentType("text/html");
			 PrintWriter out = resp.getWriter();
			 out.println("<html><head>");

		    out.println("<title>"+ncname+"</title></head><body>");
		    out.println("<h2>"+mesg+"</h2>");
		    out.println("</body></html>");
		 }
		 catch(Exception e){
			 e.printStackTrace();
		 }
	 }

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
