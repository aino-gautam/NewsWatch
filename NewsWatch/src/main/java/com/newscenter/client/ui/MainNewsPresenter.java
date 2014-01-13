package com.newscenter.client.ui;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.newscenter.client.ItemStore;
import com.newscenter.client.NewsCenterMain;
import com.newscenter.client.NewsStore;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.NewsEvent;
import com.newscenter.client.events.NewsEventListener;
import com.newscenter.client.events.TagEvent;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.tags.TagItem;

public class MainNewsPresenter extends Composite implements NewsEventListener, ClickHandler{
	
	private VerticalPanel basePanel = new VerticalPanel();
	private VerticalPanel newsPanel = new VerticalPanel();
	private Label headerLabel = new Label();
	private HorizontalPanel labelPanel = new HorizontalPanel();
	private static HorizontalPanel loaderPanel = new HorizontalPanel();
	private static Image loadingImg = new Image("images/circle_loader.gif");
	private static Label loadingMsg = new Label();
	
	private NewsItemPresenter itempresenter;
	private NewsItemPresenter currentItemPresenter;
	
	private NewsItemList fullNewsItemList;
	private NewsItemList tagNewsItemList;
	private NewsItemList currentNewsItemList;
	
	private int pageSize;
	private Label backLabel = new Label("Back to latest stories");
	private HorizontalPanel backPanel = new HorizontalPanel();
	
	private PageCriteria pageCriteria;
	private PageCriteria fullModeCriteria = new PageCriteria();
	private PageCriteria singleModeCriteria = new PageCriteria();
	
	private int mode;
	private HashMap<Integer, NewsItemPresenter> itemPresenterMap = new HashMap<Integer, NewsItemPresenter>();
	
	private FlexTable linksPanel = new FlexTable();
	private Label lbOld = new Label("NEXT");
	private Label lbNew = new Label("PREVIOUS");
	public static final int FULLSELECTIONMODE = 1;
	public static final int SINGLESELECTIONMODE = 2;
	private int counter = 0;
	
	private HorizontalPanel headerLabelPanel = new HorizontalPanel();
	private HorizontalPanel olderlinkpanel = new HorizontalPanel();
	private HorizontalPanel newerlinkpanel = new HorizontalPanel();
	private HorizontalPanel pagingPanel = new HorizontalPanel();
	
	private boolean tagPresenterStatus = false;
	private Label subscribeLabel = new Label("Subscribe Newsletter");
	private Image separatorImg = new Image("images/verticalSeparator.JPG",0,0,6,13);
	private PopupPanel popup = new PopupPanel(true);
	private Image backImg = new Image("images/back_icon.png");
	//private Image subscribeImg = new Image("subscribe_icon.png");
	private Image subscribeImg = new Image("images/setting_small.gif");
	private Label unsubscribeLabel = new Label("Unsubscribe Newsletter");
	private Label changeModeLabel = new Label("Change Filter Criteria");
	//private Label settingsLabel = new Label("Newsletter Settings");
	private Label settingsLabel = new Label("SETTINGS");
	private int subscriptionPeriod;
	private int currentPage;
	private int currentTagNewsPage;
	
	private HashMap<Integer, Label> pageLabelMap = new HashMap<Integer, Label>();
	private HashMap<Integer, Label> tagPageLabelMap = new HashMap<Integer, Label>();
	private int lastVisiblePage;
	private TagItem currentAppliedTagNewsViewed;
	private int requestedPage;
	private int requestedTagNewsPage;
	private int maxlimit = 9;
	private int minlimit = 10;
	private int newsMode;
	public static final int OR = 0;
	public static final int AND = 1;
	private static int backLinkClicked = 0;
	private int subscriptionMode;
		
	public MainNewsPresenter(){
		//AppEventManager.getInstance().addNewsEventListener(this);
		headerLabel.setText("No News to display");
		headerLabel.setStylePrimaryName("headerLabels");
		headerLabelPanel.add(headerLabel);
		headerLabelPanel.setWidth("100%");
		labelPanel.add(headerLabelPanel);
		labelPanel.setStylePrimaryName("tagDisclosureHeader");
		labelPanel.setHeight("35px");
		labelPanel.setWidth("100%");

		Image nextim = new Image("images/arrow_more.gif",0,-1,16,16);
		Image previm = new Image("images/arrow_less.gif",0,2,16,16);
		olderlinkpanel.add(lbOld);
		olderlinkpanel.add(nextim);
		newerlinkpanel.add(previm);
		newerlinkpanel.add(lbNew);
		
		olderlinkpanel.setCellVerticalAlignment(lbOld, HasVerticalAlignment.ALIGN_MIDDLE);
		olderlinkpanel.setCellVerticalAlignment(nextim, HasVerticalAlignment.ALIGN_MIDDLE);
		newerlinkpanel.setCellVerticalAlignment(lbNew, HasVerticalAlignment.ALIGN_MIDDLE);
		newerlinkpanel.setCellVerticalAlignment(nextim, HasVerticalAlignment.ALIGN_MIDDLE);
		
		olderlinkpanel.setStylePrimaryName("imageBorder");
		newerlinkpanel.setStylePrimaryName("imageBorder");
		lbOld.setStylePrimaryName("oldlink");
		lbNew.setStylePrimaryName("newlink");
		lbOld.setTitle("View next page");
		lbNew.setTitle("View previous page");
		
		//lbOld.addClickListener(this);
		//lbNew.addClickListener(this);
		lbOld.addClickHandler((ClickHandler) this);
		lbNew.addClickHandler((ClickHandler) this);
		olderlinkpanel.setVisible(true);
		newerlinkpanel.setVisible(true);
		
		linksPanel.setCellSpacing(3);
		pagingPanel.add(newerlinkpanel);
		pagingPanel.add(linksPanel);
		pagingPanel.add(olderlinkpanel);
		
		pagingPanel.setCellHorizontalAlignment(newerlinkpanel, HasHorizontalAlignment.ALIGN_LEFT);
		pagingPanel.setCellVerticalAlignment(newerlinkpanel, HasVerticalAlignment.ALIGN_MIDDLE);
		pagingPanel.setCellHorizontalAlignment(linksPanel, HasHorizontalAlignment.ALIGN_LEFT);
		pagingPanel.setCellVerticalAlignment(linksPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		pagingPanel.setCellHorizontalAlignment(olderlinkpanel, HasHorizontalAlignment.ALIGN_LEFT);
		pagingPanel.setCellVerticalAlignment(olderlinkpanel, HasVerticalAlignment.ALIGN_MIDDLE);
		
		popup.setStylePrimaryName("newsletterPopup");
		popup.setAnimationEnabled(true);
		popup.setTitle("Click outside to close");
		
		/*subscribeLabel.addClickListener(this);
		unsubscribeLabel.addClickListener(this);
		changeModeLabel.addClickListener(this);
		settingsLabel.addClickListener(this);*/
		
		
		subscribeLabel.setStylePrimaryName("subscribeLabel");		
		unsubscribeLabel.setStylePrimaryName("subscribeLabel");
		changeModeLabel.setStylePrimaryName("subscribeLabel");
		settingsLabel.setStylePrimaryName("subscribeLabel");
		subscribeLabel.setTitle("Subscribe to Newsletter");
		unsubscribeLabel.setTitle("Unsubscribe to Newsletter");
		changeModeLabel.setTitle("Manage news filter criteria");
		settingsLabel.setTitle("Manage settings of NewsCatalyst");
		backPanel.add(backImg);
		backPanel.add(backLabel);
		backPanel.add(separatorImg);
		backPanel.add(subscribeImg);
		backPanel.add(settingsLabel);
		backImg.setVisible(false);
		backLabel.setVisible(false);
		backLabel.setTitle("View all news stories");
		separatorImg.setVisible(false);
		backPanel.setCellVerticalAlignment(settingsLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		backPanel.setCellVerticalAlignment(backImg, HasVerticalAlignment.ALIGN_MIDDLE);
		backPanel.setCellVerticalAlignment(backLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		backPanel.setCellVerticalAlignment(separatorImg, HasVerticalAlignment.ALIGN_MIDDLE);
		backPanel.setSpacing(3);
		newsPanel.add(labelPanel);
		newsPanel.add(backPanel);
		newsPanel.add(loaderPanel);
		newsPanel.add(new HTML("<br>"));
		newsPanel.setCellHorizontalAlignment(backPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		newsPanel.setCellHorizontalAlignment(loaderPanel, HasHorizontalAlignment.ALIGN_CENTER);
		newsPanel.setCellVerticalAlignment(loaderPanel, HasVerticalAlignment.ALIGN_MIDDLE);
		setLoadingMessage("Fetching News...");
		newsPanel.setWidth("100%");
		basePanel.add(newsPanel);
		basePanel.add(pagingPanel);
		newsPanel.setStylePrimaryName("newsPanel");
		pagingPanel.setStylePrimaryName("pagingPanel");
		basePanel.setSpacing(0);
		initWidget(basePanel);
	}
	
	private void createPopup(String msg, String cbtext1, String cbtext2, String linkclicked){
		popup.clear();
		VerticalPanel vp = new VerticalPanel();
		Label label = new Label(msg);
		CheckBox dailycb = new CheckBox(cbtext1);
		dailycb.setStylePrimaryName("popupLabel");
		CheckBox weeklycb = new CheckBox(cbtext2);
		weeklycb.setStylePrimaryName("popupLabel");
		label.setStylePrimaryName("popupLabel");
		dailycb.addClickHandler(this);
		weeklycb.addClickHandler(this);
		if(linkclicked.equals("Subscribe Newsletter")){
			
			vp.add(label);
			vp.add(dailycb);
			vp.add(weeklycb);
		}
		else if(linkclicked.equals("Change Filter Criteria")){
			if(newsMode == this.OR){
				vp.add(label);
				vp.add(weeklycb);
			}
			if(newsMode == this.AND){
				vp.add(label);
				vp.add(dailycb);
			}
			
		vp.setSpacing(3);
		popup.add(vp);
		}
	}
	
	private Label createPagingLabel(String text){
		Label pageno = new Label(text);
		pageno.setStylePrimaryName("pageNoLabel");
		pageno.addClickHandler(this);
		
		if(getMode() == FULLSELECTIONMODE){
			if(getCurrentPage() == Integer.parseInt(text))
				pageno.addStyleName("selectedPageNoLabel");
			pageLabelMap.put(Integer.parseInt(text), pageno);
		}
		else if(getMode() == SINGLESELECTIONMODE){
			if(getCurrentTagNewsPage() == Integer.parseInt(text))
				pageno.addStyleName("selectedPageNoLabel");
			tagPageLabelMap.put(Integer.parseInt(text), pageno);
		}
		
		return pageno;
	}
	
	private void addInPagingPanel(){
		int totalpages = 0;
		int page = 0;
		if(getMode() == FULLSELECTIONMODE){
			totalpages = getCurrentNewsItemList().getTotalPages();
			page = getCurrentPage();
		}
		else if(getMode() == SINGLESELECTIONMODE){
			totalpages = getTagNewsItemList().getTotalPages();
			page = getCurrentTagNewsPage();
		}
		try{
		if(page <= 11){
			int lastpage = getLastVisiblePage() + 1;
			if(lastpage <= totalpages){
				linksPanel.setWidget(0, lastpage, createPagingLabel(String.valueOf(lastpage)));
				setLastVisiblePage(lastpage);
				if(page == totalpages)
					olderlinkpanel.setVisible(false);
			}
		}
		else{
			int lastpage = getLastVisiblePage() + 1;
			linksPanel.removeCell(0, 1);
			int cellcnt = linksPanel.getCellCount(0);
			if(lastpage <= totalpages){
				linksPanel.setWidget(0, cellcnt, createPagingLabel(String.valueOf(lastpage)));
				setLastVisiblePage(lastpage);
				if(lastpage == totalpages)
					olderlinkpanel.setVisible(false);
			}
		}
		}
		catch(Exception ex){
			GWT.getUncaughtExceptionHandler().onUncaughtException(ex);
		}
	}
	
	private void addInPagingPanel(int page){
		try{
		  if(page <= 11){
			int lastpage = getLastVisiblePage() + 1;
			int totalpages = 0;
			if(getMode() == FULLSELECTIONMODE)
				totalpages = getCurrentNewsItemList().getTotalPages();
			else if(getMode() == SINGLESELECTIONMODE)
				totalpages = getTagNewsItemList().getTotalPages();
			
			if(lastpage <= totalpages){
				linksPanel.setWidget(0, lastpage, createPagingLabel(String.valueOf(lastpage)));
				setLastVisiblePage(lastpage);
				if(page == totalpages)
					olderlinkpanel.setVisible(false);
			}
		  }
		  else{
			int lastpage = getLastVisiblePage() + 1;
			int totalpages = 0;
			if(getMode() == FULLSELECTIONMODE)
				totalpages = getCurrentNewsItemList().getTotalPages();
			else if(getMode() == SINGLESELECTIONMODE)
				totalpages = getTagNewsItemList().getTotalPages();
			linksPanel.removeCell(0, 1);
			int cellcnt = linksPanel.getCellCount(0);
			if(lastpage <= totalpages){
				linksPanel.setWidget(0, cellcnt, createPagingLabel(String.valueOf(lastpage)));
				setLastVisiblePage(lastpage);
				if(page == totalpages)
					olderlinkpanel.setVisible(false);
			}
		  }
		}
	 catch(Exception ex){
		 GWT.getUncaughtExceptionHandler().onUncaughtException(ex);
	 }
	}
	
	private void removeFromPagingPanel(int page){
		if(page <= 11){
			int lastpage = getLastVisiblePage() - 1;
			int lvp = getLastVisiblePage();
			if(getMode() == FULLSELECTIONMODE){
				if(lvp > getRequestedPage() + maxlimit){
					int cellcnt = linksPanel.getCellCount(0);
					linksPanel.removeCell(0, cellcnt - 1);
					setLastVisiblePage(lastpage);
				}
			}
			else if(getMode() == SINGLESELECTIONMODE){
				if(lvp > getRequestedTagNewsPage() + maxlimit){
					int cellcnt = linksPanel.getCellCount(0);
					linksPanel.removeCell(0, cellcnt - 1);
					setLastVisiblePage(lastpage);
				}
			}
			if(page == 1)
				newerlinkpanel.setVisible(false);
		}
		else{
			int no = (page - 11);
			int lastpage = getLastVisiblePage() - 1;
			int lvp = getLastVisiblePage();
			if(getMode() == FULLSELECTIONMODE){
				if(lvp > getRequestedPage() + maxlimit){
					int cellcnt = linksPanel.getCellCount(0);
					linksPanel.removeCell(0, cellcnt-1);
					setLastVisiblePage(lastpage);
				}
			}
			else if(getMode() == SINGLESELECTIONMODE){
				if(lvp > getRequestedTagNewsPage() + maxlimit){
					int cellcnt = linksPanel.getCellCount(0);
					linksPanel.removeCell(0, cellcnt-1);
					setLastVisiblePage(lastpage);
				}
			}
			linksPanel.insertCell(0, 1);
			linksPanel.setWidget(0, 1, createPagingLabel(String.valueOf(no)));
		}
	}
	
	/**
	 * This method takes a NewsItemList and based on the pagesize and start index provides a sublist to the NewsItemPresenter to display.
	 * @param list - a NewsItemList of NewsItems
	 */
	public void initialize(NewsItemList list){
		if(fullModeCriteria.getPageSize() == -1 && fullModeCriteria.getStartIndex() == -1)
		{
			fullModeCriteria.setPageSize(getPageCriteria().getPageSize());
			fullModeCriteria.setStartIndex(getPageCriteria().getStartIndex());
		}
		if(singleModeCriteria.getPageSize() == -1 && singleModeCriteria.getStartIndex() == -1){
			singleModeCriteria.setPageSize(getPageCriteria().getPageSize());
			singleModeCriteria.setStartIndex(getPageCriteria().getStartIndex());
		}
		if(getCurrentItemPresenter()!=null && getMode() == FULLSELECTIONMODE){
			newsPanel.remove(getCurrentItemPresenter());
		}
		if(counter == 0 || getMode() == FULLSELECTIONMODE){
		itempresenter = new NewsItemPresenter();		
		int start = getPageCriteria().getStartIndex();
		int size =  getPageCriteria().getPageSize();
		setMode(FULLSELECTIONMODE);
		if(linksPanel.getRowCount() == 1)
			linksPanel.removeRow(0);
		linksPanel.setVisible(true);
		setCurrentPage(1);
		if(list.getTotalPages() > 10){
			for(int i = 1; i<=10; i++){
				linksPanel.setWidget(0, i, createPagingLabel(String.valueOf(i)));
				setLastVisiblePage(i);
			}
			olderlinkpanel.setVisible(true);
			newerlinkpanel.setVisible(false);
		}
		else{
			for(int i = 1; i<=list.getTotalPages(); i++){
				linksPanel.setWidget(0, i, createPagingLabel(String.valueOf(i)));
				setLastVisiblePage(i);
			}
			if(list.getTotalPages() == 1)
				olderlinkpanel.setVisible(false);
			else
				olderlinkpanel.setVisible(true);
			newerlinkpanel.setVisible(false);
		}
		NewsItemList sublist = new NewsItemList();
		if(list.size() != 0){
			while(start < size){
				if(start < list.size()){
				sublist.add(list.get(start));
				start++;
			    }
				else{
					start++;
				}
			}
			headerLabel.setText("Total: "+ list.getNumItems() +" stories");
			itempresenter.populateNewsItems(sublist);
		}
		else{
			headerLabel.setText("No news to display");
			newerlinkpanel.setVisible(false);
			linksPanel.setVisible(false);
			olderlinkpanel.setVisible(false);
		}
		setCurrentNewsItemList(list);
		newsPanel.add(itempresenter);
		itemPresenterMap.put(FULLSELECTIONMODE,itempresenter);
		setCurrentItemPresenter(itempresenter);
		disableLoadingMessage();
		counter++;
		}
		else{
			setCurrentNewsItemList(list);
			disableLoadingMessage();
		}
		RootPanel.get().setHeight("100%");
	}

	/**
	 * Called when newspresenter needs to be refreshed based on minimizing or maximizing of tag presenter
	 * @param list
	 */
	public void refresh(NewsItemList list){
		if(getMode() == FULLSELECTIONMODE){
			if(getCurrentItemPresenter()!=null){
				newsPanel.remove(getCurrentItemPresenter());
			}
			itempresenter = new NewsItemPresenter();		
			int start = 0;
			int size = 0;
			start = getFullModeCriteria().getStartIndex();
			size = getFullModeCriteria().getPageSize();
			NewsItemList sublist = new NewsItemList();
			if(list.size() > size){
				olderlinkpanel.setVisible(true);
				newerlinkpanel.setVisible(false);
			}
			else{
				olderlinkpanel.setVisible(false);
				newerlinkpanel.setVisible(false);
			}
			if(list.size() != 0){
				while(start < size){
					if(start < list.size()){
					sublist.add(list.get(start));
					start++;
					}
					else{
						start++;
					}
				}
				headerLabel.setText("Total: "+ list.size() +" stories");
				getFullModeCriteria().setStartIndex(start);
				itempresenter.populateNewsItems(sublist);
			}
			setFullNewsItemList(list);
			newsPanel.add(itempresenter);
			itemPresenterMap.put(FULLSELECTIONMODE,itempresenter);
			setCurrentItemPresenter(itempresenter);
			disableLoadingMessage();
			setMode(FULLSELECTIONMODE);
			counter++;
		}
		
		if(counter == 0 || getMode() == SINGLESELECTIONMODE){
			if(getCurrentItemPresenter()!=null){
			 newsPanel.remove(getCurrentItemPresenter());
		    }
			itempresenter = new NewsItemPresenter();		
			int start = 0;
			int size = 0;
			if(!isTagPresenterStatus()){
				start = getPageCriteria().getStartIndex();
				size =  getPageCriteria().getPageSize();
			}
			else {
				start = getSingleModeCriteria().getStartIndex();
				size = getSingleModeCriteria().getPageSize();
			}
			NewsItemList sublist = new NewsItemList();
			if(list.size() > size){
				olderlinkpanel.setVisible(true);
				newerlinkpanel.setVisible(false);
			}
			else{
				olderlinkpanel.setVisible(false);
				newerlinkpanel.setVisible(false);
			}
			if(list.size() != 0){
				while(start < size){
					if(start < list.size()){
					sublist.add(list.get(start));
					start++;
					}
					else{
						start++;
					}
				}
				headerLabel.setText("Total: "+ list.size() +" stories");
				getSingleModeCriteria().setStartIndex(start);
				itempresenter.populateNewsItems(sublist);
			}
			else{
				headerLabel.setText("No news to display");
			}
			setTagNewsItemList(list);
			newsPanel.add(itempresenter);
			itemPresenterMap.put(SINGLESELECTIONMODE,itempresenter);
			setCurrentItemPresenter(itempresenter);
			disableLoadingMessage();
			setMode(SINGLESELECTIONMODE);
			counter++;
			}
		else{
			setFullNewsItemList(list);
			disableLoadingMessage();
		}
	}

	public void setNewsPresenterWidth(int width, int height){
		basePanel.setPixelSize(width, height);
	}

	public void resetSize(int width, int height){
		basePanel.setPixelSize(width, height);
		System.out.println("On resize newspresenter width= " + width);
	}
	
	/**
	 * Enables the loading sign
	 * @param message - the text to be displayed as a message while loading
	 */
	public static void setLoadingMessage(String message){
		loadingMsg.setText(message);
		loaderPanel.add(loadingImg);
		loaderPanel.add(loadingMsg);
		loadingMsg.setStylePrimaryName("fetchingLbl");
		loadingImg.setStylePrimaryName("fetchingLbl");
		loaderPanel.setHeight("30px");
		loaderPanel.setSpacing(5);
		loaderPanel.setVisible(true);
	}
	
	/**
	 * Disables the loading image & message
	 */
	public static void disableLoadingMessage(){
		loaderPanel.setVisible(false);
	}
	
	public boolean onEvent(NewsEvent evt) {
		int evttype = evt.getEventType();
		switch(evttype){
		case (NewsEvent.NEWSARRIVED):
		{
			if(getMode() == SINGLESELECTIONMODE){
				fullModeCriteria.setStartIndex(0);
				setCurrentPage(1);
				setLastVisiblePage(1);
			}
			initialize((NewsItemList)evt.getEventData());
			return true;
		}
		case (NewsEvent.PAGEAVAILABLE):
		{
			NewsItemList newslist = (NewsItemList)evt.getEventData();
			if(getMode() == SINGLESELECTIONMODE){
				setMode(FULLSELECTIONMODE);
				if(linksPanel.getRowCount() == 1)
					linksPanel.removeRow(0);
				//pageLabelMap.clear();
				if(newslist.getTotalPages() > 10){
					int currentpg  = getCurrentPage();
					int col = 1;
					if(currentpg > 10){
						for(int i = (currentpg-minlimit); i<=(currentpg+maxlimit); i++){
							if(i <= newslist.getTotalPages()){
								linksPanel.setWidget(0, col, createPagingLabel(String.valueOf(i)));
								setLastVisiblePage(i);
								col++;
							}
						}
					}
					else{
						for(int i = 1; i<=10; i++){
							linksPanel.setWidget(0, i, createPagingLabel(String.valueOf(i)));
							setLastVisiblePage(i);
						}
						if(currentpg != 1){
							int pg = minlimit - getCurrentPage();
							while(getLastVisiblePage() < getCurrentPage()+maxlimit){
								addInPagingPanel(pg+1);
								pg++;
							}
						}
					}
					
					olderlinkpanel.setVisible(true);
					newerlinkpanel.setVisible(false);
				}
				else{
					for(int i = 1; i<=getCurrentNewsItemList().getTotalPages(); i++){
						linksPanel.setWidget(0, i, createPagingLabel(String.valueOf(i)));
						setLastVisiblePage(i);
					}
					if(newslist.getTotalPages() == 1 || newslist.size() == 0)
						olderlinkpanel.setVisible(false);
					else
						olderlinkpanel.setVisible(true);
					newerlinkpanel.setVisible(false);
				}
			}

			int curpage = getCurrentPage();
			Label lbl = (Label)pageLabelMap.get(curpage);
			if(lbl != null)
				lbl.addStyleName("selectedPageNoLabel");
			if(curpage != 1)
				newerlinkpanel.setVisible(true);
			
			int curtagpage = getCurrentTagNewsPage();
			if(curtagpage != 0){
				Label lb = (Label)tagPageLabelMap.get(curtagpage);
				lb.removeStyleName("selectedPageNoLabel");
			}
			if(currentPage == 1)
				newerlinkpanel.setVisible(false);
			if(currentPage == newslist.getTotalPages())
				olderlinkpanel.setVisible(false);
			
			newsPanel.remove(getCurrentItemPresenter());
			NewsItemPresenter newsitempresenter = new NewsItemPresenter();
			newsitempresenter.populateNewsItems(newslist);
			setMode(FULLSELECTIONMODE);
			itemPresenterMap.put(getMode(),newsitempresenter);
			setCurrentItemPresenter(newsitempresenter);
			setCurrentNewsItemList(newslist);
			newsPanel.add(newsitempresenter);
			if(newslist.size()!=0)
				headerLabel.setText("Total: "+ newslist.getNumItems() +" stories");
			else
				headerLabel.setText("No news to display");
			backLinkClicked = 0;
			disableLoadingMessage();
			return true;
		}
		case(NewsEvent.ANDNEWSAVAILABLE):{
			try{		
				NewsItemList newslist = (NewsItemList)evt.getEventData();
				setMode(FULLSELECTIONMODE);
				if(linksPanel.getRowCount() == 1)
					linksPanel.removeRow(0);
				pageLabelMap.clear();
				if(newslist.size() != 0 || getCurrentPage() == 0)
					setCurrentPage(1);
				
				if(newslist.getTotalPages() > 10){
					for(int i = 1; i<=10; i++){
						linksPanel.setWidget(0, i, createPagingLabel(String.valueOf(i)));
						setLastVisiblePage(i);
					}
					olderlinkpanel.setVisible(true);
					newerlinkpanel.setVisible(false);
				}
				else{
					for(int i = 1; i<=newslist.getTotalPages(); i++){
						linksPanel.setWidget(0, i, createPagingLabel(String.valueOf(i)));
						setLastVisiblePage(i);
					}
					if(newslist.getTotalPages() <= 1)
						olderlinkpanel.setVisible(false);
					else
						olderlinkpanel.setVisible(true);
					newerlinkpanel.setVisible(false);
				}
				newsPanel.remove(getCurrentItemPresenter());
				NewsItemPresenter newsitempresenter = new NewsItemPresenter();
				newsitempresenter.populateNewsItems(newslist);
				itemPresenterMap.put(FULLSELECTIONMODE,newsitempresenter);
				setCurrentItemPresenter(newsitempresenter);
				newsPanel.add(newsitempresenter);
				setCurrentNewsItemList(newslist);
				if(newslist.size()!=0)
					headerLabel.setText("Total: "+ newslist.getNumItems() +" stories");
				else
					headerLabel.setText("No news to display");
				disableLoadingMessage();
				return true;
		      }
		      catch(Exception ex){
		    	  GWT.getUncaughtExceptionHandler().onUncaughtException(ex);
		      }
			return true;
		}
		case(NewsEvent.TAGNEWSARRIVED):{
	      try{		
			NewsItemList newslist = (NewsItemList)evt.getEventData();
			setMode(SINGLESELECTIONMODE);
			linksPanel.removeRow(0);
			tagPageLabelMap.clear();
			setCurrentTagNewsPage(1);
			if(newslist.getTotalPages() > 10){
				for(int i = 1; i<=10; i++){
					linksPanel.setWidget(0, i, createPagingLabel(String.valueOf(i)));
					setLastVisiblePage(i);
				}
				olderlinkpanel.setVisible(true);
				newerlinkpanel.setVisible(false);
			}
			else{
				for(int i = 1; i<=newslist.getTotalPages(); i++){
					linksPanel.setWidget(0, i, createPagingLabel(String.valueOf(i)));
					setLastVisiblePage(i);
				}
				if(newslist.getTotalPages() == 1)
					olderlinkpanel.setVisible(false);
				else
					olderlinkpanel.setVisible(true);
				newerlinkpanel.setVisible(false);
			}
			newsPanel.remove(getCurrentItemPresenter());
			NewsItemPresenter newsitempresenter = new NewsItemPresenter();
			newsitempresenter.populateNewsItems(newslist);
			if(!backLabel.isVisible()){
				backLabel.addClickHandler(this);
				backLabel.setStylePrimaryName("backLabel");
				backLabel.setVisible(true);
				backImg.setVisible(true);
				separatorImg.setVisible(true);
			}
			itemPresenterMap.put(SINGLESELECTIONMODE,newsitempresenter);
			setCurrentItemPresenter(newsitempresenter);
			newsPanel.add(newsitempresenter);
			setTagNewsItemList(newslist);
			disableLoadingMessage();
			return true;
	      }
	      catch(Exception ex){
	    	  GWT.getUncaughtExceptionHandler().onUncaughtException(ex);
	      }
		}
		case (NewsEvent.NEWSAVAILABLE):{
		  try{	
			NewsItemList newslist = (NewsItemList)evt.getEventData();
			int curpage = getCurrentTagNewsPage();
			Label lbl = (Label)tagPageLabelMap.get(curpage);
			lbl.addStyleName("selectedPageNoLabel");
			
			int page = getCurrentPage();
			if(page != 0){
				Label lb = (Label)pageLabelMap.get(page);
				lb.removeStyleName("selectedPageNoLabel");
			}
			
			if(currentTagNewsPage == 1)
				newerlinkpanel.setVisible(false);
			else if(currentTagNewsPage == getTagNewsItemList().getTotalPages())
				olderlinkpanel.setVisible(false);
			
			newsPanel.remove(getCurrentItemPresenter());
			NewsItemPresenter newsitempresenter = new NewsItemPresenter();
			newsitempresenter.populateNewsItems(newslist);
			setMode(SINGLESELECTIONMODE);
			itemPresenterMap.put(getMode(),newsitempresenter);
			setCurrentItemPresenter(newsitempresenter);
			newsPanel.add(newsitempresenter);
			setTagNewsItemList(newslist);
			disableLoadingMessage(); 
			return true;
		  }
		  catch(Exception ex){
			  GWT.getUncaughtExceptionHandler().onUncaughtException(ex);
		  }
		}
		case (NewsEvent.NEWSTAGSELECTED):
		{
			TagItem tag = (TagItem)evt.getEventData();
			setCurrentAppliedTagNewsViewed(tag);
			headerLabel.setText("Stories for: "+ tag.getTagName());
			singleModeCriteria.setPageSize(15);
			singleModeCriteria.setStartIndex(0);
			ItemStore.getInstance().getNews(tag, getSingleModeCriteria());
			return true;
		}
		case (NewsEvent.NEWSDELETED):{
			NewsItemList newslist = (NewsItemList)evt.getEventData();
			if(newslist== null){
				disableLoadingMessage();
				newsPanel.remove(getCurrentItemPresenter());
				headerLabel.setText("No News to display");
				linksPanel.setVisible(false);
				separatorImg.setVisible(false);
				backImg.setVisible(false);
				backLabel.setVisible(false);
				setMode(FULLSELECTIONMODE);
				fullModeCriteria.setStartIndex(0);
				if(olderlinkpanel.isVisible() || newerlinkpanel.isVisible()){
					olderlinkpanel.setVisible(false);
					newerlinkpanel.setVisible(false);
				}
				if(evt.getSource() instanceof ItemTagLabel){
					ItemStore.getInstance().setCurrentNewsCache(null);
					NewsStore.getInstance().setGlobalNewsItemList(null);
					NewsStore.getInstance().setGlobalNewsCache(null);
				    ItemStore.getInstance().clearSelection();
					TagEvent tagevt = new TagEvent(this,TagEvent.CLEARTAGS,null);
					AppEventManager.getInstance().fireEvent(tagevt);
				}
			}
			else{
				initialize(newslist);
			}
			return true;
		}
		case (NewsEvent.REFRESHNEWS):{
			boolean bool = (Boolean)evt.getEventData();
			if(bool == true){
				setTagPresenterStatus(true);
				if(getMode() == FULLSELECTIONMODE){
					int currentindex = getFullModeCriteria().getStartIndex();
					int pagesize = getFullModeCriteria().getPageSize();
					int value = (currentindex - pagesize);
					getFullModeCriteria().setPageSize(getPageCriteria().getMaxPageSize());
					getFullModeCriteria().setStartIndex(value);
					refresh(getFullNewsItemList());
				}
				else if(getMode() == SINGLESELECTIONMODE){
					int currentindex = getSingleModeCriteria().getStartIndex();
					int pagesize = getSingleModeCriteria().getPageSize();
					int value = (currentindex - pagesize);
					getSingleModeCriteria().setPageSize(getPageCriteria().getMaxPageSize());
					getSingleModeCriteria().setStartIndex(value);
					refresh(getTagNewsItemList());
				}
			}
			else{
				setTagPresenterStatus(false);
				if(getMode() == FULLSELECTIONMODE){
					int currentindex = getFullModeCriteria().getStartIndex();
					int pagesize = getPageCriteria().getMinPageSize();
					int value = (currentindex - pagesize);
					getFullModeCriteria().setPageSize(getPageCriteria().getMinPageSize());;
					getFullModeCriteria().setStartIndex(value);
					refresh(getFullNewsItemList());
				}
				else if(getMode() == SINGLESELECTIONMODE){
					
				}
				setPageSize(getPageCriteria().getMinPageSize());
			}
			return true;
		}
		}
		return false;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	

	public void saveNewsletterPreference(String choice){
		NewsStore.getInstance().saveNewsletterPreference(choice, popup);
		
	}
	public void saveNewsFilterModePreference(String choice){
		NewsStore.getInstance().saveNewsFilterModPreference(choice,getNewsMode(), popup);
	}
	public PageCriteria getPageCriteria() {
		return pageCriteria;
	}

	public void setPageCriteria(PageCriteria pageCriteria) {
		this.pageCriteria = pageCriteria;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public NewsItemList getFullNewsItemList() {
		return fullNewsItemList;
	}

	public void setFullNewsItemList(NewsItemList newsItemList) {
		this.fullNewsItemList = newsItemList;
	}

	public HashMap<Integer, NewsItemPresenter> getItemPresenterMap() {
		return itemPresenterMap;
	}

	public void setItemPresenterMap(
			HashMap<Integer, NewsItemPresenter> itemPresenterMap) {
		this.itemPresenterMap = itemPresenterMap;
	}

	public NewsItemPresenter getCurrentItemPresenter() {
		return currentItemPresenter;
	}

	public void setCurrentItemPresenter(NewsItemPresenter currentItemPresenter) {
		this.currentItemPresenter = currentItemPresenter;
	}

	public PageCriteria getFullModeCriteria() {
		return fullModeCriteria;
	}

	public void setFullModeCriteria(PageCriteria fullModeCriteria) {
		this.fullModeCriteria = fullModeCriteria;
	}

	public PageCriteria getSingleModeCriteria() {
		return singleModeCriteria;
	}

	public void setSingleModeCriteria(PageCriteria singleModeCriteria) {
		this.singleModeCriteria = singleModeCriteria;
	}

	public NewsItemList getTagNewsItemList() {
		return tagNewsItemList;
	}

	public void setTagNewsItemList(NewsItemList tagNewsItemList) {
		this.tagNewsItemList = tagNewsItemList;
	}
	
	public boolean isTagPresenterStatus() {
		return tagPresenterStatus;
	}

	public void setTagPresenterStatus(boolean tagPresenterStatus) {
		this.tagPresenterStatus = tagPresenterStatus;
	}
	
	public void setSubscribeLabelText(String text){
		subscribeLabel.setText(text);
	}
	public int getSubscriptionPeriod() {
		return subscriptionPeriod;
	}

	public void setSubscriptionPeriod(int subscriptionPeriod) {
		this.subscriptionPeriod = subscriptionPeriod;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public NewsItemList getCurrentNewsItemList() {
		return currentNewsItemList;
	}

	public void setCurrentNewsItemList(NewsItemList currentNewsItemList) {
		this.currentNewsItemList = currentNewsItemList;
	}

	public int getLastVisiblePage() {
		return lastVisiblePage;
	}

	public void setLastVisiblePage(int lastVisiblePage) {
		this.lastVisiblePage = lastVisiblePage;
	}

	public TagItem getCurrentAppliedTagNewsViewed() {
		return currentAppliedTagNewsViewed;
	}

	public void setCurrentAppliedTagNewsViewed(TagItem currentAppliedTagNewsViewed) {
		this.currentAppliedTagNewsViewed = currentAppliedTagNewsViewed;
	}

	public int getCurrentTagNewsPage() {
		return currentTagNewsPage;
	}

	public void setCurrentTagNewsPage(int currentTagNewsPage) {
		this.currentTagNewsPage = currentTagNewsPage;
	}

	public int getRequestedPage() {
		return requestedPage;
	}

	public void setRequestedPage(int requestedPage) {
		this.requestedPage = requestedPage;
	}

	public int getRequestedTagNewsPage() {
		return requestedTagNewsPage;
	}

	public void setRequestedTagNewsPage(int requestedTagNewsPage) {
		this.requestedTagNewsPage = requestedTagNewsPage;
	}
	public int getNewsMode() {
		return newsMode;
	}

	public void setNewsMode(int newsMode) {
		this.newsMode = newsMode;
	}

	public int getSubscriptionMode() {
		return subscriptionMode;
	}

	public void setSubscriptionMode(int subscriptionMode) {
		this.subscriptionMode = subscriptionMode;
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			if(label.getText().equals("Back to latest stories")){
				if(backLinkClicked == 0){
					backLinkClicked = 1;
					setLoadingMessage("Refreshing...");
					backImg.setVisible(false);
					backLabel.setVisible(false);
					separatorImg.setVisible(false);
					NewsStore.getInstance().getNewsPage(getFullModeCriteria(), getNewsMode());
				}
			}
			else if(label.getText().equals("SETTINGS")){
				popup.clear();
				VerticalPanel vp = new VerticalPanel();
				vp.setSpacing(8);
				if(getSubscriptionMode() == 0)
				  vp.add(subscribeLabel);
				else
				 vp.add(unsubscribeLabel);
				
				vp.add(changeModeLabel);
				int left = arg0.getAbsoluteLeft() - 70;
		        int top = arg0.getAbsoluteTop() + 20;
		        popup.add(vp);
		        popup.setPopupPosition(left, top);
		        popup.show();     
			}
			else if(label.getText().equals("Subscribe Newsletter")){
			    createPopup("How would you like to receive the newsletter?","Daily","Weekly",label.getText()); 
			}
			else if(label.getText().equals("Unsubscribe Newsletter")){
				setSubscriptionMode(0);
		        saveNewsletterPreference("unsubscribe");
			}
			else if(label.getText().equals("Change Filter Criteria")){
				if(getNewsMode() == 0)
				 createPopup("Your current news filter mode is OR. Would you like to change to: ","OR","AND",label.getText()); 
				else
				 createPopup("Your current news filter mode is AND. Would you like to change to: ","OR","AND",label.getText());	
			}
			else if(label.getText().equals("NEXT")){
				NewsItemList sublist = new NewsItemList();
				if(getMode() == FULLSELECTIONMODE){
					newerlinkpanel.setVisible(true);
					int page = getCurrentPage();
					Label lbl = (Label)pageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentPage(page + 1);
					int start = getFullModeCriteria().getStartIndex();
					int size = getFullModeCriteria().getPageSize();
					getFullModeCriteria().setStartIndex(start + size);
					NewsStore.getInstance().getNewsPage(getFullModeCriteria(),getNewsMode());
					addInPagingPanel();
				}
				else if(getMode() == SINGLESELECTIONMODE){
					newerlinkpanel.setVisible(true);
					int page = getCurrentTagNewsPage();
					Label lbl = (Label)tagPageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentTagNewsPage(page + 1);
					int start = getSingleModeCriteria().getStartIndex();
					int size = getSingleModeCriteria().getPageSize();
					getSingleModeCriteria().setStartIndex(start + size);
					NewsStore.getInstance().getNews(getCurrentAppliedTagNewsViewed(), getSingleModeCriteria());
					addInPagingPanel();
				}
			}
			else if(label.getText().equals("PREVIOUS")){
				olderlinkpanel.setVisible(true);
				NewsItemList sublist = new NewsItemList();
				
				if(getMode() == FULLSELECTIONMODE){
					int page = getCurrentPage();
					Label lbl = (Label)pageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentPage(page - 1);
					setRequestedPage(page-1);
					int start = getFullModeCriteria().getStartIndex();
					int size = getFullModeCriteria().getPageSize();
					getFullModeCriteria().setStartIndex(start - size);
					NewsStore.getInstance().getNewsPage(getFullModeCriteria(),getNewsMode());
					removeFromPagingPanel(page);
				}
				else if(getMode() == SINGLESELECTIONMODE){
					int page = getCurrentTagNewsPage();
					Label lbl = (Label)tagPageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentTagNewsPage(page - 1);
					setRequestedTagNewsPage(page-1);
					int start = getSingleModeCriteria().getStartIndex();
					int size = getSingleModeCriteria().getPageSize();
					getSingleModeCriteria().setStartIndex(start - size);
					NewsStore.getInstance().getNews(getCurrentAppliedTagNewsViewed(), getSingleModeCriteria());
					removeFromPagingPanel(page);
				}
			}
			else {
				//int reqpage = Integer.parseInt(label.getText());
				if(getMode() == FULLSELECTIONMODE){
					int reqpage = Integer.parseInt(label.getText());
					setRequestedPage(reqpage);
					int page = getCurrentPage();
					int index = getFullModeCriteria().getStartIndex();
					int startind = (15 * reqpage) - 15;
					getFullModeCriteria().setStartIndex(startind);
					Label lbl = (Label)pageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentPage(reqpage);
					NewsStore.getInstance().getNewsPage(getFullModeCriteria(), getNewsMode());
					if(page < reqpage){
						newerlinkpanel.setVisible(true);
						for(int j = page+1; j<=reqpage;j++){
							addInPagingPanel(j);
						}
					}
					else{
						olderlinkpanel.setVisible(true);
						for(int j = page; j>reqpage; j--){
							removeFromPagingPanel(j);
						}
					}
				}
				else if(getMode() == SINGLESELECTIONMODE){
					int reqpage = Integer.parseInt(label.getText());
					setRequestedTagNewsPage(reqpage);
					int page = getCurrentTagNewsPage();
					int index = getSingleModeCriteria().getStartIndex();
					int startind = (15 * reqpage) - 15;
					getSingleModeCriteria().setStartIndex(startind);
					Label lbl = (Label)tagPageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentTagNewsPage(reqpage);
					NewsStore.getInstance().getNews(getCurrentAppliedTagNewsViewed(), getSingleModeCriteria());
					if(page < reqpage){
						newerlinkpanel.setVisible(true);
						for(int j = page+1; j<=reqpage;j++){
							addInPagingPanel(j);
						}
					}
					else{
						olderlinkpanel.setVisible(true);
						for(int j = page; j>reqpage; j--){
							removeFromPagingPanel(j);
						}
					}
				}
				
			}
		}	
		else if(arg0 instanceof CheckBox){
			CheckBox cb = (CheckBox)arg0;
			String name = cb.getText();
			if(name.equals("Daily")){
				setSubscriptionPeriod(1);
				setSubscriptionMode(1);
				saveNewsletterPreference(name);
			}
			else if(name.equals("Weekly")){
				setSubscriptionPeriod(2);
				setSubscriptionMode(1);
				saveNewsletterPreference(name);
			}
			else if(name.equals("OR")){
				setNewsMode(this.OR);
				getFullModeCriteria().setStartIndex(0);
				NewsCenterMain.getInstance().setNewsFilterMode(MainNewsPresenter.OR);
				NewsStore.getInstance().setNewsmode(MainNewsPresenter.OR);
				ItemStore.getInstance().setNewsmode(MainNewsPresenter.OR);
				if(backImg.isVisible())
					backImg.setVisible(false);
				if(backLabel.isVisible())
					backLabel.setVisible(false);
				if(separatorImg.isVisible())
					separatorImg.setVisible(false);
				saveNewsFilterModePreference(name);
			}
			else if(name.equals("AND")){
				setNewsMode(this.AND);
				getFullModeCriteria().setStartIndex(0);
				NewsCenterMain.getInstance().setNewsFilterMode(MainNewsPresenter.AND);
				NewsStore.getInstance().setNewsmode(MainNewsPresenter.AND);
				ItemStore.getInstance().setNewsmode(MainNewsPresenter.AND);
				if(backImg.isVisible())
					backImg.setVisible(false);
				if(backLabel.isVisible())
					backLabel.setVisible(false);
				if(separatorImg.isVisible())
					separatorImg.setVisible(false);
				saveNewsFilterModePreference(name);
			}
			
		}
		
	}

}
