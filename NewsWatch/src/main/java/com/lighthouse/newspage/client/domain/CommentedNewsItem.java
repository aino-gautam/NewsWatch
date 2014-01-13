package com.lighthouse.newspage.client.domain;

import java.util.ArrayList;

import com.lighthouse.comment.client.domain.ItemComment;
import com.newscenter.client.news.NewsItems;

public class CommentedNewsItem extends NewsItems{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1502704380223158536L;
	
	private String logoImagePath = null;
	
	private ArrayList<ItemComment> commentsList = new ArrayList<ItemComment>();
	
	public void addItemComment(ItemComment itemComment){
		commentsList.add(itemComment);
	}

	public void removeItemComment(ItemComment itemComment){
		commentsList.remove(itemComment);
	}

	public ArrayList<ItemComment> getCommentsList() {
		return commentsList;
	}

	public void setCommentsList(ArrayList<ItemComment> commentsList) {
		this.commentsList = commentsList;
	}

	/**
	 * @return the logoImagePath
	 */
	public String getLogoImagePath() {
		return logoImagePath;
	}

	/**
	 * @param logoImagePath the logoImagePath to set
	 */
	public void setLogoImagePath(String logoImagePath) {
		this.logoImagePath = logoImagePath;
	}
	
}
