package com.lighthouse.feed.server;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
 
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.lighthouse.feed.client.domain.Feed;
import com.lighthouse.feed.client.domain.FeedNewsItem;
import com.lighthouse.feed.client.domain.SiloFeed;

public class FeedItemUploadServlet extends HttpServlet {
   
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean isMultipart;
    private String filePath="";
  // private int maxFileSize = 200 * 1024;
    private int maxMemSize = 4 * 1024;
    private File file ;
    public Logger logger = Logger.getLogger(FeedItemUploadServlet.class.getName());

   @Override
	public void init() throws ServletException {
		ServletContext context=getServletContext();
		//String tomcatUrl=context.getInitParameter("tomcaturl"); 
		filePath= context.getRealPath("/")+"config/";
	
	}
   
   public void doPost(HttpServletRequest req,HttpServletResponse response)throws ServletException, java.io.IOException {
	   
	   String ncid=req.getParameter("NCID");
	   String ncName=req.getParameter("ncName");
	   
	  // Check that we have a file upload request
      isMultipart = ServletFileUpload.isMultipartContent(req);
      response.setContentType("text/html");
      java.io.PrintWriter out = response.getWriter( );
     
      DiskFileItemFactory factory = new DiskFileItemFactory();
      // maximum size that will be stored in memory
      factory.setSizeThreshold(maxMemSize);
      // Location to save data that is larger than maxMemSize.
      //factory.setRepository(new File("/root/Desktop/sachin.xml"));

      // Create a new file upload handler
      ServletFileUpload upload = new ServletFileUpload(factory);
      // maximum file size to be uploaded.
     // upload.setSizeMax( maxFileSize );

      try{ 
      // Parse the request to get file items.
      List fileItems = upload.parseRequest(req);
	
      // Process the uploaded file items
      Iterator i = fileItems.iterator();

/*      out.println("<html>");
      out.println("<head>");
      out.println("<title>Servlet upload</title>");  
      out.println("</head>");
      out.println("<body>");*/
      while ( i.hasNext () ) 
      {
         FileItem fi = (FileItem)i.next();
         if ( !fi.isFormField () )	
         {
            // Get the uploaded file parameters
            String fieldName = fi.getFieldName();
            String fileName = fi.getName();
            String contentType = fi.getContentType();
            boolean isInMemory = fi.isInMemory();
            long sizeInBytes = fi.getSize();
         /*   if( sizeInBytes==0 && fileName.equals("")){
                out.println("<html>");
                out.println("<head>");
                out.println("<title>Servlet upload</title>");  
                out.println("</head>");
                out.println("<body>");
                out.println("<p>No file uploaded</p>"); 
                out.println("</body>");
                out.println("</html>");
                return;
             }*/
            String content=new String(fi.get());
            System.out.print(content);
           
            // Write the file
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yy_hh:mm:ss");
            String dateString=formatter.format(new Date());
           
            String path=null;
            
            if( fileName.lastIndexOf("\\") >= 0 ){
            	path=filePath +"/"+dateString+"_"+fileName.substring( fileName.lastIndexOf("\\"));
                file = new File(path) ;
                logger.log(Level.INFO, "File Path :"+path);
            }else{
            	path=filePath +"/"+dateString+"_"+fileName.substring(fileName.lastIndexOf("\\")+1);
                file = new File(path) ;
                logger.log(Level.INFO, "File Path :"+path);
            }
            fi.write( file ) ;
           // out.println("Uploaded Filename: " + fileName + "<br>");
            
            FactivaFeedProcessor.FEEDFILENAME=path;
            FactivaFeedProcessor feedProcessor=new FactivaFeedProcessor();
            XMLConfigFileParser parser=new XMLConfigFileParser();
            SiloFeed siloFeed=parser.getSiloFeed("XML stream from Factiva");
            Feed feed=siloFeed.getFeedUrlList().get(0);
            ArrayList<FeedNewsItem> newsItemsList=feedProcessor.parseContent(content,feed.getFeedName());
            FeedHelper  feedHelper=new FeedHelper();
            feedHelper.saveNewsFeed(newsItemsList, siloFeed.getNcid(), siloFeed.getFeedUserId(), feed);
         }
      }
    /*  out.println("</body>");
      out.println("</html>");*/
      StringBuffer reqURL=req.getRequestURL();
      req.getAttributeNames();
	  String reqHost=(String)reqURL.subSequence(0,reqURL.length()-(req.getServletPath().length()));
	  
	  //response.sendRedirect(reqHost+"/ReviewFeed.html?gwt.codesvr=127.0.0.1:9997&NCID="+ncid+"&ncName="+ncName+"");
	  response.sendRedirect(reqHost+"/ReviewFeed.html?NCID="+ncid+"&ncName="+ncName+"");
   }catch(Exception ex) {
      ex.printStackTrace();
   }
   }
   public void doGet(HttpServletRequest request, 
                       HttpServletResponse response)
        throws ServletException, java.io.IOException {
        
        throw new ServletException("GET method used with " +
                getClass( ).getName( )+": POST method required.");
   } 
}