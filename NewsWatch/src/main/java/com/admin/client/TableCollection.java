package com.admin.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import com.common.client.FilterPopup;
import com.common.client.PopUpForForgotPassword;
import com.common.client.UserHistoryPanel;
import com.common.client.UserItemAccessStats;
import com.common.client.UserItemAccessWidget;
import com.common.client.UserLogStats;
import com.common.client.UserLoginStats;
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
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.trial.client.ExistingTrialAccounts;
import com.trial.client.TrialAccount;
import com.trial.client.TrialToSubscribeUser;

public class TableCollection extends FlexTable implements ClickListener,ClickHandler,ChangeHandler,MouseOverHandler{
	private String hoverStyle = null;
	public static final String STYLE_TABLE="GridTable";
	public static final String STYLE_HEADER="GridHeader";
	public static final String STYLE_ROW_HOVER="GridHover";
	private String strName = new String("Name");
	private String strcompany = new String("Industry");
	private boolean hasHeader = false;
	private HashMap itemMap = new HashMap();
	private HashMap checkBoxMap = new HashMap();
	private HashMap extendedDurationMap = new HashMap();
	private DecoratedPopupPanel columnHeaderPopup	= new DecoratedPopupPanel(true);
		
	//private HashMap durationMap = new HashMap();
	String strduration;
	String newsCenterName="";
	private HashMap<Integer, TextBox> textBoxMap = new HashMap<Integer, TextBox>();
	private HashMap<Integer, TagItemInformation> updatedTextBoxMap = new HashMap<Integer, TagItemInformation>();
	private Collection allTagList;
	private ArrayList allCatList = new ArrayList();
	private ArrayList<TagItemInformation> tagArray = new ArrayList<TagItemInformation>();
	String industryName="";
	private ArrayList<String> list = new ArrayList<String>();
	private Collection c;
	private TagItemInformation tagiteminfo = new TagItemInformation();
	private boolean currentCategoryFlag = false;
	private boolean currentTagFlag = false;
	public static boolean flag = false;
	public static boolean f = false;
	private ManageTags manageTagsRef;
	private ManageCategories manageCategoriesRef;
	private ApprovedUsers approvedUsersRef;
	private String hoverColumnName;
	private ExistingTrialAccounts existingTrialAccountsRef;
	private UserLogStats userLogsStatsRef;
	private UserItemAccessWidget userItemAccessRef;
    private FilterPopup filterpopup;
    private HashMap<Image , String> mapOfImageVsColumnName = new HashMap<Image, String>();
    private int hoverColumn;
    private Boolean rowExpand = false;
   // protected String tableCollectionWidth;
		
	public ApprovedUsers getApprovedUsersRef() {
		return approvedUsersRef;
	}

	public void setApprovedUsersRef(ApprovedUsers approvedUsersRef) {
		this.approvedUsersRef = approvedUsersRef;
	}

	public TableCollection(){
		industryName = AdminPage.getIndustryName();
		setStylePrimaryName(STYLE_TABLE);
		setHoverStyleName(STYLE_ROW_HOVER);
		sinkEvents(Event.ONMOUSEOVER);
		sinkEvents(Event.ONMOUSEOUT);
		setCellPadding(5);
		setCellSpacing(0);
		setWidth("95%");
	}
	
	public TableCollection(ManageTags managetagsref){
		industryName = AdminPage.getIndustryName();
		setStylePrimaryName(STYLE_TABLE);
		setHoverStyleName(STYLE_ROW_HOVER);
		sinkEvents(Event.ONMOUSEOVER);
		sinkEvents(Event.ONMOUSEOUT);
		setCellPadding(5);
		setCellSpacing(0);
		setWidth("95%");
		setManageTagsRef(managetagsref);
	}
	
	public TableCollection(ManageCategories managecategoriesref){
		industryName = AdminPage.getIndustryName();
		setStylePrimaryName(STYLE_TABLE);
		setHoverStyleName(STYLE_ROW_HOVER);
		sinkEvents(Event.ONMOUSEOVER);
		sinkEvents(Event.ONMOUSEOUT);
		setCellPadding(5);
		setCellSpacing(0);
		setWidth("95%");
		setManageCategoriesRef(managecategoriesref);
	}

	public String getHoverStyleName() {
       return hoverStyle;
	}

	public void setHoverStyleName(String style) {
		hoverStyle = style;
	}

	public SpecializedLink createLink(String text, AdminRegistrationInformation user, int id){
		SpecializedLink link = new SpecializedLink();
		link.setText(text);
		if(text.equals("delete"))
			link.setAssociatedExtendLink((SpecializedLink)getWidget(id, 6));
		link.addClickListener(this);
		link.setId(id);
		link.setUserRef(user);
		return link;
	}
	public CheckBox createCheckbox(int id){
		CheckBox checkbox = new CheckBox();
		checkbox.setTabIndex(id);
		//checkbox.addClickListener(this);
		checkbox.addClickHandler(this);
		checkBoxMap.put(id, checkbox);
		return checkbox;
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
	
   public void addInTable(ArrayList list){
	   sortUsers(list);
	   setWidget(1,1,createLabel("Name"));
	   setWidget(1,2,createLabel("e-mail"));
	   setWidget(1,3,createLabel("Phone Number"));
	   setWidget(1,4,createLabel("Company"));
	   setWidget(1,5,createLabel("Duration"));
	   Iterator iter = list.iterator();
		int row=2;
		if(list.size() != 0){
		while(iter.hasNext()){
			AdminRegistrationInformation user = (AdminRegistrationInformation)iter.next();
			int id = user.getUserid();
			String userName = user.getName()+" "+user.getLastname();
			String email = user.getEmail();
			long phone = user.getPhoneno();
			String company = user.getCompanynm();
			
			setWidget(row, 0, createCheckbox(id));
			setText(row,1, userName);
			setText(row, 2, email);
			setText(row,3, String.valueOf(phone));
			setText(row, 4, company);
			setWidget(row, 5, createTextBox(id));
			row++;
		 }
		}
		else{
			setWidget(row, 0, createBoldLabel("No records found"));
			getFlexCellFormatter().setColSpan(row, 0, 6);
		}
   }

   //new
   public void addApprovedUsers(ArrayList list){
	   sortUsers(list);
	   FlexCellFormatter formatter = getFlexCellFormatter();
	   
	   setWidget(0,0,createLabelWithImage("Name"));
	   setWidget(0,1,createLabelWithImage("email"));
	   setWidget(0,2,createLabel("Phone No"));
	   setWidget(0,3,createLabelWithImage("Company"));
	   formatter.setHorizontalAlignment(1, 4, HasHorizontalAlignment.ALIGN_CENTER);
	   setWidget(0,4,createLabel("Total Duration"));
	   formatter.setHorizontalAlignment(1, 5, HasHorizontalAlignment.ALIGN_CENTER);
	   setWidget(0,5,createLabel("Duration Left"));
	   int row = 2;
	   if(list.size() > 0){
	   Iterator iter = list.iterator();
	   while(iter.hasNext()){
		   AdminRegistrationInformation user = (AdminRegistrationInformation)iter.next();
		   int id = user.getUserid();
		   String userName = user.getName()+" "+user.getLastname();
		   String email = user.getEmail();
		   long phone = user.getPhoneno();
		   String company = user.getCompanynm();
		   int isapproved = user.getIsApproved();
		   int duration = user.getDuration();
		   int durationleft = user.getDurationLeft();
		   String durationUser = Integer.toString(duration);
		   String userdurationleft = Integer.toString(durationleft);
		   if(isapproved == 1){
			   setText(row,0, userName);
			   setText(row,1, email);
			   setText(row,2, String.valueOf(phone));
			   setText(row,3,company);
			   formatter.setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
			   setText(row,4, durationUser);
			   formatter.setHorizontalAlignment(row, 5, HasHorizontalAlignment.ALIGN_CENTER);
			   setText(row,5, userdurationleft);
			   setWidget(row, 6, createLink("extend", user, row));
			   if(durationleft == 0)
				   setWidget(row, 7, createLink("delete", user, row));
			   row++;
		   }
	    }
	   }
	   else{
		   setWidget(row, 0, createBoldLabel("No records found"));
		   getFlexCellFormatter().setColSpan(row, 0, 8);
	   }
	   
   }
   
   public void addInTableforManageUser(ArrayList list){
	   sortUsers(list);
	   FlexCellFormatter formatter = getFlexCellFormatter();
	   
	   setWidget(1,1,createLabel("Name"));
	   setWidget(1,2,createLabel("email"));
	   setWidget(1,3,createLabel("Phone No"));
	   setWidget(1,4,createLabel("Company"));
	   formatter.setHorizontalAlignment(1, 5, HasHorizontalAlignment.ALIGN_CENTER);
	   setWidget(1,5,createLabel("Total Duration"));
	   formatter.setHorizontalAlignment(1, 6, HasHorizontalAlignment.ALIGN_CENTER);
	   setWidget(1,6,createLabel("Duration Left"));
	   formatter.setHorizontalAlignment(1, 7, HasHorizontalAlignment.ALIGN_CENTER);
	   setWidget(1,7,createLabel("Approval Status"));
	   Iterator iter = list.iterator();
	   int row = 2;
	   if(list.size() != 0){
	   while(iter.hasNext()){
			String approval = "Not Approved";
			AdminRegistrationInformation user = (AdminRegistrationInformation)iter.next();
			int id = user.getUserid();
			String userName = user.getName()+" "+user.getLastname();
			String email = user.getEmail();
			long phone = user.getPhoneno();
			String company = user.getCompanynm();
			int isapproved = user.getIsApproved();
			int duration = user.getDuration();
			int durationleft = user.getDurationLeft();
			String durationUser = Integer.toString(duration);
			String userdurationleft = Integer.toString(durationleft);
			if(isapproved == 1){
				approval = "Approved";
			}
			else
				approval = "Not Approved";
			if(company.equals("")){
				company ="pending to approve";
			}
			setWidget(row, 0, createCheckbox(id));
			setText(row,1, userName);
			setText(row,2, email);
			setText(row,3, String.valueOf(phone));
			setText(row,4,company);	
			formatter.setHorizontalAlignment(row, 5, HasHorizontalAlignment.ALIGN_CENTER);
			setText(row,5, durationUser);
			formatter.setHorizontalAlignment(row, 6, HasHorizontalAlignment.ALIGN_CENTER);
			setText(row,6, userdurationleft);
			formatter.setHorizontalAlignment(row, 7, HasHorizontalAlignment.ALIGN_CENTER);
			setText(row,7, approval);
			
			row++;
		}
	   }
	   else{
		   setWidget(row, 0, createBoldLabel("No records found"));
		   getFlexCellFormatter().setColSpan(row, 0, 8);
	   }
   }

   public void sortUsers(ArrayList list){
		final Comparator<AdminRegistrationInformation> NAME_ORDER = new Comparator<AdminRegistrationInformation>(){

			public int compare(AdminRegistrationInformation u1, AdminRegistrationInformation u2) {
				
				return u1.getName().compareTo(u2.getName());
			}
		};
		
		Collections.sort(list,NAME_ORDER);
	}
   
   public TextBox createTextBox(int id){
	   TextBox textbox = new TextBox();
	   String str = Integer.toString(id);
	   textbox.setName(str);
	   textBoxMap.put(id, textbox);
	   return textbox;
   }

   public TextBox createDurationLeftTextBox(AdminRegistrationInformation user, int value){
	   TextBox textbox = new TextBox();
	   String str = Integer.toString(user.getUserid());
	   textbox.setName(str);
	   textbox.setText(String.valueOf(value));
	   extendedDurationMap.put(user, textbox);
	   textBoxMap.put(user.getUserid(), textbox);
	   textbox.setSize("50px", "15px");
	   return textbox;
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

   public void mouseEntersRow(Widget sender, int row) {
       if(hasHeader && row==0)
           return;
       if (hoverStyle != null && !rowExpand && row != 0)
           this.getRowFormatter().addStyleName(row, hoverStyle);
   }

   public void mouseLeavesRow(Widget sender, int row) {
       if(hasHeader && row==0)
           return;
       if (hoverStyle != null)
           this.getRowFormatter().removeStyleName(row, hoverStyle);
   }

	public void onClick(Widget sender) {
		String str= "";
		String strdurationvalue="";
		if(sender  instanceof CheckBox) {
			CheckBox checkbox = (CheckBox)sender;
			int index = checkbox.getTabIndex();
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
		else if(sender instanceof SpecializedLink){
			SpecializedLink link = (SpecializedLink)sender;
			if(link.getText().equals("extend")){
				if(!link.isClicked()){
					link.setClicked(true);
					int row = link.getId();
					setWidget(row, 5, createDurationLeftTextBox(link.getUserRef(), link.getUserRef().getDurationLeft()));
				}
			}
			else if(link.getText().equals("delete")){
				if(!link.isClicked()){
					link.setClicked(true);
					SpecializedLink extendlink = link.getAssociatedExtendLink();
					extendlink.setClicked(true);
					getApprovedUsersRef().deleteUserSubscription(link.getUserRef());
					link.disableLink();
				}
			}
			/*else if(link.getText().equals("Extend")){
				if(!link.isClicked()){
					link.setClicked(true);
					int row = link.getId();
					setWidget(row, 7, createDurationLeftTextBox(link.getTrialAccountRef(), link.getTrialAccountRef().getDurationLeft()));
				}
			}*/
		}
	}

	public TextArea createTextArea(){
		TextArea textarea = new TextArea();
		return textarea;
	}

	public void addNewsItemFieldsInTabel(ArrayList list)
	{
		this.setBorderWidth(2);
		this.setWidth("100%");
	   setWidget(1,1,createLabel("TITLE"));
	   setWidget(1,2,createLabel("ABSTRACT"));
	   setWidget(1,3,createLabel("CONTENT"));
	   setWidget(1,4,createLabel("URL"));
	   setWidget(1,5,createLabel("DATE"));
	   FlexCellFormatter formatter = getFlexCellFormatter();
	   ColumnFormatter colFormatter = getColumnFormatter();
	   Iterator iter = list.iterator();
	   int row=2;
	   while(iter.hasNext())
		{
		   NewsItems news = (NewsItems)iter.next();
		   int newsitemId = news.getNewsId();
		   String title = news.getNewsTitle();
		   String abstractnews = news.getAbstractNews();
		   String content = news.getNewsContent();
		   String url= news.getUrl();
		   String date = news.getNewsDate().toString();

		   setWidget(row,0,createCheckbox(newsitemId));
		   formatter.setWidth(row, 1, "40px");
		   setText(row,1,title);
		   
		   formatter.setWidth(row, 2, "40px");
		   setText(row,2,abstractnews );
		   
		   formatter.setWidth(row, 3, "40px");
		   setText(row,3,content);
		   
		   formatter.setWidth(row, 4, "40px");
		   setText(row,4,url);
		   
		   formatter.setWidth(row, 5, "40px");
		   setText(row,5,date);
		   row++;
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
				setWidget(row, col+1, createEditTextBox(tag.getTagItemId(), tag.getTagName()));
			else{
				setWidget(row, col, createCheckbox(tag.getTagItemId()));
				setText(row, col+1, tag.getTagName());
			}
			row++;
		}
	}

	public void addCategoriesinTable(HashMap<Integer, TagItemInformation> hashMap, boolean readMode){
		this.clear();

		allCatList = getManageCategoriesRef().getAllCatList();
		System.out.println(allCatList);

		HashMap map = ManageCategories.getUpdatedCatMap();
		setWidget(1, 1, createLabel("Category Name"));
		int row = 2;
		int col = 0;
		hashMap = sortMap(hashMap);
		for(Object obj : hashMap.keySet()){
			TagItemInformation tag= hashMap.get(obj);

			if(readMode == true)
				setWidget(row, col+1, createEditTextBox(tag.getTagItemId(), tag.getTagName()));
			else{
				setWidget(row, col, createCheckbox(tag.getTagItemId()));
				setText(row, col+1, tag.getTagName());
			}
			row++;
		}
	}

	public HashMap sortMap(HashMap hashmap){
		HashMap map = new LinkedHashMap();
		List mapKeys = new ArrayList(hashmap.keySet());
		List mapValues = new ArrayList(hashmap.values());
		
		final Comparator<TagItemInformation> ALPHABETICAL_ORDER = new Comparator<TagItemInformation>(){

			public int compare(TagItemInformation t1, TagItemInformation t2) {
				
				return t1.getTagName().compareTo(t2.getTagName());
			}
		};
		
		TreeSet sortedSet = new TreeSet(ALPHABETICAL_ORDER);
		Iterator iter = mapValues.iterator();
		while(iter.hasNext()){
			sortedSet.add(iter.next());
		}
		Object[] sortedArray = sortedSet.toArray();
		int size = sortedArray.length;

		for (int i=0; i<size; i++) {
		   map.put(mapKeys.get(mapValues.indexOf(sortedArray[i])),sortedArray[i]);
		}
		return map;
	}
	
	
	public TextBox createEditTextBox(int id, String tagName){
	   TextBox textbox = new TextBox();
	   textbox.setText(tagName);
	   //textbox.addChangeListener(this);
	   textbox.addChangeHandler(this);
	   textbox.setTabIndex(id);
	   textbox.setWidth("150px");
	   textBoxMap.put(id, textbox);
	   return textbox;
	}

	public void onChange(Widget arg0) {
		/*if(arg0 instanceof TextBox){
			TextBox textBox = (TextBox)arg0;
			int ind = textBox.getTabIndex();
			String tagText = textBox.getText();
			int pid = 0;
			//boolean valid = tagValidation(tagText);
			boolean valid = true;
			boolean bool = false;
			String tagName = "";

			if(!tagText.equals("")){
				flag = false;
				if(getManageTagsRef()!=null){
				if(tagArray != null){
					currentTagFlag = false;
					Iterator iter = tagArray.iterator();		
					while(iter.hasNext()){
						TagItemInformation tag = (TagItemInformation)iter.next();
						if(tag.getParentId() != 0){
							if(ind == tag.getTagItemId()){
								tagName = tag.getTagName();
								pid = tag.getParentId();
								setTagiteminfo(tag);
								break;
							}
						}
					}
					Iterator itt = tagArray.iterator();
					while(itt.hasNext()){
							TagItemInformation tag = (TagItemInformation)itt.next();
							if(tagText.equals(tag.getTagName()) && pid == tag.getParentId()){
								textBox.setText(tagName);
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Name already exists!");
								popup.setPopupPosition(600, 350);
								popup.show();
								bool = true;
								flag = true;
								f = true;
								currentTagFlag = true;
								break;
							}
					}
					if(!bool){
						if(currentTagFlag == false){
							tagArray.remove(getTagiteminfo());
						}
						System.out.println("Tag Array contains the removed tag = "+tagArray.contains(getTagiteminfo()));
						TagItemInformation tagitem = new TagItemInformation();
						tagitem.setTagName(tagText);
						tagArray.add(tagitem);
						updatedTextBoxMap.put(ind, tagitem);
					}
				}
				}
				else if(allCatList != null){
					currentCategoryFlag = false;
					Iterator iter = allCatList.iterator();
					while(iter.hasNext()){
						TagItemInformation tag = (TagItemInformation)iter.next();				
							if(ind == tag.getTagItemId()){
								tagName = tag.getTagName();
								setTagiteminfo(tag);
							}
							if(tagText.equals(tag.getTagName())){
								textBox.setText(tagName);
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Name already exists!");
								popup.setPopupPosition(600, 350);
								popup.show();
								bool = true;
								flag = true;
								f = true;
								currentCategoryFlag = true;
								break;
							}						
					}
					if(!bool){
						if(currentCategoryFlag == false){
							allCatList.remove(getTagiteminfo());
						}
						TagItemInformation tagitem = new TagItemInformation();
						tagitem.setTagName(tagText);
						allCatList.add(tagitem);
						updatedTextBoxMap.put(ind, tagitem);
					}
				}
			}
			else{
				if(tagText.equals("")){
					f = true;
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter a Name");
					popup.setPopupPosition(500, 200);
					popup.show();
				}
				else{
					PopUpForForgotPassword popup = new PopUpForForgotPassword(tagText + " is an invalid Name");
					popup.setPopupPosition(500, 200);
					popup.show();
				}
				flag = true;
			}
		}*/
	}

	public boolean tagValidation(String tag){
		if(!tag.matches("[a-zA-z][a-zA-Z0-9' ']*"))
			return false;
		
		return true;
	}

	public HashMap getItemMap() {
		return itemMap;
	}

	public void setItemMap(HashMap itemMap) {
		this.itemMap = itemMap;
	}

	public String getStrduration() {
		return strduration;
	}

	public void setStrduration(String strduration) {
		this.strduration = strduration;
	}

	public HashMap<Integer, TextBox> getTextBoxMap() {
		return textBoxMap;
	}

	public void setTextBoxMap(HashMap<Integer, TextBox> textBoxMap) {
		this.textBoxMap = textBoxMap;
	}

	public String getNewsCenterName() {
		return newsCenterName;
	}

	public void setNewsCenterName(String newsCenterName) {
		this.newsCenterName = newsCenterName;
	}

	public void disableCheckBox(HashMap map){
		for(Object o : map.keySet()){
			CheckBox checkBox = (CheckBox)checkBoxMap.get(o);
			checkBox.setEnabled(false);
		}
	}

	public void disableTextBox(HashMap map){
		for(Object o : map.keySet()){
			if(o instanceof AdminRegistrationInformation){
				AdminRegistrationInformation admin = (AdminRegistrationInformation)o;
				TextBox textBox = (TextBox)textBoxMap.get(admin.getUserid());
				textBox.setEnabled(false);
			}
			else if(o instanceof TrialAccount){
			    TrialAccount trialaccount = (TrialAccount)o;
			    TextBox textBox = (TextBox)textBoxMap.get(trialaccount.getUserid());
			    textBox.setEnabled(false);
			}
			else{
				TextBox textBox = (TextBox)textBoxMap.get(o);
				textBox.setEnabled(false);
			}
		}
	}

	public HashMap<Integer, TagItemInformation> getUpdatedTextBoxMap() {
		return updatedTextBoxMap;
	}

	public void setUpdatedTextBoxMap(HashMap<Integer, TagItemInformation> updatedTextBoxMap) {
		this.updatedTextBoxMap = updatedTextBoxMap;
	}

	public TagItemInformation getTagiteminfo() {
		return tagiteminfo;
	}

	public void setTagiteminfo(TagItemInformation tagiteminfo) {
		this.tagiteminfo = tagiteminfo;
	}

	public ArrayList<TagItemInformation> getTagArray() {
		return tagArray;
	}

	public void setTagArray(ArrayList<TagItemInformation> tagArray) {
		this.tagArray = tagArray;
	}

	public ManageTags getManageTagsRef() {
		return manageTagsRef;
	}

	public void setManageTagsRef(ManageTags manageTagsRef) {
		this.manageTagsRef = manageTagsRef;
	}

	public ManageCategories getManageCategoriesRef() {
		return manageCategoriesRef;
	}

	public void setManageCategoriesRef(ManageCategories manageCategoriesRef) {
		this.manageCategoriesRef = manageCategoriesRef;
	}
	
	public HashMap getExtendedDurationMap() {
		return extendedDurationMap;
	}

	public void setExtendedDurationMap(HashMap extendedDurationMap) {
		this.extendedDurationMap = extendedDurationMap;
	}

    public void addTrialUserInTable(ArrayList list){
    	setWidget(0, 1, createLabelWithImage("Name"));
    	setWidget(0, 2, createLabelWithImage("email"));
    	setWidget(0, 3, createLabelWithImage("Start Date"));
    	setWidget(0, 4, createLabelWithImage("End Date"));
    	setWidget(0, 5, createLabelWithImage("NewsCatalyst"));
    	setWidget(0, 6, createLabelWithImage("Sales Executive"));
    	setWidget(0, 7, createLabel("DurationLeft"));
    	
    	int row = 2;
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
		if(list.size() > 0){
			Iterator itr = list.iterator();
			while(itr.hasNext()){
				TrialAccount trialaccount = (TrialAccount)itr.next();
				setText(row,1, trialaccount.getUsername());
				setText(row, 2, trialaccount.getUseremail());
				setText(row,3,dtf.format(trialaccount.getStartdate()));
				setText(row, 4, dtf.format(trialaccount.getEnddate()));
				setText(row, 5, trialaccount.getNewscatalystName());
				setText(row, 6, trialaccount.getSalesExecutiveName());
				setText(row, 7, Integer.toString(trialaccount.getDurationLeft()));
				//setWidget(row, 8, createLink("Extend", trialaccount, row));
				setWidget(row, 8, createLisBox(trialaccount.getNewscatalystName(),trialaccount, row));
				getFlexCellFormatter().setAlignment(row, 5, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 6, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 7, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 8, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				row++;
			}
		}
		else{
			setWidget(row, 0, createBoldLabel("No records found"));
			getFlexCellFormatter().setColSpan(row, 0, 8);
		}
    	
    }
    
    public void addDataInLoginStats(ArrayList<UserLoginStats> list,Boolean nameDisplay){
    	if(!nameDisplay){
    		setWidget(0, 0, createLeftLabel("Name"));
    		setWidget(0, 2, createLeftLabel("email"));
        	setWidget(0, 4, createLeftLabel("Role"));
       	}
    	else{
	    	setWidget(0, 0, createLabelWithImage("Name"));
	    	setWidget(0, 2, createLabelWithImage("email"));
	    	setWidget(0, 4, createLabelWithImage("Role"));
    	}
    	setWidget(0, 6, createLabelWithImage("Time of login"));
    	
    	int row = 2;
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
		if(list.size() > 0){
			Iterator itr = list.iterator();
			while(itr.hasNext()){
				UserLoginStats userloginstats = (UserLoginStats)itr.next();
				setText(row,0, userloginstats.getUsername());
				setText(row,2, userloginstats.getEmail());
				setText(row,4, userloginstats.getRole());
				setText(row,6, dtf.format(userloginstats.getTimeOfLogin()));
				
				getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 4, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 6, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				row++;
			}
		}
		else{
			 setWidget(row, 0, createBoldLabel("No records found"));
			   getFlexCellFormatter().setColSpan(row, 0, 5);
		}
		getFlexCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		getFlexCellFormatter().setAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		getFlexCellFormatter().setAlignment(0, 4, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		getFlexCellFormatter().setAlignment(0, 6, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
	}
    
    public void addDataUserItemAccessStats(ArrayList<UserItemAccessStats> list2,Boolean nameDisplay) {
    	if(nameDisplay){
	     setWidget(0, 0, createLabelWithImage("Name"));
    	}
	    setWidget(0, 2, createLabelWithImage("NewsItem"));
	    setWidget(0, 4, createLabel("News Date"));
	    setWidget(0, 6, createLabelWithImage("Access from newscatalyst"));
	    setWidget(0, 8, createLabelWithImage("Access from newsletter"));
	    
	    int row = 2;
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
		if(list2.size() > 0){
			Iterator itr = list2.iterator();
			while(itr.hasNext()){
				UserItemAccessStats useritemaccesstats = (UserItemAccessStats)itr.next();
				if(nameDisplay){
				 setText(row, 0,useritemaccesstats.getFirstname()+ " "+useritemaccesstats.getLastname());
				 getFlexCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
				}
				 DateTimeFormat dtf1 = DateTimeFormat.getFormat("yyyy-MM-dd");
				 setText(row, 2,useritemaccesstats.getNewsitemtitle());
				 setText(row, 4,dtf1.format(useritemaccesstats.getNewsitemdate()));
				 setText(row, 6,String.valueOf(useritemaccesstats.getNewscatalystitemcount()));
				 setText(row, 8,String.valueOf(useritemaccesstats.getNewsletteritemcount()));
				
				getFlexCellFormatter().setAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 6, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				getFlexCellFormatter().setAlignment(row, 8, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
				
				getFlexCellFormatter().setWidth(row, 2, "500px");
				row++;
			}
		}
		else{
			 setWidget(row, 0, createBoldLabel("No records found"));
			   getFlexCellFormatter().setColSpan(row, 0, 5);
		}
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

    public SpecializedLink createLink(String text, TrialAccount user, int id){
		SpecializedLink link = new SpecializedLink();
		link.setText(text);
		link.setClicked(false);
		link.addClickListener(this);
		link.setId(id);
		link.setTrialAccountRef(user);
		return link;
	}
    
    /*public TextBox createDurationLeftTextBox(TrialAccount user, int value){
 	   TextBox textbox = new TextBox();
 	   String str = Integer.toString(user.getUserid());
 	   textbox.setName(str);
 	   textbox.setText(String.valueOf(value));
 	   extendedDurationMap.put(user, textbox);
 	   textBoxMap.put(user.getUserid(), textbox);
 	   textbox.setSize("50px", "15px");
 	   getExistingTrialAccountsRef().getBtnExtendDuration().setEnabled(true);
 	   return textbox;
    }*/
    
    private ListBox createLisBox(String newscatalystname,final Object obj, final int id){
    	final ListBox lb = new ListBox();
    	lb.addItem("More Actions", "select");
    	
    	if(obj instanceof TrialAccount){
    		TrialAccount trialaccount = (TrialAccount)obj;
    		if(trialaccount.getDurationLeft() == 0){
    			lb.addItem("Extend", "Extend");
    		}
    	}
    	else{
    		lb.addItem("Extend", "Extend");
    	}
    	
    	lb.addItem("Subscribe to "+newscatalystname,"Subscribe");
    	lb.addItem("Show history", "history");
    	lb.addItem("Delete", "Delete");
    	lb.setSize("104px", "20px");
    	
    	lb.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent arg0) {
				if(lb.getItemText(lb.getSelectedIndex()).equals("Extend")){
					int row = id;
					if(obj instanceof TrialAccount){
						TrialAccount trialaccount = (TrialAccount)obj;
						getExistingTrialAccountsRef().reinitiateTrialAccount(trialaccount,row);
					    //setWidget(row, 7, createDurationLeftTextBox(trialaccount, trialaccount.getDurationLeft()));
					    
					}
					else if(obj instanceof AdminRegistrationInformation){
						AdminRegistrationInformation adminregistration = (AdminRegistrationInformation)obj;
						setWidget(row, 5, createDurationLeftTextBox(adminregistration, adminregistration.getDurationLeft()));
					}
				}
				else if(lb.getItemText(lb.getSelectedIndex()).contains("Subscribe")){
					if(obj instanceof TrialAccount){
						TrialAccount trialaccount = (TrialAccount)obj;
						TrialToSubscribeUser subscribe = new TrialToSubscribeUser(trialaccount,existingTrialAccountsRef);
						if(existingTrialAccountsRef.getDeck().getWidgetCount() >= 2){
							existingTrialAccountsRef.getDeck().remove(1);
						}
							existingTrialAccountsRef.getDeck().add(subscribe);
							existingTrialAccountsRef.getDeck().showWidget(1);
					}
					else if(obj instanceof AdminRegistrationInformation){
						AdminRegistrationInformation adminregitration = (AdminRegistrationInformation)obj;
					}
				}
				else if(lb.getItemText(lb.getSelectedIndex()).equals("Show history")){
					int row = id;
					if(obj instanceof TrialAccount){
						TrialAccount trialaccount = (TrialAccount)obj;
						createRow(trialaccount, row);
					}
					else if(obj instanceof AdminRegistrationInformation){
						AdminRegistrationInformation adminregitration = (AdminRegistrationInformation)obj;
						createRow(adminregitration, row);
					}
				}
				else if(lb.getItemText(lb.getSelectedIndex()).equals("Delete")){
			       int row = id;
					if(obj instanceof TrialAccount){
						TrialAccount trialaccount = (TrialAccount)obj;
						getExistingTrialAccountsRef().deleteTrialAccount(trialaccount);
						//removeRow(row);
					}
				}
				
			}
    		
    	});
    	return lb;
    }
    
    private void createRow(final Object obj,final int row){
    	rowExpand = true;
    	Image imgUp = new Image("images/trial/arrow.png");
    	int cellcount = getCellCount(row);
    	int rowWidth = 0;
    	removeRow(row);
    	insertRow(row);
    	
    	int rowwidth = getRowFormatter().getElement(row).getClientWidth();
		if(rowWidth == 0)
			rowWidth = rowwidth+4;
    	FlexCellFormatter formatter = getFlexCellFormatter();
		formatter.setColSpan(row, 0, cellcount);
		UserHistoryPanel panel = null;
		if(obj instanceof TrialAccount){
			TrialAccount trialaccount = (TrialAccount)obj;
		    panel = new UserHistoryPanel(trialaccount.getUserid(),trialaccount.getNewscenterid(),imgUp);
		}
		setWidget(row, 0, panel);
		getCellFormatter().setAlignment(row, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP);
		DOM.setStyleAttribute(imgUp.getElement(), "marginTop", "5px");
		DOM.setStyleAttribute(imgUp.getElement(), "marginLeft", "4px");
		//isFormDisplayed = true;
		//decolorRowFont(row);
		
		setWidth(String.valueOf(rowWidth));
		
		imgUp.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent arg0) {
				removeRow(row);
		    	insertRow(row);		
		    	DateTimeFormat dtf = DateTimeFormat.getFormat("yyyy-MM-dd HH:mm:ss");
		    	if(obj instanceof TrialAccount){
					TrialAccount trialaccount = (TrialAccount)obj;
					setText(row,1, trialaccount.getUsername());
					setText(row, 2, trialaccount.getUseremail());
					setText(row,3,dtf.format(trialaccount.getStartdate()));
					setText(row, 4, dtf.format(trialaccount.getEnddate()));
					setText(row, 5, trialaccount.getNewscatalystName());
					setText(row, 6, trialaccount.getSalesExecutiveName());
					setText(row, 7, Integer.toString(trialaccount.getDurationLeft()));
					//setWidget(row, 8, createLink("Extend", trialaccount, row));
					setWidget(row, 8, createLisBox(trialaccount.getNewscatalystName(),trialaccount, row));
					getFlexCellFormatter().setAlignment(row, 5, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
					getFlexCellFormatter().setAlignment(row, 6, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
					getFlexCellFormatter().setAlignment(row, 7, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
					getFlexCellFormatter().setAlignment(row, 8, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
					rowExpand = false;
		    	}
			}
			
		});
    	
    }
    
	public String getHoverColumnName() {
		return hoverColumnName;
	}

	public void setHoverColumnName(String hoverColumnName) {
		this.hoverColumnName = hoverColumnName;
	}

	public DecoratedPopupPanel getColumnHeaderPopup() {
		return columnHeaderPopup;
	}

	public void setColumnHeaderPopup(DecoratedPopupPanel columnHeaderPopup) {
		this.columnHeaderPopup = columnHeaderPopup;
	}

	public ExistingTrialAccounts getExistingTrialAccountsRef() {
		return existingTrialAccountsRef;
	}

	public void setExistingTrialAccountsRef(
			ExistingTrialAccounts existingTrialAccountsRef) {
		this.existingTrialAccountsRef = existingTrialAccountsRef;
	}

	@Override
	public void onMouseOver(MouseOverEvent arg0) {
		    columnHeaderPopup.clear();
	    	filterpopup = new FilterPopup(this);
	    	
	    	 for(Image img: mapOfImageVsColumnName.keySet()){
			    	if(arg0.getSource().equals(img)){
			    		String colname = mapOfImageVsColumnName.get(img);
			    		if(colname.equals("Name") || colname.equals("email") || colname.equals("Role") || colname.equals("NewsItem") || colname.equals("Company")|| colname.equals("Time of login")){
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

	public int getHoverColumn() {
		return hoverColumn;
	}

	public void setHoverColumn(int hoverColumn) {
		this.hoverColumn = hoverColumn;
	}

	public UserLogStats getUserLogsStatsRef() {
		return userLogsStatsRef;
	}

	public void setUserLogsStatsRef(UserLogStats userLogsStatsRef) {
		this.userLogsStatsRef = userLogsStatsRef;
	}

	private Label createBoldLabel(String text){
		Label lbl = new Label(text);
		DOM.setStyleAttribute(lbl.getElement(), "fontSize", "12px");
		DOM.setStyleAttribute(lbl.getElement(), "fontWeight", "bold");
		return lbl;
	}
	/*public String getTableCollectionWidth() {
		return tableCollectionWidth;
	}

	public void setTableCollectionWidth(String width) {
		this.tableCollectionWidth = width;
	}*/

	public UserItemAccessWidget getUserItemAccessRef() {
		return userItemAccessRef;
	}

	public void setUserItemAccessRef(UserItemAccessWidget userItemAccessRef) {
		this.userItemAccessRef = userItemAccessRef;
	}

	public Boolean getRowExpand() {
		return rowExpand;
	}

	public void setRowExpand(Boolean rowExpand) {
		this.rowExpand = rowExpand;
	}

	@Override
	public void onChange(ChangeEvent event) {
		
		Widget arg0 = (Widget)event.getSource();
		if(arg0 instanceof TextBox){
			TextBox textBox = (TextBox)arg0;
			int ind = textBox.getTabIndex();
			String tagText = textBox.getText();
			int pid = 0;
			//boolean valid = tagValidation(tagText);
			boolean valid = true;
			boolean bool = false;
			String tagName = "";

			if(!tagText.equals("")){
				flag = false;
				if(getManageTagsRef()!=null){
				if(tagArray != null){
					currentTagFlag = false;
					Iterator iter = tagArray.iterator();		
					while(iter.hasNext()){
						TagItemInformation tag = (TagItemInformation)iter.next();
						if(tag.getParentId() != 0){
							if(ind == tag.getTagItemId()){
								tagName = tag.getTagName();
								pid = tag.getParentId();
								setTagiteminfo(tag);
								break;
							}
						}
					}
					Iterator itt = tagArray.iterator();
					while(itt.hasNext()){
							TagItemInformation tag = (TagItemInformation)itt.next();
							if(tagText.equals(tag.getTagName()) && pid == tag.getParentId()){
								textBox.setText(tagName);
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Name already exists!");
								popup.setPopupPosition(600, 350);
								popup.show();
								bool = true;
								flag = true;
								f = true;
								currentTagFlag = true;
								break;
							}
					}
					if(!bool){
						if(currentTagFlag == false){
							tagArray.remove(getTagiteminfo());
						}
						System.out.println("Tag Array contains the removed tag = "+tagArray.contains(getTagiteminfo()));
						TagItemInformation tagitem = new TagItemInformation();
						tagitem.setTagName(tagText);
						tagArray.add(tagitem);
						updatedTextBoxMap.put(ind, tagitem);
					}
				}
				}
				else if(allCatList != null){
					currentCategoryFlag = false;
					Iterator iter = allCatList.iterator();
					while(iter.hasNext()){
						TagItemInformation tag = (TagItemInformation)iter.next();				
							if(ind == tag.getTagItemId()){
								tagName = tag.getTagName();
								setTagiteminfo(tag);
							}
							if(tagText.equals(tag.getTagName())){
								textBox.setText(tagName);
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Name already exists!");
								popup.setPopupPosition(600, 350);
								popup.show();
								bool = true;
								flag = true;
								f = true;
								currentCategoryFlag = true;
								break;
							}						
					}
					if(!bool){
						if(currentCategoryFlag == false){
							allCatList.remove(getTagiteminfo());
						}
						TagItemInformation tagitem = new TagItemInformation();
						tagitem.setTagName(tagText);
						allCatList.add(tagitem);
						updatedTextBoxMap.put(ind, tagitem);
					}
				}
			}
			else{
				if(tagText.equals("")){
					f = true;
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter a Name");
					popup.setPopupPosition(500, 200);
					popup.show();
				}
				else{
					PopUpForForgotPassword popup = new PopUpForForgotPassword(tagText + " is an invalid Name");
					popup.setPopupPosition(500, 200);
					popup.show();
				}
				flag = true;
			}
		}
		
	}

	@Override
	public void onClick(ClickEvent event) {
		
		Widget sender = (Widget)event.getSource();
		String str= "";
		String strdurationvalue="";
		if(sender  instanceof CheckBox) {
			CheckBox checkbox = (CheckBox)sender;
			int index = checkbox.getTabIndex();
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
		else if(sender instanceof SpecializedLink){
			SpecializedLink link = (SpecializedLink)sender;
			if(link.getText().equals("extend")){
				if(!link.isClicked()){
					link.setClicked(true);
					int row = link.getId();
					setWidget(row, 5, createDurationLeftTextBox(link.getUserRef(), link.getUserRef().getDurationLeft()));
				}
			}
			else if(link.getText().equals("delete")){
				if(!link.isClicked()){
					link.setClicked(true);
					SpecializedLink extendlink = link.getAssociatedExtendLink();
					extendlink.setClicked(true);
					getApprovedUsersRef().deleteUserSubscription(link.getUserRef());
					link.disableLink();
				}
			}
		}
		
	}

	
}