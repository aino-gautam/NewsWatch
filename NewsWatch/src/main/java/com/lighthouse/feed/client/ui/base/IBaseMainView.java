package com.lighthouse.feed.client.ui.base;

import com.google.gwt.user.client.ui.IsWidget;
/**
 * 
 * @author kiran@ensarm.com
 *
 */
public interface IBaseMainView extends IsWidget{
	
	public interface IBaseMainPresenter{
		
	}
	void setBody( IsWidget body );
}
