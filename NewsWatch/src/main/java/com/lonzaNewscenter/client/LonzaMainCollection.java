package com.lonzaNewscenter.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.common.client.HeaderLonza;
import com.common.client.LogoutPage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.login.client.UserInformation;

public class LonzaMainCollection extends Composite implements ClickHandler, MouseOverHandler,MouseOutHandler
{
	private HeaderLonza header = new HeaderLonza(false);

	private Label adminlbl = new Label("newsarchive");
	private Label logoutlabel = new Label("logout");
	private Label changedetailslbl = new Label("change password");
	private Label welcomelbl = new Label();
	private Label usernamelbl = new Label();
	private HorizontalPanel hpanelLabels = new HorizontalPanel();

	private LonzaWelcomeMessage  message = new LonzaWelcomeMessage();
	private LonzaLinkNews link = new LonzaLinkNews();
	private HorizontalPanel hpanelLinkMessage = new HorizontalPanel();

	private VerticalPanel vpanelContent = new VerticalPanel();
	private UserInformation userinfo;
	private HorizontalPanel hpanelusername = new HorizontalPanel();
	
	public LonzaMainCollection()
	{
		getUserInformation();
		int clientwidth = Window.getClientWidth();
		int clientHeight = Window.getClientHeight();
		int frameWidth = clientwidth - 100;
		int frameHeight = clientHeight-100;
		String frameWidthString = String.valueOf(frameWidth)+"px";
		String frameHeightString = String.valueOf(frameHeight)+"px";
		
		
		System.out.println("the widht and he>>>"+clientwidth+"   "+clientHeight);
		adminlbl.addClickHandler(this);
//		adminlbl.addMouseListener(this);
		adminlbl.addMouseOverHandler(this);
		adminlbl.addMouseOutHandler(this);
		logoutlabel.addClickHandler(this);
//		logoutlabel.addMouseListener(this);
		logoutlabel.addMouseOverHandler(this);
		logoutlabel.addMouseOutHandler(this);
		changedetailslbl.addClickHandler(this);
//		changedetailslbl.addMouseListener(this);
		changedetailslbl.addMouseOverHandler(this);
		changedetailslbl.addMouseOutHandler(this);
		
		
		
		/*logoutlabel.setStylePrimaryName("logoutLbl");
		adminlbl.setStylePrimaryName("manageNesCenterLbl");*/
		logoutlabel.setStylePrimaryName("labelLogoutLonzaNews");
		adminlbl.setStylePrimaryName("labelLogoutLonzaNews");
		changedetailslbl.setStylePrimaryName("labelLogoutLonzaNews");
		welcomelbl.setStylePrimaryName("welcomelbl");
		usernamelbl.setStylePrimaryName("welcomeusernamelbl");
		
		
		if(clientwidth >1400){
			logoutlabel.addStyleName("headerlabel1440");
			adminlbl.addStyleName("headerlabel1440");
			changedetailslbl.addStyleName("headerlabel1440");
			hpanelusername.addStyleName("headerlabel1440");
		}
		else if(clientwidth >1200){
			logoutlabel.addStyleName("headerlabel1280");
			adminlbl.addStyleName("headerlabel1280");
			changedetailslbl.addStyleName("headerlabel1280");
			hpanelusername.addStyleName("headerlabel1280");
		}
		else if(clientwidth > 1000){
			//logoutlabel.setStylePrimaryName("labelLogoutLonzaNews");
			//adminlbl.setStylePrimaryName("labelLogoutLonzaNews");
			logoutlabel.addStyleName("headerlabel1024");
			adminlbl.addStyleName("headerlabel1024");
			changedetailslbl.addStyleName("headerlabel1024");
			hpanelusername.addStyleName("headerlabel1024");
		}
		hpanelusername.add(welcomelbl);
		hpanelusername.add(usernamelbl);
		hpanelLabels.add(hpanelusername);
		
		hpanelLabels.add(adminlbl);
		hpanelLabels.add(changedetailslbl);
		hpanelLabels.add(logoutlabel);
		hpanelLabels.setSpacing(10);
		//message.setWidth("520px");
		//message.setWidth(((clientwidth/2)-100) + "px");
		//link.setWidth("900px");
		//link.setHeight("50%");
		hpanelLinkMessage.setStylePrimaryName("hpanelCenter");
		hpanelLinkMessage.setWidth(frameWidthString);
		hpanelLinkMessage.setHeight(frameHeightString);
		hpanelLinkMessage.setSpacing(10);
		hpanelLinkMessage.add(message);
		hpanelLinkMessage.add(link);
		
		//vpanelContent.setWidth("100%");
		//vpanelContent.setStylePrimaryName("vpanelMain");
		vpanelContent.setWidth(Window.getClientWidth()+"px");
		vpanelContent.setSpacing(2);
		vpanelContent.add(header);
		vpanelContent.add(hpanelLabels);
		vpanelContent.add(hpanelLinkMessage);
		
		
		/*Window.addWindowResizeListener(new WindowResizeListener() {
			public void onWindowResized(int width, int height) {
				vpanelContent.setWidth(width + "px");
			}
			});*/
		
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				// TODO Auto-generated method stub
				int width=event.getWidth();
				vpanelContent.setWidth(width + "px");
			}
		});
	
			
		

		initWidget(vpanelContent);
		
	}
	public void getUserInformation()
	{
		ServiceProviderAsync service = (ServiceProviderAsync)GWT.create(ServiceProvider.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "lonzaNewsCenter";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){
		public void onFailure(Throwable caught) {
			caught.printStackTrace();	
		}

		public void onSuccess(Object result) {
			try{
			ArrayList arrayuserinfo = (ArrayList)result;
			addusernametolabel(arrayuserinfo);
			}catch(Exception ex)
			{
				ex.printStackTrace();
				System.out.println("problem in removeFromSession()");
			}

		}
	};
	service.getUserInformation(callback);
	}
	public void addusernametolabel(ArrayList arrayuserinfo)
	{
		Iterator iter = arrayuserinfo.iterator();
		while(iter.hasNext())
		{
			userinfo = (UserInformation)iter.next();
		}
		String username = userinfo.getFirstname();
		//usernamelbl.setText(username);
		welcomelbl.setText("Welcome "+username);
	}

	public void onModuleLoad() {
		RootPanel.get().clear();
		RootPanel.get().add(this);
	}
	
	/*public void onClick(Widget sender)
	{
		if(sender instanceof Label)
		{
			Label label = (Label)sender;
			
			if(label.getText().equals("logout")){
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession();
				
				String urlClient = GWT.getModuleBaseURL();
				String[]  url = new String[5];
				url = urlClient.split("/");
				String urltoredirect = "www.aalundnetsearch.com";
				String urlport = url[0]+"//"+urltoredirect;
				Window.open(urlport, "_self", "");
			}
			if(label.getText().equals("Manage NewsCenter")){
				Window.open("http://localhost:8888/com.admin.admin/admin.html", "_self", "");
			}
			
			if(label.getText().equals("newsarchive")){
				LonzaNews lonzaNews = new LonzaNews();
				lonzaNews.onModuleLoad();
			}
			
			if(label.getText().equals("change password"))
			{
				ChangeDetails change = new ChangeDetails();
				change.onModuleLoad();
			}
		
		}
		
	}*/
	public void onMouseDown(Widget arg0, int arg1, int arg2) {	
	}

//	public void onMouseEnter(Widget arg0) {
//		if(arg0 instanceof Label){
//			Label label = (Label)arg0;
//			//label.removeStyleName("labelLogoutLonzaNews");
//			label.addStyleName("labelHover");
//		}
//	}

	/*public void onMouseLeave(Widget arg0) {
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			label.removeStyleName("labelHover");
			//label.setStylePrimaryName("labelLogoutLonzaNews");
		}
	}*/

	public void onMouseMove(Widget arg0, int arg1, int arg2) {

	}

	public void onMouseUp(Widget arg0, int arg1, int arg2) {

	}
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Widget sender=(Widget)event.getSource();
		if(sender instanceof Label)
		{
			Label label = (Label)sender;
			
			if(label.getText().equals("logout")){
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession();
				
				String urlClient = GWT.getModuleBaseURL();
				String[]  url = new String[5];
				url = urlClient.split("/");
				String urltoredirect = "www.aalundnetsearch.com";
				String urlport = url[0]+"//"+urltoredirect;
				Window.open(urlport, "_self", "");
			}
			/*if(label.getText().equals("Manage NewsCenter")){
				Window.open("http://localhost:8888/com.admin.admin/admin.html", "_self", "");
			}*/
			
			if(label.getText().equals("newsarchive")){
				LonzaNews lonzaNews = new LonzaNews();
				lonzaNews.onModuleLoad();
			}
			
			if(label.getText().equals("change password"))
			{
				ChangeDetails change = new ChangeDetails();
				change.onModuleLoad();
			}
		
		}
	}
	@Override
	public void onMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			label.removeStyleName("labelHover");
			//label.setStylePrimaryName("labelLogoutLonzaNews");
		}
	}
	@Override
	public void onMouseOver(MouseOverEvent event) {
		// TODO Auto-generated method stub
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			//label.removeStyleName("labelLogoutLonzaNews");
			label.addStyleName("labelHover");
		}
	}
}
