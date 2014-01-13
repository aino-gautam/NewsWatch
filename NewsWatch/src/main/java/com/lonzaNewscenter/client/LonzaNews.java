package com.lonzaNewscenter.client;

import com.admin.client.AdminPage;
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
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LonzaNews extends Composite implements ClickHandler, MouseOverHandler,MouseOutHandler
{
	private HeaderLonza header = new HeaderLonza(true);
	private HorizontalPanel hpanelFrame = new HorizontalPanel();
	private Frame frame = new Frame("http://myportal.cyberwatcher.com/v4/lonza/readonly/readonly.aspx?userid=30164&an=1&ke=1&sp=0&re=0&fo=&bg=white&al=f4f4f4&ad=0&fi=0&so=1&ex=1&da=1&to=1&bo=1&df=ddVMM%20HH:mm&co=&ar=1&se=1&ds=0&hc=f1ceaa&la=en&ma=1&ab=0&ext=1&dl=0&abs=1&mn=0&wi=0&bold=0&srep=0");
	//private Frame frame = new Frame("http://myportal.cyberwatcher.com/v4/agents/readonly/readonly.aspx?userid=26698&an=1&ke=1&sp=0&re=0&fo=&bg=white&al=e8f8ff&ad=0&fi=1&so=1&ex=1&da=1&to=1&bo=1&df=dd%5c%2fMM+HH%3amm&co=&ar=1&se=1&ds=0&hc=f1ceaa&la=en&ma=1&ab=0&ext=1&dl=0&abs=1&mn=0&wi=0&bold=0&srep=0");
	private Label manageNewsLbl = new Label("Manage NewsCenter");
	private VerticalPanel vpanelContainer = new VerticalPanel();
	private HorizontalPanel hpanelForFrame = new HorizontalPanel();
	private HorizontalPanel hpanelForHeader = new HorizontalPanel();
	private Label logoutLbl = new Label("logout");
	private HorizontalPanel hpanelAdminandLogout = new HorizontalPanel();
	private VerticalPanel vpanelForHeaderandLbl = new VerticalPanel();
	RootPanel panel;
	public LonzaNews()
	{
		getisadminInformation();
		int clientwidth = Window.getClientWidth();
		int clientHeight = Window.getClientHeight();
		
		int frameWidth = clientwidth - 100;
		int frameHeight = clientHeight-100;
		String frameWidthString = String.valueOf(frameWidth)+"px";
		String frameHeightString = String.valueOf(frameHeight)+"px";
		
		/*Window.addWindowResizeListener(new WindowResizeListener() {
			public void onWindowResized(int width, int height) 
			{
				int framewidth = width-100;
				int frameheight = height -100;
				frame.setWidth(framewidth + "px");
				frame.setHeight(frameheight + "px");
			}
			});
		*/
		
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				// TODO Auto-generated method stub
				int width=event.getWidth();
				int height=event.getHeight();
				int framewidth = width-100;
				int frameheight = height -100;
				frame.setWidth(framewidth + "px");
				frame.setHeight(frameheight + "px");
			}
		});
		System.out.println("The width"+clientwidth+"  "+frameWidth+"  "+frameWidthString);
		hpanelForFrame.setStylePrimaryName("hpanelFrameLonzaNews");
		hpanelForFrame.add(frame);
		
		logoutLbl.addClickHandler(this);
//		logoutLbl.addMouseListener(this);
		logoutLbl.addMouseOverHandler(this);
		logoutLbl.addMouseOutHandler(this);
		manageNewsLbl.addClickHandler(this);
//		manageNewsLbl.addMouseListener(this);
		manageNewsLbl.addMouseOverHandler(this);
		manageNewsLbl.addMouseOutHandler(this);
		
		manageNewsLbl.setStylePrimaryName("labelLogoutLonzaNews");
		logoutLbl.setStylePrimaryName("labelLogoutLonzaNews");
		
		if(clientwidth >1400){
			logoutLbl.addStyleName("headerlabel1440");
			manageNewsLbl.addStyleName("headerlabel1440");
		}
		else if(clientwidth >1200){
			logoutLbl.addStyleName("headerlabel1280");
			manageNewsLbl.addStyleName("headerlabel1280");
		}
		else if(clientwidth > 1000){
			//logoutlabel.setStylePrimaryName("labelLogoutLonzaNews");
			//adminlbl.setStylePrimaryName("labelLogoutLonzaNews");
			logoutLbl.addStyleName("headerlabel1024");
			manageNewsLbl.addStyleName("headerlabel1024");
		}
		
		frame.setWidth(frameWidthString);
		frame.setHeight(frameHeightString);
		
		//hpanelForHeader.setBorderWidth(1);
		hpanelForHeader.setStylePrimaryName("hpanelLonzaHeaderLonzaNews");
		hpanelForHeader.add(header);
		//hpanelAdminandLogout.setBorderWidth(1);
		//hpanelAdminandLogout.setStylePrimaryName("hpanelForLogoutManageLonzaNews");
		//hpanelAdminandLogout.setWidth(Window.getClientWidth() + "px");
		//hpanelAdminandLogout.setWidth("100%");
		//hpanelAdminandLogout.setHeight("50%");
		hpanelAdminandLogout.setSpacing(10);
		
		hpanelAdminandLogout.add(manageNewsLbl);
		hpanelAdminandLogout.add(logoutLbl);
		
		//vpanelForHeaderandLbl.setStylePrimaryName("vpanelForHeaderandLblLonzaNews");
		//vpanelForHeaderandLbl.setBorderWidth(1);
		//vpanelForHeaderandLbl.setWidth("1150px");
		//vpanelForHeaderandLbl.setHeight("50%");
		//vpanelForHeaderandLbl.add(hpanelForHeader);
		//vpanelForHeaderandLbl.add(hpanelAdminandLogout);
		
		//vpanelContainer.setBorderWidth(1);
		vpanelContainer.setStylePrimaryName("vpanelContainerForLonzaNews");
		//vpanelContainer.setWidth("1150px");
		//vpanelContainer.setHeight("100%");
		vpanelContainer.setSpacing(5);
		vpanelContainer.add(hpanelForHeader);
		vpanelContainer.add(hpanelAdminandLogout);
		vpanelContainer.add(hpanelForFrame);
		
		initWidget(vpanelContainer);
		
	}
	
	public void onModuleLoad() {
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}
	
	public void getisadminInformation(){
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
				int isadmin = (Integer)result;
				if(isadmin == 1){
					manageNewsLbl.setVisible(true);
				}
				else{
					manageNewsLbl.setVisible(false);
				}		
			}catch(Exception ex)
			{
				ex.printStackTrace();
				System.out.println("problem in removeFromSession()");
			}

		}
	};
	service.getisadminInformation(callback);
	}

	
	/*public void onClick(Widget sender) 
	{
		if(sender instanceof Label)
		{
			Label label = (Label)sender;
			if(label.getText().equals("Manage NewsCenter"))
			{
				AdminPage admin = new AdminPage();
				admin.onModuleLoad();
				//Window.open("http://localhost:8888/com.admin.admin/admin.html", "_self", "");
			}
			if(label.getText().equals("logout"))
			{
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession();
				
				String urlClient = GWT.getModuleBaseURL();
				String[]  url = new String[5];
				url = urlClient.split("/");
				String urltoredirect = "www.aalundnetsearch.com";
				String urlport = url[0]+"//"+urltoredirect;
				Window.open(urlport, "_self", "");
				
			}
		}
		
		
	}*/

	public void onMouseDown(Widget arg0, int arg1, int arg2) {

	}

/*
	public void onMouseEnter(Widget arg0) {
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			//label.removeStyleName("labelLogoutLonzaNews");
			label.addStyleName("labelHover");
		}
	}


	public void onMouseLeave(Widget arg0) {
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
			if(label.getText().equals("Manage NewsCenter"))
			{
				AdminPage admin = new AdminPage();
				admin.onModuleLoad();
				//Window.open("http://localhost:8888/com.admin.admin/admin.html", "_self", "");
			}
			if(label.getText().equals("logout"))
			{
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession();
				
				String urlClient = GWT.getModuleBaseURL();
				String[]  url = new String[5];
				url = urlClient.split("/");
				String urltoredirect = "www.aalundnetsearch.com";
				String urlport = url[0]+"//"+urltoredirect;
				Window.open(urlport, "_self", "");
				
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
