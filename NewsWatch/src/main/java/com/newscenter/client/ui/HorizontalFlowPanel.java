package com.newscenter.client.ui;

import com.google.gwt.user.client.ui.AbsolutePanel;

public class HorizontalFlowPanel extends CustomFlowPanel {
	AbsolutePanel boundaryPanel;
	@Override
	protected String getFlowStyle() {
		return "inline";
	}

	@Override
	protected void setBoundaryWidget(AbsolutePanel boundaryPanel) {
		this.boundaryPanel = boundaryPanel;
	}

}
