package com.lighthouse.admin.client.siloui;

import com.google.gwt.xml.client.DOMException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;

public class NewsLetterDesignConfiguration {
	Document xmlDom = null;
	Element documentElement = null;

	public NewsLetterDesignConfiguration(String xml) {
		constructDom(xml);
	}

	public void constructDom(String xml) {
		try {
			if (xml == null) {
				xmlDom = XMLParser.createDocument();
				documentElement = xmlDom.createElement("Design");
			} else {
				xmlDom = XMLParser.parse(xml);
				documentElement = xmlDom.getDocumentElement();
			}
		} catch (DOMException e) {
			e.printStackTrace();
		}
	}

	public String getDomAsString() {
		String xml = null;
		try {
			xml = documentElement.toString();
			String headerDeclaration = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			if (!xml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
				headerDeclaration = headerDeclaration.concat(xml);
				return headerDeclaration;
			}
		} catch (DOMException e) {
			e.printStackTrace();
		}
		return xml;
	}

	protected String getValue(Element nodeListName, String valueToExtract) {
		NodeList nodeList = nodeListName.getElementsByTagName(valueToExtract);
		Element node = (Element) nodeList.item(0);
		String value = "";
		if (node.hasChildNodes()) {
			value = node.getFirstChild().getNodeValue();
		}
		return value;
	}

	protected void setValue(Element nodeListName, String setValueFor,
			String value) {
		NodeList nodeList = nodeListName.getElementsByTagName(setValueFor);
		Element node = (Element) nodeList.item(0);
		if (node.hasChildNodes()) {
			node.getFirstChild().setNodeValue(value);
		} else {
			Text text = xmlDom.createTextNode(value);
			node.appendChild(text);
		}
	}

}
