package com.lighthouse.main.client.ui;

import java.util.HashMap;
import java.util.List;

import com.appUtils.client.ProgressIndicatorWidget;
import com.appUtils.client.exception.FeatureNotSupportedException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.group.client.GroupItemStore;
import com.lighthouse.group.client.GroupManager;
import com.lighthouse.group.client.GroupNewsStore;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.client.ui.GroupPresenter;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.main.client.LhMain;
import com.lighthouse.main.newsitem.client.ui.LhNewsItemPresenter;
import com.lighthouse.report.client.ui.ReportsWidget;
import com.lighthouse.search.client.ui.SearchWidget;
import com.lighthouse.statistics.client.ui.FavoritesWidget;
import com.lighthouse.statistics.client.ui.NewsStatisticsWidget;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.NewsEvent;
import com.newscenter.client.events.NewsEventListener;
import com.newscenter.client.events.TagEvent;
import com.newscenter.client.news.NewsItemList;
import com.newscenter.client.tags.TagItem;
import com.newscenter.client.ui.ItemTagLabel;

/**
 * Represents complete list of news items loaded on news presenter
 * 
 * @author kiran@ensarm.com & prachi
 * 
 */
public class LhNewsPresenter extends Composite implements NewsEventListener,ClickHandler {

	private VerticalPanel basePanel = new VerticalPanel();
	private VerticalPanel newsPanel = new VerticalPanel();
	private VerticalPanel newsPresenterPanel = new VerticalPanel();
	private Label headerLabel = new Label();
	private HorizontalPanel labelPanel = new HorizontalPanel();

	private static ProgressIndicatorWidget loader = new ProgressIndicatorWidget(true);
	
	private HorizontalPanel searchPanel = new HorizontalPanel();
	private SearchWidget searchWidget;

	private LhNewsItemPresenter itempresenter;
	private LhNewsItemPresenter currentItemPresenter;

	private NewsItemList fullNewsItemList;
	private NewsItemList tagNewsItemList;
	private NewsItemList currentNewsItemList;

	private int pageSize;
	private Label backLabel = new Label("Back to latest stories");
	private HorizontalPanel backPanel = new HorizontalPanel();

	private PageCriteria pageCriteria;
	private PageCriteria fullModeCriteria = new PageCriteria();
	private PageCriteria singleModeCriteria = new PageCriteria();
	private GroupPageCriteria groupCriteria = new GroupPageCriteria();
	private GroupPageCriteria groupFullModeCriteria = new GroupPageCriteria();
	private GroupPageCriteria groupSingleModeCriteria = new GroupPageCriteria();

	private int mode;
	private HashMap<Integer, LhNewsItemPresenter> itemPresenterMap = new HashMap<Integer, LhNewsItemPresenter>();
	private HashMap<Integer, LhGroupedNewsItemPresenter> itemPresenterMap1 = new HashMap<Integer, LhGroupedNewsItemPresenter>();
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
	private Image separatorImg = new Image("images/verticalSeparator.JPG", 0,
			0, 6, 13);
	private Image backImg = new Image("images/back_icon.png");
	//private Image subscribeImg = new Image("images/envelope.png");
																		// here
	private int subscriptionPeriod;
	private int currentPage;
	private int currentTagNewsPage;

	private HashMap<Integer, Label> pageLabelMap = new HashMap<Integer, Label>();
	private HashMap<Integer, Label> tagPageLabelMap = new HashMap<Integer, Label>();
	private int lastVisiblePage;
	private static TagItem currentAppliedTagNewsViewed;
	private int requestedPage;
	private int requestedTagNewsPage;
	private int maxlimit = 9;
	private int minlimit = 10;
	private int newsMode;
	public static final int OR = 0;
	public static final int AND = 1;
	private static int backLinkClicked = 0;
	private int subscriptionMode;
	private GroupPresenter groupPresenter;

	private DockPanel dockPanel = new DockPanel();
	private VerticalPanel statsAndReportsPanel = new VerticalPanel();
	private LhUser lhUser;
	private final static String ALERTSETTINGS = "MAIL AND USER SETTINGS";
	
	public LhNewsPresenter(){
		
	}

	public LhNewsPresenter(GroupPresenter groupPresenter, LhUser lhUser) {
		this.groupPresenter = groupPresenter;
		this.lhUser = lhUser;
		AppEventManager.getInstance().addNewsEventListener(this);
		initWidget(basePanel);
		createUI();
	}

	public void createUI() {
		headerLabel.setText("No News to display");
		headerLabel.setStylePrimaryName("headerLabels");
		headerLabelPanel.add(headerLabel);
		headerLabelPanel.setWidth("100%");
		labelPanel.add(headerLabelPanel);
		labelPanel.setStylePrimaryName("newsPresenterHeader");
		labelPanel.setHeight("35px");
		labelPanel.setWidth("100%");

		FocusPanel focusPanel = new FocusPanel();
		focusPanel.addStyleName("clickable");
		focusPanel.setTitle("Click to manage tags");
		focusPanel.add(labelPanel);
		focusPanel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TagEvent tagEvent = new TagEvent(this, TagEvent.TAGPRESENTERMINIMIZED, null);
				AppEventManager.getInstance().fireEvent(tagEvent);
			}
		});
		
		Image nextim = new Image("images/arrow_more.gif", 0, -1, 16, 16);
		Image previm = new Image("images/arrow_less.gif", 0, 2, 16, 16);
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

		lbOld.addClickHandler(this);
		lbNew.addClickHandler(this);
		olderlinkpanel.setVisible(true);
		newerlinkpanel.setVisible(true);

		linksPanel.setCellSpacing(3);
		pagingPanel.add(newerlinkpanel);
		pagingPanel.add(linksPanel);
		pagingPanel.add(olderlinkpanel);

		pagingPanel.setCellHorizontalAlignment(newerlinkpanel,
				HasHorizontalAlignment.ALIGN_LEFT);
		pagingPanel.setCellVerticalAlignment(newerlinkpanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		pagingPanel.setCellHorizontalAlignment(linksPanel,
				HasHorizontalAlignment.ALIGN_LEFT);
		pagingPanel.setCellVerticalAlignment(linksPanel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		pagingPanel.setCellHorizontalAlignment(olderlinkpanel,
				HasHorizontalAlignment.ALIGN_LEFT);
		pagingPanel.setCellVerticalAlignment(olderlinkpanel,
				HasVerticalAlignment.ALIGN_MIDDLE);

		
		
		addSearchWidget();
		backPanel.add(searchPanel);

		backPanel.add(backImg);
		backPanel.add(backLabel);
		backPanel.add(separatorImg);
		//backPanel.add(subscribeImg);
		addMailAlertsLabel();

		backImg.setVisible(false);
		backLabel.setVisible(false);
		backLabel.setTitle("View all news stories");
		separatorImg.setVisible(false);

		basePanel.setCellHorizontalAlignment(searchPanel,
				HasHorizontalAlignment.ALIGN_RIGHT);
		/*backPanel.setCellVerticalAlignment(subscribeImg,
				HasVerticalAlignment.ALIGN_MIDDLE);*/
		
		backPanel.setCellVerticalAlignment(backImg,
				HasVerticalAlignment.ALIGN_MIDDLE);
		backPanel.setCellVerticalAlignment(backLabel,
				HasVerticalAlignment.ALIGN_MIDDLE);
		backPanel.setCellVerticalAlignment(separatorImg,
				HasVerticalAlignment.ALIGN_MIDDLE);
		backPanel.setSpacing(3);
		newsPanel.add(focusPanel);
		newsPanel.add(backPanel);
		newsPanel.add(loader);
		newsPanel.add(new HTML("<br>"));

		// new code
		newsPanel.add(dockPanel);
		dockPanel.add(newsPresenterPanel, DockPanel.CENTER);
		dockPanel.add(statsAndReportsPanel, DockPanel.EAST);
		dockPanel.setCellWidth(newsPresenterPanel, "80%");
		dockPanel.setCellWidth(statsAndReportsPanel, "20%");
		dockPanel.setWidth("100%");

		newsPanel.setCellHorizontalAlignment(backPanel,
				HasHorizontalAlignment.ALIGN_RIGHT);
		newsPanel.setCellHorizontalAlignment(loader,
				HasHorizontalAlignment.ALIGN_CENTER);
		newsPanel.setCellVerticalAlignment(loader,
				HasVerticalAlignment.ALIGN_MIDDLE);
		newsPanel.setCellHorizontalAlignment(dockPanel,
				HasHorizontalAlignment.ALIGN_CENTER);

		loader.setLoadingMessage("Fetching News...");
		loader.enable();
		
		newsPresenterPanel.setWidth("100%"); // added 14 oct 2011, nairutee
		newsPanel.setWidth("100%");
		basePanel.setWidth("100%");
		basePanel.add(newsPanel);
		basePanel.add(pagingPanel);
		newsPanel.setStylePrimaryName("newsPanel");
		pagingPanel.setStylePrimaryName("pagingPanel");
		basePanel.setSpacing(0);
	}
	
	/**
	 * adds the search widget to the search panel
	 */
	private void addSearchWidget(){
		try {
			searchWidget = new SearchWidget(lhUser.getUserPermission());
			searchWidget.setLhMainRef(GroupManager.getInstance().getLhMainRef());
			searchPanel.add(searchWidget);
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * add the mail and user settings label
	 */
	private void addMailAlertsLabel(){
		try {
			MailAlertsPanel mailAlertsLabel = new MailAlertsPanel(ALERTSETTINGS, lhUser.getUserPermission());
			backPanel.add(mailAlertsLabel);
			backPanel.setCellVerticalAlignment(mailAlertsLabel, HasVerticalAlignment.ALIGN_MIDDLE);
			String ma = Window.Location.getParameter("ma");
			if(ma != null)
				mailAlertsLabel.showPopup();
		} catch (FeatureNotSupportedException e) {
			e.printStackTrace();
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
					linksPanel.setWidget(0, lastpage,
							createPagingLabel(String.valueOf(lastpage)));
					setLastVisiblePage(lastpage);
					if (page == totalpages)
						olderlinkpanel.setVisible(false);
				}
			} else {
				int lastpage = getLastVisiblePage() + 1;
				linksPanel.removeCell(0, 1);
				int cellcnt = linksPanel.getCellCount(0);
				if (lastpage <= totalpages) {
					linksPanel.setWidget(0, cellcnt,
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
					linksPanel.setWidget(0, lastpage,
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
				linksPanel.removeCell(0, 1);
				int cellcnt = linksPanel.getCellCount(0);
				if (lastpage <= totalpages) {
					linksPanel.setWidget(0, cellcnt,
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
					int cellcnt = linksPanel.getCellCount(0);
					linksPanel.removeCell(0, cellcnt - 1);
					setLastVisiblePage(lastpage);
				}
			} else if (getMode() == SINGLESELECTIONMODE) {
				if (lvp > getRequestedTagNewsPage() + maxlimit) {
					int cellcnt = linksPanel.getCellCount(0);
					linksPanel.removeCell(0, cellcnt - 1);
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
					int cellcnt = linksPanel.getCellCount(0);
					linksPanel.removeCell(0, cellcnt - 1);
					setLastVisiblePage(lastpage);
				}
			} else if (getMode() == SINGLESELECTIONMODE) {
				if (lvp > getRequestedTagNewsPage() + maxlimit) {
					int cellcnt = linksPanel.getCellCount(0);
					linksPanel.removeCell(0, cellcnt - 1);
					setLastVisiblePage(lastpage);
				}
			}
			linksPanel.insertCell(0, 1);
			linksPanel.setWidget(0, 1, createPagingLabel(String.valueOf(no)));
		}
	}

	/**
	 * This method takes a NewsItemList and based on the pagesize and start
	 * index provides a sublist to the NewsItemPresenter to display.
	 * 
	 * @param list
	 *            - a NewsItemList of NewsItems
	 */
	public void initialize(NewsItemList list) {
		getNewsStatsAndReports();
		if (groupFullModeCriteria.getPageSize() == -1
				&& groupFullModeCriteria.getStartIndex() == -1) {
			groupFullModeCriteria.setPageSize(getGroupCriteria().getPageSize());
			groupFullModeCriteria.setStartIndex(getGroupCriteria()
					.getStartIndex());
			groupFullModeCriteria.setGroupId(getGroupCriteria().getGroupId());

		}
		if (groupSingleModeCriteria.getPageSize() == -1
				&& groupSingleModeCriteria.getStartIndex() == -1) {
			groupSingleModeCriteria.setPageSize(getGroupCriteria()
					.getPageSize());
			groupSingleModeCriteria.setStartIndex(getGroupCriteria()
					.getStartIndex());
			groupSingleModeCriteria.setGroupId(getGroupCriteria().getGroupId());
		}
		if (getCurrentItemPresenter() != null && getMode() == FULLSELECTIONMODE) {
			newsPresenterPanel.remove(getCurrentItemPresenter());
		}
		if (counter == 0 || getMode() == FULLSELECTIONMODE) {
			
			int start = getGroupCriteria().getStartIndex();
			int size = getGroupCriteria().getPageSize();
			setMode(FULLSELECTIONMODE);
			if (linksPanel.getRowCount() == 1)
				linksPanel.removeRow(0);
			linksPanel.setVisible(true);
			setCurrentPage(1);
			if (list.getTotalPages() > 10) {
				for (int i = 1; i <= 10; i++) {
					linksPanel.setWidget(0, i,
							createPagingLabel(String.valueOf(i)));
					setLastVisiblePage(i);
				}
				olderlinkpanel.setVisible(true);
				newerlinkpanel.setVisible(false);
			} else {
				for (int i = 1; i <= list.getTotalPages(); i++) {
					linksPanel.setWidget(0, i,
							createPagingLabel(String.valueOf(i)));
					setLastVisiblePage(i);
				}
				if (list.getTotalPages() <= 1)
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
			
				if(lhUser.getUserPermission().isPrimaryHeadLinePermitted() == 1){
					itempresenter = new LhGroupedNewsItemPresenter(lhUser); //new LhNewsItemPresenter();
					headerLabel.setText("Stories for:Primary Tags");
				}
				else{
					itempresenter = new LhNewsItemPresenter(lhUser);
					headerLabel.setText("Total: " + list.getNumItems() + " stories");
				}
				
				itempresenter.populateNewsItems(sublist);
			} else {
				itempresenter = new LhNewsItemPresenter(lhUser);
				headerLabel.setText("No news to display");
				newerlinkpanel.setVisible(false);
				linksPanel.setVisible(false);
				olderlinkpanel.setVisible(false);
			}
			setCurrentNewsItemList(list);
		
			newsPresenterPanel.add(	itempresenter);
			itemPresenterMap.put(FULLSELECTIONMODE, itempresenter);
			setCurrentItemPresenter(itempresenter);
			loader.disable();
			counter++;
		} else {
			setCurrentNewsItemList(list);
			loader.disable();
		}
		//RootPanel.get().setHeight("100%");
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
				newsPresenterPanel.remove(getCurrentItemPresenter());
			}
			itempresenter = new LhNewsItemPresenter(lhUser);
			int start = 0;
			int size = 0;
			start = getGroupFullModeCriteria().getStartIndex();
			size = getGroupFullModeCriteria().getPageSize();
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
				getGroupFullModeCriteria().setStartIndex(start);
				itempresenter.populateNewsItems(sublist);
			}
			setFullNewsItemList(list);
			newsPresenterPanel.add(itempresenter);
			itemPresenterMap.put(FULLSELECTIONMODE, itempresenter);
			setCurrentItemPresenter(itempresenter);
			loader.disable();
			setMode(FULLSELECTIONMODE);
			counter++;
		}

		if (counter == 0 || getMode() == SINGLESELECTIONMODE) {
			if (getCurrentItemPresenter() != null) {
				newsPresenterPanel.remove(getCurrentItemPresenter());
			}
			itempresenter = new LhNewsItemPresenter(lhUser);
			int start = 0;
			int size = 0;
			if (!isTagPresenterStatus()) {
				start = getGroupCriteria().getStartIndex();
				size = getGroupCriteria().getPageSize();
			} else {
				start = getGroupSingleModeCriteria().getStartIndex();
				size = getGroupSingleModeCriteria().getPageSize();
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
				getGroupSingleModeCriteria().setStartIndex(start);
				itempresenter.populateNewsItems(sublist);
			} else {
				headerLabel.setText("No news to display");
			}
			setTagNewsItemList(list);
			newsPresenterPanel.add(itempresenter);
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

	public void setNewsPresenterWidth(int width, int height) {
		basePanel.setPixelSize(width, height);
	}

	public void resetSize(int width, int height) {
		basePanel.setPixelSize(width, height);
		System.out.println("On resize newspresenter width= " + width);
	}

	@Override
	public boolean onEvent(NewsEvent evt) {
		int evttype = evt.getEventType();
		switch (evttype) {
		case (NewsEvent.NEWSARRIVED): {
			if (groupPresenter == evt.getSource()) {
				System.out.println("News arrived handled in lhnewspresenter");
				if (getMode() == SINGLESELECTIONMODE) {
					setMode(FULLSELECTIONMODE);
					groupFullModeCriteria.setStartIndex(0);
					setCurrentPage(1);
					setLastVisiblePage(1);
				}
				initialize((NewsItemList) evt.getEventData());
				return true;
			}
			return false;
		}
		case (NewsEvent.PAGEAVAILABLE): {
			if (groupPresenter == GroupManager.getActiveGroupPresenter()) {
				NewsItemList newslist = (NewsItemList) evt.getEventData();
				if (getMode() == SINGLESELECTIONMODE) {
					setMode(FULLSELECTIONMODE);
					if (linksPanel.getRowCount() == 1)
						linksPanel.removeRow(0);

					if (newslist.getTotalPages() > 10) {
						int currentpg = getCurrentPage();
						int col = 1;
						if (currentpg > 10) {
							for (int i = (currentpg - minlimit); i <= (currentpg + maxlimit); i++) {
								if (i <= newslist.getTotalPages()) {
									linksPanel
											.setWidget(0, col,
													createPagingLabel(String
															.valueOf(i)));
									setLastVisiblePage(i);
									col++;
								}
							}
						} else {
							for (int i = 1; i <= 10; i++) {
								linksPanel.setWidget(0, i,
										createPagingLabel(String.valueOf(i)));
								setLastVisiblePage(i);
							}
							if (currentpg != 1) {
								int pg = minlimit - getCurrentPage();
								while (getLastVisiblePage() < getCurrentPage()
										+ maxlimit) {
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
							linksPanel.setWidget(0, i,
									createPagingLabel(String.valueOf(i)));
							setLastVisiblePage(i);
						}
						if (newslist.getTotalPages() == 1
								|| newslist.size() == 0)
							olderlinkpanel.setVisible(false);
						else
							olderlinkpanel.setVisible(true);
						newerlinkpanel.setVisible(false);
					}
				}

				int curpage = getCurrentPage();
				Label lbl = pageLabelMap.get(curpage);
				if (lbl != null)
					lbl.addStyleName("selectedPageNoLabel");
				if (curpage != 1)
					newerlinkpanel.setVisible(true);

				int curtagpage = getCurrentTagNewsPage();
				if (curtagpage != 0) {
					if(!tagPageLabelMap.isEmpty()){
					Label lb = tagPageLabelMap.get(curtagpage);
					lb.removeStyleName("selectedPageNoLabel");
					}
				}
				if (currentPage == 1)
					newerlinkpanel.setVisible(false);
				if (currentPage == newslist.getTotalPages())
					olderlinkpanel.setVisible(false);

				newsPresenterPanel.remove(getCurrentItemPresenter());
				LhNewsItemPresenter newsitempresenter = new LhNewsItemPresenter(lhUser);
				newsitempresenter.populateNewsItems(newslist);
				setMode(FULLSELECTIONMODE);
				itemPresenterMap.put(getMode(), newsitempresenter);
				setCurrentItemPresenter(newsitempresenter);
				setCurrentNewsItemList(newslist);
				newsPresenterPanel.add(newsitempresenter);
				if (newslist.size() != 0)
					headerLabel.setText("Total: " + newslist.getNumItems()
							+ " stories");
				else
					headerLabel.setText("No news to display");
				backLinkClicked = 0;
				loader.disable();
				return true;
			}
			return false;

		}
		case (NewsEvent.ANDNEWSAVAILABLE): {
			if (groupPresenter == GroupManager.getActiveGroupPresenter()) {
				try {
					NewsItemList newslist = (NewsItemList) evt.getEventData();
					setMode(FULLSELECTIONMODE);
					if (linksPanel.getRowCount() == 1)
						linksPanel.removeRow(0);
					pageLabelMap.clear();
					if (newslist.size() != 0 || getCurrentPage() == 0)
						setCurrentPage(1);

					if (newslist.getTotalPages() > 10) {
						for (int i = 1; i <= 10; i++) {
							linksPanel.setWidget(0, i,createPagingLabel(String.valueOf(i)));
							setLastVisiblePage(i);
						}
						olderlinkpanel.setVisible(true);
						newerlinkpanel.setVisible(false);
					} else {
						for (int i = 1; i <= newslist.getTotalPages(); i++) {
							linksPanel.setWidget(0, i,
									createPagingLabel(String.valueOf(i)));
							setLastVisiblePage(i);
						}
						if (newslist.getTotalPages() <= 1)
							olderlinkpanel.setVisible(false);
						else
							olderlinkpanel.setVisible(true);
						newerlinkpanel.setVisible(false);
					}
					newsPresenterPanel.remove(getCurrentItemPresenter());
					LhNewsItemPresenter newsitempresenter = new LhNewsItemPresenter(lhUser);
					newsitempresenter.populateNewsItems(newslist);
					itemPresenterMap.put(FULLSELECTIONMODE, newsitempresenter);
					setCurrentItemPresenter(newsitempresenter);
					newsPresenterPanel.add(newsitempresenter);
					setCurrentNewsItemList(newslist);
					if (newslist.size() != 0)
						headerLabel.setText("Total: " + newslist.getNumItems()
								+ " stories");
					else
						headerLabel.setText("No news to display");
					loader.disable();
					//disableLoadingMessage();
					return true;
				} catch (Exception ex) {
					GWT.getUncaughtExceptionHandler().onUncaughtException(ex);
				}
				return true;
			}
			return false;
		}
		case (NewsEvent.TAGNEWSARRIVED): {
			if (groupPresenter == GroupManager.getActiveGroupPresenter()) {
				try {
					GroupManager.getInstance().getLhMainRef().showDeckWidget(0);
					NewsItemList newslist = (NewsItemList) evt.getEventData();
					 
					setMode(SINGLESELECTIONMODE);
					if (linksPanel.getRowCount() == 1)
					linksPanel.removeRow(0);
					tagPageLabelMap.clear();
					setCurrentTagNewsPage(1);
					if (newslist.getTotalPages() > 10) {
						for (int i = 1; i <= 10; i++) {
							linksPanel.setWidget(0, i,
									createPagingLabel(String.valueOf(i)));
							setLastVisiblePage(i);
						}
						olderlinkpanel.setVisible(true);
						newerlinkpanel.setVisible(false);
					} else {
						for (int i = 1; i <= newslist.getTotalPages(); i++) {
							linksPanel.setWidget(0, i,
									createPagingLabel(String.valueOf(i)));
							setLastVisiblePage(i);
						}
						if (newslist.getTotalPages() == 1)
							olderlinkpanel.setVisible(false);
						else
							olderlinkpanel.setVisible(true);
						newerlinkpanel.setVisible(false);
					}
					newsPresenterPanel.remove(getCurrentItemPresenter());
					LhNewsItemPresenter newsitempresenter = new LhGroupedNewsItemPresenter(lhUser);
					((LhGroupedNewsItemPresenter)newsitempresenter).setTagNews(true); // added 14 oct 2011, nairutee
					newsitempresenter.populateNewsItems(newslist);
					
					itemPresenterMap
							.put(SINGLESELECTIONMODE, newsitempresenter);
					setCurrentItemPresenter(newsitempresenter);
					newsPresenterPanel.add(newsitempresenter);
					setTagNewsItemList(newslist);
					loader.disable();
					return true;
				} catch (Exception ex) {
					GWT.getUncaughtExceptionHandler().onUncaughtException(ex);
				}
			}
			return false;
		}
		case (NewsEvent.NEWSAVAILABLE): {
			if (groupPresenter == GroupManager.getActiveGroupPresenter()) {
				try {
					NewsItemList newslist = (NewsItemList) evt.getEventData();
					int curpage = getCurrentTagNewsPage();
					Label lbl = tagPageLabelMap.get(curpage);
					lbl.addStyleName("selectedPageNoLabel");

					int page = getCurrentPage();
					if (page != 0) {
						if(!pageLabelMap.isEmpty()){
						Label lb = pageLabelMap.get(page);
						lb.removeStyleName("selectedPageNoLabel");
						}
					}

					if (currentTagNewsPage == 1)
						newerlinkpanel.setVisible(false);
					else if (currentTagNewsPage == getTagNewsItemList()
							.getTotalPages())
						olderlinkpanel.setVisible(false);

					newsPresenterPanel.remove(getCurrentItemPresenter());
					LhNewsItemPresenter newsitempresenter = new LhGroupedNewsItemPresenter(lhUser);
					((LhGroupedNewsItemPresenter)newsitempresenter).setTagNews(true); // added 14 oct 2011, nairutee
					newsitempresenter.populateNewsItems(newslist);
					setMode(SINGLESELECTIONMODE);
					itemPresenterMap.put(getMode(), newsitempresenter);
					setCurrentItemPresenter(newsitempresenter);
					newsPresenterPanel.add(newsitempresenter);
					setTagNewsItemList(newslist);
					loader.disable();
					return true;
				} catch (Exception ex) {
					GWT.getUncaughtExceptionHandler().onUncaughtException(ex);
				}
			}
			return false;
		}
		case (NewsEvent.NEWSTAGSELECTED): {
			if (groupPresenter == GroupManager.getActiveGroupPresenter()) {
				TagItem tag = (TagItem) evt.getEventData();
				setCurrentAppliedTagNewsViewed(tag);
				headerLabel.setText("Stories for: " + tag.getTagName());
				groupSingleModeCriteria.setPageSize(15);
				groupSingleModeCriteria.setStartIndex(0);
				GroupItemStore.getInstance(groupPresenter.getGroup().getGroupId())
						.getNews(tag, getGroupSingleModeCriteria());
				return true;
			}
			return false;
		}
		case (NewsEvent.NEWSDELETED): {
			LhMain.tagSelectionChanged = true;
			if (groupPresenter == GroupManager.getActiveGroupPresenter()) {
				NewsItemList newslist = (NewsItemList) evt.getEventData();
				if (newslist == null) {
					loader.disable();
					newsPresenterPanel.remove(getCurrentItemPresenter());
					headerLabel.setText("No News to display");
					linksPanel.setVisible(false);
					separatorImg.setVisible(false);
					backImg.setVisible(false);
					backLabel.setVisible(false);
					setMode(FULLSELECTIONMODE);
					groupFullModeCriteria.setStartIndex(0);
					if (olderlinkpanel.isVisible()
							|| newerlinkpanel.isVisible()) {
						olderlinkpanel.setVisible(false);
						newerlinkpanel.setVisible(false);
					}
					if (evt.getSource() instanceof ItemTagLabel) {
						GroupItemStore.getInstance(
								groupPresenter.getGroup().getGroupId())
								.setCurrentNewsCache(null);
						GroupNewsStore.getInstance(
								groupPresenter.getGroup().getGroupId())
								.setGlobalNewsItemList(null);
						GroupNewsStore.getInstance(
								groupPresenter.getGroup().getGroupId())
								.setGlobalNewsCache(null);
						GroupItemStore.getInstance(
								groupPresenter.getGroup().getGroupId())
								.clearSelection();
						TagEvent tagevt = new TagEvent(this,
								TagEvent.CLEARTAGS, null);
						AppEventManager.getInstance().fireEvent(tagevt);
					}
				} else {
					initialize(newslist);
				}
				return true;
			}
			return false;
		}
		case (NewsEvent.REFRESHNEWS): {
			if (groupPresenter == GroupManager.getActiveGroupPresenter()) {
				boolean bool = (Boolean) evt.getEventData();
				if (bool == true) {
					setTagPresenterStatus(true);
					if (getMode() == FULLSELECTIONMODE) {
						int currentindex = getGroupFullModeCriteria()
								.getStartIndex();
						int pagesize = getGroupFullModeCriteria().getPageSize();
						int value = (currentindex - pagesize);
						getGroupFullModeCriteria().setPageSize(
								getGroupCriteria().getMaxPageSize());
						getGroupFullModeCriteria().setStartIndex(value);
						refresh(getFullNewsItemList());
					} else if (getMode() == SINGLESELECTIONMODE) {
						int currentindex = getGroupSingleModeCriteria()
								.getStartIndex();
						int pagesize = getGroupSingleModeCriteria()
								.getPageSize();
						int value = (currentindex - pagesize);
						getGroupSingleModeCriteria().setPageSize(
								getGroupCriteria().getMaxPageSize());
						getGroupSingleModeCriteria().setStartIndex(value);
						refresh(getTagNewsItemList());
					}
				} else {
					setTagPresenterStatus(false);
					if (getMode() == FULLSELECTIONMODE) {
						int currentindex = getGroupFullModeCriteria()
								.getStartIndex();
						int pagesize = getGroupCriteria().getMinPageSize();
						int value = (currentindex - pagesize);
						getGroupFullModeCriteria().setPageSize(
								getGroupCriteria().getMinPageSize());
						;
						getGroupFullModeCriteria().setStartIndex(value);
						refresh(getFullNewsItemList());
					} else if (getMode() == SINGLESELECTIONMODE) {

					}
					setPageSize(getGroupCriteria().getMinPageSize());
				}
				return true;
			}
			return false;
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

	public HashMap<Integer, LhNewsItemPresenter> getItemPresenterMap() {
		return itemPresenterMap;
	}

	public void setItemPresenterMap(
			HashMap<Integer, LhNewsItemPresenter> itemPresenterMap) {
		this.itemPresenterMap = itemPresenterMap;
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

	public void setSubscribeLabelText(String text) {
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

	public void setCurrentAppliedTagNewsViewed(
			TagItem currentAppliedTagNewsViewed) {
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

		Widget arg0 = (Widget) event.getSource();
		if (arg0 instanceof Label) {
			Label label = (Label) arg0;
			if (label.getText().equals("Back to latest stories")) {
				//if (backLinkClicked == 0) {
					backLinkClicked = 1;
					loader.setLoadingMessage("Refreshing...");
					loader.enable();
					//setLoadingMessage("Refreshing...");
					backImg.setVisible(false);
					backLabel.setVisible(false);
					separatorImg.setVisible(false);
					/*GroupNewsStore.getInstance().getNewsPage(
							getGroupFullModeCriteria(), getNewsMode());*/
					GroupItemStore.getInstance().updateSessionCategoryMap();
				//}
			}else if (label.getText().equals("NEXT")) {
				NewsItemList sublist = new NewsItemList();
				if (getMode() == FULLSELECTIONMODE) {
					newerlinkpanel.setVisible(true);
					int page = getCurrentPage();
					Label lbl = (Label) pageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentPage(page + 1);
					int start = getGroupFullModeCriteria().getStartIndex();
					int size = getGroupFullModeCriteria().getPageSize();
					getGroupFullModeCriteria().setStartIndex(start + size);
					GroupNewsStore.getInstance().getNewsPage(
							getGroupFullModeCriteria(),GroupNewsStore.getInstance().getNewsmode());
					addInPagingPanel();
				} else if (getMode() == SINGLESELECTIONMODE) {
					newerlinkpanel.setVisible(true);
					int page = getCurrentTagNewsPage();
					Label lbl = tagPageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentTagNewsPage(page + 1);
					int start = getGroupSingleModeCriteria().getStartIndex();
					int size = getGroupSingleModeCriteria().getPageSize();
					getGroupSingleModeCriteria().setStartIndex(start + size);
					GroupNewsStore.getInstance().getNews(
							getCurrentAppliedTagNewsViewed(),
							getGroupSingleModeCriteria());
					addInPagingPanel();
				}
			} else if (label.getText().equals("PREVIOUS")) {
				olderlinkpanel.setVisible(true);
				NewsItemList sublist = new NewsItemList();

				if (getMode() == FULLSELECTIONMODE) {
					int page = getCurrentPage();
					Label lbl = pageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentPage(page - 1);
					setRequestedPage(page - 1);
					int start = getGroupFullModeCriteria().getStartIndex();
					int size = getGroupFullModeCriteria().getPageSize();
					
					/*int start = getGroupSingleModeCriteria().getStartIndex();
					int size = getGroupSingleModeCriteria().getPageSize();*/
					//getGroupSingleModeCriteria().setStartIndex(start - size);
					getGroupFullModeCriteria().setStartIndex(start - size);
					
					GroupNewsStore.getInstance().getNewsPage(
							getGroupFullModeCriteria(), GroupNewsStore.getInstance().getNewsmode());
					removeFromPagingPanel(page);
				} else if (getMode() == SINGLESELECTIONMODE) {
					int page = getCurrentTagNewsPage();
					Label lbl = tagPageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentTagNewsPage(page - 1);
					setRequestedTagNewsPage(page - 1);
					int start = getGroupSingleModeCriteria().getStartIndex();
					int size = getGroupSingleModeCriteria().getPageSize();
					getGroupSingleModeCriteria().setStartIndex(start - size);
					GroupNewsStore.getInstance().getNews(
							getCurrentAppliedTagNewsViewed(),
							getGroupSingleModeCriteria());
					removeFromPagingPanel(page);
				}
			} else {
				if (getMode() == FULLSELECTIONMODE) {
					int reqpage = Integer.parseInt(label.getText());
					setRequestedPage(reqpage);
					int page = getCurrentPage();
					int index = getGroupFullModeCriteria().getStartIndex();
					int startind = (15 * reqpage) - 15;
					getGroupFullModeCriteria().setStartIndex(startind);
					Label lbl = pageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentPage(reqpage);
					GroupNewsStore.getInstance().getNewsPage(
							getGroupFullModeCriteria(), GroupNewsStore.getInstance().getNewsmode());
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
					int index = getGroupSingleModeCriteria().getStartIndex();
					int startind = (15 * reqpage) - 15;
					getGroupSingleModeCriteria().setStartIndex(startind);
					Label lbl = tagPageLabelMap.get(page);
					lbl.removeStyleName("selectedPageNoLabel");
					setCurrentTagNewsPage(reqpage);
					GroupNewsStore.getInstance().getNews(
							getCurrentAppliedTagNewsViewed(),
							getGroupSingleModeCriteria());
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

	
	/**
	 * @author kiran@ensarm.com & Mahesh@ensarm.com
	 * gets the statistics data and news reports.
	 */
	private void getNewsStatsAndReports() {
		statsAndReportsPanel.clear();
		getReportsPanel();
		getFavoritesPanel();
		getNewsStatisticsPanel();
		statsAndReportsPanel.setHeight("100%");
		statsAndReportsPanel.setWidth("100%");
		DOM.setStyleAttribute(statsAndReportsPanel.getElement(), "marginLeft", "5px");
	}
	
	private void getReportsPanel(){
		try{
			ReportsWidget reportsWidget = new ReportsWidget(groupPresenter.getGroup(),getNewsMode(),getGroupCriteria(), lhUser.getUserPermission());
			statsAndReportsPanel.add(reportsWidget);
			statsAndReportsPanel.setCellHeight(reportsWidget, "40%");
		}catch (FeatureNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	private void getFavoritesPanel(){
		try{
			List<Group> groupList = GroupManager.getInstance().getUserGroupList();
			for(Group group : groupList){
				if(group.getIsFavorite() == 1){
					FavoritesWidget newsStatsWidget = new FavoritesWidget(group, lhUser.getUserPermission());
					if(LhMain.tagSelectionChanged){
						newsStatsWidget.getFavoriteItems(true);
					}else
						newsStatsWidget.getFavoriteItems(false);
					
					statsAndReportsPanel.add(newsStatsWidget);
					statsAndReportsPanel.setCellHeight(newsStatsWidget, "20%");
					break;
				}
			}
			
		}catch (FeatureNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	private void getNewsStatisticsPanel(){
		Group group = groupPresenter.getGroup();
		try{
			NewsStatisticsWidget newsStatsWidget = new NewsStatisticsWidget(group, lhUser.getUserPermission());
			if(LhMain.tagSelectionChanged)
				newsStatsWidget.refresh(GroupNewsStore.getInstance().getNewsmode());
			else
				newsStatsWidget.initialize();
			statsAndReportsPanel.add(newsStatsWidget);
			statsAndReportsPanel.setCellHeight(newsStatsWidget, "40%");
		}catch (FeatureNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	
	public GroupPageCriteria getGroupCriteria() {
		return groupCriteria;
	}

	public void setGroupCriteria(GroupPageCriteria groupCriteria) {
		this.groupCriteria = groupCriteria;
	}

	public GroupPageCriteria getGroupFullModeCriteria() {
		return groupFullModeCriteria;
	}

	public void setGroupFullModeCriteria(GroupPageCriteria groupFullModeCriteria) {
		this.groupFullModeCriteria = groupFullModeCriteria;
	}

	public GroupPageCriteria getGroupSingleModeCriteria() {
		return groupSingleModeCriteria;
	}

	public void setGroupSingleModeCriteria(
			GroupPageCriteria groupSingleModeCriteria) {
		this.groupSingleModeCriteria = groupSingleModeCriteria;
	}

	public LhNewsItemPresenter getCurrentItemPresenter() {
		return currentItemPresenter;
	}

	public void setCurrentItemPresenter(LhNewsItemPresenter currentItemPresenter) {
		this.currentItemPresenter = currentItemPresenter;
	}

	public static ProgressIndicatorWidget getLoader() {
		return loader;
	}

}