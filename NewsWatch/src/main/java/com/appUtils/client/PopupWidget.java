package com.appUtils.client;

import com.google.gwt.user.client.ui.HasAutoHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PopupWidget extends PopupPanel{
	
	private Label label;
	private Image logo;
	private VerticalPanel panel;
	private String text="This site is developed by";
	public PopupWidget(){
		createUI();
		
	}
	public void createUI() {
		try{
			setAutoHideEnabled(true);
			setAnimationEnabled(true);
			setStylePrimaryName("popupStyle");
			setPopupPosition(500,250 );
			setSize("300px", "100px");
			logo=new Image("images/ensarm.png");
			logo.setSize("100px", "50px");
			label=new Label(text);
			label.setStylePrimaryName("popupText");
			label.setAutoHorizontalAlignment(HasAutoHorizontalAlignment.ALIGN_CENTER);
			panel=new VerticalPanel();
			panel.add(label);
			panel.setSpacing(10);
			panel.add(logo);			
			panel.setCellHorizontalAlignment(logo, HasAutoHorizontalAlignment.ALIGN_CENTER);
			add(panel);
			show();		
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void setMessage(String msg){
		label.setText(msg);
	}

}
