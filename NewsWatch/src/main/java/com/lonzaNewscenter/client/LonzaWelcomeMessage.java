package com.lonzaNewscenter.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LonzaWelcomeMessage extends Composite 
{
	
	private Label welcomeLabel = new Label("Welcome");
	
	private HorizontalPanel hpanelWelcomeLbl = new HorizontalPanel();
	
	private VerticalPanel vpanelHtmlContent = new VerticalPanel();
	
	private HTML content = new HTML("<left><b><font size="+2+" face= Verdana color=black>Welcome to the Microbial Control Intelligence Desk!</font></b></left>" +
			"<font size="+2+" face= Verdana ><br><p>The Microbial Control Intelligence Desk is a business intelligence tool designed to"
			+" support LSIM's strategic needs. Developed in collaboration with Aalund NetSearch, it"+
			" is a central repository and service channel for providing market and competitive"+
			" information regarding our business environment. We have combined numerous"+
			" external and internal sources to deliver the right intelligence at the right time - a quick"+
			" and easy tool to help you cut through the information noise and gather key"+
			" business information that meets your needs.</p>" +
			"<br><p>Please note that the information on this desk is comprised of industry-related news"+
			" and current events which have been publicly reported as well as market research"+
			" Lonza has purchased." +
			"<b>The information contained on the desk is intended for"+
			" internal Lonza use only and should not be disseminated outside of Lonza.</b></p>" +
			"<br><p>" +
			"Browse through the Intelligence Desk, explore what is interesting to you," +
			" share what you know, and together we'll" +
			" make this tool valuable to our organization.Should you have any requests or questions," +
			" please contact:<br><br>Dana Protano, <br>201-316-9298,</font><br>" +
			"<font size="+2+" face= Verdana color=blue>dana.protano@lonza.com<br>" +
			"mailto:dana.protano@lonza.com</p></font>");
	
	private VerticalPanel contentpanel = new VerticalPanel();
	
	public LonzaWelcomeMessage()
	{
		welcomeLabel.setStylePrimaryName("welcomLbl");
		hpanelWelcomeLbl.setStylePrimaryName("hpanelforWelcomeLbl");
		hpanelWelcomeLbl.add(welcomeLabel);
		
		content.setStylePrimaryName("contentStyle");
		contentpanel.add(content);
		contentpanel.setStylePrimaryName("contentPanelStyle");
		//vpanelHtmlContent.setBorderWidth(1);
		vpanelHtmlContent.add(hpanelWelcomeLbl);
		//vpanelHtmlContent.setSpacing(1);
		vpanelHtmlContent.add(contentpanel);
		vpanelHtmlContent.setStylePrimaryName("outerWelcomePanel");
		
		Window.addWindowResizeListener(new WindowResizeListener() {
			public void onWindowResized(int width, int height) 
		{
				int frameWidth = width/2;
				//vpanelHtmlContent.setWidth(((Window.getClientWidth()/2)-100) + "px");
				vpanelHtmlContent.setWidth("550px");
			}
			});
		initWidget(vpanelHtmlContent);
	}
	
}
