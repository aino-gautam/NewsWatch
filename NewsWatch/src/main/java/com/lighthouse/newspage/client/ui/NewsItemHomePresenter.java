package com.lighthouse.newspage.client.ui;

import java.util.ArrayList;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.comment.client.CommentList;
import com.lighthouse.comment.client.CommentManager;
import com.lighthouse.comment.client.domain.ItemComment;
import com.lighthouse.comment.client.service.ItemCommentServiceAsync;
import com.lighthouse.newspage.client.domain.CommentedNewsItem;

/**
 * 
 * @author kiran@ensarm.com
 * 
 */
public class NewsItemHomePresenter extends Composite implements AsyncCallback,
		ClickHandler, FocusHandler, MouseOverHandler, MouseOutHandler {

	private VerticalPanel mainBasePanel = new VerticalPanel();
	private HorizontalPanel newsItemPanel = new HorizontalPanel();
	private VerticalPanel postCommentPanel = new VerticalPanel();
	private TextArea textArea = new TextArea();
	private Button postComment = new Button();
	private VerticalPanel no_Of_Comments = new VerticalPanel();
	private VerticalPanel list_Of_Comments = new VerticalPanel();
	private HorizontalPanel textAreaPanel = new HorizontalPanel();
	private HorizontalPanel buttonhorizontalPanel = new HorizontalPanel();
	private Image loadingImg = new Image("images/circle_loader.gif");
	private Image commentBoxImg = new Image("images/commentbox.png");
	private Label loadingMsg = new Label();
	private HorizontalPanel loaderPanel = new HorizontalPanel();
	private Image imNewsCatalyst=null;
	private HorizontalPanel header = new HorizontalPanel();
	private CommentedNewsItem commentedNewsItem = new CommentedNewsItem();
	private FlexTable flexTable = new FlexTable();
	private Label label = new Label();
	private CommentedNewsItem newsItem;
	private CommentListPresenter commentListPresenter = new CommentListPresenter();
	private Label lblClosePage = new Label("CLOSE"/*"BACK TO NEWS OVERVIEW"*/);
	private static final String INSTRUCTIONTEXT = "Leave a Comment..";
	
	private String logoPath = null;
	
	public NewsItemHomePresenter() {

	}

	public NewsItemHomePresenter(NewsItemHomeWidget itemHomeWidget,CommentedNewsItem result) {
		logoPath = result.getLogoImagePath();
		setNewsItem(result);
		commentedNewsItem = result;
		textArea.addFocusHandler(this);
		mainBasePanel.setWidth("100%");
		initWidget(mainBasePanel);
		createUI(itemHomeWidget);
		if(itemHomeWidget.getLhUser().getUserPermission().isCommentsPermitted() == 1){
			ArrayList arrayList = result.getCommentsList();
			createNewsItemCommentList(arrayList);
		}
	}

	private void createUI(NewsItemHomeWidget itemHomeWidget) {
		if(logoPath!=null){
			imNewsCatalyst = new Image(logoPath);
		}
		else{
		imNewsCatalyst = new Image("images/marketscapeLogoNewscatalyst.png");
		}

		imNewsCatalyst.setVisible(false);
		imNewsCatalyst.addLoadHandler(new LoadHandler() {
			
			@Override
			public void onLoad(LoadEvent event) {
				  Element element = (Element) event.getRelativeElement();
		            if (element == imNewsCatalyst.getElement()) {
		                int originalHeight = imNewsCatalyst.getOffsetHeight();
		                int originalWidth = imNewsCatalyst.getOffsetWidth();
		                if (originalHeight > originalWidth) {
		                	imNewsCatalyst.setStylePrimaryName("siloLogoHeight");
		                } else {
		                	imNewsCatalyst.setStylePrimaryName("siloLogoWidth");
		                }
		                
		                imNewsCatalyst.setVisible(true);
		            }
			}
		});
		
		
		header.add(imNewsCatalyst);
		header.setCellHorizontalAlignment(imNewsCatalyst,
				HasHorizontalAlignment.ALIGN_LEFT);
		lblClosePage.addClickHandler(this);
		lblClosePage.setSize("160px", "0.2em");
		DOM.setStyleAttribute(lblClosePage.getElement(), "marginRight", "-10em");
		DOM.setStyleAttribute(lblClosePage.getElement(), "marginTop", "5px");
		lblClosePage.setStylePrimaryName("lblRelatedTag");
		lblClosePage.addMouseOverHandler(this);
		lblClosePage.addMouseOutHandler(this);
		header.add(lblClosePage);
		header.setWidth("100%");
		DOM.setStyleAttribute(header.getElement(), "marginRight", "20px");
		mainBasePanel.add(header);
		
		
		newsItemPanel.add(itemHomeWidget);
		newsItemPanel.setStylePrimaryName("NewsItem");
		newsItemPanel.setWidth("100%");
		mainBasePanel.add(newsItemPanel);
		
		if(itemHomeWidget.getLhUser().getUserPermission().isCommentsPermitted() == 1){
			postCommentPanel.setWidth("73%");
			postCommentPanel.setStylePrimaryName("postCommnet-panel");
			textArea.setCursorPos(0);
			textArea.setText(INSTRUCTIONTEXT);
			textArea.setWidth("99%");
			textArea.addClickHandler(this);
			textArea.setStylePrimaryName("textArea");

			loaderPanel.setVisible(false);
			commentBoxImg.setStylePrimaryName("commentBoxImg");

			postCommentPanel.add(commentBoxImg);
			postCommentPanel.add(textArea);
			loaderPanel = loadingImage();

			postComment.setText("Post Comment");
			postComment.setWidth("26%");
			postComment.setStylePrimaryName("postCommentButton");
			postComment.addClickHandler(this);

			postCommentPanel.add(postComment);
			loaderPanel.setVisible(false);

			postCommentPanel.add(loaderPanel);

			mainBasePanel.add(postCommentPanel);

			Label label1 = new Label();

			label1.setStyleName("labelLine");
			label.setStylePrimaryName("labelfont");
			no_Of_Comments.add(label);
			no_Of_Comments.add(label1);
			mainBasePanel.add(no_Of_Comments);
		}
	}

	public void createNewsItemCommentList(ArrayList arrayList) {
		VerticalPanel commentListPanel = new VerticalPanel();
		Iterator iter = arrayList.iterator();
		int size = arrayList.size();
		label.setText(" " + size + " Comments");
		label.setStylePrimaryName("labelfont");

		commentListPresenter.createCommentListWidgetUI(arrayList);
		commentListPanel.add(commentListPresenter);
		DOM.setStyleAttribute(commentListPanel.getElement(), "marginLeft",
				"20px");

		// commentListPanel.setStylePrimaryName("textArea");
		mainBasePanel.add(commentListPanel);
	}

	@Override
	public void onClick(ClickEvent event) {

		Widget args0 = (Widget) event.getSource();
		if (args0 instanceof Button) {
			Button button = (Button) args0;
			if (button.equals(postComment)) {

				// int a = commentedNewsItem.getNewsId();
				ItemComment itemComment = new ItemComment();
				String textAreaName = textArea.getValue();
				if (textAreaName.isEmpty()) {
					textArea.setText("Please Enter the comments..");
				} else {
					loaderPanel.setVisible(true);
					itemComment.setText(textAreaName);
					itemComment.setNewsItemId(commentedNewsItem.getNewsId());
					commentedNewsItem.getCommentsList();

					try {

						CommentManager commentManager = new CommentManager();
						ItemCommentServiceAsync itemCommentService = commentManager
								.postComment();
						itemCommentService.postComment(textAreaName,
								commentedNewsItem.getNewsId(), this);

					} catch (Exception e) {
						e.printStackTrace();

					}
				}
			}
		} else if (args0 instanceof TextArea) {

		} else if (args0 instanceof Label) {

			Label label = (Label) args0;
			if (label.equals(lblClosePage)) {

				BrowserUtils.close();

			}
		}

	}

	private HorizontalPanel loadingImage() {
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.add(loadingImg);
		loadingMsg.setText("posting comment");
		horizontalPanel.add(loadingMsg);
		return horizontalPanel;

	}

	public CommentedNewsItem getNewsItem() {
		return newsItem;
	}

	public void setNewsItem(CommentedNewsItem newsItem) {
		this.newsItem = newsItem;
	}

	@Override
	public void onFailure(Throwable caught) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(Object result) {
		if (result instanceof CommentList) {
			loadingImg.removeFromParent();
			loadingMsg.removeFromParent();
			createNewsItemCommentList((ArrayList<ItemComment>) result);

		}
	}

	@Override
	public void onFocus(FocusEvent event) {
		TextArea ta = (TextArea) event.getSource();
		if (ta.getText().trim().equalsIgnoreCase(INSTRUCTIONTEXT)) {
			ta.setText("");
		}

	}

	@Override
	public void onMouseOver(MouseOverEvent event) {

		Widget widget = (Widget) event.getSource();
		if (widget instanceof Label) {
			lblClosePage.setTitle("Click here to close tab");
			lblClosePage.addStyleName("newsItemCommentsLabelHover");
		}

	}

	@Override
	public void onMouseOut(MouseOutEvent event) {
		Widget widget = (Widget) event.getSource();
		if (widget instanceof Label) {

			lblClosePage.removeStyleName("newsItemCommentsLabelHover");
		}
	}

}
