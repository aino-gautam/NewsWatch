package com.admin.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import com.common.client.LogoutPage;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
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
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HorizontalPanel;
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
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;

public class ManageNewsItems extends Composite implements  MouseListener, FormHandler,
                                       ChangeHandler,ClickHandler,MouseOutHandler,MouseOverHandler,SubmitHandler,SubmitCompleteHandler{

	private FlexTable flex = new FlexTable();
	private DockPanel dock = new DockPanel();
	private VerticalPanel contanerPanel = new VerticalPanel();
	private DecoratorPanel decorator = new DecoratorPanel();
	private HorizontalPanel hpbuttonPanel = new HorizontalPanel();
	private ScrollPanel scroller = new ScrollPanel();
	private TextBox industryTextbox = new TextBox();
	private ListBox listboxtags = new ListBox();
	private ListBox tagListBox = new ListBox();
	private ListBox selectedtagListbox = new ListBox();
	private TextArea abstractTextarea = new TextArea();
	private TextArea contentTextarea = new TextArea();
	private TextBox newsTitleTextBox = new TextBox();
	private TextBox urlTextBox = new TextBox();
	private TextBox sourceTextBox = new TextBox();
	private TextBox dateTextBox = new TextBox();
	private HorizontalPanel hpanelForTags = new HorizontalPanel();
	private int index = 0;
	private Button subtractionButton = new Button("Remove");
	String industryname ="";
	int userindustryid;
	RootPanel panel;
	int userSelectedTags;
	int userRemoveTags;
	private FileUpload fileUpload = new FileUpload();
	private Label imagelbl = new Label("(upto 10KB)");
	int clientWidth;
	int clientHeight;
	private FormPanel form = new FormPanel();
	private TextBox userselectedTextbox = new TextBox();
	private String userseletedtagsStr = "";
	static boolean flag = true;

	public ManageNewsItems(String userindustryName,int industryid){
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		getIndustryNameFromSession();
		industryname = userindustryName;
		userindustryid = industryid;
		manageNewsItemInitialize();
		addInFlexTable();
		getStyleNames();

		//form.addFormHandler(this);
		form.addSubmitCompleteHandler(this);
		
		//subtractionButton.addClickListener(this);
		subtractionButton.addClickHandler(this);
		
		//tagListBox.addClickListener(this);
		tagListBox.addClickHandler(this);
		
		//selectedtagListbox.addClickListener(this);
		selectedtagListbox.addClickHandler(this);
		
		//listboxtags.addChangeListener(this);
		listboxtags.addChangeHandler(this);
		
		//listboxtags.addClickListener(this);
	    listboxtags.addClickHandler(this); 
		//scroller.setWidth(clientWidth-100+"px");
		//scroller.setHeight(clientHeight-100+"px");
		scroller.setSize("100%", "100%");
		initWidget(dock);
	}

	public void manageNewsItemInitialize(){
		imagelbl.setStylePrimaryName("lblimageMsg");
		industryTextbox.setEnabled(false);
		industryTextbox.setName("industryTextBox");
		newsTitleTextBox.setName("newstitleTextBox");
		abstractTextarea.setName("abstractNewsTextArea");
		contentTextarea.setName("contentTextArea");
		urlTextBox.setName("urlTextBox");
		sourceTextBox.setName("sourceTextBox");
		dateTextBox.setName("dateTextBox");
		fileUpload.setName("imageFileUpload");
		

		selectedtagListbox.setName("categoryList");
		listboxtags.setName("TagNameListBox");
		tagListBox.setName("userSelectedTags");

		userselectedTextbox.setName("userSelectedTagTextbox");
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setWidget(flex);

		dock.setSpacing(10);
		hpbuttonPanel.setSpacing(10);
		hpbuttonPanel.add(createButton("Save"));
		hpbuttonPanel.add(createButton("Clear"));

		subtractionButton.setStylePrimaryName("buttonAddSub");
	//	hpanelForTags.setSpacing(2);
		hpanelForTags.setWidth("100%");
		hpanelForTags.add(selectedtagListbox);
		hpanelForTags.add(listboxtags);
		hpanelForTags.add(subtractionButton);
		hpanelForTags.add(tagListBox);
		hpanelForTags.add(userselectedTextbox);

		userselectedTextbox.setVisible(false);
		tagListBox.setMultipleSelect(true);
		listboxtags.setVisibleItemCount(10);
		tagListBox.setVisibleItemCount(10);
		//selectedtagListbox.setVisibleItemCount(10);

		scroller.add(form);
		decorator.add(scroller);
		decorator.setWidth("100%");
		dock.add(decorator, DockPanel.CENTER);
		dock.add(hpbuttonPanel, DockPanel.SOUTH);
		dock.setSize("100%", "100%");
		index = selectedtagListbox.getSelectedIndex();
	}

	public void getIndustryNameFromSession(){
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
					String Industry[] = (String[])result;
					String name = Industry[0];
					int industryid = Integer.parseInt(Industry[2]);
					String userSelectedIndustryName = Industry[3];
					industryTextbox.setText(userSelectedIndustryName);
					setIndustryname(userSelectedIndustryName);
					getCategoryName(getIndustryname());
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryNameFromSession()");
				}
			}
		};
		service.getIndustryNameFromSession(callback);
	}

	public void getStyleNames(){
		listboxtags.setStylePrimaryName("taglistboxNewsItems");
		tagListBox.setStylePrimaryName("taglistboxNewsItems");
		selectedtagListbox.setStylePrimaryName("taglistboxNewsItems");
		flex.setStylePrimaryName("flexManageNewsTags");
		industryTextbox.setStylePrimaryName("textboxNewsItems");
		abstractTextarea.setStylePrimaryName("textAreaNewsItems");
		contentTextarea.setStylePrimaryName("textAreaNewsItems");
		newsTitleTextBox.setStylePrimaryName("textboxNewsItems");
		urlTextBox.setStylePrimaryName("textboxNewsItems");
		sourceTextBox.setStylePrimaryName("textboxNewsItems");
		dateTextBox.setStylePrimaryName("textboxNewsItems");
		contanerPanel.setStylePrimaryName("basePanel");
	}

	public void addInFlexTable(){
		String dateText="(YYYY-MM-DD)";
		FlexCellFormatter formatter = flex.getFlexCellFormatter();
		formatter.setColSpan(0, 0, 3);
		flex.setText(0, 0, "Fields marked with (*) are mandatory");
		flex.setWidget(2, 0, createLabel("Industry"));
		flex.setWidget(2, 2, industryTextbox);
		flex.setWidget(3, 0, createLabel("Tags*"));
		flex.setWidget(3, 2, hpanelForTags);
		flex.setWidget(4, 0, createLabel("News Title*"));
		flex.setWidget(4, 2, newsTitleTextBox);
		flex.setWidget(5, 0, createLabel("Abstract*"));
		flex.setWidget(5, 2, abstractTextarea);
		flex.setWidget(6, 0, createLabel("Content"));
		flex.setWidget(6, 2, contentTextarea);
		flex.setWidget(7, 0, createLabel("URL*"));
		flex.setWidget(7, 2, urlTextBox);
		flex.setWidget(8, 0, createLabel("Item Date*"));
		flex.setWidget(8, 2, dateTextBox);
		flex.setText(9, 2, dateText);
		flex.setWidget(11, 0, createLabel("Source"));
		flex.setWidget(11, 2, sourceTextBox);
		flex.setWidget(12, 0, createLabel("Upload Image"));
		flex.setWidget(12, 2, fileUpload);
		flex.setWidget(13,2, imagelbl);
	}

	public Label createLabel(String text){
		Label lb = new Label(text);
		lb.setStylePrimaryName("labelFlexEditNewsItems");
		return lb;
	}

	public Button createButton(String text){
		Button button = new Button(text);
		button.setStylePrimaryName("buttonOkAdmin");
		//button.addClickListener(this);
		button.addClickHandler(this);
		return button;
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
					int i=0;
					while(iter.hasNext())
					{
						i++;
						TagItemInformation tag = (TagItemInformation)iter.next();
						String name = tag.getTagName();
						selectedtagListbox.addItem(name);
						index = selectedtagListbox.getSelectedIndex();
					}
					selectedtagListbox.setVisibleItemCount(i);	
				}
				catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryName()");
				}
			}
		};
		service.getCategoryNames(userindustryid,industryName, callback);
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
		listboxtags.clear();
		Iterator iter = list.iterator();
		while(iter.hasNext()){
			TagItemInformation tag = (TagItemInformation)iter.next();
			int index = tag.getTagItemId();
			String name = tag.getTagName();
			listboxtags.addItem(name);
		}
	}

	 public void sortTags(ArrayList list){
			final Comparator<TagItemInformation> NAME_ORDER = new Comparator<TagItemInformation>(){

				public int compare(TagItemInformation u1, TagItemInformation u2) {
					
					return u1.getTagName().compareTo(u2.getTagName());
				}
			};
			
			Collections.sort(list,NAME_ORDER);
		}
	
	public void onModuleLoad(){
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}

	public void onChange(Widget sender){
		/*if(sender instanceof ListBox){
			ListBox listbox = (ListBox)sender;
			if(listbox.getName().equals("TagNameListBox")){
			}
		}*/
	}

	public void onClick(Widget sender) {
		/*NewsItemsAdminInformation newsitemInfo = new NewsItemsAdminInformation();
		ArrayList arraytaglist = new ArrayList();
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Save")){
				boolean valid = validate();
				if(valid){
					for(int i=0;i<tagListBox.getItemCount();i++){
						String taglist = tagListBox.getItemText(i);
						if(userseletedtagsStr==null){
							userseletedtagsStr = taglist+";";
						}else{
							userseletedtagsStr = userseletedtagsStr + taglist+";";
						}
					}
					userselectedTextbox.setText(userseletedtagsStr);
					String gwturlbase = GWT.getModuleBaseURL();
					String url = gwturlbase.substring(0, gwturlbase.length()-1);
					form.setAction(url +"/servlet/GetImageUploadService");
					form.submit();
				}
			}
			else if(button.getText().equals("Clear")){
				clearAllTheFields();
			}
			else if(button.getText().equals("Remove")){
				if(tagListBox.getItemCount() == 0){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to be removed");
					popup.setPopupPosition(600, 300);
					popup.show();
				}
				else{				
					int i =0;
					while(i <tagListBox.getItemCount()){
						if(tagListBox.isItemSelected(i)){
							tagListBox.removeItem(i);
						}
						else{
							i++;
						}
					}
					
				}
			}
		}
		else if(sender instanceof Label){
			Label label = (Label) sender;
			if(label.getText().equals("logout")){
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);
				//Window.open("http://server:8080/com.login.login/login.html", "_self", "");
				//Window.open("http://122.169.111.248:8080/NewsCenter/index.html", "_self", "");
			}
			else if(label.getText().equals("Delete Newsitems")){
				DeleteNewsItems deleteNewsitem = new DeleteNewsItems(industryname,userindustryid);
				deleteNewsitem.onModuleLoad();
			}
			else if(label.getText().equals("Edit Newsitems")){
				EditNewsItems edit = new EditNewsItems(industryname,userindustryid);
				edit.onModuleLoad();
			}
			else if(label.getText().equals("Back")){
				AdminPage admin = new AdminPage();
				admin.onModuleLoad();
			}
		}
		else if(sender instanceof ListBox){
			ListBox listbox =(ListBox)sender;
			if(listbox.getName().equals("TagNameListBox")){
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
			else if(listbox.getName().equals("categoryList")){
				int index = selectedtagListbox.getSelectedIndex();
				String categoryName = selectedtagListbox.getItemText(index);
				getTagName(industryname,categoryName);
			}
			else if(listbox.getName().equals("userSelectedTags")){
				userRemoveTags =  tagListBox.getSelectedIndex();
			}
		}	*/
	}

	public boolean validate(){
		flag = true;
		boolean result = validateDate();
		int tagListCount = tagListBox.getItemCount();

		if(flag == true){
			if((abstractTextarea.getText().equals("") || newsTitleTextBox.getText().equals("") || urlTextBox.getText().equals(""))){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter the required field");
				popup.setPopupPosition(600, 300);
				popup.show();
				return false;
			}
			else if(tagListCount==0){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select the categories and tags.");
				popup.setPopupPosition(600, 300);
				popup.show();
				return false;
			}
		}

		if(result==false){
			return false;
		}
		return true;
	}

	public boolean validateDate(){
		String userDate =dateTextBox.getText();
		DateValidator dateValidator = new DateValidator();
		boolean result = dateValidator.validate(userDate);
		return result;
	}

	public void clearAllTheFields(){
		listboxtags.clear();
		tagListBox.clear();
		if(!userseletedtagsStr.equals(null))
			userseletedtagsStr = "";
		abstractTextarea.setText("");
		contentTextarea.setText("");
		newsTitleTextBox.setText("");
		urlTextBox.setText("");
		sourceTextBox.setText("");
		dateTextBox.setText("");
		flex.remove(fileUpload);
		FileUpload upload = new FileUpload();
		upload.setVisible(true);
		upload.setName("imageFileUpload");
		flex.setWidget(12, 2, upload);
	    	//DOM.setElementProperty(fileUpload.getElement(), "value", "true");

	}

	public void setAllNewsItemFields(NewsItemsAdminInformation newsitemInfo){
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				PopUpForForgotPassword popup = new PopUpForForgotPassword("There was some problem in saving please check");
				popup.setPopupPosition(600, 300);
				popup.show();
			}

			public void onSuccess(Object result) {
				try{
					clearAllTheFields();
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Your News Item Fields are been saved");
					popup.setPopupPosition(600, 300);
					popup.show();
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in setAllNewsItemFields()");
				}
			}
		};
		service.setAllNewsItemFields(newsitemInfo, callback);
	}

	public void onMouseDown(Widget arg0, int arg1, int arg2) {

	}

	public void onMouseEnter(Widget sender) {
		/*if(sender instanceof Label)
		{
			Label label = (Label)sender;
			if(label.getText().equals("Delete Newsitems"))
			{
			}
			else if(label.getText().equals("Edit Newsitems"))
			{
			}
			else if(label.getText().equals("Back"))
			{
			}
		}*/
	}

	public void onMouseLeave(Widget sender) {
		/*if(sender instanceof Label)
		{
			Label label = (Label)sender;
			if(label.getText().equals("Delete Newsitems"))
			{
			}
			else if(label.getText().equals("Edit Newsitems"))
			{

			}
			else if(label.getText().equals("Back"))
			{
			}
		}*/
	}

	public void onMouseMove(Widget arg0, int arg1, int arg2) {
		
	}

	public void onMouseUp(Widget arg0, int arg1, int arg2) {
		
	}

	public String getIndustryname() {
		return industryname;
	}

	public void setIndustryname(String industryname) {
		this.industryname = industryname;
	}

	public void onSubmit(FormSubmitEvent event) {
		String imageurl = fileUpload.getFilename();
	}

	public void onSubmitComplete(FormSubmitCompleteEvent arg0) {
		clearAllTheFields();
		PopUpForForgotPassword popup = new PopUpForForgotPassword("Your News Item Fields are been saved");
		popup.setPopupPosition(600, 300);
		popup.show();
	}

	

	@Override
	public void onClick(ClickEvent event) {
		
		Widget sender = (Widget)event.getSource();
		
		NewsItemsAdminInformation newsitemInfo = new NewsItemsAdminInformation();
		ArrayList arraytaglist = new ArrayList();
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Save")){
				boolean valid = validate();
				if(valid){
					for(int i=0;i<tagListBox.getItemCount();i++){
						String taglist = tagListBox.getItemText(i);
						if(userseletedtagsStr==null){
							userseletedtagsStr = taglist+";";
						}else{
							userseletedtagsStr = userseletedtagsStr + taglist+";";
						}
					}
					
					userselectedTextbox.setText(userseletedtagsStr);
					String gwturlbase = GWT.getModuleBaseURL();
					String url = gwturlbase.substring(0, gwturlbase.length()-1);
					form.setAction(url +"/servlet/GetImageUploadService");
					form.submit();
				}
			}
			else if(button.getText().equals("Clear")){
				clearAllTheFields();
			}
			else if(button.getText().equals("Remove")){
				if(tagListBox.getItemCount() == 0){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to be removed");
					popup.setPopupPosition(600, 300);
					popup.show();
				}
				else{				
					int i =0;
					while(i <tagListBox.getItemCount()){
						if(tagListBox.isItemSelected(i)){
							tagListBox.removeItem(i);
						}
						else{
							i++;
						}
					}
					
				}
			}
		}
		else if(sender instanceof Label){
			Label label = (Label) sender;
			if(label.getText().equals("logout")){
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);
				//Window.open("http://server:8080/com.login.login/login.html", "_self", "");
				//Window.open("http://122.169.111.248:8080/NewsCenter/index.html", "_self", "");
			}
			else if(label.getText().equals("Delete Newsitems")){
				DeleteNewsItems deleteNewsitem = new DeleteNewsItems(industryname,userindustryid);
				deleteNewsitem.onModuleLoad();
			}
			else if(label.getText().equals("Edit Newsitems")){
				EditNewsItems edit = new EditNewsItems(industryname,userindustryid);
				edit.onModuleLoad();
			}
			else if(label.getText().equals("Back")){
				AdminPage admin = new AdminPage();
				admin.onModuleLoad();
			}
		}
		else if(sender instanceof ListBox){
			ListBox listbox =(ListBox)sender;
			if(listbox.getName().equals("TagNameListBox")){
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
			else if(listbox.getName().equals("categoryList")){
				int index = selectedtagListbox.getSelectedIndex();
				String categoryName = selectedtagListbox.getItemText(index);
				getTagName(industryname,categoryName);
			}
			else if(listbox.getName().equals("userSelectedTags")){
				userRemoveTags =  tagListBox.getSelectedIndex();
			}
		}	
		
	}

	@Override
	public void onChange(ChangeEvent event) {
		
		Widget sender = (Widget)event.getSource();
		if(sender instanceof ListBox){
			ListBox listbox = (ListBox)sender;
			if(listbox.getName().equals("TagNameListBox")){
			}
		}
		
	}

	
	

	

	

	@Override
	public void onMouseOver(MouseOverEvent event) {
		

		Widget sender = (Widget)event.getSource();
		if(sender instanceof Label)
		{
			Label label = (Label)sender;
			if(label.getText().equals("Delete Newsitems"))
			{
			}
			else if(label.getText().equals("Edit Newsitems"))
			{
			}
			else if(label.getText().equals("Back"))
			{
			}
		}
		
	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		
		Widget sender = (Widget)event.getSource();
		if(sender instanceof Label)
		{
			Label label = (Label)sender;
			if(label.getText().equals("Delete Newsitems"))
			{
			}
			else if(label.getText().equals("Edit Newsitems"))
			{

			}
			else if(label.getText().equals("Back"))
			{
			}
		}
		
	}

	@Override
	public void onSubmit(SubmitEvent event) {
		
		
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		// TODO Auto-generated method stub
		
	}

	
}