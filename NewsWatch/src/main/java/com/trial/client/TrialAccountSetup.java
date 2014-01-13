package com.trial.client;

import java.util.HashMap;
import com.common.client.ValidationException;
import com.common.client.Validators;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.login.client.UserInformation;

public class TrialAccountSetup extends Composite implements ClickHandler,ChangeHandler {
	
	private ListBox lbselectcatalyst = new ListBox();
	private CheckBox cbemailalert = new CheckBox("Include alert email newsletter");
	private TextBox tbfirstname;
	private TextBox tblastname;
	private TextBox tbemail;
	private Image impreviewsetuptrial = new Image("images/trial/previewsetuptrial.gif");
	private Label emailerrorlbl = new Label();
	private Label firstnameerrorlbl = new Label();
	private Label lastnameerrorlbl = new Label();
	public boolean flag = true;
	private Label blanklbl = new Label();
	private UserInformation userinfo;
	VerticalPanel vpBasePanel;
	protected DeckPanel deckpanel = new DeckPanel();
	
	
	public TrialAccountSetup(){
		vpBasePanel = new VerticalPanel();
		populateCatalystListBox();
		setHandlersAndStyle();
		deckpanel.add(vpBasePanel);
		deckpanel.showWidget(0);
		initWidget(deckpanel);
	}


	public void createUI(){
		vpBasePanel.clear();
		VerticalPanel vpInnerBasePanel = new VerticalPanel();
        FlexTable flex = new FlexTable();
        flex.setWidget(0, 2, blanklbl);
        lbselectcatalyst.setSelectedIndex(0);
        flex.setWidget(1, 2, lbselectcatalyst);
        
        flex.setWidget(2, 2, firstnameerrorlbl);
        flex.setText(3, 0, "Firstname");
        tbfirstname = new TextBox();
        tbfirstname.addChangeHandler(this);
        flex.setWidget(3, 2, tbfirstname);
        
        flex.setWidget(4, 2, lastnameerrorlbl);
        flex.setText(5, 0, "Lastname");
        tblastname = new TextBox();
        tblastname.addChangeHandler(this);
        flex.setWidget(5, 2, tblastname);
        
        flex.setWidget(8, 2, emailerrorlbl);
        flex.setText(9, 0, "Email");
        tbemail = new TextBox();
        tbemail.addChangeHandler(this);
        flex.setWidget(9, 2, tbemail);
        
        cbemailalert.setValue(true);
        flex.setWidget(12, 2, cbemailalert);
        flex.setWidget(15, 2, impreviewsetuptrial);
        flex.setCellSpacing(5);
        vpInnerBasePanel.add(flex);
        vpInnerBasePanel.setCellHorizontalAlignment(flex, HasHorizontalAlignment.ALIGN_CENTER);
        vpInnerBasePanel.setCellVerticalAlignment(flex, HasVerticalAlignment.ALIGN_MIDDLE);
        DOM.setStyleAttribute(vpInnerBasePanel.getElement(), "marginTop", "12px");
       	vpBasePanel.add(vpInnerBasePanel);
		vpBasePanel.setCellHorizontalAlignment(vpInnerBasePanel, HasHorizontalAlignment.ALIGN_LEFT);
		vpBasePanel.setCellVerticalAlignment(vpInnerBasePanel, HasVerticalAlignment.ALIGN_MIDDLE);
	}
	
	private void setHandlersAndStyle() {
		impreviewsetuptrial.setStylePrimaryName("clickable");
		blanklbl.setStylePrimaryName("errorLabels");
		emailerrorlbl.setStylePrimaryName("errorLabels");
		firstnameerrorlbl.setStylePrimaryName("errorLabels");
		lastnameerrorlbl.setStylePrimaryName("errorLabels");
	    impreviewsetuptrial.addClickHandler(this);
		lbselectcatalyst.addChangeHandler(this);
	}

	private void populateCatalystListBox() {
		TrialInformationServiceAsync service = (TrialInformationServiceAsync)GWT.create(TrialInformationService.class);
		AsyncCallback<HashMap<Integer,String>> callback = new AsyncCallback<HashMap<Integer,String>>(){
			@Override
			public void onFailure(Throwable arg0) {
				System.out.println("No catalyst found");
				
			}

			@Override
			public void onSuccess(HashMap<Integer,String> arg0) {
			  lbselectcatalyst.addItem("Select catalyst","0");	
			  if(arg0.size() > 0){	
				  for(Integer id: arg0.keySet()){
					  lbselectcatalyst.addItem(arg0.get(id),id.toString());
				  }
			 }
			}
		};
		service.getCatalystList(callback);
	}


	@Override
	public void onClick(ClickEvent arg0) {
		TrialAccount trialaccount = new TrialAccount();
		if(arg0.getSource() instanceof Image){
			boolean bool = validate();
			if(bool){
				trialaccount.setFirstname(tbfirstname.getText());
				trialaccount.setLastname(tblastname.getText());
				trialaccount.setUseremail(tbemail.getText());
				if(cbemailalert.getValue()){
					trialaccount.setEmailAlert(true);
				}
				else{
					trialaccount.setEmailAlert(false);
				}
				
				trialaccount.setActive(true);
				trialaccount.setNewscenterid(Integer.parseInt(lbselectcatalyst.getValue(lbselectcatalyst.getSelectedIndex())));
				trialaccount.setNewscatalystName(lbselectcatalyst.getItemText(lbselectcatalyst.getSelectedIndex()));
				trialaccount.setCreatedBy(userinfo.getUserId());
				trialaccount.setSalesExecutiveName(userinfo.getFirstname()+" "+userinfo.getLastname());
				trialaccount.setSignature(userinfo.getSignature());
						
				if(deckpanel.getWidgetCount() > 1){
					deckpanel.remove(1);
				}
				MailTemplate mail = new MailTemplate(trialaccount);
				mail.setTrialAccountSetupRef(this);
				deckpanel.add(mail);
				deckpanel.showWidget(1);
			 }
		}
		
	}

	private void refresh() {
		tbfirstname = new TextBox();
		tblastname = new TextBox();
		lbselectcatalyst.setSelectedIndex(0);
		cbemailalert.setValue(true);
		
	}
	

	private boolean validate() {
		String email = tbemail.getText();
		String firstname = tbfirstname.getText();
		String lastname = tblastname.getText();
		Validators validator = new Validators();
		
		if(email.equals("")|| firstname.equals("")||lastname.equals("")|| lbselectcatalyst.getSelectedIndex() == 0)
		{
			blanklbl.setText("");
			try{
				validator.blankfield();
				blanklbl.setText("");
				}
				catch(ValidationException e)
				{
					if(lbselectcatalyst.getSelectedIndex() ==0){
						blanklbl.setText("Please select the newscatalyst");
					}
					else{
					   blanklbl.setText(e.getDisplayMessage());
					}
					return false;
				}
		}
		return true;
	}


	@Override
	public void onChange(ChangeEvent sender) {
		Validators validator = new Validators();
		if(sender.getSource() instanceof TextBox)
		{
			TextBox textbox = (TextBox)sender.getSource();
			if(textbox.equals(tbemail))
			{
				try 
				{
					validator.emailValidator(tbemail.getText());
					emailerrorlbl.setText("");
					blanklbl.setText("");
				} 
				catch (ValidationException e)
				{
					emailerrorlbl.setText(e.getDisplayMessage());
					flag = false;
				}
			}
			if(textbox.equals(tbfirstname))
			{
				try{
					validator.firstnameValidator(tbfirstname.getText());
					firstnameerrorlbl.setText("");
					blanklbl.setText("");
				}
				catch(ValidationException e){
					firstnameerrorlbl.setText(e.getDisplayMessage());
					flag = false;
				}
			}
			if(textbox.equals(tblastname))
			{
				try{
					validator.lastnameValidator(tblastname.getText());
					lastnameerrorlbl.setText("");
					blanklbl.setText("");
				}
				catch(ValidationException e){
					lastnameerrorlbl.setText(e.getDisplayMessage());
					flag = false;
				}
			}
			
	    }
		if(sender.getSource() instanceof ListBox){
			ListBox lb = (ListBox)sender.getSource();
			if(lb.equals(lbselectcatalyst)){
				if(lb.getSelectedIndex() == 0){
					blanklbl.setText("Please select newscatalyst");
				}
				else{
					blanklbl.setText("");
				}
			}
		}
	}

	public UserInformation getUserinfo() {
		return userinfo;
	}


	public void setUserinfo(UserInformation userinfo) {
		this.userinfo = userinfo;
	}


	public DeckPanel getDeckpanel() {
		return deckpanel;
	}


	public void setDeckpanel(DeckPanel deckpanel) {
		this.deckpanel = deckpanel;
	}

}
