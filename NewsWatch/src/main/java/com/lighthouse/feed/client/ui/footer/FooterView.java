package com.lighthouse.feed.client.ui.footer;

import com.appUtils.client.Footer;
import com.google.inject.Singleton;
import com.lighthouse.feed.client.ui.ReverseCompositeView;
import com.lighthouse.feed.client.ui.footer.IFooterView.IFooterPresenter;
/**
 * Displaying the footer part of the Review feed module
 * @author kiran@ensarm.com
 *
 */
@Singleton
public class FooterView extends ReverseCompositeView<IFooterPresenter> implements IFooterView{
	
	//private VerticalPanel vpFooter = new VerticalPanel();

	public FooterView(){
		Footer footer = new Footer();
		initWidget(footer);

	}

}
