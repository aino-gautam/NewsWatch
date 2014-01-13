package com.lighthouse.feed.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.mvp4g.client.view.ReverseViewInterface;
/**
 * 
 * @author kiran@ensarm.com
 *
 */
public class ReverseCompositeView<P> extends Composite implements
		ReverseViewInterface<P> {

	protected P presenter;

	// setting the particular presenter
	@Override
	public void setPresenter(P presenter) {
		this.presenter = presenter;
	}

	@Override
	public P getPresenter() {
		return presenter;
	}
}
