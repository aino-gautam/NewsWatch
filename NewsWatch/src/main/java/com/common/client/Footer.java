package com.common.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Footer extends Composite {
	
	private VerticalPanel vpFooter = new VerticalPanel();
//	private HorizontalPanel hpFooterLabel = new HorizontalPanel();
	//private Label footerLabel = new Label("(c) MarketScape™ A/S 2010");
	
	public Footer(){
	/*	footerLabel.setStylePrimaryName("footerlabelStyle");
		hpFooterLabel.add(footerLabel);*/
		vpFooter.add(new HTML("<hr style=\"color:#DCDCDC; height: '1'; text-align: 'center'; width: '100%'\"> "));
		vpFooter.add(new HTML("<p style=\" font-family:Arial; color:#505050;font-size=8pt\">&copy; MarketScape™ A/S 2012. Version 2.5"));	
		vpFooter.setWidth("100%");
		initWidget(vpFooter);

	}

}
