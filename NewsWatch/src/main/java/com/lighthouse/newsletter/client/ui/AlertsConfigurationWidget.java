package com.lighthouse.newsletter.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.login.user.client.domain.LhUserPermission;


public class AlertsConfigurationWidget extends Composite {

	private TabPanel tabPanel;
	private CreateAlertsWidget createAlertWidget;
	private AllAlertsWidget allAlertsWidget;
//	private Image closeImage;
	private VerticalPanel vpBase;
	private PopupPanel parentPopupRef;
	private LhUserPermission lhUserPermission;
	
	public AlertsConfigurationWidget(LhUserPermission lhUserPermission){
		this.lhUserPermission = lhUserPermission;
		vpBase = new VerticalPanel();
		initWidget(vpBase);
	}
	
	public void createUI(){
	//	closeImage = new Image("images/close-icon.jpg");
	//	closeImage.addStyleName("clickable");
		
		tabPanel = new TabPanel();
		tabPanel.setStylePrimaryName("alertsTab");
		tabPanel.setAnimationEnabled(true);
		tabPanel.setSize("98%", "100%");
		vpBase.setWidth("100%");
		createAlertWidget = new CreateAlertsWidget(this);
		allAlertsWidget = new AllAlertsWidget(this, lhUserPermission);
		tabPanel.add(createAlertWidget, "Create an alert");
		tabPanel.add(allAlertsWidget, "All alerts");
		parentPopupRef.setAutoHideEnabled(true);
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			
			
			
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				if(event.getSelectedItem() == 0){					  
					createAlertWidget.initialize(); 
					}
				else if(event.getSelectedItem() == 1){
					createAlertWidget.setUserAlert(null);						
					allAlertsWidget.initialize();
				}
							
			}
		});
		tabPanel.selectTab(0);
		
	//	closeImage.addClickHandler(new ClickHandler() {
			
			/*@Override
			public void onClick(ClickEvent event) {
				parentPopupRef.hide();
			}
		});*/
	//	vpBase.add(closeImage);
	//	vpBase.setCellHorizontalAlignment(closeImage, HasHorizontalAlignment.ALIGN_RIGHT);
		vpBase.add(tabPanel);
	}

	public void toggleTab(int index){
		tabPanel.selectTab(index);
	}
	
	public CreateAlertsWidget getCreateAlertWidget() {
		return createAlertWidget;
	}

	public void setParentPopupRef(PopupPanel parentPopupRef) {
		this.parentPopupRef = parentPopupRef;
	}

	public AllAlertsWidget getAllAlertsWidget() {
		return allAlertsWidget;
	}

	public LhUserPermission getLhUserPermission() {
		return lhUserPermission;
	}

	public void setLhUserPermission(LhUserPermission lhUserPermission) {
		this.lhUserPermission = lhUserPermission;
	}

}
