package com.lighthouse.feed.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.feed.client.service.FeedService;
import com.lighthouse.feed.client.service.FeedServiceAsync;
import com.newscenter.client.news.NewsItems;
/**
 * For delete link panel 
 * @author kiran@ensarm.com
 *
 */
public class DeleteLinkPanel extends Composite implements ClickHandler{
	private Anchor delete = new Anchor("Delete");
	private Image deleteImage = new Image("images/feed/delete.gif");
	private HorizontalPanel mainPanel= new HorizontalPanel();
	private NewsItems  newsItems;
	private DecoratedPopupPanel popupPanel =new DecoratedPopupPanel();
	private Button btnYes= new Button("Yes");
	private Button btnNo= new Button("No");
	private Label label = new Label("Are you sure to delete News Item?");
	private HorizontalPanel horizontalPanel = new HorizontalPanel();
	private VerticalPanel verticalPanel = new VerticalPanel();
	private FeedServiceAsync serviceAsync = (FeedServiceAsync)GWT.create(FeedService.class);
	
	public DeleteLinkPanel(NewsItems newsItems) {
		this.newsItems = newsItems;
		mainPanel.add(deleteImage);
		mainPanel.add(delete);
		delete.addClickHandler(this);
		btnYes.addClickHandler(this);
		btnNo.addClickHandler(this);
		DOM.setStyleAttribute(mainPanel.getElement(), "margin", "0px 3px");
		DOM.setStyleAttribute(deleteImage.getElement(), "marginRight", "2px");
		verticalPanel.add(label);
		horizontalPanel.add(btnYes);
		horizontalPanel.add(btnNo);
		verticalPanel.add(horizontalPanel);
		popupPanel.add(verticalPanel);
		initWidget(mainPanel);
		
	}

	@Override
	public void onClick(ClickEvent event) {
		Widget sender =(Widget) event.getSource();
		if(sender instanceof Anchor){
			if(sender.equals(delete)){
				popupPanel.setPopupPosition(mainPanel.getAbsoluteLeft(), mainPanel.getAbsoluteTop()+10);		
				popupPanel.show();
				
				
			}
		}else if(sender instanceof Button){
			if(sender.equals(btnYes)){
				serviceAsync.deleteFeedNews((long) newsItems.getNewsId(), new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(Boolean result) {
					if(result){
						removeDeletedNewsWidget();
						popupPanel.hide();
					}
					
				}
			});
			}else if(sender.equals(btnNo)){
				popupPanel.hide();
			}
		}
		
	}
	
	
	public Widget removeDeletedNewsWidget(){
		try{
				Element td = DOM.getParent(this.getElement());
				Node node=td.getParentNode().getParentNode().getParentNode().getParentNode();
				Node node1=node.getParentNode().getParentNode().getParentNode().getParentNode();
				Node node2=node1.getParentNode().getParentNode().getParentNode().getParentNode();
				node2.removeFromParent();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
}
}
