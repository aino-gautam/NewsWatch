package com.lighthouse.login.user.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class PopUpForAfterRegister extends DecoratedPopupPanel implements ClickHandler
{
	private Label label,labelBlank, labelemail;
	private Button buttonClose;
	private VerticalPanel vertical;
	String buttontext = "";
	String buttonText = "";
	
	String urlPort="";
	
	public PopUpForAfterRegister()
	{
		String urlClient = GWT.getModuleBaseURL();
		String[]  url = new String[5];
		url = urlClient.split("/");
		urlPort = url[0]+"//"+url[2];
	}
	
	public PopUpForAfterRegister(String message,String bttText,String senderClass){
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
		if(button.getText().equals("OK")){
			hide();
		}
		else if(buttontext.equals("redirecttoLogin")){
			urlPort = GWT.getHostPageBaseURL()+"lhlogin.html?NCID="+Window.Location.getParameter("NCID")+"&ncName="+Window.Location.getParameter("ncName");
			Window.open(urlPort, "_self", "");
		}
				
	}

}
