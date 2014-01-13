package com.lighthouse.admin.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.admin.client.NewsItems;
import com.admin.client.TagItemInformation;
import com.appUtils.client.ProgressIndicatorWidget;
import com.common.client.ManageHeader;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.admin.client.domain.LHNewsItems;
import com.lighthouse.admin.client.domain.NewsPriority;
import com.lighthouse.admin.client.service.LHAdminInformationService;
import com.lighthouse.admin.client.service.LHAdminInformationServiceAsync;

public class LHEditNewsItems extends Composite implements FormHandler,
										ChangeHandler,ClickHandler,MouseMoveHandler,SubmitCompleteHandler,SubmitHandler  {
	private ManageHeader header = new ManageHeader();
	protected DockPanel dock = new DockPanel();
	
	private HorizontalPanel hpanel = new HorizontalPanel();
	protected HorizontalPanel hpbuttonPanel = new HorizontalPanel();
	protected HorizontalPanel hpanellink = new HorizontalPanel();
	protected HorizontalPanel tagPanel = new HorizontalPanel();
	protected HorizontalPanel appliedTagPanel = new HorizontalPanel();
	
	protected DecoratorPanel decoratorforlabel = new DecoratorPanel();
	private DecoratorPanel decoratorflex = new DecoratorPanel();
	
	protected Button button = new Button("Save");
	protected Button buttonReset = new Button("Reset");
	private Button buttonback = new Button("Back");
	protected Button subtractionButton = new Button("Remove");
	private Button deleteButton = new Button("Delete");
	
	private TextBox industryTextbox = new TextBox();
	protected TextBox urlTextbox = new TextBox();
	protected TextBox titleTextbox = new TextBox();
	protected TextBox dateTextbox = new TextBox();
	protected TextBox sourceTextbox = new TextBox();
	protected TextBox authorTextbox = new TextBox();
	protected TextBox newscenteridtbBox = new TextBox();
	protected TextBox userselectedTextbox = new TextBox();
	
	protected TextArea abstractTextarea = new TextArea();
	protected TextArea contentTextarea = new TextArea();
	
	protected ListBox categoryListBox = new ListBox();
	protected ListBox tagList = new ListBox();
	protected ListBox newsTitleListbox = new ListBox();
	private ListBox listboxtags = new ListBox();
	protected ListBox tagListBox = new ListBox();
	protected ListBox selectedtagListbox = new ListBox();
	protected ListBox appliedTagListBox = new ListBox();
	protected ListBox priorityListBox = new ListBox();
	protected ListBox primaryTagList=new ListBox();
	protected ListBox appliedPrimaryTags=new ListBox();
	
	protected static boolean flag = true;
	protected boolean editTags = false;
	
	protected String userseletedtagsStr = "";
	protected String industryName;
	
	protected ArrayList appliedTagList = new ArrayList();
	protected ArrayList<String>appliedPrimaryList=new ArrayList<String>();
	
	protected int newsItemId, resetIndex, index = 0;
	protected int userSelectedIndustryId, clientWidth, clientHeight, userSelectedTags, userRemoveTags, indextag = 0;
	
	private ScrollPanel scroller = new ScrollPanel();
	protected Label label = new Label("Please select tag  to edit");
	protected FlexTable flex = new FlexTable();
	protected VerticalPanel vpanel = new VerticalPanel();
	private HashMap<Integer,ArrayList> manageNews = new HashMap<Integer, ArrayList>();
	protected FormPanel form = new FormPanel();
	protected FileUpload fileUpload = new FileUpload();
	private RootPanel panel;
	protected NewsItems newsItems;
	protected CheckBox chkLocked = new CheckBox();
	protected Hidden isLocked = new Hidden();
	private ProgressIndicatorWidget editNewsItemLoader = new ProgressIndicatorWidget(true);
	
	public LHEditNewsItems(){
		
	}
	public LHEditNewsItems(String userIndustryName, int industryId){
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		industryName = userIndustryName;
		userSelectedIndustryId = industryId;
		getStyleNames();
		addInFlexTable();
		getCategoryName(industryName);
		industryTextbox.setText(industryName);
		industryTextbox.setEnabled(false);
		chkLocked.setEnabled(true);
		
		tagList.setVisibleItemCount(10);
		categoryListBox.setVisibleItemCount(10);
		
		vpanel.setSpacing(7);
		vpanel.add(hpanellink);
		vpanel.add(decoratorforlabel);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setWidget(flex);
		hpanel.add(header);
		dock.setSpacing(10);
		decoratorforlabel.add(label);
		hpbuttonPanel.setSpacing(7);
		/*hpbuttonPanel.add(button);
		hpbuttonPanel.add(buttonReset);*/
		createButtonPanel();
		scroller.setSize("100%", "100%");
		scroller.add(form);
		decoratorflex.add(scroller);
		dock.add(vpanel,DockPanel.NORTH);
		dock.add(decoratorflex,DockPanel.CENTER);
		dock.add(hpbuttonPanel,DockPanel.SOUTH);
		decoratorflex.setWidth("100%");
		dock.setSize("100%", "100%");
		getNamesOfVariable(); 
		addHandlersToVariable();
		newscenteridtbBox.setVisible(false);
		newsTitleListbox.setWidth("50%");
		appliedPrimaryTags.setWidth("50%");
		
		appliedTagListBox.setMultipleSelect(true);
		appliedTagListBox.setVisibleItemCount(6);
		appliedTagPanel.add(appliedTagListBox);
		appliedTagPanel.add(deleteButton);
		appliedTagPanel.setHeight("100%");
		tagPanel.setWidth("100%");
		tagPanel.add(selectedtagListbox);
		tagPanel.add(listboxtags);
		tagPanel.add(subtractionButton);
		tagPanel.add(tagListBox);
		tagPanel.add(userselectedTextbox);
		userselectedTextbox.setVisible(false);
		tagListBox.setMultipleSelect(true);
		appliedPrimaryTags.setVisibleItemCount(10);	
		listboxtags.setVisibleItemCount(10);
		tagListBox.setVisibleItemCount(10);
		selectedtagListbox.setVisibleItemCount(10);
		newsTitleListbox.setTitle("News Title");
		
		fillPrimaryTaglist();
		fillPriorityList();
		index = categoryListBox.getSelectedIndex();
		initWidget(dock);
	}
	
	public void createButtonPanel(){
		hpbuttonPanel.add(button);
		hpbuttonPanel.add(buttonReset);
	}
	
	private void addHandlersToVariable() {
		form.addSubmitHandler(this);
		form.addSubmitCompleteHandler(this);
		chkLocked.addClickHandler(this);
		subtractionButton.addClickHandler(this);
		deleteButton.addClickHandler(this);
		tagListBox.addClickHandler(this);
		selectedtagListbox.addClickHandler(this);
		listboxtags.addChangeHandler(this);
		listboxtags.addClickHandler(this);
		appliedTagListBox.addClickHandler(this);
		newsTitleListbox.addChangeHandler(this);
		tagList.addChangeHandler(this);
		tagList.addClickHandler(this);
		categoryListBox.addChangeHandler(this);
		primaryTagList.addChangeHandler(this);
		appliedPrimaryTags.addChangeHandler(this);
		button.addClickHandler(this);
		buttonReset.addClickHandler(this);
		buttonback.addClickHandler(this);
		
	}
	private void getNamesOfVariable() {
		isLocked.setName("isLocked");
		titleTextbox.setName("newstitleTextBox");
		industryTextbox.setName("industryTextBox");
		urlTextbox.setName("urlTextBox");
		dateTextbox.setName("dateTextBox");
		sourceTextbox.setName("sourceTextBox");
		authorTextbox.setName("authorTextBox");
		abstractTextarea.setName("abstractNewsTextArea");
		categoryListBox.setName("categoryList");
		contentTextarea.setName("contentTextArea");
		tagList.setName("userSelectedTags");
		fileUpload.setName("imageFileUpload");
		newscenteridtbBox.setName("NewsCenterID");
		selectedtagListbox.setName("categoriesList");
		listboxtags.setName("TagNameListBox");
		tagListBox.setName("userSelectedTags");
		primaryTagList.setName("primaryTagList");
		appliedPrimaryTags.setName("appliedPrimaryTags");
		userselectedTextbox.setName("userSelectedTagTextbox");
		priorityListBox.setName("priorityListBox");
		
	}
	public void addInFlexTable(){
		newsItems = getNewsItems();
		FlexCellFormatter formatter = flex.getFlexCellFormatter();
		flex.setWidget(0,2,createLabel("Industry"));
		flex.setWidget(1,2,createLabel("Primary Tags *"));
		flex.setWidget(2,2,createLabel("News Title"));
		flex.setWidget(5,2,createLabel("Title"));
		flex.setWidget(6,2,createLabel("News Abstract"));
		flex.setWidget(7,2,createLabel("News Content"));
		flex.setWidget(8,2,createLabel("Priority"));
		flex.setWidget(9,2,createLabel("URL"));
		flex.setWidget(10,2,createLabel("Date"));
		flex.setWidget(12, 2, createLabel("Author"));
		flex.setWidget(13, 2, createLabel("Source"));
		flex.setWidget(14, 2, createLabel("Upload Image"));
		flex.setWidget(15, 2, createLabel("Applied Primary Tag"));
		flex.setWidget(16, 2, createLabel("Applied Tags")); //new
		flex.setWidget(17, 2, createLabel("Add Tags")); //new
		
		flex.setWidget(0,5,industryTextbox);
		Label lblPrimaryTag = new Label("(*Please select Primary tag)"); 
		lblPrimaryTag.setStylePrimaryName("lblPrimaryTag");
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(10);
		panel.add(primaryTagList);
		panel.setCellWidth(lblPrimaryTag, "100%");
		panel.add(lblPrimaryTag);
		panel.add(editNewsItemLoader);
		flex.setWidget(1,5,panel);
		flex.setWidget(2,5,newsTitleListbox);
		
		HorizontalPanel hPanelforLocking = new HorizontalPanel();
		hPanelforLocking.add(titleTextbox);
		hPanelforLocking.setCellWidth(createLabel("Is Locked "), "100%");
		hPanelforLocking.add(createLabel("Is Locked "));
		hPanelforLocking.add(chkLocked);

		flex.setWidget(5, 5, hPanelforLocking);
		flex.setWidget(5, 6, isLocked);
		
		formatter.setColSpan(6, 5, 5);
		flex.setWidget(6,5,abstractTextarea);
		formatter.setColSpan(7, 5, 5);
		flex.setWidget(7,5,contentTextarea);
		flex.setWidget(8,5,priorityListBox);
		flex.setWidget(9,5,urlTextbox);
		flex.setWidget(10,5,dateTextbox);
		flex.setText(11,5, "(YYYY-MM-DD)");
		flex.setWidget(12, 5, authorTextbox);
		flex.setWidget(13, 5, sourceTextbox);
		flex.setWidget(14, 5, fileUpload);
		flex.setWidget(15, 5, appliedPrimaryTags);
		flex.setWidget(16, 5, appliedTagPanel);
		flex.setWidget(17, 5, tagPanel);
		flex.setWidget(18, 0, newscenteridtbBox);
	}
	
	/**
	 * This method gets all the Primary Tags
	 */
	
	public void fillPrimaryTaglist(){
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				try{
					ArrayList list = (ArrayList)result;
					Iterator iter = list.iterator();
					while(iter.hasNext()){
						TagItemInformation tag = (TagItemInformation)iter.next();
						String name = tag.getTagName();
						appliedPrimaryList.add(name);
						primaryTagList.addItem(name);
						//appliedPrimaryTags.addItem(name);
					}
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryName()");
				}
			}
		};
		service.fillprimaryTaglist(userSelectedIndustryId,industryName,callback);
	
	}

	private void fillPriorityList(){
		priorityListBox.addItem(" -- Please select a news priority -- ");
		priorityListBox.addItem(NewsPriority.HIGHEST.toString());
		priorityListBox.addItem(NewsPriority.HIGHER.toString());
		priorityListBox.addItem(NewsPriority.HIGH.toString());
		priorityListBox.addItem(NewsPriority.MEDIUM.toString());
		priorityListBox.addItem(NewsPriority.LOW.toString());
		priorityListBox.addItem(NewsPriority.LOWER.toString());
		priorityListBox.addItem(NewsPriority.LOWEST.toString());
	}
	
	public Label createLabel(String text){
		Label lb = new Label(text);
		lb.setStylePrimaryName("labelFlexEditNewsItems");
		return lb;
	}

	public void getStyleNames(){
		hpanellink.setStylePrimaryName("hpanellink");
		vpanel.setStylePrimaryName("hpanelbuttonAndVpanel");
		buttonReset.setStylePrimaryName("buttonOkAdmin");
		buttonback.setStylePrimaryName("buttonOkAdmin");
		button.setStylePrimaryName("buttonOkAdmin");
		subtractionButton.setStylePrimaryName("buttonAddSub");
		deleteButton.setStylePrimaryName("buttonAddSub");
		industryTextbox.setStylePrimaryName("textboxNewsItems");
		tagList.setStylePrimaryName("taglistboxNewsItems");
		categoryListBox.setStylePrimaryName("taglistboxNewsItems");
		urlTextbox.setStylePrimaryName("textboxNewsItems");
		sourceTextbox.setStylePrimaryName("textboxNewsItems");
		authorTextbox.setStylePrimaryName("textboxNewsItems");
		titleTextbox.setStylePrimaryName("textboxNewsItems");
		
		primaryTagList.setVisibleItemCount(10);
		dateTextbox.setStylePrimaryName("textboxNewsItems");
		abstractTextarea.setStylePrimaryName("textAreaNewsItems");
		contentTextarea.setStylePrimaryName("textAreaNewsItems");
		fileUpload.setStylePrimaryName("textboxNewsItems");
		hpanel.setStylePrimaryName("hpanelAdmin");
		label.setStylePrimaryName("labelNewUser");
		decoratorforlabel.setStylePrimaryName("decoratorlabelNewUserApproval");
		
		listboxtags.setStylePrimaryName("taglistboxNewsItems");
		tagListBox.setStylePrimaryName("taglistboxNewsItems");
		selectedtagListbox.setStylePrimaryName("taglistboxNewsItems");
		appliedTagListBox.setStylePrimaryName("taglistboxNewsItems");
		primaryTagList.setStylePrimaryName("taglistboxNewsItems");
		priorityListBox.setStylePrimaryName("taglistboxNewsItems");
	}
	
	public void onModuleLoad() {
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}

	public void onChange(Widget sender){
	}

	/**
	 * This method gets the News Item specific to the selected Primary Tag
	 */
	public void getNewsItemForThisTag(String industryName,String tagName){
		editNewsItemLoader.enable();
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}

			public void onSuccess(Object result) {
				try{
					editNewsItemLoader.disable();
					ArrayList list = (ArrayList)result;
					addTheContent(list);
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getNewsItemForThisTag()");
				}
			}
		};
		service.getNewsItemForThisTag(industryName, tagName, callback);
	}
	
	

	public void addTheContent(ArrayList list){
		ArrayList newsItemContent;
		int counter= 1;
		Date date;
		if(list.size()!=0){
			newsTitleListbox.addItem("-------Please Select-------");
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				newsItemContent = new ArrayList();
				LHNewsItems newsItems = (LHNewsItems)iter.next();
				String titleStr = (String)newsItems.getNewsTitle();
				String author = (String)newsItems.getAuthor();
				String content= (String)newsItems.getNewsContent();
				String abstractNews = (String)newsItems.getAbstractNews();
				String url= (String)newsItems.getUrl();
				date= (Date)newsItems.getNewsDate();
				String source = newsItems.getNewsSource();
				String imageurl = (String)newsItems.getImageUrl();
				int newsId = (Integer)newsItems.getNewsId();
				ArrayList associatedtags = (ArrayList)newsItems.getAssociatedTagList();
				int isLockedNews = newsItems.getIsLocked();
				String newsPriority = newsItems.getNewsPriority();
				
				newsTitleListbox.addItem(titleStr);
				newsTitleListbox.setName("NewsTitlesOfTag");
				
				newsItemContent.add(titleStr);
				newsItemContent.add(content);
				newsItemContent.add(abstractNews);
				newsItemContent.add(url);
				newsItemContent.add(date);
				newsItemContent.add(source);
				newsItemContent.add(newsId);
				newsItemContent.add(imageurl);
				newsItemContent.add(associatedtags);
				newsItemContent.add(isLockedNews);
				newsItemContent.add(newsPriority);
				newsItemContent.add(author);
				
				manageNews.put(counter, newsItemContent);

				counter++;
			}
		}
	}
	public void fillAppliedPrimaryTagList(){
		appliedPrimaryTags.clear();
		for(String str:appliedPrimaryList)
			appliedPrimaryTags.addItem(str);
	}

	/**
	 * This method sets the selected NewsItem data in appropriate fields for edit 
	 */
	public void addTheNewsContent(int index){
		fillAppliedPrimaryTagList();
		if(!manageNews.isEmpty()){
			if(manageNews.containsKey(index)){
				resetIndex = index;
				ArrayList abstractNews = (ArrayList)manageNews.get(index);
				String title =(String)abstractNews.get(0);
				String content= (String)abstractNews.get(1);
				String abstractnews = (String)abstractNews.get(2);
				String url= (String)abstractNews.get(3);
				Date date = (Date)abstractNews.get(4);
				String source = (String)abstractNews.get(5);
				newsItemId = (Integer)abstractNews.get(6);
				String image = (String)abstractNews.get(7);
				ArrayList tags = (ArrayList)abstractNews.get(8);
				int isLockedNews = (Integer) abstractNews.get(9);
				String newspriority = (String)abstractNews.get(10);
				String author = (String)abstractNews.get(11);
				
				if(isLockedNews==1)
					chkLocked.setValue(true);
				else
					chkLocked.setValue(false);
				
				for(int i = 1; i < priorityListBox.getItemCount(); i++){
					if(priorityListBox.getItemText(i).trim().equalsIgnoreCase(newspriority)){
						priorityListBox.setSelectedIndex(i);
						break;
					}
				}
				titleTextbox.setText(title);
				abstractTextarea.setText(abstractnews);
				contentTextarea.setText(content);
				urlTextbox.setText(url);
				dateTextbox.setText(date.toString());
				authorTextbox.setText(author);
				sourceTextbox.setText(source);
				sortTags(tags);
				if(appliedTagListBox.getItemCount()!=0)
					appliedTagListBox.clear();
				if(appliedTagListBox.getItemCount() == 0){
					Iterator iter = tags.iterator();
					while(iter.hasNext()){
						TagItemInformation tagiteminfo = (TagItemInformation)iter.next();
						if(tagiteminfo.isIsprimary()){
							for(int i = 0;i<appliedPrimaryTags.getItemCount();i++){
								if(appliedPrimaryTags.getItemText(i).equals(tagiteminfo.getTagName()))
									appliedPrimaryTags.setSelectedIndex(i);
							}
						}
						if(!tagiteminfo.isIsprimary()){
							appliedTagListBox.addItem(tagiteminfo.getTagName());
							appliedTagList.add(tagiteminfo.getTagName());
						}
					}
				}
				if(listboxtags.getItemCount()!=0)
					listboxtags.clear();
				if(tagListBox.getItemCount()!=0)
					tagListBox.clear();
				
				Image img = new Image(image,0,0,68,68);
				//img.setSize("35", "35");
				flex.setWidget(14, 4, img);
//				fileUpload.setName(image);
			}
			else{
				authorTextbox.setText("");
				titleTextbox.setText("");
				abstractTextarea.setText("");
				contentTextarea.setText("");
				urlTextbox.setText("");
				dateTextbox.setText("");
				sourceTextbox.setText("");
				appliedTagListBox.clear();
				chkLocked.setValue(false);
				isLocked.setValue("0");
			}
		}
	}
	
	/**
	 * This method gets all the Categories 
	 */
	public void getCategoryName(String industryName){
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				try{
					ArrayList list = (ArrayList)result;
					sortTags(list);
					Iterator iter = list.iterator();
					while(iter.hasNext()){
						TagItemInformation tag = (TagItemInformation)iter.next();
						if(!tag.isIsprimary()){
							String name = tag.getTagName();
							categoryListBox.addItem(name);
							selectedtagListbox.addItem(name);
						}
						index = categoryListBox.getSelectedIndex();
						categoryListBox.setName("categoryList");
					}
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryName()");
				}
			}
		};
		service.getCategoryNames(userSelectedIndustryId,industryName, callback);
	}

	public void sortTags(ArrayList list){
		final Comparator<TagItemInformation> NAME_ORDER = new Comparator<TagItemInformation>(){
			public int compare(TagItemInformation u1, TagItemInformation u2) {
				return u1.getTagName().compareTo(u2.getTagName());
			}
		};
		Collections.sort(list,NAME_ORDER);
	}
	  
	public Image createImage(String imageurl){
		Image image = new Image(imageurl, 0, 0, 35, 35);
		return image;
	}

	/**
	 * This method get Tag name specific to selected Category/Tag
	 */
	public void getTagName(String industryName,String categoryName){
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				try{
					ArrayList list = (ArrayList)result;
					sortTags(list);
					if(editTags)
						addTagsInListBox(list);
					else
						addTagNamesInListBox(list);
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryName()");
				}
			}
		};
		service.getTagNames(industryName,categoryName, callback);
	}

	public void addTagNamesInListBox(ArrayList list){
		tagList.clear();
		Iterator iter = list.iterator();
		while(iter.hasNext()){
			TagItemInformation tag = (TagItemInformation)iter.next();
			int index = tag.getTagItemId();
			String name = tag.getTagName();
			tagList.addItem(name);
			tagList.setName("TagList");
		}
	}

	public void addTagsInListBox(ArrayList list){
		listboxtags.clear();
		editTags = false;
		Iterator iter = list.iterator();
		while(iter.hasNext()){
			TagItemInformation tag = (TagItemInformation)iter.next();
			int index = tag.getTagItemId();
			String name = tag.getTagName();
			listboxtags.addItem(name);
		}
	}


	public void clearAllTheFields(){
		newsTitleListbox.clear();
		appliedPrimaryTags.clear();
		abstractTextarea.setText("");
		contentTextarea.setText("");
		titleTextbox.setText("");
		urlTextbox.setText("");
		dateTextbox.setText("");
		sourceTextbox.setText("");
		authorTextbox.setText("");
		listboxtags.clear();
		tagListBox.clear();
		appliedTagListBox.clear();
		flex.clearCell(13, 4);
		if(!userseletedtagsStr.equals(null))
			userseletedtagsStr = "";
		chkLocked.setValue(false);
		isLocked.setValue("0");
		flex.remove(fileUpload);
		FileUpload upload = new FileUpload();
		upload.setVisible(true);
		upload.setName("imageFileUpload");
		flex.setWidget(14, 5, upload);
	}

	public boolean validate(){
		flag = true;
		boolean result = validateDate();

		if(flag == true){
			indextag = tagList.getSelectedIndex();
			//String tagName = tagList.getItemText(indextag);
			int index = newsTitleListbox.getSelectedIndex();
			String titleNewsItem = newsTitleListbox.getItemText(index);

			//if((abstractTextarea.getText().equals("") || titleTextbox.getText().equals(""))){
				if(titleTextbox.getText().equals("")){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter the blank field");
				popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
				popup.show();
				return false;
			}
			else if(titleNewsItem.equals("-------Please Select-------")){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select the newstitles");
				popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
				popup.show();
				return false;
			}
			else if(appliedPrimaryTags.getSelectedIndex()<0){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select the Primary Tags");
				popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
				popup.show();
				return false;
			}
			else if(priorityListBox.getSelectedIndex() == 0){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select a news priority");
				popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
				popup.show();
				return false;
			}
			else if(appliedTagListBox.getItemCount() == 0 && tagListBox.getItemCount() == 0){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select tags to apply to the news");
				popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
				popup.show();
				return false;
			}
			/*else if(!urlTextbox.getText().matches("^((https?|ftp)://|(www|ftp)\\b.)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter the Valid NewsItem Url");
				popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
				popup.show();
				return false;
			}*/
		}

		else if(result == false){
			return false;
		}
		return true;
	}

	public boolean validateDate(){
		String userDate = dateTextbox.getText();
		LHDateValidator dateValidator = new LHDateValidator();
		boolean result = dateValidator.validate(userDate);
		return result;
	}

	public NewsItems getNewsItems() {
		return newsItems;
	}

	public void setNewsItems(NewsItems newsItems) {
		this.newsItems = newsItems;
	}

	public void onSubmit(FormSubmitEvent event) {
		/*String imageurl = fileUpload.getFilename();
		if(imageurl.isEmpty()){
			flex.getWidget(12, 4).setVisible(false);
//			event.setCancelled(true);
		}*/
	}

	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(ClickEvent event) {
		
		Widget sender =(Widget) event.getSource();
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Save")){
				buttonReset.setEnabled(false);
				userseletedtagsStr=null;
				boolean valid = validate();
				if(valid){
					for(int i=0;i<appliedTagListBox.getItemCount();i++){
						String taglist = appliedTagListBox.getItemText(i);
						if(userseletedtagsStr==null){
							userseletedtagsStr = taglist+";";
						}else{
							userseletedtagsStr = userseletedtagsStr + taglist+";";
						}
					}
					
					for(int i=0;i<tagListBox.getItemCount();i++){
						String taglist = tagListBox.getItemText(i);
						if(!appliedTagList.contains(taglist)){
							if(userseletedtagsStr==null){
								userseletedtagsStr = taglist+";";
							}
							else{
								userseletedtagsStr = userseletedtagsStr + taglist+";";
							}
						}
					}
					userseletedtagsStr = userseletedtagsStr + appliedPrimaryTags.getValue(appliedPrimaryTags.getSelectedIndex())+";";
					userselectedTextbox.setText(userseletedtagsStr);
					
					String str = Integer.toString(newsItemId);
					newscenteridtbBox.setText(str);
					
					if(chkLocked.getValue())
						isLocked.setValue("1");
					else
						isLocked.setValue("0");
					
					String gwturlbase = GWT.getModuleBaseURL();
					String url = gwturlbase.substring(0, gwturlbase.length()-1);
					form.setAction(url +"/editNewsItem");
					form.submit();
				}
				else{
					System.out.println("It is returning false");
				}
			}
			else if(button.getText().equals("Reset")){
				addTheNewsContent(resetIndex);
			}
			else if(button.getText().equals("Remove")){
				if(tagListBox.getItemCount() == 0){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to be removed");
					popup.setPopupPosition(dock.getAbsoluteLeft()+500, dock.getAbsoluteTop());
					popup.show();
				}
				else{				
					int i =0;
					while(i < tagListBox.getItemCount()){
						if(tagListBox.isItemSelected(i)){
							tagListBox.removeItem(i);
						}
						else{
							i++;
						}
					}
					
				}
			}
			else if(button.getText().equals("Delete")){
				if(appliedTagListBox.getItemCount() == 0){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to be removed");
					popup.setPopupPosition(dock.getAbsoluteLeft()+300, dock.getAbsoluteTop()+500);
					popup.show();
				}
				else{				
					int i =0;
					while(i < appliedTagListBox.getItemCount()){
						if(appliedTagListBox.isItemSelected(i)){
							String name = appliedTagListBox.getItemText(i);
							appliedTagList.remove(name);
							appliedTagListBox.removeItem(i);
						}
						else{
							i++;
						}
					}
					
				}
			}
		}
		else if(sender instanceof ListBox){
			ListBox listbox = (ListBox)sender;
			if(listbox.getName().equals("categoryList")){
				int index = categoryListBox.getSelectedIndex();
				String categoryName = categoryListBox.getItemText(index);
				getTagName(industryName,categoryName);
			}
			else if(listbox.getName().equals("TagNameListBox")){
				int index = listbox.getSelectedIndex();
				int count = 0;
				String name = listbox.getItemText(index);
				
				if(tagListBox.getItemCount() !=0){
					for(int i=0; i< tagListBox.getItemCount(); i++){
						if(name.equals(tagListBox.getItemText(i))){
							count++;
						}
					}
					if(count == 0){
						tagListBox.addItem(name);
					}
				}
				else{
					tagListBox.addItem(name);
				}
				
				tagListBox.setName("userSelectedTags");
				userSelectedTags = tagListBox.getSelectedIndex();
			}
			else if(listbox.getName().equals("categoriesList")){
				int index = selectedtagListbox.getSelectedIndex();
				String categoryName = selectedtagListbox.getItemText(index);
				editTags = true;
				getTagName(industryName,categoryName);
			}
			else if(listbox.getName().equals("userSelectedTags")){
				userRemoveTags =  tagListBox.getSelectedIndex();
			}
		}
		else if(sender instanceof CheckBox){
			CheckBox chkBox = (CheckBox) sender;
			if(chkBox.equals(chkLocked)){
				if(chkLocked.getValue())
					isLocked.setValue("1");
				else
					isLocked.setValue("0");
			}
		}
		
	}

	@Override
	public void onChange(ChangeEvent event) {
		
		Widget sender =(Widget) event.getSource();
		
		if(sender instanceof ListBox){
			ListBox listbox = (ListBox)sender;
			if(listbox.getName().equals("TagList")){
				int index = listbox.getSelectedIndex();
				String tagName = listbox.getItemText(index);
				if(!tagName.equals("-----Please Select-----")){
					getNewsItemForThisTag(industryName,tagName);
					clearAllTheFields();
				}
				else{
					clearAllTheFields();
				}
			}
			else if(listbox.getName().equals("NewsTitlesOfTag")){
				newsTitleListbox.setTitle(newsTitleListbox.getValue(newsTitleListbox.getSelectedIndex()));
				int index = listbox.getSelectedIndex();
				String titleNewsItem = listbox.getItemText(index);
				System.out.println("The selected Newsitem Title is>>>>>>>"+titleNewsItem);
				addTheNewsContent(index);
			}
			if(listbox.getName().equals("categoryList")){
				int index = categoryListBox.getSelectedIndex();
				String categoryName = categoryListBox.getItemText(index);
				getTagName(industryName,categoryName);
			}
			else if(listbox.getName().equals("TagNameListBox")){
				int index = listbox.getSelectedIndex();
				int count = 0;
				String name = listbox.getItemText(index);
				
				if(tagListBox.getItemCount() !=0){
					for(int i=0; i< tagListBox.getItemCount(); i++){
						if(name.equals(tagListBox.getItemText(i))){
							count++;
						}
					}
					if(count == 0){
						tagListBox.addItem(name);
					}
				}
				else{
					tagListBox.addItem(name);
				}
				
				tagListBox.setName("userSelectedTags");
				userSelectedTags = tagListBox.getSelectedIndex();
			}
			else if(listbox.getName().equals("categoriesList")){
				int index = selectedtagListbox.getSelectedIndex();
				String categoryName = selectedtagListbox.getItemText(index);
				editTags = true;
				getTagName(industryName,categoryName);
			}
			else if(listbox.getName().equals("userSelectedTags")){
				userRemoveTags =  tagListBox.getSelectedIndex();
			}
			if(listbox.getName().equals("primaryTagList")){
				
				int index = listbox.getSelectedIndex();
				String tagName = listbox.getItemText(index);
				if(!tagName.equals("-----Please Select-----")){
					getNewsItemForThisTag(industryName,tagName);
					clearAllTheFields();
				}
				else{
					clearAllTheFields();
				}
			}
		}
		
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		clearAllTheFields();
		PopUpForForgotPassword popup = new PopUpForForgotPassword("The news item has been successfully updated.");
		popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
		popup.show();
	}

	@Override
	public void onSubmit(SubmitEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSubmitComplete(FormSubmitCompleteEvent event) {

		String imageurl = fileUpload.getFilename();
		if(imageurl.isEmpty()){
			flex.getWidget(12, 4).setVisible(false);
//			event.setCancelled(true);
		}
		
	}

	
}