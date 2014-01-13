package com.lighthouse.newsletter.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Class for selection of groups for alerts
 * @author nairutee & pritam
 *
 */
public class WizardGroupSelectionWidget extends Composite{
	
	private WizardWidget parentWizardWidget;
	private VerticalPanel baseVP;
	private static final String CONTINUE = "Continue";
	private static final String RESET = "Reset";
	private static final String CLEAR = "Clear";
	
	
	/**
	 * Constructor
	 * @param parentWizardWidget WizardWidget
	 */
	public WizardGroupSelectionWidget(WizardWidget parentWizardWidget){
		this.parentWizardWidget = parentWizardWidget;
		baseVP = new VerticalPanel();
		initWidget(baseVP);
	}

	/**
	 * Creates the UI of the page
	 */
	public void createUI(){
		DisclosurePanel mandatoryGroupDP = new DisclosurePanel();
		DisclosurePanel customGroupDP = new DisclosurePanel();
		Button saveButton = createButton(CONTINUE);
		Button resetButton = createButton(RESET);
		Button clearButton = createButton(CLEAR);
		
	}
	
	/**
	 * creates a custom button
	 * @param buttonText the text to appear on the button
	 * @return Button
	 */
	private Button createButton(String buttonText){
		Button button = new Button(buttonText);
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				// TODO Auto-generated method stub
				;
			}
		});
		// set styles on the button
		return button;
	}
	
	/**
	 * creates a panel containing checkbox and its corresponding label
	 * @param checkboxValue the value for the checkbox
	 * @return HorizontalPanel
	 */
	private HorizontalPanel createCheckBoxComponent(String checkboxValue){
		HorizontalPanel checkboxHP = new HorizontalPanel();
		CheckBox checkBox = new CheckBox();
		Label checkboxLabel = new Label(checkboxValue);
		//set widgets in hp
		return checkboxHP;
		
	}
	
}
