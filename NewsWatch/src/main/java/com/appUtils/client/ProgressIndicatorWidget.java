package com.appUtils.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ProgressIndicatorWidget extends Composite{
	
	private Label lblmesg = new Label();
	HorizontalPanel hpbase = new HorizontalPanel();
	
	public ProgressIndicatorWidget() {
		lblmesg.setText("Loading...");
		Image.prefetch("images/circle_loader.gif");
		Image improgress = new Image("images/circle_loader.gif");
		hpbase.add(improgress);
		hpbase.add(lblmesg);
		hpbase.setSpacing(5);
		initWidget(hpbase);
	}
	
	public ProgressIndicatorWidget(Image imloader, String mesg, int space, String stylename) {
		lblmesg.setText(mesg);
		Image improgress = imloader;
		DOM.setStyleAttribute(improgress.getElement(), "border", "0px");
		hpbase.add(improgress);
		hpbase.add(lblmesg);
		hpbase.setCellVerticalAlignment(lblmesg, HasVerticalAlignment.ALIGN_MIDDLE);
		hpbase.setSpacing(space);
		if (stylename != null) {
			hpbase.addStyleName(stylename);
		}
		initWidget(hpbase);
	}
	
	public ProgressIndicatorWidget(boolean hidden) {
		lblmesg.setText("Loading...");
		Image.prefetch("images/circle_loader.gif");
		Image improgress = new Image("images/circle_loader.gif");
		hpbase.add(improgress);
		hpbase.add(lblmesg);
		hpbase.setSpacing(5);
		initWidget(hpbase);
		if(hidden)
			disable();
	}
	
	public void setLoadingMessage(String msg) {
		lblmesg.setText(msg);
	}
	
	public void disable(){
		setVisible(false);
	}
	
	public void enable(){
		setVisible(true);
	}
}
