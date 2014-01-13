package com.appUtils.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NotificationPopup extends DecoratedPopupPanel{

	private NotificationType 		notificationType;
	private Label                   lbnotificationmsg;
	private boolean 				timed=false;
	private String buttonText;
	private ClickHandler clickHandler;
	private VerticalPanel vpBase = new VerticalPanel();
	
	public static final String OK = "OK";
	public static final String CANCEL = "Cancel";
	
	private Button button;
	
	
	public enum NotificationType{
		CONFIRMATION,
		CUSTOMNOTIFICATION,
		NOTIFICATION,
		WINDOWCLOSENOTIFICATION
	}
	
	public NotificationPopup(String notificationText, NotificationType nType){
		notificationType = nType;
		lbnotificationmsg = new Label();
		lbnotificationmsg.setText(notificationText);
		add(vpBase);
	}
	
	public NotificationPopup(String notificationText, String buttonText, NotificationType nType, ClickHandler handler){
		 notificationType = nType;
		 lbnotificationmsg = new Label();
		 lbnotificationmsg.setText(notificationText);
		 this.buttonText = buttonText;
		 this.clickHandler = handler;
		 add(vpBase);
	}
	
	/**
	 * creates UI of the popup
	 */
	public void createUI(){
		this.setStylePrimaryName("searchPopup");
		this.setAnimationEnabled(true);
		this.setAutoHideEnabled(true);
		HorizontalPanel buttonsPanel = new HorizontalPanel();
		
		vpBase.add(lbnotificationmsg);
		vpBase.add(buttonsPanel);
		
		if(notificationType == NotificationType.CONFIRMATION){
			
		}else if(notificationType == NotificationType.CUSTOMNOTIFICATION){
			button = new Button(buttonText);
			button.addClickHandler(clickHandler);
			button.setStylePrimaryName("roundedButton");
			buttonsPanel.add(button);
		}else if(notificationType == NotificationType.NOTIFICATION){
			button = new Button(OK);
			button.setStylePrimaryName("roundedButton");
			button.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					hide();
					
				}
			});
			buttonsPanel.add(button);
		}
		else if(notificationType == NotificationType.WINDOWCLOSENOTIFICATION){
			button = new Button(OK);
			button.setStylePrimaryName("roundedButton");
			button.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					hide();
					closewindow();
				}

				
			});
			buttonsPanel.add(button);
		}
	}
	
	public void clearUI(){
		vpBase.clear();
	}
	
	public static native void closewindow()/*-{
	     $wnd.close(); 
	}-*/;

	/*private void addHandler(Button btn) {
	btn.addClickHandler(new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			if(event.getSource().equals(button)){
				hide();
			}
			
		}
	});
		
	}*/

	public void setNotificationMessage(String message){
		lbnotificationmsg.setText(message);
	}
	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}
	
	public boolean isTimed() {
		return timed;
	}

	public void setTimed(boolean timed) {
		this.timed = timed;
	}

}
