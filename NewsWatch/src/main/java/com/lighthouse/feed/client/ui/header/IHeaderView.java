package com.lighthouse.feed.client.ui.header;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * 
 * @author kiran@ensarm.com
 * 
 */
public interface IHeaderView extends IsWidget {

	public interface IHeaderPresenter {
		public void OnManageClick();

		public void OnNewsCenterClick();

		public void OnLogoutClick();
	}

}
