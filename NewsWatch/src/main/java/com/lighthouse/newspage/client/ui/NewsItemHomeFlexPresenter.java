package com.lighthouse.newspage.client.ui;

import com.google.gwt.user.client.ui.FlexTable;

/**
 * 
 * @author kiran@ensarm.com This class for displaying the NewsItemHome
 * 
 */
public class NewsItemHomeFlexPresenter extends FlexTable {

	public NewsItemHomeFlexPresenter(NewsItemHomePresenter newsItemHomePresenter) {
		setWidth("85%");
		setWidget(0, 0, newsItemHomePresenter);
	}

}
