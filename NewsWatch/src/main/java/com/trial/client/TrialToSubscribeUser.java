package com.trial.client;

import com.admin.client.AdminRegistration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TrialToSubscribeUser extends Composite implements ClickHandler{
	
	private VerticalPanel vpBase;
	private Label lbback = new Label("Back to existing trials");
	private AdminRegistration adminregistration;
	private ExistingTrialAccounts existingtrialaccount;
	
	public TrialToSubscribeUser(TrialAccount trialaccount,ExistingTrialAccounts existingtrialacc){
		vpBase = new VerticalPanel();
		adminregistration = new AdminRegistration();
		existingtrialaccount = existingtrialacc;
		
		adminregistration.getFirstnameTextbox().setText(trialaccount.getFirstname());
		adminregistration.getLastnameTextBox().setText(trialaccount.getLastname());
		adminregistration.getEmailTextbox().setText(trialaccount.getUseremail());
		adminregistration.getIndustryName().setEnabled(true);
		adminregistration.getIndustryName().setText(trialaccount.getNewscatalystName());
		adminregistration.getIndustryName().setEnabled(false);
		adminregistration.getAdminregInfo().setIndustryid(trialaccount.getNewscenterid());
		adminregistration.getAdminregInfo().setUserid(trialaccount.getUserid());
		adminregistration.getRbuser().setValue(true);
		adminregistration.getRbuser().setEnabled(false);
		adminregistration.getPassword().setText(trialaccount.getPassword());
		adminregistration.getRepassword().setText(trialaccount.getPassword());
		adminregistration.getRbadmin().setVisible(false);
		adminregistration.getRbsalesexecutive().setVisible(false);
		adminregistration.setValidationcount(3);
		lbback.setStylePrimaryName("clickable");
		lbback.addClickHandler(this);
		DOM.setStyleAttribute(lbback.getElement(), "textDecoration", "underline");
		vpBase.add(lbback);
		vpBase.add(adminregistration);
		vpBase.setSpacing(5);
		initWidget(vpBase);
	}

	@Override
	public void onClick(ClickEvent arg0) {
		if(arg0.getSource() instanceof Label){
		existingtrialaccount.getTrialAccounts();
		existingtrialaccount.getDeck().showWidget(0);
		}
	}

}
