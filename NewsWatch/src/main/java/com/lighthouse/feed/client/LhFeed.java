package com.lighthouse.feed.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvp4g.client.Mvp4gModule;
/**
 * Entry point class for feed
 * @author kiran@ensarm.com
 *
 */
public class LhFeed implements EntryPoint {

	@Override
	public void onModuleLoad() {
		// retrieve the start view (which is the view of the start presenter) and add it to the RootPanel.
		Mvp4gModule module = (Mvp4gModule)GWT.create( Mvp4gModule.class );
		// for creating the views,presenters and history converters instances in the application 
		//and create the event bus and injects the views and events bus and services in the presenters 
		module.createAndStartModule();
		RootPanel.get().add( (Widget)module.getStartView(), 20,10 );
	}
}
