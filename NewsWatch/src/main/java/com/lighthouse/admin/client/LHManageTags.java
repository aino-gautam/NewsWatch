package com.lighthouse.admin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.admin.client.ManageTags;
import com.admin.client.TableCollection;
import com.admin.client.TagItemInformation;
import com.common.client.LogoutPage;
import com.common.client.ManageHeader;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.admin.client.service.LHAdminInformationService;
import com.lighthouse.admin.client.service.LHAdminInformationServiceAsync;

public class LHManageTags extends ManageTags implements FormHandler,ChangeHandler,ClickHandler{

	
	boolean value;
	private Label lblprimarycategory=new Label("* Primary category");
	LHTableCollection tblCollection=new LHTableCollection();
	private HashMap<String,TagItemInformation> hmtagItems=new HashMap<String, TagItemInformation>();
	public LHManageTags(String userIndustryName, int userIndustryId) {
		super(userIndustryName, userIndustryId);
	}
	
	@Override
	public void editTagItems(){
		centerPanel.clear();
		addTagsDecorator = new DecoratorPanel();
		container = new VerticalPanel();
		horizontalPanel.clear();
		categoryListBox.clear();
		categoryListBox.setName("categoryList");
		setReadMode(true);
		scroll.setStylePrimaryName("scrollerforDecorator");
		updateBtn = new Button("Update");
		resetBtn = new Button("Reset");
		updateBtn.addClickHandler(this);
		resetBtn.addClickHandler(this);
		horizontalPanel.setSpacing(5);
		horizontalPanel.add(createLabel("Category"));
		
		lblprimarycategory.setVisible(false);
		categoryListBox.addChangeHandler(this);
		if(categoryListBox.getItemCount()<2){
			categoryListBox.clear();
			categoryListBox.addItem("All Tags");
			categoryListBox.setSelectedIndex(0);
			fillCatList(ManageHeader.getUserIndustryName());
		}
		else{
			categoryListBox.insertItem("All Tags", 0);
		    categoryListBox.setSelectedIndex(0);
		}

		horizontalPanel.add(categoryListBox);
		horizontalPanel.add(lblprimarycategory);
		getCategoryTags(categoryListBox.getSelectedIndex());
		categoryListBox.setEnabled(true);
		container.add(horizontalPanel);
		container.setSpacing(5);
		container.add(scroll);
		addTagsDecorator.add(container);
		centerPanel.add(addTagsDecorator);
		HorizontalPanel btnHPanel = new HorizontalPanel();
		btnHPanel.add(updateBtn);
		btnHPanel.setSpacing(20);
		centerPanel.add(btnHPanel);
	}
		
	public void getCategoryTags(int index){
		final int id = index;
		tbcollection = new TableCollection(this);
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
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
	

	@Override
	public void fillCatList(String industryName){
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
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
						hmtagItems.put(name, tag);
					}
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryName()");
				}
			}
		};
		service.getCategoryNames(userSelectedIndustryId,industryName, callback);
	}
  
	@Override
	public FlexTable addtagsTable(){
	   flex = new FlexTable();
	   flex.setCellPadding(2);

	   categoryListBox.clear();
	   categoryListBox.addChangeHandler(this);
	   categoryListBox.addItem("--Please Select--");
	   fillCatList(userSelectedIndustryName);

	   flex.setWidget(1,0,createLabel("Industry"));
	   flex.setText(1, 1, ManageHeader.getUserIndustryName());
	   flex.setWidget(2,0,createLabel("Category"));
	   flex.setWidget(2,1,categoryListBox);
	   flex.setWidget(2,2,lblprimarycategory);
	   lblprimarycategory.setVisible(false);
	   flex.setWidget(3,0,createLabel("Name"));
	   flex.setWidget(3,1,tbTagName);
	   return flex;
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
				 String name = categoryListBox.getItemText(categoryListBox.getSelectedIndex());
				 TagItemInformation info = hmtagItems.get(name);
				 if (info.isIsprimary())
					 lblprimarycategory.setVisible(true);
					 //tblCollection.setTagLabel(false);
				 else
					 lblprimarycategory.setVisible(false);
					 //tblCollection.setTagLabel(true);
				 
				getCategoryTags(info.getTagItemId());
				 
			 }
		 }
	}
	
	@Override
	public void addTagItems(){
		getCategoryTags(0);
		categoryListBox.clear();
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

	@Override
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
			categoryListBox.clear();
			categoryListBox.addItem("All Tags");
			categoryListBox.setSelectedIndex(0);
			fillCatList(userSelectedIndustryName);
		}
		else{
			categoryListBox.insertItem("All Tags", 0);
		    categoryListBox.setSelectedIndex(0);
		}

		horizontalPanel.add(categoryListBox);
		horizontalPanel.add(lblprimarycategory);
		getCategoryTags(categoryListBox.getSelectedIndex());
		categoryListBox.setEnabled(true);
		container.add(horizontalPanel);
		container.setSpacing(5);
		container.add(scroll);
		
		addTagsDecorator.add(container);
		centerPanel.add(addTagsDecorator);
		centerPanel.add(deleteBtn);
	}
	
	public void clearCatList(){
		categoryListBox.clear();
		categoryListBox.addItem("---Please Select---");
		categoryListBox.setSelectedIndex(0);
		fillCatList(userSelectedIndustryName);
	}

	public void saveTag(TagItemInformation tagitem, String parentName,boolean isCategory){
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object arg0) {
				try{
					if(arg0.equals(true)){
						PopUpForForgotPassword popup = new PopUpForForgotPassword("The tag has been saved");
						popup.setPopupPosition(400, 400);
						popup.show();
						tbTagName.setText("");
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Entry for tag exists!");
						popup.setPopupPosition(400, 400);
						popup.show();
					}
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in saveNewTag()");
				}
			}
		};
		service.saveNewTags(tagitem,parentName,isCategory,callback);
	}

	

	
	private boolean checkprimarycategory(String parentName) {
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(Object result) {
				value=(Boolean)result;
			}
		};
		service.checkprimarycategory(parentName,callback);
		return value;
		
		
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
					popup.setPopupPosition(400, 450);
					popup.show();
				}
				else{
					//boolean valid = tagValidation(tbTagName.getText());
					boolean valid = true;
					if(valid){
						boolean bool = false;
						boolean isprimary=false;
						String parentName = categoryListBox.getItemText(categoryListBox.getSelectedIndex());
						 //String name = categoryListBox.getItemText(categoryListBox.getSelectedIndex());
						 TagItemInformation info = hmtagItems.get(parentName);
						 if (info.isIsprimary()) {
							 isprimary=true;
						}
						 					 
						
						int industryid = LHadmin.getIndustryId();
						String tagText = tbTagName.getText();
						Iterator iter = allTagsArray.iterator();
						TagItemInformation tagitem = new TagItemInformation();
						tagitem.setTagName(tagText);
						tagitem.setIndustryId(industryid);
						tagitem.setIsprimary(isprimary);
						tbTagName.selectAll();
						saveTag(tagitem, parentName,false);
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("'"+tbTagName.getText()+"' is an Invalid Tag Name");
						popup.setPopupPosition(400, 450);
						popup.show();
					}
				}
			}
			else if(btn.getText().equals("Delete")){
				selectionMap = getCurrentTbCollection().getItemMap();
				/*if(selectionMap.isEmpty() && TableCollection.flag == false){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to delete");
					popup.setPopupPosition(500, 200);
					popup.show();
				}*/
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
				if(selectionMap.isEmpty() && LHTableCollection.f == false){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to update");
					popup.setPopupPosition(400, 450);
					popup.show();
				}
				else{
					if(LHTableCollection.flag == false)
						updateSelectedTags(selectionMap);
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Cannot update. Please make sure no field is left blank");
						popup.setPopupPosition(400, 400);
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
	
	

	/*public void addPrimaryCheckBox(String name) {
		boolean isprimary = checkprimarycategory(name);
		if (isprimary)
			lblprimaricategory.setVisible(true);
		else
			lblprimaricategory.setVisible(false);
	}*/
}