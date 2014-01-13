package com.lighthouse.admin.client;

import java.util.HashMap;

import com.admin.client.ManageUsers;
import com.appUtils.client.NotificationPopup;
import com.appUtils.client.NotificationPopup.NotificationType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.admin.client.service.LHAdminInformationService;
import com.lighthouse.admin.client.service.LHAdminInformationServiceAsync;

public class LHManageUsers extends ManageUsers {

	public LHManageUsers(){
		super();
	}
	
	public void onClick(ClickEvent event) {
		Widget sender = (Widget)event.getSource();
		if(sender instanceof Button){
			Button button = (Button)sender;
			if(button.getText().equals("Delete")){
				button.setEnabled(true);
				hashmap = tbcollection.getItemMap();
				if(!hashmap.isEmpty()){
					button.setEnabled(true);
					deleteUser(hashmap);
				}
				else{
					NotificationPopup popup = new NotificationPopup("Please select user to delele",NotificationType.NOTIFICATION);
					popup.createUI();
					popup.setPopupPosition(600, 300);
					popup.show();
					button.setEnabled(true);
				}
			}
		}
	}
	
	public void deleteUser(HashMap hashmap){
		final HashMap map = hashmap;
		LHAdminInformationServiceAsync service = (LHAdminInformationServiceAsync)GWT.create(LHAdminInformationService.class);
		AsyncCallback callback = new AsyncCallback(){

			public void onFailure(Throwable caught) {
				caught.printStackTrace();	
			}
			public void onSuccess(Object result) {
				try{
					tbcollection.disableCheckBox(map);
					System.out.println("Successful in deleteUser ");
					NotificationPopup popup = new NotificationPopup("The users has been deleted",NotificationType.NOTIFICATION);
					popup.createUI();
					popup.setPopupPosition(600, 300);
					popup.show();
					deletebtn.setEnabled(false);
				}catch(Exception ex){
					ex.printStackTrace();
					System.out.println("problem in addApprovedUserInfo()");
				}
			}
		};
		service.deleteUser(hashmap, callback);
	}
}
