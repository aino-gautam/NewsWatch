package com.lighthouse.search.client.ui;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

import com.lighthouse.main.client.LhMain;
import com.lighthouse.search.client.domain.DateRange;

public class AdvanceSearchPopup extends PopupPanel {

	DateBox fromDate = new DateBox();
	DateBox toDate = new DateBox();
	Label fromDateLbl = new Label(" Start Date:");
	Label toDateLbl = new Label(" End Date:");
	Button dateSearch = new Button("Date Search");
	Label lblError = new Label("Please select date");
	Label lblheader = new Label("Please select date range for search:  ");
	HorizontalPanel hpBtn = new HorizontalPanel();

	String searchString = null;

	VerticalPanel popupContainer = new VerticalPanel();

	HorizontalPanel dateBoxContainer = new HorizontalPanel();
	HorizontalPanel headerPanel = new HorizontalPanel();

	VerticalPanel basevp = new VerticalPanel();

	private LhMain lhMainRef;

	public AdvanceSearchPopup(String text) {
		setAnimationEnabled(true);
		setAutoHideEnabled(true);

		this.setStylePrimaryName("searchPopup");
		searchString = text;
		dateSearch.setStylePrimaryName("roundedButton");
		dateBoxContainer.setSpacing(5);

		fromDate.setStylePrimaryName("lhDatePicker");
		toDate.setStylePrimaryName("lhDatePicker");
		lblError.setStylePrimaryName("SearchErrorLabel");
		lblError.setVisible(false);
		HorizontalPanel hpFromDate = new HorizontalPanel();
		HorizontalPanel hpToDate = new HorizontalPanel();

		headerPanel.add(lblheader);
		headerPanel.setWidth("100%");
		headerPanel.setCellHorizontalAlignment(lblheader,
				HorizontalPanel.ALIGN_LEFT);

		popupContainer.add(headerPanel);
		hpFromDate.add(fromDateLbl);
		hpFromDate.add(fromDate);
		hpToDate.add(toDateLbl);
		hpToDate.add(toDate);
		// dateSearch.setWidth("100%");

		hpBtn.add(lblError);
		hpBtn.add(dateSearch);

		hpBtn.setCellHorizontalAlignment(lblError, HorizontalPanel.ALIGN_LEFT);
		hpBtn.setCellHorizontalAlignment(dateSearch,
				HorizontalPanel.ALIGN_RIGHT);

		clickHandler(dateSearch);
		dateBoxContainer.add(hpFromDate);

		dateBoxContainer.add(hpToDate);
		popupContainer.add(dateBoxContainer);
		hpBtn.setWidth("100%");
		popupContainer.add(hpBtn);

		popupContainer.setCellHorizontalAlignment(headerPanel,
				HorizontalPanel.ALIGN_LEFT);
		popupContainer.setCellHorizontalAlignment(dateBoxContainer,
				HorizontalPanel.ALIGN_CENTER);
		popupContainer.setCellHorizontalAlignment(hpBtn,
				HorizontalPanel.ALIGN_RIGHT);

		valueChangedHandler(fromDate);
		valueChangedHandler(toDate);

		basevp.add(popupContainer);
		add(basevp);
	}

	private void clickHandler(Button baseBtn) {
		baseBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (event.getSource().equals(dateSearch)) {

					
					// removeStyleName("searchPopup");
					/*
					 * hide(); popupContainer.setVisible(false);
					 */
					// lblError.setVisible(false);
					try {
						String startDateStr = fromDate.getTextBox().getText();
						String toDateStr = toDate.getTextBox().getText();

						if ((!startDateStr.matches(""))
								&& (!toDateStr.matches(""))) {
							lblError.setVisible(false);
							removeStyleName("searchPopup");
							hide();
							popupContainer.setVisible(false);
							java.sql.Date convertFromDate = getConvertedSqlDate(startDateStr);
							java.sql.Date convertedToDate = getConvertedSqlDate(toDateStr);

							DateRange range = new DateRange();
							range.setFromDate(convertFromDate);
							range.setToDate(convertedToDate);

							if (!searchString.equals("")) {
								String textSearch = searchString;
								String dateString = range.getFromDate() + "TO"
										+ range.getToDate();

								getLhMainRef().getResultPresenter()
										.performSearch(textSearch, dateString);
								getLhMainRef().showDeckWidget(1);
							} else {

								String dateString = range.getFromDate() + "TO"
										+ range.getToDate();

								getLhMainRef().getResultPresenter()
										.performSearch(null, dateString);
								getLhMainRef().showDeckWidget(1);

							}
						} else {
							lblError.setVisible(true);
							//fromDate.getTextBox().setText("");
							//toDate.getTextBox().setText("");
						}
					} catch (Exception e) {
						if (e.getClass().equals(NullPointerException.class)) {
							lblError.setVisible(true);
							 fromDate.getTextBox().setText("");
							 toDate.getTextBox().setText("");
						}
						e.printStackTrace();
					}
				}

			}
		});

	}

	private java.sql.Date getConvertedSqlDate(String strDate) {
		java.sql.Date convertedDate = null;
		try {
			if (strDate.contains("-")) {
				DateTimeFormat dateFormatSql = DateTimeFormat
						.getFormat("yyyy-MM-dd");
				Date date2 = dateFormatSql.parse(strDate);
				convertedDate = new java.sql.Date(date2.getTime());
			} else {
				DateTimeFormat dateFormatUtil = DateTimeFormat
						.getFormat("yyyy MM dd hh:mm:ss");
				Date date2 = dateFormatUtil.parse(strDate);
				convertedDate = new java.sql.Date(date2.getTime());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}

	private void valueChangedHandler(final DateBox baseDateBox) {
		baseDateBox.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				lblError.setVisible(false);
				Date date = event.getValue();
				java.sql.Date changedDate = new java.sql.Date(date.getTime());
				baseDateBox.getTextBox().setText(changedDate.toString());
			}
		});

	}

	public LhMain getLhMainRef() {
		return lhMainRef;
	}

	public void setLhMainRef(LhMain lhMainRef) {
		this.lhMainRef = lhMainRef;
	}
}
