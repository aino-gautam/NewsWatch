package com.common.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.lonzaNewscenter.client.LonzaMainCollection;


public class HeaderLonza extends Composite implements ClickHandler
{
	private Image mciImage = new Image("images/Lonza-mci.jpg");
	private Image lonzaImage = new Image("images/lonzaLogoLeft.gif");

	private HorizontalPanel hPanel = new HorizontalPanel();
	
	public HeaderLonza(boolean bool)
	{
		
		int clientWidth = Window.getClientWidth();
		
		if(clientWidth > 1400){
			mciImage.setStylePrimaryName("mciImage1440");
			lonzaImage.setStylePrimaryName("LonzaImage1440");
		}
		else if(clientWidth > 1200){
			mciImage.setStylePrimaryName("mciImage1280");
			lonzaImage.setStylePrimaryName("LonzaImage1280");
		}
		else if(clientWidth > 1000){
			mciImage.setStylePrimaryName("mciImage1024");
			lonzaImage.setStylePrimaryName("LonzaImage1024");
		}
		//mciImage.setStylePrimaryName("imageMciLogo");
		//lonzaImage.setStylePrimaryName("imageLonzaLogo");
		
		if (bool)
//			mciImage.addClickListener(this);
			mciImage.addClickHandler(this);
		
		hPanel.setStylePrimaryName("hpanelForIMages");
		
		hPanel.add(lonzaImage);
		hPanel.add(mciImage);
		

		initWidget(hPanel);
		
	}
	
	/*public void onClick(Widget sender) 
	{
		if(sender instanceof Image)
		{
			Image image = (Image)sender;

			if ( image == mciImage )
			{

			// String url = GWT.getModuleBaseURL()+"Lonza-mci.jpg";
			//if(image.getUrl().equals(url))
			LonzaMainCollection main = new LonzaMainCollection();
			main.onModuleLoad();
				LonzaNewsCenterPage lonza = new LonzaNewsCenterPage();
				lonza.onModuleLoad();
				//Window.open("http://localhost:8888/com.lonzaNewscenter.lonzaNewscenter/lonzaNewscenter.html", "_self", "");	
			}
		}
		
	}
*/
	@Override
	public void onClick(ClickEvent event) {
		Widget sender=(Widget) event.getSource();

		if(sender instanceof Image)
		{
			Image image = (Image)sender;

			if ( image == mciImage )
			{

			// String url = GWT.getModuleBaseURL()+"Lonza-mci.jpg";
			//if(image.getUrl().equals(url))
			LonzaMainCollection main = new LonzaMainCollection();
			main.onModuleLoad();
				/*LonzaNewsCenterPage lonza = new LonzaNewsCenterPage();
				lonza.onModuleLoad();*/
				//Window.open("http://localhost:8888/com.lonzaNewscenter.lonzaNewscenter/lonzaNewscenter.html", "_self", "");	
			}
		}
	}
}
