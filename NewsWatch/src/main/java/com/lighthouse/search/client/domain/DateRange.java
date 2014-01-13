package com.lighthouse.search.client.domain;

import java.io.Serializable;

import java.sql.Date;

import com.google.gwt.i18n.client.DateTimeFormat;

public class DateRange implements Serializable{
	
	Date fromDate;
	Date toDate;
	
	public DateRange(){
		
	}
	
	public DateRange(Date fromDate, Date toDate) {
		this.fromDate = fromDate;
		this.toDate = toDate;
	}
	
	public Date getFromDate() {
		return fromDate;
	}
	
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	
	public Date getToDate() {
		return toDate;
	}
	
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date convertStrToSql(String strDate){
		Date convertedDate = null;
		try {
			if (strDate.contains("-")) {
				DateTimeFormat dateFormatSql = DateTimeFormat
						.getFormat("yyyy-MM-dd");
				java.util.Date date2 = dateFormatSql.parse(strDate);
				convertedDate = new java.sql.Date(date2.getTime());
			} else {
				DateTimeFormat dateFormatUtil = DateTimeFormat
						.getFormat("yyyy MM dd hh:mm:ss");
				java.util.Date date2 = dateFormatUtil.parse(strDate);
				convertedDate = new java.sql.Date(date2.getTime());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}
	
}
