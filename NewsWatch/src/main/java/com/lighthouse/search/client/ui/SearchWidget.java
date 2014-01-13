package com.lighthouse.search.client.ui;

import com.appUtils.client.NotificationPopup;
import com.appUtils.client.NotificationPopup.NotificationType;
import com.appUtils.client.exception.FeatureNotSupportedException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.main.client.LhMain;

/**
 * Class for the search box widget
 * @author Mahesh & nairutee
 *
 */
public class SearchWidget extends Composite {
	private AdvanceSearchPopup popup;
	private ItemSuggestOracle oracle;
	public SuggestBox suggestionBox;
	private Image searchImg;

	private Label advSearchAnchor = new Label("Date Search");
	private HorizontalPanel horizontalPanel = new HorizontalPanel();
	private VerticalPanel vPanel = new VerticalPanel();
	public static String SEARCHEDTEXT = "";
/*	public final static String SEARCHEDDATE = "SearchedDate";
*/
	public static int count=0;
	private LhMain lhMainRef;
	
	public SearchWidget(LhUserPermission lhUserPermission) throws FeatureNotSupportedException {
		if(lhUserPermission.isSearchPermitted() == 1){
			initWidget(vPanel);
			createUI();
		}else
			throw new FeatureNotSupportedException("Search not supported");
	}

	
	/**
	 * creates the page ui
	 */
	private void createUI() {
		searchImg = new Image("images/search_off.png");
		searchImg.setStylePrimaryName("imagesPlacement");
		searchImg.addStyleName("clickable");
		vPanel.setStylePrimaryName("searchPanel");
		oracle = new ItemSuggestOracle();

		suggestionBox = new SuggestBox(oracle);
		suggestionBox.setStylePrimaryName("supop");
		horizontalPanel.add(suggestionBox);
		horizontalPanel.setSpacing(2);
		horizontalPanel.add(searchImg);
//		addKeyPressHandler(suggestionBox);
		addHandler(searchImg);
		advSearchAnchor.setStylePrimaryName("anchorDesign");
		horizontalPanel.add(advSearchAnchor);

		advSearchAnchor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				SEARCHEDTEXT = suggestionBox.getText();
				popup = new AdvanceSearchPopup(suggestionBox.getText());
				popup.setLhMainRef(getLhMainRef());
				Widget arg0 = (Widget) event.getSource();
				int left = arg0.getAbsoluteLeft() - 325;
				int top = arg0.getAbsoluteTop() + 25;
				popup.setAnimationEnabled(true);
				popup.setAutoHideEnabled(true);
				popup.setPopupPosition(left, top);
				popup.show();
				suggestionBox.setText("");

			}
		});
		suggestionBox.setText(SEARCHEDTEXT);
		/*suggestionBox.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
			//	count=0;
				if(event.getNativeKeyCode()==13){
					try {
				
							String searchText = suggestionBox.getText();
							suggestionBox.hideSuggestionList();

							if (searchText.matches("")) {
								NotificationPopup notificationPopup = new NotificationPopup(
										"Please enter text to be searched",
										NotificationType.NOTIFICATION);
								notificationPopup.createUI();
								Widget arg0 = (Widget) event.getSource();
								int left = arg0.getAbsoluteLeft() - 15;
								int top = arg0.getAbsoluteTop() + 25;
								notificationPopup.setAnimationEnabled(true);
								notificationPopup.setAutoHideEnabled(true);
								notificationPopup.setPopupPosition(left, top); 
								if(count==0){
								notificationPopup.show();
								count++;
								}
							
								} else {
								System.out.println("Searched text: " + searchText);
						
								getLhMainRef().getResultPresenter().performSearch(
										searchText, null);
								getLhMainRef().showDeckWidget(1);
								if(!(count==0))
								suggestionBox.setText("");
								count++;
							}
						
							
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});*/
		vPanel.add(horizontalPanel);
		vPanel.setSpacing(2);
	}

/*	private void addKeyPressHandler(final SuggestBox suggestionBox2) {
		suggestionBox2.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode()==13){
					try {
				
							String searchText = suggestionBox2.getText();
							suggestionBox2.hideSuggestionList();

							if (searchText.matches("")) {
								NotificationPopup notificationPopup = new NotificationPopup(
										"Please enter text to be searched",
										NotificationType.NOTIFICATION);
								notificationPopup.createUI();
								Widget arg0 = (Widget) event.getSource();
								int left = arg0.getAbsoluteLeft() - 425;
								int top = arg0.getAbsoluteTop() + 25;
								notificationPopup.setAnimationEnabled(true);
								notificationPopup.setAutoHideEnabled(true);
								notificationPopup.setPopupPosition(left, top);
								notificationPopup.show();
							} else {
								System.out.println("Searched text: " + searchText);
						
								getLhMainRef().getResultPresenter().performSearch(
										searchText, null);
								getLhMainRef().showDeckWidget(1);
							
							}

							suggestionBox2.setText("");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
	}*/

	private void addHandler(Image image) {
		image.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				try {
					if (event.getSource().equals(searchImg)) {
						String searchText = suggestionBox.getText();
						suggestionBox.hideSuggestionList();

						if (searchText.matches("")) {
							NotificationPopup notificationPopup = new NotificationPopup(
									"Please enter text to be searched",
									NotificationType.NOTIFICATION);
							notificationPopup.createUI();
							Widget arg0 = (Widget) event.getSource();
							int left = arg0.getAbsoluteLeft() - 15;
							int top = arg0.getAbsoluteTop() + 25;
							notificationPopup.setAnimationEnabled(true);
							notificationPopup.setAutoHideEnabled(true);
							notificationPopup.setPopupPosition(left, top);
							notificationPopup.show();
						} else {
							System.out.println("Searched text: " + searchText);
							suggestionBox.setText("");
							getLhMainRef().getResultPresenter().performSearch(
									searchText, null);
							getLhMainRef().showDeckWidget(1);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SuggestBox getSuggestionBox() {
		return suggestionBox;
	}

	public void setSuggestionBox(SuggestBox suggestionBox) {
		this.suggestionBox = suggestionBox;
	}

	public LhMain getLhMainRef() {
		return lhMainRef;
	}

	public void setLhMainRef(LhMain lhMainRef) {
		this.lhMainRef = lhMainRef;
	}
}
