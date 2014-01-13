package com.lighthouse.search.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import java.util.Comparator;
import com.appUtils.client.ProgressIndicatorWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.client.LhMain;
import com.lighthouse.main.newsitem.client.ui.LhNewsItemPresenter;

import com.lighthouse.search.client.domain.DateRange;
import com.lighthouse.search.client.domain.SearchResultList;
import com.lighthouse.search.client.service.SearchService;
import com.lighthouse.search.client.service.SearchServiceAsync;


import com.newscenter.client.NewsStore;

import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
import com.newscenter.client.ui.HorizontalFlowPanel;

public class SearchResultPresenter extends Composite implements ClickHandler {
	public static String SEARCHEDSTR;
	public static String DATESTR;
	
	private Label searchTextLbl = new Label();
	private Label noResults = new Label();
	private VerticalPanel tagCloudPresenenter = new VerticalPanel();
	private VerticalPanel resultDisplayer = new VerticalPanel();
	private HorizontalPanel horiPanel = new HorizontalPanel();

	/** Backround stuff */
	private VerticalPanel basePanel = new VerticalPanel();

	/** Loader */
	ProgressIndicatorWidget loader = new ProgressIndicatorWidget(true);
	ProgressIndicatorWidget tagCloudLoader = new ProgressIndicatorWidget(true);
	
	/** Tag cloud */
	private DockPanel dockPanel = new DockPanel();
	private Label tagCloudHeader = new Label("Tag Cloud");
	private HorizontalFlowPanel tagCloudHolder = new HorizontalFlowPanel();
	private VerticalPanel tagCloudConatiner = new VerticalPanel();

	/** Paging related stuff */
	private LhUser lhUser;
	private LhMain lhMainRef;
	//private VerticalPanel tagContainerAndSeparator = new VerticalPanel();
	//private Label separator = new Label();
	//private HorizontalPanel separator = new HorizontalPanel();
	public SearchResultPresenter() {
		createBackGround();
		
		initWidget(basePanel);

	}
	
	/**
	 * creates tag cloud ui
	 */
	private void createTagCloud(){
	//	separator.setText(" .");
		//separator.setHeight("2%");
	//	tagContainerAndSeparator.setWidth("100%");
		tagCloudPresenenter.setWidth("100%");
		tagCloudHolder.setWidth("275px");
		tagCloudHolder.setHeight("100%");
		tagCloudHeader.setStylePrimaryName("TagCloudHeaderLabel");
		tagCloudHeader.setWidth("100%");
		tagCloudConatiner.add(tagCloudHeader);
		tagCloudConatiner.add(tagCloudLoader);
		tagCloudConatiner.add(tagCloudHolder);
		tagCloudConatiner.setCellHorizontalAlignment(tagCloudHeader,
				HorizontalPanel.ALIGN_LEFT);
		tagCloudConatiner.setCellHorizontalAlignment(tagCloudHolder,
				HorizontalPanel.ALIGN_LEFT);
		tagCloudConatiner.setStylePrimaryName("tagCloudBorder");
		tagCloudPresenenter.setStylePrimaryName("tagCloudPresenenterSpacing");
		tagCloudPresenenter.add(tagCloudConatiner);
	//	tagContainerAndSeparator.add(separator);
	//	tagContainerAndSeparator.add(tagCloudPresenenter);
	}
	
	/**
	 * creates page ui
	 */
	public void createUI() {
		createTagCloud();

		horiPanel.clear();
//		Label backToGroups = new Label("BACK TO GROUPS");
		HorizontalPanel backToGroupPanel = new HorizontalPanel();
		Label backToGroups = new Label("BACK TO NEWS OVERVIEW");
		Image backImg = new Image("images/arrow_left1.png");
		backToGroups.setStylePrimaryName("backToGroups");
		backToGroups.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SearchWidget.count=0;
				
				resultDisplayer.clear();
				tagCloudHolder.clear();
				getLhMainRef().showDeckWidget(0);	
			
			
				//SearchEvent searchEvent = new SearchEvent(this, SearchEvent.SHOWGROUPS, null);
				//GroupEventManager.getInstance().fireEvent(searchEvent);
			}
		});

		searchTextLbl.setStylePrimaryName("SearchLabeltext");
		horiPanel.setWidth("100%");
		//horiPanel.setStylePrimaryName("SearchLabel");
		horiPanel.setStylePrimaryName("SearchHeaderLabelText");
		horiPanel.add(searchTextLbl);
	/*	horiPanel.add(backImg);
		horiPanel.add(backToGroups);
	*/	
		backToGroupPanel.add(backImg);
		backToGroupPanel.add(backToGroups);
		horiPanel.add(backToGroupPanel);
		horiPanel.setCellHorizontalAlignment(searchTextLbl,
				HasHorizontalAlignment.ALIGN_LEFT);
		/*horiPanel.setCellHorizontalAlignment(backImg,
				HasHorizontalAlignment.ALIGN_LEFT);

		horiPanel.setCellHorizontalAlignment(backToGroups,
				HasHorizontalAlignment.ALIGN_LEFT);
*/
		horiPanel.setCellHorizontalAlignment(backToGroupPanel,
				HasHorizontalAlignment.ALIGN_RIGHT);
		resultDisplayer.setCellHorizontalAlignment(resultDisplayer,
				HasHorizontalAlignment.ALIGN_CENTER);

		createPagingUI();
	}

	/**
	 * creates background of the ui
	 */
	private void createBackGround() {
		
		/*int width = Window.getClientWidth();
		basePanel.setPixelSize(width-50, 0);
*/
		basePanel.setWidth("100%");
		resultDisplayer.setWidth("100%");
		
	
		dockPanel.add(resultDisplayer, DockPanel.CENTER);
		dockPanel.add(tagCloudPresenenter, DockPanel.EAST);
	//	dockPanel.add(tagContainerAndSeparator, DockPanel.EAST);
		dockPanel.setCellWidth(resultDisplayer, "80%");
		dockPanel.setCellWidth(tagCloudPresenenter, "20%");
	//	dockPanel.setCellWidth(tagContainerAndSeparator, "20%");
		
		dockPanel.setWidth("100%");
	
		
		basePanel.add(horiPanel);
	/*	separator.setStylePrimaryName("separatorCss");
		basePanel.add(separator);
	*/	basePanel.add(loader);
	//	basePanel.setSpacing(8);
		basePanel.add(dockPanel);

		basePanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
	}

	/**
	 * populates the search results
	 * @param resultList
	 */
	public void populateSearchNewsItems(SearchResultList resultList) {
		loader.disable();
		System.out.println("SearchResultPresenter::: populateSearchNewsItems");
		createUI();

		NewsItemList cache = conevrtSearchListToNewsList(resultList);

		pageCriteria = new PageCriteria();
		pageCriteria.setPageSize(15);
		pageCriteria.setStartIndex(0);

		setFullModeCriteria(pageCriteria);
		setSingleModeCriteria(pageCriteria);
	
		if (getMode() == SINGLESELECTIONMODE) {
			fullModeCriteria.setStartIndex(0);
			setCurrentPage(1);
			setLastVisiblePage(1);
		}
		initialize(cache);
	}

	private void addLabelHandler(final HTML tagLabel) {
		tagLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String tagSelected = tagLabel.getText();
				tagSelected.trim();
				
				if((!(SEARCHEDSTR==null))&&(DATESTR==null)){
					if(SEARCHEDSTR.contains(":")){
						String splitter[] = SEARCHEDSTR.split(":");
						String tag = splitter[0];
						if(splitter.length>1){
						String keyword = splitter[1];
					//	String splitterTag[] = tagSelected.split("\\(");
						//String tagSelectedTemp = splitterTag[0];
					//	String searchText = tagSelectedTemp + ":" + keyword;
						SEARCHEDSTR = tagSelected + ":" + keyword;
						performSearch(SEARCHEDSTR, null);
						}
						else{
							SEARCHEDSTR = tagSelected + ":" ;
							performSearch(SEARCHEDSTR, null);
						}
					}
					else{
						/*String splitterTag[] = tagSelected.split("\\(");
						String tagSelectedTemp = splitterTag[0];
						*/
						SEARCHEDSTR = tagSelected + ":" + SEARCHEDSTR;
						performSearch(SEARCHEDSTR, null);
					}
				}
				else if(SEARCHEDSTR==null &&(!(DATESTR==null))){
				/*	String splitterTag[] = tagSelected.split("\\(");
					String tagSelectedTemp = splitterTag[0];
				*/	SEARCHEDSTR = tagSelected+":";
					performSearch(SEARCHEDSTR, DATESTR);
			}
				else if((!(SEARCHEDSTR==null))&&(!(DATESTR==null))){
					if(SEARCHEDSTR.contains(":")){
						String splitter[] = SEARCHEDSTR.split(":");
						String tag = splitter[0];
						if(!(splitter.length>1)){
								SEARCHEDSTR = tagSelected+":";
								performSearch(SearchResultPresenter.SEARCHEDSTR, DATESTR);
							}
							else{
								/*String splitterTag[] = tagSelected.split("\\(");
								String tagSelectedTemp = splitterTag[0];
								*/
								String keyword = splitter[1];
								String searchText = tagSelected + ":";
								if(!(keyword.contains("null")))
								{ 
									searchText = searchText+ keyword;
								
								}
								SEARCHEDSTR = searchText;
								performSearch(searchText, DATESTR);
							}
						}
					else{
						if(tagSelected!=null)
						{
							SEARCHEDSTR =tagSelected+":"+ SEARCHEDSTR;
							performSearch(SEARCHEDSTR, DATESTR);
						}
					}
						/*else{
							String tagSelected = tagLabel.getText();
							tagSelected.trim();
							String splitterTag[] = tagSelected.split("\\(");
							String tagSelectedTemp = splitterTag[0];
							
							String searchText = tagSelected + ":" + keyword;
							SearchResultPresenter.SEARCHEDSTR = searchText;
							performSearch(searchText, DATESTR);
						}*/
					}
					else{
					/*	String splitterTag[] = tagSelected.split("\\(");
						String tagSelectedTemp = splitterTag[0];
					*/	
					//	String searchText = tagSelectedTemp + ":" + SEARCHEDSTR;
						SEARCHEDSTR =tagSelected+":";
						performSearch(SEARCHEDSTR, DATESTR);
					}
				}
		});
	}

	private void displayNoResult() {
		//basePanel.setWidth("1000px");
		/*int width = Window.getClientWidth();
		basePanel.setPixelSize(width-50, 0);
*/
		basePanel.setWidth("100%");
		resultDisplayer.setWidth("100%");
		loader.disable();
		createUI();
		resultDisplayer.clear();
		
		pagingPanel.clear();
		noResults.setStylePrimaryName("NoSearchResult");
	//	noResults.setText("No Search Results Found");
		noResults.setText("Sorry, there were no results found");
		resultDisplayer.add(noResults);
	}

	private void displayNoRelatedTag() {
		tagCloudLoader.disable();
		tagCloudPresenenter.setWidth("100%");
		tagCloudHolder.clear();
		
		Label noRelatedTags = new Label("No Related Tags");
		noRelatedTags.setStylePrimaryName("tagCloudLink");
		tagCloudHolder.add(noRelatedTags);
	}

	private NewsItemList conevrtSearchListToNewsList(SearchResultList resultList) {
		NewsItemList cache = new NewsItemList();
		for (int i = 0; i < resultList.size(); i++) {
			NewsItems item = new NewsItems();
			item = resultList.get(i);
			cache.add(item);
		}

		cache.setCurrentPageNo(resultList.getCurrentPageNo());
		cache.setNumItems(resultList.getNumItems());
		cache.setStartIndex(resultList.getStartIndex());
		cache.setTotalPages(resultList.getTotalPages());

		return cache;
	}

	private void populateTagcloud(HashMap<String, Long> result) {
		//tagCloudHolder.clear();
		TreeMap<String, Long> tagCloud = (TreeMap<java.lang.String, java.lang.Long>) sortByValues(result);
		for (Map.Entry<String, Long> entry : tagCloud.entrySet()) {
	    	String key = (String) entry.getKey();
			String value = entry.getValue().toString();
		//	String tagString = key + "(" + value + ")";
		//	HTML tagLabel = new HTML(tagString);
			HTML tagLabel = new HTML(key.trim());
			HTML tagValue = new HTML("("+value.trim()+")&nbsp&nbsp");
		//	HTML separator = new HTML("&nbsp&nbsp");
			tagLabel.setStylePrimaryName("tagCloudLink");
			tagValue.setStylePrimaryName("tagCloudLinkNumber");
			addLabelHandler(tagLabel);
			tagCloudHolder.add(tagLabel);
			tagCloudHolder.add(tagValue);
		//	tagCloudHolder.add(separator);
	    }

		tagCloudLoader.disable();
	}
	
	
	private HashMap<String, Long> sortHashMap(HashMap<String, Long> input){
	    Map<String, Long> tempMap = new HashMap<String, Long>();
	    for (String wsState : input.keySet()){
	        tempMap.put(wsState,input.get(wsState));
	    }

	    List<String> mapKeys = new ArrayList<String>(tempMap.keySet());
	    List<Long> mapValues = new ArrayList<Long>(tempMap.values());
	    HashMap<String, Long> sortedMap = new LinkedHashMap<String, Long>();
	
	    TreeSet<Long> sortedSet = new TreeSet<Long>(mapValues);
	    Object[] sortedArray = sortedSet.toArray();
	    int size = sortedArray.length;
	    for (int i=size; i<0; i--){
	        sortedMap.put(mapKeys.get(mapValues.indexOf(sortedArray[i])), 
	                      (Long)sortedArray[i]);
	    }
	    return sortedMap;
	}
	
	public static <String, Long extends Comparable<Long>> Map<String, Long> sortByValues(final Map<String, Long> map) {
		Comparator<String> valueComparator =  new Comparator<String>() {
		    public int compare(String k1, String k2) {
		        int compare = map.get(k2).compareTo(map.get(k1));
		        if (compare == 0) return 1;
		        else return compare;
		    }
		};
		TreeMap<String, Long> sortedByValues = new TreeMap<String, Long>(valueComparator);
		
		sortedByValues.putAll(map);
	
		return sortedByValues;	
	}
	
	public void performSearch(String textStr, String dateStr) {
		System.out.println("SearchResultPresenter::: performSearch()");
		loader.setLoadingMessage("Searching....");
		loader.enable();
		tagCloudLoader.setLoadingMessage("Creating..");
		tagCloudLoader.enable();

		SearchResultPresenter.SEARCHEDSTR = textStr;
		SearchResultPresenter.DATESTR = dateStr;
		
		resultDisplayer.clear();
		tagCloudHolder.clear();
		DateRange range  = null;
		if (dateStr!=null) {
		String splitter[] = dateStr.split("TO");
		String startDateStr = splitter[0];
		String endDateStr = splitter[1];
		if(textStr!=null)
			searchTextLbl.setText("The Searched Text : " + textStr
					+ " AND Start date : [" + startDateStr + "] TO [" + endDateStr
					+ "]");
		else
			searchTextLbl.setText("The Searched under date range :Start date : [" + startDateStr + "] TO [" + endDateStr
					+ "]");
			range = new DateRange();
			java.sql.Date startDate = range.convertStrToSql(startDateStr);
			java.sql.Date endDate = range.convertStrToSql(endDateStr);
			range.setFromDate(startDate);
			range.setToDate(endDate);
		}
		else
			searchTextLbl.setText("The Searched Text : " + textStr);
		
		System.out.println("SearchResultPresenter:performSearch()::eventData is HashMap");
		SearchServiceAsync async = GWT.create(SearchService.class);
		async.getSearchResultList(textStr,null, range,
				new AsyncCallback<SearchResultList>() {

					@Override
					public void onSuccess(SearchResultList result) {
						if (result.isEmpty()) {
							displayNoResult();
						} else {
							populateSearchNewsItems(result);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});

		async.getTagCloud(textStr, range,
				new AsyncCallback<HashMap<String, Long>>() {
					@Override
					public void onSuccess(HashMap<String, Long> result) {
						if (result.isEmpty()) {
							displayNoRelatedTag();
						} else {
							populateTagcloud(result);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}
				});
	}

	/** Paging related methods */
	private VerticalPanel newsPanel ;
	private Label headerLabel;
	private FlexTable linksPagingPanel;
	private Label lbOld;
	private int mode;

	private Label lbNew;
	public static final int FULLSELECTIONMODE = 1;
	public static final int SINGLESELECTIONMODE = 2;
	private int counter;// = 0;

	private boolean tagPresenterStatus = false;

//	private HorizontalPanel headerLabelPanel = new HorizontalPanel();

	private HorizontalPanel olderlinkpanel ;//= new HorizontalPanel();
	private HorizontalPanel newerlinkpanel ;//= new HorizontalPanel();
	private HorizontalPanel pagingPanel ;// = new HorizontalPanel();

	private LhNewsItemPresenter itempresenter;
	private LhNewsItemPresenter currentItemPresenter;

	private NewsItemList fullNewsItemList;
	private NewsItemList tagNewsItemList;
	private NewsItemList currentNewsItemList;

	private int pageSize;
	private Label backLabel = new Label("Back to latest stories");
	//private HorizontalPanel backPanel = new HorizontalPanel();

	private PageCriteria pageCriteria;
	private PageCriteria fullModeCriteria ;//= new PageCriteria();
	private PageCriteria singleModeCriteria ;//= new PageCriteria();

	private int currentPage;
	private int currentTagNewsPage;

	private HashMap<Integer, Label> pageLabelMap;
	private HashMap<Integer, Label> tagPageLabelMap ;
	private int lastVisiblePage;
	private TagItem currentAppliedTagNewsViewed;
	private int requestedPage;
	private int requestedTagNewsPage;
	private int maxlimit = 9;
	private int minlimit = 10;
	private int newsMode;

	private static int backLinkClicked = 0;
	private int subscriptionMode;

	private HashMap<Integer, LhNewsItemPresenter> itemPresenterMap ;

	private VerticalPanel paginContainer ;
	
	/**code modification*/
	Image nextim;
	Image previm;

	private void createPagingUI() {
		
		/**code modifications*/
		maxlimit =9;
		minlimit = 10;
		counter = 0;
		backLinkClicked = 0;
		olderlinkpanel = new HorizontalPanel();
		newerlinkpanel = new HorizontalPanel();
		pagingPanel = new HorizontalPanel();
		fullModeCriteria = new PageCriteria();
		singleModeCriteria = new PageCriteria();
		pageLabelMap =  new HashMap<Integer, Label>();
		tagPageLabelMap = new HashMap<Integer, Label>();
		itemPresenterMap = new HashMap<Integer, LhNewsItemPresenter>();
		paginContainer = new VerticalPanel();
		lbOld = new Label("NEXT");
		lbNew  = new Label("PREVIOUS");
		newsPanel = new VerticalPanel();
		headerLabel = new Label();
		linksPagingPanel= new FlexTable();
		tagPresenterStatus = false;
		
		
		paginContainer.clear();
		olderlinkpanel.clear();
		newerlinkpanel.clear();
		nextim = new Image("images/arrow_more.gif", 0, -1, 16, 16);
		previm = new Image("images/arrow_less.gif", 0, 2, 16, 16);
		olderlinkpanel.add(lbOld);
		olderlinkpanel.add(nextim);
		newerlinkpanel.add(previm);
		newerlinkpanel.add(lbNew);

		olderlinkpanel.setCellVerticalAlignment(lbOld,
				HasVerticalAlignment.ALIGN_MIDDLE);
		olderlinkpanel.setCellVerticalAlignment(nextim,
				HasVerticalAlignment.ALIGN_MIDDLE);
		newerlinkpanel.setCellVerticalAlignment(lbNew,
				HasVerticalAlignment.ALIGN_MIDDLE);
		newerlinkpanel.setCellVerticalAlignment(nextim,
				HasVerticalAlignment.ALIGN_MIDDLE);

		olderlinkpanel.setStylePrimaryName("imageBorder");
		newerlinkpanel.setStylePrimaryName("imageBorder");
		lbOld.setStylePrimaryName("oldlink");
		lbNew.setStylePrimaryName("newlink");
		lbOld.setTitle("View next page");
		lbNew.setTitle("View previous page");

		lbOld.addClickHandler((ClickHandler) this);
		lbNew.addClickHandler((ClickHandler) this);
		olderlinkpanel.setVisible(true);
		newerlinkpanel.setVisible(true);

		linksPagingPanel.setCellSpacing(3);
		pagingPanel.add(newerlinkpanel);
		pagingPanel.add(linksPagingPanel);
		pagingPanel.add(olderlinkpanel);

		pagingPanel.setCellHorizontalAlignment(newerlinkpanel,
				HasHorizontalAlignment.ALIGN_LEFT);
		pagingPanel.setCellVerticalAlignment(newerlinkpanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		pagingPanel.setCellHorizontalAlignment(linksPagingPanel,
				HasHorizontalAlignment.ALIGN_LEFT);
		pagingPanel.setCellVerticalAlignment(linksPagingPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		pagingPanel.setCellHorizontalAlignment(olderlinkpanel,
				HasHorizontalAlignment.ALIGN_LEFT);
		pagingPanel.setCellVerticalAlignment(olderlinkpanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		pagingPanel.setStylePrimaryName("pagingPanel");
		newsPanel.setStylePrimaryName("newsPanel");
		paginContainer.add(newsPanel);
		paginContainer.add(pagingPanel);
		resultDisplayer.add(paginContainer);
	}

	private void addInPagingPanel() {
		int totalpages = 0;
		int page = 0;
		if (getMode() == FULLSELECTIONMODE) {
			totalpages = getCurrentNewsItemList().getTotalPages();
			page = getCurrentPage();
		} else if (getMode() == SINGLESELECTIONMODE) {
			totalpages = getTagNewsItemList().getTotalPages();
			page = getCurrentTagNewsPage();
		}
		try {
			if (page <= 11) {
				int lastpage = getLastVisiblePage() + 1;
				if (lastpage <= totalpages) {
					linksPagingPanel.setWidget(0, lastpage,
							createPagingLabel(String.valueOf(lastpage)));
					setLastVisiblePage(lastpage);
					if (page == totalpages)
						olderlinkpanel.setVisible(false);
				}
			} else {
				int lastpage = getLastVisiblePage() + 1;
				linksPagingPanel.removeCell(0, 1);
				int cellcnt = linksPagingPanel.getCellCount(0);
				if (lastpage <= totalpages) {
					linksPagingPanel.setWidget(0, cellcnt,
							createPagingLabel(String.valueOf(lastpage)));
					setLastVisiblePage(lastpage);
					if (lastpage == totalpages)
						olderlinkpanel.setVisible(false);
				}
			}
		} catch (Exception ex) {
			GWT.getUncaughtExceptionHandler().onUncaughtException(ex);
		}
	}

	private void addInPagingPanel(int page) {
		try {
			if (page <= 11) {
				int lastpage = getLastVisiblePage() + 1;
				int totalpages = 0;
				if (getMode() == FULLSELECTIONMODE)
					totalpages = getCurrentNewsItemList().getTotalPages();
				else if (getMode() == SINGLESELECTIONMODE)
					totalpages = getTagNewsItemList().getTotalPages();

				if (lastpage <= totalpages) {
					linksPagingPanel.setWidget(0, lastpage,
							createPagingLabel(String.valueOf(lastpage)));
					setLastVisiblePage(lastpage);
					if (page == totalpages)
						olderlinkpanel.setVisible(false);
				}
			} else {
				int lastpage = getLastVisiblePage() + 1;
				int totalpages = 0;
				if (getMode() == FULLSELECTIONMODE)
					totalpages = getCurrentNewsItemList().getTotalPages();
				else if (getMode() == SINGLESELECTIONMODE)
					totalpages = getTagNewsItemList().getTotalPages();
				linksPagingPanel.removeCell(0, 1);
				int cellcnt = linksPagingPanel.getCellCount(0);
				if (lastpage <= totalpages) {
					linksPagingPanel.setWidget(0, cellcnt,
							createPagingLabel(String.valueOf(lastpage)));
					setLastVisiblePage(lastpage);
					if (page == totalpages)
						olderlinkpanel.setVisible(false);
				}
			}
		} catch (Exception ex) {
			GWT.getUncaughtExceptionHandler().onUncaughtException(ex);
		}
	}

	private void removeFromPagingPanel(int page) {
		if (page <= 11) {
			int lastpage = getLastVisiblePage() - 1;
			int lvp = getLastVisiblePage();
			if (getMode() == FULLSELECTIONMODE) {
				if (lvp > getRequestedPage() + maxlimit) {
					int cellcnt = linksPagingPanel.getCellCount(0);
					linksPagingPanel.removeCell(0, cellcnt - 1);
					setLastVisiblePage(lastpage);
				}
			} else if (getMode() == SINGLESELECTIONMODE) {
				if (lvp > getRequestedTagNewsPage() + maxlimit) {
					int cellcnt = linksPagingPanel.getCellCount(0);
					linksPagingPanel.removeCell(0, cellcnt - 1);
					setLastVisiblePage(lastpage);
				}
			}
			if (page == 1)
				newerlinkpanel.setVisible(false);
		} else {
			int no = (page - 11);
			int lastpage = getLastVisiblePage() - 1;
			int lvp = getLastVisiblePage();
			if (getMode() == FULLSELECTIONMODE) {
				if (lvp > getRequestedPage() + maxlimit) {
					int cellcnt = linksPagingPanel.getCellCount(0);
					linksPagingPanel.removeCell(0, cellcnt - 1);
					setLastVisiblePage(lastpage);
				}
			} else if (getMode() == SINGLESELECTIONMODE) {
				if (lvp > getRequestedTagNewsPage() + maxlimit) {
					int cellcnt = linksPagingPanel.getCellCount(0);
					linksPagingPanel.removeCell(0, cellcnt - 1);
					setLastVisiblePage(lastpage);
				}
			}
			linksPagingPanel.insertCell(0, 1);
			linksPagingPanel.setWidget(0, 1,
					createPagingLabel(String.valueOf(no)));
		}
	}

	private Label createPagingLabel(String text) {
		Label pageno = new Label(text);
		pageno.setStylePrimaryName("pageNoLabel");
		pageno.addClickHandler(this);

		if (getMode() == FULLSELECTIONMODE) {
			if (getCurrentPage() == Integer.parseInt(text))
				pageno.addStyleName("selectedPageNoLabel");
			pageLabelMap.put(Integer.parseInt(text), pageno);
		} else if (getMode() == SINGLESELECTIONMODE) {
			if (getCurrentTagNewsPage() == Integer.parseInt(text))
				pageno.addStyleName("selectedPageNoLabel");
			tagPageLabelMap.put(Integer.parseInt(text), pageno);
		}

		return pageno;
	}

	public void initialize(NewsItemList list) {
		if (fullModeCriteria.getPageSize() == -1
				&& fullModeCriteria.getStartIndex() == -1) {
			fullModeCriteria.setPageSize(getPageCriteria().getPageSize());
			fullModeCriteria.setStartIndex(getPageCriteria().getStartIndex());
		}
		if (singleModeCriteria.getPageSize() == -1
				&& singleModeCriteria.getStartIndex() == -1) {
			singleModeCriteria.setPageSize(getPageCriteria().getPageSize());
			singleModeCriteria.setStartIndex(getPageCriteria().getStartIndex());
		}
		if (getCurrentItemPresenter() != null && getMode() == FULLSELECTIONMODE) {
			newsPanel.remove(getCurrentItemPresenter());
		}
		if (counter == 0 || getMode() == FULLSELECTIONMODE) {
			itempresenter = new LhNewsItemPresenter(getLhUser());
			int start = getPageCriteria().getStartIndex();
			int size = getPageCriteria().getPageSize();
			setMode(FULLSELECTIONMODE);
			if (linksPagingPanel.getRowCount() == 1)
				linksPagingPanel.removeRow(0);
			linksPagingPanel.setVisible(true);
			setCurrentPage(1);
			if (list.getTotalPages() > 10) {
				for (int i = 1; i <= 10; i++) {
					linksPagingPanel.setWidget(0, i,
							createPagingLabel(String.valueOf(i)));
					setLastVisiblePage(i);
				}
				olderlinkpanel.setVisible(true);
				newerlinkpanel.setVisible(false);
			} else {
				for (int i = 1; i <= list.getTotalPages(); i++) {
					linksPagingPanel.setWidget(0, i,
							createPagingLabel(String.valueOf(i)));
					setLastVisiblePage(i);
				}
				if (list.getTotalPages() == 1)
					olderlinkpanel.setVisible(false);
				else
					olderlinkpanel.setVisible(true);
				newerlinkpanel.setVisible(false);
			}
			NewsItemList sublist = new NewsItemList();
			if (list.size() != 0) {
				while (start < size) {
					if (start < list.size()) {
						sublist.add(list.get(start));
						start++;
					} else {
						start++;
					}
				}
				headerLabel
						.setText("Total: " + list.getNumItems() + " stories");
				itempresenter.populateNewsItems(sublist);
			} else {
				headerLabel.setText("No news to display");
				newerlinkpanel.setVisible(false);
				linksPagingPanel.setVisible(false);
				olderlinkpanel.setVisible(false);
			}
			setCurrentNewsItemList(list);
			newsPanel.add(itempresenter);
			itemPresenterMap.put(FULLSELECTIONMODE, itempresenter);
			setCurrentItemPresenter(itempresenter);
			loader.disable();
			counter++;
		} else {
			setCurrentNewsItemList(list);
			loader.disable();
		}
		RootPanel.get().setHeight("100%");
	}

	/**
	 * Called when newspresenter needs to be refreshed based on minimizing or
	 * maximizing of tag presenter
	 * 
	 * @param list
	 */
	public void refresh(NewsItemList list) {
		if (getMode() == FULLSELECTIONMODE) {
			if (getCurrentItemPresenter() != null) {
				newsPanel.remove(getCurrentItemPresenter());
			}
			itempresenter = new LhNewsItemPresenter(getLhUser());
			int start = 0;
			int size = 0;
			start = getFullModeCriteria().getStartIndex();
			size = getFullModeCriteria().getPageSize();
			NewsItemList sublist = new NewsItemList();
			if (list.size() > size) {
				olderlinkpanel.setVisible(true);
				newerlinkpanel.setVisible(false);
			} else {
				olderlinkpanel.setVisible(false);
				newerlinkpanel.setVisible(false);
			}
			if (list.size() != 0) {
				while (start < size) {
					if (start < list.size()) {
						sublist.add(list.get(start));
						start++;
					} else {
						start++;
					}
				}
				headerLabel.setText("Total: " + list.size() + " stories");
				getFullModeCriteria().setStartIndex(start);
				itempresenter.populateNewsItems(sublist);
			}
			setFullNewsItemList(list);
			newsPanel.add(itempresenter);
			itemPresenterMap.put(FULLSELECTIONMODE, itempresenter);
			setCurrentItemPresenter(itempresenter);
			loader.disable();
			setMode(FULLSELECTIONMODE);
			counter++;
		}

		if (counter == 0 || getMode() == SINGLESELECTIONMODE) {
			if (getCurrentItemPresenter() != null) {
				newsPanel.remove(getCurrentItemPresenter());
			}
			itempresenter = new LhNewsItemPresenter(getLhUser());
			int start = 0;
			int size = 0;
			if (!isTagPresenterStatus()) {
				start = getPageCriteria().getStartIndex();
				size = getPageCriteria().getPageSize();
			} else {
				start = getSingleModeCriteria().getStartIndex();
				size = getSingleModeCriteria().getPageSize();
			}
			NewsItemList sublist = new NewsItemList();
			if (list.size() > size) {
				olderlinkpanel.setVisible(true);
				newerlinkpanel.setVisible(false);
			} else {
				olderlinkpanel.setVisible(false);
				newerlinkpanel.setVisible(false);
			}
			if (list.size() != 0) {
				while (start < size) {
					if (start < list.size()) {
						sublist.add(list.get(start));
						start++;
					} else {
						start++;
					}
				}
				headerLabel.setText("Total: " + list.size() + " stories");
				getSingleModeCriteria().setStartIndex(start);
				itempresenter.populateNewsItems(sublist);
			} else {
				headerLabel.setText("No news to display");
			}
			setTagNewsItemList(list);
			newsPanel.add(itempresenter);
			itemPresenterMap.put(SINGLESELECTIONMODE, itempresenter);
			setCurrentItemPresenter(itempresenter);
			loader.disable();
			setMode(SINGLESELECTIONMODE);
			counter++;
		} else {
			setFullNewsItemList(list);
			loader.disable();
		}
	}

	public Label getLbOld() {
		return lbOld;
	}

	public void setLbOld(Label lbOld) {
		this.lbOld = lbOld;
	}

	public Label getLbNew() {
		return lbNew;
	}

	public void setLbNew(Label lbNew) {
		this.lbNew = lbNew;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public HorizontalPanel getOlderlinkpanel() {
		return olderlinkpanel;
	}

	public void setOlderlinkpanel(HorizontalPanel olderlinkpanel) {
		this.olderlinkpanel = olderlinkpanel;
	}

	public HorizontalPanel getNewerlinkpanel() {
		return newerlinkpanel;
	}

	public void setNewerlinkpanel(HorizontalPanel newerlinkpanel) {
		this.newerlinkpanel = newerlinkpanel;
	}

	public HorizontalPanel getPagingPanel() {
		return pagingPanel;
	}

	public void setPagingPanel(HorizontalPanel pagingPanel) {
		this.pagingPanel = pagingPanel;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

	public void setItempresenter(LhNewsItemPresenter itempresenter) {
		this.itempresenter = itempresenter;
	}

	public LhNewsItemPresenter getItempresenter() {
		return itempresenter;
	}

	public void setCurrentItemPresenter(LhNewsItemPresenter currentItemPresenter) {
		this.currentItemPresenter = currentItemPresenter;
	}

	public LhNewsItemPresenter getCurrentItemPresenter() {
		return currentItemPresenter;
	}

	public void setFullNewsItemList(NewsItemList fullNewsItemList) {
		this.fullNewsItemList = fullNewsItemList;
	}

	public NewsItemList getFullNewsItemList() {
		return fullNewsItemList;
	}

	public void setTagNewsItemList(NewsItemList tagNewsItemList) {
		this.tagNewsItemList = tagNewsItemList;
	}

	public NewsItemList getTagNewsItemList() {
		return tagNewsItemList;
	}

	public void setCurrentNewsItemList(NewsItemList currentNewsItemList) {
		this.currentNewsItemList = currentNewsItemList;
	}

	public NewsItemList getCurrentNewsItemList() {
		return currentNewsItemList;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setBackLabel(Label backLabel) {
		this.backLabel = backLabel;
	}

	public Label getBackLabel() {
		return backLabel;
	}

	public void setPageCriteria(PageCriteria pageCriteria) {
		this.pageCriteria = pageCriteria;
	}

	public PageCriteria getPageCriteria() {
		return pageCriteria;
	}

	public void setFullModeCriteria(PageCriteria fullModeCriteria) {
		this.fullModeCriteria = fullModeCriteria;
	}

	public PageCriteria getFullModeCriteria() {
		return fullModeCriteria;
	}

	public void setSingleModeCriteria(PageCriteria singleModeCriteria) {
		this.singleModeCriteria = singleModeCriteria;
	}

	public PageCriteria getSingleModeCriteria() {
		return singleModeCriteria;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentTagNewsPage(int currentTagNewsPage) {
		this.currentTagNewsPage = currentTagNewsPage;
	}

	public int getCurrentTagNewsPage() {
		return currentTagNewsPage;
	}

	public void setLastVisiblePage(int lastVisiblePage) {
		this.lastVisiblePage = lastVisiblePage;
	}

	public int getLastVisiblePage() {
		return lastVisiblePage;
	}

	public void setCurrentAppliedTagNewsViewed(
			TagItem currentAppliedTagNewsViewed) {
		this.currentAppliedTagNewsViewed = currentAppliedTagNewsViewed;
	}

	public TagItem getCurrentAppliedTagNewsViewed() {
		return currentAppliedTagNewsViewed;
	}

	public void setRequestedPage(int requestedPage) {
		this.requestedPage = requestedPage;
	}

	public int getRequestedPage() {
		return requestedPage;
	}

	public void setRequestedTagNewsPage(int requestedTagNewsPage) {
		this.requestedTagNewsPage = requestedTagNewsPage;
	}

	public int getRequestedTagNewsPage() {
		return requestedTagNewsPage;
	}

	public void setMinlimit(int minlimit) {
		this.minlimit = minlimit;
	}

	public int getMinlimit() {
		return minlimit;
	}

	public void setNewsMode(int newsMode) {
		this.newsMode = newsMode;
	}

	public int getNewsMode() {
		return newsMode;
	}

	public static void setBackLinkClicked(int backLinkClicked) {
		SearchResultPresenter.backLinkClicked = backLinkClicked;
	}

	public static int getBackLinkClicked() {
		return backLinkClicked;
	}

	public void setSubscriptionMode(int subscriptionMode) {
		this.subscriptionMode = subscriptionMode;
	}

	public int getSubscriptionMode() {
		return subscriptionMode;
	}

	public void setTagPresenterStatus(boolean tagPresenterStatus) {
		this.tagPresenterStatus = tagPresenterStatus;
	}

	public boolean isTagPresenterStatus() {
		return tagPresenterStatus;
	}

	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Widget arg0 = (Widget) event.getSource();
		if (arg0 instanceof Label) {
			Label label = (Label) arg0;

			if (label.getText().equals("NEXT")) {
				NewsItemList sublist = new NewsItemList();
				if (getMode() == FULLSELECTIONMODE) {
					newerlinkpanel.setVisible(true);
					int page = getCurrentPage();
					Label lbl = new Label();
					lbl = (Label) pageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentPage(page + 1);
					int start = getFullModeCriteria().getStartIndex();
					int size = getFullModeCriteria().getPageSize();
					getFullModeCriteria().setStartIndex(start + size);
					// NewsStore.getInstance().getNewsPage(getFullModeCriteria(),getNewsMode());
					loader.setLoadingMessage("Fetching...");
					loader.enable();
					
					SearchServiceAsync async = GWT.create(SearchService.class);
					async.getPage(getFullModeCriteria(), getNewsMode(),
							new AsyncCallback<SearchResultList>() {

								@Override
								public void onSuccess(SearchResultList result) {

									NewsItemList newslist = conevrtSearchListToNewsList(result);
									if (getMode() == SINGLESELECTIONMODE) {
										setMode(FULLSELECTIONMODE);
										if (linksPagingPanel.getRowCount() == 1)
											linksPagingPanel.removeRow(0);
										// pageLabelMap.clear();
										if (newslist.getTotalPages() > 10) {
											int currentpg = getCurrentPage();
											int col = 1;
											if (currentpg > 10) {
												for (int i = (currentpg - minlimit); i <= (currentpg + maxlimit); i++) {
													if (i <= newslist
															.getTotalPages()) {
														linksPagingPanel
																.setWidget(
																		0,
																		col,
																		createPagingLabel(String
																				.valueOf(i)));
														setLastVisiblePage(i);
														col++;
													}
												}
											} else {
												for (int i = 1; i <= 10; i++) {
													linksPagingPanel
															.setWidget(
																	0,
																	i,
																	createPagingLabel(String
																			.valueOf(i)));
													setLastVisiblePage(i);
												}
												if (currentpg != 1) {
													// int pg =
													// getCurrentPage();
													int pg = minlimit
															- getCurrentPage();
													while (getLastVisiblePage() < getCurrentPage()
															+ maxlimit) {
														// addInPagingPanel(pg);
														addInPagingPanel(pg + 1);
														pg++;
													}
												}
											}

											olderlinkpanel.setVisible(true);
											newerlinkpanel.setVisible(false);
										} else {
											for (int i = 1; i <= getCurrentNewsItemList()
													.getTotalPages(); i++) {
												linksPagingPanel
														.setWidget(
																0,
																i,
																createPagingLabel(String
																		.valueOf(i)));
												setLastVisiblePage(i);
											}
											if (newslist.getTotalPages() == 1
													|| newslist.size() == 0)
												olderlinkpanel
														.setVisible(false);
											else
												olderlinkpanel.setVisible(true);
											newerlinkpanel.setVisible(false);
										}
									}

									int curpage = getCurrentPage();
									Label lbl = (Label) pageLabelMap
											.get(curpage);
									if (lbl != null)
										lbl.addStyleName("selectedPageNoLabel");
									if (curpage != 1)
										newerlinkpanel.setVisible(true);

									int curtagpage = getCurrentTagNewsPage();
									if (curtagpage != 0) {
										Label lb = (Label) tagPageLabelMap
												.get(curtagpage);
										lb.removeStyleName("selectedPageNoLabel");
									}
									if (currentPage == 1)
										newerlinkpanel.setVisible(false);
									if (currentPage == newslist.getTotalPages())
										olderlinkpanel.setVisible(false);

									newsPanel.remove(getCurrentItemPresenter());
									LhNewsItemPresenter newsitempresenter = new LhNewsItemPresenter(getLhUser());
									newsitempresenter
											.populateNewsItems(newslist);
									setMode(FULLSELECTIONMODE);
									itemPresenterMap.put(getMode(),
											newsitempresenter);
									setCurrentItemPresenter(newsitempresenter);
									setCurrentNewsItemList(newslist);
									newsPanel.add(newsitempresenter);
									if (newslist.size() != 0)
										headerLabel.setText("Total: "
												+ newslist.getNumItems()
												+ " stories");
									else
										headerLabel
												.setText("No news to display");
									backLinkClicked = 0;
									loader.disable();

								}

								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
								}
							});

					addInPagingPanel();
				} else if (getMode() == SINGLESELECTIONMODE) {
					newerlinkpanel.setVisible(true);
					int page = getCurrentTagNewsPage();
					Label lbl = (Label) tagPageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentTagNewsPage(page + 1);
					int start = getSingleModeCriteria().getStartIndex();
					int size = getSingleModeCriteria().getPageSize();
					getSingleModeCriteria().setStartIndex(start + size);
					NewsStore.getInstance().getNews(
							getCurrentAppliedTagNewsViewed(),
							getSingleModeCriteria());
					addInPagingPanel();
				}
			} else if (label.getText().equals("PREVIOUS")) {
				olderlinkpanel.setVisible(true);
				NewsItemList sublist = new NewsItemList();

				if (getMode() == FULLSELECTIONMODE) {
					int page = getCurrentPage();
					Label lbl = (Label) pageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentPage(page - 1);
					setRequestedPage(page - 1);
					int start = getFullModeCriteria().getStartIndex();
					int size = getFullModeCriteria().getPageSize();
					getFullModeCriteria().setStartIndex(start - size);
					// NewsStore.getInstance().getNewsPage(getFullModeCriteria(),getNewsMode());
					loader.setLoadingMessage("Fetching...");
					loader.enable();
					SearchServiceAsync async = GWT.create(SearchService.class);
					async.getPage(getFullModeCriteria(), getNewsMode(),
							new AsyncCallback<SearchResultList>() {

								@Override
							public void onSuccess(SearchResultList result) {

									NewsItemList newslist = conevrtSearchListToNewsList(result);
									if (getMode() == SINGLESELECTIONMODE) {
										setMode(FULLSELECTIONMODE);
										if (linksPagingPanel.getRowCount() == 1)
											linksPagingPanel.removeRow(0);
										// pageLabelMap.clear();
										if (newslist.getTotalPages() > 10) {
											int currentpg = getCurrentPage();
											int col = 1;
											if (currentpg > 10) {
												for (int i = (currentpg - minlimit); i <= (currentpg + maxlimit); i++) {
													if (i <= newslist
															.getTotalPages()) {
														linksPagingPanel
																.setWidget(
																		0,
																		col,
																		createPagingLabel(String
																				.valueOf(i)));
														setLastVisiblePage(i);
														col++;
													}
												}
											} else {
												for (int i = 1; i <= 10; i++) {
													linksPagingPanel
															.setWidget(
																	0,
																	i,
																	createPagingLabel(String
																			.valueOf(i)));
													setLastVisiblePage(i);
												}
												if (currentpg != 1) {
													// int pg =
													// getCurrentPage();
													int pg = minlimit
															- getCurrentPage();
													while (getLastVisiblePage() < getCurrentPage()
															+ maxlimit) {
														// addInPagingPanel(pg);
														addInPagingPanel(pg + 1);
														pg++;
													}
												}
											}

											olderlinkpanel.setVisible(true);
											newerlinkpanel.setVisible(false);
										} else {
											for (int i = 1; i <= getCurrentNewsItemList()
													.getTotalPages(); i++) {
												linksPagingPanel
														.setWidget(
																0,
																i,
																createPagingLabel(String
																		.valueOf(i)));
												setLastVisiblePage(i);
											}
											if (newslist.getTotalPages() == 1
													|| newslist.size() == 0)
												olderlinkpanel
														.setVisible(false);
											else
												olderlinkpanel.setVisible(true);
											newerlinkpanel.setVisible(false);
										}
									}

									int curpage = getCurrentPage();
									Label lbl = (Label) pageLabelMap
											.get(curpage);
									if (lbl != null)
										lbl.addStyleName("selectedPageNoLabel");
									if (curpage != 1)
										newerlinkpanel.setVisible(true);

									int curtagpage = getCurrentTagNewsPage();
									if (curtagpage != 0) {
										Label lb = (Label) tagPageLabelMap
												.get(curtagpage);
										lb.removeStyleName("selectedPageNoLabel");
									}
									if (currentPage == 1)
										newerlinkpanel.setVisible(false);
									if (currentPage == newslist.getTotalPages())
										olderlinkpanel.setVisible(false);

									newsPanel.remove(getCurrentItemPresenter());
									LhNewsItemPresenter newsitempresenter = new LhNewsItemPresenter(getLhUser());
									newsitempresenter
											.populateNewsItems(newslist);
									setMode(FULLSELECTIONMODE);
									itemPresenterMap.put(getMode(),
											newsitempresenter);
									setCurrentItemPresenter(newsitempresenter);
									setCurrentNewsItemList(newslist);
									newsPanel.add(newsitempresenter);
									if (newslist.size() != 0)
										headerLabel.setText("Total: "
												+ newslist.getNumItems()
												+ " stories");
									else
										headerLabel
												.setText("No news to display");
									backLinkClicked = 0;
									loader.disable();

								}

								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
								}
							});

					removeFromPagingPanel(page);
				} else if (getMode() == SINGLESELECTIONMODE) {
					int page = getCurrentTagNewsPage();
					Label lbl = (Label) tagPageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentTagNewsPage(page - 1);
					setRequestedTagNewsPage(page - 1);
					int start = getSingleModeCriteria().getStartIndex();
					int size = getSingleModeCriteria().getPageSize();
					getSingleModeCriteria().setStartIndex(start - size);
					NewsStore.getInstance().getNews(
							getCurrentAppliedTagNewsViewed(),
							getSingleModeCriteria());
					removeFromPagingPanel(page);
				}
			} else {
				// int reqpage = Integer.parseInt(label.getText());
				if (getMode() == FULLSELECTIONMODE) {
					int reqpage = Integer.parseInt(label.getText());
					setRequestedPage(reqpage);
					int page = getCurrentPage();
					int index = getFullModeCriteria().getStartIndex();
					int startind = (15 * reqpage) - 15;
					getFullModeCriteria().setStartIndex(startind);
					Label lbl = (Label) pageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentPage(reqpage);
					// NewsStore.getInstance().getNewsPage(getFullModeCriteria(),
					// getNewsMode());
					loader.setLoadingMessage("Fetching...");
					SearchServiceAsync async = GWT.create(SearchService.class);
					async.getPage(getFullModeCriteria(), getNewsMode(),
							new AsyncCallback<SearchResultList>() {

								@Override
								public void onSuccess(SearchResultList result) {

									NewsItemList newslist = conevrtSearchListToNewsList(result);
									if (getMode() == SINGLESELECTIONMODE) {
										setMode(FULLSELECTIONMODE);
										if (linksPagingPanel.getRowCount() == 1)
											linksPagingPanel.removeRow(0);
										// pageLabelMap.clear();
										if (newslist.getTotalPages() > 10) {
											int currentpg = getCurrentPage();
											int col = 1;
											if (currentpg > 10) {
												for (int i = (currentpg - minlimit); i <= (currentpg + maxlimit); i++) {
													if (i <= newslist
															.getTotalPages()) {
														linksPagingPanel
																.setWidget(
																		0,
																		col,
																		createPagingLabel(String
																				.valueOf(i)));
														setLastVisiblePage(i);
														col++;
													}
												}
											} else {
												for (int i = 1; i <= 10; i++) {
													linksPagingPanel
															.setWidget(
																	0,
																	i,
																	createPagingLabel(String
																			.valueOf(i)));
													setLastVisiblePage(i);
												}
												if (currentpg != 1) {
													// int pg =
													// getCurrentPage();
													int pg = minlimit
															- getCurrentPage();
													while (getLastVisiblePage() < getCurrentPage()
															+ maxlimit) {
														// addInPagingPanel(pg);
														addInPagingPanel(pg + 1);
														pg++;
													}
												}
											}

											olderlinkpanel.setVisible(true);
											newerlinkpanel.setVisible(false);
										} else {
											for (int i = 1; i <= getCurrentNewsItemList()
													.getTotalPages(); i++) {
												linksPagingPanel
														.setWidget(
																0,
																i,
																createPagingLabel(String
																		.valueOf(i)));
												setLastVisiblePage(i);
											}
											if (newslist.getTotalPages() == 1
													|| newslist.size() == 0)
												olderlinkpanel
														.setVisible(false);
											else
												olderlinkpanel.setVisible(true);
											newerlinkpanel.setVisible(false);
										}
									}

									int curpage = getCurrentPage();
									Label lbl = (Label) pageLabelMap
											.get(curpage);
									if (lbl != null)
										lbl.addStyleName("selectedPageNoLabel");
									if (curpage != 1)
										newerlinkpanel.setVisible(true);

									int curtagpage = getCurrentTagNewsPage();
									if (curtagpage != 0) {
										Label lb = (Label) tagPageLabelMap
												.get(curtagpage);
										lb.removeStyleName("selectedPageNoLabel");
									}
									if (currentPage == 1)
										newerlinkpanel.setVisible(false);
									if (currentPage == newslist.getTotalPages())
										olderlinkpanel.setVisible(false);

									newsPanel.remove(getCurrentItemPresenter());
									LhNewsItemPresenter newsitempresenter = new LhNewsItemPresenter(getLhUser());
									newsitempresenter
											.populateNewsItems(newslist);
									setMode(FULLSELECTIONMODE);
									itemPresenterMap.put(getMode(),
											newsitempresenter);
									setCurrentItemPresenter(newsitempresenter);
									setCurrentNewsItemList(newslist);
									newsPanel.add(newsitempresenter);
									if (newslist.size() != 0)
										headerLabel.setText("Total: "
												+ newslist.getNumItems()
												+ " stories");
									else
										headerLabel
												.setText("No news to display");
									backLinkClicked = 0;
									loader.disable();

								}

								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
								}
							});

					if (page < reqpage) {
						newerlinkpanel.setVisible(true);
						for (int j = page + 1; j <= reqpage; j++) {
							addInPagingPanel(j);
						}
					} else {
						olderlinkpanel.setVisible(true);
						for (int j = page; j > reqpage; j--) {
							removeFromPagingPanel(j);
						}
					}
				} else if (getMode() == SINGLESELECTIONMODE) {
					int reqpage = Integer.parseInt(label.getText());
					setRequestedTagNewsPage(reqpage);
					int page = getCurrentTagNewsPage();
					int index = getSingleModeCriteria().getStartIndex();
					int startind = (15 * reqpage) - 15;
					getSingleModeCriteria().setStartIndex(startind);
					Label lbl = (Label) tagPageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentTagNewsPage(reqpage);
					NewsStore.getInstance().getNews(
							getCurrentAppliedTagNewsViewed(),
							getSingleModeCriteria());
					if (page < reqpage) {
						newerlinkpanel.setVisible(true);
						for (int j = page + 1; j <= reqpage; j++) {
							addInPagingPanel(j);
						}
					} else {
						olderlinkpanel.setVisible(true);
						for (int j = page; j > reqpage; j--) {
							removeFromPagingPanel(j);
						}
					}
				}

			}
		}
	}

	public LhUser getLhUser() {
		return lhUser;
	}

	public void setLhUser(LhUser lhUser) {
		this.lhUser = lhUser;
	}

	public LhMain getLhMainRef() {
		return lhMainRef;
	}

	public void setLhMainRef(LhMain lhMainRef) {
		this.lhMainRef = lhMainRef;
	}

}
