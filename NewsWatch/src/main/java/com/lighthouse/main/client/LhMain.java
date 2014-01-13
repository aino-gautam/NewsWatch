package com.lighthouse.main.client;

import java.util.ArrayList;

import com.appUtils.client.Footer;
import com.appUtils.client.PopupWidget;
import com.appUtils.client.exception.AuthorizationException;
import com.appUtils.client.service.AppUtilsService;
import com.appUtils.client.service.AppUtilsServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.group.client.GroupItemStore;
import com.lighthouse.group.client.GroupManager;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.client.service.LhNewsProviderService;
import com.lighthouse.main.client.service.LhNewsProviderServiceAsync;
import com.lighthouse.search.client.ui.SearchResultPresenter;



/**
 * The main class for application initialization
 * @author nairutee
 *
 */
public class LhMain extends Composite implements EntryPoint, ClickHandler {
// {
	private LhUser lhUser;
	private Footer footer = new Footer();
	private HorizontalPanel headerPanel = new HorizontalPanel();
	private VerticalPanel basePanel = new VerticalPanel();
	private Anchor adminLink = new Anchor("Manage");
	private Anchor logoutLink = new Anchor("logout");
	private Anchor reviewFeedLink = new Anchor("Review Feed");
	private Image adminImage = new Image("images/manage-key.gif");
	private Image logoutImage = new Image("images/log-out.gif");
	private Image reviewFeedImage = new Image("images/feed/reviewFeed.png");
	
	private Label welcomelbl = new Label();
	private Image imNewsCatalyst;
	private HorizontalPanel linksPanel = new HorizontalPanel();
	private HorizontalPanel header = new HorizontalPanel();
	private String email = "";
	public static final String INIT_STATE = "mainpage";
	int isadmin = 0;
	int subscriptionMode = 0;
	int newsFilterMode = 0;
	GWT.UncaughtExceptionHandler uncaughtExceptionHandler;
	public static int logoutclick = 0;
	public static int manageclick = 0;
	public static int previewFeedClick =0;
	public static boolean tagSelectionChanged = false;
	private RootPanel panel;
	private GroupManager groupManager = GroupManager.getInstance();
	private SearchResultPresenter resultPresenter; 
	private DeckPanel deckPanel = new DeckPanel();
	private String clientLogo = null;
	private String keyVal="";
	private String keyCombination="18171669";
	 
	public LhMain() {		
		initWidget(basePanel);		
		String nid = Window.Location.getParameter("nid");            
		String lockednid = Window.Location.getParameter("lockednid");         
		if(nid == null && lockednid ==  null)
		{		       
			initialize();               
		}	
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
		panel.add(this,20,10);
	}
	
	/**
	* initializes the page
	*/
	private void initialize(){
		groupManager.setLhMainRef(this);
		resultPresenter = new SearchResultPresenter();
	
		resultPresenter.setLhMainRef(this);
		
		LhNewsProviderServiceAsync newsProviderService = GWT.create(LhNewsProviderService.class);
		newsProviderService.getadmininformation(new AsyncCallback() {
		
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("User information could not be fetched:: initialize() LhMain");
				caught.printStackTrace();
			}
		
			@Override
			public void onSuccess(Object result) {
				if(result == null){
					String urlQueryString = Window.Location.getQueryString();
					String url = GWT.getHostPageBaseURL() + "lhlogin.html"+urlQueryString;
					Window.open(url, "_self", null);
				}else{
					ArrayList list = (ArrayList)result;
					final LhUser user = (LhUser)list.get(0);
					clientLogo = (String) list.get(1);
					if(user == null){
						String urlQueryString = Window.Location.getQueryString();
						String url = GWT.getHostPageBaseURL() + "lhlogin.html"+urlQueryString;
						Window.open(url, "_self", null);
					}else{
						
						 AppUtilsServiceAsync service = (AppUtilsServiceAsync)GWT.create(AppUtilsService.class);
						 service.isValidAppAccess(new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
									if (caught instanceof AuthorizationException) {
											PopupPanel popupPanel=new PopupPanel(false, false);
											popupPanel.setSize("100%", "100%");
											popupPanel.center();
											
											AuthorizationException aex=(AuthorizationException)caught;
												if(aex.getAuthExceptionType()==AuthorizationException.SERVICENOTACTIVATED){
														popupPanel.setWidget(new Label("You are not authorised to access this page."));
														popupPanel.show();
													
												}
												if(aex.getAuthExceptionType()==AuthorizationException.SERVICEEXPIRED){
													popupPanel.setWidget(new Label("This service is currently not activated."));
													popupPanel.show();
												
											}
										}
								
							}

							@Override
							public void onSuccess(Boolean result) {
								if(result){
									setUserInformation(user);
									resultPresenter.setLhUser(user);
									groupManager.setLhUser(user);
									String firstname="";
									String lastname = "";
									isadmin = user.getIsAdmin();
									firstname = user.getFirstname();
									lastname = user.getLastname();
									email = user.getEmail();
									subscriptionMode = user.getPeriod();
									newsFilterMode = user.getNewsFilterMode();
									System.out.println("SUBSCRIPTION PERIOD IS "+ subscriptionMode);
									if(isadmin==1){
										adminImage.setVisible(true);
										adminLink.setVisible(true);
										reviewFeedLink.setVisible(true);
										reviewFeedImage.setVisible(true);
									}else{
										adminImage.setVisible(false);
										adminLink.setVisible(false);
										reviewFeedLink.setVisible(false);
										reviewFeedImage.setVisible(false);
									}
									welcomelbl.setText("Logged in as "+firstname+" "+lastname);
									welcomelbl.setStylePrimaryName("welcomlabelNCMain");
									groupManager.initialize();
									createUI();
								}
								else{
									PopupPanel popupPanel=new PopupPanel(false, false);
									popupPanel.setWidget(new Label("You are not authorised to access this page."));
									popupPanel.setSize("100%", "100%");
									popupPanel.center();
									popupPanel.show();
								}
								}
						});
						
						
					}
				}
			}
		});
	}
	
	/**
	* creates the page UI
	*/
	private void createUI(){
		adminLink.addClickHandler(this);
		logoutLink.addClickHandler(this);
		reviewFeedLink.addClickHandler(this);
		
		 RootPanel.get().addDomHandler(new KeyDownHandler() {
				
				@Override
				public void onKeyDown(KeyDownEvent event) {
					int key = event.getNativeEvent().getKeyCode();
					keyVal=keyVal+key;
								
					if(!keyCombination.startsWith(keyVal))
						keyVal="";
				
					if(keyVal.equalsIgnoreCase(keyCombination)){
						if(event.isControlKeyDown()&&event.isAltKeyDown()&&event.isShiftKeyDown()&&key==69){
							PopupWidget popupWidget=new PopupWidget();
							//popupWidget.setMessage("This site is developed by");
							keyVal="";
						}else{
							keyVal="";
						}
					}							
				}
			},KeyDownEvent.getType());
		
		reviewFeedLink.setStylePrimaryName("NChyperlinkStyle");
		adminLink.addStyleName("NChyperlinkStyle");
		logoutLink.addStyleName("NChyperlinkStyle");
		reviewFeedLink.setTitle("Preview Feed");
		adminLink.setTitle("Manage tags,news etc.");
		logoutLink.setTitle("Logout from the NewsCatalyst");
		linksPanel.clear();
		linksPanel.add(welcomelbl);
		linksPanel.add(new Image("images/verticalSeparator.JPG",0,0,6,13));
		linksPanel.add(reviewFeedImage);
		linksPanel.add(reviewFeedLink);
		linksPanel.add(adminImage);
		linksPanel.add(adminLink);
		linksPanel.add(logoutImage);
		linksPanel.add(logoutLink);
		linksPanel.setSpacing(3);
		
		if(clientLogo!=null){
			imNewsCatalyst = new Image(clientLogo);
		
		//	imNewsCatalyst.setStylePrimaryName("siloLogo");
		}
		else
			imNewsCatalyst = new Image("images/marketscapeLogoNewscatalyst.png");
		
	/*	HorizontalPanel logoImageContainer = new HorizontalPanel();
		imNewsCatalyst.setStylePrimaryName("siloLogo");
		logoImageContainer.setStylePrimaryName("siloLogoImage");
		logoImageContainer.add(imNewsCatalyst);
		*/
	
		imNewsCatalyst.setVisible(false);
		imNewsCatalyst.addLoadHandler(new LoadHandler() {
			
			@Override
			public void onLoad(LoadEvent event) {
				  Element element = (Element) event.getRelativeElement();
		            if (element == imNewsCatalyst.getElement()) {
		                int originalHeight = imNewsCatalyst.getOffsetHeight();
		                int originalWidth = imNewsCatalyst.getOffsetWidth();
		                if (originalHeight > originalWidth) {
		                	imNewsCatalyst.setStylePrimaryName("siloLogoHeight");
		                } else {
		                	imNewsCatalyst.setStylePrimaryName("siloLogoWidth");
		                }
		                imNewsCatalyst.setVisible(true);
		            }
			}
		});
		
	
		header.add(imNewsCatalyst);
	//	header.add(logoImageContainer);
		header.setCellHorizontalAlignment(imNewsCatalyst, HasHorizontalAlignment.ALIGN_LEFT);
		header.add(linksPanel);
		header.setCellHorizontalAlignment(linksPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		header.setWidth("100%");
		
		headerPanel.add(header);
		headerPanel.setWidth("100%");
		
		basePanel.add(headerPanel);
		
		deckPanel.add(groupManager);
		deckPanel.add(resultPresenter);
		deckPanel.setAnimationEnabled(true);
		
		basePanel.add(deckPanel);
		basePanel.setCellVerticalAlignment(groupManager, HasVerticalAlignment.ALIGN_TOP);
		basePanel.setCellHorizontalAlignment(groupManager, HasHorizontalAlignment.ALIGN_LEFT);
		
		basePanel.add(footer);
		basePanel.setSpacing(10);
		
		basePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		Window.addResizeHandler(new ResizeHandler() {
		
			@Override
			public void onResize(ResizeEvent event) {
				calculateWidth();
				/*// TODO Auto-generated method stub
				int arg0 = event.getWidth();
				int arg1 = event.getHeight();
				int width = arg0 - 200;
				if (arg0 < 1255) {
		
				} else {
					groupManager.resetSize(width, 0);
				}*/
		
			}
		});
		Window.addWindowClosingHandler(new Window.ClosingHandler() {
			
			@Override
			public void onWindowClosing(ClosingEvent event) {
				if(logoutclick == 0 && manageclick == 0 && previewFeedClick ==0){
					if(tagSelectionChanged == true)
						event.setMessage("Your changes have not been saved. Press OK to exit without saving changes. Press Cancel and use the logout link to save your changes");
				}
				
			}
		});
		basePanel.setStylePrimaryName("NCMainBasePanel");
		calculateWidth();
		showDeckWidget(0);
	}
	
	public void calculateWidth(){
		int width = Window.getClientWidth()-50;
		basePanel.setPixelSize(width, 0);
	}
	
	/**
	* show the particular widget at the given index from the deck panel
	* @param index - of the widget to be shown
	*/
	public void showDeckWidget(int index){
		deckPanel.showWidget(index);
	}
	
	public LhUser getUserInformation() {
		return lhUser;
	}
	
	public void setUserInformation(LhUser userInformation) {
		this.lhUser = userInformation;
	}
	
	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		if (sender instanceof Anchor) {
			Anchor link = (Anchor) sender;
		
			if (link.getText().equals("logout")) {
				if (logoutclick == 0) {
					logoutclick = 1;
					GroupItemStore.getInstance().saveUserSelection();
					LhLogoutPage logout = new LhLogoutPage(lhUser);
					logout.removeFromSession(isadmin);
				}
			}
			if (link.getText().equals("Manage")) {
				manageclick = 1;
				GroupItemStore.getInstance().saveUserSelection();
				String urlQueryString = Window.Location.getQueryString();
				String url = GWT.getHostPageBaseURL() + "lhadmin.html"+urlQueryString;
				Window.open(url, "_self", null);
			}
			
			if (link.getText().equals("Review Feed")) {
				previewFeedClick =1;
				GroupItemStore.getInstance().saveUserSelection();
				String urlQueryString = Window.Location.getQueryString();
				String url = GWT.getHostPageBaseURL() + "ReviewFeed.html"+urlQueryString;
				Window.open(url, "_self", null);
			}
		}
	}

	public SearchResultPresenter getResultPresenter() {
		return resultPresenter;
	}

	public void setResultPresenter(SearchResultPresenter resultPresenter) {
		this.resultPresenter = resultPresenter;
	}

	/*@Override
	public boolean onEvent(SearchEvent evt) {
		int evttype = evt.getEventType();

		switch(evttype){
			case SearchEvent.SHOWGROUPS:{
				showDeckWidget(0);
				return true;
			}case SearchEvent.SHOWSEARCHRESULTS:{
				HashMap<String, String> map = (HashMap<String, String>)evt.getEventData();
				String searchText = map.get(SearchWidget.SEARCHEDTEXT);
				String dateSearchText = map.get(SearchWidget.SEARCHEDDATE);
				resultPresenter.performSearch(searchText, dateSearchText);
				showDeckWidget(1);
				return true;
			}
		}
		return false;
	}*/
}