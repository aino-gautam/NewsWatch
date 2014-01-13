package com.lighthouse.feed.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.admin.client.TagItemInformation;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.lighthouse.admin.client.LHEditNewsItems;

import com.lighthouse.feed.client.service.FeedService;
import com.lighthouse.feed.client.service.FeedServiceAsync;

import com.lighthouse.feed.client.ui.body.IBodyView.IBodyPresenter;

import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;
import com.sun.syndication.io.impl.FeedParsers;
/**
 * class for presenting the edit form on popup 
 * @author kiran@ensarm.com
 *
 */
public class EditLinkPopupPresenter extends LHEditNewsItems implements
		IBodyPresenter {

	private NewsItems newsItem;
	private Image closeImage = new Image("images/cross.png");
    private ArrayList tag;
    private EditLinkPopup editLinkPopup;
    private PopUpForForgotPassword popup;
    
	public EditLinkPopupPresenter(NewsItems newsItems) {

	}

	public EditLinkPopupPresenter(String userIndustryName, int industryId,
			NewsItems newsItem, EditLinkPopup editLinkPopup) {

		super(userIndustryName, industryId);
		this.newsItem = newsItem;
		this.editLinkPopup = editLinkPopup;
		newsItemId = newsItem.getNewsId();
		setNewsItems(newsItems);
		hpanellink.add(closeImage);
		closeImage.setTitle("Close");
		closeImage.addStyleName("clickable");
		
		label.setText("Please edit News");
        
		closeImage.addClickHandler(this); 
		addTheNewsContent();
		
		/*
		 * categoryListBox.addChangeHandler(this);
		 * subtractionButton.addClickHandler(this);
		 */

	}
	
	@Override
	public void createButtonPanel(){
		Button cancelButton = new Button("Cancel");
		cancelButton.setStylePrimaryName("buttonOkAdmin");
		cancelButton.addClickHandler(this);
		hpbuttonPanel.add(button);
		hpbuttonPanel.add(buttonReset);
		hpbuttonPanel.add(cancelButton);
	}
	
    /**
     * This method is for adding the particular news field on the particular widget 
     */
	void addTheNewsContent(){
		titleTextbox.setText(newsItem.getNewsTitle());
		authorTextbox.setText(newsItem.getAuthor());
		abstractTextarea.setText(newsItem.getAbstractNews());
		contentTextarea.setText(newsItem.getNewsContent());
		dateTextbox.setText(newsItem.getNewsDate());
		urlTextbox.setText(newsItem.getUrl());
		sourceTextbox.setText(newsItem.getNewsSource());
		
		ArrayList<TagItem> items = newsItem.getAssociatedTagList();
		addTagNamesInListBox(items);
		for(int i = 1; i < priorityListBox.getItemCount(); i++){
			if(priorityListBox.getItemText(i).trim().equalsIgnoreCase(newsItem.getNewsPriority())){
				priorityListBox.setSelectedIndex(i);
				break;
			}
		}
		
		
	}
	@Override
	public void addInFlexTable() {

		FlexCellFormatter formatter = flex.getFlexCellFormatter();
		// flex.setWidget(0,2,createLabel("Industry"));
		// flex.setWidget(1,2,createLabel("Primary Tags *"));
		// flex.setWidget(1,2,createLabel("News Title"));
		flex.setWidget(1, 2, createLabel("Title"));
		flex.setWidget(3, 2, createLabel("News Abstract"));
		flex.setWidget(4, 2, createLabel("News Content"));
		// flex.setWidget(4,2,createLabel("Priority"));
		flex.setWidget(6, 2, createLabel("URL"));
		flex.setWidget(7, 2, createLabel("Date"));
		flex.setWidget(9, 2, createLabel("Source"));
		flex.setWidget(10, 2, createLabel("Author"));
		flex.setWidget(11, 2, createLabel("Upload Image"));
		flex.setWidget(12, 2, createLabel("Applied Primary Tag"));
		flex.setWidget(13, 2, createLabel("Applied Tags")); // new
		flex.setWidget(14, 2, createLabel("Add Tags")); // new
		flex.setWidget(16,2,createLabel("Priority"));
		// flex.setWidget(0,5,industryTextbox);
		// Label lblPrimaryTag = new Label("(*Please select Primary tag)");
		// lblPrimaryTag.setStylePrimaryName("lblPrimaryTag");
		// HorizontalPanel panel = new HorizontalPanel();
		// panel.setSpacing(10);
		// panel.add(primaryTagList);
		// panel.setCellWidth(lblPrimaryTag, "100%");
		// panel.add(lblPrimaryTag);
		// panel.add(editNewsItemLoader);
		// flex.setWidget(1,5,panel);
		// flex.setWidget(1,5,newsTitleListbox);

		HorizontalPanel hPanelforLocking = new HorizontalPanel();
		hPanelforLocking.add(titleTextbox);

		hPanelforLocking.setCellWidth(createLabel("Is Locked "), "90%");
		hPanelforLocking.add(createLabel("Is Locked "));
		hPanelforLocking.add(chkLocked);

		flex.setWidget(1, 5, hPanelforLocking);
		flex.setWidget(2, 6, isLocked);

		formatter.setColSpan(6, 5, 5);
		flex.setWidget(3, 5, abstractTextarea);

		formatter.setColSpan(7, 5, 5);
		flex.setWidget(4, 5, contentTextarea);

		// flex.setWidget(5,5,priorityListBox);
		flex.setWidget(6, 5, urlTextbox);

		flex.setWidget(7, 5, dateTextbox);

		flex.setText(8, 5, "(YYYY-MM-DD)");
		//flex.setWidget(15,5,priorityListBox);
		flex.setWidget(9, 5, sourceTextbox);

		flex.setWidget(10, 5, authorTextbox);
		
		flex.setWidget(11, 5, fileUpload);
		flex.setWidget(12, 5, appliedPrimaryTags);

		flex.setWidget(13, 5, appliedTagPanel);
		flex.setWidget(14, 5, tagPanel);
		flex.setWidget(15, 0, newscenteridtbBox);
		flex.setWidget(16,5,priorityListBox);
		// super.addInFlexTable();
	}

	public void addTagNamesInListBox(ArrayList list) {
		appliedTagListBox.clear();
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			TagItem tag = (TagItem) iter.next();
			// int index = tag.getTagItemId();
			if(!tag.isPrimary()){
			String name = tag.getTagName();
			appliedTagListBox.addItem(name);
		}
		}
	}

	@Override
	public void getCategoryName(String industryName) {
		FeedServiceAsync service = (FeedServiceAsync) GWT
				.create(FeedService.class);

		service.getCategoryNames(userSelectedIndustryId, industryName,
				new AsyncCallback<ArrayList>() {

					@Override
					public void onSuccess(ArrayList result) {
						try {
							ArrayList list = (ArrayList) result;
							sortTags(list);
							Iterator iter = list.iterator();
							while (iter.hasNext()) {
								TagItemInformation tag = (TagItemInformation) iter
										.next();
								if (!tag.isIsprimary()) {
									String name = tag.getTagName();
									categoryListBox.addItem(name);
									selectedtagListbox.addItem(name);
								}
								index = categoryListBox.getSelectedIndex();
								categoryListBox.setName("categoryList");
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							System.out.println("problem in getIndustryName()");
						}

					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

					}
				});

	}

	@Override
	public void fillPrimaryTaglist() {
		FeedServiceAsync service = (FeedServiceAsync) GWT
				.create(FeedService.class);
		service.fillprimaryTaglist(userSelectedIndustryId, industryName,
				new AsyncCallback<ArrayList>() {

					@Override
					public void onSuccess(ArrayList result) {
						try {
							ArrayList<TagItem> tagItems=newsItem.getAssociatedTagList();
							TagItem primaryTag=null;
							for(TagItem tagItem:tagItems){
								if(tagItem.isPrimary())
									primaryTag=tagItem;
							}
							ArrayList list = (ArrayList) result;
							tag = list;
							Iterator iter = list.iterator();
							while (iter.hasNext()) {
								TagItemInformation tag = (TagItemInformation) iter
										.next();
								String name = tag.getTagName();
								appliedPrimaryList.add(name);
								primaryTagList.addItem(name);
								
								// appliedPrimaryTags.addItem(name);
								fillAppliedPrimaryTagList();
								if(tag.isIsprimary()){
									for(int i = 0;i<appliedPrimaryTags.getItemCount();i++){
										if(appliedPrimaryTags.getItemText(i).equals(primaryTag.getTagName()))
											appliedPrimaryTags.setSelectedIndex(i);
									}
								}
								
							}
						} catch (Exception ex) {
							ex.printStackTrace();
							System.out.println("problem in getIndustryName()");
						}

					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

					}
				});

	}

	@Override
	public void getTagName(String industryName, String categoryName) {
		FeedServiceAsync service = (FeedServiceAsync) GWT
				.create(FeedService.class);
		// LHAdminInformationServiceAsync service =
		// (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		service.getTagNames(industryName, categoryName,
				new AsyncCallback<ArrayList>() {

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

					}

					@Override
					public void onSuccess(ArrayList result) {
						try {
							ArrayList list = (ArrayList) result;
							sortTags(list);
							if (editTags)
								addTagsInListBox(list);
							else
								addTagNamesInListBox(list);
						} catch (Exception ex) {
							ex.printStackTrace();
							System.out.println("problem in getIndustryName()");
						}

					}
				});

	}

	@Override
	public void fillAppliedPrimaryTagList() {
		appliedPrimaryTags.clear();
		for (String str : appliedPrimaryList)
			appliedPrimaryTags.addItem(str);

	}

	@Override
	public boolean validate() {
		flag = true;
		boolean result = validateDate();
		if (flag == true) {
			indextag = tagList.getSelectedIndex();
			// String tagName = tagList.getItemText(indextag);
			// int index = newsTitleListbox.getSelectedIndex();
			// String titleNewsItem = newsTitleListbox.getItemText(index);

			if (titleTextbox.getText().equals("")) {
				//if ((abstractTextarea.getText().equals("")
					//	|| titleTextbox.getText().equals(""))) {
				PopUpForForgotPassword popup = new PopUpForForgotPassword(
						"Please enter the blank field");
				popup.setPopupPosition(dock.getAbsoluteLeft() + 400,
						dock.getAbsoluteTop() + 650);
				popup.show();
				return false;
			}
			/*
			 * else if(titleNewsItem.equals("-------Please Select-------")){
			 * PopUpForForgotPassword popup = new
			 * PopUpForForgotPassword("Please select the newstitles");
			 * popup.setPopupPosition(dock.getAbsoluteLeft()+400,
			 * dock.getAbsoluteTop()+650); popup.show(); return false; }
			 */
			else if (appliedPrimaryTags.getSelectedIndex() < 0) {
				PopUpForForgotPassword popup = new PopUpForForgotPassword(
						"Please select the Primary Tags");
				popup.setPopupPosition(dock.getAbsoluteLeft() + 400,
						dock.getAbsoluteTop() + 650);
				popup.show();
				return false;
			}
			/*
			 * else if(priorityListBox.getSelectedIndex() == 0){
			 * PopUpForForgotPassword popup = new
			 * PopUpForForgotPassword("Please select a news priority");
			 * popup.setPopupPosition(dock.getAbsoluteLeft()+400,
			 * dock.getAbsoluteTop()+650); popup.show(); return false; }
			 */
			else if (appliedTagListBox.getItemCount() == 0
					&& tagListBox.getItemCount() == 0) {
				PopUpForForgotPassword popup = new PopUpForForgotPassword(
						"Please select tags to apply to the news");
				popup.setPopupPosition(dock.getAbsoluteLeft() + 400,
						dock.getAbsoluteTop() + 650);
				popup.show();
				return false;
			} else if(!urlTextbox.getText().equals("")){
				
					if (!urlTextbox.getText().matches("^((https?|ftp)://|(www|ftp)\\b.)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
						PopUpForForgotPassword popup = new PopUpForForgotPassword(
								"Please enter the Valid NewsItem Url");
						popup.setPopupPosition(dock.getAbsoluteLeft() + 400,
								dock.getAbsoluteTop() + 650);
						popup.show();
						return false;
				}
			}
		}

		else if (result == false) {
			return false;
		}
		return true;
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender= (Widget) event.getSource();
		if(sender instanceof Image){
			if(sender.equals(closeImage)){
				editLinkPopup.hidePopup();
			}
		}else if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("OK")){
				popup.hide();
				editLinkPopup.hidePopup();
			}else if(button.getText().equals("Reset")){
				addTheNewsContent();
			}else if(button.getText().equals("Cancel")){
				editLinkPopup.hidePopup();
			}else{
				super.onClick(event);
			}
		}else{
			super.onClick(event);
		}
	}
	
	private void repopulateNewsItem(){
		newsItem.setNewsTitle(titleTextbox.getText());
		if(chkLocked.getValue() == true)
			newsItem.setIsLocked(1);
		else
			newsItem.setIsLocked(0);
		newsItem.setAbstractNews(abstractTextarea.getText());
		newsItem.setNewsContent(contentTextarea.getText());
		newsItem.setNewsPriority(priorityListBox.getItemText(priorityListBox.getSelectedIndex()));
		newsItem.setUrl(urlTextbox.getText());
		newsItem.setNewsDate(dateTextbox.getText());
		newsItem.setAuthor(authorTextbox.getText());
		newsItem.setNewsSource(sourceTextbox.getText());
		ArrayList<TagItem> associatedTagList = new ArrayList<TagItem>();
		for(int i=0;i<appliedTagListBox.getItemCount();i++){
			String taglist = appliedTagListBox.getItemText(i);
			TagItem tag = new TagItem();
			tag.setTagName(taglist);
			tag.setPrimary(false);
			associatedTagList.add(tag);
		}
		
		for(int i=0;i<tagListBox.getItemCount();i++){
			String taglist = tagListBox.getItemText(i);
			if(!appliedTagList.contains(taglist)){
				TagItem tag = new TagItem();
				tag.setTagName(taglist);
				tag.setPrimary(false);
				associatedTagList.add(tag);
			}
		}
		String primarytag = appliedPrimaryTags.getValue(appliedPrimaryTags.getSelectedIndex());
		TagItem tag = new TagItem();
		tag.setTagName(primarytag);
		tag.setPrimary(true);
		associatedTagList.add(tag);
		newsItem.setAssociatedTagList(associatedTagList);
	}
	
	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		if(event.getResults() != null){
			repopulateNewsItem();
			//clearAllTheFields();
			popup = new PopUpForForgotPassword("The news item has been successfully updated.", this);
			popup.setAutoHideEnabled(true);
			popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
			popup.show();
			//super.onSubmitComplete(event);
			editLinkPopup.getFeedNewsPresenter().refreshWidget(newsItem);
		}else{
			PopUpForForgotPassword popup = new PopUpForForgotPassword(" Unable to update news item ");
			popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
			popup.show();
			popup.setAutoHideEnabled(true);
		}
		
	}
}
