package com.lighthouse.report.client;



import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.user.client.ui.RootPanel;

import com.lighthouse.report.client.ui.ReportsWidget;

public class ReportsEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
	//	ReportsWidget reportwidget = new ReportsWidget();
	/*	try{
		String reportIdStr = Window.Location.getParameter("reportid"); 
		
		int reportId = Integer.parseInt(reportIdStr);
		
		ReportDisplay reportDisplay = new ReportDisplay(reportId);
		RootPanel.get().clear();
	    RootPanel.get().add(reportDisplay);
		}
		catch (Exception e) {
			int reportId = 14998;
			
			ReportDisplay reportDisplay = new ReportDisplay(reportId);
			RootPanel.get().clear();
		    RootPanel.get().add(reportDisplay);
			}
	*/
		/*ReportsWidget reportwidget = new ReportsWidget();
		RootPanel.get().clear();
	    RootPanel.get().add(reportwidget);*/
	}

}
