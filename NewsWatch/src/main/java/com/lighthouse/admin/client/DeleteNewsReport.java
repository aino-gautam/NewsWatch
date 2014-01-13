package com.lighthouse.admin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
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
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.lighthouse.admin.client.AdminReportItemList;
import com.lighthouse.admin.client.service.LHAdminInformationService;
import com.lighthouse.admin.client.service.LHAdminInformationServiceAsync;
import com.admin.client.AdminInformationService;
import com.admin.client.AdminInformationServiceAsync;
import com.admin.client.TagItemInformation;
import com.appUtils.client.ProgressIndicatorWidget;
import com.common.client.PopUpForForgotPassword;

/**
 * This class is for delete the existing News Report
 * 
 * @author Milind Bharambe
 *
 */

public class DeleteNewsReport extends Composite implements ClickHandler ,ChangeHandler {
	private LHTableCollection tbcollection;
	private DockPanel dock = new DockPanel();
	private HorizontalPanel hpanel = new HorizontalPanel();
	private Label label = new Label("Please Check Report to Delete");
	private DecoratorPanel decoratorforlabel = new DecoratorPanel();
	private ScrollPanel scroller = new ScrollPanel();
	private DecoratorPanel decoratorflex = new DecoratorPanel();
	private HorizontalPanel hpbuttonPanel = new HorizontalPanel();
	private Button button = new Button("Delete");
	private Button backButton = new Button("Back");
	private HashMap hashmap = new HashMap();
	private VerticalPanel vpanel = new VerticalPanel();
	private VerticalPanel vrtpanel = new VerticalPanel();
	private FlexTable flex = new FlexTable();
	private ListBox primaryTagList=new ListBox();
	ProgressIndicatorWidget deleteReportLoader = new ProgressIndicatorWidget(true);
	String userSelectedIndustryName = "";
	String userIndustryname;
	int userIndustryid;
	RootPanel panel;
	int clientWidth;
	int clientHeight;

	public DeleteNewsReport(String userIndustryName,int industryId){
		dock.clear();
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		userIndustryname = userIndustryName;
		userIndustryid = industryId;
		getIndustryNameFromSession();
		addInFlexTable();
		getStyleNames();
		dock.setSpacing(30);
		scroller.setWidth(clientWidth-100+"px");
		scroller.setHeight(clientHeight-100+"px");
		
		tbcollection = new LHTableCollection();
		decoratorforlabel.add(label);
		button.addClickHandler(this);
		backButton.addClickHandler(this);
		vpanel.setSpacing(7);
		vpanel.add(decoratorforlabel);
		
		vrtpanel.setHeight("100%");
		vrtpanel.setWidth("100%");
		
		hpbuttonPanel.setSpacing(7);
		hpbuttonPanel.add(button);
		
		scroller.add(tbcollection);
		decoratorflex.add(scroller);
		dock.add(vpanel,DockPanel.NORTH);
		dock.add(vrtpanel,DockPanel.NORTH);
		dock.add(decoratorflex,DockPanel.CENTER);
		dock.add(hpbuttonPanel,DockPanel.SOUTH);
		fillPrimaryTaglist();
		initWidget(dock);
	}
	
	public void addInFlexTable(){
		FlexCellFormatter formatter = flex.getFlexCellFormatter();
		formatter.setColSpan(0, 0, 3);
		primaryTagList.setName("primaryTagList");
		primaryTagList.setVisibleItemCount(10);
		primaryTagList.addChangeHandler(this);
		flex.setWidget(0, 1, createLabel("Please Select primary Tag"));
		flex.setWidget(2, 0, createLabel("Primary Tag*"));
		flex.setWidget(2, 3, primaryTagList);
		flex.setWidget(2, 5, deleteReportLoader);
		vrtpanel.add(flex);
	}
	
	public Label createLabel(String text){
		Label lb = new Label(text);
		lb.setStylePrimaryName("labelFlexEditNewsItems");
		return lb;
	}
	
	public void getStyleNames(){
		flex.setStylePrimaryName("flexManageNewsTags");
		primaryTagList.setStylePrimaryName("taglistboxNewsItems");
		vpanel.setStylePrimaryName("hpanelbuttonAndVpanel");
		button.setStylePrimaryName("buttonOkAdmin");
		backButton.setStylePrimaryName("buttonOkAdmin");
		hpanel.setStylePrimaryName("hpanelAdmin");
		label.setStylePrimaryName("labelNewUser");
		decoratorforlabel.setStylePrimaryName("decoratorlabelNewUserApproval");
	}

	public void onModuleLoad(){
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}
	
	/**
	 * This method gets all the Primary Tags stored in DB and add those in PrimaryTagListBox
	 */
	
	public void fillPrimaryTaglist(){
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				try{
					ArrayList list = (ArrayList)result;
					Iterator iter = list.iterator();
					while(iter.hasNext()){
						TagItemInformation tag = (TagItemInformation)iter.next();
						String name = tag.getTagName();
						primaryTagList.addItem(name);
					}
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in fillPrimaryTaglist()");
				}
			}
		};
		service.fillprimaryTaglist(userIndustryid,userIndustryname,callback);
	
	}

	public void getIndustryNameFromSession(){
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
					userSelectedIndustryName = userSelectedIndustryName;
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryNameFromSession()");
				}
			}
		};
		service.getIndustryNameFromSession(callback);
	}

	/**
	 * This method gets all the News Reports specific to the selected Primary Tag
	 */
	public void getAllReportsForTag(int userIndustryid, String tagName){
		deleteReportLoader.enable();
		LHAdminInformationServiceAsync service = GWT.create(LHAdminInformationService.class);
		service.getAllReportsInListBox(userIndustryname,tagName,new AsyncCallback<AdminReportItemList>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(AdminReportItemList result) {
				try{
					
					ArrayList list = (ArrayList)result;
					deleteReportLoader.disable();
					if(list.size()!=0)
						tbcollection.addReportFieldsInTabel(list);
					else{
						tbcollection.removeAllRows();
						PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no reports to delete.");
						popup.setPopupPosition(600, 300);
						popup.show();
					}
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getAllReportsInListBox()");
				}
			}
		});
	}
	
	/**
	 * This method used to delete the selected NewsReport
	 */
	public void deleteSelectedReport(HashMap hashmap){
		final HashMap selectedTagMap = hashmap;
		LHAdminInformationServiceAsync service = GWT.create(LHAdminInformationService.class);
		service.deleteSelectedReport(selectedTagMap,new AsyncCallback() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
				PopUpForForgotPassword popup = new PopUpForForgotPassword("There is some problem in deletion.");
				popup.setPopupPosition(600, 300);
				popup.show();
			}

			@Override
			public void onSuccess(Object result) {
				try{
					tbcollection.disableCheckBox(selectedTagMap);
					PopUpForForgotPassword popup = new PopUpForForgotPassword("The Selected News Reports are deleted.");
					popup.setPopupPosition(600, 300);
					popup.show();
				}catch(Exception ex){
					ex.printStackTrace();
					
					System.out.println("problem in getUserInfo()");
				}
			}
		});
	}

	public void onClick(ClickEvent event) {
		Widget sender = (Widget)event.getSource();
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Delete")){
				hashmap = tbcollection.getItemMap();
				if(!hashmap.isEmpty()){
					deleteSelectedReport(hashmap);
				}
				else{
					button.setEnabled(true);
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please check to delete.");
					popup.setPopupPosition(600, 600);
					popup.show();
				}
			}
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		Widget box =(Widget) event.getSource();
		if(box instanceof ListBox){
			ListBox listbox = (ListBox)box;
			if(listbox.getName().equals("primaryTagList")){
				int index = listbox.getSelectedIndex();
				String tagName = listbox.getItemText(index);
				if(!tagName.equals("-----Please Select-----"))
					getAllReportsForTag(userIndustryid,tagName);
			}
		}
	}
}