package com.newscenter.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

public class NewsItemView extends FlexTable implements ClickHandler,MouseOutHandler,MouseOverHandler  {
	
	private NewsItems newsItem;
	private HorizontalPanel abstractnewspanel = new HorizontalPanel();
	private VerticalPanel vpPanel = new VerticalPanel();
	private HashMap<HTML,NewsItems> mapOfLinkVsNewsitem = new HashMap<HTML, NewsItems>();
	
	int index;
	
	public NewsItemView()
	{
		setStylePrimaryName("newsitemview");
	}
	
	public NewsItemView(NewsItems newsitem){
		setNewsItem(newsitem);
		setStylePrimaryName("newsitemview");
	}

	public void createNewsView(){
      try{
    	
		NewsItems newsitems = getNewsItem();
		HorizontalFlowPanel relatedtagpanel = new HorizontalFlowPanel();
		HorizontalPanel hpanel = new HorizontalPanel();
		
		relatedtagpanel.add(createLabel("TAGS: "));
		ArrayList arraylisttags = newsitems.getAssociatedTagList();
		Iterator iter = arraylisttags.iterator();
		int count = 0;
		while(iter.hasNext())
		{
			if(count != arraylisttags.size()-1){
			TagItem tag = (TagItem)iter.next();
			relatedtagpanel.add(createLblRelatedTag(tag));
			relatedtagpanel.add(new Image("images/verticalSeparator.JPG",0,0,6,13));
			count++;
			}
			else{
				TagItem tag = (TagItem)iter.next();
				relatedtagpanel.add(createLblRelatedTag(tag));
				break;
			}
		}
		//hpanel.add(createLabel("PUBLISHED: "+ String.valueOf(newsitems.getNewsDate())));
		hpanel.add(createLabel("PUBLISHED: "+ newsitems.getNewsDate()));
		hpanel.add(new Image("images/verticalSeparator.JPG",0,0,6,13));
		hpanel.add(createLabel("SOURCE: "+ newsitems.getNewsSource()));
		
		String abstractnews = newsitems.getAbstractNews();
		abstractnewspanel.add(createLabelAbstract(abstractnews));
		
		FlexCellFormatter formatter = getFlexCellFormatter();
		formatter.setRowSpan(0,2,5);
		HorizontalPanel imagePanel = new HorizontalPanel();
		if(newsitems.getImageUrl() != null){
			if(!newsitems.getImageUrl().equals("")){
				imagePanel.setStylePrimaryName("imageBorder");
				imagePanel.add(createImage(newsitems.getImageUrl()));
				setWidget(0,2, imagePanel);
				formatter.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_TOP);
			}
		}
		setWidget(0,0,createHtml(newsitems));
		formatter.setWordWrap(1, 0, true);
		formatter.setWidth(1, 0, "75%");
		//formatter.setWordWrap(4, 0, true);
		//formatter.setWidth(4, 0, "200px");
		setWidget(1, 0, createLabelAbstract(abstractnews));
		setWidget(3, 0, hpanel);
		setWidget(4,0, relatedtagpanel);
		setWidget(5,0,vpPanel);
		//setWidget(6,0,hiddenNewsItemID);
      }
      catch(Exception e){
    	  e.printStackTrace();
      }
	}

	public HTML createHtml(NewsItems newsitems)
	{
		HTML html = new HTML();
		String link = newsitems.getUrl();
		String text = newsitems.getNewsTitle();
		if(link.startsWith("http://")){
			html.setHTML("<a href='"+link+"' target=\"_new\" style=\" font-family:Arial; color:#0066CA; \"><font size=\"2\"><strong class=\"newslink\">"+text+"</strong></font></a>");
			mapOfLinkVsNewsitem.put(html, newsitems);
		}
		else{
			link = "http://" + link;
			html.setHTML("<a href='"+link+"' target=\"_new\" style=\" font-family:Arial; color:#0066CA; \"><font size=\"2\"><strong class=\"newslink\">"+text+"</strong></font></a>");
			mapOfLinkVsNewsitem.put(html, newsitems);
		}
		html.setWordWrap(true);
		/*html.addClickListener(this);
		html.addMouseListener(this);*/
		html.addClickHandler(this);
	    html.addMouseOutHandler(this);
	    html.addMouseOverHandler(this);
		
		/*html.addMouseDownHandler(new MouseDownHandler(){

			@Override
			public void onMouseDown(MouseDownEvent arg0) {
				HTML html = (HTML)arg0.getSource();
				NewsItems newsitems = mapOfLinkVsNewsitem.get(html);
				Hidden hiddenNewsItemID = new Hidden();
				hiddenNewsItemID.setName("newsitemid");
				hiddenNewsItemID.setDefaultValue(String.valueOf(newsitems.getNewsId()));
				String url =  GWT.getHostPageBaseURL()+"com.login.login/itemView";
				FormPanel form = new FormPanel();
				vpPanel.add(form);
				form.setEncoding(FormPanel.ENCODING_MULTIPART);
				form.setMethod(FormPanel.METHOD_POST);
				form.setAction(url);
				form.add(hiddenNewsItemID);
				form.submit();
				form.addSubmitCompleteHandler(new SubmitCompleteHandler(){
	                       
					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
					}
				});
			}
			
		});*/
		
		//html.setStylePrimaryName("lblUrl");
		return html;
	}
	
	public Hyperlink createLink(String text,String link)
	{
		Hyperlink lnk = new Hyperlink(text,link);
		lnk.setStylePrimaryName("newslink");
		return lnk;
	}
	
	public Label createLabel(String text){
		Label label = new Label(text);
		label.setStylePrimaryName("newsItemGeneralLbl");
		return label;
	}
	
	public Label createLabelAbstract(String abstractnews){
		
		Label label = new Label(abstractnews);
		label.setStylePrimaryName("lblAbstract");
		//label.setWordWrap(false);
		return label;
	}

	public Label createLblRelatedTag(TagItem tagitem){
		AppliedTagsLabel appliedtaglabel = new AppliedTagsLabel(tagitem);
		appliedtaglabel.setText(tagitem.getTagName());
		appliedtaglabel.setWordWrap(false);
		appliedtaglabel.setStylePrimaryName("lblRelatedTag");
		appliedtaglabel.setTitle("View news related to "+ tagitem.getTagName());
		return appliedtaglabel;
	}
 
	public Image createImage(String imageurl){
			String urlClient = GWT.getModuleBaseURL();
			String[]  mainurl = new String[5];
			mainurl = urlClient.split("/");
			String urlPort = mainurl[0]+"//"+mainurl[2];
			String imageurls = /*urlPort+"/NewsCenter/"+*/imageurl;
			Image image = new Image(imageurls,0,0,68,68);
			image.setStylePrimaryName("newsimage");
			return image;
	}
	
	public NewsItems getNewsItem() {
		return newsItem;
	}

	public void setNewsItem(NewsItems newsItem) {
		this.newsItem = newsItem;
	}
	
	public void onMouseDown(Widget arg0, int arg1, int arg2) {
       
	}
	
	/*public void onMouseEnter(Widget sender) {
		if(sender instanceof HTML){
			HTML label = (HTML)sender;
			label.removeStyleDependentName("lblUrl");
			label.setStylePrimaryName("lblUrlHover");
		}
	}*/
	
	/*public void onMouseLeave(Widget sender) {
		if(sender instanceof HTML){
			HTML label = (HTML)sender;
			label.addStyleDependentName("lblUrl");
			label.removeStyleName("lblUrlHover");

		}
	}*/
	
	public void onMouseMove(Widget arg0, int arg1, int arg2) {

	}
	
	public void onMouseUp(Widget arg0, int arg1, int arg2) {
		
	}
	
	/*public void onClick(Widget sender) {
		if(sender instanceof HTML){
			HTML html = (HTML) sender;
			NewsItems newsitems = mapOfLinkVsNewsitem.get(html);
			Hidden hiddenNewsItemID = new Hidden();
			hiddenNewsItemID.setName("newsitemid");
			hiddenNewsItemID.setDefaultValue(String.valueOf(newsitems.getNewsId()));
			
			Hidden infohidden = new Hidden();
			
			int userid = NewsCenterMain.getUserInformation().getUserId();
			int newscenterid = NewsCenterMain.getUserSelectedIndustryId();
			int newsitemid = newsitems.getNewsId();
			String link = newsitems.getUrl();
			String info = userid+"**"+newscenterid+"**"+newsitemid+"**"+link;
            
			infohidden.setName("info");
			infohidden.setDefaultValue(info);
			
			String url =  GWT.getHostPageBaseURL()+"com.login.login/itemView";
			//String url =  GWT.getHostPageBaseURL()+"com.login.login/emailitemView";
			FormPanel form = new FormPanel();
			vpPanel.add(form);
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
			form.setAction(url);
			//form.add(infohidden);
			form.add(hiddenNewsItemID);
			form.submit();
			form.addSubmitCompleteHandler(new SubmitCompleteHandler(){
                       
							@Override
							public void onSubmitComplete(SubmitCompleteEvent event) {
						}
						});
			
		}
	}*/

	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget)event.getSource();
		if(sender instanceof HTML){
			HTML html = (HTML) sender;
			NewsItems newsitems = mapOfLinkVsNewsitem.get(html);
			Hidden hiddenNewsItemID = new Hidden();
			hiddenNewsItemID.setName("newsitemid");
			hiddenNewsItemID.setDefaultValue(String.valueOf(newsitems.getNewsId()));
			
			/*Hidden infohidden = new Hidden();
			
			int userid = NewsCenterMain.getUserInformation().getUserId();
			int newscenterid = NewsCenterMain.getUserSelectedIndustryId();
			int newsitemid = newsitems.getNewsId();
			String link = newsitems.getUrl();
			String info = userid+"**"+newscenterid+"**"+newsitemid+"**"+link;
            
			infohidden.setName("info");
			infohidden.setDefaultValue(info);*/
			
			String url =  GWT.getHostPageBaseURL()+"com.login.login/itemView";
			//String url =  GWT.getHostPageBaseURL()+"com.login.login/emailitemView";
			FormPanel form = new FormPanel();
			vpPanel.add(form);
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
			form.setAction(url);
			//form.add(infohidden);
			form.add(hiddenNewsItemID);
			form.submit();
			form.addSubmitCompleteHandler(new SubmitCompleteHandler(){
                       
							@Override
							public void onSubmitComplete(SubmitCompleteEvent event) {
						}
						});
		}
		
	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		Widget sender = (Widget)event.getSource();
		if(sender instanceof HTML){
			HTML label = (HTML)sender;
			label.removeStyleDependentName("lblUrl");
			label.setStylePrimaryName("lblUrlHover");
		}
		
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		Widget sender = (Widget)event.getSource();
		if(sender instanceof HTML){
			HTML label = (HTML)sender;
			label.addStyleDependentName("lblUrl");
			label.removeStyleName("lblUrlHover");

		}
		
	}
}
