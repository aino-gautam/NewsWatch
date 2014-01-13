package com.common.client;

import com.admin.client.TableCollection;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class FilterPopup extends FlexTable {
	private DecoratedPopupPanel flexFilterPopupPanel = new DecoratedPopupPanel(true);
	protected TableCollection tbCollection;
	protected Label lblFilter;
	protected Image imgFilter;
	
	public FilterPopup(TableCollection parentTable){
		tbCollection = parentTable;
		sinkEvents(Event.ONMOUSEOVER);
		sinkEvents(Event.ONMOUSEOUT);
		sinkEvents(Event.ONCLICK);
		createNormalPopup();
		setStyleName("flexPopupBorder");
	}
	protected void createNormalPopup() {
		DockPanel dckPanelRow = new DockPanel();	
		dckPanelRow.setSpacing(2);
		Image imgSortAsc = new Image("images/trial/az_up.png");				
		
		dckPanelRow.add(imgSortAsc,DockPanel.WEST);
		Label lblSortAsc = new Label("Sort Ascending");
		lblSortAsc.setStyleName("labelColumnHeaderPopup");

		dckPanelRow.add(lblSortAsc,DockPanel.CENTER);
		dckPanelRow.setCellHorizontalAlignment(lblSortAsc, HasHorizontalAlignment.ALIGN_LEFT);
		dckPanelRow.setCellVerticalAlignment(lblSortAsc, HasVerticalAlignment.ALIGN_MIDDLE);
		setWidget(0, 0, dckPanelRow);
		getRowFormatter().setStyleName(0, "flexPopupRowNormal");		 
		
		dckPanelRow = new DockPanel();
		dckPanelRow.setSpacing(2);
		Image imgSortDesc = new Image("images/trial/az_down.png");
		dckPanelRow.add(imgSortDesc,DockPanel.WEST);
		
		Label lblSortDesc = new Label("Sort Descending");
		lblSortDesc.setStyleName("labelColumnHeaderPopup");
		
		dckPanelRow.add(lblSortDesc, DockPanel.CENTER);		
		dckPanelRow.setCellHorizontalAlignment(lblSortDesc, HasHorizontalAlignment.ALIGN_LEFT);
		dckPanelRow.setCellVerticalAlignment(lblSortDesc, HasVerticalAlignment.ALIGN_MIDDLE);
		setWidget(1, 0, dckPanelRow);
		getRowFormatter().setStyleName(1, "flexPopupRowNormal");
		
		
		dckPanelRow = new DockPanel();
		dckPanelRow.setSpacing(2);
		imgFilter = new Image("images/trial/filter.png");
		
		dckPanelRow.add(imgFilter, DockPanel.WEST);
		
		lblFilter = new Label("Filter");
		lblFilter.setStyleName("labelColumnHeaderPopup");
		
		dckPanelRow.add(lblFilter, DockPanel.CENTER);
		dckPanelRow.setCellHorizontalAlignment(lblFilter, HasHorizontalAlignment.ALIGN_LEFT);
		dckPanelRow.setCellVerticalAlignment(lblFilter, HasVerticalAlignment.ALIGN_MIDDLE);
		setWidget(2, 0,dckPanelRow );
		addStyleName("sortFlexStyle");
		setCellSpacing(0);
		setCellPadding(5);
	}
	
	public void onBrowserEvent(Event event) {
		try{
			Element td = getEventTargetCell(event);				
			if (td == null)
				return;
			Element tr = DOM.getParent(td);
			if (tr == null)
				return;
			Element body = getBodyElement();
			int row = DOM.getChildIndex(body, tr);
			switch (DOM.eventGetType(event)) {			
				case Event.ONMOUSEOVER: {					
	            	getRowFormatter().removeStyleName(row, "flexPopupRowNormal");
					getRowFormatter().setStyleName(row, "flexPopupRowHover");									
					break;
				}
				case Event.ONMOUSEOUT: {
					getRowFormatter().removeStyleName(row, "flexPopupRowHover");
					getRowFormatter().setStyleName(row, "flexPopupRowNormal");
					break;
				}	
				case Event.ONCLICK:{
					getRowFormatter().removeStyleName(row, "flexPopupRowHover");
					getRowFormatter().setStyleName(row, "flexPopupRowNormal");
					switch(row){
					case 0:{
						if(tbCollection.getExistingTrialAccountsRef() != null){
							 tbCollection.getExistingTrialAccountsRef().setSortMode("asc");
							 tbCollection.getColumnHeaderPopup().hide();
							 tbCollection.getExistingTrialAccountsRef().setColumnName(tbCollection.getHoverColumnName());
							 tbCollection.getExistingTrialAccountsRef().getSortedTrialAccount();
						}
						else if(tbCollection.getUserLogsStatsRef() != null){
							tbCollection.getUserLogsStatsRef().setSortMode("asc");
							tbCollection.getColumnHeaderPopup().hide();
							tbCollection.getUserLogsStatsRef().setColumnName(tbCollection.getHoverColumnName());
							tbCollection.getUserLogsStatsRef().getSortedUserLoginStats();
						}
						else if(tbCollection.getApprovedUsersRef() != null){
							tbCollection.getApprovedUsersRef().setSortMode("asc");
							tbCollection.getColumnHeaderPopup().hide();
							tbCollection.getApprovedUsersRef().setColumnName(tbCollection.getHoverColumnName());
							tbCollection.getApprovedUsersRef().getSortedApprovedUsers();
						}
						else if(tbCollection.getUserItemAccessRef() != null){
							tbCollection.getUserItemAccessRef().setSortMode("asc");
							tbCollection.getColumnHeaderPopup().hide();
							tbCollection.getUserItemAccessRef().setColumnName(tbCollection.getHoverColumnName());
							tbCollection.getUserItemAccessRef().getSortUserItemAccesStats();
						}
						break;
					}
					case 1:{
						if(tbCollection.getExistingTrialAccountsRef() != null){
							tbCollection.getExistingTrialAccountsRef().setSortMode("desc");
							tbCollection.getColumnHeaderPopup().hide();
							tbCollection.getExistingTrialAccountsRef().setColumnName(tbCollection.getHoverColumnName());
							tbCollection.getExistingTrialAccountsRef().getSortedTrialAccount();
						}
						else if(tbCollection.getUserLogsStatsRef() != null){
							tbCollection.getUserLogsStatsRef().setSortMode("desc");
							tbCollection.getColumnHeaderPopup().hide();
							tbCollection.getUserLogsStatsRef().setColumnName(tbCollection.getHoverColumnName());
							tbCollection.getUserLogsStatsRef().getSortedUserLoginStats();
						}
						else if(tbCollection.getApprovedUsersRef() != null){
							tbCollection.getApprovedUsersRef().setSortMode("desc");
							tbCollection.getColumnHeaderPopup().hide();
							tbCollection.getApprovedUsersRef().setColumnName(tbCollection.getHoverColumnName());
							tbCollection.getApprovedUsersRef().getSortedApprovedUsers();
						}
						else if(tbCollection.getUserItemAccessRef() != null){
							tbCollection.getUserItemAccessRef().setSortMode("desc");
							tbCollection.getColumnHeaderPopup().hide();
							tbCollection.getUserItemAccessRef().setColumnName(tbCollection.getHoverColumnName());
							tbCollection.getUserItemAccessRef().getSortUserItemAccesStats();
						}
						break;
					}
					case 2:{
						if(tbCollection.getExistingTrialAccountsRef() != null){
							tbCollection.getColumnHeaderPopup().hide();
							SearchPopup searchpopup = new SearchPopup(tbCollection);
							tbCollection.insertCell(1, 1);
							tbCollection.setWidget(1, 1, searchpopup);
						    tbCollection.getExistingTrialAccountsRef().setColumnName(tbCollection.getHoverColumnName());
						}
						else if(tbCollection.getUserLogsStatsRef() != null){
							tbCollection.getColumnHeaderPopup().hide();	
						
							if(tbCollection.getHoverColumnName().equals("Name") || tbCollection.getHoverColumnName().equals("email") || tbCollection.getHoverColumnName().equals("Role")){
								SearchPopup searchpopup = new SearchPopup(tbCollection);
								tbCollection.insertCell(1, 1);
								tbCollection.setWidget(1, 1,searchpopup);
							}
							else{
 							    DateFilterPopup dateFilterPopup = new DateFilterPopup(tbCollection);
 							    tbCollection.insertCell(1, 1);
 								tbCollection.setWidget(1, 1,dateFilterPopup);
							}
							tbCollection.getUserLogsStatsRef().setColumnName(tbCollection.getHoverColumnName());
						}
						else if(tbCollection.getApprovedUsersRef() != null){
							tbCollection.getColumnHeaderPopup().hide();
							SearchPopup searchpopup = new SearchPopup(tbCollection);
							tbCollection.insertCell(1, 1);
							tbCollection.setWidget(1, 1, searchpopup);
							tbCollection.getApprovedUsersRef().setColumnName(tbCollection.getHoverColumnName());
						}
						else if(tbCollection.getUserItemAccessRef() != null){
							tbCollection.getColumnHeaderPopup().hide();
							SearchPopup searchpopup = new SearchPopup(tbCollection);
							tbCollection.insertCell(1, 1);
							tbCollection.setWidget(1, 1, searchpopup);
							tbCollection.getUserItemAccessRef().setColumnName(tbCollection.getHoverColumnName());
						}
					    break;
					}
					}
					break;
			}
		}				
		super.onBrowserEvent(event);
	}
	catch(Exception ex)	{
		ex.printStackTrace();
	}
	}
	public Label getLblFilter() {
		return lblFilter;
	}
	public void setLblFilter(Label lblFilter) {
		this.lblFilter = lblFilter;
	}
	public Image getImgFilter() {
		return imgFilter;
	}
	public void setImgFilter(Image imgFilter) {
		this.imgFilter = imgFilter;
	}
	
}
