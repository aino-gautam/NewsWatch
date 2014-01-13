package com.common.client;

import com.admin.client.TableCollection;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SearchPopup extends FlexTable{
    
	private TableCollection parenttable;
	Image imgSearch= new Image("images/trial/search.png");
	Image imgCancel= new Image("images/trial/cancel.png");
	
	public SearchPopup(TableCollection parentTable){
		parenttable = parentTable;
		Widget widget=parenttable.getWidget(0,parenttable.getHoverColumn());
		int left=widget.getAbsoluteLeft(); 
		int top=widget.getAbsoluteTop() + widget.getOffsetHeight();
		final PopupPanel dpFilter=new PopupPanel();
		//dpFilter.setWidth(widget.getOffsetWidth()+10 + "px");
		dpFilter.setStyleName("popupFilterByValue");
		HorizontalPanel hp = new HorizontalPanel();		
		hp.setSpacing(3);
		final TextBox txtFilter= new TextBox();
		
		txtFilter.setWidth(widget.getOffsetWidth()+ "px");		
		parenttable.getCellFormatter().removeStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell");	
		parenttable.getCellFormatter().addStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell-Filter");
		txtFilter.addKeyPressHandler(new KeyPressHandler(){

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					dpFilter.hide();
					parenttable.getCellFormatter().addStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell");			
					parenttable.getCellFormatter().removeStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell-Filter");
					try {						
						//parentContainerGrid.filterEntityList(txtFilter.getValue());
					} catch (Exception e) {					
						e.printStackTrace();
					} 
				}else if(event.getCharCode() == KeyCodes.KEY_ESCAPE){
					dpFilter.hide();
					parenttable.getCellFormatter().addStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell");			
					parenttable.getCellFormatter().removeStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell-Filter");
				}
			}});
		
		imgSearch.setStylePrimaryName("imageSearchStyle");
		imgSearch.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				dpFilter.hide();
				parenttable.getCellFormatter().addStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell");			
				parenttable.getCellFormatter().removeStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell-Filter");
				try {
					if(parenttable.getExistingTrialAccountsRef() != null){
						parenttable.getExistingTrialAccountsRef().setColumnName(parenttable.getHoverColumnName());
						parenttable.getExistingTrialAccountsRef().setSearchString(txtFilter.getText());
						parenttable.getExistingTrialAccountsRef().getSearchData();
					}
					else if(parenttable.getUserLogsStatsRef() != null){
						if(parenttable.getHoverColumnName().equals("Role")){
							if(txtFilter.getText().toLowerCase().equals("admin"))
								parenttable.getUserLogsStatsRef().setSearchString("1");
							else if(txtFilter.getText().toLowerCase().equals("subscriber"))
								parenttable.getUserLogsStatsRef().setSearchString("0");
							else if(txtFilter.getText().toLowerCase().equals("executive"))
								parenttable.getUserLogsStatsRef().setSearchString("2");
							else if(txtFilter.getText().toLowerCase().equals("trial"))
								parenttable.getUserLogsStatsRef().setSearchString("3");
							
						}else{
							parenttable.getUserLogsStatsRef().setSearchString(txtFilter.getText());
						}
						parenttable.getUserLogsStatsRef().setColumnName(parenttable.getHoverColumnName());
						parenttable.getUserLogsStatsRef().getSearchData();
					}
					else if(parenttable.getApprovedUsersRef() != null){
						parenttable.getApprovedUsersRef().setColumnName(parenttable.getHoverColumnName());
						parenttable.getApprovedUsersRef().setSearchString(txtFilter.getText());
						parenttable.getApprovedUsersRef().getSearchApprovedUsers();
					}
					else if(parenttable.getUserItemAccessRef() != null){
						parenttable.getUserItemAccessRef().setColumnName(parenttable.getHoverColumnName());
						parenttable.getUserItemAccessRef().setSearchString(txtFilter.getText());
						parenttable.getUserItemAccessRef().getSearchUserItemAccessStats();
					}
					dpFilter.hide();
					parenttable.getCellFormatter().addStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell");			
					parenttable.getCellFormatter().removeStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell-Filter");
				} catch (Exception e) {					
					e.printStackTrace();
				}
			}});
		
		imgCancel.setStylePrimaryName("imageSearchCancelStyle");
		imgCancel.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				dpFilter.hide();
				parenttable.getCellFormatter().addStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell");			
				parenttable.getCellFormatter().removeStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell-Filter");
				parenttable.removeCell(1, 1);
				
			}});
		hp.add(txtFilter);
		hp.add(imgSearch);
		hp.add(imgCancel);
		hp.setCellHorizontalAlignment(imgSearch, HasHorizontalAlignment.ALIGN_LEFT);
		hp.setCellHorizontalAlignment(imgCancel, HasHorizontalAlignment.ALIGN_LEFT);
		dpFilter.add(hp);
		dpFilter.setPopupPosition(left-5, top);
		dpFilter.setModal(true);
		dpFilter.show();
		txtFilter.setFocus(true);
	}
}
