package com.lighthouse.main.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * 
 * @author prachi@ensarm.com
 * This class is displayed on the click of report dead link. 
 */
public class ReportDeadLinkPopup extends PopupPanel implements ClickHandler {

	
	private VerticalPanel vpBasePanel = new VerticalPanel();
	private HorizontalPanel  buttonpanel = new HorizontalPanel();
	private FlexTable flex = new FlexTable();
	private TextBox tbtitle = new TextBox();
	private TextBox tblink  = new TextBox();
	private TextArea tamessage = new TextArea();
	private FormPanel form = new FormPanel();
	private Button submit = new Button("Submit");
	//private Button cancelBtn = new Button("Cancel");
	private String title = null;
	private String link  = null;
	private String description = null;
	private String email = null;
	private Hidden hiddenemail = new Hidden();
	private Hidden hiddenTitle = new Hidden();
	private Hidden hiddenLink = new Hidden();
	private Hidden hiddenMessage = new Hidden();
	private Image imgLoader	= new Image("images/circle_loader.gif");
	private Image closeImage = new Image("images/close-icon.jpg");
	
	private Label lblLoading = new Label("Email send successfully");
	HorizontalPanel hpOuter = new HorizontalPanel();
	
	public ReportDeadLinkPopup(String url, String title, String userEmail) {
	
		this.setAutoHideEnabled(true);
		tamessage.setVisibleLines(10);
		tamessage.setWidth("99%");
		tamessage.setText("The link for this newsItem seems to be broken...");
				
		tbtitle.setSize("99%", "28px");
		tbtitle.setText(title);
		tbtitle.setStyleName("disabled");
		tbtitle.setEnabled(false);
		
		tblink.setSize("99%", "28px");
		tblink.setText(url);
		tblink.setStyleName("disabled");
		tblink.setEnabled(false);
		
		DOM.setStyleAttribute(tamessage.getElement(), "fontSize", "11pt");
		DOM.setStyleAttribute(tbtitle.getElement(),  "fontSize", "11pt");
		DOM.setStyleAttribute(tblink.getElement(),  "fontSize", "11pt");
		
		hpOuter.add(imgLoader);
		hpOuter.add(lblLoading);
		
		/*hpOuter.add(closeImage);
		closeImage.addStyleName("clickable");
		closeImage.addClickHandler(this);*/
		
		hpOuter.setSize("100%","35px");
		hpOuter.setCellHorizontalAlignment(closeImage,HasHorizontalAlignment.ALIGN_RIGHT);
		hpOuter.setCellHorizontalAlignment(imgLoader,HasHorizontalAlignment.ALIGN_CENTER);
		hpOuter.setCellVerticalAlignment(imgLoader, HasVerticalAlignment.ALIGN_MIDDLE);
		
		
		hpOuter.setCellHorizontalAlignment(lblLoading,HasHorizontalAlignment.ALIGN_CENTER);
		hpOuter.setCellVerticalAlignment(lblLoading, HasVerticalAlignment.ALIGN_MIDDLE);
		imgLoader.setVisible(false);
		//imgLoader.setStylePrimaryName("loaderImage");
		
		lblLoading.setVisible(false);
		//lblLoading.setStylePrimaryName("loaderLabel");
		vpBasePanel.add(hpOuter);
		//DOM.setStyleAttribute(vpBasePanel.getElement(), "backgroundColor", "#d0e4f6");
		
		submit.addClickHandler(this);
		//cancelBtn.addClickHandler(this);
		
		flex.setTitle("Report a dead link");
		flex.setText(0, 0, "Report a dead link");
		flex.setText(2, 0,"Title");
		flex.setText(4,0,"Link");
		flex.setText(6, 0, "Message");
		
		flex.setWidget(2, 2, tbtitle);
		flex.setWidget(4, 2 ,tblink);
		flex.setWidget(6, 2, tamessage);
		flex.setWidth("100%");
		flex.getFlexCellFormatter().setColSpan(0, 0, 3);
		flex.getFlexCellFormatter().setVisible(0, 0, false);
		flex.getCellFormatter().setWidth(2, 0, "15px");
		flex.getCellFormatter().setWidth(4, 0, "15px");
		flex.getCellFormatter().setWidth(6, 0, "15px");
		
		flex.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(2, 2, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(4, 2, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		flex.getCellFormatter().setAlignment(6, 2, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);
		DOM.setStyleAttribute(flex.getElement(), "fontSize", "10pt");
		
		
		buttonpanel.add(submit);
		//buttonpanel.add(cancelBtn);
		buttonpanel.setHeight("35px");
		buttonpanel.setSpacing(5);
		buttonpanel.setCellVerticalAlignment(submit, HasVerticalAlignment.ALIGN_MIDDLE);
		//buttonpanel.setCellVerticalAlignment(cancelBtn, HasVerticalAlignment.ALIGN_MIDDLE);
		
		vpBasePanel.add(flex);
		vpBasePanel.add(buttonpanel);
		vpBasePanel.add(form);
		vpBasePanel.setCellHorizontalAlignment(flex, HasHorizontalAlignment.ALIGN_LEFT);
		vpBasePanel.setCellHorizontalAlignment(buttonpanel, HasHorizontalAlignment.ALIGN_CENTER);
		vpBasePanel.setWidth("500px");
		vpBasePanel.setStylePrimaryName("curvedPopup");
		add(vpBasePanel);
		setWidth("900px");
		setAnimationEnabled(true);
		setModal(true);
		
		VerticalPanel vpPanel = new VerticalPanel();
		hiddenemail.setName("email");
		hiddenTitle.setName("title");
		hiddenLink.setName("link");
		hiddenMessage.setName("description");
		
		vpPanel.add(hiddenemail);
		vpPanel.add(hiddenTitle);
		vpPanel.add(hiddenLink);
		vpPanel.add(hiddenMessage);
		
		form.add(vpPanel);
		form.setSize("100%", "100%");
		setStylePrimaryName("searchPopup");
		
		
	}

       @Override
		public void onClick(ClickEvent event) {
    	   if (event.getSource() instanceof Image) {
            	hide();
   		   }
    	   if(event.getSource() instanceof Button){
			Button btn = (Button)event.getSource();
				if(btn == submit){
					title = tbtitle.getText();
					link = tblink.getText();
					description = tamessage.getText();
					
				/*	UserInformation user = LhMain.getUserInformation();
					String email = user.getEmail();*/
					/*hiddenemail.setDefaultValue(email);
					hiddenLink.setDefaultValue(link);
					hiddenTitle.setDefaultValue(title);
					hiddenMessage.setDefaultValue(description);*/
					
					
					
					String url =  GWT.getHostPageBaseURL()+"reportDeadLink?title="+title+"&link="+link+"&description="+description+"";  // url to send all parameters
					RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
					try{
						// add code to submit the form
						/*form.setEncoding(FormPanel.ENCODING_MULTIPART);
						form.setMethod(FormPanel.METHOD_POST);
						form.setAction(url);
						imgLoader.setVisible(true);
						lblLoading.setVisible(true);
						
						form.submit();
						form.addSubmitCompleteHandler(new  SubmitCompleteHandler(){
							

							@Override
							public void onSubmitComplete(SubmitCompleteEvent event) {
								imgLoader.setVisible(false);
								hide();
							}

											
						});*/
						 Request request = requestBuilder.sendRequest(null,new RequestCallback() {

							@Override
							public void onResponseReceived(Request request,	Response response) {
								imgLoader.setVisible(true);
								lblLoading.setVisible(true);
								hide();
								
							}

							@Override
							public void onError(Request request,
									Throwable exception) {
								// TODO Auto-generated method stub
								
							}
                
				         						
					 	
					});
				} catch(Exception e){
						e.printStackTrace();
					}
				}/*else if(btn == cancelBtn)
					     hide();*/
				else
					flex.getFlexCellFormatter().setVisible(0, 0, true);
				   
			
		
		
	         
			}
		}
}
