package com.newscenter.client.obsolete;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.newscenter.client.news.NewsItems;

public class NewsPresenterMain extends Composite implements ClickHandler,MouseOverHandler,MouseOutHandler
{
	private NewsItemStore newsitemstore = new NewsItemStore(this);
	public ArrayList arraynewscollection = new ArrayList();
	private HashMap mapitems = new HashMap();
	private EffectsONFlexTabel flex = new EffectsONFlexTabel();
	private VerticalPanel vpanelforflex = new VerticalPanel();
	private DecoratorPanel decoratornewscontent = new DecoratorPanel();
	private HorizontalPanel hpanelcontainer = new HorizontalPanel();
	
	public NewsPresenterMain()
	{
		//arraynewscollection = NewsItemStore.getNewscollection();
		hpanelcontainer.add(vpanelforflex);
		hpanelcontainer.setCellHorizontalAlignment(vpanelforflex, HasHorizontalAlignment.ALIGN_LEFT);
		hpanelcontainer.addStyleName("newsPresenterBorder");
		initWidget(hpanelcontainer);
	}
	public void getnewsitemcontent()
	{
		arraynewscollection = NewsItemStore.getNewscollection();
		Iterator iter = arraynewscollection.iterator();
		while(iter.hasNext())
		{
			mapitems.clear();
			mapitems = (HashMap)iter.next();
			if(!mapitems.isEmpty())
			{
			addthenewscontentindisplay(mapitems);
			}
		}
		
	}
	public void addthenewscontentindisplay(HashMap mapnewscontent)
	{
		//flex.setStylePrimaryName("flexforNewsPresenterMain");
		//NewsCategory newscategory = new NewsCategory();
		NewsItems newsitem = new NewsItems();
		HashMap mapitems = new HashMap();
		FlexTable flexnews;
		FlexCellFormatter formatter;
		int row = 0;
		int col =0;
		for(Object obj: mapnewscontent.keySet())
		{
			int id = (Integer)obj;
			//newscategory = (NewsCategory)mapnewscontent.get(obj);
			//mapitems = newscategory.getnewsitems();
				for(Object objmapitems : mapitems.keySet())
				{
					flexnews = new FlexTable();
					//flexnews.setStylePrimaryName("flexforNewsPresenterMain");
					formatter = flexnews.getFlexCellFormatter();
					int newsitemsid = (Integer)objmapitems;
					newsitem = (NewsItems)mapitems.get(objmapitems);
					String newstitle = newsitem.getNewsTitle();
					String newsabstract = newsitem.getAbstractNews();
					String imageurl = newsitem.getImageUrl();
					String newsurl = newsitem.getUrl();
					String newsdate = newsitem.getNewsDate();
					
					formatter.setRowSpan(0,0,5);
					formatter.setColSpan(0,1, 2);
					
					flexnews.setWidget(0,0,createImageNewsContent(imageurl));
					flexnews.setWidget(0,1,createLabelBold(newstitle));
					flexnews.setWidget(1,1,createLabelabstract(newsabstract));
					flexnews.setWidget(2,1,createLblurl(newsurl));
					flexnews.setWidget(3,1,createLabelabstract(newsdate.toString()));
					flexnews.setWidget(4,1,new HTML("<hr width=100%>"));
					flex.setWidget(row,col, flexnews);
					
					vpanelforflex.add(flex);
					
					row++;	
				}
		}	
	}
	public Label createLabelBold(String text)
	{
		Label label = new Label(text);
		label.setStylePrimaryName("lblnewstitle");
		return label;
		
	}
	public Label createLabelabstract(String text)
	{
		Label label = new Label(text);
		label.setStylePrimaryName("lblnewsabstract");
		return label;
	}
	public Label createLblurl(String text)
	{
		Label label = new Label(text);
		label.setStylePrimaryName("lblurlnews");
		label.addClickHandler((ClickHandler) this);
		label.addMouseOutHandler(this);
		label.addMouseOverHandler(this);
		return label;
	}
	public Image createImageNewsContent(String url)
	{
		/*String urlClient = GWT.getModuleBaseURL();
		String[]  mainurl = new String[5];
		mainurl = urlClient.split("/");
		String urlPort = mainurl[0]+"//"+mainurl[2];
		String imageurl = urlPort+"/NewsCenter/"+url;
		Image image = new Image(imageurl,1,1,150,150);*/
		
		Image image = new Image(url,1,1,150,150);
		image.setSize("40px", "40px");
		image.setStylePrimaryName("newsImages");
		return image;
	}
	public void onModuleLoad() 
	{
		RootPanel.get().clear();
		RootPanel.get().add(this);
	}
	
	public void onClick(Widget sender) 
	{
		/*if(sender instanceof Label)
		{
			Label label =(Label)sender;
			String lbltext = label.getText();
			Window.open(lbltext, "_self", "");
		}*/
	}
	
	public void onMouseDown(Widget arg0, int arg1, int arg2) {
		
		
	}
	
	/*public void onMouseEnter(Widget sender) {
		
		if(sender instanceof Label)
		{
			Label lbl = (Label)sender;
			lbl.setStylePrimaryName("lblurlmousehover");
		}
	}*/
	
	/*public void onMouseLeave(Widget sender) {
		
		if(sender instanceof Label)
		{
			Label lbl = (Label)sender;
			lbl.setStylePrimaryName("lblurlnews");
		}
		
	}*/
	
	public void onMouseMove(Widget arg0, int arg1, int arg2) {
		
		
	}
	
	public void onMouseUp(Widget arg0, int arg1, int arg2) {
		
		
	}
	
	@Override
	public void onClick(ClickEvent event) {
		
		Widget sender = (Widget)event.getSource();
		
		if(sender instanceof Label)
		{
			Label label =(Label)sender;
			String lbltext = label.getText();
			Window.open(lbltext, "_self", "");
		}
		
	}
	@Override
	public void onMouseOut(MouseOutEvent event) {
		Widget sender = (Widget)event.getSource();
		if(sender instanceof Label)
		{
			Label lbl = (Label)sender;
			lbl.setStylePrimaryName("lblurlnews");
		}
		
	}
	@Override
	public void onMouseOver(MouseOverEvent event) {
		Widget sender = (Widget)event.getSource();
		if(sender instanceof Label)
		{
			Label lbl = (Label)sender;
			lbl.setStylePrimaryName("lblurlmousehover");
		}
		
	}
	
	
	

}
