package com.lighthouse.admin.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;

import com.admin.client.AdminInformationService;
import com.admin.client.AdminInformationServiceAsync;
import com.admin.client.TagItemInformation;
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
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.Hidden;
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
import com.google.gwt.user.datepicker.client.DateBox;
import com.lighthouse.admin.client.service.LHAdminInformationService;
import com.lighthouse.admin.client.service.LHAdminInformationServiceAsync;
/**
 * This class is for add new News Report
 * 
 * @author Milind Bharambe
 *
 */
public class AddNewsReport extends Composite implements  MouseListener, FormHandler,ValueChangeHandler,
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
	private TextBox urlTextBox = new TextBox();
	private TextBox titleTextBox = new TextBox();
	private TextBox sourceTextBox = new TextBox();
	private TextBox dateTextBox = new TextBox();
	private HorizontalPanel hpanelForTags = new HorizontalPanel();
	private Button subtractionButton = new Button("Remove");
	private int index = 0;
	String industryname ="";
	int userindustryid;
	RootPanel panel;
	int userSelectedTags;
	int userRemoveTags;
	private FileUpload fileUpload = new FileUpload();
	private Label imagelbl = new Label("(upto 10MB)");
	int clientWidth;
	int clientHeight;
	private FormPanel form = new FormPanel();
	private TextBox userselectedTextbox = new TextBox();
	private String userseletedtagsStr = "";
	static boolean flag = true;
	public DateBox dateBox = new DateBox() ;
	Hidden lifeSpanDateTextBox = new Hidden();
	private ListBox primaryTagList=new ListBox();
	boolean validFileSize = false;

	public AddNewsReport(String userindustryName,int industryid){
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		getIndustryNameFromSession();
		industryname = userindustryName;
		userindustryid = industryid;
		manageNewsItemInitialize();
		addInFlexTable();
		getStyleNames();
		form.addSubmitCompleteHandler(this);
		subtractionButton.addClickHandler(this);
		tagListBox.addClickHandler(this);
		selectedtagListbox.addClickHandler(this);
		listboxtags.addChangeHandler(this);
		listboxtags.addClickHandler(this); 
		scroller.setSize("100%", "100%");
		primaryTagList.addChangeHandler(this);
		dateBox.addValueChangeHandler(this);
		fillPrimaryTaglist();
		initWidget(dock);
	}

	public void manageNewsItemInitialize(){
		imagelbl.setStylePrimaryName("lblimageMsg");
		industryTextbox.setEnabled(false);
		industryTextbox.setName("industryTextBox");
		titleTextBox.setName("titleTextBox");
		abstractTextarea.setName("abstractTextArea");
		contentTextarea.setName("contentTextArea");
		urlTextBox.setName("urlTextBox");
		sourceTextBox.setName("sourceTextBox");
		dateTextBox.setName("dateTextBox");
		lifeSpanDateTextBox.setName("lifeSpanDateTextBox");
		fileUpload.setName("imageFileUpload");
		primaryTagList.setName("primaryTagList");
		primaryTagList.setVisibleItemCount(10);
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
		scroller.add(form);
		decorator.add(scroller);
		decorator.setWidth("100%");
		dock.add(decorator, DockPanel.CENTER);
		dock.add(hpbuttonPanel, DockPanel.SOUTH);
		dock.setSize("100%", "100%");
		index = selectedtagListbox.getSelectedIndex();
		
	}

	/**
	 * This method used to get all the Primary Tags from DB and add those in PrimaryTagListBox
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
						primaryTagList.addItem(name);
						
					}
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in fillPrimaryTaglist()");
				}
			}
		};
		service.fillprimaryTaglist(userindustryid,getIndustryname(),callback);
	
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
		titleTextBox.setStylePrimaryName("textboxNewsItems");
		urlTextBox.setStylePrimaryName("textboxNewsItems");
		sourceTextBox.setStylePrimaryName("textboxNewsItems");
		dateTextBox.setStylePrimaryName("textboxNewsItems");
		dateBox.setStylePrimaryName("textboxNewsItems");
		contanerPanel.setStylePrimaryName("basePanel");
		primaryTagList.setStylePrimaryName("taglistboxNewsItems");
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
		flex.setWidget(4, 0, createLabel("Primary Tag*"));
		flex.setWidget(4, 2, primaryTagList);
		flex.setWidget(5, 0, createLabel("Report Title*"));
		flex.setWidget(5, 2, titleTextBox);
		flex.setWidget(6, 0, createLabel("Abstract"));
		flex.setWidget(6, 2, abstractTextarea);
		flex.setWidget(7, 0, createLabel("Content"));
		flex.setWidget(7, 2, contentTextarea);
		flex.setWidget(8, 0, createLabel("URL"));
		flex.setWidget(8, 2, urlTextBox);
		flex.setWidget(9, 0, createLabel("Item Date*"));
		flex.setWidget(9, 2, dateTextBox);
		flex.setText(10, 2, dateText);
		flex.setWidget(11, 0, createLabel("Life Span*"));
		flex.setWidget(11, 2, dateBox);
		flex.setWidget(11, 3, lifeSpanDateTextBox);
		flex.setWidget(13, 0, createLabel("Source"));
		flex.setWidget(13, 2, sourceTextBox);
		flex.setWidget(14, 0, createLabel("Upload Report*"));
		flex.setWidget(14, 2, fileUpload);
		flex.setWidget(15,2, imagelbl);
	}

	public Label createLabel(String text){
		Label lb = new Label(text);
		lb.setStylePrimaryName("labelFlexEditNewsItems");
		return lb;
	}

	public Button createButton(String text){
		Button button = new Button(text);
		button.setStylePrimaryName("buttonOkAdmin");
		button.addClickHandler(this);
		return button;
	}
	 
	public void onModuleLoad(){
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}

	public void onChange(Widget sender){
	}

	public void onClick(Widget sender) {
	}

	/**
	 * This method used for validations of mandatory fields
	 */
	public boolean validate(){
		flag = true;
		boolean result = validateDate();
		if(flag == true){
			if((dateTextBox.getText().equals("") || titleTextBox.getText().equals("")||dateBox.getTextBox().getText().equals("")||
					primaryTagList.getSelectedIndex()<0)||fileUpload.getFilename().equals("")){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter the required field");
				popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
				popup.show();
				return false;
			}
			else if(!urlTextBox.getText().equals("")){
				if(!urlTextBox.getText().matches("^((https?|ftp)://|(www|ftp)\\b.)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter the Valid NewsReport Url");
					popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
					popup.show();
					return false;
				}
			}
		}
		if(result==false){
			return false;
		}
		return true;
	}

	public boolean validateDate(){
		String userDate =dateTextBox.getText();
		LHDateValidator dateValidator = new LHDateValidator();
		boolean result = dateValidator.validate(userDate);
		return result;
	}

	/**
	 * This method clears all the text fields 
	 */
	public void clearAllTheFields(){
		if(!userseletedtagsStr.equals(null))
			userseletedtagsStr = "";
		abstractTextarea.setText("");
		titleTextBox.setText("");
		sourceTextBox.setText("");
		dateTextBox.setText("");
		flex.remove(fileUpload);
		dateBox.getTextBox().setText("");
		contentTextarea.setText("");
		urlTextBox.setText("");
		FileUpload upload = new FileUpload();
		upload.setVisible(true);
		upload.setName("imageFileUpload");
		flex.setWidget(14, 2, upload);

	}

	/**
	 * This method get all the categories
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
					int i=0;
					while(iter.hasNext()){
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
	
	/**
	 * This method gets Tag name specific to the selected Category/Tag
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
					addTagNamesInListBox(list);
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryName()");
				}
			}
		};
		service.getTagNames(industryName,categoryName, callback);
	}
	
	public void sortTags(ArrayList list){
		final Comparator<TagItemInformation> NAME_ORDER = new Comparator<TagItemInformation>(){

			public int compare(TagItemInformation u1, TagItemInformation u2) {
				
				return u1.getTagName().compareTo(u2.getTagName());
			}
		};
		
		Collections.sort(list,NAME_ORDER);
	}
	
	/**
	 * This method adds all the Tag names in the TagListBox
	 */
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

	public void onMouseDown(Widget arg0, int arg1, int arg2) {

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
		PopUpForForgotPassword popup = new PopUpForForgotPassword("Your News Report Fields are been saved");
		popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
		popup.show();
	}

	public void onClick(ClickEvent event) {
		Widget sender = (Widget)event.getSource();
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
					userseletedtagsStr = userseletedtagsStr + primaryTagList.getValue(primaryTagList.getSelectedIndex())+";";
					userselectedTextbox.setText(userseletedtagsStr);
					String gwturlbase = GWT.getModuleBaseURL();
					String url = gwturlbase.substring(0, gwturlbase.length()-1);
					form.setAction(url +"/GetImageUploadService");
					form.submit();
				}
			}
			else if(button.getText().equals("Clear")){
				clearAllTheFields();
			}
			else if(button.getText().equals("Remove")){
				if(tagListBox.getItemCount() == 0){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to be removed");
					popup.setPopupPosition(dock.getAbsoluteLeft()+500, dock.getAbsoluteTop());
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
			}
			else if(label.getText().equals("Delete Newsreport")){
				DeleteNewsReport deleteNewsreport = new DeleteNewsReport(industryname,userindustryid);
				deleteNewsreport.onModuleLoad();
			}
			else if(label.getText().equals("Edit Newsreport")){
				EditNewsReport edit = new EditNewsReport(industryname,userindustryid);
				edit.onModuleLoad();
			}
			else if(label.getText().equals("Add Newsreport")){
				AddNewsReport addNewsReport = new AddNewsReport(industryname,userindustryid);
				addNewsReport.onModuleLoad();
			}
			else if(label.getText().equals("Back")){
				LHadmin admin = new LHadmin();
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
	}

	@Override
	public void onSubmit(SubmitEvent event) {
		
	}
	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		if(event.getResults().contains("Failed")){
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Please upload file less than 10 Mb");
			popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
			popup.show();
		}
		else{
			clearAllTheFields();
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Your News Report Fields are been saved");
			popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
			popup.show();
		}
	}

	@Override
	public void onMouseEnter(Widget sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMouseLeave(Widget sender) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onValueChange(ValueChangeEvent event) {
		Date date = (Date) event.getValue();
		java.sql.Date changedDate = new java.sql.Date(date.getTime());
		dateBox.getTextBox().setText(changedDate.toString());
		lifeSpanDateTextBox.setValue(changedDate.toString());
	}
	
}