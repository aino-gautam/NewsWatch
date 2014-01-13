package com.appUtils.client;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * 
 * @author kiran@ensarm.com 
 * 
 * This class provide glass panel for disable the LhTagPresenter
 *
 */
public class GlassPanel extends Composite implements ResizeHandler{

	public static final String STYLE = "gcomp-GlassPanel1";
	public static final String STYLE1 ="gcomp-GlassPanel2";

    private SimplePanel panel = new SimplePanel();
    
    public GlassPanel() {
        initWidget(panel);
        panel.setStylePrimaryName(STYLE);
        Window.addResizeHandler(this);
        resize();
    }
    
    public GlassPanel(boolean b) {
    	initWidget(panel);

       
        Window.addResizeHandler(this);
        resize();
	}

	public void show() {
        // Override the styles explicitly, because it's needed
        // every time the widget is detached
        Element elem = panel.getElement();
        DOM.setStyleAttribute(elem, "left", "0");
        DOM.setStyleAttribute(elem, "top", "0");
        DOM.setStyleAttribute(elem, "position", "absolute");
        resize();
        RootPanel.get().add(this, 85, 155);
        setVisible(true);
    }

    public void hide() {
        RootPanel.get().remove(this);
        setVisible(false);
       
    }

    private void resize() {
   
        panel.setSize("220px","240px");
        panel.setStylePrimaryName(STYLE);
        
    }
	
    @Override
	public void onResize(ResizeEvent event) {
		resize();
		
	}
}
