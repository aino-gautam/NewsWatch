package com.lighthouse.group.client.ui;

import com.appUtils.client.NotificationPopup;
import com.appUtils.client.NotificationPopup.NotificationType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.group.client.GroupManager;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.client.service.ManageGroupService;
import com.lighthouse.group.client.service.ManageGroupServiceAsync;
import com.lighthouse.login.user.client.domain.LhUser;

public class GroupTabWidget extends HorizontalPanel {

	private Group group;
	private int index;
	private LhUser lhUser;
	private Image image ;
	private Label lblTabName;
	private PushButton deletePushButton;
	
	public GroupTabWidget() {

	}

	public GroupTabWidget(LhUser user,Group group, int index) {
		this.lhUser =  user;
		this.group = group;
		this.index = index;
		
		if(group.getIsFavorite() == 1){
			this.setStylePrimaryName("favgroupTabWidget");
			this.addStyleName("favgroupTabWidgetBg");
		}
		else
			this.setStylePrimaryName("groupTabWidgetBg");
		
		this.setTitle(group.getGroupName());
		createPopupUi();
	}

	/**
	 * creates the tab widget
	 * 
	 * @param user
	 */
	public void createUI() {
		this.setWidth("100px");
		if (lhUser.getAdmin() == 0) {
			if(group.getIsMandatory()==1){
			String lblTitle = group.getGroupName();
			String tempTitle = null;
			if (lblTitle.length() > 8) {
				tempTitle = lblTitle.substring(0, 8);
				tempTitle = tempTitle + "...";
				lblTabName = new Label(tempTitle);
			} else {

				lblTabName = new Label(group.getGroupName());
			}

			lblTabName.setStylePrimaryName("groupTabWidgetLabel");

			add(lblTabName);

			this.setCellHorizontalAlignment(lblTabName,
					HorizontalPanel.ALIGN_LEFT);
			}
			else if(group.getIsMandatory()==0){

				String lblTitle = group.getGroupName();
				String tempTitle = null;
				if (lblTitle.length() > 8) {
					tempTitle = lblTitle.substring(0, 8);
					
					tempTitle = tempTitle + "...";
					setLblTabName(new Label(tempTitle));

				} else {

					setLblTabName(new Label(group.getGroupName()));
				}
				
				getLblTabName().setStylePrimaryName("groupTabWidgetLabel");
				
				image = new Image("images/close-green.png");
				deletePushButton = new PushButton(image);
				deletePushButton.setPixelSize(20, 10);
				image.setVisible(false);

				getLblTabName().setWidth("85px");
				add(getLblTabName());
			    deletePushButton.setStylePrimaryName("pushBtn");
				add(deletePushButton);
				this.setCellHorizontalAlignment(getLblTabName(),
						HorizontalPanel.ALIGN_LOCALE_START);
				this.setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_LOCALE_END);

				getLblTabName().addMouseOverHandler(new MouseOverHandler() {

					@Override
					public void onMouseOver(MouseOverEvent event) {
						if (!image.isVisible())
							image.setVisible(true);

					}
				});
				getLblTabName().addMouseOutHandler(new MouseOutHandler() {

					@Override
					public void onMouseOut(MouseOutEvent event) {
							image.setVisible(false);
					}
				});

				deletePushButton.addMouseOverHandler(new MouseOverHandler() {

					@Override
					public void onMouseOver(MouseOverEvent event) {
						if (!image.isVisible())
						image.setVisible(true);
					}
				});
				deletePushButton.addMouseOutHandler(new MouseOutHandler() {

					@Override
					public void onMouseOut(MouseOutEvent event) {
						image.setVisible(false);
					}
				});

				deletePushButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						Widget arg0 = (Widget) event.getSource();
						int left = arg0.getAbsoluteLeft() - 25;
						int top = arg0.getAbsoluteTop() + 25;

						popupPanel.setAnimationEnabled(true);
						popupPanel.setAutoHideEnabled(true);

						popupPanel.setPopupPosition(left, top);

						popupPanel.show();
					}
				});
			}
		}else if (lhUser.getAdmin() == 1) {

			String lblTitle = group.getGroupName();
			String tempTitle = null;
			if (lblTitle.length() > 8) {
				tempTitle = lblTitle.substring(0, 8);
				
				tempTitle = tempTitle + "...";
				setLblTabName(new Label(tempTitle));

			} else {

				setLblTabName(new Label(group.getGroupName()));
			}
			
			getLblTabName().setStylePrimaryName("groupTabWidgetLabel");
			
			image = new Image("images/close-green.png");
			deletePushButton = new PushButton(image);
			deletePushButton.setPixelSize(20, 10);
			
			image.setVisible(false);
			getLblTabName().setWidth("85px");
			add(getLblTabName());
		    deletePushButton.setStylePrimaryName("pushBtn");
			add(deletePushButton);
			this.setCellHorizontalAlignment(getLblTabName(),HorizontalPanel.ALIGN_LOCALE_START);
			this.setCellHorizontalAlignment(image, HorizontalPanel.ALIGN_LOCALE_END);

			getLblTabName().addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					if (!image.isVisible())
						image.setVisible(true);

				}
			});
			getLblTabName().addMouseOutHandler(new MouseOutHandler() {

				@Override
				public void onMouseOut(MouseOutEvent event) {
						image.setVisible(false);
				}
			});

			deletePushButton.addMouseOverHandler(new MouseOverHandler() {

				@Override
				public void onMouseOver(MouseOverEvent event) {
					if (!image.isVisible())
					image.setVisible(true);
				}
			});
			deletePushButton.addMouseOutHandler(new MouseOutHandler() {

				@Override
				public void onMouseOut(MouseOutEvent event) {
					image.setVisible(false);
				}
			});

			deletePushButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Widget arg0 = (Widget) event.getSource();
					int left = arg0.getAbsoluteLeft() - 25;
					int top = arg0.getAbsoluteTop() + 25;

					popupPanel.setAnimationEnabled(true);
					popupPanel.setAutoHideEnabled(true);

					popupPanel.setPopupPosition(left, top);

					popupPanel.show();
				}
			});
		}
	}

	PopupPanel popupPanel = new PopupPanel();
	VerticalPanel vpContainer = new VerticalPanel();
	HorizontalPanel hpConatainer = new HorizontalPanel();
	Label lbnotificationmsg = new Label(
			"Delete group and tag selections. Do you wish to continue?");
	Button okBtn = new Button("Ok");
	Button cancelBtn = new Button("Cancel");

	private void createPopupUi() {
		hpConatainer.setSpacing(2);
		hpConatainer.add(okBtn);
		okBtn.setStylePrimaryName("roundedButton");
		addButtonHandler(okBtn);
		hpConatainer.add(cancelBtn);
		cancelBtn.setStylePrimaryName("roundedButton");
		addButtonHandler(cancelBtn);
		vpContainer.add(lbnotificationmsg);
		vpContainer.add(hpConatainer);
		popupPanel.add(vpContainer);
		popupPanel.setStylePrimaryName("searchPopup");

		popupPanel.hide();

	}

	private void addButtonHandler(Button btn) {
		btn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (event.getSource().equals(okBtn)) {

					ManageGroupServiceAsync groupService = GWT
							.create(ManageGroupService.class);
					groupService.deleteGroup(group.getGroupId(),
							new AsyncCallback<Boolean>() {

								@Override
								public void onSuccess(Boolean result) {
									if (result) {
										popupPanel.hide();
										GroupManager.getInstance().refreshUi(group);
										NotificationPopup notificationPopup = new NotificationPopup("Group deleted",NotificationType.NOTIFICATION);
										notificationPopup.createUI();
										notificationPopup.setPopupPosition(500,200);
										notificationPopup.show();
									} else {
										popupPanel.hide();
										NotificationPopup notificationPopup = new NotificationPopup("Cannot delete this group",NotificationType.NOTIFICATION);
										notificationPopup.createUI();
										notificationPopup.setPopupPosition(500,200);
										notificationPopup.show();
									}
								}

								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();
									popupPanel.hide();
									NotificationPopup notificationPopup = new NotificationPopup("Cannot delete this group",NotificationType.NOTIFICATION);
									notificationPopup.createUI();
									notificationPopup.setPopupPosition(500,200);
									notificationPopup.show();
								}
							});
				}
				else if (event.getSource().equals(cancelBtn)) {
					popupPanel.hide();
				}
			}
		});

	}

	public Image getImage() {
		return image;
	}

	public void setLblTabName(Label lblTabName) {
		this.lblTabName = lblTabName;
	}

	public Label getLblTabName() {
		return lblTabName;
	}
}
