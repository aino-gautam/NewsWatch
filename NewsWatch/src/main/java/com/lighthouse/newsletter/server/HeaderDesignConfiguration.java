package com.lighthouse.newsletter.server;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



public class HeaderDesignConfiguration extends NewsLetterDesignConfiguration {
	Element alertNameElement;
	Element logoElement;
	Element dateElement;
	Element baseTableElement;

	public HeaderDesignConfiguration(String xml) {
		super(xml);
		NodeList alertNameNodeList = xmlDom.getElementsByTagName("AlertName");
		alertNameElement = (Element) alertNameNodeList.item(0);
		NodeList logoNodeList = xmlDom.getElementsByTagName("Logo");
		logoElement = (Element) logoNodeList.item(0);
		NodeList dateNodeList = xmlDom.getElementsByTagName("Date");
		dateElement = (Element) dateNodeList.item(0);
		NodeList baseTableNodeList = xmlDom.getElementsByTagName("BaseTable");
		baseTableElement = (Element) baseTableNodeList.item(0);
	}

	public String getAlertNameFontColor() {
		return getValue(alertNameElement, "color");
	}

	public String getAlertNameFontFamily() {
		return getValue(alertNameElement, "family");
	}

	public String getAlertNameFontSize() {
		return getValue(alertNameElement, "size");
	}

	public String getAlertNameFontWeight() {
		return getValue(alertNameElement, "weight");
	}

	public String getAlertNameFontStyle() {
		return getValue(alertNameElement, "style");
	}

	public String getAlertNameTextAlignment() {
		return getValue(alertNameElement, "TextAlignment");
	}

	public String getLogoAlignment() {
		return getValue(logoElement, "Alignment");
	}

	public String getLogoImage() {
		return getValue(logoElement, "LogoImage");
	}

	public String getLogoTargetUrl() {
		return getValue(logoElement, "TargetUrl");
	}

	public String getLogoAlternativeText() {
		return getValue(logoElement, "AlternativeText");
	}

	public String getDateFontColor() {
		return getValue(dateElement, "color");
	}

	public String getDateFontFamily() {
		return getValue(dateElement, "family");
	}

	public String getDateFontSize() {
		return getValue(dateElement, "size");
	}

	public String getDateFontWeight() {
		return getValue(dateElement, "weight");
	}

	public String getDateFontStyle() {
		return getValue(dateElement, "style");
	}

	public String getDateTextAlignment() {
		return getValue(dateElement, "TextAlignment");
	}

	public String getBaseTableWidth() {
		return getValue(baseTableElement, "Width");
	}

	public String getBaseTableCellSpacing() {
		return getValue(baseTableElement, "CellSpacing");
	}

	public String getBaseTableCellPadding() {
		return getValue(baseTableElement, "CellPadding");
	}

	public String getBaseTableBackgroundColor() {
		return getValue(baseTableElement, "BackgroundColor");
	}

	public void setAlertNameFontColor(String value) {
		setValue(alertNameElement, "color", value);
	}

	public void setAlertNameFontFamily(String value) {
		setValue(alertNameElement, "family", value);
	}

	public void setAlertNameFontSize(String value) {
		setValue(alertNameElement, "size", value);
	}

	public void setAlertNameFontWeight(String value) {
		setValue(alertNameElement, "weight",value);
	}

	public void setAlertNameFontStyle(String value) {
		setValue(alertNameElement, "style", value);
	}

	public void setAlertNameTextAlignment(String value) {
		setValue(alertNameElement, "TextAlignment", value);
	}

	public void setLogoAlignment(String value) {
		setValue(logoElement, "Alignment", value);
	}

	public void setLogoImage(String value) {
		setValue(logoElement, "LogoImage", value);
	}

	public void setLogoTargetUrl(String value) {
		setValue(logoElement, "TargetUrl", value);
	}

	public void setLogoAlternativeText(String value) {
		setValue(logoElement, "AlternativeText", value);
	}

	public void setDateFontColor(String value) {
		setValue(dateElement, "color", value);
	}

	public void setDateFontFamily(String value) {
		setValue(dateElement, "family", value);
	}

	public void setDateFontSize(String value) {
		setValue(dateElement, "size", value);
	}

	public void setDateFontWeight(String value) {
		setValue(dateElement, "weight", value);
	}

	public void setDateFontStyle(String value) {
		setValue(dateElement, "style", value);
	}

	public void setDateTextAlignment(String value) {
		setValue(dateElement, "TextAlignment", value);
	}

	public void setBaseTableWidth(String value) {
		setValue(baseTableElement, "Width", value);
	}

	public void setBaseTableCellSpacing(String value) {
		setValue(baseTableElement, "CellSpacing", value);
	}

	public void setBaseTableCellPadding(String value) {
		setValue(baseTableElement, "CellPadding",value);
	}

	public void setBaseTableBackgroundColor(String value) {
		setValue(baseTableElement, "BackgroundColor", value);
	}
	
	public void setLogoImageUploadUrl(String value) {
		setValue(logoElement, "LogoImageUploadUrl", value);
	}
	
	public String getLogoImageUploadUrl() {
		return getValue(logoElement, "LogoImageUploadUrl");
	}
	
	public void setUseUploadedImg(String value) {
		 setValue(logoElement, "UseUploadedImg", value);
	}
	
	public String getUseUploadedImg() {
		return getValue(logoElement, "UseUploadedImg");
	}
}
