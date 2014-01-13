package com.admin.client;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.Widget;
import com.trial.client.TrialAccount;

public class SpecializedLink extends Hyperlink{
	
	private int id;
	private AdminRegistrationInformation userRef;
	private boolean clicked  = false;
	private SpecializedLink associatedExtendLink;
	private TrialAccount trialAccountRef;
	

	public boolean isClicked() {
		return clicked;
	}

	public void setClicked(boolean clicked) {
		this.clicked = clicked;
	}

	public SpecializedLink(){
		addStyleName("hyperlinkStyle");
		setTargetHistoryToken("link");
	}
	
	public void disableLink(){
		removeStyleName("hyperlinkStyle");
		addStyleName("disablehyperlinkStyle");
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AdminRegistrationInformation getUserRef() {
		return userRef;
	}

	public void setUserRef(AdminRegistrationInformation userRef) {
		this.userRef = userRef;
	}

	public SpecializedLink getAssociatedExtendLink() {
		return associatedExtendLink;
	}

	public void setAssociatedExtendLink(SpecializedLink associatedExtendLink) {
		this.associatedExtendLink = associatedExtendLink;
	}

	public TrialAccount getTrialAccountRef() {
		return trialAccountRef;
	}

	public void setTrialAccountRef(TrialAccount trialAccountRef) {
		this.trialAccountRef = trialAccountRef;
	}

}
