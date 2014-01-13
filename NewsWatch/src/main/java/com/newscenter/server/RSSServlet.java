package com.newscenter.server;

import com.login.server.db.AllocateResources;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedOutput;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RSSServlet extends HttpServlet {
    private static final String MIME_TYPE = "application/xml; charset=UTF-8";
    private static final String COULD_NOT_GENERATE_FEED_ERROR = "Could not generate feed";

    private static final DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");

    private String _defaultFeedType;
    
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)	throws ServletException, IOException {
		try {
            SyndFeed feed = null;
			try {
				feed = getFeed(req);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            String feedType = "atom_0.3";
            feed.setFeedType(feedType);

            resp.setContentType(MIME_TYPE);
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed,resp.getWriter());
        }
        catch (FeedException ex) {
            String msg = COULD_NOT_GENERATE_FEED_ERROR;
            log(msg,ex);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,msg);
        }
    }

    protected SyndFeed getFeed(HttpServletRequest req) throws IOException,FeedException, Exception {
    	ServletContext context=getServletContext();
		String connectionUrl =(String)context.getAttribute(AllocateResources.DBCONNECTIONURL);
		String driverClassName =(String)context.getAttribute(AllocateResources.DRIVERCLASSNAME);
		String username =(String)context.getAttribute(AllocateResources.USERNAME);
		String password =(String)context.getAttribute(AllocateResources.PASSWORD);
		getServletContext().log("Returning connection for Newsletter Servlet");
		Driver drv =(Driver)Class.forName(driverClassName).newInstance();
		DriverManager.registerDriver(drv);
		Connection conn=DriverManager.getConnection(connectionUrl,username,password);
		Statement stmt = conn.createStatement();
		String newsTitle;
		String url;
		String newsAbstract;
		Date publisheddate;
		String source;
    	
    	
    	
        SyndFeed feed = new SyndFeedImpl();

        feed.setTitle("OffshoreWind NewsCatalyst");
        feed.setLink("http://122.169.111.248:8090/NewsCenter/index.html");
        feed.setDescription("This feed has been created for offshorewind newscatalyst");

        List entries = new ArrayList();
        SyndEntry entry;
        SyndContent description;
        
        
        
        try{
        String query = "SELECT distinct n.Title,n.Abstract,n.URL,n.ItemDate,n.Source from newsitem as n, newstagitem as nt where n.NewsItemId = nt.NewsItemId and nt.IndustryEnumId = 4 order by n.ItemDate desc";
        ResultSet rs = stmt.executeQuery(query);
        while(rs.next()){
		       newsTitle = new String(rs.getString("Title").getBytes("utf-8"),"utf-8");
		       url = rs.getString("url");
		       newsAbstract = new String(rs.getString("Abstract").getBytes("utf-8"),"utf-8");
		       publisheddate = rs.getDate("ItemDate");
		       source = rs.getString("Source");
		       entry = new SyndEntryImpl();
		       entry.setTitle(newsTitle);
		       entry.setLink(url);
		       entry.setPublishedDate(DATE_PARSER.parse(publisheddate.toString()));
		       description = new SyndContentImpl();
		       description.setType("text/plain");
		       description.setValue(newsAbstract);
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
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
