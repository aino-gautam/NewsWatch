package com.admin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.common.client.ManageHeader;
import com.common.client.PopUpForForgotPassword;
import com.common.client.LogoutPage;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class NewUserApproval extends Composite implements EntryPoint, ClickHandler
{
	private DecoratorPanel decoratorflex = new DecoratorPanel();
	protected Button button = new Button("Approve");
	private DockPanel dock = new DockPanel();
	private ScrollPanel scroller = new ScrollPanel();
	protected HashMap hashmap = new HashMap();
	private HorizontalPanel hpanel = new HorizontalPanel();
	private VerticalPanel contanerPanel = new VerticalPanel();
	protected TableCollection tbcollection;
	protected HashMap<Integer, TextBox> textBoxMap = new HashMap<Integer, TextBox>();
	protected HashMap<Integer, Integer> collectionMap = new HashMap<Integer, Integer>();
	private Label logoutlabel = new Label("logout");
	private HorizontalPanel hpanelbutton = new HorizontalPanel();
	private HorizontalPanel hpanellink = new HorizontalPanel();
	private VerticalPanel vpanel = new VerticalPanel();
	protected String newsCenterName = "";
	RootPanel panel;
	protected UserAdminInformation useradmininfo;
	HashMap map;
	private int approvedUserCount = 0;
	public int count = 0;
	protected String LoginLink = "";
	protected String adminemail = "";
	protected ArrayList<Integer> arrayList = new ArrayList<Integer>();
	protected ArrayList<UserAdminInformation> array = new ArrayList<UserAdminInformation>();

	public NewUserApproval(int industryid, String email){
		String urlClient = GWT.getModuleBaseURL();
		String[]  url = new String[5];
		url = urlClient.split("/");
		LoginLink = url[0]+"//"+url[2];
		getUserInfo();
		newsCenterName = ManageHeader.getUserIndustryName();//AdminPage.getIndustryName();
		button.setStylePrimaryName("buttonOkAdmin");
		scroller.setStylePrimaryName("scrollerforDecorator");
		//button.addClickListener(this);
		button.addClickHandler(this);
		hpanelbutton.add(button);

		dock.setSpacing(7);
		tbcollection = new TableCollection();
		scroller.add(tbcollection);
		decoratorflex.add(scroller);

		adminemail = email;
		dock.add(decoratorflex,DockPanel.CENTER);
		dock.add(hpanelbutton,DockPanel.SOUTH);

		initWidget(dock);
	}

	public void getUserInfo(){
		int industryid = ManageHeader.getUserIndustryId();
		System.out.println("industry id is..."+industryid);
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
					newsCenterName = ManageHeader.getUserIndustryName();
					tbcollection.setNewsCenterName(newsCenterName);
					tbcollection.addInTable(list);
				}catch(Exception ex)
				{
					ex.printStackTrace();
					System.out.println("problem in getUserInfo()");
				}
			}
		};
		service.getUserInformation(industryid,callback);
	}

	public CheckBox createCheckbox(int id)
	{
		CheckBox checkbox = new CheckBox();
		checkbox.setTabIndex(id);
		return checkbox;
	}

	public void onModuleLoad() 
	{
		panel = RootPanel.get();
		panel.clear();
		panel.add(this);
	}

	public void onClick(Widget sender)
	{
		/*boolean flag=true;
		if(sender instanceof Button)
		{
			Button button = (Button)sender;
			if(button.getText().equals("Approve"))
			{
				hashmap = tbcollection.getItemMap();
				textBoxMap = tbcollection.getTextBoxMap();
				if(!hashmap.isEmpty())
				{
					for(Object obj:hashmap.keySet())
					{
						int id = (Integer)obj;
						if(textBoxMap.containsKey(id)){
							TextBox textBox = textBoxMap.get(id);
							String text = textBox.getText();
							if(validate(text))
							{
								int value=Integer.parseInt(text);
								collectionMap.put(id, value);
							}
							else
							{
								collectionMap.clear();
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter digits for duration");
								popup.setPopupPosition(600, 300);
								popup.show();
								button.setEnabled(true);
								System.out.println("HashMap after clearing: "+collectionMap);
								flag=false;
								break;
							}
							if(!flag)
								break;
						}
					}
				}
				else
				{
					button.setEnabled(true);
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select to approve user.");
					popup.setPopupPosition(600, 300);
					popup.show();
					flag= false;
				}
				if(flag){
					addApprovedUserInfo(collectionMap);
				}
			}
			else if(button.getText().equals("Back"))
			{
				AdminPage admin = new AdminPage();
				admin.onModuleLoad();
			}
		}
		else if(sender instanceof Label)
		{
			Label label = (Label) sender;
			if(label.getText().equals("logout"))
			{
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);
			}
		}*/
	}
	protected boolean validate(String text)
	{
		if(text.matches("[1-9][0-9]*"))
		{
			return true;
		}
		else
			return false;
	}
	public void addApprovedUserInfo(HashMap hashmap)
	{ 
		final HashMap map = hashmap;
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
					tbcollection.disableCheckBox(map);
					tbcollection.disableTextBox(map);
					System.out.println("Successful in saving the user approval");
					idmap(map);
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in addApprovedUserInfo()");
				}
			}
		};
		service.saveApprovedUserInfo(hashmap,ManageHeader.getUserIndustryName()/*"Hearing Aid"*/,callback);
	}

	public void idmap(HashMap map){
		setApprovedUserCount(map.size());
		for(Object obj:map.keySet()){
			int id = (Integer)obj;
			int value = (Integer)map.get(id);
			System.out.println("the values are >>>>>>>>>>>>"+value);
			arrayList.add(id);
		}
		userapprovalMail(arrayList);
	}

	public void userapprovalMail(ArrayList<Integer> arrayList){//int index
		AdminInformationServiceAsync service = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "admin";
		endpoint.setServiceEntryPoint(moduleRelativeURL);
		Iterator iter = arrayList.iterator();
		while(iter.hasNext()){
			int index = (Integer)iter.next();
			useradmininfo = new UserAdminInformation();
			useradmininfo.setUserId(index);
			array.add(useradmininfo);
		}
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				ArrayList<UserAdminInformation> list = (ArrayList<UserAdminInformation>)result;
				System.out.println("THE FUNCTION IS>>>>>>>>>>>>>>>>>>>>>>>>>>>>...");
				//Iterator iter = list.iterator();
				mailsending(list);
			}
		};
		service.getUserApprovalItems(array, callback);//useradmininfo
	}

	public void mailsending(ArrayList<UserAdminInformation> list){
		UserAdminInformation userinfo = new UserAdminInformation();
		Iterator iter = list.iterator();
		while(iter.hasNext()){
			UserAdminInformation useradmininfo = (UserAdminInformation)iter.next();
			sendmailtoAdmin(useradmininfo);

			String name = (String)useradmininfo.getName();
			String email = (String)useradmininfo.getEmail();
			String password = (String)useradmininfo.getPassword();
			int duration = (Integer)useradmininfo.getDuration();
			String newscenterName = (String)ManageHeader.getUserIndustryName();

			userinfo.setBodyMail("Hi " +name+ "\n\n\n Your subscription for the NewsCatalyst " +newscenterName+ " has been approved for a duration of  " +duration+ " days\n\n Here are your Login details: \n\n UserName: " +name+ "\n\n Password: " +password+ "\n\nClick the link below to return to the NewsCatalyst setup page\n" +LoginLink);
			userinfo.setPasswordMail("");
			userinfo.setRecipientsMail(email);
			userinfo.setSenderMail("no_reply@newscatalyst.com");
			userinfo.setSubjectMail("NewsCatalyst User Approval");
		}

		AdminInformationServiceAsync serviceforMail = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) serviceforMail;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "administrator";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callbackforSentMail = new AsyncCallback(){
	
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				try{
					Boolean valid =(Boolean) result;
					if(valid == false){
						System.out.println(count);
						PopUpForForgotPassword popupforgot = new PopUpForForgotPassword("The user(s) has been approved and the mail has been sent" );
						popupforgot.setPopupPosition(600, 350);
						popupforgot.show();
					}
				}
				catch(Exception ex){
					System.out.println("exception in on success");
					ex.printStackTrace();
				}
			}
		};
		serviceforMail.sendMailForApproval(userinfo, callbackforSentMail);
	}

	public void sendmailtoAdmin(UserAdminInformation userAdminInfo){//ArrayList list
		String name = (String)userAdminInfo.getName();
		int duration = (Integer)userAdminInfo.getDuration();
		String newscenterName = (String)ManageHeader.getUserIndustryName();

		UserAdminInformation userinfo = new UserAdminInformation();
		userinfo.setBodyMail("Hi \n\n\n The user " + name + "for " +newscenterName+ " has been approved for a Duration of  " +duration+ " days" );
		userinfo.setRecipientsMail(adminemail);
		userinfo.setSenderMail("no_reply@newscatalyst.com");
		userinfo.setSubjectMail("NewsCatalyst User Approval");

		AdminInformationServiceAsync serviceforMail = (AdminInformationServiceAsync)GWT.create(AdminInformationService.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) serviceforMail;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "administrator";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callbackforSentMail = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				try{
					Boolean valid =(Boolean) result;
					if(valid == false){
						count+=1;
					}
				}
				catch(Exception ex){
					System.out.println("exception in on success");
					ex.printStackTrace();
				}
			}
		};
		serviceforMail.sendMailForApproval(userinfo, callbackforSentMail);
	}

	public int getApprovedUserCount() {
		return approvedUserCount;
	}

	public void setApprovedUserCount(int approvedUserCount) {
		this.approvedUserCount = approvedUserCount;
	}

	@Override
	public void onClick(ClickEvent event) {
		
		Widget sender = (Widget)event.getSource();
		boolean flag=true;
		if(sender instanceof Button)
		{
			Button button = (Button)sender;
			if(button.getText().equals("Approve"))
			{
				hashmap = tbcollection.getItemMap();
				textBoxMap = tbcollection.getTextBoxMap();
				if(!hashmap.isEmpty())
				{
					for(Object obj:hashmap.keySet())
					{
						int id = (Integer)obj;
						if(textBoxMap.containsKey(id)){
							TextBox textBox = textBoxMap.get(id);
							String text = textBox.getText();
							if(validate(text))
							{
								int value=Integer.parseInt(text);
								collectionMap.put(id, value);
							}
							else
							{
								collectionMap.clear();
								PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter digits for duration");
								popup.setPopupPosition(600, 300);
								popup.show();
								button.setEnabled(true);
								System.out.println("HashMap after clearing: "+collectionMap);
								flag=false;
								break;
							}
							if(!flag)
								break;
						}
					}
				}
				else
				{
					button.setEnabled(true);
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Please select to approve user.");
					popup.setPopupPosition(600, 300);
					popup.show();
					flag= false;
				}
				if(flag){
					addApprovedUserInfo(collectionMap);
				}
			}
			else if(button.getText().equals("Back"))
			{
				AdminPage admin = new AdminPage();
				admin.onModuleLoad();
			}
		}
		else if(sender instanceof Label)
		{
			Label label = (Label) sender;
			if(label.getText().equals("logout"))
			{
				LogoutPage logout = new LogoutPage();
				logout.removeFromSession(1);
			}
		}
	}
}