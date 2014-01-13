package com.lighthouse.admin.client;

import com.appUtils.client.ProgressIndicatorWidget;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class SyncNewsFeed extends Composite implements ClickHandler,SubmitCompleteHandler{

	private RootPanel panel;
	private VerticalPanel vPanel=new VerticalPanel();
	private RadioButton autoButton;
	private RadioButton manButton;
	private String mode;
	private Button button;
	private ProgressIndicatorWidget loader = new ProgressIndicatorWidget(true);
	
	
	  public SyncNewsFeed() {
		  
		autoButton=new RadioButton("group", "Auto");
		manButton=new RadioButton("group","Manual"); 
		button=new Button("Sync now");
		button.setStylePrimaryName("buttonOkAdmin");
		autoButton.addClickHandler(this);
		manButton.addClickHandler(this);
		button.addClickHandler(this);
		vPanel.add(autoButton);
		vPanel.add(manButton);
		vPanel.add(button);
		button.setVisible(false);
		vPanel.setCellHorizontalAlignment(loader,
				HasHorizontalAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(loader,
				HasVerticalAlignment.ALIGN_MIDDLE);
		loader.setLoadingMessage("Synchronizing feed..This may take few minutes....");
		vPanel.add(loader);
		loader.disable();
		initWidget(vPanel);
	}
	@Override
	public void onClick(ClickEvent event) {
		
		Widget sender =(Widget) event.getSource();
		
		if(sender instanceof RadioButton){
			button.setVisible(true);
			RadioButton radioButton = (RadioButton)sender;
			if(radioButton.getText().equalsIgnoreCase("Auto")){
				mode="A";
			}
			if(radioButton.getText().equalsIgnoreCase("Manual")){
				mode="M";
			}
		}
		if(sender instanceof Button){
			loader.enable();
			String url = GWT.getHostPageBaseURL()+"feed";
			FormPanel form = new FormPanel();
			Hidden hidden=new Hidden();
			hidden.setName("mode");
			hidden.setValue(mode);
			form.add(hidden);
			vPanel.add(form);
			form.setEncoding(FormPanel.ENCODING_MULTIPART);
			form.setMethod(FormPanel.METHOD_POST);
			form.setAction(url);
			form.addSubmitCompleteHandler(this);
			form.submit();
				
		}
		
	}

	
		public void onModuleLoad(){
			panel = RootPanel.get();
			panel.clear();
			panel.add(this);
	}
		@Override
		public void onSubmitComplete(SubmitCompleteEvent event) {
			loader.disable();
			PopUpForForgotPassword popUp=null;
			if(event.getResults().contains("Success")){
				popUp=new PopUpForForgotPassword("NewsFeed's Synchronized Successfully..");
			}
			else if(event.getResults().contains("exist")){
				popUp=new PopUpForForgotPassword("Some NewsFeed's already exist ..");
			}
			else if(event.getResults().contains("Processing")){
				popUp=new PopUpForForgotPassword("Processing your request.. ");
			}
			else if(event.getResults().contains("Invalid Url")){
				popUp=new PopUpForForgotPassword("Sync Failed due to invalid url.. ");
			}
			else if(event.getResults().contains("Failed to save")){
				popUp=new PopUpForForgotPassword("Failed to save feed news items .. ");
			}
			else	
				popUp=new PopUpForForgotPassword("Sync Failed .. ");
			
			
		popUp.setPopupPosition(vPanel.getAbsoluteLeft()+150,vPanel.getAbsoluteTop() );
		popUp.show();
			
		}


		


	
		
	

}
