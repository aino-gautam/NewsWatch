package com.appUtils.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopUpForForgotPassword extends DecoratedPopupPanel implements ClickHandler
{
	private Label label,labelBlank, labelemail;
	private Button buttonClose;
	private VerticalPanel vertical;
	String buttontext = "";
	String buttonText = "";
	
	String urlPort="";
	
	public PopUpForForgotPassword()
	{
		String urlClient = GWT.getModuleBaseURL();
		String[]  url = new String[5];
		url = urlClient.split("/");
		urlPort = url[0]+"//"+url[2];
		//String urlDirection = urlPort/*+"/"*/;
	//	urlPort = urlDirection;
	}
	
	public PopUpForForgotPassword(String message,String email)
	{
		this();
		label = new Label(message);
		label.setStylePrimaryName("popupLabel");
		labelBlank = new Label(email); 
		labelBlank.setStylePrimaryName("popupLabelBold");
		buttonClose = new Button("Close");
		vertical = new VerticalPanel();
		vertical.setStylePrimaryName("verticalPopup");
//		buttonClose.addClickListener(this);
		buttonClose.addClickHandler(this);
		vertical.add(label);
		vertical.add(labelBlank);
		vertical.add(buttonClose);
		vertical.setSpacing(5);
		add(vertical);
	}

	public PopUpForForgotPassword(String message, String email, String btnText, Boolean bool){
		this();
		this.buttonText = btnText;
		label = new Label(message);
		label.setStylePrimaryName("popupLabel");
		labelemail = new Label(email);
		labelemail.setStylePrimaryName("popupLabelBold");
		buttonClose = new Button("Close  ");
		vertical = new VerticalPanel();
		vertical.setStylePrimaryName("verticalPopup");
//		buttonClose.addClickListener(this);
		buttonClose.addClickHandler(this);
		vertical.add(label);
		vertical.add(buttonClose);
		vertical.setSpacing(5);
		add(vertical);
	}

	public PopUpForForgotPassword(String message)
	{
		this();
		label = new Label(message);
		label.setStylePrimaryName("popupLabel");
		buttonClose = new Button("OK");
		vertical = new VerticalPanel();
		vertical.setStylePrimaryName("verticalPopup");
//		buttonClose.addClickListener(this);
		buttonClose.addClickHandler(this);
		vertical.add(label);
		vertical.add(buttonClose);
		vertical.setSpacing(5);
		add(vertical);
	}
	
	public PopUpForForgotPassword(String message,String bttText,String senderClass){
		this();
		this.buttontext = bttText;
		label = new Label(message);
		label.setStylePrimaryName("popupLabel");
		buttonClose = new Button("OK  ");
		vertical = new VerticalPanel();
		vertical.setStylePrimaryName("verticalPopup");
//		buttonClose.addClickListener(this);
		buttonClose.addClickHandler(this);
		vertical.add(label);
		vertical.add(buttonClose);
		vertical.setSpacing(5);
		add(vertical);
	}

	/*public void onClick(Widget sender) 
	{
		Button button =(Button)sender;
		if(button.getText().equals("Close"))
		{
			hide();
			Window.open(urlPort, "_self", "");
		}
		else if(button.getText().equals("Close  "))
		{
			hide();
			Window.open(urlPort, "_self", "");
		}
		else if(buttonText.equals("redirectToLogin")){
			Window.open(urlPort, "_self", "");
		}
		if(button.getText().equals("OK")){
			hide();
		}
		else if(buttontext.equals("redirectToAdmin"))
		{
			AdminPage admin = new AdminPage();
			admin.onModuleLoad();
		}
		else if(buttontext.equals("redirecttoLogin")){
			Window.open(urlPort, "_self", "");
		}
		else if(buttontext.equals("redirectToIndexPage"))
		{
			Window.open(urlPort, "_self", "");
		}
		else if(buttontext.equals("redirectLonza"))
		{
			String urllocalhost = "login.html?NCID=3&ncName=Lonza%20NewsCenter";
			Window.open(urlPort+urllocalhost,  "_self", "");
		}
	}
*/
	public Button getButtonClose() {
		return buttonClose;
	}

	public void setButtonClose(Button buttonClose) {
		this.buttonClose = buttonClose;
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender=(Widget) event.getSource();
		Button button =(Button)sender;
		if(button.getText().equals("Close"))
		{
			hide();
			Window.open(urlPort, "_self", "");
		}
		else if(button.getText().equals("Close  "))
		{
			hide();
			Window.open(urlPort, "_self", "");
		}
		else if(buttonText.equals("redirectToLogin")){
			Window.open(urlPort, "_self", "");
		}
		if(button.getText().equals("OK")){
			hide();
		}
		/*else if(buttontext.equals("redirectToAdmin"))
		{
			AdminPage admin = new AdminPage();
			admin.onModuleLoad();
		}*/
		else if(buttontext.equals("redirecttoLogin")){
			Window.open(urlPort, "_self", "");
		}
		else if(buttontext.equals("redirectToIndexPage"))
		{
			Window.open(urlPort, "_self", "");
		}
		else if(buttontext.equals("redirectLonza"))
		{
			String urllocalhost = "login.html?NCID=3&ncName=Lonza%20NewsCenter";
			Window.open(urlPort+urllocalhost,  "_self", "");
		}
		
	}

}
