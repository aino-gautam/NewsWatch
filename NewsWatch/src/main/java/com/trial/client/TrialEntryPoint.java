package com.trial.client;


import com.appUtils.client.Footer;
import com.common.client.LogoutPage;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedTabPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.login.client.UserInformation;
import com.newscenter.client.ItemStore;
import com.newscenter.client.NewsCenterMain;

public class TrialEntryPoint extends Composite implements EntryPoint,ClickHandler, SelectionHandler<Integer> , ValueChangeHandler {

	private Image imLogo = new Image("images/marketscapeLogoNewscatalyst.png");
	private VerticalPanel mainBasePanel = new VerticalPanel(); 
	private Hyperlink newscatalystlink = new Hyperlink("newscenter","newscenter");
	private Hyperlink logoutLink = new Hyperlink("logout","logoutpage");
    private Image logoutImage = new Image("images/log-out.gif");
	private Label welcomelbl = new Label();
	private HorizontalPanel linksPanel = new HorizontalPanel();
	private RootPanel panel;
	private DecoratedTabPanel decoratedTab;
	private UserInformation user;
	private TrialAccountSetup account;
	private ExistingTrialAccounts existingaccount;
	//private Statistics statistics;
	private Footer footer = new Footer();
	public static int logoutclick = 0;
	private int subscriptionMode = 0, newsFilterMode = 0, newsCenterId = 0;
	public static final String INIT_STATE = "trialstartpage";
	GWT.UncaughtExceptionHandler uncaughtExceptionHandler;
	
	public TrialEntryPoint(){
			Window.addResizeHandler(new ResizeHandler(){

				@Override
				public void onResize(ResizeEvent arg0) {
					mainBasePanel.setWidth(arg0.getWidth() - 200+"px");
				}
			});
			initWidget(mainBasePanel);
	}
			
	
	
	@Override
	public void onModuleLoad() {
		 uncaughtExceptionHandler = new GWT.UncaughtExceptionHandler() {
			 public void onUncaughtException(Throwable e) {
			        	  e.printStackTrace();
			 }
		    };
			GWT.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		panel = RootPanel.get();
		panel.clear();
		panel.add(this,90,0);
		initialize();
		
	}
	
	public void initHistorySupport(){
		History.addValueChangeHandler(this);
		String token = History.getToken();
		if (token.length() == 0) {
			History.newItem(INIT_STATE);
		}
		else {
			History.newItem(token);
		}
	}
	
	public void initialize(){
		getuserinformation();
		mainBasePanel.clear();
		linksPanel.clear();
		linksPanel.add(welcomelbl);
		linksPanel.add(new Image("images/verticalSeparator.JPG",0,0,6,13));
		linksPanel.add(newscatalystlink);
		linksPanel.add(new Image("images/verticalSeparator.JPG",0,0,6,13));
		linksPanel.add(logoutImage);
		linksPanel.add(logoutLink);
		linksPanel.setSpacing(3);
		
		
		account = new TrialAccountSetup();
		account.createUI();
		existingaccount = new ExistingTrialAccounts();
		//statistics = new Statistics(getNewsCenterId());
		
		decoratedTab = new DecoratedTabPanel();
		decoratedTab.setStylePrimaryName("decoratorTab");
		decoratedTab.add(account, "Setup Trial");
		decoratedTab.add(existingaccount,"Existing Trial");
		//decoratedTab.add(statistics,"Statistics");
		decoratedTab.setAnimationEnabled(true);
		decoratedTab.selectTab(0);
		decoratedTab.addSelectionHandler(this);
		
		
		logoutLink.addClickHandler(this);
		newscatalystlink.addClickHandler(this);
		HorizontalPanel header = new HorizontalPanel();
		header.add(imLogo);
		header.setCellHorizontalAlignment(imLogo, HasHorizontalAlignment.ALIGN_LEFT);
		header.add(linksPanel);
		header.setCellHorizontalAlignment(linksPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		header.setWidth("100%");
		
		HorizontalPanel headerPanel = new HorizontalPanel();
		headerPanel.add(header);
		headerPanel.setWidth("100%");
		
		mainBasePanel.add(headerPanel);
		
		HTML horizontalLine = new HTML("<hr align=left size=1 width=100% color:#DEDEDE>");
		mainBasePanel.add(horizontalLine);
		
		final VerticalPanel vp = new VerticalPanel();
		vp.add(decoratedTab);
		vp.setWidth("100%");
		mainBasePanel.add(vp);
		mainBasePanel.setSpacing(7);
		mainBasePanel.setCellHorizontalAlignment(vp,HasHorizontalAlignment.ALIGN_LEFT);
		DOM.setStyleAttribute(vp.getElement(), "marginTop", "3%");
		DOM.setStyleAttribute(vp.getElement(), "marginBottom", "5%");
		
		mainBasePanel.add(footer);
		
		mainBasePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainBasePanel.setWidth(Window.getClientWidth() - 200+"px");
		mainBasePanel.setStylePrimaryName("NCMainBasePanel");
		
		//initHistorySupport();
		}

	public void getuserinformation()
	{
		TrialInformationServiceAsync service = (TrialInformationServiceAsync)GWT.create(TrialInformationService.class);
		AsyncCallback<UserInformation> callback = new AsyncCallback<UserInformation>(){
			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
				System.out.println("There is some prob in receving user information");
			}
			public void onSuccess(UserInformation result) {
				try{
					String firstname="";
					String lastname = "";
					user = result;
					firstname = user.getFirstname();
					lastname = user.getLastname();
					account.setUserinfo(user);				
					welcomelbl.setText("Logged in as "+firstname+" "+lastname);
					welcomelbl.setStylePrimaryName("welcomlabelNCMain");
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("Problem in getnewsitemsforuser()");
				}	
			}
		};
		service.getuserinformation(callback);
	}

	@Override
	public void onClick(ClickEvent arg0) {
		if(arg0.getSource() instanceof Hyperlink){
			Hyperlink link = (Hyperlink)arg0.getSource();
			if(link.equals(logoutLink)){
				if(logoutclick == 0){
	 				logoutclick = 1;
		 			ItemStore.getInstance().saveUserSelection();
		 			LogoutPage logout = new LogoutPage();
		 			logout.removeFromSession(2);
	 			}
			}
			if(link.equals(newscatalystlink)){
				History.newItem("newscenter");
			}
		}
		
	}

	@Override
	public void onSelection(SelectionEvent<Integer> arg0) {
	  if(arg0.getSelectedItem() == 1){
		  //existingaccount.createFlexHeader();
		  existingaccount.getTrialAccounts();
		  existingaccount.getDeck().showWidget(0);
		  
	  }
	  else if(arg0.getSelectedItem() == 0){
		  account.createUI();
	  }
	}

	public int getSubscriptionMode() {
		return subscriptionMode;
	}

	public void setSubscriptionMode(int subscriptionMode) {
		this.subscriptionMode = subscriptionMode;
	}

	public int getNewsFilterMode() {
		return newsFilterMode;
	}

	public void setNewsFilterMode(int newsFilterMode) {
		this.newsFilterMode = newsFilterMode;
	}

	public int getNewsCenterId() {
		return newsCenterId;
	}

	public void setNewsCenterId(int newsCenterId) {
		this.newsCenterId = newsCenterId;
	}

	@Override
	public void onValueChange(ValueChangeEvent arg0) {
		if(arg0.getValue().equals("newscenter")){
			NewsCenterMain main = NewsCenterMain.getInstance();
			main.setSubscriptionMode(getSubscriptionMode());
		    main.setNewsFilterMode(getNewsFilterMode());
			main.onModuleLoad();
		}
		else if(arg0.getValue().equals("trialstartpage") || arg0.getValue().equals("")){
			onModuleLoad();
		}
	}

	
}
