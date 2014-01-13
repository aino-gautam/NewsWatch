package com.newscenter.server.db;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import com.admin.client.UserAdminInformation;
import com.admin.server.UserInformationforMail;
import com.login.client.UserInformation;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
import com.newscenter.server.categorydb.ItemProviderServiceImpl;
/**** This class is extended by Dbhelper for getting connection from db****/
public class NewsletterHelper extends DBHelper {
	
	
	StringBuilder stringBuilder;
	String smtpusername,smtppassword,smtphost,smtpport,tomcaturl,deploymentname;
	protected ServletConfig config;
	
	public NewsletterHelper(ServletContext context,ServletConfig config){
		smtpusername = context.getInitParameter("smtpusername");
		smtppassword = context.getInitParameter("smtppassword");
		smtphost = context.getInitParameter("smtphost");
		smtpport = context.getInitParameter("smtpport");
		tomcaturl = context.getInitParameter("tomcaturl");
		deploymentname = context.getInitParameter("deploymentname");
		this.config = config;
	}
	
	public void createLayout(UserInformation user){
		stringBuilder = new StringBuilder();
		stringBuilder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		stringBuilder.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		stringBuilder.append("<head>");
		stringBuilder.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">");
		stringBuilder.append("</head>");
		stringBuilder.append("<body style=\"border: thin solid rgb(222,222,222);padding:5px 5px 5px 5px; background-color: white;\">");
		stringBuilder.append("<h3 style='font-family:Arial;font-size:11pt;color:black;margin-left: 5px;text-align:left'>"+user.getIndustryNewsCenterName()+" NewsCatalyst </h3>");
		stringBuilder.append("<table width=\"100%\">");
	}
	
	public void endLayout(boolean bool,UserInformation user){
		stringBuilder.append("</table>");
		if(bool)
			stringBuilder.append("<p style=\"font-family:Arial;font-size:8pt;margin-left: 5px;color:rgb(0,102,202);line-height: 250%;\"> Please be aware that you have not yet configured your newsletter from the "+user.getIndustryNewsCenterName()+" Newscatalyst</p>");
		stringBuilder.append("<p style=\"font-family:Arial;font-size:8pt;margin-left: 5px;color:rgb(0,102,202);line-height: 250%;\"> To adjust filters for your newsletter or to deactivate this alert please click <a href='"+user.getUserSelectedNewsCenterURL()+"' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:rgb(0,102,202)\"> here </a> </p>");
		stringBuilder.append("<p style=\"font-family:Arial;font-size:8pt;margin-left: 5px; padding-bottom: 15px;color:rgb(0,102,202);word-wrap:break-word;\"> For questions regarding the newsletter please contact "+user.getIndustryNewsCenterName()+" principal analyst Miriam Baurichter at +4586204422 or mb@aalundnetsearch.com </p>");
		stringBuilder.append("</body>");
		stringBuilder.append("</html>");
	}
	
	public void sendMail(UserInformation user){
		UserAdminInformation adminInformation = new UserAdminInformation();
		//adminInformation.setBodyMail("Hello "+ user.getFirstname()+" "+user.getLastname()+" \n\n\n Your account for the NewsCatalyst "+user.getIndustryNewsCenterName()+" has expired.");
		adminInformation.setBodyMail(stringBuilder.toString());
		adminInformation.setRecipientsMail(user.getEmail());
		adminInformation.setSenderMail("no_reply@newscatalyst.com");
		adminInformation.setSubjectMail(user.getIndustryNewsCenterName() + " Newsletter");
		UserInformationforMail mailService = new UserInformationforMail();
		mailService.setSmtpusername(smtpusername);
		mailService.setSmtppassword(smtppassword);
		mailService.setSmtphost(smtphost);
		mailService.setSmtpport(smtpport);
		mailService.sendMailForApproval(adminInformation);
	}
	
	public void fetchNewsForSingleUser(int userid,Timestamp datetime){
		System.out.println("!*!*!*! IN FETCH NEWS METHOD !*!*!*!*!*!*!");
		ItemProviderServiceImpl itemprovider = new ItemProviderServiceImpl();
		try {
			itemprovider.init(config);
			HashMap map = itemprovider.getAllUserSelectionMaps(datetime,userid);
			getLayout(map);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void fetchNewsForBulkUser(){
		System.out.println("!*!*!*! IN FETCH NEWS METHOD !*!*!*!*!*!*!");
		ItemProviderServiceImpl itemprovider = new ItemProviderServiceImpl();
		try {
			itemprovider.init(config);
			Timestamp datetime = itemprovider.getNewsletterDeliveryTime();
			HashMap map = itemprovider.getAllUserSelectionMaps(datetime,-1);
			getLayout(map);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getLayout(HashMap map){
		for(Object obj : map.keySet()){
			UserInformation user = (UserInformation)obj;
			createLayout(user);
			NewsItemList newslist = (NewsItemList)map.get(obj);
				 if(newslist.isNoTagSelected()){
						endLayout(newslist.isNoTagSelected(), user);
						sendMail(user);
					}
				 else if(newslist.size()!=0){
					Iterator iter = newslist.iterator();
					while(iter.hasNext()){
						NewsItems news = (NewsItems)iter.next();
						//System.out.println("NEWS ITEMS: " + news.getNewsTitle());
						createNewsItem(news,user);
					}
					endLayout(newslist.isNoTagSelected(), user);
					sendMail(user);
				}
			}
	}
	
	public void createNewsItem(NewsItems news,UserInformation user){
		stringBuilder.append("<table width=\"100%\">");
		String name = user.getIndustryNewsCenterName();
		String url = user.getUserSelectedNewsCenterURL()+"/"+deploymentname+"/imagefolder/"+news.getNewsId()+".jpg";
		stringBuilder.append("<tr> <td width=\"75%\">"); 
		stringBuilder.append("<a href='"+tomcaturl+"com.login.login/emailitemView?info="+String.valueOf(user.getUserId())+"|"+String.valueOf(user.getUserSelectedIndustryID())+"|"+String.valueOf(news.getNewsId())+"|"+news.getUrl().toString()+"' target=\"new\" style=\"font-family:Arial;color:#0066CA;word-wrap:break-word;padding-left:3px\"><font size=\"2\"><strong class=\"newslink\">"+news.getNewsTitle()+"</strong></font></a></td>");
		if(!news.getImageUrl().equals(""))
			stringBuilder.append("<td width=\"25%\" rowspan=\"5\" valign=\"top\" style=\"padding-left: 30px;\"><img src=\""+url+"\" alt=\"No Image\" width=\"68\" height=\"68\" style=\"border: 1px solid rgb(196,196,196);margin-left:5px;padding: 5px 5px 5px 5px;\"/></td>");
		else
			stringBuilder.append("<td width=\"25%\" rowspan=\"5\"></td>");
		stringBuilder.append("</tr>");
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:9pt;color:black;text-align:left;word-wrap:break-word;padding-left:3px\">"+news.getAbstractNews()+"</td></tr>");
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:8pt;color:rgb(110,110,110);padding-right:5px;padding-left:3px\">PUBLISHED: "+news.getNewsDate()+"<font size = 1pt> | </font> SOURCE: "+news.getNewsSource()+"</td></tr>");
		stringBuilder.append("<tr><td width=\"75%\" style=\"font-family:Arial;font-size:8pt;color:rgb(110,110,110);padding-right:5px;padding-left:3px;padding-bottom:8px;text-align:top;word-wrap:break-word;\">TAGS:"); 
		ArrayList list = news.getAssociatedTagList();
		Iterator iter = list.iterator();
		int count = 0;
		while(iter.hasNext()){
			if(count != list.size()-1){
				TagItem tag = (TagItem)iter.next();
				stringBuilder.append("<a href='' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:black;padding-left:2px;padding-right:2px\">"+tag.getTagName()+"</a>");
				stringBuilder.append("<font size = 1pt> | </font>");	
				count++;
			}
			else{
				TagItem tag = (TagItem)iter.next();
				stringBuilder.append("<a href='' target=\"new\" style=\"font-family:Arial;font-size:8pt;color:black;padding-left:2px;padding-right:2px\">"+tag.getTagName()+"</a>");
				break;
			}
			
		} 
		stringBuilder.append("<p></br></p></td></tr>");
		stringBuilder.append("</table>");
	}

}
