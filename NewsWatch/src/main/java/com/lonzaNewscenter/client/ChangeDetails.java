package com.lonzaNewscenter.client;

import java.util.ArrayList;
import java.util.Iterator;

import com.common.client.ChangeUserDetails;
import com.common.client.HeaderLonza;
import com.common.client.PopUpForForgotPassword;
import com.common.client.SessionInformation;
import com.common.client.ValidationException;
import com.common.client.Validators;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.login.client.UserInformation;

public class ChangeDetails extends Composite implements ClickHandler, ChangeHandler, MouseOverHandler,MouseOutHandler
{
	private HeaderLonza header = new HeaderLonza(false);
	private Label homelbl = new Label("home");
	private VerticalPanel vpanelcontainer = new VerticalPanel();
	private FlexTable flex = new FlexTable();
	private PasswordTextBox oldpassword = new PasswordTextBox();
	private PasswordTextBox newpassword = new PasswordTextBox();
	private PasswordTextBox confirmpassword = new PasswordTextBox();
	private DecoratorPanel decorator = new DecoratorPanel();
	private HorizontalPanel hpanelflex = new HorizontalPanel();
	private Button okbutton = new Button("change");
	private Label oldpasslbl = new Label();
	private Label newpasslbl = new Label();
	private Label repasslbl = new Label();
	private ArrayList arrayuserinfo = new ArrayList();
	private Label selectionlbl = new Label("Please enter details");
	private VerticalPanel vpanelflexandmsg = new VerticalPanel();
	private HorizontalPanel hpanelhomelbl = new HorizontalPanel();
	private HorizontalPanel hpanelheader = new HorizontalPanel();
	public boolean flag = true;
	
	public ChangeDetails()
	{
		int clientwidth = Window.getClientWidth();
		oldpasslbl.setStylePrimaryName("errorLabels");
		newpasslbl.setStylePrimaryName("errorLabels");
		repasslbl.setStylePrimaryName("errorLabels");
		okbutton.addClickHandler(this);
		oldpassword.addChangeHandler(this);
		newpassword.addChangeHandler(this);
		confirmpassword.addChangeHandler(this);
		homelbl.addClickHandler(this);
//		homelbl.addMouseListener(this);
		homelbl.addMouseOverHandler(this);
		homelbl.addMouseOutHandler(this);
		homelbl.setStylePrimaryName("labelLogoutLonzaNews");
		
		flex.setWidget(0, 2, oldpasslbl);
		flex.setWidget(1, 0, createLabel("Old Password"));
		flex.setWidget(1, 2, oldpassword);
		
		flex.setWidget(2, 2, newpasslbl);
		flex.setWidget(3, 0, createLabel("New Password"));
		flex.setWidget(3, 2, newpassword);
		
		flex.setWidget(5, 2, repasslbl);
		flex.setWidget(6, 0, createLabel("Confirm New Password"));
		flex.setWidget(6, 2, confirmpassword);
		flex.setWidget(9, 1, okbutton);
		if(clientwidth >1400)
		{
			homelbl.addStyleName("homelbl1440");//.setStylePrimaryName("homelbl1400");
			//hpanelflex.setStyleName("hpanelchangedetails1400");//.setStylePrimaryName("hpanelchangedetails1400");
		}
		
		else if(clientwidth >1200)
		{
			homelbl.addStyleName("homelbl1280");//.setStylePrimaryName("homelbl1200");
			//hpanelflex.setStyleName("hpanelchangedetails1200");//.setStylePrimaryName("hpanelchangedetails1200");
		}
		else if(clientwidth >1000)
		{
			homelbl.addStyleName("homelbl1024");//.setStylePrimaryName("homelbl1020");
			//hpanelflex.setStyleName("hpanelchangedetails1020");//.setStylePrimaryName("hpanelchangedetails1020");
		}
		
		selectionlbl.setStylePrimaryName("msglbl");
		
		hpanelhomelbl.add(homelbl);
		hpanelhomelbl.setWidth("100%");
		vpanelflexandmsg.add(selectionlbl);
		vpanelflexandmsg.add(flex);
		vpanelflexandmsg.setStylePrimaryName("vpanelflexandmsg");
		decorator.add(vpanelflexandmsg);
		
		hpanelflex.add(decorator);
		hpanelflex.setCellHorizontalAlignment(decorator, HasHorizontalAlignment.ALIGN_CENTER);
		hpanelflex.setStylePrimaryName("hpanelflex");
		//vpanelcontainer.setWidth(Window.getClientWidth()+"px");
		vpanelcontainer.setWidth("100%");
		//vpanelcontainer.setStylePrimaryName("vpanelcontainerchangedetail");
		hpanelheader.add(header);
		hpanelheader.setWidth(Window.getClientWidth()+"px");
		//vpanelcontainer.setBorderWidth(1);//.setSpacing(10);
		vpanelcontainer.add(header);
		vpanelcontainer.add(hpanelhomelbl);
		//vpanelcontainer.setCellHorizontalAlignment(hpanelhomelbl, HasHorizontalAlignment.ALIGN_RIGHT);
		vpanelcontainer.add(hpanelflex);
		vpanelcontainer.setCellHorizontalAlignment(hpanelflex, HasHorizontalAlignment.ALIGN_CENTER);
		
		
		/*Window.addWindowResizeListener(new WindowResizeListener() {
			public void onWindowResized(int width, int height) {
				vpanelcontainer.setWidth(width + "px");
			}
			});*/
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				// TODO Auto-generated method stub
				int width=event.getWidth();
				vpanelcontainer.setWidth(width+"px");
			}
		});
		
		initWidget(vpanelcontainer);
		
	}
	public void onModuleLoad() {
		RootPanel.get().clear();
		RootPanel.get().add(this);
	}
	public Label createLabel(String text)
	{
		Label label = new Label(text);
		label.setStylePrimaryName("Label");
		return label;
	}
	
	/*public void onClick(Widget sender) 
	{
		if(sender instanceof Button)
		{
			Button button = (Button)sender;
			if(button.getText().equals("change"))
			{
				boolean bool = validation();
				if(bool)
				{
					getUserInformation();
					//checkvaliduser();
				}
			}
		}
		if(sender instanceof Label)
		{
			Label label = (Label)sender;
			if(label.getText().equals("home"))
			{
				LonzaMainCollection lonza = new LonzaMainCollection();
				lonza.onModuleLoad();
			}
		}
		
		
	}*/
	public void getUserInformation()
	{
		ServiceProviderAsync service = (ServiceProviderAsync)GWT.create(ServiceProvider.class);
		ServiceDefTarget endpoint = (ServiceDefTarget) service;
		String moduleRelativeURL = GWT.getModuleBaseURL() + "lonzaNewsCenter";
		endpoint.setServiceEntryPoint(moduleRelativeURL);

		AsyncCallback callback = new AsyncCallback(){
		public void onFailure(Throwable caught) {
			caught.printStackTrace();	
		}

		public void onSuccess(Object result) {
			try{
				arrayuserinfo = (ArrayList)result;
				checkvaliduser();
			}catch(Exception ex)
			{
				ex.printStackTrace();
				System.out.println("problem in removeFromSession()");
			}

		}
	};
	service.getUserInformation(callback);
	}
	public void checkvaliduser()
	{
		Iterator iter = arrayuserinfo.iterator();
		while(iter.hasNext())
		{
			UserInformation userinfo = (UserInformation)iter.next();
			String password = userinfo.getPassword();
			String email = userinfo.getEmail();
			String passwordtext = oldpassword.getText();
			String newpass = newpassword.getText();
			if(password.equals(passwordtext))
			{
				ChangeUserDetails changepass = new ChangeUserDetails(newpass,email);
				changepass.changeuserdetails(newpass, email);
				oldpasslbl.setText("");
				cleartextfields();
			}
			else
			{
				/*PopUpForForgotPassword popup = new PopUpForForgotPassword("Your old password do not match");
				popup.setPopupPosition(600, 300);
				popup.show();*/
				oldpasslbl.setText("Your old password does not match");
				cleartextfields();
			}
			
		}
	}
	public void cleartextfields()
	{
		oldpassword.setText("");
		newpassword.setText("");
		confirmpassword.setText("");
	}
	public boolean validation()
	{
		Validators validator = new Validators();
		String newpasswordstr = newpassword.getText();
		String repasswordstr = confirmpassword.getText();
		if((oldpassword.getText().equals(""))||(newpassword.getText().equals(""))|| (confirmpassword.getText().equals("")))
		{
			try{
			validator.blankfield();
			oldpasslbl.setText("");
			
			}
			catch(ValidationException e)
			{
				oldpasslbl.setText(e.getDisplayMessage());
				return false;
			}
			
		}
		else if(!newpasswordstr.equals(repasswordstr))
		{
			try
			{
				validator.wrongpassword();
				repasslbl.setText("");
			}
			catch(ValidationException e)
			{
				repasslbl.setText(e.getDisplayMessage());
				cleartextfields();
				return false;
			}
		}
		return true;
	}
	
	/*public void onChange(Widget sender)
	{
		Validators validator = new Validators();
		if(sender instanceof PasswordTextBox)
		{
			PasswordTextBox password = (PasswordTextBox)sender;
				if(password.equals(oldpassword))
				{
					try 
					{
						validator.passwordValidator(password.getText());//.firstnameValidator(((TextBox)sender).getText());
						oldpasslbl.setText("");
					} 
					catch (ValidationException e)
					{
						oldpasslbl.setText(e.getDisplayMessage());
						flag = false;
					}
					
				}
				else if(password.equals(newpassword))
				{
					try 
					{
						validator.passwordValidator(password.getText());//.firstnameValidator(((TextBox)sender).getText());
						newpasslbl.setText("");
					} 
					catch (ValidationException e)
					{
						newpasslbl.setText(e.getDisplayMessage());
						flag = false;
					}
					
				}
				else if (password.equals(confirmpassword))
				{
					try 
					{
						validator.passwordValidator(password.getText());//.firstnameValidator(((TextBox)sender).getText());
						repasslbl.setText("");
						//onClick(okbutton);
					} 
					catch (ValidationException e)
					{
						repasslbl.setText(e.getDisplayMessage());
						flag = false;
					}
					
				}
		}
		
		
	}
	*/
	public void onMouseDown(Widget arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	/*
	public void onMouseEnter(Widget sender) {
		if(sender instanceof Label){
			Label label = (Label)sender;
			//label.removeStyleName("labelLogoutLonzaNews");
			label.addStyleName("labelHover");
		}
		
	}
	*/
	/*public void onMouseLeave(Widget arg0) {
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			label.removeStyleName("labelHover");
			//label.setStylePrimaryName("homelbl1020");//.removeStyleName("homelblmouse");
			//label.setStylePrimaryName("labelLogoutLonzaNews");
		}
		
	}*/
	
	public void onMouseMove(Widget arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public void onMouseUp(Widget arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Widget sender=(Widget) event.getSource();
		if(sender instanceof Button)
		{
			Button button = (Button)sender;
			if(button.getText().equals("change"))
			{
				boolean bool = validation();
				if(bool)
				{
					getUserInformation();
					//checkvaliduser();
				}
			}
		}
		if(sender instanceof Label)
		{
			Label label = (Label)sender;
			if(label.getText().equals("home"))
			{
				LonzaMainCollection lonza = new LonzaMainCollection();
				lonza.onModuleLoad();
			}
		}
		
	}
	@Override
	public void onChange(ChangeEvent event) {
		// TODO Auto-generated method stub
		 Widget sender=(Widget) event.getSource();
		Validators validator = new Validators();
		if(sender instanceof PasswordTextBox)
		{
			PasswordTextBox password = (PasswordTextBox)sender;
				if(password.equals(oldpassword))
				{
					try 
					{
						validator.passwordValidator(password.getText());//.firstnameValidator(((TextBox)sender).getText());
						oldpasslbl.setText("");
					} 
					catch (ValidationException e)
					{
						oldpasslbl.setText(e.getDisplayMessage());
						flag = false;
					}
					
				}
				else if(password.equals(newpassword))
				{
					try 
					{
						validator.passwordValidator(password.getText());//.firstnameValidator(((TextBox)sender).getText());
						newpasslbl.setText("");
					} 
					catch (ValidationException e)
					{
						newpasslbl.setText(e.getDisplayMessage());
						flag = false;
					}
					
				}
				else if (password.equals(confirmpassword))
				{
					try 
					{
						validator.passwordValidator(password.getText());//.firstnameValidator(((TextBox)sender).getText());
						repasslbl.setText("");
						//onClick(okbutton);
					} 
					catch (ValidationException e)
					{
						repasslbl.setText(e.getDisplayMessage());
						flag = false;
					}
					
				}
		}
	}
	@Override
	public void onMouseOut(MouseOutEvent event) {
		// TODO Auto-generated method stub
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			label.removeStyleName("labelHover");
			//label.setStylePrimaryName("homelbl1020");//.removeStyleName("homelblmouse");
			//label.setStylePrimaryName("labelLogoutLonzaNews");
		}
	}
	@Override
	public void onMouseOver(MouseOverEvent event) {
		// TODO Auto-generated method stub
		Widget sender=(Widget)event.getSource();
		if(sender instanceof Label){
			Label label = (Label)sender;
			//label.removeStyleName("labelLogoutLonzaNews");
			label.addStyleName("labelHover");
		}
	}
	
	
}
