package com.lighthouse.main.client.ui;

import com.appUtils.client.exception.FeatureNotSupportedException;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.newsletter.client.ui.AlertsConfigurationWidget;

public class MailAlertsPanel extends Composite implements ClickHandler{

	private LhUserPermission lhUserPermission;
	private HorizontalPanel mailAlertsPanel;
	private Label label;
	
	public MailAlertsPanel(String labelText, LhUserPermission lhUserPermission) throws FeatureNotSupportedException{
		if(lhUserPermission.isMailAlertPermitted() == 1){
			this.lhUserPermission = lhUserPermission;
			mailAlertsPanel = new HorizontalPanel();
			label = new Label(labelText);
			createUI();
			initWidget(mailAlertsPanel);
		}else
			throw new FeatureNotSupportedException("Mail & User settings not supported");
	}
	
	/**
	 * creates the UI
	 */
	private void createUI(){
		Image image = new Image("images/envelope.png");
		DOM.setStyleAttribute(image.getElement(), "margin", "2px 3px 0px 0px");
		label.setStylePrimaryName("subscribeLabel");
		label.setTitle("Manage alerts");
		label.addClickHandler(this);
		mailAlertsPanel.add(image);
		mailAlertsPanel.add(label);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		showPopup();
	}

	public void showPopup(){
		PopupPanel popup = new PopupPanel(false);
		popup.setAnimationEnabled(true);
		
		AlertsConfigurationWidget configWidget = new AlertsConfigurationWidget(lhUserPermission);
		configWidget.setParentPopupRef(popup);
		configWidget.createUI();
		
		popup.add(configWidget);
		popup.setStylePrimaryName("newsletterPopup");
		
		int width = (Window.getClientWidth() * 35) / 100;
		int height = (Window.getClientHeight() * 30) / 100;
		popup.setPopupPosition(width, height + 10);
		
		//Window.scrollTo(width, height);
		
		popup.show();
	}
	
}
