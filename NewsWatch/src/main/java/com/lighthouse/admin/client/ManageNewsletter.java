package com.lighthouse.admin.client;



import java.util.ArrayList;
import java.util.List;


import com.common.client.PopUpForForgotPassword;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.lighthouse.newsletter.client.UserNewsletterAlertConfigService;
import com.lighthouse.newsletter.client.UserNewsletterAlertConfigServiceAsync;

/**
 * Creates the manage newsLetterTemplate
 * @author pallavi & prachi
 *
 */
public class ManageNewsletter extends Composite implements ClickHandler,SubmitCompleteHandler{

	
	private TextArea txtHeader;
	private TextArea txtFooter;
	
	private TextBox txtTime=new TextBox();
	private Label lbltimeString=new Label("HH:MM:SS");
	private HorizontalPanel manualhp;
	private HorizontalPanel automatedhp;
	private RadioButton manualrb;
	private RadioButton automatedrb;
	private int newscenterid,userid;
	private Label lblheading = new Label("Please select any of the following: ");
	private ListBox lbOption;
	private VerticalPanel vpEditNewsletterTemplate;
	private VerticalPanel vpEditNewsletterDelivery;
	private Button btnsave = new Button("Save");
	private Button btnsendnow = new Button("SendNow");
	String str="";
	VerticalPanel vpBase;
	private DeckPanel deck;
	
	private static final String EDIT_NEWSLETTER_DELIVERY = "Edit Newsletter delivery";
	
	public ManageNewsletter(int newscenterid,int userid){
		this.newscenterid = newscenterid;
		this.userid = userid;
		vpBase = new VerticalPanel();
		initWidget(vpBase);
	}
	
	public void createUI(){
		vpBase.clear();
		deck = new DeckPanel();
		lbOption = new ListBox();
		lbOption.addItem(EDIT_NEWSLETTER_DELIVERY);
		lbOption.addChangeHandler(new ChangeHandler(){

			@Override
			public void onChange(ChangeEvent arg0) {
				if(lbOption.getItemText(lbOption.getSelectedIndex()).equalsIgnoreCase(EDIT_NEWSLETTER_DELIVERY))
					deck.showWidget(0);
			}
			
		});
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(lblheading);
		hp.add(lbOption);
		hp.setSpacing(5);
		lblheading.setStylePrimaryName("labelSelection");
	    
		vpBase.add(hp);
		vpBase.add(deck);
		txtTime.setEnabled(false);
		deck.add(creatEditNewsletterDeliveryWidget());
		deck.showWidget(0);
		vpBase.setSpacing(7);
		vpBase.setSize("100%", "100%");
		btnsave.addClickHandler(this);
		btnsendnow.addClickHandler(this);
		btnsendnow.setWidth("100%");
	}
	
	public Label createLabel(String text){
		Label label = new Label(text);
		label.setStylePrimaryName("labelTitle");
		return label;
	}

	/**
	 * Creates the template for edit newsLetter
	 * @return
	 */
	private VerticalPanel createEditNewsletterTemplate(){
		vpEditNewsletterTemplate = new VerticalPanel();
		HorizontalPanel hpNewsletterTemplateHeader = new HorizontalPanel();
		HorizontalPanel hpNewsletterTemplateFooter = new HorizontalPanel();
		txtHeader = new TextArea();
		txtFooter = new TextArea();
		txtHeader.setStylePrimaryName("textAreaEmailTemplate");
		txtFooter.setStylePrimaryName("textAreaEmailTemplate");
		txtHeader.setWidth("90%");
		txtFooter.setWidth("90%");
		txtHeader.setVisibleLines(25);
		txtFooter.setVisibleLines(25);
		
		vpEditNewsletterTemplate.setSize("100%", "100%");
		vpEditNewsletterTemplate.add(createLabel("Please enter header html"));
		hpNewsletterTemplateHeader.add(txtHeader);
		vpEditNewsletterTemplate.add(hpNewsletterTemplateHeader);
		
		vpEditNewsletterTemplate.add(createLabel("Please enter footer html"));
		hpNewsletterTemplateFooter.add(txtFooter);
		vpEditNewsletterTemplate.add(hpNewsletterTemplateFooter);
		vpEditNewsletterTemplate.setSpacing(20);
		
		Button btnsave = new Button("Save");
		btnsave.setStylePrimaryName("buttonOkSaveEmail");
		vpEditNewsletterTemplate.add(btnsave);
		getNewsletterTemplate();
		btnsave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				String s ="";
				String header = txtHeader.getText();
				String footer = txtFooter.getText();
				
				if((header.isEmpty()) && (footer.isEmpty())){
					 PopUpForForgotPassword popup = new PopUpForForgotPassword("Please enter html for NewsLetter template header and footer");
					 popup.setAnimationEnabled(true);
					 popup.center();
				  }
				else
					saveNewsletterTemplate();
			}

		});
		return vpEditNewsletterTemplate;
		
	}
	
	/**
	 * Creates the template for Newsletterdelivery  
	 * @return
	 */
	private VerticalPanel creatEditNewsletterDeliveryWidget(){
		vpEditNewsletterDelivery = new VerticalPanel();
		manualhp =new HorizontalPanel();
		automatedhp =new HorizontalPanel();
		manualrb=new RadioButton("group", "Manual  ");
		automatedrb=new RadioButton("group","Automated");
		manualrb.setValue(true);
		manualhp.add(manualrb);
		manualhp.add(btnsendnow);
		automatedhp.add(automatedrb);
		automatedhp.add(txtTime);
		automatedhp.add(lbltimeString);
		automatedhp.add(btnsave);
		btnsave.setVisible(false);
		
		DOM.setStyleAttribute(lbltimeString.getElement(), "fontSize", "9pt");
		DOM.setStyleAttribute(lbltimeString.getElement(), "marginTop", "5px");
		DOM.setStyleAttribute(btnsendnow.getElement(), "marginLeft", "5.5px");
		
		manualhp.setSpacing(7);
		automatedhp.setSpacing(7);
		vpEditNewsletterDelivery.add(manualhp);
		vpEditNewsletterDelivery.add(automatedhp);
		vpEditNewsletterDelivery.setSpacing(7);
		manualrb.addClickHandler(this);
		automatedrb.addClickHandler(this);
		txtTime.addClickHandler(this);
		return vpEditNewsletterDelivery;
	}
	
	
	private void saveNewsletterTemplate(){
		UserNewsletterAlertConfigServiceAsync service = (UserNewsletterAlertConfigServiceAsync)GWT.create(UserNewsletterAlertConfigService.class);
		AsyncCallback callback = new AsyncCallback(){

			@Override
			public void onFailure(Throwable arg0) {
								
			}

			@Override
			public void onSuccess(Object arg0) {
			 PopUpForForgotPassword popup = new PopUpForForgotPassword("Newletter template saved");
			 popup.setAnimationEnabled(true);
			 popup.center();
			
			}
			
		};
		service.saveNewsletterTemplate(txtHeader.getText(),txtFooter.getText(), newscenterid, callback);
	}
	
	private void getNewsletterTemplate(){
		UserNewsletterAlertConfigServiceAsync service = (UserNewsletterAlertConfigServiceAsync)GWT.create(UserNewsletterAlertConfigService.class);
		service.getNewsletterTemplate(newscenterid, new AsyncCallback<ArrayList<String>>() {
			
			@Override
			public void onSuccess(ArrayList<String> result) {
				
				String header="",footer="";
				for(Object obj :result ){
					 header = result.get(0);
					 footer = result.get(1);
				}
				txtHeader.setText(header);
				txtFooter.setText(footer);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}
		});
	}
	
	private void saveNewsletterDelivery(){
		UserNewsletterAlertConfigServiceAsync service = (UserNewsletterAlertConfigServiceAsync)GWT.create(UserNewsletterAlertConfigService.class);
		
		AsyncCallback callback = new AsyncCallback(){

			@Override
			public void onFailure(Throwable arg0) {
				 
			}

			@Override
			public void onSuccess(Object arg0) {
				boolean val=(Boolean)arg0;
				if(val)
				{
				 PopUpForForgotPassword popup = new PopUpForForgotPassword("Newletter delivery saved");
				 popup.setAnimationEnabled(true);
				 popup.center();
				txtTime.setText("");
				btnsave.setVisible(false);
				}
				else
				{
					PopUpForForgotPassword popup = new PopUpForForgotPassword("Enter correct time format");
					 popup.setAnimationEnabled(true);
					 popup.center();
				}
				 //ManageHeader.getUserinformation().setSignature(txtSignature.getText());
			}
			
		};
		service.saveNewsletterDelivery(newscenterid, txtTime.getText(), callback);
	}

	@Override
	public void onClick(ClickEvent event) {
		if (event.getSource() == manualrb) {
			btnsave.setVisible(false);
			btnsendnow.setVisible(true);
			txtTime.setEnabled(false);
			
		} else if (event.getSource() == automatedrb) {
			btnsendnow.setVisible(false);
			txtTime.setEnabled(true);

		} else if (event.getSource() == txtTime) {
			btnsendnow.setVisible(false);
			btnsave.setVisible(true);
		} else if (event.getSource() == btnsave) {
			if (txtTime.getText().equals("")) {
				PopUpForForgotPassword popup = new PopUpForForgotPassword("Please Enter time");
				popup.setAnimationEnabled(true);
				popup.center();
			} else {
				saveNewsletterDelivery();
			}
		} else if (event.getSource() == btnsendnow) {
			
			try {
						
				FormPanel formPanel = new FormPanel();
				String url = GWT.getHostPageBaseURL() + "sendNewsLetter";
				formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
				formPanel.setMethod(FormPanel.METHOD_POST);
				Hidden hiddenId = new Hidden();
				hiddenId.setValue(newscenterid+"");
				hiddenId.setName("ncid");
				formPanel.add(hiddenId);
				vpBase.add(formPanel);
			    formPanel.setAction(url);
			    formPanel.addSubmitCompleteHandler(this);
				formPanel.submit();
				

			} catch (Exception e) {
				e.printStackTrace();
			}
						
		}
		 
		
	}

	
	@Override
	public void onSubmitComplete(SubmitCompleteEvent event) {
		PopUpForForgotPassword popUp=null;
		if(event.getResults().contains("Success")){
			popUp=new PopUpForForgotPassword("NewsLetter Sent Successfully..");
			popUp.setAnimationEnabled(true);
			popUp.center();
			popUp.show();
			
		}
		else if(event.getResults().contains("Failed")){
			popUp=new PopUpForForgotPassword("NewsLetter Sending Failed..");
			popUp.setAnimationEnabled(true);
			popUp.center();
			popUp.show();
			
		}else{
			popUp=new PopUpForForgotPassword("Unauthorised access...");
			popUp.setAnimationEnabled(true);
			popUp.center();
			popUp.show();
		}
		
				
	}
	

}
