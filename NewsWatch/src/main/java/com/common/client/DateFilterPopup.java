package com.common.client;

import java.util.Date;

import com.admin.client.TableCollection;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DatePicker;

public class DateFilterPopup extends FlexTable implements ClickHandler, ValueChangeHandler<Date>{
	
	private TableCollection parenttable;
	private Image imgSearch= new Image("images/trial/search.png");
	private Image imgCancel= new Image("images/trial/cancel.png");
	private TextBox tbFromDate = new TextBox();
	private TextBox tbToDate = new TextBox();
	private DatePicker fromDatePicker = new DatePicker();
	private DatePicker toDatePicker = new DatePicker();
	private PopupPanel fromDatePopup = new PopupPanel();
	private PopupPanel toDatePopup = new PopupPanel();
	
	public DateFilterPopup(TableCollection prtTb){
		parenttable = prtTb;
		Widget widget=parenttable.getWidget(0,parenttable.getHoverColumn());
		
		int left=widget.getAbsoluteLeft(); 
		int top=widget.getAbsoluteTop() + widget.getOffsetHeight();
		final PopupPanel dpFilter=new PopupPanel();
	
		dpFilter.setStyleName("popupFilterByValue");
		
		parenttable.getCellFormatter().removeStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell");	
		parenttable.getCellFormatter().addStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell-Filter");
		tbFromDate.setWidth(widget.getOffsetWidth()+ "px");	
		tbToDate.setWidth(widget.getOffsetWidth()+ "px");
		
		VerticalPanel vp = new VerticalPanel();
		 HorizontalPanel hp1 = new HorizontalPanel();
		 hp1.add(tbFromDate);
		 hp1.add(tbToDate);
		 hp1.setSpacing(2);
		 HorizontalPanel hp2 = new HorizontalPanel();
		 hp2.add(imgSearch);
		 hp2.add(imgCancel);
		 hp2.setSpacing(2);
		 
		 tbFromDate.addClickHandler(this);
		 tbToDate.addClickHandler(this);
		 toDatePicker.addValueChangeHandler(this);
		 fromDatePicker.addValueChangeHandler(this);
		 toDatePopup.add(toDatePicker);
		 toDatePopup.setAutoHideEnabled(true);
		 fromDatePopup.add(fromDatePicker);
		 fromDatePopup.setAutoHideEnabled(true);
		
		 vp.add(hp1);
		 vp.add(hp2);
		 vp.setSpacing(2);
		 dpFilter.add(vp);
		 dpFilter.setPopupPosition(left-5, top);
		 dpFilter.setModal(true);
		 dpFilter.show();
		 
		 imgSearch.setStylePrimaryName("imageSearchStyle");
			imgSearch.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					dpFilter.hide();
					parenttable.getCellFormatter().addStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell");			
					parenttable.getCellFormatter().removeStyleName(0, parenttable.getHoverColumn(), "SimpleFlex-Heading-Cell-Filter");
					try {
						parenttable.getUserLogsStatsRef().setColumnName(parenttable.getHoverColumnName());
						parenttable.getUserLogsStatsRef().setFromDate(tbFromDate.getText());
						parenttable.getUserLogsStatsRef().setToDate(tbToDate.getText());
						parenttable.getUserLogsStatsRef().getSearchData();
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
		
	}

	@Override
	public void onClick(ClickEvent arg0) {
		if(arg0.getSource() == tbFromDate){
		  fromDatePopup.setPopupPosition(tbFromDate.getAbsoluteLeft(), tbFromDate.getAbsoluteTop() +20);
		  fromDatePopup.show();
		}
		if(arg0.getSource() == tbToDate){
		  toDatePopup.setPopupPosition(tbToDate.getAbsoluteLeft(), tbToDate.getAbsoluteTop() + 20);
		  toDatePopup.show();
		}
		
	}

	@Override
	public void onValueChange(ValueChangeEvent<Date> event) {
		if (event.getSource() == fromDatePicker) {
			Date date = (Date) event.getValue();
			String dateString = DateTimeFormat.getFormat("yyyy-MM-dd").format(date);
			tbFromDate.setText(dateString);
			fromDatePopup.hide();
		}
		
		else if (event.getSource() == toDatePicker) {
			Date date = (Date) event.getValue();
			String dateString = DateTimeFormat.getFormat("yyyy-MM-dd").format(date);
			tbToDate.setText(dateString);
			toDatePopup.hide();
		}
		
	}

}
