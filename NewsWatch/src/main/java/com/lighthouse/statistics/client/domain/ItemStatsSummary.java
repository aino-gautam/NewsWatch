package com.lighthouse.statistics.client.domain;

import java.io.Serializable;

/**
 * 
 * @author kiran@ensarm.com
 *
 */
public class ItemStatsSummary implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	private int dayReadCount;
	private int weekReadCount;
	private int groupDayReadCount;
	private int newsItemId;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDayReadCount() {
		return dayReadCount;
	}
	public void setDayReadCount(int dayReadCount) {
		this.dayReadCount = dayReadCount;
	}
	public int getWeekReadCount() {
		return weekReadCount;
	}
	public void setWeekReadCount(int weekReadCount) {
		this.weekReadCount = weekReadCount;
	}
	public int getGroupDayReadCount() {
		return groupDayReadCount;
	}
	public void setGroupDayReadCount(int groupDayReadCount) {
		this.groupDayReadCount = groupDayReadCount;
	}
	public int getNewsItemId() {
		return newsItemId;
	}
	public void setNewsItemId(int newsItemId) {
		this.newsItemId = newsItemId;
	}

}
