package com.admin.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
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
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.common.client.Footer;
import com.common.client.ManageHeader;
import com.common.client.PopUpForForgotPassword;

public class EditNewsItems extends Composite implements  MouseListener, FormHandler,
										ChangeHandler,ClickHandler,MouseMoveHandler,SubmitCompleteHandler,SubmitHandler  {
	private ManageHeader header = new ManageHeader();
	private DockPanel dock = new DockPanel();
	private HorizontalPanel hpanel = new HorizontalPanel();
	private Label label = new Label("Please select tag  to edit");
	private DecoratorPanel decoratorforlabel = new DecoratorPanel();
	private ScrollPanel scroller = new ScrollPanel();
	private DecoratorPanel decoratorflex = new DecoratorPanel();
	private HorizontalPanel hpbuttonPanel = new HorizontalPanel();
	private Button button = new Button("Save");
	private Button buttonReset = new Button("Reset");
	private Button buttonback = new Button("Back");
	private Button subtractionButton = new Button("Remove");
	private Button deleteButton = new Button("Delete");
	private FlexTable flex = new FlexTable();
	private TextBox industryTextbox = new TextBox();
	private TextBox urlTextbox = new TextBox();
	private TextBox titleTextbox = new TextBox();
	private TextBox dateTextbox = new TextBox();
	private TextBox sourceTextbox = new TextBox();
	private TextArea abstractTextarea = new TextArea();
	private TextArea contentTextarea = new TextArea();
	private VerticalPanel vpanel = new VerticalPanel();
//	private Label logoutlabel = new Label("logout");
	private HorizontalPanel hpanellink = new HorizontalPanel();
	private HashMap<Integer,ArrayList> manageNews = new HashMap<Integer, ArrayList>();
	private ListBox categoryListBox = new ListBox();
	private ListBox tagList = new ListBox();
	private ListBox newsTitleListbox = new ListBox();
	private ListBox listboxtags = new ListBox();
	private ListBox tagListBox = new ListBox();
	private ListBox selectedtagListbox = new ListBox();
	private ListBox appliedTagListBox = new ListBox();
	private HorizontalPanel hpanelForTags = new HorizontalPanel();
	private HorizontalPanel tagPanel = new HorizontalPanel();
	private HorizontalPanel appliedTagPanel = new HorizontalPanel();
	private TextBox newscenteridtbBox = new TextBox();
	private TextBox userselectedTextbox = new TextBox();
	private FormPanel form = new FormPanel();
	static boolean flag = true;
	private String userseletedtagsStr = "";
	private ArrayList appliedTagList = new ArrayList();
	private FileUpload fileUpload = new FileUpload();
	int newsItemId;
	int resetIndex;
	int index = 0;
	RootPanel panel;
	String industryName;
	int userSelectedIndustryId;
	int clientWidth;
	int clientHeight;
	private NewsItems newsItems;
	int indextag = 0;
	int userSelectedTags;
	int userRemoveTags; 
	private boolean editTags = false;
	
	public EditNewsItems(String userIndustryName, int industryId){
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		industryName = userIndustryName;
		userSelectedIndustryId = industryId;
		getStyleNames();
		addInFlexTable();
		getCategoryName(industryName);

		industryTextbox.setText(industryName);
		industryTextbox.setEnabled(false);

		//form.addFormHandler(this);
		
		form.addSubmitHandler(this);
		form.addSubmitCompleteHandler(this);
		//hpanelForTags.setSpacing(5);
		hpanelForTags.add(categoryListBox);
		hpanelForTags.add(tagList);
		
		//newsTitleListbox.addChangeListener(this);
		newsTitleListbox.addChangeHandler(this);
		
		//tagList.addChangeListener(this);
		tagList.addChangeHandler(this);
		
		//tagList.addClickListener(this);
		tagList.addClickHandler(this);
		
		//categoryListBox.addChangeListener(this);
		categoryListBox.addChangeHandler(this);
		
		//categoryListBox.addClickListener(this);
		categoryListBox.addChangeHandler(this);
		tagList.setVisibleItemCount(10);
		categoryListBox.setVisibleItemCount(10);
		
		//button.addClickListener(this);
		button.addClickHandler(this);
		
		//buttonReset.addClickListener(this);
		buttonReset.addClickHandler(this);
		
		//buttonback.addClickListener(this);
		buttonback.addClickHandler(this);
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
		hpbuttonPanel.add(button);
		hpbuttonPanel.add(buttonReset);
		//scroller.setHeight(clientHeight-100+"px");
		//scroller.setWidth(clientWidth-100+"px");
		scroller.setSize("100%", "100%");
		scroller.add(form);
		decoratorflex.add(scroller);
		dock.add(vpanel,DockPanel.NORTH);
		dock.add(decoratorflex,DockPanel.CENTER);
		dock.add(hpbuttonPanel,DockPanel.SOUTH);
		decoratorflex.setWidth("100%");
		dock.setSize("100%", "100%");
		titleTextbox.setName("newstitleTextBox");
		industryTextbox.setName("industryTextBox");
		urlTextbox.setName("urlTextBox");
		dateTextbox.setName("dateTextBox");
		sourceTextbox.setName("sourceTextBox");
		abstractTextarea.setName("abstractNewsTextArea");
		contentTextarea.setName("contentTextArea");
		tagList.setName("userSelectedTags");
		fileUpload.setName("imageFileUpload");
		newscenteridtbBox.setName("NewsCenterID");
		newscenteridtbBox.setVisible(false);
		selectedtagListbox.setName("categoriesList");
		listboxtags.setName("TagNameListBox");
		tagListBox.setName("userSelectedTags");
		userselectedTextbox.setName("userSelectedTagTextbox");
		//subtractionButton.addClickListener(this);
		subtractionButton.addClickHandler(this);
		
		//deleteButton.addClickListener(this);
		deleteButton.addClickHandler(this);
		
		//tagListBox.addClickListener(this);
		tagListBox.addClickHandler(this);
		
		//selectedtagListbox.addClickListener(this);
		selectedtagListbox.addClickHandler(this);
		
		//listboxtags.addChangeListener(this);
		listboxtags.addChangeHandler(this);
		
		//listboxtags.addClickListener(this);
		listboxtags.addClickHandler(this);
		
		//appliedTagListBox.addClickListener(this);
		appliedTagListBox.addClickHandler(this);
		
		appliedTagListBox.setMultipleSelect(true);
		
		
		appliedTagListBox.setVisibleItemCount(6);
		//appliedTagPanel.setSpacing(2);
		appliedTagPanel.add(appliedTagListBox);
		appliedTagPanel.add(deleteButton);
		appliedTagPanel.setHeight("100%");
		//tagPanel.setSpacing(2);
		tagPanel.setWidth("100%");
		tagPanel.add(selectedtagListbox);
		tagPanel.add(listboxtags);
		tagPanel.add(subtractionButton);
		tagPanel.add(tagListBox);
		tagPanel.add(userselectedTextbox);
		userselectedTextbox.setVisible(false);
		tagListBox.setMultipleSelect(true);
		
		
		
		listboxtags.setVisibleItemCount(10);
		tagListBox.setVisibleItemCount(10);
		selectedtagListbox.setVisibleItemCount(10);
		
		index = categoryListBox.getSelectedIndex();
		initWidget(dock);
	}

	public void addInFlexTable(){
		newsItems = getNewsItems();
		FlexCellFormatter formatter = flex.getFlexCellFormatter();
		flex.setWidget(0,2,createLabel("Industry"));
		flex.setWidget(1,2,createLabel("Tags"));
		flex.setWidget(2,2,createLabel("News Title"));
		
		flex.setWidget(5,2,createLabel("Title"));
		flex.setWidget(6,2,createLabel("News Abstract"));
		flex.setWidget(7,2,createLabel("News Content"));
		flex.setWidget(8,2,createLabel("URL"));
		flex.setWidget(9,2,createLabel("Date"));
		flex.setWidget(11, 2, createLabel("Source"));
		flex.setWidget(12, 2, createLabel("Upload Image"));
		flex.setWidget(13, 2, createLabel("Applied Tags")); //new
		flex.setWidget(14, 2, createLabel("Add Tags")); //new
		
		flex.setWidget(0,5,industryTextbox);
		flex.setWidget(1,5,hpanelForTags);
		flex.setWidget(2,5,newsTitleListbox);
		
		flex.setWidget(5,5,titleTextbox);
		formatter.setColSpan(6, 5, 5);
		flex.setWidget(6,5,abstractTextarea);
		formatter.setColSpan(7, 5, 5);
		flex.setWidget(7,5,contentTextarea);
		flex.setWidget(8,5,urlTextbox);
		flex.setWidget(9,5,dateTextbox);
		flex.setText(10, 5, "(YYYY-MM-DD)");
		flex.setWidget(11, 5, sourceTextbox);
		flex.setWidget(12, 5, fileUpload);
		flex.setWidget(13, 5, appliedTagPanel);
		flex.setWidget(14, 5, tagPanel);
		flex.setWidget(15, 0, newscenteridtbBox);
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
		titleTextbox.setStylePrimaryName("textboxNewsItems");
		newsTitleListbox.setStylePrimaryName("textboxNewsItems");
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
		
	}
	
	public void onModuleLoad() {
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}

	public void onChange(Widget sender){
		/*if(sender instanceof ListBox){
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
				int index = listbox.getSelectedIndex();
				String titleNewsItem = listbox.getItemText(index);
				System.out.println("The selected Newsitem Title is>>>>>>>"+titleNewsItem);
				addTheNewsContent(index);
			}
		}*/
	}

	public void getNewsItemForThisTag(String industryName,String tagName){
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}

			public void onSuccess(Object result) {
				try{
					ArrayList list = (ArrayList)result;
					addTheContent(list);
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getUserInfo()");
				}
			}
		};
		service.getNewsItemForThisTag(industryName, tagName, callback);
	}

	public void addTheContent(ArrayList list){
		ArrayList contentContent;
		int counter= 1;
		Date date;
		if(list.size()!=0){
			newsTitleListbox.addItem("-------Please Select-------");
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				contentContent = new ArrayList();
				NewsItems newsItems = (NewsItems)iter.next();
				String titleStr = (String)newsItems.getNewsTitle();
				String content= (String)newsItems.getNewsContent();
				String abstractNews = (String)newsItems.getAbstractNews();
				String url= (String)newsItems.getUrl();
				date= (Date)newsItems.getNewsDate();
				String source = newsItems.getNewsSource();
				String imageurl = (String)newsItems.getImageUrl();
				int newsId = (Integer)newsItems.getNewsId();
				ArrayList associatedtags = (ArrayList)newsItems.getAssociatedTagList();
				newsTitleListbox.addItem(titleStr);
				newsTitleListbox.setName("NewsTitlesOfTag");

				contentContent.add(titleStr);
				contentContent.add(content);
				contentContent.add(abstractNews);
				contentContent.add(url);
				contentContent.add(date);
				contentContent.add(source);
				contentContent.add(newsId);
				contentContent.add(imageurl);
				contentContent.add(associatedtags);
				manageNews.put(counter, contentContent);

				counter++;
			}
		}
		else{
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Sorry there is no News for this tag");
			popup.setPopupPosition(600, 300);
			popup.show();
		}
	}

	public void addTheNewsContent(int index){
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
	//			String date= (String)abstractNews.get(4);
				newsItemId = (Integer)abstractNews.get(6);
				String image = (String)abstractNews.get(7);
				ArrayList tags = (ArrayList)abstractNews.get(8);
				
				titleTextbox.setText(title);
				abstractTextarea.setText(abstractnews);
				contentTextarea.setText(content);
				urlTextbox.setText(url);
				dateTextbox.setText(date.toString());
				sourceTextbox.setText(source);
				sortTags(tags);
				if(appliedTagListBox.getItemCount()!=0)
					appliedTagListBox.clear();
				if(appliedTagListBox.getItemCount() == 0){
					Iterator iter = tags.iterator();
					while(iter.hasNext()){
						TagItemInformation tagiteminfo = (TagItemInformation)iter.next();
						appliedTagListBox.addItem(tagiteminfo.getTagName());
						appliedTagList.add(tagiteminfo.getTagName());
					}
				}
				if(listboxtags.getItemCount()!=0)
					listboxtags.clear();
				if(tagListBox.getItemCount()!=0)
					tagListBox.clear();
			
				Image img = new Image(image,0,0,68,68);
				//img.setSize("35", "35");
				flex.setWidget(12, 4, img);
//				fileUpload.setName(image);
			}
			else{
				titleTextbox.setText("");
				abstractTextarea.setText("");
				contentTextarea.setText("");
				urlTextbox.setText("");
				dateTextbox.setText("");
				sourceTextbox.setText("");
				appliedTagListBox.clear();
			}
		}
	}

	public void getCategoryName(String industryName){
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				try{
					ArrayList list = (ArrayList)result;
					sortTags(list);
					Iterator iter = list.iterator();
					while(iter.hasNext())
					{
						TagItemInformation tag = (TagItemInformation)iter.next();
							String name = tag.getTagName();
							categoryListBox.addItem(name);
							selectedtagListbox.addItem(name);
							index = categoryListBox.getSelectedIndex();
							categoryListBox.setName("categoryList");
					}
				}catch(Exception ex)
				{
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

public void getTagName(String industryName,String categoryName){
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

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
	public void onClick(Widget sender){
		/*NewsItemsAdminInformation newsitemInfo = new NewsItemsAdminInformation();
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Save")){
				buttonReset.setEnabled(false);

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
					userselectedTextbox.setText(userseletedtagsStr);
					
					String str = Integer.toString(newsItemId);
					newscenteridtbBox.setText(str);
					
					String gwturlbase = GWT.getModuleBaseURL();
					String url = gwturlbase.substring(0, gwturlbase.length()-1);
					form.setAction(url +"/servlet/GetEditImageUploadService");
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
					popup.setPopupPosition(600, 300);
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
					popup.setPopupPosition(600, 300);
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
		}*/
	}

	public void clearAllTheFields(){
		newsTitleListbox.clear();
		abstractTextarea.setText("");
		contentTextarea.setText("");
		titleTextbox.setText("");
		urlTextbox.setText("");
		dateTextbox.setText("");
		sourceTextbox.setText("");
		listboxtags.clear();
		tagListBox.clear();
		appliedTagListBox.clear();
		flex.clearCell(12, 4);
		if(!userseletedtagsStr.equals(null))
			userseletedtagsStr = "";
	}

	public boolean validate(){
		flag = true;
		boolean result = validateDate();

		if(flag == true){
			indextag = tagList.getSelectedIndex();
			String tagName = tagList.getItemText(indextag);
			int index = newsTitleListbox.getSelectedIndex();
			String titleNewsItem = newsTitleListbox.getItemText(index);

			if((abstractTextarea.getText().equals("") || titleTextbox.getText().equals("") || urlTextbox.getText().equals(""))){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter the blank field");
				popup.setPopupPosition(600, 300);
				popup.show();
				return false;
			}

			else if(tagName.equals("-----Please Select----")){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select the tag");
				popup.setPopupPosition(600, 300);
				popup.show();
				return false;
			}

			else if(titleNewsItem.equals("-------Please Select-------")){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select the newstitles");
				popup.setPopupPosition(600, 300);
				popup.show();
				return false;
			}
			else if(appliedTagListBox.getItemCount() == 0 && tagListBox.getItemCount() == 0){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select tags to apply to the news");
				popup.setPopupPosition(600, 300);
				popup.show();
				return false;
			}
		}

		else if(result == false){
			return false;
		}
		return true;
	}

	public boolean validateDate(){
		String userDate = dateTextbox.getText();
		DateValidator dateValidator = new DateValidator();
		boolean result = dateValidator.validate(userDate);
		return result;
	}

	public void onMouseDown(Widget arg0, int arg1, int arg2) {
		
	}

	public void onMouseEnter(Widget arg0) {
	
		//backLink.setStylePrimaryName("linkChangelabel"); 
	}
	
	public void onMouseLeave(Widget arg0) {
		
		//backLink.setStylePrimaryName("linklabel"); 
	}

	public void onMouseMove(Widget arg0, int arg1, int arg2) {
		
	}

	public void onMouseUp(Widget arg0, int arg1, int arg2) {
	
	}

	public NewsItems getNewsItems() {
		return newsItems;
	}

	public void setNewsItems(NewsItems newsItems) {
		this.newsItems = newsItems;
	}

	/*public void onSubmitComplete(FormSubmitCompleteEvent arg0) {
		clearAllTheFields();
		PopUpForForgotPassword popup = new PopUpForForgotPassword("Your News Item Fields are been saved");
		popup.setPopupPosition(600, 300);
		popup.show();
	}*/

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
		
		NewsItemsAdminInformation newsitemInfo = new NewsItemsAdminInformation();
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Save")){
				buttonReset.setEnabled(false);

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
					userselectedTextbox.setText(userseletedtagsStr);
					
					String str = Integer.toString(newsItemId);
					newscenteridtbBox.setText(str);
					
					String gwturlbase = GWT.getModuleBaseURL();
					String url = gwturlbase.substring(0, gwturlbase.length()-1);
					form.setAction(url +"/servlet/GetEditImageUploadService");
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
					popup.setPopupPosition(600, 300);
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
					popup.setPopupPosition(600, 300);
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
				int index = listbox.getSelectedIndex();
				String titleNewsItem = listbox.getItemText(index);
				System.out.println("The selected Newsitem Title is>>>>>>>"+titleNewsItem);
				addTheNewsContent(index);
			}
		}
		
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		
		clearAllTheFields();
		PopUpForForgotPassword popup = new PopUpForForgotPassword("Your News Item Fields are been saved");
		popup.setPopupPosition(600, 300);
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