package com.newscenter.client.obsolete;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;


public class CategoryService 
{

	public ArrayList categoryArray = new ArrayList();
	public ArrayList tagArray = new ArrayList();
	
	public CategoryService(String IndustryName)
	{
		getAllCategoryForIndustry(IndustryName);
		getAllTagsForIndustry(IndustryName);
	}
	
	public void getAllCategoryForIndustry(String industry)
	{
		CategoryProviderServiceAsync service = (CategoryProviderServiceAsync)GWT.create(CategoryProviderService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "NewsCenter";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}

			public void onSuccess(Object result) {
				try{
					categoryArray = (ArrayList)result;
					setCategoryArray(categoryArray);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("Problem in getUserSelectedTabName()");
				}	
			}
		};
		service.getAllCategoryForIndustry(industry,callback);
	}
	public void getAllTagsForIndustry(String industry)
	{
		CategoryProviderServiceAsync service = (CategoryProviderServiceAsync)GWT.create(CategoryProviderService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "NewsCenter";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}

			public void onSuccess(Object result) {
				try{
					tagArray = (ArrayList)result;
					setTagArray(tagArray);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("Problem in getUserSelectedTabName()");
				}	
			}
		};
		service.getAllTagsForIndustry(industry,callback);
		
	}

	public ArrayList getCategoryArray() {
		return categoryArray;
	}

	public void setCategoryArray(ArrayList categoryArray) {
		this.categoryArray = categoryArray;
	}

	public ArrayList getTagArray() {
		return tagArray;
	}

	public void setTagArray(ArrayList tagArray) {
		this.tagArray = tagArray;
	}
}
