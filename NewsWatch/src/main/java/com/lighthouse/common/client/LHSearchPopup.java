package com.lighthouse.common.client;

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
import com.lighthouse.admin.client.LHTableCollection;

public class LHSearchPopup extends FlexTable{
    
	private LHTableCollection parenttable;
	Image imgSearch= new Image("images/trial/search.png");
	Image imgCancel= new Image("images/trial/cancel.png");
	
	public LHSearchPopup(LHTableCollection parentTable){
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
					if(parenttable.getNewsLetterStats() != null){
						parenttable.getNewsLetterStats().setColumnName(parenttable.getHoverColumnName());
						parenttable.getNewsLetterStats().setSearchString(txtFilter.getText());
						parenttable.getNewsLetterStats().getPaging().setCurrentpage(1);
						parenttable.getNewsLetterStats().getSearchData();
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
