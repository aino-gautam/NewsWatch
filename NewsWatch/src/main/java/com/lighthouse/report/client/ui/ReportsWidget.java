package com.lighthouse.report.client.ui;

import java.util.Date;

import com.appUtils.client.exception.FeatureNotSupportedException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.FormElement;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.report.client.domain.ReportItem;
import com.lighthouse.report.client.domain.ReportItemList;
import com.lighthouse.report.client.service.ReportsService;
import com.lighthouse.report.client.service.ReportsServiceAsync;

public class ReportsWidget extends Composite {

	private Label reportHeader = new Label("Reports");
	private FlexTable reportFlex = new FlexTable();
	private HorizontalPanel hpHeader = new HorizontalPanel();
	private ReportItemList reportList = new ReportItemList();
	private Image newImage;
	private VerticalPanel reportHolder = new VerticalPanel();
	private VerticalPanel reportContainer = new VerticalPanel();

	public ReportsWidget(Group group, int newsmode, GroupPageCriteria criteria, LhUserPermission lhUserPermission) throws FeatureNotSupportedException {
		if (lhUserPermission.isReportsPermitted() == 1) {
			getAllReports(group, newsmode, criteria);
			initWidget(reportContainer);
		} else
			throw new FeatureNotSupportedException("Reports not supported");
	}

	/**
	 * creates the UI
	 */
	private void createUI() {
		reportHeader.setStylePrimaryName("ReportsHeaderLabel");
		hpHeader.add(reportHeader);
		hpHeader.setCellHorizontalAlignment(reportHeader, HorizontalPanel.ALIGN_LEFT);
		hpHeader.setWidth("100%");

		hpHeader.setStylePrimaryName("reportsHeaderPanel");

		reportContainer.add(hpHeader);
		reportContainer.add(reportHolder);

		reportContainer.setStylePrimaryName("reportBorder");
		reportContainer.setCellHorizontalAlignment(hpHeader,
				HorizontalPanel.ALIGN_LEFT);

		reportContainer.setCellHorizontalAlignment(reportHolder,
				HorizontalPanel.ALIGN_LEFT);

		reportContainer.setWidth("100%");
	}

	/**
	 * gets a list of all reports
	 * 
	 * @param group
	 * @param newsmode
	 * @param criteria
	 */
	private void getAllReports(Group group, int newsmode,
			GroupPageCriteria criteria) {
		ReportsServiceAsync async = GWT.create(ReportsService.class);
		async.getAllReports(group, newsmode, criteria,
				new AsyncCallback<ReportItemList>() {

					@Override
					public void onSuccess(ReportItemList result) {
						int row = 1, col = 0;
						createUI();
						if (result.isEmpty()) {
							Label noReports = new Label("No reports");
							noReports.setStylePrimaryName("reportLink");
							reportHolder.add(noReports);

						} else {
							reportList = result;
							for (ReportItem reportItem : reportList) {
								String format = reportItem.getReportMimeType();
								ReportTitle reportTitle = new ReportTitle(
										reportItem.getNewsTitle(), Integer
												.toString(reportItem
														.getNewsId()),
										reportItem.getUrl());

								Image image = null;
								if (format.matches("pdf")) {
									image = new Image("images/pdf_grey.png");
								} else if (format.matches("ppt")
										|| format.matches("pptx")) {
									image = new Image("images/ppt_grey.png");

								} else if (format.matches("gif")
										|| format.matches("jpg")
										|| format.matches("jpeg")) {
									image = new Image("images/image_grey.png");

								} else if (format.matches("xls")) {
									image = new Image("images/excel_grey.png");

								} else if (format.matches("doc")
										|| format.matches("docx")) {
									image = new Image(
											"images/document_grey.png");

								} else if (format.matches("unknown")) {
									image = new Image(
											"images/download_grey.png");
								} else {
									image = new Image(
											"images/download_grey.png");
								}

								ImageDownload imageDownlaod = new ImageDownload(
										image, Integer.toString(reportItem
												.getNewsId()));

								Label reportLbL = reportTitle.getTitleLbl();
								Image downldImg = imageDownlaod.getImage();

								addLabelHandler(reportTitle);

								addImageHandler(imageDownlaod);

								reportFlex.setWidget(row, col, reportLbL);

								col++;
								reportFlex.setWidget(row, col, downldImg);
								col++;
								if (isNewReportItem(reportItem)) {
									newImage = new Image(
											"images/new_yellow.png");
									reportFlex.setWidget(row, col, newImage);
								}
								row++;
								col = 0;
							}
							reportHolder.add(reportFlex);
						}
					}

					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();

					}
				});

	}

	private void addImageHandler(final ImageDownload downldImg) {
		Image image = downldImg.getImage();

		image.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int reportId = Integer.parseInt(downldImg.getId());
				Window.open(GWT.getModuleBaseURL() +"DownloadReport?reportId=" + reportId, "", "");
				 
			}
		});

	}

	private void addLabelHandler(final ReportTitle reportTitle) {
		final HTML titleLabel = reportTitle.getTitleLbl();

		titleLabel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				int reportId = Integer.parseInt(reportTitle.getId());
				String url = reportTitle.getUrl();

				if (url.startsWith("http://")) {
					Window.open(url, "_blank", "");
				}
				else if (url.startsWith("https://")) {
					Window.open(url, "_blank", "");
				}
				else {
					Window.open("http://" + url, "_blank", "");
				}
				String urlServlet = GWT.getModuleBaseURL()
						+ "itemView?newsItemId=" + reportId;
				RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
						URL.encode(urlServlet));

				try {
					Request request = builder.sendRequest(null,
							new RequestCallback() {

								@Override
								public void onResponseReceived(Request request,
										Response response) {
								}

								@Override
								public void onError(Request request,
										Throwable exception) {
									exception.printStackTrace();

								}

							});
				} catch (RequestException e) {
					e.printStackTrace();
				}

			}
		});

	}

	/**
	 * gets a list of all reports
	 */
	private void getAllReports() {
		ReportsServiceAsync async = GWT.create(ReportsService.class);
		async.getAllReports(new AsyncCallback<ReportItemList>() {

			@Override
			public void onSuccess(ReportItemList result) {
				int row = 1, col = 0;
				createUI();
				if (result.isEmpty()) {
					Label noReports = new Label("No reports");
					noReports.setStylePrimaryName("reportLink");
					reportHolder.add(noReports);

				} else {
					reportList = result;
					for (ReportItem reportItem : reportList) {
						String format = reportItem.getReportMimeType();
						ReportTitle reportTitle = new ReportTitle(reportItem
								.getNewsTitle(), Integer.toString(reportItem
								.getNewsId()), reportItem.getUrl());

						Image image = null;
						if (format.matches("pdf")) {
							image = new Image("images/pdf_grey.png");
						} else if (format.matches("ppt")
								|| format.matches("pptx")) {
							image = new Image("images/ppt_grey.png");

						} else if (format.matches("gif")
								|| format.matches("jpg")
								|| format.matches("jpeg")) {
							image = new Image("images/image_grey.png");

						} else if (format.matches("xls")) {
							image = new Image("images/excel_grey.png");

						} else if (format.matches("doc")
								|| format.matches("docx")) {
							image = new Image("images/document_grey.png");

						} else if (format.matches("unknown")) {
							image = new Image("images/download_grey.png");
						} else {
							image = new Image("images/download_grey.png");
						}

						ImageDownload imageDownlaod = new ImageDownload(image,
								Integer.toString(reportItem.getNewsId()));

						Label reportLbL = reportTitle.getTitleLbl();
						Image downldImg = imageDownlaod.getImage();

						addLabelHandler(reportTitle);

						addImageHandler(imageDownlaod);

						reportFlex.setWidget(row, col, reportLbL);

						col++;
						reportFlex.setWidget(row, col, downldImg);
						col++;
						if (isNewReportItem(reportItem)) {
							newImage = new Image("images/new_yellow.png");
							reportFlex.setWidget(row, col, newImage);
						}
						row++;
						col = 0;
					}
					reportHolder.add(reportFlex);
				}
			}

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

			}
		});
	}

	private Boolean isNewReportItem(ReportItem reportItem) {
		Boolean result = false;
		String reportDate = reportItem.getNewsDate();
		DateTimeFormat dateFormatSql = DateTimeFormat.getFormat("yyyy-MM-dd");
		Date convertedDate = dateFormatSql.parse(reportDate);
		Date todayDate = new Date();

		int defaultNewTime = (24 * 60 * 60 * 1000) * 3;
		if ((todayDate.getTime() - convertedDate.getTime()) <= defaultNewTime)
			result = true;
		return result;
	}
}
