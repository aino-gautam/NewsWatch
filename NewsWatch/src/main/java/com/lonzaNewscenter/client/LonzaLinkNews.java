package com.lonzaNewscenter.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.WindowResizeListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LonzaLinkNews extends Composite 
{
	//private Frame frame = new Frame("http://myportal.cyberwatcher.com/v4/agents/readonly/viewxml.aspx?userid=26698&index=0&templateID=-1&an=1&ke=1&sp=0&re=0&fo=&bg=white&al=e8f8ff&ad=0&fi=1&so=1&ex=1&da=1&to=1&bo=1&df=dd%5c%2fMM+HH%3amm&co=&ar=1&se=1&ds=0&hc=f1ceaa&la=en&ma=1&ab=0&ext=1&dl=0&abs=1&mn=0&wi=0&bold=0&srep=0");
	private Frame frame = new Frame("http://myportal.cyberwatcher.com/v4/lonza/readonly/viewxml.aspx?userid=30164&index=0&templateID=-1&an=1&ke=1&sp=0&re=0&fo=&bg=white&al=f4f4f4&ad=0&fi=1&so=1&ex=1&da=1&to=0&bo=1&df=dd%5c%2fMM+HH%3amm&co=&ar=1&se=0&ds=0&hc=f1ceaa&la=en&ma=1&ab=0&ext=1&dl=0&abs=1&mn=0&wi=0&bold=0&srep=0");
	private VerticalPanel panel = new VerticalPanel();
	private Label latestNewsLabel = new Label("Latest News");
	private HorizontalPanel hpanelLatestNewsLbl = new HorizontalPanel();
	String frameWidthString;
	public LonzaLinkNews()
	{
		latestNewsLabel.setStylePrimaryName("welcomLbl");
		hpanelLatestNewsLbl.setStylePrimaryName("hpanelforWelcomeLbl");
		hpanelLatestNewsLbl.add(latestNewsLabel);
		
		int clientwidth = Window.getClientWidth();
		int clientHeight = Window.getClientHeight();
		//int frameWidth = (clientwidth/2) - 50;
		int frameWidth = clientwidth - 550;
		int frameHeight = clientHeight-100;
		String frameWidthString = String.valueOf(frameWidth)+"px";
		
		
		//frameWidthString = String.valueOf(frameWidth)+"px";
		String frameHeightString = String.valueOf(frameHeight)+"px";
		//frame.setWidth(frameWidthString);
		frame.setWidth(frameWidthString);
		frame.setHeight("500px");
		frame.setStylePrimaryName("newsFrame");
			
		//panel.setStylePrimaryName("framePanel");
		panel.add(hpanelLatestNewsLbl);
		panel.add(frame);
		//panel.setStylePrimaryName("outerWelcomePanel");
		
		Window.addWindowResizeListener(new WindowResizeListener() {
			public void onWindowResized(int width, int height) 
			{
				int frameWidth = width - 550;
				frame.setWidth(frameWidth + "px");
				//frame.setWidth(frameWidthString);
				//frame.setWidth("500px");
			}
			});
		
		initWidget(panel);
	}
}
