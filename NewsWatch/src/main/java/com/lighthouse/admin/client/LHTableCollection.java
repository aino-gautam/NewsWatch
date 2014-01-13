package com.lighthouse.admin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.admin.client.AdminRegistrationInformation;
import com.admin.client.ApprovedUsers;
import com.admin.client.NewsItems;
import com.admin.client.TagItemInformation;
import com.common.client.UserHistoryPanel;
import com.common.client.UserItemAccessWidget;
import com.common.client.UserLogStats;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.lighthouse.admin.client.domain.AdminReportItem;
import com.lighthouse.common.client.LHFilterPopup;
import com.lighthouse.common.client.NewsLetterAccessStats;
import com.lighthouse.newsletter.client.domain.NewsLetterStats;
import com.trial.client.ExistingTrialAccounts;
import com.trial.client.TrialAccount;
import com.trial.client.TrialToSubscribeUser;

public class LHTableCollection extends FlexTable implements ClickListener,ClickHandler,ChangeHandler,MouseOverHandler{
	public static final String STYLE_TABLE="GridTable";
	public static final String STYLE_HEADER="GridHeader";
	public static final String STYLE_ROW_HOVER="GridHover";
	private HashMap itemMap = new HashMap();
	private HashMap checkBoxMap = new HashMap();
	String strduration;
	String newsCenterName="";
	private HashMap<Long, TextBox> textBoxMap = new HashMap<Long, TextBox>();
	String industryName="";
	public static boolean flag = false;
	public static boolean f = false;
	private Label lblprimTag;
	private LHManageTags manageTagsRef;
	private LHManageCategories manageCategoriesRef;
	private ArrayList<TagItemInformation> tagArray = new ArrayList<TagItemInformation>();

	private String hoverStyle = null;
	
	private UserLogStats userLogsStatsRef;
	private UserItemAccessWidget userItemAccessRef;
	private NewsLetterAccessStats newsLetterStats;
	private HashMap<Image , String> mapOfImageVsColumnName = new HashMap<Image, String>();
	private DecoratedPopupPanel columnHeaderPopup	= new DecoratedPopupPanel(true);
	private LHFilterPopup filterpopup;
	private String hoverColumnName;
	private boolean hasHeader = false;
	private int hoverColumn;
	private Boolean rowExpand = false;

	public NewsLetterAccessStats getNewsLetterStats() {
		return newsLetterStats;
	}

	public void setNewsLetterStats(NewsLetterAccessStats newsLetterStats) {
		this.newsLetterStats = newsLetterStats;
	}

	public UserLogStats getUserLogsStatsRef() {
		return userLogsStatsRef;
	}

	public void setUserLogsStatsRef(UserLogStats userLogsStatsRef) {
		this.userLogsStatsRef = userLogsStatsRef;
	}
	
	public UserItemAccessWidget getUserItemAccessRef() {
		return userItemAccessRef;
	}

	public void setUserItemAccessRef(UserItemAccessWidget userItemAccessRef) {
		this.userItemAccessRef = userItemAccessRef;
	}
	
	public String getHoverColumnName() {
		return hoverColumnName;
	}

	public void setHoverColumnName(String hoverColumnName) {
		this.hoverColumnName = hoverColumnName;
	}
	public int getHoverColumn() {
		return hoverColumn;
	}

	public void setHoverColumn(int hoverColumn) {
		this.hoverColumn = hoverColumn;
	}
	
	public LHManageCategories getManageCategoriesRef() {
		return manageCategoriesRef;
	}

	public void setManageCategoriesRef(LHManageCategories manageCategoriesRef) {
		this.manageCategoriesRef = manageCategoriesRef;
	}
	
	public String getHoverStyleName() {
	    return hoverStyle;
	}
	
	public void setHoverStyleName(String style) {
		hoverStyle = style;
	}
		
	public HashMap getItemMap() {
		return itemMap;
	}

	public void setItemMap(HashMap itemMap) {
		this.itemMap = itemMap;
	}
	
	public LHManageTags getManageTagsRef() {
		return manageTagsRef;
	}

	public void setManageTagsRef(LHManageTags manageTagsRef) {
		this.manageTagsRef = manageTagsRef;
	}
	public DecoratedPopupPanel getColumnHeaderPopup() {
		return columnHeaderPopup;
	}

	public void setColumnHeaderPopup(DecoratedPopupPanel columnHeaderPopup) {
		this.columnHeaderPopup = columnHeaderPopup;
	}
	
	private void showColumnHeaderPopup(MouseOverEvent event) {
		// TODO Auto-generated method stub
		columnHeaderPopup.hide();
		Widget source = (Widget) event.getSource();
		int left = source.getAbsoluteLeft();
		int top = source.getAbsoluteTop() + 10;
		columnHeaderPopup.setPopupPosition(left, top);
		columnHeaderPopup.show();	
	 }
	 
	public LHTableCollection(){
		industryName = LHadmin.getIndustryName();
		setStylePrimaryName(STYLE_TABLE);
		sinkEvents(Event.ONMOUSEOVER);
		sinkEvents(Event.ONMOUSEOUT);
		setCellPadding(5);
		setCellSpacing(0);
		setWidth("95%");
	}
	
	public LHTableCollection(LHManageTags managetagsref){
		industryName = LHadmin.getIndustryName();
		setStylePrimaryName(STYLE_TABLE);
		setHoverStyleName(STYLE_ROW_HOVER);
		sinkEvents(Event.ONMOUSEOVER);
		sinkEvents(Event.ONMOUSEOUT);
		setCellPadding(5);
		setCellSpacing(0);
		setWidth("95%");
		setManageTagsRef(managetagsref);
	}
	
	public LHTableCollection(LHManageCategories managecategoriesref){
		industryName = LHadmin.getIndustryName();
		setStylePrimaryName(STYLE_TABLE);
		setHoverStyleName(STYLE_ROW_HOVER);
		sinkEvents(Event.ONMOUSEOVER);
		sinkEvents(Event.ONMOUSEOUT);
		setCellPadding(5);
		setCellSpacing(0);
		setWidth("95%");
		setManageCategoriesRef(managecategoriesref);
	}
	
	
	public LHCheckBox createCheckbox(Long id){
		LHCheckBox checkbox = new LHCheckBox();
		checkbox.setId(id);
		checkbox.addClickHandler(this);
		checkBoxMap.put(id, checkbox);
		return checkbox;
	}
	
	public void disableCheckBox(HashMap map){
		for(Object o : map.keySet()){
			LHCheckBox checkBox = (LHCheckBox)checkBoxMap.get(o);
			checkBox.setEnabled(false);
		}
	}
	
	public Label createLabel(String text){
	   Label label = new Label(text);
	   label.setStylePrimaryName("labelTitle");
	   return label;
   }
	
	public Label createLeftLabel(String text){
		   Label label = new Label(text);
		   label.setStylePrimaryName("labelLeftTitle");
		   return label;
	}
	
	private Label createBoldLabel(String text){
		Label lbl = new Label(text);
		DOM.setStyleAttribute(lbl.getElement(), "fontSize", "12px");
		DOM.setStyleAttribute(lbl.getElement(), "fontWeight", "bold");
		return lbl;
	}
	
	public DockPanel createLabelWithImage(String text){
	 	   DockPanel dckHeader = new DockPanel();
	 	   dckHeader.setSpacing(7);
	 	   Image imgDown = new Image("images/trial/downarrow1.png");
	 	   imgDown.setStyleName("ImageDown-HeaderStyle");
	 	   imgDown.addMouseOverHandler(this);
	 	   
	 	   dckHeader.add(createLabel(text), DockPanel.WEST);
	 		
	 	   dckHeader.add(imgDown, DockPanel.EAST);
	 	   imgDown.setStyleName("imgDownColumn");
	 	   dckHeader.setCellHorizontalAlignment(imgDown, DockPanel.ALIGN_RIGHT);
	 	   dckHeader.setCellVerticalAlignment(imgDown, DockPanel.ALIGN_MIDDLE);
	 	   		
	 	   dckHeader.setStyleName("dockPanelHeader");
	 	   mapOfImageVsColumnName.put(imgDown, text);
	 	   return dckHeader;
	    }
	
	public TextBox createEditTextBox(int id, String tagName){
		   TextBox textbox = new TextBox();
		   textbox.setText(tagName);
		   textbox.addChangeHandler(this);
		   textbox.setTabIndex(id);
		   textbox.setWidth("150px");
		   textBoxMap.put((long) id, textbox);
		   return textbox;
	}
	
	public void addNewsItemFieldsInTabel(ArrayList list){
		this.setWidth("100%");
	   setWidget(1,1,createLabel("TITLE"));
	   setWidget(1,2,createLabel("ABSTRACT"));
	   setWidget(1,3,createLabel("CONTENT"));
	   setWidget(1,4,createLabel("URL"));
	   setWidget(1,5,createLabel("DATE"));
	   FlexCellFormatter formatter = getFlexCellFormatter();
	   Iterator iter = list.iterator();
	   int row=2;
	   LHCheckBox checkBox = new LHCheckBox();
	   while(iter.hasNext()){
		   NewsItems news = (NewsItems)iter.next();
		   Long newsitemId = (long) news.getNewsId();
		   String title = news.getNewsTitle();
		   String abstractnews = news.getAbstractNews();
		   String content = news.getNewsContent();
		   String url= news.getUrl();
		   String date = news.getNewsDate().toString();

		   checkBox = new LHCheckBox();
		   checkBox.setId(newsitemId);
		   checkBox.addClickHandler(this);
		   checkBoxMap.put(newsitemId, checkBox);
		   
		   setWidget(row,0,checkBox);
		   formatter.setWidth(row, 0, "10px");
		   
		   formatter.setWidth(row, 1, "35%");
		   setText(row,1,title);
		   
		   formatter.setWidth(row, 2, "40%");
		   setText(row,2,abstractnews );
		   
		   formatter.setWidth(row, 3, "80px");
		   setText(row,3,content);
		   
		   formatter.setWidth(row, 4, "50px");
		   setText(row,4,url);
		   
		   formatter.setWidth(row, 5, "40px");
		   setText(row,5,date);
		   row++;
		}
	}
	
	public void addReportFieldsInTabel(ArrayList list){
		this.removeAllRows();
		this.setBorderWidth(1);
		this.setWidth("100%");
	   setWidget(1,1,createLabel("TITLE"));
	   setWidget(1,2,createLabel("ABSTRACT"));
	   setWidget(1,3,createLabel("MIME TYPE"));
	   setWidget(1,4,createLabel("DATE"));
	   setWidget(1,5,createLabel("LIFE SPAN"));
	   FlexCellFormatter formatter = getFlexCellFormatter();
	   if (list.size()!=0) {
			Iterator iter = list.iterator();
			LHCheckBox checkBox = new LHCheckBox();
			int row = 2;
			while (iter.hasNext()) {
				AdminReportItem report = (AdminReportItem) iter.next();
				Long newsitemId = (long) report.getNewsId();
				String title = report.getNewsTitle();
				String abstractnews = report.getAbstractNews();
				String mimeType = report.getReportMimeType();
				String date = report.getNewsDate().toString();
				String lifeSpan = report.getReportLifeSpan();
	
				checkBox = new LHCheckBox();
				checkBox.setId(newsitemId);
				checkBox.addClickHandler(this);
				checkBoxMap.put(newsitemId, checkBox);
	
				formatter.setWidth(row, 0, "50px");
				setWidget(row, 0, checkBox);
	
				formatter.setWidth(row, 1, "35%");
				setText(row, 1, title);
	
				formatter.setWidth(row, 2, "40%");
				setText(row, 2, abstractnews);
	
				formatter.setWidth(row, 3, "80px");
				setText(row, 3, mimeType);
	
				formatter.setWidth(row, 4, "50px");
				setText(row, 4, date);
	
				formatter.setWidth(row, 5, "50px");
				setText(row, 5, lifeSpan);
				row++;
			}
	   }
	}
	
	public void addTagsinTable(HashMap<Integer, TagItemInformation> hashMap, boolean readMode){
		this.clear();
		tagArray = getManageTagsRef().getAllTagsArray();
		setWidget(1, 1, createLabel("Tag Name"));
		int row = 2;
		int col = 0;
		for(Object obj : hashMap.keySet()){
			TagItemInformation tag= hashMap.get(obj);
			
			if(readMode == true)
			{
				setWidget(row, col+1, createEditTextBox(tag.getTagItemId(), tag.getTagName()));
				if(tag.isIsprimary())
					setWidget(row, col+3,new Label("* Primary Tag"));
			}
			else{
				setWidget(row, col, createCheckbox((long) tag.getTagItemId()));
				
				setText(row, col+1, tag.getTagName());
				if(tag.isIsprimary())
					setWidget(row, col+3,new Label("* Primary Tag"));
			}
			row++;
		}
	}
	
	public void setTagLabel(Boolean val)
	{
		lblprimTag.setVisible(val);
	}
	
	@Override
	public void onChange(ChangeEvent event){
		// TODO Auto-generated method stub
		
	}

	public void onClick(ClickEvent event){
		Widget sender = (Widget)event.getSource();
		String str= "";
		Long index = 0L;
		String strdurationvalue="";
		LHCheckBox checkbox = new LHCheckBox();
		if(sender  instanceof LHCheckBox) {
			checkbox = (LHCheckBox)sender;
			index = checkbox.getId();
			TextBox textbox = textBoxMap.get(index);
			
			if(checkbox.isChecked()){
				itemMap.put(index, str);
			}
			else{
				itemMap.remove(index);
				if(itemMap.size() == 0){
				}
			}
		}
	}
	public void addDataInNewsLetterStats(ArrayList<NewsLetterStats> list){
    	setWidget(0, 0, createLabelWithImage("Name"));
    	setWidget(0, 2, createLabelWithImage("email"));
    	setWidget(0, 4, createLeftLabel("NewsLetter Sent On"));
    	setWidget(0, 6, createLeftLabel("NewsLetter Accessed On"));
    	
    	int row = 2;
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
		if(list.size() > 0){
			Iterator itr = list.iterator();
			while(itr.hasNext()){
				NewsLetterStats newsLetterstats = (NewsLetterStats)itr.next();
				String str = newsLetterstats.getNewsSentDate();
				if(str.contains(".0"))
					str = newsLetterstats.getNewsSentDate().replace(".0", "");
				setText(row,0, newsLetterstats.getFirstName());
				setText(row,2, newsLetterstats.getEmail());
				setText(row,4, str);
				setText(row,6, dtf.format(newsLetterstats.getNewsOpenedDate()));
				
				getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 2, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 6, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				row++;
			}
		}
		else{
			 setWidget(row, 0, createBoldLabel("No records found"));
			   getFlexCellFormatter().setColSpan(row, 0, 5);
		}
		getFlexCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		getFlexCellFormatter().setAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		getFlexCellFormatter().setAlignment(0, 4, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		getFlexCellFormatter().setAlignment(0, 6, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
	}
	
	@Override
	public void onMouseOver(MouseOverEvent arg0) {
		    columnHeaderPopup.clear();
	    	filterpopup = new LHFilterPopup(this);
	    	
	    	 for(Image img: mapOfImageVsColumnName.keySet()){
			    	if(arg0.getSource().equals(img)){
			    		String colname = mapOfImageVsColumnName.get(img);
			    		if(colname.equals("Name") || colname.equals("email") || colname.equals("accessedOn") || colname.equals("sentOn")){
			    			filterpopup.getImgFilter().setVisible(true);
			    			filterpopup.getLblFilter().setVisible(true);	
			    		}
			    		else{
			    			filterpopup.getImgFilter().setVisible(false);
			    		    filterpopup.getLblFilter().setVisible(false);
			    		}
			    	}
			    }
	    	columnHeaderPopup.add(filterpopup);
			showColumnHeaderPopup(arg0);
	}
	
	public void mouseLeavesRow(Widget sender, int row) {
	       if(hasHeader && row==0)
	           return;
	       if (hoverStyle != null)
	           this.getRowFormatter().removeStyleName(row, hoverStyle);
	}
	
	public void mouseEntersRow(Widget sender, int row) {
	       if(hasHeader && row==0)
	           return;
	       if (hoverStyle != null && !rowExpand && row != 0)
	           this.getRowFormatter().addStyleName(row, hoverStyle);
	}
	
	public void onBrowserEvent(Event event) {
	       switch (DOM.eventGetType(event)) {
		       case Event.ONMOUSEOVER: {
		           // Find out which cell was entered.
		           Element td = getEventTargetCell(event);
		           if (td == null)
		               return;
		           Element tr = DOM.getParent(td);
		           if (tr == null)
		               return;
		           Element body = getBodyElement();
		           int row = DOM.getChildIndex(body, tr);
		           // Fire the event
		           setHoverColumn(DOM.getChildIndex(tr,td));
		           setHoverColumnName(DOM.getInnerText(td));
		           mouseEntersRow(this, row);
		           break;
		       }
		       case Event.ONMOUSEOUT: {
		           // Find out which cell was exited.
		           Element td = getEventTargetCell(event);
		           if (td == null)
		               return;
		           Element tr = DOM.getParent(td);
		           if (tr == null)
		               return;
		           Element body = getBodyElement();
		           int row = DOM.getChildIndex(body, tr);
		           // Fire the event.
		           mouseLeavesRow(this, row);
		           break;
		       }
	       }
	       super.onBrowserEvent(event);
	   }
	 
	@Override
	public void onClick(Widget sender) {
		// TODO Auto-generated method stub
	}
}