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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.newsletter.client.NewsletterConfigService;
import com.lighthouse.newsletter.client.NewsletterConfigServiceAsync;

public class ManageNewsletterContent extends Composite implements ClickHandler,ChangeHandler,ValueChangeHandler<String>{
	
	private VerticalPanel vpBase = new VerticalPanel();
	private ListBox lbSubOption;
	
	private static final String TABLE = " Base Table";
	private static final String FIRSTCOLUMN = "First (News) Column";
	private static final String SECONDCOLUMN = "Second (Side) Column";
	private static final String PULSE = "Pulse";
	private static final String REPORT = "Report";
	private static final String FAVORITES = "Favorites";
	private static final String TAGS = "Tags";
	private static final String SOURCE = "Source";
	private static final String PUBLISHED_DATE = "Published Date";
	private static final String NEWS_TITLE = "News Title";
	private static final String HEADLINE = "Headline";
	private static final String ABSTRACT = "Abstract";
	
	private HorizontalPanel hpBasePanel = new HorizontalPanel(); 
	private HorizontalPanel hpSelectedHeaderPanel = new HorizontalPanel(); 
	private HorizontalPanel hpChkPanel = new HorizontalPanel(); 
	private CheckBox enableChk = new CheckBox("Enable");
	private CheckBox disableChk = new CheckBox("Disable");
	
	private Label headerLbl;
	final TextBox tbFontFamily = new TextBox();
	final TextBox tbColor = new TextBox();
	final TextBox tbFontSize = new TextBox();
	final TextBox tbFontWeight = new TextBox();
	final TextBox tbFontPadding = new TextBox();
	final TextBox tbFontStyle = new TextBox();
	
	final TextBox tbPRFBgcolor = new TextBox();
	final TextBox tbPRFHeadercolor  = new TextBox();
	final TextBox tbPRFItemcolor = new TextBox();
	final TextBox tbPRFRulerColor = new TextBox();
	
	final TextBox tbColumnPadding = new TextBox();
	final TextBox tbColumnBorder  = new TextBox();
	final TextBox tbColumnAlign = new TextBox();
	final TextBox tbColumnWidth = new TextBox();
	final TextBox tbColumnVAlign = new TextBox();
	
	ContentDesignConfiguration configuration;
	public static boolean hasValueChanged = false;
	public ManageNewsletterContent() {
		initWidget(vpBase);
	}
	
	public void initialize(){
		NewsletterConfigServiceAsync service = (NewsletterConfigServiceAsync)GWT.create(NewsletterConfigService.class);
		service.getNewsletterContentConfig(new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			public void onSuccess(String result) {
				configuration = new ContentDesignConfiguration(result);
				vpBase.clear();
				createUI();
			}
		});
	}
	
	private void createUI() {
		lbSubOption = new ListBox();
		
		lbSubOption.addItem("----- Please Select -----");
		lbSubOption.addItem(NEWS_TITLE);
		lbSubOption.addItem(SOURCE);
		lbSubOption.addItem(ABSTRACT);
		lbSubOption.addItem(PUBLISHED_DATE);
		lbSubOption.addItem(TAGS);
		lbSubOption.addItem(FAVORITES);
		lbSubOption.addItem(REPORT);
		lbSubOption.addItem(PULSE);
		lbSubOption.addItem(TABLE);
		lbSubOption.addItem(FIRSTCOLUMN);
		lbSubOption.addItem(SECONDCOLUMN);
		lbSubOption.addItem(HEADLINE);
		/*lbSubOption.addClickHandler(new ClickHandler() {
			
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
					popup.setPopupPosition(vpBase.getAbsoluteLeft()+400, vpBase.getAbsoluteTop()+100);
					popup.show();
				}
			}
		});*/
		lbSubOption.addChangeHandler(this);
		enableChk.addClickHandler(this);
		disableChk.addClickHandler(this);
		hpChkPanel.add(enableChk);
		hpChkPanel.add(disableChk);
		
		tbFontFamily.addValueChangeHandler(this);
		tbColor.addValueChangeHandler(this);
		tbFontSize.addValueChangeHandler(this);
		tbFontWeight.addValueChangeHandler(this);
		tbFontPadding.addValueChangeHandler(this);
		tbFontStyle.addValueChangeHandler(this);
		
		tbPRFBgcolor.addValueChangeHandler(this);
		tbPRFHeadercolor.addValueChangeHandler(this);
		tbPRFItemcolor.addValueChangeHandler(this);
		tbPRFRulerColor.addValueChangeHandler(this);
		
		tbColumnPadding.addValueChangeHandler(this);
		tbColumnBorder.addValueChangeHandler(this);
		tbColumnAlign.addValueChangeHandler(this);
		tbColumnWidth.addValueChangeHandler(this);
		tbColumnVAlign.addValueChangeHandler(this);
		
		vpBase.setSpacing(5);
		Label lblheading = new Label("Please select style: ");
		lblheading.setStylePrimaryName("messageLabels");
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setSpacing(5);
		hPanel.add(lblheading);
		hPanel.add(lbSubOption);
		vpBase.add(hPanel);
		hpBasePanel.setSpacing(3);
		vpBase.add(hpSelectedHeaderPanel);
		vpBase.add(hpBasePanel);
	}
	
	public void createHeaderLabel(String text){
		hpSelectedHeaderPanel.clear();
		headerLbl = new Label(text);
		headerLbl.setStylePrimaryName("newslettereditLbl");
		hpSelectedHeaderPanel.add(headerLbl);
	}
	
	public void getFontFieldsWithPaddingOption() {
		FlexTable fontFlex = new FlexTable();
		
		HorizontalPanel hpBtn = new HorizontalPanel();
		Button btnSave = new Button("Save");
		Button btnClear = new Button("Clear");
		
		setFontValuesInFields();
		
		Label lblFontFamily = new Label("Font Family");
		lblFontFamily.setStylePrimaryName("labelFlexEditNewsItems");
		fontFlex.setWidget(0, 0, lblFontFamily);
		fontFlex.setWidget(0, 2, tbFontFamily);
		
		Label lblFontColor = new Label("Font Color");
		lblFontColor.setStylePrimaryName("labelFlexEditNewsItems");
		
		Label lblFormat = new Label("hex format : eg. #fff");
		lblFormat.setStylePrimaryName("SuggestionTextColor");
		fontFlex.setWidget(2, 0, lblFontColor);
		fontFlex.setWidget(2, 2, tbColor);
		fontFlex.setWidget(2, 3, lblFormat);
		
		Label lblFontSize = new Label("Font Size");
		lblFontSize.setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(4, 0, lblFontSize);
		fontFlex.setWidget(4, 2, tbFontSize);
		
		Label lblFontWeight = new Label("Font Weight");
		lblFontWeight.setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(6, 0, lblFontWeight);
		fontFlex.setWidget(6, 2, tbFontWeight);
		
		Label lblFontStyle = new Label("Font Style");
		lblFontStyle.setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(8, 0, lblFontStyle);
		fontFlex.setWidget(8, 2, tbFontStyle);
		
		Label lblFontPadding = new Label("Padding");
		lblFontPadding.setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(10, 0, lblFontPadding);
		fontFlex.setWidget(10, 2, tbFontPadding);
		
		fontFlex.setWidget(12, 0, hpBtn);
		
		btnSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(headerLbl.getText().equals(HEADLINE)){
					configuration.setHeadlineFontFamily(tbFontFamily.getText());
					configuration.setHeadlineFontColor(tbColor.getText());
					configuration.setHeadlineFontSize(tbFontSize.getText());
					configuration.setHeadlineFontWeight(tbFontWeight.getText());
					configuration.setHeadlinePadding(tbFontPadding.getText());
					configuration.setHeadlineFontStyle(tbFontStyle.getText());
					String xml = configuration.getDomAsString();
					saveConfiguration(xml);
				}
				else if(headerLbl.getText().equals(NEWS_TITLE)){
					configuration.setNewsTitleFontFamily(tbFontFamily.getText());
					configuration.setNewsTitleFontColor(tbColor.getText());
					configuration.setNewsTitleFontSize(tbFontSize.getText());
					configuration.setNewsTitleFontWeight(tbFontWeight.getText());
					configuration.setNewsTitlePadding(tbFontPadding.getText());
					configuration.setNewsTitleFontStyle(tbFontStyle.getText());
					String xml = configuration.getDomAsString();
					saveConfiguration(xml);
				}
				else if(headerLbl.getText().equals(ABSTRACT)){
					configuration.setAbstractFontFamily(tbFontFamily.getText());
					configuration.setAbstractFontColor(tbColor.getText());
					configuration.setAbstractFontSize(tbFontSize.getText());
					configuration.setAbstractFontWeight(tbFontWeight.getText());
					configuration.setAbstractPadding(tbFontPadding.getText());
					configuration.setAbstractFontStyle(tbFontStyle.getText());
					String xml = configuration.getDomAsString();
					saveConfiguration(xml);
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
				tbFontPadding.setText("");
			}
		});
		hpBtn.setSpacing(5);
		hpBtn.add(btnSave);
		hpBtn.add(btnClear);
		hpBasePanel.add(fontFlex);
	}
	
	private void setFontValuesInFields() {
		if(headerLbl.getText().equals(HEADLINE)){
			tbFontFamily.setText(configuration.getHeadlineFontFamily());
			tbColor.setText(configuration.getHeadlineFontColor());
			tbFontSize.setText(configuration.getHeadlineFontSize());
			tbFontWeight.setText(configuration.getHeadlineFontWeight());
			tbFontPadding.setText(configuration.getHeadlinePadding());
			tbFontStyle.setText(configuration.getHeadlineFontStyle());
		}
		else if(headerLbl.getText().equals(NEWS_TITLE)){
			tbFontFamily.setText(configuration.getNewsTitleFontFamily());
			tbColor.setText(configuration.getNewsTitleFontColor());
			tbFontSize.setText(configuration.getNewsTitleFontSize());
			tbFontWeight.setText(configuration.getNewsTitleFontWeight());
			tbFontPadding.setText(configuration.getNewsTitlePadding());
			tbFontStyle.setText(configuration.getNewsTitleFontStyle());
		}
		else if(headerLbl.getText().equals(ABSTRACT)){
			tbFontFamily.setText(configuration.getAbstractFontFamily());
			tbColor.setText(configuration.getAbstractFontColor());
			tbFontSize.setText(configuration.getAbstractFontSize());
			tbFontWeight.setText(configuration.getAbstractFontWeight());
			tbFontPadding.setText(configuration.getAbstractPadding());
			tbFontStyle.setText(configuration.getAbstractFontStyle());
		}
	}

	public void getFontFieldsWithEnableDisableOption() {
		FlexTable fontFlex = new FlexTable();
		
		HorizontalPanel hpBtn = new HorizontalPanel();
		Button btnSave = new Button("Save");
		Button btnClear = new Button("Clear");
		
		setFontEDValuesInFields();
		Label lblFontFamily = new Label("Font Family");
		lblFontFamily.setStylePrimaryName("labelFlexEditNewsItems");
		fontFlex.setWidget(0, 0, lblFontFamily);
		fontFlex.setWidget(0, 2, tbFontFamily);
		
		Label lblFontColor = new Label("Font Color");
		lblFontColor.setStylePrimaryName("labelFlexEditNewsItems");
		Label lblFormat = new Label("hex format : eg. #fff");
		lblFormat.setStylePrimaryName("SuggestionTextColor");
		fontFlex.setWidget(2, 0, lblFontColor);
		fontFlex.setWidget(2, 2, tbColor);
		fontFlex.setWidget(2, 3, lblFormat);
		
		Label lblFontSize = new Label("Font Size");
		lblFontSize.setStylePrimaryName("labelFlexEditNewsItems");
		fontFlex.setWidget(4, 0, lblFontSize);
		fontFlex.setWidget(4, 2, tbFontSize);
		
		Label lblFontWeight = new Label("Font Weight");
		lblFontWeight.setStylePrimaryName("labelFlexEditNewsItems");
		fontFlex.setWidget(6, 0, lblFontWeight);
		fontFlex.setWidget(6, 2, tbFontWeight);
		
		Label lblFontStyle = new Label("Font Style");
		lblFontStyle.setStylePrimaryName("labelFlexEditNewsItems");
		fontFlex.setWidget(8, 0, lblFontStyle);
		fontFlex.setWidget(8, 2, tbFontStyle);
		
		Label lblFontAlignment = new Label(headerLbl.getText());
		lblFontAlignment.setStylePrimaryName("labelFlexEditNewsItems");
		fontFlex.setWidget(10, 0, lblFontAlignment);
		fontFlex.setWidget(10, 2, hpChkPanel);
		
		fontFlex.setWidget(12, 0, hpBtn);
		
		btnSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(headerLbl.getText().equals(PUBLISHED_DATE)){
					configuration.setPublishedDateFontFamily(tbFontFamily.getText());
					configuration.setPublishedDateFontColor(tbColor.getText());
					configuration.setPublishedDateFontSize(tbFontSize.getText());
					configuration.setPublishedDateFontWeight(tbFontWeight.getText());
					configuration.setPublishedDateFontStyle(tbFontStyle.getText());
					String val = "false";
					if(enableChk.getValue())
						val = "true";
					configuration.setPublishedDateEnabledOrNot(val);
					String xml = configuration.getDomAsString();
					saveConfiguration(xml);
				}
				else if(headerLbl.getText().equals(SOURCE)){
					configuration.setSourceFontFamily(tbFontFamily.getText());
					configuration.setSourceFontColor(tbColor.getText());
					configuration.setSourceFontSize(tbFontSize.getText());
					configuration.setSourceFontWeight(tbFontWeight.getText());
					configuration.setSourceFontStyle(tbFontStyle.getText());
					String val = "false";
					if(enableChk.getValue())
						val = "true";
					configuration.setSourceEnabledOrNot(val);
					String xml = configuration.getDomAsString();
					saveConfiguration(xml);
				}
				else if(headerLbl.getText().equals(TAGS)){
					configuration.setTagsFontFamily(tbFontFamily.getText());
					configuration.setTagsFontColor(tbColor.getText());
					configuration.setTagsFontSize(tbFontSize.getText());
					configuration.setTagsFontWeight(tbFontWeight.getText());
					configuration.setTagsFontStyle(tbFontStyle.getText());
					String val = "false";
					if(enableChk.getValue())
						val = "true";
					configuration.setTagsEnabledOrNot(val);
					String xml = configuration.getDomAsString();
					saveConfiguration(xml);
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
				enableChk.setValue(false);
				disableChk.setValue(false);
			}
		});
		hpBtn.setSpacing(5);
		hpBtn.add(btnSave);
		hpBtn.add(btnClear);
		hpBasePanel.add(fontFlex);
	}

	private void setFontEDValuesInFields() {
		if(headerLbl.getText().equals(PUBLISHED_DATE)){
			tbFontFamily.setText(configuration.getPublishedDateFontFamily());
			tbColor.setText(configuration.getPublishedDateFontColor());
			tbFontSize.setText(configuration.getPublishedDateFontSize());
			tbFontWeight.setText(configuration.getPublishedDateFontWeight());
			tbFontStyle.setText(configuration.getPublishedDateFontStyle());
			String val = configuration.getPublishedDateEnabledOrNot();
			if(val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes")){
				enableChk.setValue(true);
				disableChk.setValue(false);
			}
			else{
				disableChk.setValue(true);
				enableChk.setValue(false);
			}
		}
		else if(headerLbl.getText().equals(SOURCE)){
			tbFontFamily.setText(configuration.getSourceFontFamily());
			tbColor.setText(configuration.getSourceFontColor());
			tbFontSize.setText(configuration.getSourceFontSize());
			tbFontWeight.setText(configuration.getSourceFontWeight());
			tbFontStyle.setText(configuration.getSourceFontStyle());
			String val = configuration.getSourceEnabledOrNot();
			if(val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes")){
				enableChk.setValue(true);
				disableChk.setValue(false);
			}
			else{
				disableChk.setValue(true);
				enableChk.setValue(false);
			}
		}
		else if(headerLbl.getText().equals(TAGS)){
			tbFontFamily.setText(configuration.getTagsFontFamily());
			tbColor.setText(configuration.getTagsFontColor());
			tbFontSize.setText(configuration.getTagsFontSize());
			tbFontWeight.setText(configuration.getTagsFontWeight());
			tbFontStyle.setText(configuration.getTagsFontStyle());
			String val = configuration.getTagsEnabledOrNot();
			if(val.equalsIgnoreCase("true") || val.equalsIgnoreCase("yes")){
				enableChk.setValue(true);
				disableChk.setValue(false);
			}
			else{
				disableChk.setValue(true);
				enableChk.setValue(false);
			}
		}
	}

	public void getPulseReportFavioriteFields() {
		FlexTable fontFlex = new FlexTable();
		
		HorizontalPanel hpBtn = new HorizontalPanel();
		Button btnSave = new Button("Save");
		Button btnClear = new Button("Clear");
		
		setPulseReportFavioriteValuesInFields();
		
		Label lblPRFBgcolor = new Label("BackGround Color");
		lblPRFBgcolor.setStylePrimaryName("labelFlexEditNewsItems");
		Label lblFormat = new Label("hex format : eg. #fff");
		lblFormat.setStylePrimaryName("SuggestionTextColor");
		
		fontFlex.setWidget(0, 0, lblPRFBgcolor);
		fontFlex.setWidget(0, 2, tbPRFBgcolor);
		fontFlex.setWidget(0, 3, lblFormat);
		
		Label lblPRFHeadercolor = new Label("Header Color");
		lblPRFHeadercolor .setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(2, 0, lblPRFHeadercolor );
		fontFlex.setWidget(2, 2, tbPRFHeadercolor);
		
		
		Label lblPRFItemcolor = new Label("Item Color");
		lblPRFItemcolor.setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(4, 0, lblPRFItemcolor);
		fontFlex.setWidget(4, 2, tbPRFItemcolor);
		
		Label lblRulerColor = new Label("Header horizontal ruler color");
		lblRulerColor.setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(6, 0, lblRulerColor);
		fontFlex.setWidget(6, 2, tbPRFRulerColor);
				
		fontFlex.setWidget(8, 0, hpBtn);
		
		btnSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(headerLbl.getText().equals(PULSE)){
					configuration.setPulseBackgroundColor(tbPRFBgcolor.getText());
					configuration.setPulseHeaderColor(tbPRFHeadercolor.getText());
					configuration.setPulseItemColor(tbPRFItemcolor.getText());
					configuration.setPulseHeaderHorizontalRulerColor(tbPRFRulerColor.getText());
					String xml = configuration.getDomAsString();
					saveConfiguration(xml);
				}
				else if(headerLbl.getText().equals(REPORT)){
					configuration.setReportBackgroundColor(tbPRFBgcolor.getText());
					configuration.setReportHeaderColor(tbPRFHeadercolor.getText());
					configuration.setReportItemColor(tbPRFItemcolor.getText());
					configuration.setReportHeaderHorizontalRulerColor(tbPRFRulerColor.getText());
					String xml = configuration.getDomAsString();
					saveConfiguration(xml);
				}
				if(headerLbl.getText().equals(FAVORITES)){
					configuration.setFavouritesBackgroundColor(tbPRFBgcolor.getText());
					configuration.setFavouritesHeaderColor(tbPRFHeadercolor.getText());
					configuration.setFavouritesItemColor(tbPRFItemcolor.getText());
					configuration.setFavouritesHeaderHorizontalRulerColor(tbPRFRulerColor.getText());
					String xml = configuration.getDomAsString();
					saveConfiguration(xml);
				}
			}
		});
		
		btnClear.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				tbPRFBgcolor.setText("");
				tbPRFHeadercolor.setText("");
				tbPRFItemcolor.setText("");
				tbPRFRulerColor.setText("");
			}
		});
		hpBtn.setSpacing(5);
		hpBtn.add(btnSave);
		hpBtn.add(btnClear);
		hpBasePanel.add(fontFlex);
	}
	
	private void setPulseReportFavioriteValuesInFields() {
		if(headerLbl.getText().equals(PULSE)){
			tbPRFBgcolor.setText(configuration.getPulseBackgroundColor());
			tbPRFHeadercolor.setText(configuration.getPulseHeaderColor());
			tbPRFItemcolor.setText(configuration.getPulseItemColor());
			tbPRFRulerColor.setText(configuration.getPulseHeaderHorizontalRulerColor());
		}
		else if(headerLbl.getText().equals(REPORT)){
			tbPRFBgcolor.setText(configuration.getReportBackgroundColor());
			tbPRFHeadercolor.setText(configuration.getReportHeaderColor());
			tbPRFItemcolor.setText(configuration.getReportItemColor());
			tbPRFRulerColor.setText(configuration.getReportHeaderHorizontalRulerColor());
		}
		else if(headerLbl.getText().equals(FAVORITES)){
			tbPRFBgcolor.setText(configuration.getFavouritesBackgroundColor());
			tbPRFHeadercolor.setText(configuration.getFavouritesHeaderColor());
			tbPRFItemcolor.setText(configuration.getFavouritesItemColor());
			tbPRFRulerColor.setText(configuration.getFavouritesHeaderHorizontalRulerColor());
		}
	}

	public void getColumnFields() {
		FlexTable fontFlex = new FlexTable();
		
		HorizontalPanel hpBtn = new HorizontalPanel();
		Button btnSave = new Button("Save");
		Button btnClear = new Button("Clear");
		
		setColumnValuesInFields();
		
		Label lblColumnPadding = new Label("Padding");
		lblColumnPadding.setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(0, 0, lblColumnPadding);
		fontFlex.setWidget(0, 2, tbColumnPadding);
		
		Label lblColumnBorder = new Label("Border");
		lblColumnBorder .setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(2, 0, lblColumnBorder );
		fontFlex.setWidget(2, 2, tbColumnBorder);
		
		Label lblColumnAlign = new Label("Align");
		lblColumnAlign.setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(4, 0, lblColumnAlign);
		fontFlex.setWidget(4, 2, tbColumnAlign);
		
		Label lblColumnVAlign = new Label("VAlign");
		lblColumnVAlign.setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(6, 0, lblColumnVAlign);
		fontFlex.setWidget(6, 2, tbColumnVAlign);
		
		Label lblColumnWidth = new Label("Width");
		lblColumnWidth.setStylePrimaryName("labelFlexEditNewsItems");
		
		fontFlex.setWidget(8, 0, lblColumnWidth);
		fontFlex.setWidget(8, 2, tbColumnWidth);
				
		fontFlex.setWidget(10, 0, hpBtn);
		
		btnSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(headerLbl.getText().equals(FIRSTCOLUMN)){
					configuration.setFirstColumnAlign(tbColumnAlign.getText());
					configuration.setFirstColumnBorder(tbColumnBorder.getText());
					configuration.setFirstColumnPadding(tbColumnPadding.getText());
					configuration.setFirstColumnVerticalAlign(tbColumnVAlign.getText());
					configuration.setFirstColumnWidth(tbColumnWidth.getText());
					String xml = configuration.getDomAsString();
					saveConfiguration(xml);
				}
				else if(headerLbl.getText().equals(SECONDCOLUMN)){
					configuration.setSecondColumnAlign(tbColumnAlign.getText());
					configuration.setSecondColumnBorder(tbColumnBorder.getText());
					configuration.setSecondColumnPadding(tbColumnPadding.getText());
					configuration.setSecondColumnVerticalAlign(tbColumnVAlign.getText());
					configuration.setSecondColumnWidth(tbColumnWidth.getText());
					String xml = configuration.getDomAsString();
					saveConfiguration(xml);
				}
			}
		});
		
		btnClear.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				tbColumnWidth.setText("");
				tbColumnVAlign.setText("");
				tbColumnBorder.setText("");
				tbColumnAlign.setText("");
				tbColumnPadding.setText("");
			}
		});
		hpBtn.setSpacing(5);
		hpBtn.add(btnSave);
		hpBtn.add(btnClear);
		hpBasePanel.add(fontFlex);
	}
	
	private void setColumnValuesInFields() {
		if(headerLbl.getText().equals(FIRSTCOLUMN)){
			tbColumnAlign.setText(configuration.getFirstColumnAlign());
			tbColumnBorder.setText(configuration.getFirstColumnBorder());
			tbColumnPadding.setText(configuration.getFirstColumnPadding());
			tbColumnVAlign.setText(configuration.getFirstColumnVerticalAlign());
			tbColumnWidth.setText(configuration.getFirstColumnWidth());
		}
		else if(headerLbl.getText().equals(SECONDCOLUMN)){
			tbColumnAlign.setText(configuration.getSecondColumnAlign());
			tbColumnBorder.setText(configuration.getSecondColumnBorder());
			tbColumnPadding.setText(configuration.getSecondColumnPadding());
			tbColumnVAlign.setText(configuration.getSecondColumnVerticalAlign());
			tbColumnWidth.setText(configuration.getSecondColumnWidth());
		}
	}

	public void getTableFields() {
		FlexTable fontFlex = new FlexTable();
		
		HorizontalPanel hpBtn = new HorizontalPanel();
		Button btnSave = new Button("Save");
		Button btnClear = new Button("Clear");
		
		Label lblTableWidth = new Label("Width");
		lblTableWidth.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbTableWidth = new TextBox();
		tbTableWidth.addValueChangeHandler(this);
		tbTableWidth.setText(configuration.getTableWidth());
		fontFlex.setWidget(0, 0, lblTableWidth);
		fontFlex.setWidget(0, 2, tbTableWidth);
		
		Label lblTableCellSpacing = new Label("Cell Spacing");
		lblTableCellSpacing .setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbTableCellSpacing  = new TextBox();
		tbTableCellSpacing.addValueChangeHandler(this);
		tbTableCellSpacing.setText(configuration.getTableCellSpacing());
		fontFlex.setWidget(2, 0, lblTableCellSpacing);
		fontFlex.setWidget(2, 2, tbTableCellSpacing);
		
		Label lblTableCellPadding = new Label("Cell Padding");
		lblTableCellPadding.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox tbTableCellPadding = new TextBox();
		tbTableCellPadding.addValueChangeHandler(this);
		tbTableCellPadding.setText(configuration.getTableCellPadding());
		fontFlex.setWidget(4, 0, lblTableCellPadding);
		fontFlex.setWidget(4, 2, tbTableCellPadding);
		
		Label lblTableBackGroundColor = new Label("BackGround Color");
		lblTableBackGroundColor.setStylePrimaryName("labelFlexEditNewsItems");
		Label lblFormat = new Label("hex format : eg. #fff");
		lblFormat.setStylePrimaryName("SuggestionTextColor");
		final TextBox tbTableBackGroundColor = new TextBox();
		tbTableBackGroundColor.addValueChangeHandler(this);
		tbTableBackGroundColor.setText(configuration.getTableBackgroundColor());
		fontFlex.setWidget(6, 0, lblTableBackGroundColor);
		fontFlex.setWidget(6, 2, tbTableBackGroundColor);
		fontFlex.setWidget(6, 3, lblFormat);
		
		fontFlex.setWidget(8, 0, hpBtn);
		
		btnSave.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				configuration.setTableBackgroundColor(tbTableBackGroundColor.getText());
				configuration.setTableCellPadding(tbTableCellPadding.getText());
				configuration.setTableCellSpacing(tbTableCellSpacing.getText());
				configuration.setTableWidth(tbTableWidth.getText());
				String xml = configuration.getDomAsString();
				saveConfiguration(xml);
			}
		});
		
		btnClear.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				tbTableCellSpacing.setText("");
				tbTableCellPadding.setText("");
				tbTableBackGroundColor.setText("");
				tbTableWidth.setText("");
			}
		});
		hpBtn.setSpacing(5);
		hpBtn.add(btnSave);
		hpBtn.add(btnClear);
		hpBasePanel.add(fontFlex);
	}
	
	@Override
	public void onChange(ChangeEvent event) {
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
			popup.setPopupPosition(vpBase.getAbsoluteLeft()+400, vpBase.getAbsoluteTop()+100);
			popup.show();
			event.stopPropagation();
			event.preventDefault();
		}else{
			if(lbSubOption.getSelectedIndex() == 0){
				hpBasePanel.clear();
			}
			if(lbSubOption.getSelectedIndex() == 1){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(1));
				getFontFieldsWithPaddingOption();
			}
			if(lbSubOption.getSelectedIndex() == 2){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(2));
				getFontFieldsWithEnableDisableOption();
			}
			if(lbSubOption.getSelectedIndex() == 3){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(3));
				getFontFieldsWithPaddingOption();
			}
			if(lbSubOption.getSelectedIndex() == 4){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(4));
				getFontFieldsWithEnableDisableOption();
			}
			if(lbSubOption.getSelectedIndex() == 5){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(5));
				getFontFieldsWithEnableDisableOption();
			}
			if(lbSubOption.getSelectedIndex() == 6){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(6));
				getPulseReportFavioriteFields();
			}
			if(lbSubOption.getSelectedIndex() == 7){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(7));
				getPulseReportFavioriteFields();
			}
			if(lbSubOption.getSelectedIndex() == 8){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(8));
				getPulseReportFavioriteFields();
			}
			if(lbSubOption.getSelectedIndex() == 9){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(9));
				getTableFields();
			}
			if(lbSubOption.getSelectedIndex() == 10){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(10));
				getColumnFields();
			}
			if(lbSubOption.getSelectedIndex() == 11){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(11));
				getColumnFields();
			}
			if(lbSubOption.getSelectedIndex() == 12){
				hpBasePanel.clear();
				createHeaderLabel(lbSubOption.getItemText(12));
				getFontFieldsWithPaddingOption();
			}
		}
	}

	@Override
	public void onClick(ClickEvent event) {
		if(event.getSource() instanceof CheckBox){
			CheckBox chk = (CheckBox) event.getSource();
			if(chk==enableChk)
				disableChk.setValue(false);
			else
				enableChk.setValue(false);
		}
	}

	private void saveConfiguration(String xml) {
		NewsletterConfigServiceAsync async = GWT.create(NewsletterConfigService.class);
		AsyncCallback callback = new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				hasValueChanged=true;
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Could not update changes");
				popup.setPopupPosition(vpBase.getAbsoluteLeft()+400, vpBase.getAbsoluteTop()+100);
				popup.show();
			}

			@Override
			public void onSuccess(Object result) {
				hasValueChanged=false;
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Changes successfully saved.");
				popup.setPopupPosition(vpBase.getAbsoluteLeft()+400, vpBase.getAbsoluteTop()+100);
				popup.show();
			}
		};
		async.saveNewsletterContentConfig(xml, callback);
	}
	
	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		hasValueChanged = true;
	}
}
