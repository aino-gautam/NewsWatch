package com.newscenter.client.obsolete;

import com.google.gwt.user.client.ui.Button;

public class SpecializedButton extends Button 
{
	
	private int buttonrowvalue;
	private int buttoncolvalue;
	private String buttonText;
	public SpecializedButton()
	{}
	
	public String getButtonText() {
		return buttonText;
	}
	public void setButtonText(String buttonText) {
		this.buttonText = buttonText;
	}

	public int getButtonrowvalue() {
		return buttonrowvalue;
	}

	public void setButtonrowvalue(int buttonrowvalue) {
		this.buttonrowvalue = buttonrowvalue;
	}

	public int getButtoncolvalue() {
		return buttoncolvalue;
	}

	public void setButtoncolvalue(int buttoncolvalue) {
		this.buttoncolvalue = buttoncolvalue;
	}

}
