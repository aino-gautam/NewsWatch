package com.newscenter.client.ui;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class CustomFlowPanel extends FlowPanel{
	
	protected abstract String getFlowStyle();
	protected abstract void setBoundaryWidget(AbsolutePanel boundaryPanel);
	
	@Override
	public void add(Widget w) {
		w.getElement().getStyle().setProperty("display", getFlowStyle());
		super.add(w);
	}
}
