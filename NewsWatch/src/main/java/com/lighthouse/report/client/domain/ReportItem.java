package com.lighthouse.report.client.domain;



import java.sql.Blob;

import com.newscenter.client.news.NewsItems;

public class ReportItem extends NewsItems {

	private String reportContent;
	private String reportMimeType;
	private String reportLink;
	private String reportLifeSpan;
	private String isReport;
	//private byte[] reportContent;

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

/*	public void setReportContent(byte[] reportContent) {
		this.reportContent = reportContent;
	}

	public byte[] getReportContent() {
		return reportContent;
	}
*/
}
