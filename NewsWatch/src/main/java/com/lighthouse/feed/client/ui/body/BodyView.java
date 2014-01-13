package com.lighthouse.feed.client.ui.body;


import java.util.ArrayList;
import com.appUtils.client.ProgressIndicatorWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.lighthouse.feed.client.domain.Feed;
import com.lighthouse.feed.client.service.FeedService;
import com.lighthouse.feed.client.service.FeedServiceAsync;
import com.lighthouse.feed.client.ui.LhFeedNewsItemPresenter;
import com.lighthouse.feed.client.ui.ReverseCompositeView;
import com.lighthouse.feed.client.ui.body.IBodyView.IBodyPresenter;
import com.newscenter.client.news.NewsItemList;

/**
 * This class for representing the list of the news items on the body part 
 * @author kiran@ensarm.com
 *
 */
@Singleton
public class BodyView extends ReverseCompositeView<IBodyPresenter> implements IBodyView, ChangeHandler, ClickHandler{
	
    private VerticalPanel mainPanel = new VerticalPanel();
    private HorizontalPanel horizontalPanel = new HorizontalPanel();
    private HorizontalPanel hpLoaderPanel = new HorizontalPanel();
    
    private VerticalPanel feedListingPanel = new VerticalPanel();
    private VerticalPanel feedInfoPanel = new VerticalPanel();
    private HorizontalPanel feedPanel = new HorizontalPanel();
    private Label feedNameLb = new Label();
    private Label feedDescLb = new Label();
    private Anchor anchor=new Anchor("Click here to upload feed");
    private VerticalPanel verticalNewsPanel = new VerticalPanel();

    private Button allNewsItemsBtn = new Button("Review all news");
    private ListBox feedList = new ListBox();

    private Label orLabel = new Label("OR");
	private LhFeedNewsItemPresenter itemPresenter;
///	private String url=null;
	private ProgressIndicatorWidget loader = new ProgressIndicatorWidget(true);
	//private HashMap<String, String> feednamevsurl;
	private ArrayList<Feed> siloFeedList;
	int ncId;
	String ncnameStr;
	private Label newsletterDeliveryLb = new Label();
	
    @Inject
	public BodyView(LhFeedNewsItemPresenter itemPresenter) {
    	this.itemPresenter = itemPresenter;
		createUi();
		initWidget(mainPanel);
	}
    
	private void createUi() {
		String ncIdStr=Window.Location.getParameter("NCID");
		ncnameStr=Window.Location.getParameter("ncName");
		ncId=Integer.parseInt(ncIdStr);
		getLastNewsletterDeliveryTime();
		createFeedListBox();
		
		//create feedlisting panel
		Label feedLabel = new Label("Choose a feed url from the list below to review items of the feed : ");
		feedLabel.setStylePrimaryName("labelfont");
		feedLabel.addStyleName("chooseFeedLabel");
		feedList.setStylePrimaryName("feedListBox");
		feedListingPanel.add(feedLabel);
		feedListingPanel.add(feedList);
		
		//create feed info panel
		FlexTable feedInfoFlex = new FlexTable();
		Label feedName = new Label("Feed Name: ");
		Label feedDesc = new Label("Description: ");
		feedName.setStylePrimaryName("feedInfoLabel");
		feedDesc.setStylePrimaryName("feedInfoLabel");
		feedName.addStyleName("feedInfoLabelText");
		feedDesc.addStyleName("feedInfoLabelText");
		
		feedInfoFlex.setWidget(0, 0, feedName);
		feedInfoFlex.setWidget(0, 2, feedNameLb);
		feedInfoFlex.setWidget(2, 0, feedDesc);
		feedInfoFlex.setWidget(2, 2, feedDescLb);
		
		feedInfoPanel.add(feedInfoFlex);
		feedNameLb.setStylePrimaryName("feedInfoLabel");
		feedDescLb.setStylePrimaryName("feedInfoLabel");
		feedInfoPanel.setVisible(false);
		
		feedPanel.add(feedListingPanel);
		feedPanel.add(feedInfoPanel);
		feedPanel.setCellVerticalAlignment(feedInfoPanel, HasVerticalAlignment.ALIGN_BOTTOM);
		DOM.setStyleAttribute(feedInfoPanel.getElement(), "marginLeft", "10px");
		mainPanel.add(feedPanel);
		
		mainPanel.add(orLabel);
		mainPanel.setCellHorizontalAlignment(orLabel, HasHorizontalAlignment.ALIGN_LEFT);
		orLabel.setStylePrimaryName("orLabel");
		
		allNewsItemsBtn.setWidth("100%");
		allNewsItemsBtn.addStyleName("reviewNewsButton");
		horizontalPanel.setStylePrimaryName("reviewNewsButtonPanel");
		//feedPanel.add(allNewsItemsBtn);
		allNewsItemsBtn.addClickHandler(this);
		horizontalPanel.add(allNewsItemsBtn);
		
		anchor.addClickHandler(this);
		horizontalPanel.add(anchor);
		horizontalPanel.setSpacing(10);
		mainPanel.add(horizontalPanel);
		mainPanel.add(hpLoaderPanel);
		mainPanel.setCellHorizontalAlignment(hpLoaderPanel, HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.add(newsletterDeliveryLb);
		mainPanel.setCellHorizontalAlignment(newsletterDeliveryLb, HasHorizontalAlignment.ALIGN_CENTER);
		newsletterDeliveryLb.setStylePrimaryName("newsletterSentLabel");
		mainPanel.add(verticalNewsPanel);
		mainPanel.setWidth("100%");
		
		verticalNewsPanel.setStylePrimaryName("verticalNewsPanel");
		verticalNewsPanel.setWidth("100%");
		Label noItemsLabel = new Label("No feed url selected to review news ");
		noItemsLabel.setStylePrimaryName("labelfont");
		verticalNewsPanel.add(noItemsLabel);
		mainPanel.setSpacing(5);
		feedList.addChangeHandler(this);
		
	}
	/**
	 * This method is responsible for the populating the list box of the feed
	 */
	private void createFeedListBox() {
		FeedServiceAsync feedService = GWT.create(FeedService.class);
		feedService.getFeedUrlForCatalyst(ncId, new AsyncCallback<ArrayList<Feed>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(ArrayList<Feed> result) {
				try{
					
					//feednamevsurl = result;
					siloFeedList = result;
					feedList.addItem("------- Please select a feed url -------");
					feedList.setSelectedIndex(0);
					for(Feed feed : result){
						feedList.addItem(feed.getFeedName()/*+"  ==>>  "+feed.getFeedUrl()*/);
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void getLastNewsletterDeliveryTime(){
		FeedServiceAsync feedService = GWT.create(FeedService.class);
		feedService.getLastEditorialNewsletterDelivery(ncId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(String result) {
				newsletterDeliveryLb.setText("The last editorial was sent out at : " + result);
			}
		});
	}
	
	private void getFeedItemsForReview() {
		String ncIdStr=Window.Location.getParameter("NCID");
		
		int industryId=Integer.parseInt(ncIdStr);
		hpLoaderPanel.add(loader);
		hpLoaderPanel.setCellHorizontalAlignment(loader,
				HasHorizontalAlignment.ALIGN_CENTER);
		hpLoaderPanel.setCellVerticalAlignment(loader,
				HasVerticalAlignment.ALIGN_MIDDLE);
		loader.setLoadingMessage("Loading News..This may take few minutes....");
		loader.enable();
		
		FeedServiceAsync feedService = GWT.create(FeedService.class);
		feedService.getFeedItemsForReview(industryId, new AsyncCallback<NewsItemList>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				verticalNewsPanel.clear();
				Label errorLabel = new Label(" Failed to load news items. Please try again later...");
				errorLabel.setStylePrimaryName("errorLabel");
				verticalNewsPanel.add(errorLabel);
				loader.disable();
			}

			@Override
			public void onSuccess(NewsItemList result) {
				verticalNewsPanel.clear();
				itemPresenter.populateNewsItems(result);
				verticalNewsPanel.add(itemPresenter);
				loader.disable();
				
			}
		});
		
	}
	
	@Override
	public void onChange(ChangeEvent event) {
		Widget sender =(Widget) event.getSource();
		if(sender.equals(feedList)){
			if(feedList.getSelectedIndex() == 0){
				feedList.setSelectedIndex(0);
			}else{
				String feedName=feedList.getItemText(feedList.getSelectedIndex());
			//	url=url.substring(url.indexOf("==>> ")+5);
				String feedUrl = "";
				String feedDescription = "";
				for(Feed feed : siloFeedList){
					if(feed.getFeedName().equals(feedName)){
						feedUrl = feed.getFeedUrl();
						feedDescription = feed.getFeedDescription();
					}
				}
				if(!feedInfoPanel.isVisible())
					feedInfoPanel.setVisible(true);
				feedNameLb.setText(feedName);
				feedDescLb.setText(feedDescription);
				
				hpLoaderPanel.add(loader);
				hpLoaderPanel.setCellHorizontalAlignment(loader,
						HasHorizontalAlignment.ALIGN_CENTER);
				hpLoaderPanel.setCellVerticalAlignment(loader,
						HasVerticalAlignment.ALIGN_MIDDLE);
				loader.setLoadingMessage("Loading news items for review....This may take few minutes....");
				loader.enable();
				
				FeedServiceAsync feedService = GWT.create(FeedService.class);
				feedService.getFeedSourceNewsItems(feedUrl,feedName,ncId, new AsyncCallback<NewsItemList>() {
					
					@Override
					public void onSuccess(NewsItemList result) {
						verticalNewsPanel.clear();
						itemPresenter.populateNewsItems(result);
						verticalNewsPanel.add(itemPresenter);
						loader.disable();
					}
					
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
						verticalNewsPanel.clear();
						Label errorLabel = new Label(" Failed to load news items. Please try again later...");
						errorLabel.setStylePrimaryName("errorLabel");
						verticalNewsPanel.add(errorLabel);
						loader.disable();
					}
				});
		}
		}
	}
	@Override
	public void onClick(ClickEvent event) {
		Widget sender =(Widget) event.getSource();
		if(sender instanceof Button){
			if(sender.equals(allNewsItemsBtn)){
				getFeedItemsForReview();
			}
		}
		if(event.getSource().equals(anchor)){
			String url=GWT.getModuleBaseURL()+"uploadReq?NCID="+Window.Location.getParameter("NCID")+"&ncName="+Window.Location.getParameter("ncName");
			
			//String url = "http://127.0.0.1:8888/com.lighthouse.feed.feed/uploadReq?NCID=1&ncName=ensarm";
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

			try {
			  Request request = builder.sendRequest(null, new RequestCallback() {
			    public void onError(Request request, Throwable exception) {
			      System.out.print("Error");
			    }

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					
					 HTML html=new HTML(response.getText());
					 PopupPanel popupPanel=new PopupPanel(true);
					 popupPanel.add(html);
					 popupPanel.show();
					 popupPanel.setPopupPosition(350, 200);
				}

			       
			  });
			} catch (RequestException e) {
			  // Couldn't connect to server        
			}
		}
	}
	
	
	
	
	
}
