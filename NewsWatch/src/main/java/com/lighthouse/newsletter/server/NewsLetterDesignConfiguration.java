package com.lighthouse.newsletter.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

public class NewsLetterDesignConfiguration {
	Element documentElement = null;
	Document xmlDom = null;

	public NewsLetterDesignConfiguration(String xml) {
		constructDom(xml);
	}

	public void constructDom(String xml) {
		System.out.println(xml);
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			if (xml == null) {
				xmlDom = db.newDocument();
				documentElement = xmlDom.createElement("Design");
			} else {
				xmlDom = db.parse(new ByteArrayInputStream(xml.getBytes()));
				xmlDom.getDocumentElement().normalize();
				documentElement = xmlDom.getDocumentElement();
			}
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDomAsString() {
		String xml = null;
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			//initialize StreamResult with File object to save to file
			StreamResult result = new StreamResult(new StringWriter());
			DOMSource source = new DOMSource(xmlDom);
			transformer.transform(source, result);

			xml = result.getWriter().toString();
			//System.out.println(xml);
			//xml = documentElement.toString();
		} catch (Exception e) {
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
