package com.lighthouse.newspage.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.newscenter.client.news.NewsItems;

/**
 * 
 * @author kiran@ensarm.com
 * 
 */
public class ShareLabel extends Label implements ClickHandler, MouseOutHandler,
		MouseOverHandler {

	private NewsItems newsItems;
	private Hidden hiddenemail = new Hidden();
	private Hidden hiddenTitle = new Hidden();
	private Hidden hiddenAbstract = new Hidden();
	private Hidden hiddenDate = new Hidden();
	private Hidden hiddenSource = new Hidden();
	private Hidden hiddenUser = new Hidden();
	private FormPanel form = new FormPanel();
	private Image imgLoader	= new Image("images/circle_loader.gif");
	private Label lblLoading = new Label("Sending email");
	public ShareLabel() {

	}

	public ShareLabel(NewsItems newsItems) {
		setNewsItems(newsItems);
		addClickHandler(this);
		addMouseOutHandler(this);
		addMouseOverHandler(this);

	}

	
	/*private void shareStory(ArrayList<UserInformation> list) {
		
		Iterator iterator = list.iterator();
		while(iterator.hasNext()){
			UserInformation userInformation = (UserInformation) iterator.next();
			
			
		NewsItems newsitems = getNewsItems();
		
		String title = newsitems.getNewsTitle();
		String abstractNews = newsitems.getAbstractNews();
		String date = newsitems.getNewsDate();
		String source = newsitems.getNewsSource();
		int userId = userInformation.getUserId();
		String userEmail = userInformation.getEmail();
		
		//UserInformation user = LhMain.getUserInformation();
		//String email = userInformation.getEmail();
		//hiddenemail.setDefaultValue(source);
		hiddenAbstract.setDefaultValue(abstractNews);
		hiddenTitle.setDefaultValue(title);
		hiddenDate.setDefaultValue(date);
		hiddenUser.setDefaultValue(userEmail);
		hiddenSource.setDefaultValue(source);
		
		
		
		
		String url =  GWT.getHostPageBaseURL()+"com.lighthouse.newspage.newspage/sharestory"; // url to send all parameters
		try{
			// add code to submit the form
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
			form.setAction(url);
			imgLoader.setVisible(true);
			lblLoading.setVisible(true);
			
			form.submit();
			form.addSubmitCompleteHandler(new  SubmitCompleteHandler(){

				@Override
				public void onSubmitComplete(SubmitCompleteEvent event) {
					imgLoader.setVisible(false);
					//hide();
				}

								
			});
	}catch(Exception e){
		e.printStackTrace();
		
	}
	}
	}*/
	@Override
	public void onClick(ClickEvent event) {
		
		Widget arg0=(Widget) event.getSource();
		NewsItems newsItems = getNewsItems();
		ShareLabel label = (ShareLabel) arg0;
		ShareStoryPopup shareStoryPopup = new ShareStoryPopup(newsItems); 
		shareStoryPopup.setGlassEnabled(true);
		shareStoryPopup.show();
		shareStoryPopup.center();
		
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMouseOver(MouseOverEvent event) {
		// TODO Auto-generated method stub

	}

	public NewsItems getNewsItems() {
		return newsItems;
	}

	public void setNewsItems(NewsItems newsItems) {
		this.newsItems = newsItems;
	}

}
