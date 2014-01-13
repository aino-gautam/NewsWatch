package com.lighthouse.admin.client.siloui;

import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.lighthouse.admin.client.LHadmin;
import com.lighthouse.newsletter.client.NewsletterConfigService;
import com.lighthouse.newsletter.client.NewsletterConfigServiceAsync;

public class ManageSiloClientLogo extends Composite {
	private VerticalPanel mainContainer = new VerticalPanel();
	private FlexTable flex = new FlexTable();
	private FormPanel form = new FormPanel();
	private Label selectlbl = new Label("Please upload Image:");
	private Button saveBtn = new Button("Save");
	private int industryId;
	public ManageSiloClientLogo() {
		initWidget(mainContainer);
	}
	
	public void initialize(){
		createUI();
	}

	private void createUI() {
		final FileUpload clientImageUploader = new FileUpload();
		industryId= LHadmin.industryId;
		getClientLogoImage(industryId);
		clientImageUploader.setName("imageFile");
		flex.setWidget(1, 0, selectlbl);
		selectlbl.setStylePrimaryName("newslettereditLbl");
		flex.setWidget(1, 2, clientImageUploader);
		flex.setWidget(3, 1, saveBtn);
		saveBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				mainContainer.add(form);
				String gwturlbase = GWT.getHostPageBaseURL();
				String url = gwturlbase.substring(0,gwturlbase.length() - 1);
				Hidden type = new Hidden();
				type.setName("type");
				type.setValue("siloLogo");
				HorizontalPanel formFieldsPanel = new HorizontalPanel();
				formFieldsPanel.add(type);
				formFieldsPanel.add(clientImageUploader);
				form.setEncoding(FormPanel.ENCODING_MULTIPART);
				form.setMethod(FormPanel.METHOD_POST);
				form.setWidget(formFieldsPanel);
				form.setAction(url + "/newsletterConfig");
				form.submit();
				form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
					
					@Override
					public void onSubmitComplete(SubmitCompleteEvent event) {
						getClientLogoImage(industryId);
						flex.setWidget(1, 2, clientImageUploader);
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Successfully Saved...");
						popup.setPopupPosition(mainContainer.getAbsoluteLeft()+400, mainContainer.getAbsoluteTop()+100);
						popup.show();
					}
				});
			}
		});
		mainContainer.add(flex);
	}
	private void getClientLogoImage(int newsCenterId) {
		NewsletterConfigServiceAsync service = (NewsletterConfigServiceAsync)GWT.create(NewsletterConfigService.class);
		 service.getImageUrl(newsCenterId,"siloLogo", true, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}
			@Override
			public void onSuccess(String result) {
				if(result!=null){
					if(flex.isCellPresent(1, 4))
						flex.clearCell(1, 4);
					Image imgLogoPreview = new Image(result);
					imgLogoPreview.setStylePrimaryName("siloLogo");
					flex.setWidget(1, 4,imgLogoPreview);
				}
				else{
					if(flex.isCellPresent(1, 4))
						flex.clearCell(1, 4);
					Image imgLogoPreview = new Image("config/NoImage.png");
					imgLogoPreview.setStylePrimaryName("siloLogo");
					flex.setWidget(1, 4,imgLogoPreview);
				}
			}
		 });
	}
}
