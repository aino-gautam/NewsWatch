package com.lighthouse.feed.client.ui.base;

import com.appUtils.client.PopupWidget;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.lighthouse.feed.client.ui.ReverseCompositeView;
import com.lighthouse.feed.client.ui.base.IBaseMainView.IBaseMainPresenter;
import com.lighthouse.feed.client.ui.body.BodyView;
import com.lighthouse.feed.client.ui.footer.FooterView;
import com.lighthouse.feed.client.ui.header.HeaderView;
import com.lighthouse.login.user.client.domain.LhUser;
/**
 * 
 * @author kiran@ensarm.com
 *
 */
public class BaseMainView extends ReverseCompositeView<IBaseMainPresenter> implements IBaseMainView{

	private Widget headerView,bodyView,footerView;
	private VerticalPanel mainPanel= new VerticalPanel();
	private LhUser lhUser;
	private String keyVal="";
    private String keyCombination="18171669";
	
	@Inject
	public BaseMainView(HeaderView headerView,BodyView bodyView,FooterView footerView) {
		this.headerView=headerView;
		this.bodyView = bodyView;
		this.footerView =footerView;
		initWidget(mainPanel);
	}
	
	public void createUI(){
		 RootPanel.get().addDomHandler(new KeyDownHandler() {
				
				@Override
				public void onKeyDown(KeyDownEvent event) {
					int key = event.getNativeEvent().getKeyCode();
					keyVal=keyVal+key;
								
					if(!keyCombination.startsWith(keyVal))
						keyVal="";
				
					if(keyVal.equalsIgnoreCase(keyCombination)){
						if(event.isControlKeyDown()&&event.isAltKeyDown()&&event.isShiftKeyDown()&&key==69){
							PopupWidget popupWidget=new PopupWidget();
							//popupWidget.setMessage("This site is developed by");
							keyVal="";
						}else{
							keyVal="";
						}
					}							
				}
			},KeyDownEvent.getType());
		
		mainPanel.add(headerView);
		mainPanel.add(bodyView);
		mainPanel.add(footerView);
		mainPanel.setSpacing(10);
		mainPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		mainPanel.setStylePrimaryName("NCMainBasePanel");
		((HeaderView)headerView).setLoggedInUser(getLhUser().getFirstname(), getLhUser().getLastname());
		calculateWidth();
	}
	
	@Override
	public void setBody(IsWidget body) {
		this.mainPanel.add( body );
	}
	
	public void calculateWidth(){
		int width = Window.getClientWidth()-50;
		mainPanel.setPixelSize(width, 0);
	}
	
	public LhUser getLhUser() {
		return lhUser;
	}
	
	public void setLhUser(LhUser lhUser) {
		this.lhUser = lhUser;
	}

}
