package com.lighthouse.newspage.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.user.client.ui.FlexTable;
import com.lighthouse.comment.client.domain.ItemComment;

/**
 * 
 * @author kiran@ensarm.com
 *
 */
public class CommentListPresenter extends FlexTable {

	private CommentListWidget commentListWidget;

	CommentListPresenter() {

		setWidth("100%");
	}

	public void createCommentListWidgetUI(ArrayList arrayList) {
		// clear();

		Iterator iter = arrayList.iterator();
		int row = 0;
		int col = 0;

		while (iter.hasNext()) {
			ItemComment itemComment = (ItemComment) iter.next();
			commentListWidget = new CommentListWidget(itemComment);
			commentListWidget.createCommentListWidgetUI();

			setWidget(row, col, commentListWidget);
			row++;
		}

	}
}
