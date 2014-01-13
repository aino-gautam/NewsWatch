package com.lighthouse.feed.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UploadRequestServlet extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	
		try{
			String ncid=req.getParameter("NCID");
			String ncName=req.getParameter("ncName");
				StringBuilder builder =new StringBuilder();
		         builder.append("<html>");
		         builder.append("<head>");
		         builder.append("<title>File Uploading Form</title>");
		         builder.append("<script language=\"javascript\" type=\"text/javascript\">");
		         builder.append("function validateField(){");
		         builder.append("var x=document.forms[\"myForm\"][\"file\"].value;");
		         builder.append("if (x==null || x==\"\"){");
		         builder.append("alert(\"File has not been chosen.\");");
		         builder.append("return false;}"); 
		         builder.append("}");		
		         builder.append("</script>");		
		         builder.append("</head>");
		         builder.append("<body>");
		         builder.append("<h3>File Upload:</h3>"); 
		         builder.append("Select a file to upload: <br />");
		         builder.append("<form name=\"myForm\" action=\"com.lighthouse.feed.feed/uploadFeed?NCID="+ncid+"&ncName="+ncName+"\" method=\"post\" onsubmit=\"return validateField();\" enctype=\"multipart/form-data\">");
		         builder.append("<input type=\"file\" name=\"file\" size=\"50\" /><br><br>");
		         builder.append("<input type=\"submit\" value=\"Upload File\" /></form>");
		         builder.append("</body>");
		         builder.append("</html>");
		         
		         resp.getWriter().write(builder.toString());
		        
		        
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(req, resp);
	}

}
