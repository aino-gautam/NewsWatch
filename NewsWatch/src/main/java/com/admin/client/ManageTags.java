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

import com.common.client.LogoutPage;
import com.common.client.ManageHeader;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ManageTags extends Composite implements FormHandler,ChangeHandler,ClickHandler{

	protected VerticalPanel container;
	private HorizontalPanel hPanel = new HorizontalPanel();
	protected FileUpload fileUpload = new FileUpload();
	protected Button uploadBtn;
	protected Button saveBtn;
	protected Button deleteBtn;
	protected Button updateBtn;
	protected Button resetBtn;
	private DockPanel dock = new DockPanel();
	protected DecoratorPanel addTagsDecorator;
	protected HorizontalPanel horizontalPanel = new HorizontalPanel();
	protected TableCollection tbcollection;
	private TableCollection currentTbCollection = new TableCollection();
	private HashMap<Integer,TableCollection> currentTbCollectionMap = new HashMap<Integer, TableCollection>();
	private ListBox choiceListBox = new ListBox();
	protected ListBox categoryListBox = new ListBox();
	protected VerticalPanel centerPanel = new VerticalPanel();
	protected TextBox tbTagName = new TextBox();
	protected FlexTable flex;
	protected ScrollPanel scroll = new ScrollPanel();
	protected HashMap selectionMap = new HashMap();
	private boolean readMode = false;
	private VerticalPanel vpanel = new VerticalPanel();
	protected static Collection allTagsList;
	private RootPanel panel;
	protected String userSelectedIndustryName;
	protected int userSelectedIndustryId;
	public HashMap<Integer, TagItemInformation> map;
	protected ArrayList<TagItemInformation> allTagsArray = new ArrayList<TagItemInformation>();
	protected FormPanel form = new FormPanel();
	protected ArrayList categoryList = new ArrayList();
	
	
	public ManageTags(String userIndustryName, int userIndustryId){
		userSelectedIndustryName = userIndustryName;
		userSelectedIndustryId = userIndustryId;
		setUserSelectedIndustryId(userIndustryId);
		fileUpload.setName("fileUpload");
		vpanel.setStylePrimaryName("vpanelManageTags");
		choiceListBox.addItem("---Select---");
		choiceListBox.addItem("Add Tags");
		choiceListBox.addItem("Edit Tags");
		choiceListBox.addItem("Delete Tags");
		//choiceListBox.addChangeListener(this);
		choiceListBox.addChangeHandler(this);
		choiceListBox.setName("choiceList");

		hPanel.add(createMessageLabel("Please select an action"));
		hPanel.add(choiceListBox);
		hPanel.setSpacing(5);

		centerPanel.setWidth("100%");
		dock.setSpacing(30);
		vpanel.add(hPanel);
		
		form.addFormHandler(this);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		
		dock.add(vpanel, DockPanel.NORTH);
		dock.add(centerPanel,DockPanel.CENTER);
		initWidget(dock);
	}

	public void addTagItems(){
		getCategoryTags(0);
		centerPanel.clear();
		addTagsDecorator = new DecoratorPanel();
		container = new VerticalPanel();
		container.add(createMessageLabel("Please select a file containing tag information to upload"));
		container.add(fileUpload);
		uploadBtn = new Button("Upload");
		container.add(uploadBtn);
		//uploadBtn.addClickListener(this);
		uploadBtn.addClickHandler(this);
		container.add(createLabel("OR"));
		container.add(createMessageLabel("Add the following tag information"));
		container.add(addtagsTable());
		saveBtn = new Button("Save");
		container.add(saveBtn);
		//saveBtn.addClickListener(this);
		saveBtn.addClickHandler(this);
		container.setSpacing(5);
		form.setWidget(container);
		addTagsDecorator.add(form);
		centerPanel.add(addTagsDecorator);
	}

	public void editTagItems(){
		centerPanel.clear();
		addTagsDecorator = new DecoratorPanel();
		container = new VerticalPanel();
		horizontalPanel.clear();
		categoryListBox.clear();
		setReadMode(true);
		scroll.setStylePrimaryName("scrollerforDecorator");
		updateBtn = new Button("Update");
		resetBtn = new Button("Reset");
		//updateBtn.addClickListener(this);
		updateBtn.addClickHandler(this);
		//resetBtn.addClickListener(this);
		resetBtn.addClickHandler(this);
		horizontalPanel.setSpacing(5);
		horizontalPanel.add(createLabel("Category"));
		
		//categoryListBox.addChangeListener(this);
		categoryListBox.addChangeHandler(this);
		if(categoryListBox.getItemCount()<2){
			categoryListBox.addItem("All Tags");
			categoryListBox.setSelectedIndex(0);
			fillCatList(ManageHeader.getUserIndustryName());
		}
		else{
			categoryListBox.insertItem("All Tags", 0);
		    categoryListBox.setSelectedIndex(0);
		}

		horizontalPanel.add(categoryListBox);
		getCategoryTags(categoryListBox.getSelectedIndex());
		categoryListBox.setEnabled(true);
		container.add(horizontalPanel);
		container.setSpacing(5);
		container.add(scroll);
		addTagsDecorator.add(container);
		centerPanel.add(addTagsDecorator);
		HorizontalPanel btnHPanel = new HorizontalPanel();
		btnHPanel.add(updateBtn);
//		btnHPanel.add(resetBtn);
		btnHPanel.setSpacing(20);
		centerPanel.add(btnHPanel);
		
	}

	

	public void deleteTagItems(){
		setReadMode(false);
		centerPanel.clear();
		addTagsDecorator = new DecoratorPanel();
		container = new VerticalPanel();
		categoryListBox.clear();
		horizontalPanel.clear();
		
		scroll.setStylePrimaryName("scrollerforDecorator");
		deleteBtn = new Button("Delete");
		//deleteBtn.addClickListener(this);
		deleteBtn.addClickHandler(this);

		horizontalPanel.setSpacing(5);
		horizontalPanel.add(createLabel("Category"));
		//categoryListBox.addChangeListener(this);
		categoryListBox.addChangeHandler(this);
		if(categoryListBox.getItemCount()<2){
			categoryListBox.addItem("All Tags");
			categoryListBox.setSelectedIndex(0);
			fillCatList(userSelectedIndustryName);
		}
		else{
			categoryListBox.insertItem("All Tags", 0);
		    categoryListBox.setSelectedIndex(0);
		}

		horizontalPanel.add(categoryListBox);
		getCategoryTags(categoryListBox.getSelectedIndex());
		categoryListBox.setEnabled(true);
		container.add(horizontalPanel);
		container.setSpacing(5);
		container.add(scroll);
		
		addTagsDecorator.add(container);
		centerPanel.add(addTagsDecorator);
		centerPanel.add(deleteBtn);
	}

	public FlexTable addtagsTable(){
	   flex = new FlexTable();
	   flex.setCellPadding(2);

	  // categoryListBox.addChangeListener(this);
	   categoryListBox.addChangeHandler(this);
	   categoryListBox.clear();
	   categoryListBox.addItem("--Please Select--");
	   fillCatList(userSelectedIndustryName);

	   flex.setWidget(1,0,createLabel("Industry"));
	   flex.setText(1, 1, ManageHeader.getUserIndustryName());
	   flex.setWidget(2,0,createLabel("Category"));
	   flex.setWidget(2,1,categoryListBox);
	   flex.setWidget(3,0,createLabel("Name"));
	   flex.setWidget(3,1,tbTagName);
	   return flex;
	}

	public void getCategoryTags(int index){
		final int id = index;
		tbcollection = new TableCollection(this);
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			public void onSuccess(Object arg0) {
				try{
					scroll.clear();
					map = (HashMap<Integer, TagItemInformation>)arg0;
					if(id==0){
						allTagsList = map.values();
						Iterator iter = allTagsList.iterator();
						while(iter.hasNext()){
							TagItemInformation tag = (TagItemInformation)iter.next();
							allTagsArray.add(tag);
						}
					}
					map = sortMap(map);
					HashMap<Integer,TableCollection> TbCollectionMap = new HashMap<Integer, TableCollection>();
					TbCollectionMap.put(id, tbcollection);
					setCurrentTbCollectionMap(TbCollectionMap);
					setCurrentTbCollection(tbcollection);
					if(isReadMode())
						tbcollection.addTagsinTable(map,true);
					else
						tbcollection.addTagsinTable(map, false);
					scroll.add(tbcollection);
				}
				catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getCategoryTags()");
				}
			}	
		};
		service.getCategoryTagsInfo(userSelectedIndustryName,getUserSelectedIndustryId(),index,false, callback);
	}

	public void sortList(ArrayList list){
		final Comparator<TagItemInformation> ALPHABETICAL_ORDER = new Comparator<TagItemInformation>(){

			public int compare(TagItemInformation t1, TagItemInformation t2) {
				
				return t1.getTagName().compareTo(t2.getTagName());
			}
		};
		
		Collections.sort(list,ALPHABETICAL_ORDER);
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
	
	public void fillCatList(String industryName){
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
					Iterator iter = list.iterator();
					sortList(list);
					while(iter.hasNext()){
						TagItemInformation tag = (TagItemInformation)iter.next();
						String name = tag.getTagName();
						categoryListBox.addItem(name);
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

	public Button createButton(String buttonText){
		Button button = new Button(buttonText);
		//button.addClickListener(this);
		button.addClickHandler(this);
		return button;
	}

	public Label createLabel(String labelTxt){
		Label  label = new Label(labelTxt);
		label.setStylePrimaryName("labelTitle");
		return label;
	}

	public Label createMessageLabel(String msgText){
		Label label = new Label(msgText);
		label.setStylePrimaryName("messageLabels");
		return label;
	}

	public void onModuleLoad(){
		 panel = RootPanel.get();
		 panel.clear();
		 panel.add(this);
	}

	public void saveTag(TagItemInformation tagitem, String parentName,boolean isCategory){
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object arg0) {
				try{
					if(arg0.equals(true)){
						PopUpForForgotPassword popup = new PopUpForForgotPassword("The tag has been saved");
						popup.setPopupPosition(500, 200);
						popup.show();
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Entry for tag exists!");
						popup.setPopupPosition(500, 200);
						popup.show();
					}
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in saveNewTag()");
				}
			}
		};
		service.saveNewTag(tagitem,parentName,isCategory,callback);
	}

	public void deleteSelectedTags(HashMap tagMap) {
		final HashMap selectedTagMap = tagMap;
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				PopUpForForgotPassword popup = new PopUpForForgotPassword("There is some problem in deletion.");
				popup.setPopupPosition(600, 300);
				popup.show();
			}

			public void onSuccess(Object result) {
				try{
					tbcollection.disableCheckBox(selectedTagMap);
					selectionMap.clear();
					PopUpForForgotPassword popup = new PopUpForForgotPassword("The selected tags were deleted");
					popup.setPopupPosition(600, 300);
					popup.show();
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in deleteSelectedTags()");
				}
			}
		};
		service.deleteTags(selectedTagMap, false, callback);
	}

	public void updateSelectedTags(HashMap tagMap){
		final HashMap selectedTagMap = tagMap;
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				PopUpForForgotPassword popup = new PopUpForForgotPassword("There is some problem in updating the tags.");
				popup.setPopupPosition(600, 300);
				popup.show();
			}

			public void onSuccess(Object result) {
				if(TableCollection.flag == false){
					try{
						tbcollection.disableTextBox(selectedTagMap);
						PopUpForForgotPassword popup = new PopUpForForgotPassword("The tags were updated");
						popup.setPopupPosition(600, 300);
						popup.show();
					}catch(Exception ex){
						ex.printStackTrace();
						System.out.println("problem in updateSelectedTags()");
					}
				}
			}
		};
		service.updateTags(selectedTagMap, callback);
	}

	public void onClick(Widget arg0) {
		/*if(arg0 instanceof Button){
			Button btn = (Button)arg0;
			if(btn.getText().equals("Upload")){
		        	String gwturlbase = GWT.getModuleBaseURL();
					String url = gwturlbase.substring(0, gwturlbase.length()-1);
					form.setAction(url +"/servlet/GetUploadedFileTags");
					form.submit();
			}
			else if(btn.getText().equals("Save")){
				if(categoryListBox.getSelectedIndex() == 0 || tbTagName.getText().equals("")){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter all details");
					popup.setPopupPosition(600, 350);
					popup.show();
				}
				else{
					//boolean valid = tagValidation(tbTagName.getText());
					boolean valid = true;
					if(valid){
						boolean bool = false;
						String parentName = categoryListBox.getItemText(categoryListBox.getSelectedIndex());
						int industryid = AdminPage.getIndustryId();
						String tagText = tbTagName.getText();

						Iterator iter = allTagsArray.iterator();
						TagItemInformation tagitem = new TagItemInformation();
						tagitem.setTagName(tagText);
						tagitem.setIndustryId(industryid);
						tbTagName.selectAll();
						saveTag(tagitem, parentName,false);
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("'"+tbTagName.getText()+"' is an Invalid Tag Name");
						popup.setPopupPosition(500, 200);
						popup.show();
					}
				}
			}
			else if(btn.getText().equals("Delete")){
				selectionMap = getCurrentTbCollection().getItemMap();
				if(selectionMap.isEmpty() && TableCollection.flag == false){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to delete");
					popup.setPopupPosition(500, 200);
					popup.show();
				}
				if(selectionMap.size()!=0){
					deleteSelectedTags(selectionMap);
					ArrayList tempList = new ArrayList();
					for(Object obj:selectionMap.keySet()){
						int id =  (Integer)obj;
						Iterator iter = allTagsArray.iterator();
						while(iter.hasNext()){
							TagItemInformation taginfo = (TagItemInformation)iter.next();
							if(id == taginfo.getTagItemId()){
								tempList.add(taginfo);
							}
						}
					}
					Iterator itt = tempList.iterator();
					while(itt.hasNext()){
						allTagsArray.remove((TagItemInformation)itt.next());
					}
				}
			}
			else if(btn.getText().equals("Update")){
				selectionMap = getCurrentTbCollection().getUpdatedTextBoxMap();
				if(selectionMap.isEmpty() && TableCollection.f == false){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to update");
					popup.setPopupPosition(500, 200);
					popup.show();
				}
				else{
					if(TableCollection.flag == false)
						updateSelectedTags(selectionMap);
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Cannot update. Please make sure no field is left blank");
						popup.setPopupPosition(500, 200);
						popup.show();
					}
				}
			}
			else if(btn.getText().equals("Reset")){
				int id = categoryListBox.getSelectedIndex();
				getCategoryTags(id);
			}
		}
		else if(arg0 instanceof Label){
			Label label = (Label)arg0;
			if(label.getText().equals("logout")){
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);
			}
		}*/
	}

	public void getCategoryId(String name){
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
			public void onSuccess(Object arg0) {
				try{
					int id = (Integer)arg0;
					getCategoryTags(id);
				}
				catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getCategoryTags()");
				}
			}	
		};
		service.getCategoryId(name,callback);
	}
	
	public boolean tagValidation(String tag){
		if(tag.matches("[a-zA-Z][a-zA-Z0-9-&' ']*")){
			return true;
		}
		return false;
	}
	
	public TableCollection getCurrentTbCollection() {
		return currentTbCollection;
	}

	public void setCurrentTbCollection(TableCollection currentTbCollection) {
		this.currentTbCollection = currentTbCollection;
	}

	public boolean isReadMode() {
		return readMode;
	}

	public void setReadMode(boolean readMode) {
		this.readMode = readMode;
	}

	public HashMap<Integer, TableCollection> getCurrentTbCollectionMap() {
		return currentTbCollectionMap;
	}

	public void setCurrentTbCollectionMap(
			HashMap<Integer, TableCollection> currentTbCollectionMap) {
		this.currentTbCollectionMap = currentTbCollectionMap;
	}

	public static Collection getAllTagsList() {
		return allTagsList;
	}

	public static void setAllTagsList(Collection allTagsList) {
		ManageTags.allTagsList = allTagsList;
	}

	public int getUserSelectedIndustryId() {
		return userSelectedIndustryId;
	}

	public void setUserSelectedIndustryId(int userSelectedIndustryId) {
		this.userSelectedIndustryId = userSelectedIndustryId;
	}
	
	public ArrayList<TagItemInformation> getAllTagsArray() {
		return allTagsArray;
	}

	public void setAllTagsArray(ArrayList<TagItemInformation> allTagsArray) {
		this.allTagsArray = allTagsArray;
	}
	
	public void onSubmit(FormSubmitEvent arg0) {
		String filename = fileUpload.getFilename().replace("\\", "/");
		 if (filename.length() == 0) {
	        	PopUpForForgotPassword popup = new PopUpForForgotPassword("No File Selected");
	        	popup.setPopupPosition(500, 200);
	        	popup.show();
	        	arg0.setCancelled(true);
	      }
		  else if(!(filename.contains(".xls"))) {
	        	PopUpForForgotPassword popup = new PopUpForForgotPassword("Only Excel/CSV Files can be uploaded");
	        	popup.setPopupPosition(600, 350);
	        	popup.show();
	        	arg0.setCancelled(true);
	    }
	    else if(filename.contains(".xlsx")){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please save the file as Excel 97-2003 compatible");
	        	popup.setPopupPosition(600, 350);
	        	popup.show();
	        	arg0.setCancelled(true);
		 }
		
	}

	public void onSubmitComplete(FormSubmitCompleteEvent arg0){
		PopUpForForgotPassword popup = new PopUpForForgotPassword("File is successfully uploaded");
		popup.setPopupPosition(500, 200);
		popup.show();
	}

	public ArrayList getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(ArrayList categoryList) {
		this.categoryList = categoryList;
	}

	@Override
	public void onClick(ClickEvent event) {
			
		Widget arg0 = (Widget)event.getSource();
		if(arg0 instanceof Button){
			Button btn = (Button)arg0;
			if(btn.getText().equals("Upload")){
		        	String gwturlbase = GWT.getModuleBaseURL();
					String url = gwturlbase.substring(0, gwturlbase.length()-1);
					form.setAction(url +"/servlet/GetUploadedFileTags");
					form.submit();
			}
			else if(btn.getText().equals("Save")){
				if(categoryListBox.getSelectedIndex() == 0 || tbTagName.getText().equals("")){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter all details");
					popup.setPopupPosition(600, 350);
					popup.show();
				}
				else{
					//boolean valid = tagValidation(tbTagName.getText());
					boolean valid = true;
					if(valid){
						boolean bool = false;
						String parentName = categoryListBox.getItemText(categoryListBox.getSelectedIndex());
						int industryid = AdminPage.getIndustryId();
						String tagText = tbTagName.getText();

						Iterator iter = allTagsArray.iterator();
						TagItemInformation tagitem = new TagItemInformation();
						tagitem.setTagName(tagText);
						tagitem.setIndustryId(industryid);
						tbTagName.selectAll();
						saveTag(tagitem, parentName,false);
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("'"+tbTagName.getText()+"' is an Invalid Tag Name");
						popup.setPopupPosition(500, 200);
						popup.show();
					}
				}
			}
			else if(btn.getText().equals("Delete")){
				selectionMap = getCurrentTbCollection().getItemMap();
				if(selectionMap.isEmpty() && TableCollection.flag == false){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to delete");
					popup.setPopupPosition(500, 200);
					popup.show();
				}
				if(selectionMap.size()!=0){
					deleteSelectedTags(selectionMap);
					ArrayList tempList = new ArrayList();
					for(Object obj:selectionMap.keySet()){
						int id =  (Integer)obj;
						Iterator iter = allTagsArray.iterator();
						while(iter.hasNext()){
							TagItemInformation taginfo = (TagItemInformation)iter.next();
							if(id == taginfo.getTagItemId()){
								tempList.add(taginfo);
							}
						}
					}
					Iterator itt = tempList.iterator();
					while(itt.hasNext()){
						allTagsArray.remove((TagItemInformation)itt.next());
					}
				}
			}
			else if(btn.getText().equals("Update")){
				selectionMap = getCurrentTbCollection().getUpdatedTextBoxMap();
				if(selectionMap.isEmpty() && TableCollection.f == false){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to update");
					popup.setPopupPosition(500, 200);
					popup.show();
				}
				else{
					if(TableCollection.flag == false)
						updateSelectedTags(selectionMap);
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Cannot update. Please make sure no field is left blank");
						popup.setPopupPosition(500, 200);
						popup.show();
					}
				}
			}
			else if(btn.getText().equals("Reset")){
				int id = categoryListBox.getSelectedIndex();
				getCategoryTags(id);
			}
		}
		else if(arg0 instanceof Label){
			Label label = (Label)arg0;
			if(label.getText().equals("logout")){
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);
			}
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		
		Widget arg0 = (Widget)event.getSource();
		ListBox list = (ListBox)arg0;
		 String choice = list.getItemText(list.getSelectedIndex());
		 if(list.getName().equals("choiceList")){
			 if(choice.equals("Add Tags")){
				 addTagItems();
			 }
			 else if(choice.equals("Edit Tags")){
				 editTagItems();
			 }
			 else if(choice.equals("Delete Tags")){
				 deleteTagItems();
			 }
		 }
		 else if(list.getName().equals("industryList")){
			 if(!choice.equals("")){
				 categoryListBox.setEnabled(true);
				 if(categoryListBox.getItemCount()<2)
					 fillCatList(choice);
			 }
		 }
		 else if(list.getName().equals("categoryList")){
			 if(!choice.equals("")){
				 getCategoryId(categoryListBox.getItemText(categoryListBox.getSelectedIndex()));
			 }
		 }
		
	}
}