package com.lighthouse.newspage.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.newscenter.client.news.NewsItems;

public class ShareStoryPopup extends PopupPanel implements ClickHandler{
	
	private VerticalPanel vpBasePanel = new VerticalPanel();
	private VerticalPanel vpPanel = new VerticalPanel();
	private HorizontalPanel  buttonpanel = new HorizontalPanel();
	private HorizontalPanel  labelPanel = new HorizontalPanel();
	private FlexTable flex = new FlexTable();
	private Label label = new Label("EnterEmail Addresses");
	private Label labelClose = new Label("close");
	private TextArea emailTextArea  = new TextArea();
    private Button btnShare = new Button("Share");
    private Button btnCancel = new Button("Cancel");
    private Hidden hiddenemail = new Hidden();
	private Hidden hiddenTitle = new Hidden();
	private Hidden hiddenAbstract = new Hidden();
	private Hidden hiddenDate = new Hidden();
	private Hidden hiddenSource = new Hidden();
	private Hidden hiddenId = new Hidden();
	private Hidden hiddenurl = new Hidden();
	private FormPanel form = new FormPanel();
	private Image imgLoader	= new Image("images/circle_loader.gif");
	private Label lblLoading = new Label("Sending email");
	NewsItems newsItems;
	
	public ShareStoryPopup(NewsItems newsItems){
		setNewsItems(newsItems);
		
		add(vpBasePanel);
		setWidth("600px");
		setAnimationEnabled(true);
		setModal(true);
		labelPanel.add(label);
		labelPanel.add(labelClose);
		labelClose.addClickHandler(this);
		vpBasePanel.add(labelPanel);
		emailTextArea.setWidth("85%");
		vpBasePanel.add(emailTextArea);
		
		buttonpanel.add(btnShare);
		
		
		//VerticalPanel vpPanel = new VerticalPanel();
		hiddenemail.setName("email");
		hiddenTitle.setName("title");
		hiddenAbstract.setName("abstract");
		hiddenDate.setName("date");
		hiddenSource.setName("source");
		hiddenId.setName("newsId");
		hiddenurl.setName("url");
		vpPanel.add(hiddenemail);
		vpPanel.add(hiddenTitle);
		vpPanel.add(hiddenAbstract);
		vpPanel.add(hiddenDate);
		vpPanel.add(hiddenSource);
        vpPanel.add(hiddenId);
        vpPanel.add(hiddenurl);
		form.add(vpPanel);
		vpBasePanel.add(form);
		//form.add(vpPanel);
		btnShare.addClickHandler(this);
		//buttonpanel.add(btnCancel);
		vpBasePanel.add(buttonpanel);
	}

	@Override
	public void onClick(ClickEvent event) {
		
		Widget arg0=(Widget) event.getSource();
		if(arg0 instanceof Label){
			if(((Label) arg0).getText().equals("close")){
				hide();
			}
			
		}
		if(arg0 instanceof Button){
			if(arg0 == btnShare){
				
				
				String emailString=emailTextArea.getText();
				NewsItems newsItems = getNewsItems();
				int newsId=newsItems.getNewsId();
				String id = ""+newsId;
				String title = newsItems.getNewsTitle();
				String abstractNews = newsItems.getAbstractNews();
				String date = newsItems.getNewsDate();
				String source = newsItems.getNewsSource();
				String nurl=newsItems.getUrl();
				hiddenemail.setDefaultValue(emailString);
				hiddenAbstract.setDefaultValue(abstractNews);
				hiddenTitle.setDefaultValue(title);
				hiddenDate.setDefaultValue(date);
				hiddenSource.setDefaultValue(source);
				hiddenId.setDefaultValue(id);
				hiddenurl.setDefaultValue(nurl);
				//vpPanel.add(form);
				
				
				
				/*String url =  GWT.getHostPageBaseURL()+"com.lighthouse.newspage.newspage/sharestory"*/; // url to send all parameters
				String url =  GWT.getModuleBaseURL()+"sharestory";
				try{
					// add code to submit the form
					form.setEncoding(FormPanel.ENCODING_MULTIPART);
					form.setMethod(FormPanel.METHOD_POST);
					form.setAction(url);
									
					form.submit();
					form.addSubmitCompleteHandler(new  SubmitCompleteHandler(){

						@Override
						public void onSubmitComplete(SubmitCompleteEvent event) {
							//imgLoader.setVisible(false);
							//hide();
						}

						
					});
					/*Window.Location.replace("/sharestory");  */
			}catch(Exception e){
				e.printStackTrace();
				
			}
			}
			
		}
	}

	public NewsItems getNewsItems() {
		return newsItems;
	}

	public void setNewsItems(NewsItems newsItems) {
		this.newsItems = newsItems;
	}

}
