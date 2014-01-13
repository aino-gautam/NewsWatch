package com.lighthouse.newspage.client.ui;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.comment.client.domain.ItemComment;

/**
 * 
 * @author kiran@ensarm.com
 * This class for showing all the comments related to particular newsitem  
 */
public class CommentListWidget extends FlexTable {

	private VerticalPanel commentListPanel = new VerticalPanel();

	private ItemComment itemComment;

	CommentListWidget() {

		setWidth("100%");
	}

	CommentListWidget(ItemComment itemComment) {
		this.itemComment = itemComment;

	}

	public void createCommentListWidgetUI() {
		try {

			FlexCellFormatter formatter = getFlexCellFormatter();
			formatter.setAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT,
					HasVerticalAlignment.ALIGN_TOP);
			formatter.setHeight(0, 0, "5%");

			formatter.setWordWrap(1, 0, true);
			formatter.setWidth(1, 0, "75%");
			//String strCommentTime = ((itemComment.getCommentTime()).toString());
			String strCommentTime =    DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(itemComment.getCommentTime());
			
			setWidget(
					1,
					0,
					createLine("______________________________________________________________"));
			setWidget(3, 0, createUserNameLabel(itemComment.getUserName()));
			setWidget(4, 0, createCommentTime(strCommentTime));
			setWidget(5, 0, createCommentText(itemComment.getText()));

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	private HorizontalPanel createCommentCountLabel(String string) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Label label = new Label();
		label.setText(string);
		label.setStylePrimaryName("labelfont");
		horizontalPanel.add(label);
		return horizontalPanel;
	}

	private HorizontalPanel createLine(String string) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Label label = new Label();
		label.setText(string);
		label.setStylePrimaryName("labelLine");
		horizontalPanel.add(label);
		return horizontalPanel;
	}

	private HorizontalPanel createCommentText(String text) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Label label = new Label();
		label.setText(text);
		label.setStylePrimaryName("commentText");

		horizontalPanel.add(label);

		return horizontalPanel;

	}

	private HorizontalPanel createCommentTime(String commentTime) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Label label = new Label();
		label.setText(commentTime);
		label.setStylePrimaryName("labelLine");
		horizontalPanel.add(label);
		return horizontalPanel;
	}

	private HorizontalPanel createUserNameLabel(String userName) {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		Label label = new Label();
		label.setText(userName);
		label.setStylePrimaryName("labelLine");
		horizontalPanel.add(label);
		return horizontalPanel;
	}

}
