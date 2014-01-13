package com.lighthouse.newsletter.client.ui;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Holder class for the alert configurations
 * @author nairutee & pritam
 *
 */
public class WizardWidget extends Composite{

	private DeckPanel deckPanel;
	private VerticalPanel baseVP;
	private WizardGroupSelectionWidget groupSelectionWidget;
	
	/**
	 * Constructor
	 */
	public WizardWidget(){
		baseVP = new VerticalPanel();
		initWidget(baseVP);
		initialize();
	}
	
	/**
	 * iniitalizes various components and inserts in deckpanel
	 */
	public void initialize(){
		groupSelectionWidget = new WizardGroupSelectionWidget(this);
	}
	
	/**
	 * Creates the UI
	 */
	public void createUI(){
		deckPanel = new DeckPanel();
		deckPanel.setAnimationEnabled(true);
		deckPanel.add(groupSelectionWidget);
		baseVP.add(deckPanel);
	}
	
	public void showGroupSelectionWidget(){
		groupSelectionWidget.createUI();
		deckPanel.showWidget(0);
	}
}
