package com.lighthouse.report.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class ReportTitle {
	
	private HTML titleLbl;
	private String id;
	private String url;

	public ReportTitle(String title, String id, String url){

		this.titleLbl = new HTML(title);
		titleLbl.setStylePrimaryName("reportLink");
		this.id = id;
		this.url = url;
	}

	public HTML getTitleLbl() {
		return titleLbl;
	}

	public void setTitleLbl(HTML titleLbl) {
		this.titleLbl = titleLbl;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
}
