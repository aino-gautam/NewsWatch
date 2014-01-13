package com.common.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class PagingPanel extends HorizontalPanel implements ClickHandler{
	
	protected HorizontalPanel hpBase = new HorizontalPanel();
	protected Panel customwidget = new HorizontalPanel();
	protected Label prevlink = new Label("<<prev");
	protected Label nextlink = new Label("next>>");
	protected Label currentPageNumber = new Label();
	protected Label pagesize = new Label();
	protected int pagelimit = 30;
	protected static int currentpage = 1;
	
	public PagingPanel(){
		HorizontalPanel hp = new HorizontalPanel();
		nextlink.addClickHandler(this);
		prevlink.addClickHandler(this);
		currentPageNumber.setStylePrimaryName("paginglbl");
		pagesize.setStylePrimaryName("paginglbl");
		hp.add(prevlink);
		hp.add(currentPageNumber);
		hp.add(pagesize);
		hp.add(nextlink);
		hp.setSpacing(10);
		
		add(customwidget);
		add(hp);
		setWidth("100%");
		setCellHorizontalAlignment(customwidget, HasHorizontalAlignment.ALIGN_LEFT);
		setCellVerticalAlignment(customwidget, HasVerticalAlignment.ALIGN_MIDDLE);
		setCellHorizontalAlignment(hp, HasHorizontalAlignment.ALIGN_RIGHT);
		
	}

	public Panel getCutomWidget() {
		return customwidget;
	}

	public void setCutomWidget(Widget customwidget) {
		this.customwidget.add(customwidget);
	}

	@Override
	public void onClick(ClickEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public int getPagelimit() {
		return pagelimit;
	}

	public void setPagelimit(int pagelimit) {
		this.pagelimit = pagelimit;
	}

	public int getCurrentpage() {
		return currentpage;
	}

	public void setCurrentpage(int currentpage) {
		this.currentpage = currentpage;
	}

	public Label getLblprev() {
		return prevlink;
	}

	public void setLblprev(Label lblprev) {
		this.prevlink = lblprev;
	}

	public Label getLblnext() {
		return nextlink;
	}

	public void setLblnext(Label lblnext) {
		this.nextlink = lblnext;
	}

	public Label getCurrentPageNumber() {
		return currentPageNumber;
	}

	public void setCurrentPageNumber(Label currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}

	public Label getPagesize() {
		return pagesize;
	}

	public void setPagesize(Label pagesize) {
		this.pagesize = pagesize;
	}

	
}
