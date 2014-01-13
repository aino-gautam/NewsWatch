package com.common.client;

import java.io.Serializable;
import java.util.ArrayList;

public class PageResult implements Serializable{
	
	private int totalNoOfPages;
	private int currentPage=1;
	private int pagesize = 5;
	
	private ArrayList list = new ArrayList();
	public int getTotalNoOfPages() {
		return totalNoOfPages;
	}
	public void setTotalNoOfPages(int noOfPages) {
		this.totalNoOfPages = noOfPages;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public ArrayList getList() {
		return list;
	}
	public void setList(ArrayList list) {
		this.list = list;
	}
	public int getPagesize() {
		return pagesize;
	}
	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}
	

}
