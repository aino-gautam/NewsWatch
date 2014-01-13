package com.trial.client;

import com.common.client.CommonInformationService;
import com.common.client.CommonInformationServiceAsync;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MailTemplate extends Composite implements ClickHandler {
	
	private DecoratorPanel decopanel = new DecoratorPanel();
	private VerticalPanel vpBase = new VerticalPanel();
	private Label lblheader = new Label("Trial account will be created for");
	private Image imgsetupbtn = new Image("images/trial/setup.gif");
	private StringBuilder stringBuilder;
	private TrialAccount trialusrinfo;
	private String username = "#*username*#";
	private String emailusername = "#*emailusername*#";
	private String executive = "#*executive*#";
	private String executiveSignature = "#*signature*#";
	private TrialAccountSetup trialAccountSetupRef;
	private Label subject;
	private VerticalPanel vpmain = new VerticalPanel(); 
	
	
	
	public MailTemplate(TrialAccount trialUserInformation){
		setTrialusrinfo(trialUserInformation);
		getEmailTemplateForNewsCatalyst(trialusrinfo.getNewscenterid());
		DOM.setStyleAttribute(lblheader.getElement(), "fontSize", "11pt");
		DOM.setStyleAttribute(lblheader.getElement(), "color", "#1C5793");
		DOM.setStyleAttribute(lblheader.getElement(), "fontWeight", "bold");
		vpBase.add(lblheader);
		
		HTML dottedLine =  new HTML("<hr size=\"1\" style=\"border-style:solid\"; color=\"#858484\">");
		dottedLine.setWidth("100%");
		vpBase.add(dottedLine);
		vpBase.setSpacing(10);
		DOM.setStyleAttribute(vpBase.getElement(), "backgroundColor", "#f6f6f6");
		decopanel.add(vpBase);
		vpmain.add(decopanel);
		vpmain.setWidth("740px");
		initWidget(vpmain);
	}
	
	public void getEmailTemplateForNewsCatalyst(int newscenterid) {
		CommonInformationServiceAsync service = (CommonInformationServiceAsync)GWT.create(CommonInformationService.class);
		ServiceDefTarget target = (ServiceDefTarget)service;
		String moduleRelativeUrl = GWT.getModuleBaseURL()+"common";
		target.setServiceEntryPoint(moduleRelativeUrl);
		AsyncCallback callback = new AsyncCallback(){

			@Override
			public void onFailure(Throwable arg0) {
				arg0.getMessage();
			}

			@Override
			public void onSuccess(Object arg0) {
				stringBuilder = new StringBuilder();
				stringBuilder.append(arg0.toString());
				int usernameindex = stringBuilder.indexOf("#*username*#");
				stringBuilder.replace(usernameindex, usernameindex+username.length(), trialusrinfo.getFirstname());
				
				int useremailindex = stringBuilder.indexOf("#*emailusername*#");
				stringBuilder.replace(useremailindex, useremailindex+emailusername.length(), trialusrinfo.getUseremail());
				
				int executiveindex = stringBuilder.indexOf("#*executive*#");
				stringBuilder.replace(executiveindex, executiveindex+executive.length(), trialusrinfo.getSalesExecutiveName());
				
				int signatureindex = stringBuilder.indexOf("#*signature*#");
				stringBuilder.replace(signatureindex, signatureindex+executiveSignature.length(), trialusrinfo.getSignature());
				createUserInfoPanel();
			}
			
		};
		service.getEmailTemplate(newscenterid, callback);
	}

	private void createUserInfoPanel(){
		subject = new Label("Welcome to trial of "+getTrialusrinfo().getNewscatalystName());
		FlexTable flex = new FlexTable();
		flex.setText(0, 0, "User:");
		flex.setText(0, 4, getTrialusrinfo().getFirstname()+" "+getTrialusrinfo().getLastname());
		flex.setText(3, 0, "Email:");
		flex.setText(3, 4, getTrialusrinfo().getUseremail());
		flex.setText(6, 0, "Subject:");
		flex.setWidget(6, 4, subject);
		vpBase.add(flex);
		
		HTML html = new HTML();
		html.setHTML(stringBuilder.toString());
		html.setWidth("740px");
		html.setWordWrap(true);
		VerticalPanel vpcontentpanel = new VerticalPanel();
		vpcontentpanel.add(html);
		vpcontentpanel.add(imgsetupbtn);
		vpcontentpanel.setSpacing(7);
		vpcontentpanel.setCellHorizontalAlignment(imgsetupbtn, HasHorizontalAlignment.ALIGN_RIGHT);
		imgsetupbtn.setStylePrimaryName("clickable");
		imgsetupbtn.addClickHandler(this);
		DOM.setStyleAttribute(vpcontentpanel.getElement(),"backgroundColor","white");
		DOM.setStyleAttribute(vpcontentpanel.getElement(), "border", "1px solid #9f9f9f");
		vpBase.add(vpcontentpanel);
		vpBase.setCellHorizontalAlignment(vpcontentpanel, HasHorizontalAlignment.ALIGN_CENTER);
	}


	@Override
	public void onClick(ClickEvent arg0) {
		TrialInformationServiceAsync service = (TrialInformationServiceAsync)GWT.create(TrialInformationService.class);
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>(){

			@Override
			public void onFailure(Throwable arg0) {
				
			}

			@Override
			public void onSuccess(Boolean bool) {
				if(bool){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("User successfully created");
					popup.setAnimationEnabled(true);
					popup.show();
					popup.center();
					popup.getButtonClose().addClickHandler(new ClickHandler(){
	
						@Override
						public void onClick(ClickEvent arg0) {
							getTrialAccountSetupRef().createUI();
							getTrialAccountSetupRef().getDeckpanel().showWidget(0);
						}
					});
				}
				else{
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Duplicate email entry Please enter another email");
					popup.setPopupPosition(600, 300);
					popup.show();
					popup.getButtonClose().addClickHandler(new ClickHandler(){
						
						@Override
						public void onClick(ClickEvent arg0) {
							getTrialAccountSetupRef().getDeckpanel().showWidget(0);
						}
					});
				}
			}
		};
		service.createTrialAccount(getTrialusrinfo(), stringBuilder.toString(),getSubject().getText(),callback);
	}

	public TrialAccount getTrialusrinfo() {
		return trialusrinfo;
	}

	public void setTrialusrinfo(TrialAccount trialusrinfo) {
		this.trialusrinfo = trialusrinfo;
	}

	public TrialAccountSetup getTrialAccountSetupRef() {
		return trialAccountSetupRef;
	}

	public void setTrialAccountSetupRef(TrialAccountSetup trialAccountSetupRef) {
		this.trialAccountSetupRef = trialAccountSetupRef;
	}

	public Label getSubject() {
		return subject;
	}

	public void setSubject(Label subject) {
		this.subject = subject;
	}
}