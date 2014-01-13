package com.common.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Statistics extends Composite {
 
	private VerticalPanel vpBase = new VerticalPanel();
	private Label lbl = new Label("Please select any of the following: ");
	private DeckPanel deck = new DeckPanel();
	UserLogStats log;
	UserItemAccessWidget itemaccess;
	private int industryid;
	
	public Statistics(int industryId){
		this.industryid = industryId;
		itemaccess = new UserItemAccessWidget();
		log = new UserLogStats();
		log.setIndustryId(industryid);
		log.getLoginStatistics();
		lbl.setStylePrimaryName("labelSelection");
		
		final ListBox selectionListBox = new ListBox();
		selectionListBox.addItem("User login statistics");
		selectionListBox.addItem("Item Access statistics");
		
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(lbl);
		hp.add(selectionListBox);
		hp.setSpacing(5);
		
		selectionListBox.setSelectedIndex(0);
		vpBase.add(hp);
		vpBase.add(deck);
		vpBase.setWidth("100%");
		deck.setWidth("100%");
		deck.add(log);
		deck.add(itemaccess);
		deck.showWidget(0);
		
		initWidget(vpBase);
		selectionListBox.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent arg0) {
				if(selectionListBox.getSelectedIndex() == 0){
					log.setIndustryId(industryid);
					log.getLoginStatistics();
					deck.showWidget(0);
					
				}
				else if(selectionListBox.getSelectedIndex() == 1){
					itemaccess.setIndustryId(industryid);
					itemaccess.getUserAccessStatistics();
					deck.add(itemaccess);
					deck.showWidget(1);
				}
				
			}
			
		});
		
		
	}
}
