package com.lighthouse.admin.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.admin.client.TagItemInformation;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.admin.client.service.LHAdminInformationService;
import com.lighthouse.admin.client.service.LHAdminInformationServiceAsync;


public class LHManageCategories extends com.admin.client.ManageCategories implements ClickHandler{
	
	CheckBox primarychkbox =new CheckBox("Primary category");
	boolean primarychk=false;
	int tagCount=0,tagLimit=0;
	boolean isPrimaryDefined;
	public LHManageCategories(String userIndustryName, int userIndustryId) {
		super(userIndustryName, userIndustryId);
		primarychkbox.addClickHandler(this);
	}
	@Override
	public FlexTable addCategoriesTable(){
		   flex = new FlexTable();
		   flex.setCellPadding(2);
		   flex.setWidget(1,0,createLabel("Industry"));
		   flex.setText(1, 1, userSelectedIndustry);
		   flex.setWidget(2,0,createLabel("Category"));
		   flex.setWidget(2,1,tbCatName);
		   flex.setWidget(3, 1, primarychkbox);
		   if(isPrimaryDefined){
			   primarychkbox.setEnabled(false);
		   }
		   return flex;
		}
	
	@Override
	public void onClick(ClickEvent event) {
		
		Widget arg0 = (Widget)event.getSource();
		
		if(arg0 instanceof Button){
			Button btn = (Button)arg0;
			if(btn.getText().equals("Save")){
				if(tbCatName.getText().equals("")){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter the Category Name");
					popup.setPopupPosition(400, 350);
					popup.show();
				}
				else{
					boolean valid = true;
					if(valid){
						boolean bool = false;
						int industryid = LHadmin.getIndustryId();
						int parentid = getCategoriesParentId();
						String categoryText = tbCatName.getText();

						Iterator iter = allCatList.iterator();
						System.out.println("Categories list: "+allCatList.size());
						while(iter.hasNext()){
							TagItemInformation tag = (TagItemInformation)iter.next();
							if(categoryText.trim().equals(tag.getTagName()) && parentid == tag.getParentId() && industryid == tag.getIndustryId() ){
								bool = true;
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Entry for category exists!");
								popup.setPopupPosition(400, 350);
								popup.show();
							}
						}
						if(!bool){
							TagItemInformation tagitem = new TagItemInformation();
							tagitem.setTagName(categoryText);
							tagitem.setParentId(parentid);
							tagitem.setIndustryId(industryid);
							tagitem.setIsprimary(primarychk);
							tbCatName.setText("");
							allCatList.add(tagitem);
							saveCategory(tagitem, userSelectedIndustryName, true);
						}
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("'"+tbCatName.getText()+"' is an Invalid category Name");
						popup.setPopupPosition(400, 350);
						popup.show();
                    }
				}
			}
			else if(btn.getText().equals("Delete")){
				selectionMap = getCurrentTbCollection().getItemMap();

				if(selectionMap.isEmpty()){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no Categories to delete");
					popup.setPopupPosition(500, 350);
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
				if(selectionMap.isEmpty() && LHTableCollection.f == false){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no updated Categories");
					popup.setPopupPosition(500, 350);
					popup.show();
				}
				else{
					if(LHTableCollection.flag == false){
						setUpdatedCatMap(selectionMap);
						updateSelectedCategories(selectionMap);
					}
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("Cannot update. Please make sure no field is left blank");
						popup.setPopupPosition(400, 350);
						popup.show();
					}
				}
			}
			else if(btn.getText().equals("Reset")){
				int id = getCategoriesParentId();
				getCategories(id);
			}
		}
		else if(arg0 instanceof CheckBox){
			CheckBox chk = (CheckBox) event.getSource();
			if(chk.getValue()){
				primarychk=true;
			}
		}
	}
	@Override
	public void onModuleLoad() {
		rootPanel = RootPanel.get();
		rootPanel.clear();
		rootPanel.add(this);
	}
	
	@Override
	public void deleteSelectedCategories(HashMap tagMap) {
		final HashMap selectedTagMap = tagMap;
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
				PopUpForForgotPassword popup = new PopUpForForgotPassword("There is some problem in deletion.");
				popup.setPopupPosition(600,350);
				popup.show();
			}

			public void onSuccess(Object result) {
				try{
					HashMap<String, Serializable>  map = new HashMap<String, Serializable>();
					ArrayList<TagItemInformation> list = new ArrayList<TagItemInformation>();
					map = (HashMap<String, Serializable>) result;
					for(String str : map.keySet()){
						if(str.equalsIgnoreCase("tagList"))
							list = (ArrayList<TagItemInformation>) map.get(str);
						else if(str.equalsIgnoreCase("tagsCount"))
							tagCount = (Integer) map.get(str);
						else if(str.equalsIgnoreCase("tagsLimit"))
							tagLimit = (Integer) map.get(str);
						else if(str.equalsIgnoreCase("isPrimaryDefined"))
							isPrimaryDefined = (Boolean) map.get(str);
					}
					Iterator iter = list.iterator();
					while(iter.hasNext()){
						TagItemInformation tag = (TagItemInformation)iter.next();
						setCategoriesParentId(tag.getParentId());
						allCatList.add(tag);
					}
					tbcollection.disableCheckBox(selectedTagMap);
					deleteBtn.setEnabled(false);
					PopUpForForgotPassword popup = new PopUpForForgotPassword("The selected categories were deleted");
					popup.setPopupPosition(600, 350);
					popup.show();
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in deleteSelectedCategories()");
				}
			}
		};
		service.deleteSelectedTags(selectedTagMap,true,callback);
	}
	
	@Override
	public void getCatList(String industryName){
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}
			public void onSuccess(Object result) {
				try{
					HashMap<String, Serializable>  map = new HashMap<String, Serializable>();
					ArrayList<TagItemInformation> list = new ArrayList<TagItemInformation>();
					map = (HashMap<String, Serializable>) result;
					for(String str : map.keySet()){
						if(str.equalsIgnoreCase("tagList"))
							list = (ArrayList<TagItemInformation>) map.get(str);
						else if(str.equalsIgnoreCase("tagsCount"))
							tagCount = (Integer) map.get(str);
						else if(str.equalsIgnoreCase("tagsLimit"))
							tagLimit = (Integer) map.get(str);
						else if(str.equalsIgnoreCase("isPrimaryDefined"))
							isPrimaryDefined = (Boolean) map.get(str);
					}
					Iterator iter = list.iterator();
					while(iter.hasNext()){
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
		service.getCategoryNames(callback);
	}
	
	@Override
	public void onChange(ChangeEvent event) {
		Widget arg0 = (Widget)event.getSource();
		if(arg0 instanceof ListBox)
		{
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
	
	public void saveCategory(TagItemInformation tagitem, String parentName, boolean isCategory){
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object arg0) {
				try{
					if(primarychk){
						primarychkbox.setEnabled(false);
						primarychk=false;
					}
					PopUpForForgotPassword popup = new PopUpForForgotPassword("The category has been saved");
					popup.setPopupPosition(400, 300);
					popup.show();
					tbCatName.setText("");
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in saveCategory()");
				}
			}
		};
		service.saveNewTags(tagitem,parentName,isCategory,callback);
	}
}
