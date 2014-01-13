package com.login.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.appUtils.client.Header;
import com.appUtils.client.Footer;
import com.appUtils.client.PopUpForForgotPassword;

public class MainPage extends Composite  implements EntryPoint,ClickHandler {
	
		RootPanel panel;
		private VerticalPanel baseVPanel = new VerticalPanel();
		private VerticalPanel headerVPanel = new VerticalPanel();
		private VerticalPanel footerVPanel = new VerticalPanel();
		private VerticalPanel mainBasePanel = new VerticalPanel();
		private VerticalPanel linkPanel = new VerticalPanel();
		private VerticalPanel labelPanel = new VerticalPanel();
		private Label welcomeMsg = new Label("Welcome to NewsCenter");
		private Label label = new Label("Please select a NewsCenter to view/subscribe to");
		private Header headerBar = new Header();
		private Footer footerBar = new Footer();
		
		public static int selectedNewsCenter;
		public static int HEARINGAID = 1;
		public static int LONZA = 2;
		public static String industryNewsCeterName; 
		
//		public static int WINDENERGRY = 2;
		
		public MainPage(){
		
			welcomeMsg.setStylePrimaryName("welcomeMsg");
			label.setStylePrimaryName("labelMsg");
			
			headerVPanel.add(headerBar);
			headerVPanel.setStylePrimaryName("containerPanel");
			
			footerVPanel.add(footerBar);
			footerVPanel.setStylePrimaryName("containerPanel");
			labelPanel.add(welcomeMsg);
			labelPanel.add(label);
			labelPanel.setSpacing(10);
			labelPanel.setStylePrimaryName("containerPanel");
			
			linkPanel.add(labelPanel);
			linkPanel.add(createLink("Hearing Aids NewsCenter"));
			linkPanel.add(createLink("Lonza NewsCenter"));
//			linkPanel.add(createLink("Wind Energy NewsCenter"));
//			linkPanel.add(createLink("Solar Energy NewsCenter"));
			linkPanel.setStylePrimaryName("bodyPanel");
			linkPanel.setSpacing(25);
			
			baseVPanel.add(headerVPanel);
			baseVPanel.add(linkPanel);
			baseVPanel.add(footerVPanel);
			baseVPanel.setStylePrimaryName("containerPanel");
			
			mainBasePanel.add(baseVPanel);
			mainBasePanel.setStylePrimaryName("basePanel");
			//baseVPanel.setStylePrimaryName("basePanel");

			initWidget(mainBasePanel);
			
		}

		//Old method
		/*public Hyperlink createLink(String linkTxt)
		{
			//HTML hyperlink = new HTML("<a href='www.newscenter.com'>"+linkTxt+"</a>",true);
			Hyperlink hyperlink = new Hyperlink();
			hyperlink.setText(linkTxt);
			hyperlink.setStylePrimaryName("NewsCenterLinks");
			hyperlink.addClickListener(this);
			return hyperlink;
		}*/
		
		//New method
		public Anchor createLink(String linkTxt)
		{
			Anchor hyperlink = new Anchor();
			hyperlink.setText(linkTxt);
			hyperlink.setStylePrimaryName("NewsCenterLinks");
			hyperlink.addClickHandler(this);
			return hyperlink;
		}
		
		/*public void onClick(Widget arg0) {
			 if(arg0 instanceof Hyperlink){
				 Hyperlink link = (Hyperlink) arg0;
				 if(link.getText().equals("Hearing Aids NewsCenter")){
					 setSelectedNewsCenter(MainPage.HEARINGAID);
					 setIndustryNewsCeterName("Hearing Aid");
					 LoginPage loginPage = new LoginPage();
					 linkPanel.clear();
					 linkPanel.add(loginPage);
				//	 panel.clear();
				//	 panel.add(loginPage);
				 }
				 else if(link.getText().equals("Lonza NewsCenter")){
					 setSelectedNewsCenter(MainPage.LONZA);
					 setIndustryNewsCeterName("Lonza NewsCenter");
					 LoginPage loginPage = new LoginPage();
					 linkPanel.clear();
					 linkPanel.add(loginPage);
				 }
				 else if(link.getText().equals("Wind Energy NewsCenter")|| link.getText().equals("Solar Energy NewsCenter")){
					 PopUpForForgotPassword popup = new PopUpForForgotPassword("This site is under construction");
					 popup.setPopupPosition(600, 300);
					 popup.show();
				 }
			 }
		}
*/
		public void onModuleLoad() {
			 panel = RootPanel.get();
			 panel.add(this);
		}

		public static int getSelectedNewsCenter() {
			return selectedNewsCenter;
		}

		public static void setSelectedNewsCenter(int selectedNewsCenter) {
			MainPage.selectedNewsCenter = selectedNewsCenter;
		}

		public static String getIndustryNewsCeterName() {
			return industryNewsCeterName;
		}

		public static void setIndustryNewsCeterName(String industryNewsCeterName) {
			MainPage.industryNewsCeterName = industryNewsCeterName;
		}

		@Override
		public void onClick(ClickEvent event) {
			Widget arg0=(Widget) event.getSource(); 
			if(arg0 instanceof Hyperlink){
				 Hyperlink link = (Hyperlink) arg0;
				 if(link.getText().equals("Hearing Aids NewsCenter")){
					 setSelectedNewsCenter(MainPage.HEARINGAID);
					 setIndustryNewsCeterName("Hearing Aid");
					 LoginPage loginPage = new LoginPage();
					 linkPanel.clear();
					 linkPanel.add(loginPage);
				//	 panel.clear();
				//	 panel.add(loginPage);
				 }
				 else if(link.getText().equals("Lonza NewsCenter")){
					 setSelectedNewsCenter(MainPage.LONZA);
					 setIndustryNewsCeterName("Lonza NewsCenter");
					 LoginPage loginPage = new LoginPage();
					 linkPanel.clear();
					 linkPanel.add(loginPage);
				 }
				 else if(link.getText().equals("Wind Energy NewsCenter")|| link.getText().equals("Solar Energy NewsCenter")){
					 PopUpForForgotPassword popup = new PopUpForForgotPassword("This site is under construction");
					 popup.setPopupPosition(600, 300);
					 popup.show();
				 }
			 }
			
		}
	}
