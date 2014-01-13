package com.newscenter.client.obsolete;

import com.google.gwt.user.client.ui.Label;

public class SpecializedLabelForFlex extends Label
{
	private int labelId;
	private boolean cliked;
	
	public SpecializedLabelForFlex()
	{}
	
	
	public int getLabelId() {
		return labelId;
	}
	public void setLabelId(int labelId) {
		this.labelId = labelId;
	}
	public boolean isCliked() {
		return cliked;
	}
	public void setCliked(boolean cliked) {
		this.cliked = cliked;
	}
}
