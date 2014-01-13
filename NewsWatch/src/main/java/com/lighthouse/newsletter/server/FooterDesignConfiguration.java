package com.lighthouse.newsletter.server;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class FooterDesignConfiguration extends NewsLetterDesignConfiguration {

	Element tableElement;
	Element firstRowElement;
	Element nameAndEmailElement;
	Element unsubscribeElement;
	Element poweredByElement;
	
	public FooterDesignConfiguration(String xml) {
		super(xml);
		NodeList tableNameNodeList = xmlDom.getElementsByTagName("table");
		tableElement = (Element) tableNameNodeList.item(0);
		
		NodeList firstRowNameNodeList = xmlDom.getElementsByTagName("firstRow");
		firstRowElement = (Element) firstRowNameNodeList.item(0);
		
		NodeList nameAndEmailNameNodeList = xmlDom.getElementsByTagName("nameAndemail");
		nameAndEmailElement = (Element) nameAndEmailNameNodeList.item(0);
		
		NodeList unsubscribeNameNodeList = xmlDom.getElementsByTagName("unsubscribe");
		unsubscribeElement = (Element) unsubscribeNameNodeList.item(0);
		
		NodeList poweredByNameNodeList = xmlDom.getElementsByTagName("poweredBy");
		poweredByElement = (Element) poweredByNameNodeList.item(0);
	}
	
	public String getTableWidth() {
		return getValue(tableElement, "width");
	}
	
	public String getTableCellSpacing() {
		return getValue(tableElement, "cellSpacing");
	}

	public String getTableCellPadding() {
		return getValue(tableElement, "cellPadding");
	}

	public String getTableBackgroundColor() {
		return getValue(tableElement, "backgroundColor");
	}

	public String getFirstRowPadding() {
		return getValue(firstRowElement, "padding");
	}
	
	public String getFirstRowBorder() {
		return getValue(firstRowElement, "border");
	}
	
	public String getFirstRowAlign() {
		return getValue(firstRowElement, "align");
	}
	
	public String getFirstRowVerticalAlign() {
		return getValue(firstRowElement, "valign");
	}
	
	public String getFirstRowWidth() {
		return getValue(firstRowElement, "width");
	}
	
	public String getNameAndElementFontColor() {
		return getValue(nameAndEmailElement, "color");
	}

	public String getNameAndElementFontFamily() {
		return getValue(nameAndEmailElement, "family");
	}

	public String getNameAndElementFontSize() {
		return getValue(nameAndEmailElement, "size");
	}

	public String getNameAndElementFontWeight() {
		return getValue(nameAndEmailElement, "weight");
	}

	public String getNameAndElementFontStyle() {
		return getValue(nameAndEmailElement, "style");
	}

	public String getNameAndElementTextAlignment() {
		return getValue(nameAndEmailElement, "TextAlignment");
	}

	public String getUnsubscribedEnabeledOrNot() {
		return getValue(unsubscribeElement, "enableOrNot");
	}
	
	public String getUnsubscribedFontColor() {
		return getValue(unsubscribeElement, "color");
	}

	public String getUnsubscribedFontFamily() {
		return getValue(unsubscribeElement, "family");
	}

	public String getUnsubscribedFontSize() {
		return getValue(unsubscribeElement, "size");
	}

	public String getUnsubscribedFontWeight() {
		return getValue(unsubscribeElement, "weight");
	}

	public String getUnsubscribedFontStyle() {
		return getValue(unsubscribeElement, "style");
	}

	public String getUnsubscribedDisplayText() {
		return getValue(unsubscribeElement, "displayText");
	}
	
	public String getUnsubscribedDisplayLinkText() {
		return getValue(unsubscribeElement, "displayLinkText");
	}
	
	public String getUnsubscribedLinkUrl() {
		return getValue(unsubscribeElement, "linkUrl");
	}
	
	public String getUnsubscribedTextAlignment() {
		return getValue(unsubscribeElement, "TextAlignment");
	}

	public String getPoweredByEnabeledOrNot() {
		return getValue(poweredByElement, "enableOrNot");
	}
	
	public String getPoweredByFontColor() {
		return getValue(poweredByElement, "color");
	}

	public String getPoweredByFontFamily() {
		return getValue(poweredByElement, "family");
	}

	public String getPoweredByFontSize() {
		return getValue(poweredByElement, "size");
	}

	public String getPoweredByFontWeight() {
		return getValue(poweredByElement, "weight");
	}

	public String getPoweredByFontStyle() {
		return getValue(poweredByElement, "style");
	}

	public String getPoweredByDisplayText() {
		return getValue(poweredByElement, "displayText");
	}
	
	public String getPoweredByDisplayLinkText() {
		return getValue(poweredByElement, "displayLinkText");
	}
	
	public String getPoweredByLinkUrl() {
		return getValue(poweredByElement, "linkUrl");
	}
	
	public String getPoweredByTextAlignment() {
		return getValue(poweredByElement, "TextAlignment");
	}
	
	public void setPoweredByDisplayText(String value) {
		setValue(poweredByElement, "dispalyText", value);
	}
	
	public void setPoweredByLinkUr(String value) {
		setValue(poweredByElement, "linkUrl", value);
	}
	
	public void setPoweredByTextAlignment(String value) {
		setValue(poweredByElement, "TextAlignment", value);
	}
		
	public void setPoweredByEnabeledOrNot(String value) {
		setValue(poweredByElement, "enableOrNot", value);
	}
		
	public void setPoweredByFontColor(String value) {
		setValue(poweredByElement, "color", value);
	}

	public void setPoweredByFontFamily(String value) {
		setValue(poweredByElement, "family", value);
	}

	public void setPoweredByFontSize(String value) {
		setValue(poweredByElement, "size", value);
	}

	public void setPoweredByFontWeight(String value) {
		setValue(poweredByElement, "weight",value);
	}

	public void setPoweredByFontStyle(String value) {
		setValue(poweredByElement, "style", value);
	}

	public void setPoweredByElementFontColor(String value) {
		setValue(poweredByElement, "color", value);
	}

	public void setPoweredByElementFontFamily(String value) {
		setValue(poweredByElement, "family", value);
	}
	
	public void setUnsubscribedDisplayText(String value) {
		setValue(unsubscribeElement, "displayText", value);
	}
	
	public void setUnsubscribedDisplayLinkText(String value) {
		setValue(unsubscribeElement, "displayLinkText", value);
	}
	
	public void setUnsubscribedLinkUr(String value) {
		setValue(unsubscribeElement, "linkUrl", value);
	}
	
	public void setUnsubscribedTextAlignment(String value) {
		setValue(unsubscribeElement, "TextAlignment", value);
	}
		
	public void setUnsubscribedEnabeledOrNot(String value) {
		setValue(unsubscribeElement, "enableOrNot", value);
	}
		
	public void setUnsubscribedFontColor(String value) {
		setValue(unsubscribeElement, "color", value);
	}

	public void setUnsubscribedFontFamily(String value) {
		setValue(unsubscribeElement, "family", value);
	}

	public void setUnsubscribedFontSize(String value) {
		setValue(unsubscribeElement, "size", value);
	}

	public void setUnsubscribedFontWeight(String value) {
		setValue(unsubscribeElement, "weight",value);
	}

	public void setUnsubscribedFontStyle(String value) {
		setValue(unsubscribeElement, "style", value);
	}

	public void setNameAndElementFontColor(String value) {
		setValue(nameAndEmailElement, "color", value);
	}

	public void setNameAndElementFontFamily(String value) {
		setValue(nameAndEmailElement, "family", value);
	}

	public void setNameAndElementFontSize(String value) {
		setValue(nameAndEmailElement, "size", value);
	}

	public void setNameAndElementFontWeight(String value) {
		setValue(nameAndEmailElement, "weight",value);
	}

	public void setNameAndElementFontStyle(String value) {
		setValue(nameAndEmailElement, "style", value);
	}

	public void setNameAndElementTextAlignment(String value) {
		setValue(nameAndEmailElement, "TextAlignment", value);
	}

	public void setFirstRowPadding(String value) {
		setValue(firstRowElement, "padding", value);
	}
	
	public void setFirstRowBorder(String value) {
		setValue(firstRowElement, "border", value);
	}
	
	public void setFirstRowAlign(String value) {
		setValue(firstRowElement, "align", value);
	}
	
	public void setFirstRowVerticalAlign(String value) {
		setValue(firstRowElement, "valign", value);
	}
	
	public void setFirstRowWidth(String value) {
		setValue(firstRowElement, "width", value);
	}

	public void setTableWidth(String value) {
		setValue(tableElement, "width", value);
	}

	public void setTableCellSpacing(String value) {
		setValue(tableElement, "cellSpacing", value);
	}

	public void setTableCellPadding(String value) {
		setValue(tableElement, "cellPadding",value);
	}

	public void setTableBackgroundColor(String value) {
		setValue(tableElement, "backgroundColor", value);
	}

	public String getNameAndElementNameToBeDisplayed() {
		return getValue(nameAndEmailElement, "NameToBeDisplayed");
	}

	public String getNameAndElementEmailToBeDisplayed() {
		return getValue(nameAndEmailElement, "EmailToBeDisplayed");
	}
	
	public void setNameAndElementNameToBeDisplayed(String value) {
		setValue(nameAndEmailElement, "NameToBeDisplayed", value);
	}

	public void setNameAndElementEmailToBeDisplayed(String value) {
		setValue(nameAndEmailElement, "EmailToBeDisplayed", value);
	}
}
