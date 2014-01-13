package com.lighthouse.feed.client.ui.header;



import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import com.lighthouse.feed.client.service.FeedService;
import com.lighthouse.feed.client.service.FeedServiceAsync;
import com.lighthouse.feed.client.ui.ReverseCompositeView;
import com.lighthouse.feed.client.ui.header.IHeaderView.IHeaderPresenter;
/**
 * This class is responsible for the creating the header part of the review feed
 * @author kiran@ensarm.com
 *
 */
@Singleton
public class HeaderView extends ReverseCompositeView<IHeaderPresenter>
		implements IHeaderView, ClickHandler {

	private HorizontalPanel horizontalPanel = new HorizontalPanel();
	private HorizontalPanel linksPanel = new HorizontalPanel();
	private Image imNewsCatalyst;
	
	private Anchor adminLink = new Anchor("Manage");
	private Anchor newsCenterLink = new Anchor("NewsCenter");
	private Anchor logoutLink = new Anchor("logout");
	
	private Image adminImage = new Image("images/manage-key.gif");
	private Image newscenterHomeImage = new Image("images/feed/nchome.png");
	private Image logoutImage = new Image("images/log-out.gif");
	
	private Label welcomelbl = new Label();
	private String siloLogo =null; 

	public HeaderView() {
		initialize();
		initWidget(horizontalPanel);
	}

	public void setLoggedInUser(String firstname, String lastname){
		welcomelbl.setText("Logged in as "+firstname+" "+lastname);
		welcomelbl.setStylePrimaryName("welcomlabelNCMain");
	}
	
	private void initialize() {
		FeedServiceAsync serviceAsync =GWT.create(FeedService.class);
		serviceAsync.getSiloLogo(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				siloLogo = result;
				createUi();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	private void createUi() {

		adminLink.addStyleName("NChyperlinkStyle");
		logoutLink.addStyleName("NChyperlinkStyle");
		

		linksPanel.clear();
		linksPanel.add(welcomelbl);
		linksPanel.add(new Image("images/verticalSeparator.JPG",0,0,6,13));
		
		adminLink.setTitle("Manage tags,news etc.");
		linksPanel.add(adminImage);
		linksPanel.add(adminLink);
		
		newsCenterLink.setTitle("Go to main silo");
		linksPanel.add(newscenterHomeImage);
		linksPanel.add(newsCenterLink);
		linksPanel.setCellVerticalAlignment(newscenterHomeImage, HasVerticalAlignment.ALIGN_MIDDLE);
		DOM.setStyleAttribute(newscenterHomeImage.getElement(), "marginBottom", "2px");
		
		logoutLink.setTitle("logout from the application");
		linksPanel.add(logoutImage);
		linksPanel.add(logoutLink);
		
		linksPanel.setSpacing(3);
		adminLink.addClickHandler(this);
		newsCenterLink.addClickHandler(this);
		logoutLink.addClickHandler(this);
        if(siloLogo!=null){
        	imNewsCatalyst = new Image(siloLogo);
        }else{
        	imNewsCatalyst = new Image("images/marketscapeLogoNewscatalyst.png");
        }
        
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
		
		horizontalPanel.add(imNewsCatalyst);
		horizontalPanel.setCellHorizontalAlignment(imNewsCatalyst,
				HasHorizontalAlignment.ALIGN_LEFT);
		horizontalPanel.add(linksPanel);
		horizontalPanel.setCellHorizontalAlignment(linksPanel,
				HasHorizontalAlignment.ALIGN_RIGHT);
		horizontalPanel.setWidth("100%");

	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender = (Widget) event.getSource();
		if (sender instanceof Anchor) {
			if (sender.equals(adminLink)) {

				presenter.OnManageClick();
			} else if (sender.equals(newsCenterLink)) {
				presenter.OnNewsCenterClick();
			}else if(sender.equals(logoutLink)){
				presenter.OnLogoutClick();
				
			}
		}

	}

}
