package com.lighthouse.admin.client.siloui;

import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.lighthouse.admin.client.LHadmin;
import com.lighthouse.newsletter.client.NewsletterConfigService;
import com.lighthouse.newsletter.client.NewsletterConfigServiceAsync;

public class ManageTemplateOutline extends Composite implements ValueChangeHandler<String>{
	
	private VerticalPanel mainContainer = new VerticalPanel(); 
	private VerticalPanel subContainer = new VerticalPanel(); 
	OutlineDesignConfiguration configuration;
	private boolean fileUploader = false;
	private FormPanel form = new FormPanel();
	FlexTable baseTableUi = new FlexTable();
	public static boolean hasValueChanged = false;
	
	public ManageTemplateOutline(){
		initWidget(mainContainer);
	}

	public void initialize(){
		NewsletterConfigServiceAsync service = (NewsletterConfigServiceAsync)GWT.create(NewsletterConfigService.class);
		service.getNewsletterOutlineConfig(new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(String result) {
				configuration = new OutlineDesignConfiguration(result);
				createUI();
			}
		});
	}
	
	/**
	 * creates the page ui
	 */
	public void createUI(){
		
		createMainList();
		mainContainer.add(subContainer);
	}

	private void createMainList() {
		HorizontalPanel hpLbContainer = new HorizontalPanel();
		Label lblplaeseSelect = new Label();
		lblplaeseSelect.setText("Please select an element to style :");
		lblplaeseSelect.setStylePrimaryName("messageLabels");
		
		final ListBox mainListBox = new ListBox();
		mainListBox.addItem("----Select----");
		mainListBox.addItem("Table <table>");
		
		mainListBox.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent arg0) {
				if(mainListBox.getSelectedIndex() == 1){
					subContainer.clear();
					createTableUi();
				}
			}
		});
		
		hpLbContainer.add(lblplaeseSelect);
		hpLbContainer.add(mainListBox);
		mainContainer.add(hpLbContainer);
	}
	
	private void createTableUi() {
		createHeaderLblUi("Table");
		
		HorizontalPanel hpBtn = new HorizontalPanel();
		Button btnSave = new Button("Save");
		Button btnClear = new Button("Clear");
		
		Label lblWidth = new Label();
		lblWidth.setText("Width");
		lblWidth.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbWidth = new TextBox();
		tbWidth.addValueChangeHandler(this);
		tbWidth.setText(configuration.getTableWidth());
		baseTableUi.setWidget(0, 0, lblWidth);
		baseTableUi.setWidget(0, 2, tbWidth);
		
		Label lblBgColor = new Label();
		lblBgColor.setText("Background Color");
		lblBgColor.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbBgColor = new TextBox();
		tbBgColor.addValueChangeHandler(this);
		tbBgColor.setText(configuration.getTableBackgroundColor());
		baseTableUi.setWidget(2, 0, lblBgColor);
		baseTableUi.setWidget(2, 2, tbBgColor);

		final RadioButton radioBtn = new RadioButton("Select_BackImage");
		final RadioButton radioBtn2 = new RadioButton("Select_BackImage");
		
		String byFileUploader = configuration.getUseUploadedImg();
		String url = configuration.getTableBackgroundImageUrl();
		Image image;
		
		if(byFileUploader.equals("true")||byFileUploader.equals("")){
			radioBtn.setValue(true);
			fileUploader=true;
			if(url.equals(""))
				image = new Image("config/NoImage.png");
			else
				image = new Image(url);
			image.setStylePrimaryName("configImage");
			baseTableUi.setWidget(4, 5, image);
		}
		else{
			radioBtn2.setValue(true);
			fileUploader=false;
			String imgUrl =configuration.getTableBackgroundImage();
			Image img;
			if(imgUrl.equals(""))
				img = new Image("config/NoImage.png");
			else
				img = new Image(imgUrl);
			img.setStylePrimaryName("configImage");
			baseTableUi.setWidget(6, 3, img);
		}
		
		radioBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(radioBtn.getValue()){
					configuration.setUseUploadedImg("true");
					radioBtn2.setValue(false);
					fileUploader=true;
				}
			}
		});
		
		radioBtn2.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(radioBtn2.getValue()){
					configuration.setUseUploadedImg("false");
					radioBtn.setValue(false);
					fileUploader=false;
				}
			}
		});
		
		Label lblBgImage = new Label();
		lblBgImage.setText("Background Image");
		lblBgImage.setStylePrimaryName("labelFlexEditNewsItems");
		final FileUpload fuBackimage = new FileUpload();
		fuBackimage.setName("imageFile");
		HorizontalPanel hPanel1 = new HorizontalPanel();
		hPanel1.add(radioBtn);
		hPanel1.add(lblBgImage);
		baseTableUi.setWidget(4, 0, hPanel1);
		baseTableUi.setWidget(4, 2, fuBackimage);
		
		final TextBox tbBgImage = new TextBox();
		tbBgImage.addValueChangeHandler(this);
		tbBgImage.setText(configuration.getTableBackgroundImage());
		
		Label lblBckgImage = new Label();
		lblBckgImage.setText("Background Image");
		lblBckgImage.setStylePrimaryName("labelFlexEditNewsItems");
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(radioBtn2);
		hPanel.add(lblBckgImage);
		baseTableUi.setWidget(6, 0, hPanel);
		baseTableUi.setWidget(6, 2, tbBgImage);
		
		baseTableUi.setWidget(8, 0, hpBtn);
		btnSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (fileUploader) {
					configuration.setTableBackgroundColor(tbBgColor.getText());
					configuration.setTableWidth(tbWidth.getText());
					HorizontalPanel formFieldsPanel = new HorizontalPanel();
					String xmlConfig = configuration.getDomAsString();
					Hidden xml = new Hidden();
					xml.setName("xml");
					xml.setValue(xmlConfig);
					Hidden type = new Hidden();
					type.setName("type");
					type.setValue("outline");
					formFieldsPanel.add(type);
					formFieldsPanel.add(xml);
					formFieldsPanel.add(fuBackimage);
					String gwturlbase = GWT.getHostPageBaseURL();
					String url = gwturlbase.substring(0,gwturlbase.length() - 1);
					subContainer.add(form);
					form.setEncoding(FormPanel.ENCODING_MULTIPART);
					form.setMethod(FormPanel.METHOD_POST);
					form.setWidget(formFieldsPanel);
					form.setAction(url + "/newsletterConfig");
					form.submit();
					form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
						
						@Override
						public void onSubmitComplete(SubmitCompleteEvent event) {
							getImageFromDB(LHadmin.industryId, "bgImg");
							baseTableUi.setWidget(4, 2, fuBackimage);
						}
					});
				}
				else{
					configuration.setTableBackgroundColor(tbBgColor.getText());
					configuration.setTableWidth(tbWidth.getText());
					configuration.setTableBackgroundImage(tbBgImage.getText());
					String xmlConfig = configuration.getDomAsString();
					saveConfiguration(xmlConfig);
				}
			}
		});
		
		btnClear.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				tbWidth.setText("");
				tbBgColor.setText("");
				tbBgImage.setText("");
			}
		});
		hpBtn.setSpacing(5);
		hpBtn.add(btnSave);
		hpBtn.add(btnClear);
		subContainer.add(baseTableUi);
	}
	
	private void getImageFromDB(int industryId, String string) {
		NewsletterConfigServiceAsync service = (NewsletterConfigServiceAsync)GWT.create(NewsletterConfigService.class);
		service.getImageUrl(industryId,string, true, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(String result) {
				Image image = new Image(result);
				image.setStylePrimaryName("configImage");
				baseTableUi.setWidget(4, 5, image);
			}
		});
	}
	
	private void saveConfiguration(String xmlConfig) {
		NewsletterConfigServiceAsync async = GWT.create(NewsletterConfigService.class);
		AsyncCallback callback = new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				hasValueChanged = true;
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Could not update changes");
				popup.setPopupPosition(mainContainer.getAbsoluteLeft()+400, mainContainer.getAbsoluteTop()+100);
				popup.show();
			}

			@Override
			public void onSuccess(Object result) {
				hasValueChanged = false;
				String imgUrl =configuration.getTableBackgroundImage();
				Image image;
				if(!imgUrl.equals("")){
					image = new Image(imgUrl);
					image.setStylePrimaryName("configImage");
					baseTableUi.setWidget(6, 3, image);
				}
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Changes successfully saved.");
				popup.setPopupPosition(mainContainer.getAbsoluteLeft()+400, mainContainer.getAbsoluteTop()+100);
				popup.show();
				
			}
		};
		async.saveNewsletterOutlineConfig(xmlConfig, callback);
	}
	
	public void createHeaderLblUi(String text){
		Label headerLblUi = new Label();
		headerLblUi.setText(text);
		headerLblUi.setStylePrimaryName("newslettereditLbl");
		subContainer.add(headerLblUi);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		hasValueChanged = true;
	}
}
