package com.lighthouse.admin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.admin.client.NewUserApproval;
import com.admin.client.UserAdminInformation;
import com.appUtils.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.admin.client.service.LHAdminInformationService;
import com.lighthouse.admin.client.service.LHAdminInformationServiceAsync;

public class LhNewUserApproval extends NewUserApproval{
	String ncId;
	public LhNewUserApproval(int id,String email) {
		super(id,email);
		newsCenterName = Window.Location.getParameter("ncName");
		ncId = Window.Location.getParameter("NCID");
	}
	
	public void onClick(ClickEvent event) {
		Widget sender = (Widget)event.getSource();
		boolean flag=true;
		if(sender instanceof Button){
			Button btn = (Button)sender;
			if(btn.equals(button)){
				hashmap = tbcollection.getItemMap();
				textBoxMap = tbcollection.getTextBoxMap();
				if(!hashmap.isEmpty()){
					for(Object obj:hashmap.keySet()){
						int id = (Integer)obj;
						if(textBoxMap.containsKey(id)){
							TextBox textBox = textBoxMap.get(id);
							String text = textBox.getText();
							if(validate(text)){
								int value=Integer.parseInt(text);
								collectionMap.put(id, value);
							}
							else{
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
				else{
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
		}
	}
	
	public void addApprovedUserInfo(HashMap hashmap){ 
		final HashMap map = hashmap;
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
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
		service.saveApprovedUserInfo(hashmap,callback);
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
	
	public void userapprovalMail(ArrayList<Integer> arrayList){
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
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
			
			sendmailtoAdmin(useradmininfo);//mail to admin
			String urlQueryString = Window.Location.getQueryString();
			String LoginLinkForUser = GWT.getHostPageBaseURL() +"lhlogin.html"+urlQueryString;
			String name = (String)useradmininfo.getName();
			String email = (String)useradmininfo.getEmail();
			String password = (String)useradmininfo.getPassword();
			int duration = (Integer)useradmininfo.getDuration();

			userinfo.setBodyMail("Hi, " +name+ "\n\n\n Your subscription for the NewsCatalyst " +newsCenterName+ " has been approved for a duration of  " +duration+ " days\n\n, Here are your Login details: \n\n UserName: " +name+ "\n\n Password: " +password+ "\n\nClick the link below to return to the NewsCatalyst setup page\n" +LoginLinkForUser);
			userinfo.setPasswordMail("");
			userinfo.setRecipientsMail(email);
			userinfo.setSenderMail("no_reply@newscatalyst.com");
			userinfo.setSubjectMail("NewsCatalyst User Approval");
		}

		LHAdminInformationServiceAsync serviceforMail = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callbackforSentMail = new AsyncCallback(){
	
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			public void onSuccess(Object result) {
				try{
					Boolean valid =(Boolean) result;
					if(valid){
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

		UserAdminInformation userinfo = new UserAdminInformation();
		userinfo.setBodyMail("Hi, \n\n\n The user " + name + " for " +newsCenterName+ " has been approved for a Duration of  " +duration+ " days." );
		userinfo.setRecipientsMail(adminemail);
		userinfo.setSenderMail("no_reply@newscatalyst.com");
		userinfo.setSubjectMail("NewsCatalyst User Approval");

		LHAdminInformationServiceAsync serviceforMail = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
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
}
