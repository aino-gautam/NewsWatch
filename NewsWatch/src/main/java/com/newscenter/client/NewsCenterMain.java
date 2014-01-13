package com.newscenter.client;

import java.util.ArrayList;
import java.util.Iterator;

//import com.admin.client.AdminPage;
import com.appUtils.client.Footer;
//import com.common.client.LogoutPage;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.login.client.UserInformation;

import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsProviderServiceAsync;
import com.newscenter.client.ui.MainNewsPresenter;
import com.newscenter.client.ui.MainTagPresenter;

public class NewsCenterMain extends Composite implements EntryPoint, HistoryListener,ClickHandler {

	private MainTagPresenter tagPresenter = new MainTagPresenter();
	private MainNewsPresenter newsPresenter = new MainNewsPresenter();
	private VerticalPanel basePanel = new VerticalPanel();
	private Footer footer = new Footer();
	private HorizontalPanel headerPanel = new HorizontalPanel();
	/*private Hyperlink adminLink = new Hyperlink("Manage","adminpage");
	private Hyperlink logoutLink = new Hyperlink("logout","logoutpage");*/
	
	private Anchor adminLink = new Anchor("Manage","adminpage");
	private Anchor logoutLink = new Anchor("logout","logoutpage");
	
	private Image adminImage = new Image("images/manage-key.gif");
	private Image logoutImage = new Image("images/log-out.gif");
	private Hyperlink accountLink = new Hyperlink("My Account","myaccount");
	private Label welcomelbl = new Label();
	private PageCriteria criteria = new PageCriteria();
	private Image imNewsCatalyst = new Image("images/marketscapeLogoNewscatalyst.png");
	private HorizontalPanel linksPanel = new HorizontalPanel();
	private HorizontalPanel header = new HorizontalPanel();
	private String email = "";
	public static final String INIT_STATE = "mainpage";
	int isadmin = 0;
	int subscriptionMode = 0;
	int newsFilterMode = 0;
	GWT.UncaughtExceptionHandler uncaughtExceptionHandler;
	public static int logoutclick = 0;
	public static boolean tagSelectionChanged = false;
	RootPanel panel;
	private static UserInformation userInformation = new UserInformation();
	private static int userSelectedIndustryId = 0;
	private static NewsCenterMain newsCenterMain = new NewsCenterMain();
	
	public NewsCenterMain(){
		criteria.setPageSize(15);
		criteria.setStartIndex(0);
		ItemStore.getInstance().setCriteria(criteria);
		NewsStore.getInstance().setCriteria(criteria);
		newsPresenter.setPageCriteria(criteria);
		initWidget(basePanel);
		History.addHistoryListener(this);
	}
	
	public static NewsCenterMain getInstance(){
		return newsCenterMain;
	}
	
	public void initialize(){
		newsPresenter.setNewsMode(getNewsFilterMode());
		newsPresenter.setSubscriptionMode(getSubscriptionMode());
		ItemStore.getInstance().setNewsmode(getNewsFilterMode());
		NewsStore.getInstance().setNewsmode(getNewsFilterMode());
		getadmininformation();
		
		adminLink.setVisible(false);
		adminImage.setVisible(false);
		//adminLink.addClickListener(this);
		
		adminLink.addClickHandler(this);
		logoutLink.addClickHandler(this);
		adminLink.addStyleName("NChyperlinkStyle");
		//logoutLink.addClickListener(this);
		logoutLink.addStyleName("NChyperlinkStyle");
		adminLink.setTitle("Manage tags,news etc.");
		logoutLink.setTitle("Logout from the NewsCatalyst");
		linksPanel.clear();
		linksPanel.add(welcomelbl);
		linksPanel.add(new Image("images/verticalSeparator.JPG",0,0,6,13));
		linksPanel.add(adminImage);
		linksPanel.add(adminLink);
		linksPanel.add(logoutImage);
		linksPanel.add(logoutLink);
		linksPanel.setSpacing(3);
		
		header.add(imNewsCatalyst);
		header.setCellHorizontalAlignment(imNewsCatalyst, HasHorizontalAlignment.ALIGN_LEFT);
		header.add(linksPanel);
		header.setCellHorizontalAlignment(linksPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		header.setWidth("100%");
		
		headerPanel.add(header);
		headerPanel.setWidth("100%");
		
		basePanel.add(headerPanel);
		basePanel.add(tagPresenter);
		basePanel.setCellVerticalAlignment(tagPresenter, HasVerticalAlignment.ALIGN_TOP);
		basePanel.setCellHorizontalAlignment(tagPresenter, HasHorizontalAlignment.ALIGN_LEFT);
		basePanel.add(newsPresenter);
		basePanel.setCellHorizontalAlignment(newsPresenter, HasHorizontalAlignment.ALIGN_LEFT);
		basePanel.add(footer);
		basePanel.setSpacing(10);
	
		basePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		//old method
		/*Window.addWindowResizeListener(new WindowResizeListener(){
			public void onWindowResized(int arg0, int arg1) {
				int width = arg0 - 200;
				if(arg0 < 1255){
					
				}
				else{
					tagPresenter.resetSize(width, 0);
					newsPresenter.resetSize(width, 0);
				}
			}
		});*/
		
		//new method
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				// TODO Auto-generated method stub
				int arg0 = event.getWidth();
				int arg1 = event.getHeight();
				int width = arg0 - 200;
				if(arg0 < 1255){
					
				}
				else{
					tagPresenter.resetSize(width, 0);
					newsPresenter.resetSize(width, 0);
				}
				
			}
		});
		
		/*Window.addWindowCloseListener(new WindowCloseListener(){
			public void onWindowClosed() {
						if(onWindowClosing().equals("Cancel")){
							System.out.println("Saving settings");
							ItemStore.getInstance().saveUserSelection();
						}	
			}
			public String onWindowClosing() {
				if(logoutclick == 0){
					if(tagSelectionChanged == true)
						return "Your changes have not been saved. Press OK to exit without saving changes. Press Cancel and use the logout link to save your changes";
				}
			return null;
				
			}
		});*/	
		
		Window.addCloseHandler(new CloseHandler<Window>() {
			
			@Override
			public void onClose(CloseEvent<Window> event) {
				// TODO Auto-generated method stub
				if(onWindowClosing().equals("Cancel")){
					System.out.println("Saving settings");
					ItemStore.getInstance().saveUserSelection();
				}
			}
			public String onWindowClosing() {
				if(logoutclick == 0){
					if(tagSelectionChanged == true)
						return "Your changes have not been saved. Press OK to exit without saving changes. Press Cancel and use the logout link to save your changes";
				}
			return null;
				
			}
			
		});
		
		
		basePanel.setStylePrimaryName("NCMainBasePanel");
		calculateWidth();
	}
	
	public void calculateWidth(){
		int width = Window.getClientWidth()-200;
		
		if(Window.getClientWidth() < 1255){
			tagPresenter.resetSize(1055,0);
			newsPresenter.resetSize(1055,0);
		}
		else{
			tagPresenter.resetSize(width, 0);
			newsPresenter.resetSize(width, 0);
		}
	}
	
	public void initHistorySupport(){
		History.addHistoryListener(this);
		String token = History.getToken();
		if (token.length() == 0) {
			onHistoryChanged(INIT_STATE);
		}
		else {
			onHistoryChanged(token);
		}
	}
	
	public void onHistoryChanged(String historytoken){
		try{
			if(historytoken.equals("adminpage")){
				ItemStore.getInstance().setCurrentNewsCache(null);
				ItemStore.getInstance().saveUserSelection();
				/*AdminPage admin = new AdminPage(email);
		 		admin.onModuleLoad();*/
			}
	
		else if(historytoken.equals("mainpage")||historytoken.length()==0){
			//NewsCenterMain newscentermain = new NewsCenterMain();
			RootPanel.get().clear();
			RootPanel.get().add(this,90,0);
			this.onModuleLoad();
			initialize();
		}
		else if(historytoken.equals("trialstartpage"))	{
			ItemStore.getInstance().setCurrentNewsCache(null);
			ItemStore.getInstance().saveUserSelection();
			//RootPanel.get().clear();
			History.newItem("trialstartpage");
		}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void getadmininformation()
	{
		NewsProviderServiceAsync service = ServiceUtils.getNewsProviderServiceAsync();
		AsyncCallback callback = new AsyncCallback(){
			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
				System.out.println("There is some prob in receving newsitems");
			}
			public void onSuccess(Object result) {
				try{
					String firstname="";
					String lastname = "";
					ArrayList arrayref =(ArrayList)result;
					Iterator iter = arrayref.iterator();
					while(iter.hasNext()){
						UserInformation user = (UserInformation)iter.next();
						userInformation = user;
						isadmin = user.getIsAdmin();
						firstname = user.getFirstname();
						lastname = user.getLastname();
						email = user.getEmail();
						subscriptionMode = user.getPeriod();
						newsFilterMode = user.getNewsFilterMode();
						System.out.println("SUBSCRIPTION PERIOD IS "+ subscriptionMode);
					}
					if(isadmin==1)
					{
						adminImage.setVisible(true);
						adminLink.setVisible(true);
					}
					welcomelbl.setText("Logged in as "+firstname+" "+lastname);
					welcomelbl.setStylePrimaryName("welcomlabelNCMain");
					newsPresenter.setSubscriptionMode(subscriptionMode);
					newsPresenter.setNewsMode(newsFilterMode);
					
					ItemStore.getInstance().setNewsmode(newsFilterMode);
					NewsStore.getInstance().setNewsmode(newsFilterMode);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("Problem in getnewsitemsforuser()");
				}	
			}
		};
		service.getadmininformation(callback);
	}

	public void onModuleLoad() {
		   uncaughtExceptionHandler = new GWT.UncaughtExceptionHandler() {
		          public void onUncaughtException(Throwable e) {
		        	  e.printStackTrace();
		          }
		    };
		    GWT.setUncaughtExceptionHandler(uncaughtExceptionHandler);
		ItemStore.getInstance().setNcMainRef(this);
		ItemStore.getInstance().initialize();
		panel = RootPanel.get();
		panel.clear();
		panel.add(this,90,0);
	}

	/*public void onClick(Widget sender) {
		 if(sender instanceof Hyperlink)
		 {
			Hyperlink link = (Hyperlink)sender;
	
		 	if(link.getText().equals("logout"))
		 		{
		 			if(logoutclick == 0){
		 				logoutclick = 1;
			 			ItemStore.getInstance().saveUserSelection();
			 			LogoutPage logout = new LogoutPage();
			 			logout.removeFromSession(isadmin);
		 			}
		 		}
		 }
	}*/

	public int getNewsFilterMode() {
		return newsFilterMode;
	}

	public void setNewsFilterMode(int newsFilterMode) {
		this.newsFilterMode = newsFilterMode;
	}

	public int getSubscriptionMode() {
		return subscriptionMode;
	}

	public void setSubscriptionMode(int subscriptionMode) {
		this.subscriptionMode = subscriptionMode;
	}

	public static UserInformation getUserInformation() {
		return userInformation;
	}

	public static void setUserInformation(UserInformation userInformation) {
		NewsCenterMain.userInformation = userInformation;
	}

	public static int getUserSelectedIndustryId() {
		return userSelectedIndustryId;
	}

	public static void setUserSelectedIndustryId(int userSelectedIndustryId) {
		NewsCenterMain.userSelectedIndustryId = userSelectedIndustryId;
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		if(sender instanceof Anchor || sender instanceof Hyperlink)
		 {
			Hyperlink link = (Hyperlink)sender;
	
		 	if(link.getText().equals("logout"))
		 		{
		 			if(logoutclick == 0){
		 				logoutclick = 1;
			 			ItemStore.getInstance().saveUserSelection();
			 			/*LogoutPage logout = new LogoutPage();
			 			logout.removeFromSession(isadmin);*/
		 			}
		 		}
		 }
		
	}
}
