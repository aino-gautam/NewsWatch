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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.newsletter.client.NewsletterConfigService;
import com.lighthouse.newsletter.client.NewsletterConfigServiceAsync;

public class ManageNewsletterFooter extends Composite implements ChangeHandler,ValueChangeHandler<String>{
	
	private VerticalPanel vpBase = new VerticalPanel();
	private ListBox lbSubOption;
	private static final String TABLE = "Table";
	private static final String FIRSTROW = "First row";
	private static final String NAME_EMAIL = "Name and Email";
	private static final String UNSUBSCRIBE = "Unsubscribe";
	private static final String POWEREDBY = "Powered by";
	private HorizontalPanel hpFields = new HorizontalPanel();
	private HorizontalPanel hpHeaderLbl = new HorizontalPanel();
	Label headerLblUi;
	FooterDesignConfiguration configuration;
	public static boolean hasValueChanged = false;
	
	public ManageNewsletterFooter() {
		initWidget(vpBase);
	}
	
	public void initialize(){
		NewsletterConfigServiceAsync service = (NewsletterConfigServiceAsync)GWT.create(NewsletterConfigService.class);
		service.getNewsletterFooterConfig(new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(String result) {
				configuration = new FooterDesignConfiguration(result);
				vpBase.clear();
				createUI();
			}
		});
	}

	private void createUI() {
		lbSubOption = new ListBox();
		lbSubOption.addItem("----- Please Select -----");
		lbSubOption.addItem(TABLE);
		lbSubOption.addItem(FIRSTROW);
		lbSubOption.addItem(NAME_EMAIL);
		lbSubOption.addItem(UNSUBSCRIBE);
		lbSubOption.addItem(POWEREDBY);
		lbSubOption.addChangeHandler(this);
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
					
					panel.add(new Label("You may lost your changes, Please click on Save button to save modified data...."));
					panel.add(okbtn);
					popup.add(panel);
					popup.setPopupPosition(vpBase.getAbsoluteLeft()+400, vpBase.getAbsoluteTop()+100);
					popup.show();
				}
			}
		});*/
		Label lblheading = new Label("Please select style: ");
		lblheading.setStylePrimaryName("messageLabels");
	
		HorizontalPanel hPanel = new HorizontalPanel();
		
		hPanel.add(lblheading);
		hPanel.add(lbSubOption);
		
		vpBase.add(hPanel);
		vpBase.add(hpHeaderLbl);
		vpBase.add(hpFields);
	}
	
	private Widget getTableFields() {
		
		FlexTable flex = new FlexTable();
		 HorizontalPanel hpBtnPanel = new HorizontalPanel(); 
		 Button savebtn = new Button("Save");
		 Button clearbtn = new Button("Clear");
		
		Label lblWidth = new Label("Width");
		lblWidth.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtWidth = new TextBox();
		txtWidth.addValueChangeHandler(this);
		txtWidth.setText(configuration.getTableWidth());
		flex.setWidget(0, 0, lblWidth);
		flex.setWidget(0, 1, txtWidth);
		
		Label lblCellSpacing = new Label("Cell Spacing");
		lblCellSpacing.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtCellSpacing = new TextBox();
		txtCellSpacing.addValueChangeHandler(this);
		txtCellSpacing.setText(configuration.getTableCellSpacing());
		flex.setWidget(1, 0, lblCellSpacing);
		flex.setWidget(1, 1, txtCellSpacing);
		
		Label lblCellPadding = new Label("Cell Padding");
		lblCellPadding.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtCellPadding = new TextBox();
		txtCellPadding.addValueChangeHandler(this);
		txtCellPadding.setText(configuration.getTableCellPadding());
		flex.setWidget(2, 0, lblCellPadding);
		flex.setWidget(2, 1, txtCellPadding);
		
		Label lblBgColor = new Label("BackGround Color");
		lblBgColor.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtBgColor = new TextBox();
		txtBgColor.addValueChangeHandler(this);
		txtBgColor.setText(configuration.getTableBackgroundColor());
		flex.setWidget(3, 0, lblBgColor);
		flex.setWidget(3, 1, txtBgColor);
		
		hpBtnPanel.setSpacing(5);
		hpBtnPanel.add(savebtn);
		hpBtnPanel.add(clearbtn);
		flex.setWidget(4, 0, hpBtnPanel);
	
		
		savebtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				configuration.setTableBackgroundColor(txtBgColor.getText());
				configuration.setTableCellPadding(txtCellPadding.getText());
				configuration.setTableCellSpacing(txtCellSpacing.getText());
				configuration.setTableWidth(txtWidth.getText());
				String xmlConfig = configuration.getDomAsString();
				saveConfiguration(xmlConfig);
			}
		});
		clearbtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				txtWidth.setText("");
				txtCellSpacing.setText("");
				txtCellPadding.setText("");
				txtBgColor.setText("");
			}
		});
		
		return flex;
	}
	
	private Widget getFirstRowFields() {
		
		FlexTable flex = new FlexTable();
		 HorizontalPanel hpBtnPanel = new HorizontalPanel(); 
		 Button savebtn = new Button("Save");
		 Button clearbtn = new Button("Clear");
		
		Label lblFRCellPadding = new Label("Padding");
		lblFRCellPadding.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtFRCellPadding = new TextBox();
		txtFRCellPadding.addValueChangeHandler(this);
		txtFRCellPadding.setText(configuration.getFirstRowPadding());
		flex.setWidget(0, 0, lblFRCellPadding);
		flex.setWidget(0, 1, txtFRCellPadding);
		
		Label lblFRBorder = new Label("Border");
		lblFRBorder.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtFRBorder = new TextBox();
		txtFRBorder.addValueChangeHandler(this);
		txtFRBorder.setText(configuration.getFirstRowBorder());
		flex.setWidget(1, 0, lblFRBorder);
		flex.setWidget(1, 1, txtFRBorder);
		
		Label lblFRAlign = new Label("Align");
		lblFRAlign.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtFRAlign = new TextBox();
		txtFRAlign.addValueChangeHandler(this);
		txtFRAlign.setText(configuration.getFirstRowAlign());
	/*	flex.setWidget(2, 0, lblFRAlign);
		flex.setWidget(2, 1, txtFRAlign);*/
		
		Label lblFRVAlign = new Label("VAlign");
		lblFRVAlign.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtFRVAlign = new TextBox();
		txtFRVAlign.addValueChangeHandler(this);
		txtFRVAlign.setText(configuration.getFirstRowVerticalAlign());
		flex.setWidget(2, 0, lblFRVAlign);
		flex.setWidget(2, 1, txtFRVAlign);
		
		Label lblFRWidth = new Label("Width");
		lblFRWidth.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtFRWidth = new TextBox();
		txtFRWidth.addValueChangeHandler(this);
		txtFRWidth.setText(configuration.getFirstRowWidth());
		flex.setWidget(3, 0, lblFRWidth);
		flex.setWidget(3, 1, txtFRWidth);
		
		hpBtnPanel.setSpacing(5);
		hpBtnPanel.add(savebtn);
		hpBtnPanel.add(clearbtn);
		flex.setWidget(4, 0, hpBtnPanel);
		
		savebtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				configuration.setFirstRowAlign(txtFRAlign.getText());
				configuration.setFirstRowBorder(txtFRBorder.getText());
				configuration.setFirstRowPadding(txtFRCellPadding.getText());
				configuration.setFirstRowVerticalAlign(txtFRVAlign.getText());
				configuration.setFirstRowWidth(txtFRWidth.getText());
				String xmlconfig = configuration.getDomAsString();
				saveConfiguration(xmlconfig);
			}
		});
		
		clearbtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				txtFRCellPadding.setText("");
				txtFRBorder.setText("");
				txtFRAlign.setText("");
				txtFRVAlign.setText("");
				txtFRWidth.setText("");
			}
		});
		
		return flex;
	}
	
	private Widget getEmailandNameFields() {
		
		FlexTable flex = new FlexTable();
		 HorizontalPanel hpBtnPanel = new HorizontalPanel(); 
		 Button savebtn = new Button("Save");
		 Button clearbtn = new Button("Clear");
		
		Label lblENFontColor = new Label("Font color");
		lblENFontColor.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtENFontColor = new TextBox();
		txtENFontColor.addValueChangeHandler(this);
		txtENFontColor.setText(configuration.getNameAndElementFontColor());
		flex.setWidget(0, 0, lblENFontColor);
		flex.setWidget(0, 1, txtENFontColor);
		
		Label lblENFontFamily = new Label("Font family");
		lblENFontFamily.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtENFontFamily = new TextBox();
		txtENFontFamily.addValueChangeHandler(this);
		txtENFontFamily.setText(configuration.getNameAndElementFontFamily());
		flex.setWidget(1, 0, lblENFontFamily);
		flex.setWidget(1, 1, txtENFontFamily);
		
		Label lblENFontSize = new Label("Font size");
		lblENFontSize.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtENFontSize = new TextBox();
		txtENFontSize.addValueChangeHandler(this);
		txtENFontSize.setText(configuration.getNameAndElementFontSize());
		flex.setWidget(2, 0, lblENFontSize);
		flex.setWidget(2, 1, txtENFontSize);
		
		Label lblENFontWght = new Label("Font weight");
		lblENFontWght.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtENFontWght = new TextBox();
		txtENFontWght.addValueChangeHandler(this);
		txtENFontWght.setText(configuration.getNameAndElementFontWeight());
		flex.setWidget(3, 0, lblENFontWght);
		flex.setWidget(3, 1, txtENFontWght);
		
		Label lblENFontStyle = new Label("Font style");
		lblENFontStyle.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtENFontStyle = new TextBox();
		txtENFontStyle.addValueChangeHandler(this);
		txtENFontStyle.setText(configuration.getNameAndElementFontStyle());
		flex.setWidget(4, 0, lblENFontStyle);
		flex.setWidget(4, 1, txtENFontStyle);
		
		Label lblENTextAlign = new Label("Text alignment");
		lblENTextAlign.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtENTextAlign = new TextBox();
		txtENTextAlign.addValueChangeHandler(this);
		txtENTextAlign.setText(configuration.getNameAndElementTextAlignment());
		flex.setWidget(5, 0, lblENTextAlign);
		flex.setWidget(5, 1, txtENTextAlign);
		
		Label lblENNameDisplay = new Label("Display Name");
		lblENNameDisplay.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtENNameDisplay= new TextBox();
		txtENNameDisplay.addValueChangeHandler(this);
		txtENNameDisplay.setText(configuration.getNameAndElementNameToBeDisplayed());
		flex.setWidget(6, 0, lblENNameDisplay);
		flex.setWidget(6, 1, txtENNameDisplay);
		
		Label lblENEmailDisplay = new Label("Display Email");
		lblENEmailDisplay.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtENEmailDisplay= new TextBox();
		txtENEmailDisplay.addValueChangeHandler(this);
		txtENEmailDisplay.setText(configuration.getNameAndElementEmailToBeDisplayed());
		flex.setWidget(7, 0, lblENEmailDisplay);
		flex.setWidget(7, 1, txtENEmailDisplay);
		
		hpBtnPanel.setSpacing(5);
		hpBtnPanel.add(savebtn);
		hpBtnPanel.add(clearbtn);
		flex.setWidget(8, 0, hpBtnPanel);
		
		savebtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				configuration.setNameAndElementFontColor(txtENFontColor.getText());
				configuration.setNameAndElementFontFamily(txtENFontFamily.getText());
				configuration.setNameAndElementFontSize(txtENFontSize.getText());
				configuration.setNameAndElementFontStyle(txtENFontStyle.getText());
				configuration.setNameAndElementFontWeight(txtENFontWght.getText());
				configuration.setNameAndElementTextAlignment(txtENTextAlign.getText());
				configuration.setNameAndElementNameToBeDisplayed(txtENNameDisplay.getText());
				configuration.setNameAndElementEmailToBeDisplayed(txtENEmailDisplay.getText());
				String xmlconfig = configuration.getDomAsString();
				saveConfiguration(xmlconfig);
			}
		});
		
		clearbtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				txtENFontColor.setText("");
				txtENFontFamily.setText("");
				txtENFontSize.setText("");
				txtENFontWght.setText("");
				txtENFontStyle.setText("");
				txtENTextAlign.setText("");
				txtENNameDisplay.setText("");
				txtENEmailDisplay.setText("");
			}
		});
		
		
		return flex;
	}
	
	private Widget getUnsubscribedFields() {
	
		FlexTable flex = new FlexTable();
	
		 HorizontalPanel hpBtnPanel = new HorizontalPanel(); 
		 Button savebtn = new Button("Save");
		 Button clearbtn = new Button("Clear");
		
		
		Label lblUnEnableDisable = new Label("Enable / Disable");
		lblUnEnableDisable.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtUnEnableDisable = new TextBox();
		txtUnEnableDisable.addValueChangeHandler(this);
		txtUnEnableDisable.setText(configuration.getUnsubscribedEnabeledOrNot());
		flex.setWidget(0, 0, lblUnEnableDisable);
		flex.setWidget(0, 1, txtUnEnableDisable);
		
		Label lblUnFontColor = new Label("Font color");
		lblUnFontColor.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtUnFontColor = new TextBox();
		txtUnFontColor.addValueChangeHandler(this);
		txtUnFontColor.setText(configuration.getUnsubscribedFontColor());
		flex.setWidget(1, 0, lblUnFontColor);
		flex.setWidget(1, 1, txtUnFontColor);
		
		Label lblUnFontFamily = new Label("Font family");
		lblUnFontFamily.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtUnFontFamily = new TextBox();
		txtUnFontFamily.addValueChangeHandler(this);
		txtUnFontFamily.setText(configuration.getUnsubscribedFontFamily());
		flex.setWidget(2, 0, lblUnFontFamily);
		flex.setWidget(2, 1, txtUnFontFamily);
		
		Label lblUnFontSize = new Label("Font size");
		lblUnFontSize.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtUnFontSize = new TextBox();
		txtUnFontSize.addValueChangeHandler(this);
		txtUnFontSize.setText(configuration.getUnsubscribedFontSize());
		flex.setWidget(3, 0, lblUnFontSize);
		flex.setWidget(3, 1, txtUnFontSize);
		
		Label lblUnFontWght = new Label("Font weight");
		lblUnFontWght.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtUnFontWght = new TextBox();
		txtUnFontWght.addValueChangeHandler(this);
		txtUnFontWght.setText(configuration.getUnsubscribedFontWeight());
		flex.setWidget(4, 0, lblUnFontWght);
		flex.setWidget(4, 1, txtUnFontWght);
		
		Label lblUnFontStyle = new Label("Font style");
		lblUnFontStyle.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtUnFontStyle = new TextBox();
		txtUnFontStyle.addValueChangeHandler(this);
		txtUnFontStyle.setText(configuration.getUnsubscribedFontStyle());
		flex.setWidget(5, 0, lblUnFontStyle);
		flex.setWidget(5, 1, txtUnFontStyle);
		
		
		Label lblUnDisplayText = new Label("Display Text");
		lblUnDisplayText.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtUnDisplayText = new TextBox();
		txtUnDisplayText.addValueChangeHandler(this);
		txtUnDisplayText.setText(configuration.getUnsubscribedDisplayText());
		flex.setWidget(6, 0, lblUnDisplayText);
		flex.setWidget(6, 1, txtUnDisplayText);
		
		
		Label lblUnDisplayLinkText = new Label("Display Link");
		lblUnDisplayLinkText.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtUnDisplayLinkText = new TextBox();
		txtUnDisplayLinkText.addValueChangeHandler(this);
		txtUnDisplayLinkText.setText(configuration.getUnsubscribedDisplayLinkText());
		flex.setWidget(7, 0, lblUnDisplayLinkText);
		flex.setWidget(7, 1, txtUnDisplayLinkText);
		
		Label lblUnLinkURL = new Label("Link Url");
		lblUnLinkURL.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtUnLinkURL = new TextBox();
		txtUnLinkURL.addValueChangeHandler(this);
		txtUnLinkURL.setText(configuration.getUnsubscribedLinkUrl());
		flex.setWidget(8, 0, lblUnLinkURL);
		flex.setWidget(8, 1, txtUnLinkURL);
		
		Label lblUnTextAlign = new Label("Text alignment");
		lblUnTextAlign.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtUnTextAlign = new TextBox();
		txtUnTextAlign.addValueChangeHandler(this);
		txtUnTextAlign.setText(configuration.getUnsubscribedTextAlignment());
		flex.setWidget(9, 0, lblUnTextAlign);
		flex.setWidget(9, 1, txtUnTextAlign);
		
		hpBtnPanel.setSpacing(5);
		hpBtnPanel.add(savebtn);
		hpBtnPanel.add(clearbtn);
		flex.setWidget(10, 0, hpBtnPanel);
		
		savebtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				configuration.setUnsubscribedDisplayLinkText(txtUnDisplayLinkText.getText());
				configuration.setUnsubscribedDisplayText(txtUnDisplayText.getText());
				configuration.setUnsubscribedEnabeledOrNot(txtUnEnableDisable.getText());
				configuration.setUnsubscribedFontColor(txtUnFontColor.getText());
				configuration.setUnsubscribedFontFamily(txtUnFontFamily.getText());
				configuration.setUnsubscribedFontSize(txtUnFontSize.getText());
				configuration.setUnsubscribedFontStyle(txtUnFontStyle.getText());
				configuration.setUnsubscribedFontWeight(txtUnFontWght.getText());
				configuration.setUnsubscribedLinkUr(txtUnLinkURL.getText());
				configuration.setUnsubscribedTextAlignment(txtUnTextAlign.getText());
				String config = configuration.getDomAsString();
				saveConfiguration(config);
			}
		});
		
		clearbtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				txtUnEnableDisable.setText("");
				txtUnFontColor.setText("");
				txtUnFontFamily.setText("");
				txtUnFontSize.setText("");
				txtUnFontWght.setText("");
				txtUnFontStyle.setText("");
				txtUnDisplayLinkText.setText("");
				txtUnLinkURL.setText("");
				txtUnTextAlign.setText("");
			}
		});
		
		
		
		return flex;
	}

	private Widget getPoweredByFields() {
		
		FlexTable flex = new FlexTable();
		 HorizontalPanel hpBtnPanel = new HorizontalPanel(); 
		 Button savebtn = new Button("Save");
		 Button clearbtn = new Button("Clear");
		
		Label lblPBEnableDisable = new Label("Enable / Disable");
		lblPBEnableDisable.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtPBEnableDisable = new TextBox();
		txtPBEnableDisable.addValueChangeHandler(this);
		txtPBEnableDisable.setText(configuration.getPoweredByEnabeledOrNot());
		flex.setWidget(0, 0, lblPBEnableDisable);
		flex.setWidget(0, 1, txtPBEnableDisable);
				
		Label lblPBFontColor = new Label("Font color");
		lblPBFontColor.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtPBFontColor = new TextBox();
		txtPBFontColor.addValueChangeHandler(this);
		txtPBFontColor.setText(configuration.getPoweredByFontColor());
		flex.setWidget(1, 0, lblPBFontColor);
		flex.setWidget(1, 1, txtPBFontColor);
		
		Label lblPBFontFamily = new Label("Font family");
		lblPBFontFamily.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtPBFontFamily = new TextBox();
		txtPBFontFamily.addValueChangeHandler(this);
		txtPBFontFamily.setText(configuration.getPoweredByFontFamily());
		flex.setWidget(2, 0, lblPBFontFamily);
		flex.setWidget(2, 1, txtPBFontFamily);
		
		Label lblPBFontSize = new Label("Font size");
		lblPBFontSize.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtPBFontSize = new TextBox();
		txtPBFontSize.addValueChangeHandler(this);
		txtPBFontSize.setText(configuration.getPoweredByFontSize());
		flex.setWidget(3, 0, lblPBFontSize);
		flex.setWidget(3, 1, txtPBFontSize);
		
		Label lblPBFontWght = new Label("Font weight");
		lblPBFontWght.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtPBFontWght = new TextBox();
		txtPBFontWght.addValueChangeHandler(this);
		txtPBFontWght.setText(configuration.getPoweredByFontWeight());
		flex.setWidget(4, 0, lblPBFontWght);
		flex.setWidget(4, 1, txtPBFontWght);
		
		Label lblPBFontStyle = new Label("Font style");
		lblPBFontStyle.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtPBFontStyle = new TextBox();
		txtPBFontStyle.setText(configuration.getPoweredByFontStyle());
		flex.setWidget(5, 0, lblPBFontStyle);
		flex.setWidget(5, 1, txtPBFontStyle);
		
		Label lblPBDisplayText = new Label("Display Text");
		lblPBDisplayText.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtPBDisplayText = new TextBox();
		txtPBDisplayText.addValueChangeHandler(this);
		txtPBDisplayText.setText(configuration.getPoweredByDisplayText());
		flex.setWidget(6, 0, lblPBDisplayText);
		flex.setWidget(6, 1, txtPBDisplayText);
		
		Label lblPBDisplayLinkText = new Label("Display Link");
		lblPBDisplayLinkText.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtPBDisplayLinkText = new TextBox();
		txtPBDisplayLinkText.addValueChangeHandler(this);
		txtPBDisplayLinkText.setText(configuration.getPoweredByDisplayLinkText());
		flex.setWidget(7, 0, lblPBDisplayLinkText);
		flex.setWidget(7, 1, txtPBDisplayLinkText);
		
		Label lblPBLinkURL = new Label("Link Url");
		lblPBLinkURL.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtPBLinkURL = new TextBox();
		txtPBLinkURL.addValueChangeHandler(this);
		txtPBLinkURL.setText(configuration.getPoweredByLinkUrl());
		flex.setWidget(8, 0, lblPBLinkURL);
		flex.setWidget(8, 1, txtPBLinkURL);
		
		Label lblPBTextAlign = new Label("Text alignment");
		lblPBTextAlign.setStylePrimaryName("labelFlexEditNewsItems");
		final TextBox txtPBTextAlign = new TextBox();
		txtPBTextAlign.addValueChangeHandler(this);
		txtPBTextAlign.setText(configuration.getPoweredByTextAlignment());
		flex.setWidget(9, 0, lblPBTextAlign);
		flex.setWidget(9, 1, txtPBTextAlign);
		
		hpBtnPanel.setSpacing(5);
		hpBtnPanel.add(savebtn);
		hpBtnPanel.add(clearbtn);
		flex.setWidget(10, 0, hpBtnPanel);
				
		savebtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				configuration.setPoweredByDisplayLinkText(txtPBDisplayLinkText.getText());
				configuration.setPoweredByDisplayText(txtPBDisplayText.getText());
				configuration.setPoweredByFontColor(txtPBFontColor.getText());
				configuration.setPoweredByEnabeledOrNot(txtPBEnableDisable.getText());
				configuration.setPoweredByFontFamily(txtPBFontFamily.getText());
				configuration.setPoweredByFontSize(txtPBFontSize.getText());
				configuration.setPoweredByFontStyle(txtPBFontSize.getText());
				configuration.setPoweredByFontWeight(txtPBFontWght.getText());
				configuration.setPoweredByLinkUr(txtPBLinkURL.getText());
				configuration.setPoweredByTextAlignment(txtPBTextAlign.getText());
				String config = configuration.getDomAsString();
				saveConfiguration(config);
				
			}
		});
		
		clearbtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				txtPBEnableDisable.setText("");
				txtPBFontColor.setText("");
				txtPBFontFamily.setText("");
				txtPBFontSize.setText("");
				txtPBFontWght.setText("");
				txtPBFontStyle.setText("");
				txtPBDisplayLinkText.setText("");
				txtPBLinkURL.setText("");
				txtPBTextAlign.setText("");
			}
		});
		return flex;
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
				hpFields.clear();
			}
			
			if(lbSubOption.getSelectedIndex() == 1){
				hpFields.clear();
				createHeaderLblUi(lbSubOption.getItemText(1));
				hpFields.add(getTableFields());
			}
			if(lbSubOption.getSelectedIndex() == 2){
				hpFields.clear();
				createHeaderLblUi(lbSubOption.getItemText(2));
				hpFields.add(getFirstRowFields());
			}
			if(lbSubOption.getSelectedIndex() == 3){
				hpFields.clear();
				createHeaderLblUi(lbSubOption.getItemText(3));
				hpFields.add(getEmailandNameFields());
			}
			if(lbSubOption.getSelectedIndex() == 4){
				hpFields.clear();
				createHeaderLblUi(lbSubOption.getItemText(4));
				hpFields.add(getUnsubscribedFields());
			}
			if(lbSubOption.getSelectedIndex() == 5){
				hpFields.clear();
				createHeaderLblUi(lbSubOption.getItemText(5));
				hpFields.add(getPoweredByFields());
			}
		}
	}
	
	public void createHeaderLblUi(String text){
		headerLblUi = new Label(text);
		headerLblUi.setStylePrimaryName("newslettereditLbl");
		hpHeaderLbl.clear();
		hpHeaderLbl.add(headerLblUi);
	}
	
	private void saveConfiguration(String xmlconfig) {
		NewsletterConfigServiceAsync async = GWT.create(NewsletterConfigService.class);
		AsyncCallback callback = new AsyncCallback() {

			@Override
			public void onFailure(Throwable caught) {
				hasValueChanged = true;
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Could not update changes");
				popup.setPopupPosition(vpBase.getAbsoluteLeft()+400, vpBase.getAbsoluteTop()+100);
				popup.show();
			}

			@Override
			public void onSuccess(Object result) {
				hasValueChanged = false;
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Changes successfully saved.");
				popup.setPopupPosition(vpBase.getAbsoluteLeft()+400, vpBase.getAbsoluteTop()+100);
				popup.show();
			}
		};
		async.saveNewsletterFooterConfig(xmlconfig, callback);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		hasValueChanged = true;
	}
}
