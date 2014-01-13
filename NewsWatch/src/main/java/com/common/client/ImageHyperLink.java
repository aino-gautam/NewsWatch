package com.common.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Hyperlink;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MouseListener;
import com.google.gwt.user.client.ui.MouseListenerCollection;
import com.google.gwt.user.client.ui.SourcesMouseEvents;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.Event;

public class ImageHyperLink extends Hyperlink implements HasAllMouseHandlers,ClickHandler 
{
//	private MouseListenerCollection mouseListeners;
	private MouseOverHandler mouseListeners;
	
	public ImageHyperLink(Image img)
	{
		this(img, "");
	}
	public ImageHyperLink(Image img, String targetHistoryToken) {
	    super();
	    DOM.appendChild(DOM.getFirstChild(getElement()), img.getElement());
	    setTargetHistoryToken(targetHistoryToken);

	    img.unsinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
	    sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
	    sinkEvents(Event.ONCLICK);
	  }

	
	/*public void addMouseListener(MouseListener arg0) {
		
	}

	
	public void removeMouseListener(MouseListener arg0) {
		
	}
	
	/*public void onClick(Widget arg0) {
		Window.open("http://localhost:8888/com.lonzaNewscenter.lonzaNewscenter/lonzaNewscenter.html", "_self", "");	
		
	}*/
	@Override
	public void onClick(ClickEvent event) {
		// TODO Auto-generated method stub
		Widget arg0=(Widget) event.getSource();
		Window.open("http://localhost:8888/com.lonzaNewscenter.lonzaNewscenter/lonzaNewscenter.html", "_self", "");	
		
	}
	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}

}
