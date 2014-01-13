package com.lighthouse.main.client.ui;

import java.util.ArrayList;

import com.appUtils.client.ValidationException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.newscenter.client.news.NewsItems;
import com.newscenter.client.tags.TagItem;

/**
 * class for designing Share Story Popup
 * 
 * @author kiran@ensarm.com
 * 
 */
public class ShareStoryPopup extends PopupPanel implements ClickHandler,
		FocusHandler {

	private VerticalPanel vpBasePanel = new VerticalPanel();
	private VerticalPanel vpPanel = new VerticalPanel();
	private HorizontalPanel buttonpanel = new HorizontalPanel();
	private HorizontalPanel labelPanel = new HorizontalPanel();
	private HorizontalPanel panel = new HorizontalPanel();
	private HorizontalPanel textAreaPanel = new HorizontalPanel();
	private FlexTable flex = new FlexTable();
	private Label label = new Label("Please enter email addresses separated by commas(,)");
	private Label labelNote = new Label("The news has been shared with the given recipients");
	private TextArea emailTextArea = new TextArea();
	private Button btnShare = new Button("Share");
	private Button btnCancel = new Button("Cancel");
	private Hidden hiddenemail = new Hidden();
	private Hidden hiddenTitle = new Hidden();
	private Hidden hiddenAbstract = new Hidden();
	private Hidden hiddenDate = new Hidden();
	private Hidden hiddenSource = new Hidden();
	private Hidden hiddenId = new Hidden();
	private Hidden hiddenurl = new Hidden();
	private Hidden txtNewsItemTags = new Hidden();
	private FormPanel form = new FormPanel();
	private Image imgLoader = new Image("images/circle_loader.gif");
	private Label lblLoading = new Label("Sending email");
	private Image closeImage = new Image("images/close-icon.jpg");
	private String newsItemTags = "";
	private static final String INSTRUCTIONTEXT = "Please enter the email addresses..";
	NewsItems newsItems;

	public ShareStoryPopup(NewsItems newsItems) {
		setNewsItems(newsItems);

		add(vpBasePanel);
		setWidth("600px");
		setAnimationEnabled(true);
		setModal(true);
		
		this.setAutoHideEnabled(true);
		labelPanel.add(label);
		
		
			/*labelPanel.add(closeImage);
			closeImage.addStyleName("clickable");
			closeImage.addClickHandler(this);*/
		
	
		label.setStylePrimaryName("popupLabel");
	    labelPanel.setWidth("100%");
		vpBasePanel.add(labelPanel);

		emailTextArea.setWidth("94%");
		textAreaPanel.setWidth("100%");
		textAreaPanel.add(emailTextArea);
		vpBasePanel.add(textAreaPanel);
		emailTextArea.addFocusHandler(this);
		buttonpanel.add(btnShare);

		hiddenemail.setName("email");
		hiddenTitle.setName("title");
		hiddenAbstract.setName("abstract");
		hiddenDate.setName("date");
		hiddenSource.setName("source");
		hiddenId.setName("newsId");
		hiddenurl.setName("url");
		txtNewsItemTags.setName("txtNewsItemTags");
		vpPanel.add(hiddenemail);
		vpPanel.add(hiddenTitle);
		vpPanel.add(hiddenAbstract);
		vpPanel.add(hiddenDate);
		vpPanel.add(hiddenSource);
		vpPanel.add(hiddenId);
		vpPanel.add(hiddenurl);
		vpPanel.add(txtNewsItemTags);
		form.add(vpPanel);
		vpBasePanel.add(form);

		btnShare.addClickHandler(this);

		vpBasePanel.add(buttonpanel);
		
		vpBasePanel.setSpacing(5);
		setStylePrimaryName("searchPopup");

	}

	@Override
	public void onClick(ClickEvent event) {

		Widget arg0 = (Widget) event.getSource();
		if (arg0 instanceof Image) {

			hide();
		}

		if (arg0 instanceof Button) {
			if (arg0 == btnShare) {
				String emailString = emailTextArea.getText();

				if (emailString.isEmpty()) {
					emailTextArea.setText("Please enter the email addresses..");
				} else {
					try {
						boolean flag = validateEmailAdress(emailString);
						if (flag == true) {
							NewsItems newsItems = getNewsItems();
							int newsId = newsItems.getNewsId();
							String id = "" + newsId;
							String title = newsItems.getNewsTitle();
							String abstractNews = newsItems.getAbstractNews();
							String date = newsItems.getNewsDate();
							String source = newsItems.getNewsSource();
							String nurl = newsItems.getUrl();
							ArrayList list = newsItems.getAssociatedTagList();
							String imageUrl=newsItems.getImageUrl();

							for (int i = 0; i < list.size(); i++) {
								TagItem item = new TagItem();
								item = (TagItem) list.get(i);
								newsItemTags = newsItemTags + ";"
										+ item.getTagName();
							}

							hiddenemail.setDefaultValue(emailString);
							hiddenAbstract.setDefaultValue(abstractNews);
							hiddenTitle.setDefaultValue(title);
							hiddenDate.setDefaultValue(date);
							hiddenSource.setDefaultValue(source);
							hiddenId.setDefaultValue(id);
							hiddenurl.setDefaultValue(nurl);
							txtNewsItemTags.setDefaultValue(newsItemTags);

							
							labelPanel.clear();
							textAreaPanel.clear();
							buttonpanel.clear();
							panel.add(labelNote);
					//		panel.add(closeImage);
							panel.setWidth("100%");
							vpBasePanel.add(panel);
							if(imageUrl.equals("")||imageUrl==null)
								imageUrl="-";
							String url = GWT.getModuleBaseURL() + "sharestory?id="+id+"&title="+title+"&abstractNews="+abstractNews+
							"&date="+date+"&source="+source+"&url="+nurl+"&newsItemTag="+newsItemTags+"&email="+emailString+"&imageUrl="+imageUrl+"";
							RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, url);
							try {
								// add code to submit the form
								/*form.setEncoding(FormPanel.ENCODING_MULTIPART);
								form.setMethod(FormPanel.METHOD_POST);
								form.setAction(url);

								form.submit();
								form.addSubmitCompleteHandler(new SubmitCompleteHandler() {

									@Override
									public void onSubmitComplete(
											SubmitCompleteEvent event) {

									}

								});*/
								 Request request = requestBuilder.sendRequest(null,new RequestCallback() {

									@Override
									public void onResponseReceived(
											Request request, Response response) {
										// TODO Auto-generated method stub
										
									}

									@Override
									public void onError(Request request,
											Throwable exception) {
										// TODO Auto-generated method stub
										
									}

								 });	
								

							} catch (Exception e) {
								e.printStackTrace();

							}
						} else {
							emailTextArea.setText("Please enter the valid email addresses..for e.g xyz@gmail.com");
						}
					} catch (ValidationException e) {
						emailTextArea.setText(e.getDisplayMessage());

					}
				}
			}
		}
	}

	/**
	 * This method for email validation for share story
	 * 
	 * @param emailString
	 * @return
	 * @throws ValidationException
	 */
	private boolean validateEmailAdress(String emailString)
			throws ValidationException {

		String[] email = emailString.split(",");

		for (int index = 0; index < email.length; index++) {
			if (!email[index]
					.matches(("[\\w-+]+(?:\\.[\\w-+]+)*@(?:[\\w-]+\\.)+[a-zA-Z]{2,7}"))
					|| email.equals("")) {
				throw new ValidationException("Invalid email address");

			} else {
				return true;
			}

		}
		return false;

	}

	public NewsItems getNewsItems() {
		return newsItems;
	}

	public void setNewsItems(NewsItems newsItems) {
		this.newsItems = newsItems;
	}

	@Override
	public void onFocus(FocusEvent event) {
		TextArea ta = (TextArea) event.getSource();
		if (ta.getText().trim().equalsIgnoreCase(INSTRUCTIONTEXT)) {
			ta.setText("");
		}
		if (ta.getText().trim().equalsIgnoreCase("Invalid email address")) {
			ta.setText("");
		}

	}

}
