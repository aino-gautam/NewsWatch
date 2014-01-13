package com.lonzaNewscenter.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class LonzaNewsCenterPage extends Composite implements EntryPoint, ClickHandler{
	public static int userid;
	public static int newscenterid;
	public static String path;
	private HorizontalPanel hPanel = new HorizontalPanel();
	private VerticalPanel vPanel = new VerticalPanel();
	private Label adminlbl = new Label("Lonza NewsCenter");
	private Label logoutlabel = new Label("logout");
	//private Label label = new Label();
	private Image img = new Image("images/Lonza-mci.jpg");
	private Image image = new Image("images/lonzaLogoLeft.gif");
	
	//private HeaderLonza header = new HeaderLonza();
	
	
	
	//private Frame frame = new Frame("http://myportal.cyberwatcher.com/v4/agents/readonly/readonly.aspx?userid=26698&an=1&ke=1&sp=0&re=0&fo=&bg=white&al=e8f8ff&ad=0&fi=1&so=1&ex=1&da=1&to=1&bo=1&df=dd%5c%2fMM+HH%3amm&co=&ar=0&se=1&ds=0&hc=f1ceaa&la=en&ma=1&ab=0&ext=1&dl=0&abs=1&mn=0&wi=0&bold=0&srep=0");
	//private DockPanel dock = new DockPanel();
	//private Frame frame = new Frame("http://myportal.cyberwatcher.com/v4/agents/readonly/readonly.aspx?userid=26698&an=1&ke=1&sp=0&re=0&fo=&bg=white&al=e8f8ff&ad=0&fi=0&so=1&ex=1&da=1&to=1&bo=1&df=dd%5c%2fMM+HH%3amm&co=&ar=1&se=0&ds=0&hc=f1ceaa&la=en&ma=1&ab=0&ext=1&dl=0&abs=1&mn=0&wi=0&bold=0&srep=0");
	private Frame frame = new Frame("http://myportal.cyberwatcher.com/v4/agents/readonly/viewxml.aspx?userid=26698&index=0&templateID=-1&an=1&ke=1&sp=0&re=0&fo=&bg=white&al=e8f8ff&ad=0&fi=1&so=1&ex=1&da=1&to=1&bo=1&df=dd%5c%2fMM+HH%3amm&co=&ar=1&se=1&ds=0&hc=f1ceaa&la=en&ma=1&ab=0&ext=1&dl=0&abs=1&mn=0&wi=0&bold=0&srep=0");
	RootPanel panel;
	private VerticalPanel vpanelContainer = new VerticalPanel();
	private HorizontalPanel hpanelContanerForAdminandLogout = new HorizontalPanel();
	private HorizontalPanel hpanelForFrame = new HorizontalPanel();
	private HorizontalPanel hpanelForHTMLandFrame = new HorizontalPanel();
	private Label welcomLbl = new Label("Welcome");
	private HorizontalPanel hpanelForWelcomeLbl = new HorizontalPanel();
	private VerticalPanel vpanelForHtmlandWelPanel = new VerticalPanel();
	//private HorizontalPanel hpanelContainer
	
	private HTML content = new HTML("<center><b><font size="+4+">Welcome to the Microbial Control Intelligence Desk!</font></b></center>" +
			"<font size="+3+" face= sans-serif ><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The Microbial Control Intelligence Desk is a business intelligence tool designed to"
			+"support LSIM's strategic needs. Developed in collaboration with Aalund NetSearch, it"+
			"&nbsp;is a central repository and service channel for providing market and &nbsp;competitive"+
			"&nbsp;information regarding our business environment. We have combined numerous"+
			"&nbsp;external and internal sources to deliver the right intelligence at the right time - a quick"+
			"&nbsp;and easy tool to help you cut through the information "+"noise"+" and &nbsp;gather key"+
			"&nbsp;business information that meets your needs." +
			"<br><br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Please note that the information on this desk is comprised of industry-related news"+
			"&nbsp;and current events which have been publicly reported as well as market research"+
			"&nbsp;Lonza has purchased." +
			"<b><i>The information contained on the desk is &nbsp;intended for"+
			"&nbsp;internal Lonza use only and should not be disseminated outside of Lonza.</b></i>" +
			"<br><br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
			"Browse through the Intelligence Desk, explore what is interesting to you," +
			"&nbsp;share what you know, and together we'll" +
			"&nbsp;make this tool valuable to our organization. Should you have any requests or questions," +
			"&nbsp;please contact: Dana Protano, &nbsp201-316-9298,</font>" +
			"<font size="+3+" face= Verdana color=blue>dana.protano@lonza.com  &nbsp;&nbsp;" +
			"mailto:dana.protano@lonza.com<br><br></font>");
	

	private HorizontalPanel htmlHorizontalPanel = new HorizontalPanel();
	
	public LonzaNewsCenterPage(){
		validateUser();

		//initWidget(dock);
		initWidget(vpanelContainer);
	}
	public static int getClientWidth()
	{
		return Window.getClientWidth();
	}
	
	public void lonzanewscenterInitialize(){
		getInformationFromSession();
		//getisadminInformation();
		
		adminlbl.addClickHandler(this);
		logoutlabel.addClickHandler(this);
		logoutlabel.setStylePrimaryName("logoutLbl");
		adminlbl.setStylePrimaryName("manageNesCenterLbl");
		//hpanelContanerForAdminandLogout.setBorderWidth(1);
		hpanelContanerForAdminandLogout.setStylePrimaryName("hpanelContainerAdminLogin");
		hpanelContanerForAdminandLogout.add(adminlbl);
		hpanelContanerForAdminandLogout.add(logoutlabel);
		
		
		
		int clientWidth = getClientWidth();
		
		//frame.setWidth("clientWidthpx");
		hpanelForFrame.setStylePrimaryName("hpanelforFrame");
		hpanelForFrame.setBorderWidth(1);
		
		frame.setWidth("714px");
		frame.setHeight("500px");
		hpanelForFrame.add(frame);
		//frame.setStylePrimaryName("framePanel");
		
		//hPanel.setStylePrimaryName("hpanelForIMages");
		img.setStylePrimaryName("imgLonzaRight");
		
		//hPanel.setBorderWidth(1);
		hPanel.add(image);
		hPanel.add(img);
		
		//hPanel.add(header);

		vPanel.setStylePrimaryName("vpanelForHorizontalPanels");
		vPanel.add(hPanel);
		vPanel.add(hpanelContanerForAdminandLogout);
		
		welcomLbl.setStylePrimaryName("welcomLbl");
		hpanelForWelcomeLbl.setStylePrimaryName("hpanelforWelcomeLbl");
		hpanelForWelcomeLbl.add(welcomLbl);
		//htmlHorizontalPanel.setBorderWidth(1);
		htmlHorizontalPanel.setStylePrimaryName("hpanelForHTMLContent");
		//htmlHorizontalPanel.setBorderWidth(1);
		htmlHorizontalPanel.add(content);
		
		//vpanelForHtmlandWelPanel.setStylePrimaryName("vpanelForHtmlandFrame");
		vpanelForHtmlandWelPanel.setWidth("500px");
		vpanelForHtmlandWelPanel.setHeight("500px");
		vpanelForHtmlandWelPanel.setBorderWidth(1);
		vpanelForHtmlandWelPanel.add(hpanelForWelcomeLbl);
		vpanelForHtmlandWelPanel.add(htmlHorizontalPanel);
		
		hpanelForHTMLandFrame.setStylePrimaryName("hpanelforFrameandHtml");
		//hpanelForHTMLandFrame.setBorderWidth(1);
		hpanelForHTMLandFrame.setSpacing(5);
		hpanelForHTMLandFrame.add(vpanelForHtmlandWelPanel);
		hpanelForHTMLandFrame.add(hpanelForFrame);
		
		vpanelContainer.setStylePrimaryName("vpanelContaner");
		//vpanelContainer.setBorderWidth(1);
		vpanelContainer.setSpacing(5);
		vpanelContainer.add(vPanel);
		vpanelContainer.add(hpanelForHTMLandFrame);
		//vpanelContainer.add(frame);

		//dock.add(vPanel,DockPanel.NORTH);
		//dock.add(frame,DockPanel.CENTER);
	}
	public void validateUser() {
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
					String valid = result.toString();
					if(valid.equals("true"))
					{
						lonzanewscenterInitialize();
					}
					else
					{
						Window.open("http://122.169.111.248:8080/NewsCenter/index.html", "_self", "");
					}

				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in getUserInfo()");
				}
			}
		};
		service.validateUser(callback);
	}

	public void getInformationFromSession()
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
					int id = (Integer)result;
					setUserid(id);
					
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in removeFromSession()");
				}

			}
		};
		service.getInformationFromSession(callback);
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
					adminlbl.setVisible(true);
				}
				else{
					adminlbl.setVisible(false);
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

	public void onModuleLoad() {
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}

	public static int getUserid() {
		return userid;
	}

	public static void setUserid(int userid) {
		LonzaNewsCenterPage.userid = userid;
	}

	public static int getNewscenterid() {
		return newscenterid;
	}

	public static void setNewscenterid(int newscenterid) {
		LonzaNewsCenterPage.newscenterid = newscenterid;
	}

	public static String getPath() {
		return path;
	}

	public static void setPath(String path) {
		LonzaNewsCenterPage.path = path;
	}

	/*public void onClick(Widget arg0) {
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			if(label.getText().equals("logout")){
				Window.open("http://122.169.111.248:8080/NewsCenter/index.html", "_self", "");
			}
			if(label.getText().equals("Manage NewsCenter")){
				Window.open("http://localhost:8888/com.admin.admin/admin.html", "_self", "");
			}
			
			if(label.getText().equals("Lonza NewsCenter")){
				LonzaNews lonzaNews = new LonzaNews();
				lonzaNews.onModuleLoad();
			}
		}
	}*/
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			if(label.getText().equals("logout")){
				Window.open("http://122.169.111.248:8080/NewsCenter/index.html", "_self", "");
			}
			/*if(label.getText().equals("Manage NewsCenter")){
				Window.open("http://localhost:8888/com.admin.admin/admin.html", "_self", "");
			}*/
			
			if(label.getText().equals("Lonza NewsCenter")){
				LonzaNews lonzaNews = new LonzaNews();
				lonzaNews.onModuleLoad();
			}
		}
	}
}