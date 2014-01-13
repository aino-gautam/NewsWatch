package com.lighthouse.admin.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.admin.client.AdminInformationService;
import com.admin.client.AdminInformationServiceAsync;
import com.admin.client.TagItemInformation;
import com.appUtils.client.ProgressIndicatorWidget;
import com.common.client.ManageHeader;
import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.lighthouse.admin.client.domain.AdminReportItem;
import com.lighthouse.admin.client.service.LHAdminInformationService;
import com.lighthouse.admin.client.service.LHAdminInformationServiceAsync;
import com.lighthouse.report.client.domain.ReportItem;

/**
 * This class is for edit the existing News Report
 * 
 * @author Milind Bharambe
 *
 */

public class EditNewsReport extends Composite implements  MouseListener, FormHandler,ValueChangeHandler,
										ChangeHandler,ClickHandler,MouseMoveHandler,SubmitCompleteHandler,SubmitHandler  {
	private ManageHeader header = new ManageHeader();
	private DockPanel dock = new DockPanel();
	private HorizontalPanel hpanel = new HorizontalPanel();
	private DecoratorPanel decoratorforlabel = new DecoratorPanel();
	private ScrollPanel scroller = new ScrollPanel();
	private DecoratorPanel decoratorflex = new DecoratorPanel();
	private HorizontalPanel hpbuttonPanel = new HorizontalPanel();
	private Button button = new Button("Save");
	private Button buttonReset = new Button("Reset");
	private Button buttonback = new Button("Back");
	private Button subtractionButton = new Button("Remove");
	private Button deleteButton = new Button("Delete");
	private FlexTable flex = new FlexTable();
	private TextBox industryTextbox = new TextBox();
	private ListBox reportTitleListbox = new ListBox();
	private TextBox titleTextbox = new TextBox();
	private TextBox urlTextbox = new TextBox();
	private TextBox dateTextbox = new TextBox();
	private TextBox sourceTextbox = new TextBox();
	private TextArea abstractTextarea = new TextArea();
	private TextArea contentTextarea = new TextArea();
	private VerticalPanel vpanel = new VerticalPanel();
	private HorizontalPanel hpanellink = new HorizontalPanel();
	private HashMap<Integer,ArrayList> manageReports = new HashMap<Integer, ArrayList>();
	private ListBox categoryListBox = new ListBox();
	private ListBox tagList = new ListBox();
	private ListBox listboxtags = new ListBox();
	private ListBox tagListBox = new ListBox();
	private ListBox selectedtagListbox = new ListBox();
	private ListBox appliedTagListBox = new ListBox();
	private HorizontalPanel tagPanel = new HorizontalPanel();
	private HorizontalPanel appliedTagPanel = new HorizontalPanel();
	private TextBox newscenteridtbBox = new TextBox();
	private TextBox userselectedTextbox = new TextBox();
	private FormPanel form = new FormPanel();
	static boolean flag = true;
	public DateBox dateBox = new DateBox();
	Hidden lifeSpanDateTextBox = new Hidden();
	private ArrayList appliedTagList = new ArrayList();
	private FileUpload fileUpload = new FileUpload();
	private Label imagelbl = new Label("(upto 10MB)");
	int newsItemId;
	int resetIndex;
	int index = 0;
	RootPanel panel;
	String industryName;
	int userSelectedIndustryId;
	int clientWidth;
	int clientHeight;
	private String userseletedtagsStr = "";
	private ReportItem reportItem;
	int indextag = 0;
	int userSelectedTags;
	int userRemoveTags; 
	private boolean editTags = false;
	private ListBox primaryTagList=new ListBox();
	private ListBox appliedPrimaryTags=new ListBox();
	private ArrayList<String>appliedPrimaryList=new ArrayList<String>();
	String primaryTag="";
	String lifeSpan=null;
	ProgressIndicatorWidget editNewsReportLoader = new ProgressIndicatorWidget(true);
	
	public EditNewsReport(String userIndustryName, int industryId){
		clientWidth = Window.getClientWidth();
		clientHeight = Window.getClientHeight();
		industryName = userIndustryName;
		userSelectedIndustryId = industryId;
		getStyleNames();
		addInFlexTable();
		getCategoryName(industryName);
		industryTextbox.setText(industryName);
		industryTextbox.setEnabled(false);
		form.addSubmitHandler(this);
		form.addSubmitCompleteHandler(this);
		tagList.addChangeHandler(this);
		tagList.addClickHandler(this);
		categoryListBox.addChangeHandler(this);
		tagList.setVisibleItemCount(10);
		categoryListBox.setVisibleItemCount(10);
		button.addClickHandler(this);
		buttonReset.addClickHandler(this);
		buttonback.addClickHandler(this);
		reportTitleListbox.addChangeHandler(this);
		dateBox.addValueChangeHandler(this);
		vpanel.setSpacing(7);
		vpanel.add(hpanellink);
		vpanel.add(decoratorforlabel);
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setWidget(flex);
		hpanel.add(header);
		dock.setSpacing(10);
		hpbuttonPanel.setSpacing(7);
		hpbuttonPanel.add(button);
		hpbuttonPanel.add(buttonReset);
		scroller.setSize("100%", "100%");
		scroller.add(form);
		decoratorflex.add(scroller);
		dock.add(vpanel,DockPanel.NORTH);
		dock.add(decoratorflex,DockPanel.CENTER);
		dock.add(hpbuttonPanel,DockPanel.SOUTH);
		decoratorflex.setWidth("100%");
		dock.setSize("100%", "100%");
		titleTextbox.setName("titleTextBox");
		industryTextbox.setName("industryTextBox");
		urlTextbox.setName("urlTextBox");
		dateTextbox.setName("dateTextBox");
		sourceTextbox.setName("sourceTextBox");
		abstractTextarea.setName("abstractTextArea");
		fileUpload.setName("imageFileUpload");
		categoryListBox.setName("categoryList");
		contentTextarea.setName("contentTextArea");
		tagList.setName("userSelectedTags");
		newscenteridtbBox.setName("NewsCenterID");
		newscenteridtbBox.setVisible(false);
		selectedtagListbox.setName("categoriesList");
		listboxtags.setName("TagNameListBox");
		tagListBox.setName("userSelectedTags");
		primaryTagList.setName("primaryTagList");
		appliedPrimaryTags.setName("appliedPrimaryTags");
		userselectedTextbox.setName("userSelectedTagTextbox");
		appliedPrimaryTags.setWidth("50%");
		lifeSpanDateTextBox.setName("lifeSpanDateTextBox");
		subtractionButton.addClickHandler(this);
		deleteButton.addClickHandler(this);
		tagListBox.addClickHandler(this);
		selectedtagListbox.addClickHandler(this);
		listboxtags.addChangeHandler(this);
		listboxtags.addClickHandler(this);
		appliedTagListBox.addClickHandler(this);
		appliedTagListBox.setMultipleSelect(true);
		appliedTagListBox.setVisibleItemCount(6);
		appliedTagPanel.add(appliedTagListBox);
		appliedTagPanel.add(deleteButton);
		appliedTagPanel.setHeight("100%");
		tagPanel.setWidth("100%");
		tagPanel.add(selectedtagListbox);
		tagPanel.add(listboxtags);
		tagPanel.add(subtractionButton);
		tagPanel.add(tagListBox);
		tagPanel.add(userselectedTextbox);
		deleteButton.addClickHandler(this);
		userselectedTextbox.setVisible(false);
		tagListBox.setMultipleSelect(true);
		reportTitleListbox.setWidth("50%");
		listboxtags.setVisibleItemCount(10);
		tagListBox.setVisibleItemCount(10);
		selectedtagListbox.setVisibleItemCount(10);
		primaryTagList.addChangeHandler(this);
		appliedPrimaryTags.setVisibleItemCount(10);
		appliedPrimaryTags.addChangeHandler(this);
		index = categoryListBox.getSelectedIndex();
		fillPrimaryTaglist();
		initWidget(dock);
	}

	public void addInFlexTable(){
		reportItem = getReportItem();
		FlexCellFormatter formatter = flex.getFlexCellFormatter();
		flex.setWidget(0,2,createLabel("Industry"));
		flex.setWidget(1,2,createLabel("Primary Tags *"));
		flex.setWidget(2,2,createLabel("Report Title"));
		
		flex.setWidget(5,2,createLabel("Report Title"));
		flex.setWidget(6,2,createLabel("Abstract"));
		flex.setWidget(7,2,createLabel("Content"));
		flex.setWidget(8,2,createLabel("URL"));
		flex.setWidget(9,2,createLabel("Date"));
		flex.setWidget(11,2,createLabel("Life Span*"));
		flex.setWidget(12, 2, createLabel("Source"));
		flex.setWidget(13, 2, createLabel("Upload Report*"));
		flex.setWidget(14, 2, createLabel("Applied Primary Tag"));
		flex.setWidget(15, 2, createLabel("Applied Tags")); //new
		flex.setWidget(16, 2, createLabel("Add Tags")); //new
		
		flex.setWidget(0,5,industryTextbox);
		Label lblPrimaryTag = new Label("(*Please select Primary tag)"); 
		lblPrimaryTag.setStylePrimaryName("lblPrimaryTag");
		HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(10);
		panel.add(primaryTagList);
		panel.setCellWidth(lblPrimaryTag, "100%");
		panel.add(lblPrimaryTag);
		panel.add(editNewsReportLoader);
		flex.setWidget(1,5,panel);
		flex.setWidget(2,5,reportTitleListbox);
		
		flex.setWidget(5,5,titleTextbox);
		formatter.setColSpan(6, 5, 5);
		flex.setWidget(6,5,abstractTextarea);
		formatter.setColSpan(7, 5, 5);
		flex.setWidget(7,5,contentTextarea);
		flex.setWidget(8,5,urlTextbox);
		flex.setWidget(9,5,dateTextbox);
		flex.setText(10, 5, "(YYYY-MM-DD)");
		flex.setWidget(11,5,dateBox);
		flex.setWidget(11,6,lifeSpanDateTextBox);
		flex.setWidget(12, 5, sourceTextbox);
		HorizontalPanel panelForUpload = new HorizontalPanel();
		panelForUpload.setCellWidth(fileUpload, "100%");
		panelForUpload.add(fileUpload);
		panelForUpload.add(imagelbl);
		flex.setWidget(13, 5, panelForUpload);
		
		flex.setWidget(14, 5, appliedPrimaryTags);
		flex.setWidget(15, 5, appliedTagPanel);
		flex.setWidget(16, 5, tagPanel);
		flex.setWidget(17, 0, newscenteridtbBox);
	}

	/**
	 * This method used to get all the Primary Tags from DB and add those in PrimaryTagListBox
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
						appliedPrimaryList.add(name);
						primaryTagList.addItem(name);
					}
				}catch(Exception ex)				{
					ex.printStackTrace();
					System.out.println("problem in getIndustryName()");
				}
			}
		};
		service.fillprimaryTaglist(userSelectedIndustryId,industryName,callback);
	}
	
	public Label createLabel(String text){
		Label lb = new Label(text);
		lb.setStylePrimaryName("labelFlexEditNewsItems");
		return lb;
	}

	public void getStyleNames(){
		imagelbl.setStylePrimaryName("lblimageMsg");
		hpanellink.setStylePrimaryName("hpanellink");
		vpanel.setStylePrimaryName("hpanelbuttonAndVpanel");
		buttonReset.setStylePrimaryName("buttonOkAdmin");
		buttonback.setStylePrimaryName("buttonOkAdmin");
		button.setStylePrimaryName("buttonOkAdmin");
		subtractionButton.setStylePrimaryName("buttonAddSub");
		deleteButton.setStylePrimaryName("buttonAddSub");
		industryTextbox.setStylePrimaryName("textboxNewsItems");
		tagList.setStylePrimaryName("taglistboxNewsItems");
		categoryListBox.setStylePrimaryName("taglistboxNewsItems");
		urlTextbox.setStylePrimaryName("textboxNewsItems");
		sourceTextbox.setStylePrimaryName("textboxNewsItems");
		titleTextbox.setStylePrimaryName("textboxNewsItems");
		primaryTagList.setVisibleItemCount(10);
		dateTextbox.setStylePrimaryName("textboxNewsItems");
		dateBox.setStylePrimaryName("textboxNewsItems");
		abstractTextarea.setStylePrimaryName("textAreaNewsItems");
		contentTextarea.setStylePrimaryName("textAreaNewsItems");
		fileUpload.setStylePrimaryName("textboxNewsItems");
		hpanel.setStylePrimaryName("hpanelAdmin");
		decoratorforlabel.setStylePrimaryName("decoratorlabelNewUserApproval");
		listboxtags.setStylePrimaryName("taglistboxNewsItems");
		tagListBox.setStylePrimaryName("taglistboxNewsItems");
		selectedtagListbox.setStylePrimaryName("taglistboxNewsItems");
		appliedTagListBox.setStylePrimaryName("taglistboxNewsItems");
	}
	
	public void onModuleLoad() {
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}

	/**
	 * This method gets all the News Reports specific to the selected Primary Tag
	 */
	public void getAllReportsInListBox(String industryName, String tagName){
		editNewsReportLoader.enable();
		LHAdminInformationServiceAsync service = GWT.create(LHAdminInformationService.class);
		service.getAllReportsInListBox(industryName,tagName,new AsyncCallback<AdminReportItemList>() {
			@Override
			public void onFailure(Throwable caught) {
			}
			@Override
			public void onSuccess(AdminReportItemList result) {
				try{
					editNewsReportLoader.disable();
					ArrayList list = (ArrayList)result;
					if(list.size()!=0)
						addTheContent(list);
					else{
						PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no reports to edit.");
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

	public void addTheContent(ArrayList list){
		ArrayList contentContent;
		int counter= 1;
		if(list.size()!=0){
			reportTitleListbox.addItem("-------Please Select-------");
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				contentContent = new ArrayList();
				AdminReportItem reportItem = (AdminReportItem)iter.next();
				String titleStr = (String)reportItem.getNewsTitle();
				String abstractNews = (String)reportItem.getAbstractNews();
				Date date = (Date) reportItem.getNewsDate();
				String lifeSapn = reportItem.getReportLifeSpan();
				String source = reportItem.getReportLink();
				String url = reportItem.getUrl();
				int newsId = (Integer)reportItem.getNewsId();
				ArrayList associatedtags = (ArrayList)reportItem.getAssociatedTagList();
				reportTitleListbox.addItem(titleStr);
				reportTitleListbox.setName("reportTitleListbox");
				contentContent.add(titleStr);
				contentContent.add(abstractNews);
				contentContent.add(date.toString());
				contentContent.add(source);
				contentContent.add(newsId);
				contentContent.add(lifeSapn);
				contentContent.add(url);
				contentContent.add(associatedtags);
				manageReports.put(counter, contentContent);
				counter++;
			}
		}
	}

	/**
	 * This method sets the selected Report data in appropriate fields for edit 
	 */
	
	public void addReportContent(int index){
		fillAppliedPrimaryTagList();
		if(!manageReports.isEmpty()){
			if(manageReports.containsKey(index)){
				resetIndex = index;
				ArrayList abstractReport = (ArrayList)manageReports.get(index);
				String title =(String)abstractReport.get(0);
				String abstractnews = (String)abstractReport.get(1);
				String date = (String) abstractReport.get(2);
				String source = (String)abstractReport.get(3);
				newsItemId = (Integer)abstractReport.get(4);
				String lifeSapn = (String)abstractReport.get(5);
				String url = (String)abstractReport.get(6);
				ArrayList tags = (ArrayList)abstractReport.get(7);
				Date reportLifeSpanDate = getConvertedSqlDate(lifeSapn);
				lifeSpan = reportLifeSpanDate.toString();
				titleTextbox.setText(title);
				abstractTextarea.setText(abstractnews);
				dateTextbox.setText(date);
				dateBox.getTextBox().setText(reportLifeSpanDate.toString());
				sourceTextbox.setText(source);
				urlTextbox.setText(url);
				sortTags(tags);
				if(appliedTagListBox.getItemCount()!=0)
					appliedTagListBox.clear();
				if(appliedTagListBox.getItemCount() == 0){
					Iterator iter = tags.iterator();
					while(iter.hasNext()){
						TagItemInformation tagiteminfo = (TagItemInformation)iter.next();
						if(tagiteminfo.isIsprimary()){
							for(int i = 0;i<appliedPrimaryTags.getItemCount();i++){
								if(appliedPrimaryTags.getItemText(i).equals(tagiteminfo.getTagName())){
									appliedPrimaryTags.setSelectedIndex(i);
								}
							}
						}
						appliedTagListBox.addItem(tagiteminfo.getTagName());
						appliedTagList.add(tagiteminfo.getTagName());
					}
				}
				if(listboxtags.getItemCount()!=0)
					listboxtags.clear();
				if(tagListBox.getItemCount()!=0)
					tagListBox.clear();
			}
			else{
				titleTextbox.setText("");
				abstractTextarea.setText("");
				contentTextarea.setText("");
				urlTextbox.setText("");
				dateTextbox.setText("");
				sourceTextbox.setText("");
				appliedTagListBox.clear();
			}
		}
	}
	
	public void fillAppliedPrimaryTagList(){
		appliedPrimaryTags.clear();
		for(String str:appliedPrimaryList){
			appliedPrimaryTags.addItem(str);
		}
	}
	
	/**
	 * This method gets all the Categories 
	 */
	
	public void getCategoryName(String industryName){
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
					ArrayList list = (ArrayList)result;
					sortTags(list);
					Iterator iter = list.iterator();
					while(iter.hasNext()){
						TagItemInformation tag = (TagItemInformation)iter.next();
						if(!tag.isIsprimary()){
							String name = tag.getTagName();
							categoryListBox.addItem(name);
							selectedtagListbox.addItem(name);
						}
							index = categoryListBox.getSelectedIndex();
							categoryListBox.setName("categoryList");
					}
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in getIndustryName()");
				}
			}
		};
		service.getCategoryNames(userSelectedIndustryId,industryName, callback);
	}
	
	public void sortTags(ArrayList list){
		final Comparator<TagItemInformation> NAME_ORDER = new Comparator<TagItemInformation>(){
			public int compare(TagItemInformation u1, TagItemInformation u2) {
				return u1.getTagName().compareTo(u2.getTagName());
			}
		};
		Collections.sort(list,NAME_ORDER);
}
	  
	private Date getConvertedSqlDate(String strDate) {
		  Date convertedDate = null;
		  try {
		   if (strDate.contains("-")) {
			    DateTimeFormat dateFormatSql = DateTimeFormat.getFormat("yyyy-MM-dd");
			    java.util.Date date2 = dateFormatSql.parse(strDate);
			    convertedDate = new java.sql.Date(date2.getTime());
		   }else{
			    DateTimeFormat dateFormatUtil = DateTimeFormat.getFormat("yyyy MM dd hh:mm:ss");
			    java.util.Date date2 = dateFormatUtil.parse(strDate);
			    convertedDate = new java.sql.Date(date2.getTime());
		   }

		  }catch (Exception e){
			  e.printStackTrace();
		  }
		  return convertedDate;
	}
	
	public void clearAllTheFields(){
		appliedPrimaryTags.clear();
		abstractTextarea.setText("");
		contentTextarea.setText("");
		urlTextbox.setText("");
		listboxtags.clear();
		tagListBox.clear();
		appliedTagListBox.clear();
		reportTitleListbox.clear();
		abstractTextarea.setText("");
		titleTextbox.setText("");
		dateTextbox.setText("");
		sourceTextbox.setText("");
		dateBox.getTextBox().setText("");
		flex.remove(fileUpload);
		FileUpload upload = new FileUpload();
		upload.setVisible(true);
		upload.setName("imageFileUpload");
		flex.setWidget(13, 5, upload);
		//flex.clearCell(10, 5);
		if(!userseletedtagsStr.equals(null))
			userseletedtagsStr = "";
	}

	public boolean validate(){
		flag = true;
		boolean result = validateDate();
		if(flag == true){
			int index = reportTitleListbox.getSelectedIndex();
			String titleReportItem = reportTitleListbox.getItemText(index);
			if((titleTextbox.getText().equals("") || dateBox.getTextBox().getText().equals("")||
					dateTextbox.getText().equals(""))){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter the required field");
				popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
				popup.show();
				return false;
			}
			else if(titleReportItem.equals("-------Please Select-------")){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select the  report titles");
				popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
				popup.show();
				return false;
			}
			else if(appliedPrimaryTags.getValue(appliedPrimaryTags.getSelectedIndex()).equals("-------Please Select-------")){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select the Primary Tags");
				popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
				popup.show();
				return false;
			}
			else if(appliedTagListBox.getItemCount() == 0 && tagListBox.getItemCount() == 0){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select tags to apply to the news");
				popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
				popup.show();
				return false;
			}
			else if(!urlTextbox.getText().equals("")){
				if(!urlTextbox.getText().matches("^((https?|ftp)://|(www|ftp)\\b.)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")){
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter the Valid NewsReport Url");
					popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
					popup.show();
					return false;
				}
			}
		}
		else if(result == false){
			return false;
		}
		return true;
	}
	
	public boolean validateDate(){
		String userDate = dateTextbox.getText();
		LHDateValidator dateValidator = new LHDateValidator();
		boolean result = dateValidator.validate(userDate);
		return result;
	}

	public void onMouseDown(Widget arg0, int arg1, int arg2) {
	}

	public void onMouseEnter(Widget arg0) {
		//backLink.setStylePrimaryName("linkChangelabel"); 
	}
	
	public void onMouseLeave(Widget arg0) {
		//backLink.setStylePrimaryName("linklabel"); 
	}

	public void onMouseMove(Widget arg0, int arg1, int arg2) {
		
	}

	public void onMouseUp(Widget arg0, int arg1, int arg2) {
	
	}

	public ReportItem getReportItem() {
		return reportItem;
	}

	public void setReportItem(ReportItem reportItem) {
		this.reportItem = reportItem;
	}
	
	public void onSubmit(FormSubmitEvent event) {
		/*String imageurl = fileUpload.getFilename();
		if(imageurl.isEmpty()){
			flex.getWidget(12, 4).setVisible(false);
//			event.setCancelled(true);
		}*/
	}
	@Override
	public void onMouseMove(MouseMoveEvent event) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender =(Widget) event.getSource();
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Save")){
				buttonReset.setEnabled(false);
				boolean valid = validate();
				if(valid){
					for(int i=0;i<appliedTagListBox.getItemCount();i++){
						String taglist = appliedTagListBox.getItemText(i);
						if(userseletedtagsStr==null)
							userseletedtagsStr = taglist+";";
						else
							userseletedtagsStr = userseletedtagsStr + taglist+";";
					}
					for(int i=0;i<tagListBox.getItemCount();i++){
						String taglist = tagListBox.getItemText(i);
						if(!appliedTagList.contains(taglist)){
							if(userseletedtagsStr==null)
								userseletedtagsStr = taglist+";";
							else
								userseletedtagsStr = userseletedtagsStr + taglist+";";
						}
					}
					userseletedtagsStr = userseletedtagsStr + appliedPrimaryTags.getValue(appliedPrimaryTags.getSelectedIndex())+";";
					userselectedTextbox.setText(userseletedtagsStr);
					String str = Integer.toString(newsItemId);
					newscenteridtbBox.setText(str);
					String gwturlbase = GWT.getModuleBaseURL();
					String url = gwturlbase.substring(0, gwturlbase.length()-1);
					lifeSpanDateTextBox.setValue(lifeSpan);
					form.setAction(url +"/GetEditImageUploadService");
					form.submit();
			}
			else
				System.out.println("It is returning false");
		}
		else if(button.getText().equals("Reset"))
			addReportContent(resetIndex);
		else if(button.getText().equals("Remove")){
			if(tagListBox.getItemCount() == 0){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to be removed");
				popup.setPopupPosition(dock.getAbsoluteLeft()+500, dock.getAbsoluteTop());
				popup.show();
			}
			else{
				int i =0;
				while(i < tagListBox.getItemCount()){
					if(tagListBox.isItemSelected(i))
						tagListBox.removeItem(i);
					else
						i++;
				}
			}
		}
		else if(button.getText().equals("Delete")){
			if(appliedTagListBox.getItemCount() == 0){
				PopUpForForgotPassword popup = new PopUpForForgotPassword("There are no tags to be removed");
				popup.setPopupPosition(dock.getAbsoluteLeft()+500, dock.getAbsoluteTop()+300);
				popup.show();
			}
			else {
				int i =0;
				while(i < appliedTagListBox.getItemCount()){
					if(appliedTagListBox.isItemSelected(i)){
						String name = appliedTagListBox.getItemText(i);
						appliedTagList.remove(name);
						appliedTagListBox.removeItem(i);
					}
					else
						i++;
				}
			}
		}
	}
		else if(sender instanceof ListBox){
			ListBox listbox = (ListBox)sender;
			if(listbox.getName().equals("categoryList")){
				int index = categoryListBox.getSelectedIndex();
				String categoryName = categoryListBox.getItemText(index);
				getTagName(industryName,categoryName);
			}
			else if(listbox.getName().equals("TagNameListBox")){
				int index = listbox.getSelectedIndex();
				int count = 0;
				String name = listbox.getItemText(index);
				
				if(tagListBox.getItemCount() !=0){
					for(int i=0; i< tagListBox.getItemCount(); i++){
						if(name.equals(tagListBox.getItemText(i))){
							count++;
						}
					}
					if(count == 0){
						tagListBox.addItem(name);
					}
				}
				else
					tagListBox.addItem(name);
				
				tagListBox.setName("userSelectedTags");
				userSelectedTags = tagListBox.getSelectedIndex();
			}
			else if(listbox.getName().equals("categoriesList")){
				int index = selectedtagListbox.getSelectedIndex();
				String categoryName = selectedtagListbox.getItemText(index);
				editTags = true;
				getTagName(industryName,categoryName);
			}
			else if(listbox.getName().equals("userSelectedTags"))
				userRemoveTags =  tagListBox.getSelectedIndex();
		}
	}

	@Override
	public void onChange(ChangeEvent event) {
		Widget box =(Widget) event.getSource();
		if(box instanceof ListBox){
			ListBox listbox = (ListBox)box;
			if(listbox.getName().equals("reportTitleListbox")){
				reportTitleListbox.setTitle(reportTitleListbox.getValue(reportTitleListbox.getSelectedIndex()));
				int index = listbox.getSelectedIndex();
				String titleReportItem = listbox.getItemText(index);
				System.out.println("The selected Newsitem Title is>>>>>>>"+titleReportItem);
				addReportContent(index);
			}
			 else if(listbox.getName().equals("TagNameListBox")){
					int index = listbox.getSelectedIndex();
					int count = 0;
					String name = listbox.getItemText(index);
					
					if(tagListBox.getItemCount() !=0){
						for(int i=0; i< tagListBox.getItemCount(); i++){
							if(name.equals(tagListBox.getItemText(i)))
								count++;
						}
						if(count == 0)
							tagListBox.addItem(name);
					}
					else
						tagListBox.addItem(name);
					tagListBox.setName("userSelectedTags");
					userSelectedTags = tagListBox.getSelectedIndex();
			}
			 
			else if(listbox.getName().equals("categoriesList")){
				int index = selectedtagListbox.getSelectedIndex();
				String categoryName = selectedtagListbox.getItemText(index);
				editTags = true;
				getTagName(industryName,categoryName);
			}
			else if(listbox.getName().equals("userSelectedTags"))
				userRemoveTags =  tagListBox.getSelectedIndex();
			else if(listbox.getName().equals("primaryTagList")){
				int index = listbox.getSelectedIndex();
				String tagName = listbox.getItemText(index);
				if(!tagName.equals("-----Please Select-----")){
					getAllReportsInListBox(industryName,tagName);
					clearAllTheFields();
				}
				else
					clearAllTheFields();
			}
		}
	}
	
	/**
	 * This method get Tag name specific to selected Category/Tag
	 */
	public void getTagName(String industryName,String categoryName){
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
					ArrayList list = (ArrayList)result;
					sortTags(list);
					if(editTags)
						addTagsInListBox(list);
					else
						addTagNamesInListBox(list);
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in getIndustryName()");
				}
			}
		};
		service.getTagNames(industryName,categoryName, callback);
	}
	public void addTagsInListBox(ArrayList list){
		listboxtags.clear();
		editTags = false;
		Iterator iter = list.iterator();
		while(iter.hasNext()){
			TagItemInformation tag = (TagItemInformation)iter.next();
			int index = tag.getTagItemId();
			String name = tag.getTagName();
			listboxtags.addItem(name);
		}
	}
	
	public void addTagNamesInListBox(ArrayList list){
		tagList.clear();
		Iterator iter = list.iterator();
		while(iter.hasNext()){
			TagItemInformation tag = (TagItemInformation)iter.next();
			int index = tag.getTagItemId();
			String name = tag.getTagName();
			tagList.addItem(name);
			tagList.setName("TagList");
		}
	}
	

	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
//		String imageurl = fileUpload.getFilename();
//		if(imageurl.isEmpty()){
//			//flex.getWidget(10, 5).setVisible(false);
//		}
		if(event.getResults().contains("Failed")){
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Please upload file less than 10 Mb");
			popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
			popup.show();
		}
		else{
			clearAllTheFields();
			PopUpForForgotPassword popup = new PopUpForForgotPassword("Your News Report Fields are been saved");
			popup.setPopupPosition(dock.getAbsoluteLeft()+400, dock.getAbsoluteTop()+650);
			popup.show();
		}
	}

	@Override
	public void onSubmit(SubmitEvent event) {
	}
	@Override
	public void onSubmitComplete(FormSubmitCompleteEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onValueChange(ValueChangeEvent event) {
		Date date = (Date) event.getValue();
		java.sql.Date changedDate = new java.sql.Date(date.getTime());
		dateBox.getTextBox().setText(changedDate.toString());
		lifeSpan = new String();
		lifeSpan = changedDate.toString();
		lifeSpanDateTextBox.setValue(lifeSpan);
	}
	
}