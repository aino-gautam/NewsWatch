package com.lighthouse.admin.client.siloui;

import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.lighthouse.admin.client.LHadmin;
import com.lighthouse.newsletter.client.NewsletterConfigService;
import com.lighthouse.newsletter.client.NewsletterConfigServiceAsync;

public class ManageNewsletterHeader extends Composite implements ValueChangeHandler<String>{

	private VerticalPanel mainContainer = new VerticalPanel(); 
	
	private VerticalPanel subContainer = new VerticalPanel(); 
	private Label headerLblUi ;
	
	final TextBox tbFontFamily = new TextBox();
	final TextBox tbColor = new TextBox();
	final TextBox tbFontSize = new TextBox();
	final TextBox tbFontWeight = new TextBox();
	final TextBox tbFontStyle = new TextBox();
	final TextBox tbFontAlignment = new TextBox();
	private FormPanel form = new FormPanel();
	private boolean fileUploader = false;
	FlexTable baseUiLogo = new FlexTable();
	HeaderDesignConfiguration configuration;
	public static boolean hasValueChanged = false;
	public ManageNewsletterHeader() {
		initWidget(mainContainer);
	}

	/**
	 * iniitializes the page
	 */

	public void initialize(){
			NewsletterConfigServiceAsync service = (NewsletterConfigServiceAsync)GWT.create(NewsletterConfigService.class);
			service.getNewsletterHeaderConfig(new AsyncCallback<String>() {

				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}

				public void onSuccess(String result) {
					configuration = new HeaderDesignConfiguration(result);
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
	
	public void createMainList(){
		HorizontalPanel hpLbContainer = new HorizontalPanel();
		Label lblplaeseSelect = new Label();
		lblplaeseSelect.setText("Please select an element to style :");
		lblplaeseSelect.setStylePrimaryName("messageLabels");
		
		final ListBox mainListBox = new ListBox();
		mainListBox.addItem("----Select----");
		mainListBox.addItem("Alert name");
		mainListBox.addItem("Logo");
		mainListBox.addItem("Date");
		mainListBox.addItem("Table");
		/*mainListBox.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(hasValueChanged){
					VerticalPanel panel = new VerticalPanel();
					final PopupPanel popup = new PopupPanel();
					Button okbtn = new Button("Ok");
					okbtn.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							popup.hide();
						}
					});
					
					panel.add(new Label("You have unsaved changes. Please click the save button to save the changes before proceeding."));
					panel.add(okbtn);
					popup.add(panel);
					popup.setPopupPosition(mainContainer.getAbsoluteLeft()+400, mainContainer.getAbsoluteTop()+100);
					popup.show();
				}
			}
		});*/
		mainListBox.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent arg0) {
				
				if(hasValueChanged){
					VerticalPanel panel = new VerticalPanel();
					final PopupPanel popup = new PopupPanel();
					Button okbtn = new Button("Ok");
					okbtn.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							popup.hide();
						}
					});
					
					panel.add(new Label("You have unsaved changes. Please click the save button to save the changes before proceeding."));
					panel.add(okbtn);
					popup.add(panel);
					popup.setPopupPosition(mainContainer.getAbsoluteLeft()+400, mainContainer.getAbsoluteTop()+100);
					popup.show();
					arg0.stopPropagation();
					arg0.preventDefault();
				}else{
					if(mainListBox.getSelectedIndex() == 1){
						subContainer.clear();
						createAlertnameUi();
					}
					else if(mainListBox.getSelectedIndex() == 2){
						subContainer.clear();
						selectLogoUi();
					}
					else if(mainListBox.getSelectedIndex() == 3){
						subContainer.clear();
						createDateUi();
					}
					else if(mainListBox.getSelectedIndex() == 4){
						subContainer.clear();
						createTableUi();
					}
				}
			}
			
		});
		tbFontFamily.addValueChangeHandler(this);
		tbColor.addValueChangeHandler(this);
		tbFontSize.addValueChangeHandler(this);
		tbFontWeight.addValueChangeHandler(this);
		tbFontStyle.addValueChangeHandler(this);
		tbFontAlignment.addValueChangeHandler(this);
		
		hpLbContainer.add(lblplaeseSelect);
		hpLbContainer.add(mainListBox);
		mainContainer.add(hpLbContainer);
	}
	
	public void createAlertnameUi(){
		createHeaderLblUi("Alert Name");
		createCommonUI();
	
	}
	
	public void selectLogoUi(){
		createHeaderLblUi("Logo");
		
		HorizontalPanel hpBtn = new HorizontalPanel();
		
		Button btnSave = new Button("Save");
		Button btnClear = new Button("Clear");
		
		Label lblAlignment = new Label();
		lblAlignment.setText("Alignment");
		lblAlignment.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbAlihnment = new TextBox();
		tbAlihnment.addValueChangeHandler(this);
		tbAlihnment.setText(configuration.getLogoAlignment());
		baseUiLogo.setWidget(0, 1, lblAlignment);
		baseUiLogo.setWidget(0, 2, tbAlihnment);
			
		final RadioButton radioBtn = new RadioButton("Select_Logo");
		final RadioButton radioBtn2 = new RadioButton("Select_Logo");
		
		String byFileUploader = configuration.getUseUploadedImg();
		String url = configuration.getLogoImageUploadUrl();
		Image image;
		
		if(byFileUploader.equals("true")||byFileUploader.equals("")){
			radioBtn.setValue(true);
			fileUploader=true;
			if(url.equals(""))
				image = new Image("config/NoImage.png");
			else
				image = new Image(url);
			image.setStylePrimaryName("configImage");
			baseUiLogo.setWidget(2, 3, image);
		}
		else{
			radioBtn2.setValue(true);
			fileUploader=false;
			String imgUrl =configuration.getLogoImage();
			Image img;
			if(imgUrl.equals(""))
				img = new Image("config/NoImage.png");
			else
				img = new Image(imgUrl);
			img.setStylePrimaryName("configImage");
			baseUiLogo.setWidget(3, 3, img);
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
		
		Label lblLogoupload = new Label();
		lblLogoupload.setText("Logo Image");
		lblLogoupload.setStylePrimaryName("labelFlexEditNewsItems");
		
		Label lblLogoUrl = new Label();
		lblLogoUrl.setText("Logo Url");
		lblLogoUrl.setStylePrimaryName("labelFlexEditNewsItems");
	
		final FileUpload fuLogoimage = new FileUpload();
		fuLogoimage.setName("imageFile");
		final TextBox tblogoImgTb = new TextBox();
		tblogoImgTb.addValueChangeHandler(this);
		tblogoImgTb.setText(configuration.getLogoImage());
		baseUiLogo.setWidget(2, 0, radioBtn);
		baseUiLogo.setWidget(2, 1, lblLogoupload);
		baseUiLogo.setWidget(2, 2, fuLogoimage);
		
		
		baseUiLogo.setWidget(3, 0, radioBtn2);
		baseUiLogo.setWidget(3, 1, lblLogoUrl);
		baseUiLogo.setWidget(3, 2, tblogoImgTb);
		
		
		Label lblTargetUrl = new Label();
		lblTargetUrl.setText("Target Url");
		lblTargetUrl.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbTargetUrl = new TextBox();
		tbTargetUrl.addValueChangeHandler(this);
		tbTargetUrl.setText(configuration.getLogoTargetUrl());
		baseUiLogo.setWidget(4, 1, lblTargetUrl);
		baseUiLogo.setWidget(4, 2, tbTargetUrl);
		
		Label lblAlternateText = new Label();
		lblAlternateText.setText("Alternate Text");
		lblAlternateText.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbAlternateText = new TextBox();
		tbAlternateText.addValueChangeHandler(this);
		tbAlternateText.setText(configuration.getLogoAlternativeText());
		baseUiLogo.setWidget(6, 1, lblAlternateText);
		baseUiLogo.setWidget(6, 2, tbAlternateText);
		
		baseUiLogo.setWidget(8, 1, hpBtn);
		
		btnSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if (fileUploader) {
					configuration.setLogoAlignment(tbAlihnment.getText());
					configuration.setLogoAlternativeText(tbAlternateText.getText());
					configuration.setLogoTargetUrl(tbTargetUrl.getText());
					HorizontalPanel formFieldsPanel = new HorizontalPanel();
					String xmlConfig = configuration.getDomAsString();
					Hidden xml = new Hidden();
					xml.setName("xml");
					xml.setValue(xmlConfig);
					Hidden type = new Hidden();
					type.setName("type");
					type.setValue("header");
					formFieldsPanel.add(type);
					formFieldsPanel.add(xml);
					formFieldsPanel.add(fuLogoimage);
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
							getImageFromDB(LHadmin.industryId, "logo");
							baseUiLogo.setWidget(2, 2, fuLogoimage);
						}
					});
				}
				else{
					configuration.setLogoAlignment(tbAlihnment.getText());
					configuration.setLogoAlternativeText(tbAlternateText.getText());
					configuration.setLogoTargetUrl(tbTargetUrl.getText());
					configuration.setLogoImage(tblogoImgTb.getText());
					String xmlConfig = configuration.getDomAsString();
					saveConfiguration(xmlConfig);
				}
			}
		});
		
		btnClear.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				tbAlihnment.setText("");
				tbTargetUrl.setText("");
				tbAlternateText.setText("");
			}
		});
		hpBtn.setSpacing(5);
		hpBtn.add(btnSave);
		hpBtn.add(btnClear);
		subContainer.add(baseUiLogo);
	}
	
	private void getImageFromDB(int industryId, String string) {
		NewsletterConfigServiceAsync service = (NewsletterConfigServiceAsync)GWT.create(NewsletterConfigService.class);
		service.getImageUrl(industryId,string, true, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(String result) {
				if(baseUiLogo.isCellPresent(2, 3))
					baseUiLogo.clearCell(2, 3);
				Image image = new Image(result);
				image.setStylePrimaryName("configImage");
				baseUiLogo.setWidget(2, 3, image);
			}
		});
	}
	
	public void createDateUi(){
		createHeaderLblUi("Date");
		createCommonUI();
	}
	
	public void createTableUi(){
		createHeaderLblUi("Table");
		FlexTable baseTableUi = new FlexTable();
		HorizontalPanel hpBtn = new HorizontalPanel();
		
		Button btnSave = new Button("Save");
		
		Button btnClear = new Button("Clear");
		
		Label lblWidth = new Label();
		lblWidth.setText("Width");
		lblWidth.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbWidth = new TextBox();
		tbWidth.addValueChangeHandler(this);
		tbWidth.setText(configuration.getBaseTableWidth());
		baseTableUi.setWidget(0, 0, lblWidth);
		baseTableUi.setWidget(0, 2, tbWidth);
		
		Label lblCellSpacing = new Label();
		lblCellSpacing.setText("Cell Spacing");
		lblCellSpacing.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbCellspacing = new TextBox();
		tbCellspacing.addValueChangeHandler(this);
		tbCellspacing.setText(configuration.getBaseTableCellSpacing());
		baseTableUi.setWidget(2, 0, lblCellSpacing);
		baseTableUi.setWidget(2, 2, tbCellspacing);
		
		Label lblCellPadding = new Label();
		lblCellPadding.setText("Cell Padding");
		lblCellPadding.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbCellPadding = new TextBox();
		tbCellPadding.addValueChangeHandler(this);
		tbCellPadding.setText(configuration.getBaseTableCellPadding());
		baseTableUi.setWidget(4, 0, lblCellPadding);
		baseTableUi.setWidget(4, 2, tbCellPadding);
		
		Label lblBgColor = new Label();
		lblBgColor.setText("Background Color");
		lblBgColor.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbBgColor = new TextBox();
		tbBgColor.addValueChangeHandler(this);
		tbBgColor.setText(configuration.getBaseTableBackgroundColor());
		baseTableUi.setWidget(6, 0, lblBgColor);
		baseTableUi.setWidget(6, 2, tbBgColor);
		
		baseTableUi.setWidget(8, 0, hpBtn);
		
		btnSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				configuration.setBaseTableWidth(tbWidth.getText());
				configuration.setBaseTableBackgroundColor(tbBgColor.getText());
				configuration.setBaseTableCellPadding(tbCellPadding.getText());
				configuration.setBaseTableCellSpacing(tbCellspacing.getText());
				String xmlconfig = configuration.getDomAsString();
				saveConfiguration(xmlconfig);
			}
		});
		
		btnClear.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				tbWidth.setText("");
				tbCellspacing.setText("");
				tbCellPadding.setText("");
				tbBgColor.setText("");
			}
		});
		hpBtn.setSpacing(5);
		hpBtn.add(btnSave);
		hpBtn.add(btnClear);
		subContainer.add(baseTableUi);
	}
	
	public void createHeaderLblUi(String text){
		headerLblUi = new Label();
		headerLblUi.setText(text);
		headerLblUi.setStylePrimaryName("newslettereditLbl");
		subContainer.add(headerLblUi);
	}
	
	public void createCommonUI(){
			FlexTable baseUiAlertName = new FlexTable();
			
			HorizontalPanel hpBtn = new HorizontalPanel();
			Button btnSave = new Button("Save");
			Button btnClear = new Button("Clear");
			
			getValuesInTextBox();
			Label lblFontFamily = new Label();
			lblFontFamily.setText("Font Family");
			lblFontFamily.setStylePrimaryName("labelFlexEditNewsItems");
			baseUiAlertName.setWidget(0, 0, lblFontFamily);
			baseUiAlertName.setWidget(0, 2, tbFontFamily);
			
			Label lblFontColor = new Label();
			lblFontColor.setText("Font Color");
			lblFontColor.setStylePrimaryName("labelFlexEditNewsItems");
			
			Label lblFormat = new Label();
			lblFormat.setStylePrimaryName("SuggestionTextColor");
			lblFormat.setText("hex format : eg. #fff");
			
			baseUiAlertName.setWidget(2, 0, lblFontColor);
			baseUiAlertName.setWidget(2, 2, tbColor);
			baseUiAlertName.setWidget(2, 3, lblFormat);
			
			Label lblFontSize = new Label();
			lblFontSize.setText("Font Size");
			lblFontSize.setStylePrimaryName("labelFlexEditNewsItems");
			
			baseUiAlertName.setWidget(4, 0, lblFontSize);
			baseUiAlertName.setWidget(4, 2, tbFontSize);
			
			Label lblFontWeight = new Label();
			lblFontWeight.setText("Font Weight");
			lblFontWeight.setStylePrimaryName("labelFlexEditNewsItems");
			
			baseUiAlertName.setWidget(6, 0, lblFontWeight);
			baseUiAlertName.setWidget(6, 2, tbFontWeight);
			
			Label lblFontStyle = new Label();
			lblFontStyle.setText("Font Style");
			lblFontStyle.setStylePrimaryName("labelFlexEditNewsItems");
			
			baseUiAlertName.setWidget(8, 0, lblFontStyle);
			baseUiAlertName.setWidget(8, 2, tbFontStyle);
			
			Label lblFontAlignment = new Label();
			lblFontAlignment.setText("Font Alignment");
			lblFontAlignment.setStylePrimaryName("labelFlexEditNewsItems");
			
			
			baseUiAlertName.setWidget(10, 0, lblFontAlignment);
			baseUiAlertName.setWidget(10, 2, tbFontAlignment);
			
			baseUiAlertName.setWidget(12, 0, hpBtn);
			
			btnSave.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(headerLblUi.getText().equals("Date")){
						configuration.setDateFontColor(tbColor.getText());
						configuration.setDateFontFamily(tbFontFamily.getText());
						configuration.setDateFontSize(tbFontSize.getText());
						configuration.setDateFontStyle(tbFontStyle.getText());
						configuration.setDateFontWeight(tbFontWeight.getText());
						configuration.setDateTextAlignment(tbFontAlignment.getText());
						String xmlconfig = configuration.getDomAsString();
						saveConfiguration(xmlconfig);
					}
					else if(headerLblUi.getText().equals("Alert Name")){
						configuration.setAlertNameFontColor(tbColor.getText());
						configuration.setAlertNameFontFamily(tbFontFamily.getText());
						configuration.setAlertNameFontSize(tbFontSize.getText());
						configuration.setAlertNameFontStyle(tbFontStyle.getText());
						configuration.setAlertNameFontWeight(tbFontWeight.getText());
						configuration.setAlertNameTextAlignment(tbFontAlignment.getText());
						String xmlconfig = configuration.getDomAsString();
						saveConfiguration(xmlconfig);
					}
				}
			});
			
			btnClear.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					tbFontFamily.setText("");
					tbColor.setText("");
					tbFontSize.setText("");
					tbFontWeight.setText("");
					tbFontStyle.setText("");
					tbFontAlignment.setText("");
				}
			});
			hpBtn.setSpacing(5);
			hpBtn.add(btnSave);
			hpBtn.add(btnClear);
			subContainer.add(baseUiAlertName);
		}

	
	private void getValuesInTextBox() {
		if(headerLblUi.getText().equals("Date")){
			tbColor.setText(configuration.getDateFontColor());
			tbFontAlignment.setText(configuration.getDateTextAlignment());
			tbFontFamily.setText(configuration.getDateFontFamily());
			tbFontSize.setText(configuration.getDateFontSize());
			tbFontStyle.setText(configuration.getDateFontStyle());
			tbFontWeight.setText(configuration.getDateFontWeight());
		}
		else if(headerLblUi.getText().equals("Alert Name")){
			tbColor.setText(configuration.getAlertNameFontColor());
			tbFontAlignment.setText(configuration.getAlertNameTextAlignment());
			tbFontFamily.setText(configuration.getAlertNameFontFamily());
			tbFontSize.setText(configuration.getAlertNameFontSize());
			tbFontStyle.setText(configuration.getAlertNameFontStyle());
			tbFontWeight.setText(configuration.getAlertNameFontWeight());
		}
	}
	
	private void saveConfiguration(String xmlconfig) {
		NewsletterConfigServiceAsync async = GWT.create(NewsletterConfigService.class);
		AsyncCallback callback = new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				hasValueChanged = true;
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Could not update changes");
				popup.setPopupPosition(mainContainer.getAbsoluteLeft()+400, mainContainer.getAbsoluteTop()+400);
				popup.show();
			}

			@Override
			public void onSuccess(Object result) {
				if((Boolean) result){
					hasValueChanged = false;
					String imgUrl =configuration.getLogoImage();
					Image image;
					if(!imgUrl.equals("")){
						image = new Image(imgUrl);
						image.setStylePrimaryName("configImage");
						baseUiLogo.setWidget(3, 3, image);
					}
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Changes successfully saved.");
					popup.setPopupPosition(mainContainer.getAbsoluteLeft()+400, mainContainer.getAbsoluteTop()+100);
					popup.show();
				}
				else{
					hasValueChanged = true;
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Could not update changes");
					popup.setPopupPosition(mainContainer.getAbsoluteLeft()+400, mainContainer.getAbsoluteTop()+100);
					popup.show();
				}
			}
		};
		async.saveNewsletterHeaderConfig(xmlconfig, callback);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		hasValueChanged = true;
	}
}
