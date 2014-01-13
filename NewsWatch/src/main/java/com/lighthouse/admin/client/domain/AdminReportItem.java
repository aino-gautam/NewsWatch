package com.lighthouse.admin.client.domain;

import java.util.ArrayList;

import com.admin.client.NewsItems;
import com.admin.client.TagItemInformation;

/**
 * This is a POJO class for ReportItem for admin
 * 
 * @author Milind Bharambe
 *
 */

public class AdminReportItem extends NewsItems {

	private String reportContent;
	private String reportMimeType;
	private String reportLink;
	private String reportLifeSpan;
	private String isReport;
	private ArrayList<TagItemInformation> associatedTagList = new ArrayList<TagItemInformation>();

	public ArrayList<TagItemInformation> getAssociatedTagList() {
		return associatedTagList;
	}

	public void setAssociatedTagList(ArrayList<TagItemInformation> associatedTagList) {
		this.associatedTagList = associatedTagList;
	}
	
	public void addTagforNews(TagItemInformation tagitem){
		associatedTagList.add(tagitem);
	}
	
	public void removeTagforNews(TagItemInformation tagitem){
		associatedTagList.remove(tagitem);
	}

	public String getReportContent() {
		return reportContent;
	}

	public void setReportContent(String reportContent) {
		this.reportContent = reportContent;
	}

	public String getReportMimeType() {
		return reportMimeType;
	}

	public void setReportMimeType(String reportMimeType) {
		this.reportMimeType = reportMimeType;
	}

	public String getReportLink() {
		return reportLink;
	}

	public void setReportLink(String reportLink) {
		this.reportLink = reportLink;
	}

	public String getReportLifeSpan() {
		return reportLifeSpan;
	}

	public void setReportLifeSpan(String reportLifeSpan) {
		this.reportLifeSpan = reportLifeSpan;
	}

	public String getIsReport() {
		return isReport;
	}

	public void setIsReport(String string) {
		this.isReport = string;
	}
}
