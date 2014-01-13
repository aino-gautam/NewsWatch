package com.lighthouse.admin.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.admin.client.siloui.ManageNewsletterContent;
import com.lighthouse.admin.client.siloui.ManageNewsletterFooter;
import com.lighthouse.admin.client.siloui.ManageNewsletterHeader;
import com.lighthouse.admin.client.siloui.ManageSiloClientLogo;
import com.lighthouse.admin.client.siloui.ManageTemplateOutline;

public class ManageSilo extends Composite{
	
	private VerticalPanel vpBase = new VerticalPanel();
	private int newscenterid,userid;
	private ListBox lbOption;
	private VerticalPanel vpMain = new VerticalPanel();
	private static final String HEADER = "Header";
	private static final String FOOTER = "Footer";
	private static final String CONTENT_NEWSITEM = "Content(Newsitem)";
	private static final String BASE_OUTLINE = "Base outline";
	private static final String CLIENT_LOGO = "Client logo";
	
	private ManageNewsletterContent editNewsletterContent;
	private ManageNewsletterFooter editNewsletterFooter;
	private ManageNewsletterHeader editNewsletterHeader;
	private ManageTemplateOutline editTemplateOutline;
	private ManageSiloClientLogo uploadClientLogo; 
	
	public ManageSilo(int newscenterid,int userid){
		this.newscenterid = newscenterid;
		this.userid = userid;
		initWidget(vpBase);
	}
	
	/**
	 * iniitializes the page
	 */
	public void initialize(){
		vpBase.clear();
		createUI();
	}
	
	/**
	 * creates the page ui
	 */
	public void createUI(){
		Label lblheading = new Label("Please select any of the following: ");
		lblheading.setStylePrimaryName("labelSelection");
		
		lbOption = new ListBox();
		lbOption.addItem("----- Please Select -----");
		lbOption.addItem(CLIENT_LOGO);
		lbOption.addItem(BASE_OUTLINE);
		lbOption.addItem(HEADER);
		lbOption.addItem(CONTENT_NEWSITEM);
		lbOption.addItem(FOOTER);
		
		/*lbOption.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(ManageNewsletterFooter.hasValueChanged||ManageTemplateOutline.hasValueChanged||ManageNewsletterHeader.hasValueChanged||ManageNewsletterContent.hasValueChanged){
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
		
		lbOption.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent arg0) {
				if(ManageNewsletterFooter.hasValueChanged||ManageTemplateOutline.hasValueChanged||ManageNewsletterHeader.hasValueChanged||ManageNewsletterContent.hasValueChanged){
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
					arg0.stopPropagation();
					arg0.preventDefault();
				}else{
					if(lbOption.getSelectedIndex() == 0){
						vpMain.clear();
					}
					else if(lbOption.getSelectedIndex() == 1){
						vpMain.clear();
						vpMain.add(editClientLogo());
					}
					else if(lbOption.getSelectedIndex() == 2){
						vpMain.clear();
						vpMain.add(editTemplateOutline());
					}
					else if(lbOption.getSelectedIndex() == 3){
						vpMain.clear();
						vpMain.add(editNewsLetterHeader());
					}
					else if(lbOption.getSelectedIndex() == 4){
						vpMain.clear();
						vpMain.add(editNewsLetterContent());
					}
					else if(lbOption.getSelectedIndex() == 5){
						vpMain.clear();
						vpMain.add(editNewsLetterFooter());
					}
				}
			}
		});
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(lblheading);
		hp.add(lbOption);
		hp.setSpacing(5);
		
		vpBase.add(hp);
		vpBase.add(vpMain);
		vpBase.setSpacing(7);
		vpBase.setSize("100%", "100%");

	}
	
	private VerticalPanel editNewsLetterFooter() {
		VerticalPanel vPanelEdit = new VerticalPanel();
		editNewsletterFooter = new ManageNewsletterFooter();
		editNewsletterFooter.initialize();
	    vPanelEdit.add(editNewsletterFooter);
		return vPanelEdit;
	}
	
	private VerticalPanel editNewsLetterContent() {
		VerticalPanel vPanelEdit = new VerticalPanel();
		editNewsletterContent = new ManageNewsletterContent();
		editNewsletterContent.initialize();
		vPanelEdit.add(editNewsletterContent);
		return vPanelEdit;
	}
	
	private VerticalPanel editNewsLetterHeader() {
		VerticalPanel vPanelEdit = new VerticalPanel();
		editNewsletterHeader = new ManageNewsletterHeader();
		editNewsletterHeader.initialize();
		vPanelEdit.add(editNewsletterHeader);
		return vPanelEdit;
	}
	
	private VerticalPanel editTemplateOutline() {
		VerticalPanel vPanelEdit = new VerticalPanel();
		editTemplateOutline = new ManageTemplateOutline();
		editTemplateOutline.initialize();
		vPanelEdit.add(editTemplateOutline);
		return vPanelEdit;
	}
	
	private VerticalPanel editClientLogo() {
		VerticalPanel vPanelEdit = new VerticalPanel();
		uploadClientLogo = new ManageSiloClientLogo();
		uploadClientLogo.initialize();
		vPanelEdit.add(uploadClientLogo);
		return vPanelEdit;
	}
}
