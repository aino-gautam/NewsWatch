package com.appUtils.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class Header extends Composite {

	private HorizontalPanel upperHeaderBar = new HorizontalPanel();
	public HorizontalPanel lowerHeaderBar = new HorizontalPanel();
	private VerticalPanel headerBar = new VerticalPanel();
	private Label welcomeLabel = new Label("Welcome to MarketScape NewsCatalyst");
	private HorizontalPanel lowerPanel = new HorizontalPanel();
	
	public Header()
	{
		upperHeaderBar.setStylePrimaryName("upperHeaderBar");
		lowerHeaderBar.setStylePrimaryName("lowerHeaderBar");
		
		upperHeaderBar.add(welcomeLabel);
		welcomeLabel.setStylePrimaryName("welcomeLabel");
		upperHeaderBar.setCellHorizontalAlignment(welcomeLabel, HasHorizontalAlignment.ALIGN_LEFT);
		headerBar.add(upperHeaderBar);
		lowerPanel.setSpacing(5);
		lowerHeaderBar.add(lowerPanel);
		lowerHeaderBar.setCellHorizontalAlignment(lowerPanel, HasHorizontalAlignment.ALIGN_RIGHT);
		headerBar.add(lowerHeaderBar);
		
		headerBar.setStylePrimaryName("headerBar");
		headerBar.setSpacing(0);
		headerBar.setCellVerticalAlignment(upperHeaderBar, HasVerticalAlignment.ALIGN_TOP);
		headerBar.setCellVerticalAlignment(lowerHeaderBar, HasVerticalAlignment.ALIGN_TOP);
		initWidget(headerBar);
	}
	
	public void addLowerHeaderComponent(Widget component){
		lowerPanel.add(component);
		lowerPanel.setCellVerticalAlignment(component, HasVerticalAlignment.ALIGN_MIDDLE);
		component.setStylePrimaryName("lowerHeaderComponent");
		
	}
	
	

}

