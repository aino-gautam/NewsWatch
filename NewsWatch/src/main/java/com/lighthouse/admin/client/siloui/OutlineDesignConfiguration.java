package com.lighthouse.admin.client.siloui;

import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

public class OutlineDesignConfiguration extends NewsLetterDesignConfiguration{

	Element tableElement;
	
	public OutlineDesignConfiguration(String xml) {
		super(xml);
		NodeList tableNameNodeList = xmlDom.getElementsByTagName("table");
		tableElement = (Element) tableNameNodeList.item(0);
	}

	public String getTableWidth() {
		return getValue(tableElement, "width");
	}

	public String getTableBackgroundColor() {
		return getValue(tableElement, "backgroundColor");
	}
	
	public String getTableBackgroundImage() {
		return getValue(tableElement, "backgroundImage");
	}
	
	public void setTableBackgroundImageUrl(String value) {
		 setValue(tableElement, "backgroundImageUrl", value);
	}
	
	public String getTableBackgroundImageUrl() {
		return getValue(tableElement, "backgroundImageUrl");
	}
	
	public void setUseUploadedImg(String value) {
		 setValue(tableElement, "UseUploadedImg", value);
	}
	
	public String getUseUploadedImg() {
		return getValue(tableElement, "UseUploadedImg");
	}
	
	public void setTableWidth(String value) {
		 setValue(tableElement, "width", value);
	}
	
	public void setTableBackgroundColor(String value) {
		 setValue(tableElement, "backgroundColor", value);
	}
	
	public void setTableBackgroundImage(String value) {
		 setValue(tableElement, "backgroundImage", value);
	}
	
}
