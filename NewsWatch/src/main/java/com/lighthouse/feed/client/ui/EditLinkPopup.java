package com.lighthouse.feed.client.ui;
 
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.newscenter.client.news.NewsItems;

/**
 * class for edit popup link panel
 * 
 * @author kiran@ensarm.com
 * 
 */
public class EditLinkPopup extends PopupPanel{
	
	private LhFeedNewsItemPresenter feedNewsPresenter;
	
	public EditLinkPopup() {
		setAutoHideEnabled(true);
		setAnimationEnabled(true);
		setGlassEnabled(true);
	}

	public EditLinkPopup(NewsItems newsItems, LhFeedNewsItemPresenter feedNewsPresenter) {
		setAutoHideEnabled(true);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setFeedNewsPresenter(feedNewsPresenter);
		String ncIdStr = Window.Location.getParameter("NCID");
		String userIndustryName = Window.Location.getParameter("ncName");
		int industryId = Integer.parseInt(ncIdStr);
		EditLinkPopupPresenter editLinkPopupPresenter = new EditLinkPopupPresenter(
				userIndustryName, industryId, newsItems, this);

		add(editLinkPopupPresenter);
		setStylePrimaryName("searchPopup");
	}

	public void hidePopup(){
		hide();
	}

	public LhFeedNewsItemPresenter getFeedNewsPresenter() {
		return feedNewsPresenter;
	}

	public void setFeedNewsPresenter(LhFeedNewsItemPresenter feedNewsPresenter) {
		this.feedNewsPresenter = feedNewsPresenter;
	}

}
