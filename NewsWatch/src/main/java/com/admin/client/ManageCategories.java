package com.admin.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ManageCategories extends Composite implements ChangeHandler,ClickHandler{

	private VerticalPanel vPanel = new VerticalPanel();
	private VerticalPanel container;
	private ListBox choiceListBox = new ListBox();
	private ListBox categoryListBox = new ListBox();
	private HorizontalPanel hPanel = new HorizontalPanel();
	private HorizontalPanel horizontalPanel = new HorizontalPanel();
	private VerticalPanel centerPanel = new VerticalPanel();
	private DockPanel dock = new DockPanel();
	private boolean readMode = false;
	private DecoratorPanel addcategoryDecorator;
	protected Button deleteBtn;
	protected FlexTable flex;
	protected TextBox tbCatName = new TextBox();
	private Button saveBtn;
	private Button updateBtn;
	private Button resetBtn;
	protected ArrayList<TagItemInformation> allCatList = new ArrayList<TagItemInformation>();
	private int categoriesParentId;
	protected TableCollection tbcollection;
	private TableCollection currentTbCollection = new TableCollection();
	private ScrollPanel scroll = new ScrollPanel();
	protected HashMap selectionMap = new HashMap();
	static HashMap updatedCatMap = new HashMap();
	protected RootPanel rootPanel;
	protected String userSelectedIndustry="";
	protected String userSelectedIndustryName;
	int userSelectedIndustryId;

	/**
	 * Constructor
	 * @param userIndustryName - the logged in industryname
	 * @param userIndustryId - the logged in industry id
	 */
	public ManageCategories(String userIndustryName,int userIndustryId ){
		userSelectedIndustryName = userIndustryName;
		userSelectedIndustryId = userIndustryId;
		setUserSelectedIndustryId(userIndustryId);
		getIndustryNameFromSession();
		choiceListBox.addItem("---Select---");
		choiceListBox.addItem("Add Categories");
		choiceListBox.addItem("Edit Categories");
		choiceListBox.addItem("Delete Categories");
		//choiceListBox.addChangeListener(this);
		choiceListBox.addChangeHandler(this);
		choiceListBox.setName("choiceList");

		hPanel.add(createMessageLabel("Please select an action"));
		hPanel.add(choiceListBox);
		hPanel.setSpacing(5);
		vPanel.add(hPanel);

		centerPanel.setWidth("100%");
		dock.setSpacing(30);
	
		dock.add(vPanel, DockPanel.NORTH);
		dock.add(centerPanel,DockPanel.CENTER);
		initWidget(dock);
	}

	/**
	 * creates the UI to add a category
	 */
	public void addCategories(){
		centerPanel.clear();
		addcategoryDecorator = new DecoratorPanel();
		container = new VerticalPanel();
		container.add(createMessageLabel("Add the following category information"));
		container.add(addCategoriesTable());
		saveBtn = new Button("Save");
		container.add(saveBtn);
		//saveBtn.addClickListener(this);
		saveBtn.addClickHandler(this);
		container.setSpacing(5);
		addcategoryDecorator.add(container);
		centerPanel.add(addcategoryDecorator);
	}

	/**
	 * fetches the current industry details into which the user has logged in
	 */
	public void getIndustryNameFromSession()
	{
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
					userSelectedIndustry = userSelectedIndustryName;
					getParentId(userSelectedIndustry);
					getCatList(userSelectedIndustry);
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryNameFromSession()");
				}
			}
		};
		service.getIndustryNameFromSession(callback);
	}

	/**
	 * creates the UI to edit categories
	 */
	public void editCategories(){
		setReadMode(true);
		centerPanel.clear();
		addcategoryDecorator = new DecoratorPanel();
		container = new VerticalPanel();
		horizontalPanel.clear();
		scroll.setStylePrimaryName("scrollerforDecorator");
		updateBtn = new Button("Update");
		//updateBtn.addClickListener(this);
		updateBtn.addClickHandler(this);
		resetBtn = new Button("Reset");
		getCategories(getCategoriesParentId());

		container.setSpacing(5);
		container.add(scroll);
		addcategoryDecorator.add(container);
		centerPanel.add(addcategoryDecorator);

		HorizontalPanel btnHPanel = new HorizontalPanel();
		btnHPanel.add(updateBtn);
		btnHPanel.setSpacing(20);
		centerPanel.add(btnHPanel);
		//resetBtn.addClickListener(this);
		resetBtn.addClickHandler(this);
	}

	/**
	 * creates the UI to delete categories
	 */
	public void deleteCategories(){
		setReadMode(false);
		centerPanel.clear();
		addcategoryDecorator = new DecoratorPanel();
		container = new VerticalPanel();
		horizontalPanel.clear();
		deleteBtn = new Button("Delete");
		//deleteBtn.addClickListener(this);
		deleteBtn.addClickHandler(this);
		scroll.setStylePrimaryName("scrollerforDecorator");
		getCategories(getCategoriesParentId());
		container.setSpacing(5);
		container.add(scroll);
		addcategoryDecorator.add(container);
		centerPanel.add(addcategoryDecorator);
		centerPanel.add(deleteBtn);
	}

	public void getCategories(int index){
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
					selectionMap = getUpdatedCatMap();
					System.out.println(selectionMap);
					HashMap<Integer, TagItemInformation> map = (HashMap<Integer, TagItemInformation>)arg0;
					HashMap<Integer,TableCollection> TbCollectionMap = new HashMap<Integer, TableCollection>();
					TbCollectionMap.put(id, tbcollection);
					setCurrentTbCollection(tbcollection);
					if(isReadMode())
						tbcollection.addCategoriesinTable(map,true);
					else
						tbcollection.addCategoriesinTable(map, false);
					scroll.add(tbcollection);
				}
				catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getCategoryTags()");
				}
			}	
		};
		service.getCategoryTagsInfo(userSelectedIndustryName,userSelectedIndustryId,index,true, callback);
	}

	/**
	 * creates a flex table to add a category
	 * @return FlexTable
	 */
	public FlexTable addCategoriesTable(){
	   flex = new FlexTable();
	   flex.setCellPadding(2);
	   flex.setWidget(1,0,createLabel("Industry"));
	   flex.setText(1, 1, userSelectedIndustry);
	   flex.setWidget(2,0,createLabel("Category"));
	   flex.setWidget(2,1,tbCatName);
	   return flex;
	}

	/**
	 * gets the base parent tagitem id for the industry 
	 * @param industryName
	 */
	public void getParentId(String industryName){
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
					int parentid = (Integer)result;
				    setCategoriesParentId(parentid);		
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in getCatList()");
				}
			}
		};
		service.getParentId(getUserSelectedIndustryId(),industryName, callback);

	}
	
	/**
	 * gets the categories list for an industry
	 * @param industryName
	 */
	public void getCatList(String industryName){
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
					while(iter.hasNext())
					{
						TagItemInformation tag = (TagItemInformation)iter.next();
							setCategoriesParentId(tag.getParentId());
							allCatList.add(tag);
					}
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in getCatList()");
				}
			}
		};
		service.getCategoryNames(getUserSelectedIndustryId(),industryName, callback);
	}

	/**
	 * creates a Label widget to display messages
	 * @param msgText the text to be displayed on the label
	 * @return Label
	 */
	public Label createMessageLabel(String msgText){
		Label label = new Label(msgText);
		label.setStylePrimaryName("messageLabels");
		return label;
	}

	/**
	 * creates a Label widget
	 * @param labelTxt the text to be displayed on the label
	 * @return Label
	 */
	public Label createLabel(String labelTxt){
		Label  label = new Label(labelTxt);
		label.setStylePrimaryName("labelTitle");
		return label;
	}

	public void onModuleLoad() {
		rootPanel = RootPanel.get();
		rootPanel.clear();
		rootPanel.add(this);
	}

	public void onChange(Widget arg0) {
		/*ListBox list = (ListBox)arg0;
		String choice = list.getItemText(list.getSelectedIndex());
		if(list.getName().equals("choiceList")){
			 if(choice.equals("Add Categories")){
				 addCategories();
			 }
			 else if(choice.equals("Edit Categories")){
				 editCategories();
			 }
			 else if(choice.equals("Delete Categories")){
				 deleteCategories();
			 }
		 }*/
	}

	public void onClick(Widget arg0) {
		/*if(arg0 instanceof Button){
			Button btn = (Button)arg0;
			if(btn.getText().equals("Save")){
				if(tbCatName.getText().equals("")){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter the Category Name");
					popup.setPopupPosition(500, 200);
					popup.show();
				}
				else{
					//boolean valid = tagValidation(tbCatName.getText());
					boolean valid = true;
					if(valid){
						boolean bool = false;
						int industryid = AdminPage.getIndustryId();
						int parentid = getCategoriesParentId();
						String categoryText = tbCatName.getText();

						Iterator iter = allCatList.iterator();
						System.out.println("Categories list: "+allCatList.size());
						while(iter.hasNext()){
							TagItemInformation tag = (TagItemInformation)iter.next();
							if(categoryText.trim().equals(tag.getTagName()) && parentid == tag.getParentId() && industryid == tag.getIndustryId() ){
								bool = true;
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Entry for category exists!");
								popup.setPopupPosition(500, 200);
								popup.show();
							}
						}
						if(!bool){
							TagItemInformation tagitem = new TagItemInformation();
							tagitem.setTagName(categoryText);
							tagitem.setParentId(parentid);
							tagitem.setIndustryId(industryid);
							tbCatName.setText("");
							allCatList.add(tagitem);
							saveCategory(tagitem, userSelectedIndustryName, true);
						}
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("'"+tbCatName.getText()+"' is an Invalid category Name");
						popup.setPopupPosition(500, 200);
						popup.show();
                    }
				}
			}
			else if(btn.getText().equals("Delete")){
				selectionMap = getCurrentTbCollection().getItemMap();

				if(selectionMap.isEmpty()){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no Categories to delete");
					popup.setPopupPosition(600, 300);
					popup.show();
				}else{
					deleteSelectedCategories(selectionMap);
					ArrayList tempList = new ArrayList();
					for(Object obj : selectionMap.keySet()){
						int id = (Integer)obj;
						Iterator iter = allCatList.iterator();
						while(iter.hasNext()){
							TagItemInformation taginfo = (TagItemInformation)iter.next();
							if(id == taginfo.getTagItemId()){
								tempList.add(taginfo);
							}
						}
						Iterator itt = tempList.iterator();
						while(itt.hasNext()){
							allCatList.remove((TagItemInformation)itt.next());
						}
					}
				}
				System.out.println("Category list: " +allCatList.size());
			}
			else if(btn.getText().equals("Update")){
				selectionMap = getCurrentTbCollection().getUpdatedTextBoxMap();
				if(selectionMap.isEmpty() && TableCollection.f == false){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no updated Categories");
					popup.setPopupPosition(600, 300);
					popup.show();
				}
				else{
					if(TableCollection.flag == false){
						setUpdatedCatMap(selectionMap);
						updateSelectedCategories(selectionMap);
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Cannot update. Please make sure no field is left blank");
						popup.setPopupPosition(500, 200);
						popup.show();
					}
				}
			}
			else if(btn.getText().equals("Reset")){
				int id = getCategoriesParentId();
				getCategories(id);
			}
		}*/
	}
	
	/**
	 * saves the edited categories
	 * @param tagMap a HashMap of the edited categories (TagItem)
	 */
	public void updateSelectedCategories(HashMap tagMap){
		final HashMap selectedTagMap = tagMap;
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				PopUpForForgotPassword popup = new PopUpForForgotPassword("There is some problem in updating the categories.");
				popup.setPopupPosition(600, 300);
				popup.show();
			}

			public void onSuccess(Object result) {
				try{
					tbcollection.disableTextBox(selectedTagMap);
					PopUpForForgotPassword popup = new PopUpForForgotPassword("The categories were updated");
					popup.setPopupPosition(600, 300);
					popup.show();
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in updateSelectedategories()");
				}
			}
		};
		service.updateTags(selectedTagMap, callback);
	}

	/**
	 * delets the selected categories
	 * @param tagMap a HashMap of the selected categories(TagItem) to be deleted
	 */ 
	public void deleteSelectedCategories(HashMap tagMap) {
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
					deleteBtn.setEnabled(false);
					PopUpForForgotPassword popup = new PopUpForForgotPassword("The selected categories were deleted");
					popup.setPopupPosition(600, 300);
					popup.show();
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in deleteSelectedCategories()");
				}
			}
		};
		service.deleteTags(selectedTagMap,true,callback);
	}

	/**
	 * save a newly created category(TagItem)
	 * @param tagitem - the created category
	 * @param parentName - the parent (industry name) of the category
	 * @param isCategory - true as a category is being saved.
	 */
	public void saveCategory(TagItemInformation tagitem, String parentName, boolean isCategory){
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
					PopUpForForgotPassword popup = new PopUpForForgotPassword("The category has been saved");
					popup.setPopupPosition(500, 200);
					popup.show();
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in saveCategory()");
				}
			}
		};
		service.saveNewTag(tagitem,parentName,isCategory,callback);
	}

	/**
	 * validates tag format
	 * @param tag - the tag name
	 * @return true if valid format else false
	 */
	public boolean tagValidation(String tag){
		if(tag.matches("[a-zA-Z][a-zA-Z0-9-&' ']*")){
			return true;
		}
		return false;
	}

	public boolean isReadMode() {
		return readMode;
	}

	public void setReadMode(boolean readMode) {
		this.readMode = readMode;
	}

	public int getCategoriesParentId() {
		return categoriesParentId;
	}

	public void setCategoriesParentId(int categoriesParentId) {
		this.categoriesParentId = categoriesParentId;
	}

	public TableCollection getCurrentTbCollection() {
		return currentTbCollection;
	}

	public void setCurrentTbCollection(TableCollection currentTbCollection) {
		this.currentTbCollection = currentTbCollection;
	}

	public  ArrayList getAllCatList() {
		return allCatList;
	}

	public  void setAllCatList(ArrayList allCatList) {
		this.allCatList = allCatList;
	}

	public static HashMap getUpdatedCatMap() {
		return updatedCatMap;
	}

	public static void setUpdatedCatMap(HashMap updatedCatMap) {
		ManageCategories.updatedCatMap = updatedCatMap;
	}

	public int getUserSelectedIndustryId() {
		return userSelectedIndustryId;
	}

	public void setUserSelectedIndustryId(int userSelectedIndustryId) {
		this.userSelectedIndustryId = userSelectedIndustryId;
	}

	/**
	 * Overridden method for the ClickHandler
	 */
	@Override
	public void onClick(ClickEvent event) {
		
		Widget arg0 = (Widget)event.getSource();
		
		if(arg0 instanceof Button){
			Button btn = (Button)arg0;
			if(btn.getText().equals("Save")){
				if(tbCatName.getText().equals("")){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter the Category Name");
					popup.setPopupPosition(500, 200);
					popup.show();
				}
				else{
					//boolean valid = tagValidation(tbCatName.getText());
					boolean valid = true;
					if(valid){
						boolean bool = false;
						int industryid = AdminPage.getIndustryId();
						int parentid = getCategoriesParentId();
						String categoryText = tbCatName.getText();

						Iterator iter = allCatList.iterator();
						System.out.println("Categories list: "+allCatList.size());
						while(iter.hasNext()){
							TagItemInformation tag = (TagItemInformation)iter.next();
							if(categoryText.trim().equals(tag.getTagName()) && parentid == tag.getParentId() && industryid == tag.getIndustryId() ){
								bool = true;
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Entry for category exists!");
								popup.setPopupPosition(500, 200);
								popup.show();
							}
						}
						if(!bool){
							TagItemInformation tagitem = new TagItemInformation();
							tagitem.setTagName(categoryText);
							tagitem.setParentId(parentid);
							tagitem.setIndustryId(industryid);
							tbCatName.setText("");
							allCatList.add(tagitem);
							saveCategory(tagitem, userSelectedIndustryName, true);
						}
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("'"+tbCatName.getText()+"' is an Invalid category Name");
						popup.setPopupPosition(500, 200);
						popup.show();
                    }
				}
			}
			else if(btn.getText().equals("Delete")){
				selectionMap = getCurrentTbCollection().getItemMap();

				if(selectionMap.isEmpty()){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no Categories to delete");
					popup.setPopupPosition(600, 300);
					popup.show();
				}else{
					deleteSelectedCategories(selectionMap);
					ArrayList tempList = new ArrayList();
					for(Object obj : selectionMap.keySet()){
						int id = (Integer)obj;
						Iterator iter = allCatList.iterator();
						while(iter.hasNext()){
							TagItemInformation taginfo = (TagItemInformation)iter.next();
							if(id == taginfo.getTagItemId()){
								tempList.add(taginfo);
							}
						}
						Iterator itt = tempList.iterator();
						while(itt.hasNext()){
							allCatList.remove((TagItemInformation)itt.next());
						}
					}
				}
				System.out.println("Category list: " +allCatList.size());
			}
			else if(btn.getText().equals("Update")){
				selectionMap = getCurrentTbCollection().getUpdatedTextBoxMap();
				if(selectionMap.isEmpty() && TableCollection.f == false){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no updated Categories");
					popup.setPopupPosition(600, 300);
					popup.show();
				}
				else{
					if(TableCollection.flag == false){
						setUpdatedCatMap(selectionMap);
						updateSelectedCategories(selectionMap);
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Cannot update. Please make sure no field is left blank");
						popup.setPopupPosition(500, 200);
						popup.show();
					}
				}
			}
			else if(btn.getText().equals("Reset")){
				int id = getCategoriesParentId();
				getCategories(id);
			}
		}
		
	}

	/**
	 * Overridden method for the ChangeHandler
	 */
	@Override
	public void onChange(ChangeEvent event) {
		Widget arg0 = (Widget)event.getSource();
		
		ListBox list = (ListBox)arg0;
		String choice = list.getItemText(list.getSelectedIndex());
		if(list.getName().equals("choiceList")){
			 if(choice.equals("Add Categories")){
				 addCategories();
			 }
			 else if(choice.equals("Edit Categories")){
				 editCategories();
			 }
			 else if(choice.equals("Delete Categories")){
				 deleteCategories();
			 }
		 }
		
	}
}