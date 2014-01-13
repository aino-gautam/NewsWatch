package com.admin.client;

import com.common.client.CommonInformationService;
import com.common.client.CommonInformationServiceAsync;
import com.common.client.ManageHeader;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EmailTemplate extends Composite {

	private TextArea txtArea;
	private TextArea txtSignature;
	private int newscenterid,userid;
	private Label lblheading = new Label("Please select any of the following: ");
	private ListBox lbOption = new ListBox();
	private VerticalPanel vpEditEmailTemplate;
	private VerticalPanel vpEditSignatureTemplate;
	
	public EmailTemplate(int newscenterid,int userid){
		this.newscenterid = newscenterid;
		this.userid = userid;
		final DeckPanel deck = new DeckPanel();
		
		lbOption.addItem("Edit email template");
		lbOption.addItem("Edit signature");
		lbOption.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent arg0) {
				if(lbOption.getSelectedIndex() == 0){
					//deck.add(createEditEmailTemplate());
					deck.showWidget(0);
				}
				else if(lbOption.getSelectedIndex() == 1){
					//deck.add(creatEditSignatureTemplate());
					deck.showWidget(1);
				}
			}
			
		});
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(lblheading);
		hp.add(lbOption);
		hp.setSpacing(5);
		
		lblheading.setStylePrimaryName("labelSelection");
		
		VerticalPanel vpBase = new VerticalPanel();
		vpBase.add(hp);
		vpBase.add(deck);
		
		deck.add(createEditEmailTemplate());
		deck.add(creatEditSignatureTemplate());
		deck.showWidget(0);
		
		vpBase.setSpacing(7);
		vpBase.setSize("100%", "100%");
		initWidget(vpBase);
	}
	
	private VerticalPanel createEditEmailTemplate(){
		vpEditEmailTemplate = new VerticalPanel();
		txtArea = new TextArea();
		Button btnsave = new Button("Save");
		btnsave.setStylePrimaryName("buttonOkAdmin");
		txtArea.setWidth("75%");
		vpEditEmailTemplate.add(txtArea);
		vpEditEmailTemplate.add(btnsave);
		vpEditEmailTemplate.setSpacing(7);
		
		getEmailTemplateToEdit();
		
		btnsave.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent arg0) {
				saveEmailTemplate();			
			}
			
		});
		return vpEditEmailTemplate;
	}
	
	private VerticalPanel creatEditSignatureTemplate(){
		vpEditSignatureTemplate = new VerticalPanel();
		txtSignature = new TextArea();
		Button btnsave = new Button("Save");
		btnsave.setStylePrimaryName("buttonOkAdmin");
		vpEditSignatureTemplate.add(txtSignature);
		vpEditSignatureTemplate.add(btnsave);
		vpEditSignatureTemplate.setSpacing(7);
		
		btnsave.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent arg0) {
				saveSignature();				
			}
			
		});
		getSignature();
		return vpEditSignatureTemplate;
	}
	
	private void getEmailTemplateToEdit(){
		CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		ServiceDefTarget target = (ServiceDefTarget)service;
		String moduleRelativeUrl = GWT.getModuleBaseURL()+"common";
		target.setServiceEntryPoint(moduleRelativeUrl);
		AsyncCallback<String> callback = new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable arg0) {
								
			}

			@Override
			public void onSuccess(String arg0) {
				if(arg0 != null){
				  txtArea.setVisibleLines(33);	
				  txtArea.setText(arg0);
				}
			}
			
		};
		service.getEmailTemplate(newscenterid, callback);
	}
	
	private void getSignature(){
		CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		ServiceDefTarget target = (ServiceDefTarget)service;
		String moduleRelativeUrl = GWT.getModuleBaseURL()+"common";
		target.setServiceEntryPoint(moduleRelativeUrl);
		AsyncCallback<String> callback = new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(String arg0) {
				if(arg0 != null){
				 txtSignature.setText(arg0);
				}
			}
			
		};
		service.getSignature(userid,callback);
	}
	
	private void saveEmailTemplate(){
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		
		AsyncCallback callback = new AsyncCallback(){

			@Override
			public void onFailure(Throwable arg0) {
								
			}

			@Override
			public void onSuccess(Object arg0) {
			 PopUpForForgotPassword popup = new PopUpForForgotPassword("Email template saved");
			 popup.setAnimationEnabled(true);
			 popup.center();
			}
			
		};
		service.saveEmailTemplate(txtArea.getText(), newscenterid, callback);
	}
	
	private void saveSignature(){
		CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		ServiceDefTarget target = (ServiceDefTarget)service;
		String moduleRelativeUrl = GWT.getModuleBaseURL()+"common";
		target.setServiceEntryPoint(moduleRelativeUrl);
		AsyncCallback callback = new AsyncCallback(){

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(Object arg0) {
				 PopUpForForgotPassword popup = new PopUpForForgotPassword("Signature saved");
				 popup.setAnimationEnabled(true);
				 popup.center();
				 ManageHeader.getUserinformation().setSignature(txtSignature.getText());
			}
			
		};
		service.saveSignature(userid, txtSignature.getText(), callback);
	}

	
}
