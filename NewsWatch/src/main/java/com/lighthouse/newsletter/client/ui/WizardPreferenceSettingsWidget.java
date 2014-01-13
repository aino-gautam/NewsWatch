package com.lighthouse.newsletter.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Class for configuring preference settings for alerts
 * @author nairutee
 *
 */
public class WizardPreferenceSettingsWidget extends Composite{
	
	private WizardWidget parentWidget;
	private VerticalPanel baseVP;
	private static final String SAVE = "Save";
	private static final String RESET = "Reset";
	private static final String BACK = "Back";
	
	/**
	 * Constructor
	 * @param parentWidget WizardWidget
	 */
	public WizardPreferenceSettingsWidget(WizardWidget parentWidget){
		this.parentWidget = parentWidget;
		baseVP = new VerticalPanel();
		initWidget(baseVP);
	}

	/**
	 * Creates the page UI
	 */
	public void createUI(){
		
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
}
