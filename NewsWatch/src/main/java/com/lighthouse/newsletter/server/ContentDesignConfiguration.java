package com.lighthouse.newsletter.server;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class ContentDesignConfiguration extends NewsLetterDesignConfiguration {

	Element headlineElement;
	Element newstitleElement;
	Element abstractElement;
	Element publishedDateElement;
	Element sourceElement;
	Element tagsElement;
	Element tableElement;
	Element firstColumnElement;
	Element secondColumnElement;
	Element pulseElement;
	Element favoritesElement;
	Element reportElement;
	
	
	public ContentDesignConfiguration(String xml) {
		super(xml);
		NodeList headlineNameNodeList = xmlDom.getElementsByTagName("headline");
		headlineElement = (Element) headlineNameNodeList.item(0);
	
		NodeList newsTitleNameNodeList = xmlDom.getElementsByTagName("newstitle");
		newstitleElement = (Element) newsTitleNameNodeList.item(0);
		
		NodeList abstractNameNodeList = xmlDom.getElementsByTagName("abstract");
		abstractElement = (Element) abstractNameNodeList.item(0);
		
		NodeList publishedDateNameNodeList = xmlDom.getElementsByTagName("publishedDate");
		publishedDateElement = (Element) publishedDateNameNodeList.item(0);
		
		NodeList sourceNameNodeList = xmlDom.getElementsByTagName("source");
		sourceElement = (Element) sourceNameNodeList.item(0);
		
		NodeList tagsNameNodeList = xmlDom.getElementsByTagName("tags");
		tagsElement = (Element) tagsNameNodeList.item(0);
		
		NodeList tableNameNodeList = xmlDom.getElementsByTagName("table");
		tableElement = (Element) tableNameNodeList.item(0);
		
		NodeList firstColumnNameNodeList = xmlDom.getElementsByTagName("firstColumn");
		firstColumnElement = (Element) firstColumnNameNodeList.item(0);
		
		NodeList secondColumnNameNodeList = xmlDom.getElementsByTagName("secondColumn");
		secondColumnElement = (Element) secondColumnNameNodeList.item(0);
		
		NodeList pulseNameNodeList = xmlDom.getElementsByTagName("pulse");
		pulseElement = (Element) pulseNameNodeList.item(0);
		
		NodeList favoritesNameNodeList = xmlDom.getElementsByTagName("favorites");
		favoritesElement = (Element) favoritesNameNodeList.item(0);
		
		NodeList reportNameNodeList = xmlDom.getElementsByTagName("report");
		reportElement = (Element) reportNameNodeList.item(0);
	}
		
	public String getHeadlineFontColor() {
		return getValue(headlineElement, "color");
	}
	
	public String getHeadlineFontFamily() {
		return getValue(headlineElement, "family");
	}
	
	public String getHeadlineFontSize() {
		return getValue(headlineElement, "size");
	}
	
	public String getHeadlineFontWeight() {
		return getValue(headlineElement, "weight");
	}
	
	public String getHeadlineFontStyle() {
		return getValue(headlineElement, "style");
	}
	
	public String getHeadlinePadding() {
		return getValue(headlineElement, "padding");
	}
	
	public String getNewsTitleFontColor() {
		return getValue(newstitleElement, "color");
	}
	
	public String getNewsTitleFontFamily() {
		return getValue(newstitleElement, "family");
	}
	
	public String getNewsTitleFontSize() {
		return getValue(newstitleElement, "size");
	}
	
	public String getNewsTitleFontWeight() {
		return getValue(newstitleElement, "weight");
	}
	
	public String getNewsTitleFontStyle() {
		return getValue(newstitleElement, "style");
	}
	
	public String getNewsTitlePadding() {
		return getValue(newstitleElement, "padding");
	}

	public String getAbstractFontColor() {
		return getValue(abstractElement, "color");
	}
	
	public String getAbstractFontFamily() {
		return getValue(abstractElement, "family");
	}
	
	public String getAbstractFontSize() {
		return getValue(abstractElement, "size");
	}
	
	public String getAbstractFontWeight() {
		return getValue(abstractElement, "weight");
	}
	
	public String getAbstractFontStyle() {
		return getValue(abstractElement, "style");
	}
	
	public String getAbstractPadding() {
		return getValue(abstractElement, "padding");
	}
	
	public String getPublishedDateFontColor() {
		return getValue(publishedDateElement, "color");
	}
	
	public String getPublishedDateFontFamily() {
		return getValue(publishedDateElement, "family");
	}
	
	public String getPublishedDateFontSize() {
		return getValue(publishedDateElement, "size");
	}
	
	public String getPublishedDateFontWeight() {
		return getValue(publishedDateElement, "weight");
	}
	
	public String getPublishedDateFontStyle() {
		return getValue(publishedDateElement, "style");
	}
	
	public String getPublishedDateEnabledOrNot() {
		return getValue(publishedDateElement, "enableOrNot");
	}

	public String getSourceFontColor() {
		return getValue(sourceElement, "color");
	}
	
	public String getSourceFontFamily() {
		return getValue(sourceElement, "family");
	}
	
	public String getSourceFontSize() {
		return getValue(sourceElement, "size");
	}
	
	public String getSourceFontWeight() {
		return getValue(sourceElement, "weight");
	}
	
	public String getSourceFontStyle() {
		return getValue(sourceElement, "style");
	}
	
	public String getSourceEnabledOrNot() {
		return getValue(sourceElement, "enableOrNot");
	}
		
	public String getTagsFontColor() {
		return getValue(tagsElement, "color");
	}
	
	public String getTagsFontFamily() {
		return getValue(tagsElement, "family");
	}
	
	public String getTagsFontSize() {
		return getValue(tagsElement, "size");
	}
	
	public String getTagsFontWeight() {
		return getValue(tagsElement, "weight");
	}
	
	public String getTagsFontStyle() {
		return getValue(tagsElement, "style");
	}
	
	public String getTagsEnabledOrNot() {
		return getValue(tagsElement, "enableOrNot");
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
	
	
	public String getFirstColumnPadding() {
		return getValue(firstColumnElement, "padding");
	}
	
	public String getFirstColumnBorder() {
		return getValue(firstColumnElement, "border");
	}
	
	public String getFirstColumnAlign() {
		return getValue(firstColumnElement, "align");
	}
	
	public String getFirstColumnVerticalAlign() {
		return getValue(firstColumnElement, "valign");
	}
	
	public String getFirstColumnWidth() {
		return getValue(firstColumnElement, "width");
	}

	public String getSecondColumnPadding() {
		return getValue(secondColumnElement, "padding");
	}
	
	public String getSecondColumnBorder() {
		return getValue(secondColumnElement, "border");
	}
	
	public String getSecondColumnAlign() {
		return getValue(secondColumnElement, "align");
	}
	
	public String getSecondColumnVerticalAlign() {
		return getValue(secondColumnElement, "valign");
	}
	
	public String getSecondColumnWidth() {
		return getValue(secondColumnElement, "width");
	}
	
	public String getPulseBackgroundColor() {
		return getValue(pulseElement, "backgroundColor");
	}
	
	public String getPulseHeaderColor() {
		return getValue(pulseElement, "headerColor");
	}
	
	public String getPulseItemColor() {
		return getValue(pulseElement, "itemColor");
	}
	
	public String getPulseHeaderHorizontalRulerColor() {
		return getValue(pulseElement, "headerHorizontalRulerColor");
	}
	

	public String getFavouritesBackgroundColor() {
		return getValue(favoritesElement, "backgroundColor");
	}
	
	public String getFavouritesHeaderColor() {
		return getValue(favoritesElement, "headerColor");
	}
	
	public String getFavouritesItemColor() {
		return getValue(favoritesElement, "itemColor");
	}
	
	public String getFavouritesHeaderHorizontalRulerColor() {
		return getValue(favoritesElement, "headerHorizontalRulerColor");
	}
	
	public void setFavouritesBackgroundColor(String value) {
		setValue(favoritesElement, "backgroundColor", value);
	}
	
	public void setFavouritesHeaderColor(String value) {
		setValue(favoritesElement, "headerColor", value);
	}
	
	public void setFavouritesItemColor(String value) {
		setValue(favoritesElement, "itemColor", value);
	}
	
	public void setFavouritesHeaderHorizontalRulerColor(String value) {
		setValue(favoritesElement, "headerHorizontalRulerColor", value);
	}
	
	public String getReportBackgroundColor() {
		return getValue(reportElement, "backgroundColor");
	}
	
	public String getReportHeaderColor() {
		return getValue(reportElement, "headerColor");
	}
	
	public String getReportItemColor() {
		return getValue(reportElement, "itemColor");
	}
	
	public String getReportHeaderHorizontalRulerColor() {
		return getValue(reportElement, "headerHorizontalRulerColor");
	}
	
	public void setReportBackgroundColor(String value) {
		setValue(reportElement, "backgroundColor", value);
	}
	
	public void setReportHeaderColor(String value) {
		setValue(reportElement, "headerColor", value);
	}
	
	public void setReportItemColor(String value) {
		setValue(reportElement, "itemColor", value);
	}
	
	public void setReportHeaderHorizontalRulerColor(String value) {
		setValue(reportElement, "headerHorizontalRulerColor", value);
	}

	public void setPulseBackgroundColor(String value) {
		setValue(pulseElement, "backgroundColor", value);
	}
	
	public void setPulseHeaderColor(String value) {
		setValue(pulseElement, "headerColor", value);
	}
	
	public void setPulseItemColor(String value) {
		setValue(pulseElement, "itemColor", value);
	}
	
	public void setPulseHeaderHorizontalRulerColor(String value) {
		setValue(pulseElement, "headerHorizontalRulerColor", value);
	}

	public void setSecondColumnPadding(String value) {
		setValue(secondColumnElement, "padding", value);
	}
	
	public void setSecondColumnBorder(String value) {
		setValue(secondColumnElement, "border", value);
	}
	
	public void setSecondColumnAlign(String value) {
		setValue(secondColumnElement, "align", value);
	}
	
	public void setSecondColumnVerticalAlign(String value) {
		setValue(secondColumnElement, "valign", value);
	}
	
	public void setSecondColumnWidth(String value) {
		setValue(secondColumnElement, "width", value);
	}
	
	public void setFirstColumnPadding(String value) {
		setValue(firstColumnElement, "padding", value);
	}
	
	public void setFirstColumnBorder(String value) {
		setValue(firstColumnElement, "border", value);
	}
	
	public void setFirstColumnAlign(String value) {
		setValue(firstColumnElement, "align", value);
	}
	
	public void setFirstColumnVerticalAlign(String value) {
		setValue(firstColumnElement, "valign", value);
	}
	
	public void setFirstColumnWidth(String value) {
		setValue(firstColumnElement, "width", value);
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

	public void setTagsFontColor(String value) {
		setValue(tagsElement, "color", value);
	}
	
	public void setTagsFontFamily(String value) {
		setValue(tagsElement, "family", value);
	}
	
	public void setTagsFontSize(String value) {
		setValue(tagsElement, "size", value);
	}
	
	public void setTagsFontWeight(String value) {
		setValue(tagsElement, "weight",value);
	}
	
	public void setTagsFontStyle(String value) {
		setValue(tagsElement, "style", value);
	}
	
	public void setTagsEnabledOrNot(String value) {
		setValue(tagsElement, "enableOrNot", value);
	}

	public void setSourceFontColor(String value) {
		setValue(sourceElement, "color", value);
	}
	
	public void setSourceFontFamily(String value) {
		setValue(sourceElement, "family", value);
	}
	
	public void setSourceFontSize(String value) {
		setValue(sourceElement, "size", value);
	}
	
	public void setSourceFontWeight(String value) {
		setValue(sourceElement, "weight",value);
	}
	
	public void setSourceFontStyle(String value) {
		setValue(sourceElement, "style", value);
	}
	
	public void setSourceEnabledOrNot(String value) {
		setValue(sourceElement, "enableOrNot", value);
	}

	public void setPublishedDateFontColor(String value) {
		setValue(publishedDateElement, "color", value);
	}
	
	public void setPublishedDateFontFamily(String value) {
		setValue(publishedDateElement, "family", value);
	}
	
	public void setPublishedDateFontSize(String value) {
		setValue(publishedDateElement, "size", value);
	}
	
	public void setPublishedDateFontWeight(String value) {
		setValue(publishedDateElement, "weight",value);
	}
	
	public void setPublishedDateFontStyle(String value) {
		setValue(publishedDateElement, "style", value);
	}
	
	public void setPublishedDateEnabledOrNot(String value) {
		setValue(publishedDateElement, "enableOrNot", value);
	}
	
	public void setAbstractFontColor(String value) {
		setValue(abstractElement, "color", value);
	}
	
	public void setAbstractFontFamily(String value) {
		setValue(abstractElement, "family", value);
	}
	
	public void setAbstractFontSize(String value) {
		setValue(abstractElement, "size", value);
	}
	
	public void setAbstractFontWeight(String value) {
		setValue(abstractElement, "weight",value);
	}
	
	public void setAbstractFontStyle(String value) {
		setValue(abstractElement, "style", value);
	}
	public void setAbstractPadding(String value) {
		setValue(abstractElement, "padding", value);
	}

	public void setNewsTitleFontColor(String value) {
		setValue(newstitleElement, "color", value);
	}
	
	public void setNewsTitleFontFamily(String value) {
		setValue(newstitleElement, "family", value);
	}
	
	public void setNewsTitleFontSize(String value) {
		setValue(newstitleElement, "size", value);
	}
	
	public void setNewsTitleFontWeight(String value) {
		setValue(newstitleElement, "weight",value);
	}
	
	public void setNewsTitleFontStyle(String value) {
		setValue(newstitleElement, "style", value);
	}
	public void setNewsTitlePadding(String value) {
		setValue(newstitleElement, "padding", value);
	}

	public void setHeadlineFontColor(String value) {
		setValue(headlineElement, "color", value);
	}
	
	public void setHeadlineFontFamily(String value) {
		setValue(headlineElement, "family", value);
	}
	
	public void setHeadlineFontSize(String value) {
		setValue(headlineElement, "size", value);
	}
	
	public void setHeadlineFontWeight(String value) {
		setValue(headlineElement, "weight",value);
	}
	
	public void setHeadlineFontStyle(String value) {
		setValue(headlineElement, "style", value);
	}
	public void setHeadlinePadding(String value) {
		setValue(headlineElement, "padding", value);
	}

}
