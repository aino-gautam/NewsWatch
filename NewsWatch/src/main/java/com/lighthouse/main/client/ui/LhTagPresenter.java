package com.lighthouse.main.client.ui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.lighthouse.group.client.GroupItemStore;
import com.lighthouse.group.client.GroupManager;
import com.lighthouse.group.client.GroupNewsStore;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.ui.GroupPresenter;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.main.client.LhMain;
import com.login.client.UserInformation;
import com.newscenter.client.ItemStore;
import com.newscenter.client.NewsStore;
import com.newscenter.client.criteria.PageCriteria;
import com.newscenter.client.events.AppEventManager;
import com.newscenter.client.events.NewsEvent;
import com.newscenter.client.events.TagEvent;
import com.newscenter.client.events.TagEventListener;
import com.newscenter.client.tags.CategoryItem;
import com.newscenter.client.tags.CategoryMap;
import com.newscenter.client.ui.CategoryView;
import com.newscenter.client.ui.CheckBoxComponent;
import com.newscenter.client.ui.ItemTagLabel;
import com.newscenter.client.ui.SnippetView;

/**
 * 
 * @author prachi@ensarm.com
 * 
 */
public class LhTagPresenter extends Composite implements ClickHandler,TagEventListener /*OpenHandler<DisclosurePanel>,CloseHandler<DisclosurePanel> */{

	private HorizontalPanel basePanel = new HorizontalPanel();
	private VerticalPanel mainBasePanel = new VerticalPanel();
	private ItemTagLabel clearLbl = new ItemTagLabel();
	private Label filterSettings = new Label(); // new label added for filter settings
	private PopupPanel popup = new PopupPanel(true);
	private HorizontalPanel backPanel = new HorizontalPanel();
	private Label backLabel = new Label("Back to latest stories");
	private HorizontalPanel tagDisclosureHeader = new HorizontalPanel();
	private DisclosurePanel tagDisclosure = new DisclosurePanel();
	private int displayedSnippetsNumber;
	public HashMap<CategoryItem, SnippetView> visibleSnippetMap = new HashMap<CategoryItem, SnippetView>();
	public static HashMap<CategoryItem, SnippetView> snippetMap = new HashMap<CategoryItem, SnippetView>();
	private Label lbSelectTags = new Label("Select Tags");
	private VerticalPanel vp;
	private int tagDisclosureWidth;
	private static ArrayList categoryList;
	public static HashMap<Integer, CategoryItem> allCategoriesMap = new HashMap<Integer, CategoryItem>();
	private GroupPresenter parent;
	private int newsMode;
	public static final int OR = 0;
	public static final int AND = 1;
	private CategoryMap map = null;
	private Image backImg = new Image("images/back_icon.png");
	private Image separatorImg = new Image("images/verticalSeparator.JPG",0,0,6,13);
	private PageCriteria fullModeCriteria = new PageCriteria();
    private GroupPageCriteria groupFullModeCriteria = new GroupPageCriteria();
    HandlerRegistration handlerRegistrationForCriteria ;
	private LhUser lhUser;
  
    
    /**
	 * Constructor
	 * @param parentGroupPresenter
	 */
	public LhTagPresenter(GroupPresenter parentGroupPresenter, LhUser lhUser) {
		this.parent = parentGroupPresenter;
		this.lhUser = lhUser;
		calculateSnippetNumber();
		lbSelectTags.setStylePrimaryName("headerLabels");
		basePanel.setSpacing(0);
		tagDisclosureHeader.setStylePrimaryName("tagDisclosureHeader");
		tagDisclosureHeader.setWidth("100%");
		tagDisclosure.setHeader(tagDisclosureHeader);

		AppEventManager.getInstance().addTagEventListener(this);
            
		if(tagDisclosure.isOpen())
			tagDisclosure.setOpen(false);
		mainBasePanel.add(basePanel);
		basePanel.add(backPanel);
		
		int isMandatory = parent.getGroup().getIsMandatory();
		int isAdmin = lhUser.getIsAdmin();
		if(isAdmin == 1 && isMandatory == 1)
			isMandatory = 0;
		
		
		if(isMandatory == 0)
		{   
			clearLbl.setText("CLEAR ALL");
			clearLbl.setClicked(false);
			clearLbl.setStylePrimaryName("clearAllLabelStyle");
			clearLbl.setTitle("Clears all selections");
			
			filterSettings.setText("Change Filter Criteria");
			filterSettings.addClickHandler(this);
			filterSettings.setStylePrimaryName("changeCriteriaLabelStyle");
			filterSettings.setTitle("Manage news filter criteria");
	        
		}
		else if(isMandatory == 1)
		{
			filterSettings.removeFromParent();
			filterSettings.removeStyleDependentName("changeCriteriaLabelStyle");
			clearLbl.removeFromParent();
		}
		popup.setStylePrimaryName("newsletterPopup");
		popup.setAnimationEnabled(true);
		popup.setTitle("Click outside to close");
		
		backPanel.add(backImg);
		backPanel.add(separatorImg);
		backPanel.add(backLabel);
		backLabel.setVisible(false);
		separatorImg.setVisible(false);
		backLabel.setTitle("View all news stories");
		
		mainBasePanel.add(clearLbl);
		mainBasePanel.add(filterSettings);
		mainBasePanel.setWidth("100%");
		basePanel.setWidth("100%");
		
		mainBasePanel.setCellHorizontalAlignment(filterSettings,HasHorizontalAlignment.ALIGN_LEFT);
		mainBasePanel.setCellHorizontalAlignment(clearLbl,HasHorizontalAlignment.ALIGN_RIGHT);
		
		backPanel.setCellVerticalAlignment(backLabel, HasVerticalAlignment.ALIGN_MIDDLE);
		backPanel.setCellVerticalAlignment(separatorImg, HasVerticalAlignment.ALIGN_MIDDLE);
		
		tagDisclosure.setContent(mainBasePanel);
		tagDisclosure.setAnimationEnabled(true);
		initWidget(tagDisclosure);
	}

	public void setTagPresenterWidth(int width, int height) {
		tagDisclosure.setPixelSize(width, height);
	}

	public void resetSize(int width, int height) {
		tagDisclosure.setPixelSize(width, height);
		setTagDisclosureWidth(width);
		System.out.println("On resize tagpresenter width= " + width);
		if (width > 1400) {
			refresh();
		}
	}

     public void refresh() {
		int isMandatory  = parent.getGroup().getIsMandatory();
		
		int isAdmin = lhUser.getIsAdmin();
		if(isAdmin == 1 && isMandatory == 1)
			isMandatory = 0;
		
		categoryList = parent.getGroupItemStore().getVisibleCategories();
		int count = 0;
		int ind = 1;
		Iterator itt = categoryList.iterator();
		while (itt.hasNext()) {
			CategoryItem citem = (CategoryItem)itt.next();
			if (citem.isSelected()) {
				count++;
			}
			allCategoriesMap.put(ind, citem);
			ind++;
		}
		basePanel.clear();
		visibleSnippetMap.clear();
		snippetMap.clear();
		calculateSnippetNumber();
		SnippetView snip = null;
		int snippetsToDisplay = getDisplayedSnippetsNumber();
		int indx = 0;
		Iterator iter = categoryList.iterator();
		if (count >= snippetsToDisplay) {
				while (iter.hasNext()) {
				CategoryItem catitem = (CategoryItem) iter.next();
				if (catitem.isSelected()) {
					if (indx < snippetsToDisplay) {
						snip = new SnippetView(isMandatory);
						catitem.setSelected(true); 
						//catitem.setDirty(true);
						snip.setCategory(catitem);
						snip.setCategoryList(categoryList);
						snip.createSnippet();
			
						visibleSnippetMap.put(catitem, snip);
						snippetMap.put(catitem, snip);
						basePanel.add(snip);
						
						if (indx != snippetsToDisplay - 1) {
							vp = new VerticalPanel();
							vp.setHeight("240px");
							vp.setWidth("1px");
							vp.add(new Image("images/longsnippetseparator.gif"));
							vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
							basePanel.add(vp);
							basePanel.setCellHorizontalAlignment(vp,HasHorizontalAlignment.ALIGN_LEFT);
						}
						indx++;
					} else {
						break;
					}
				}
			}
		} else {
			while (iter.hasNext()) {

				CategoryItem catitem = (CategoryItem) iter.next();
				//indx = catitem.getIndexId();
				if (indx < snippetsToDisplay) {
					snip = new SnippetView(isMandatory);
					catitem.setSelected(true); 
					//catitem.setDirty(true);
					snip.setCategory(catitem);
					snip.setCategoryList(categoryList);
					snip.createSnippet();
					visibleSnippetMap.put(catitem, snip);
					snippetMap.put(catitem, snip);
					basePanel.add(snip);
					if (indx != snippetsToDisplay - 1) {
						vp = new VerticalPanel();
						vp.setHeight("240px");
						vp.setWidth("1px");
						vp.add(new Image("images/longsnippetseparator.gif"));
						vp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
						basePanel.add(vp);
						basePanel.setCellHorizontalAlignment(vp,HasHorizontalAlignment.ALIGN_LEFT);
					}
					indx++;
					
				} else {
					break;
				}
			}
		}
		if (tagDisclosure.isOpen()) {
			tagDisclosure.setOpen(false);
		}
	}
	
	
	private void calculateSnippetNumber() {
		if(Window.getClientWidth() > 1400) {
			setDisplayedSnippetsNumber(5);
		}else{
			setDisplayedSnippetsNumber(4);
		}
	}

	public int getDisplayedSnippetsNumber() {
		return displayedSnippetsNumber;
	}

	public void setDisplayedSnippetsNumber(int displayedSnippetsNumber) {
		this.displayedSnippetsNumber = displayedSnippetsNumber;
	}
    

	@Override
	public boolean onEvent(TagEvent evt) {
		int evttype = evt.getEventType();

		switch(evttype){
		case (TagEvent.TAGSAVAILABLE):
		{
			if(parent == evt.getSource()){
				map = (CategoryMap)evt.getEventData();
				refresh();
			
				GroupItemStore.getInstance(parent.getGroup().getGroupId()).updateSessionCategoryMap();
				return true;
			}
			return false;
		}
		case (TagEvent.DOWNCATEGORYCHANGED): {
			if(parent == GroupManager.getActiveGroupPresenter()){ 
				if (categoryList.size() > getDisplayedSnippetsNumber()) {
					CategoryItem categoryItem = (CategoryItem) evt.getEventData();
					SnippetView snippet = (SnippetView) evt.getSource();
					int index = categoryItem.getIndexId();
					while (index > 0) {
						if (index <= allCategoriesMap.size()) {
							CategoryItem catitem = allCategoriesMap.get(index);
							if (!snippetMap.containsKey(catitem)) {
								snippet.setCategory(catitem);
								catitem.setSelected(true);
								categoryItem.setSelected(false);
								visibleSnippetMap.remove(categoryItem);
								snippetMap.remove(categoryItem);
								snippet.createSnippet();
								visibleSnippetMap.put(catitem, snippet);
								snippetMap.put(catitem, snippet);
								break;
							} else {
								index++;
							}
						} else {
							index = 1;
						}
					}
				}
				return true;
			}
			return false;
		}
		case (TagEvent.UPCATEGORYCHANGED): {
			if(parent == GroupManager.getActiveGroupPresenter()){
				if (categoryList.size() > getDisplayedSnippetsNumber()) {
					CategoryItem categoryItem = (CategoryItem) evt.getEventData();
					SnippetView snippet = (SnippetView) evt.getSource();
					int index = categoryItem.getIndexId();
					while (index >= 0) {
						if (index >= 1) {
							CategoryItem catitem = allCategoriesMap.get(index);
							if (!snippetMap.containsKey(catitem)) {
								snippet.setCategory(catitem);
								catitem.setSelected(true);
								categoryItem.setSelected(false);
								visibleSnippetMap.remove(categoryItem);
								snippetMap.remove(categoryItem);
								snippet.createSnippet();
								visibleSnippetMap.put(catitem, snippet);
								snippetMap.put(catitem, snippet);
								break;
							} else {
								index--;
							}
						} else {
							index = allCategoriesMap.size();
						}
					}
				}
				return true;
			}
			return false;
		}
		case (TagEvent.VIEWCATEGORY):
		{
			if(parent == GroupManager.getActiveGroupPresenter()){
				tagDisclosure.setOpen(false);
				
				basePanel.clear();
				CategoryItem item = (CategoryItem)evt.getEventData();
				CategoryView catview = new CategoryView(item);
				catview.createCategoryView();
				basePanel.add(catview);
				basePanel.setCellHorizontalAlignment(catview, HasHorizontalAlignment.ALIGN_CENTER);
				tagDisclosure.setOpen(true);
			return true;
			}
			return false;
		}
		case (TagEvent.VIEWSNIPPETS):
		{
			if(parent == GroupManager.getActiveGroupPresenter()){
				tagDisclosure.setOpen(false);
				refresh();
				return true;
			}
			return false;
		}
		case (TagEvent.TAGPRESENTERMINIMIZED):
		{
			if(parent == GroupManager.getActiveGroupPresenter()){
				if(tagDisclosure.isOpen())
					tagDisclosure.setOpen(false);
				else
					tagDisclosure.setOpen(true);
				return true;
			}
			return false;
		}
		case(TagEvent.CLEARTAGS):{
			if(parent == GroupManager.getActiveGroupPresenter()){
				for(SnippetView snipView : visibleSnippetMap.values()){
					snipView.getSnippetDetail().clearTags();
				}
				return true;
			}
			if(evt.getSource() instanceof CategoryView){
				if(parent == GroupManager.getActiveGroupPresenter()){
					for(SnippetView snipView : visibleSnippetMap.values()){
						snipView.getSnippetDetail().clearTags();
					}
					return true;
				}	
			}
			return false;
		}
		}
		return false;
	}

	/*@Override
	public void onClose(CloseEvent<DisclosurePanel> event) {
		plusMinusImage.setUrl("images/plus.gif");
		//tagDisclosure.getHeader().setTitle("click to maximize");
	}

	@Override
	public void onOpen(OpenEvent<DisclosurePanel> event) {
		plusMinusImage.setUrl("images/minus.gif");
		//tagDisclosure.getHeader().setTitle("click to minimize");
	}*/

	public int getTagDisclosureWidth() {
		return tagDisclosureWidth;
	}

	public void setTagDisclosureWidth(int tagDisclosureWidth) {
		this.tagDisclosureWidth = tagDisclosureWidth;
	}
    
	private void createPopup(String msg, String cbtext1, String cbtext2, String linkclicked){
		popup.clear();
		VerticalPanel vp = new VerticalPanel();
		Label label = new Label(msg);
		CheckBox dailycb = new CheckBox(cbtext1);
		CheckBox weeklycb = new CheckBox(cbtext2);
		
		dailycb.addClickHandler(this);
		weeklycb.addClickHandler(this);
		if(linkclicked.equals("Subscribe Newsletter")){
			vp.add(label);
			vp.add(dailycb);
			vp.add(weeklycb);
		}
		else if(linkclicked.equals("Change Filter Criteria")){
			if(newsMode == LhTagPresenter.OR){
				vp.add(label);
				vp.add(weeklycb);
			}
			if(newsMode == LhTagPresenter.AND){
				vp.add(label);
				vp.add(dailycb);
			}
		}
		
		vp.setSpacing(3);
		popup.add(vp);
	}
	
	@Override
	public void onClick(ClickEvent event) {
		Widget arg0=(Widget)event.getSource();
		if(arg0 instanceof Label){
			Label label = (Label)arg0;
			if(label.getText().equals("Change Filter Criteria")){
				popup.clear();
				VerticalPanel vp = new VerticalPanel();
				vp.setSpacing(8);
				
				int left = arg0.getAbsoluteLeft() - 70;
		        int top = arg0.getAbsoluteTop() + 20;
		        
		        popup.add(vp);
		        popup.setPopupPosition(left, top);
		        popup.show();     
				
		        if(getNewsMode() == 0)
					 createPopup("Your current news filter mode is OR. Would you like to change to: ","OR","AND",label.getText()); 
					else
					 createPopup("Your current news filter mode is AND. Would you like to change to: ","OR","AND",label.getText());	
				}
			}
		else if(arg0 instanceof CheckBox){
			CheckBox cb = (CheckBox)arg0;
			String name = cb.getText();
			if(name.equals("OR")){
				setNewsMode(LhTagPresenter.OR);
				getGroupFullModeCriteria().setStartIndex(0);
				
				
				GroupNewsStore.getInstance().setNewsmode(LhTagPresenter.OR);
				GroupItemStore.getInstance().setNewsmode(LhTagPresenter.OR);
				
				if(backImg.isVisible())
					backImg.setVisible(false);
				if(backLabel.isVisible())
					backLabel.setVisible(false);
				if(separatorImg.isVisible())
					separatorImg.setVisible(false);
				saveNewsFilterModePreference(name);
			}
			else if(name.equals("AND")){
				setNewsMode(LhTagPresenter.AND);
				getGroupFullModeCriteria().setStartIndex(0);
				
			    
				GroupNewsStore.getInstance().setNewsmode(LhTagPresenter.AND);
				GroupNewsStore.getInstance().setGroupFullModeCriteria(getGroupFullModeCriteria());
				GroupItemStore.getInstance().setNewsmode(LhTagPresenter.AND);
				
				if(backImg.isVisible())
					backImg.setVisible(false);
				if(backLabel.isVisible())
					backLabel.setVisible(false);
				if(separatorImg.isVisible())
					separatorImg.setVisible(false);
				saveNewsFilterModePreference(name);
			}
			
		}
	    }

	public void close(){
		tagDisclosure.setOpen(false);
	}
	
	public int getNewsMode() {
		return newsMode;
	}

	public void setNewsMode(int newsMode) {
		this.newsMode = newsMode;
	}

	public PageCriteria getFullModeCriteria() {
		return fullModeCriteria;
	}

	public void setFullModeCriteria(PageCriteria fullModeCriteria) {
		this.fullModeCriteria = fullModeCriteria;
	}
   
	public void saveNewsFilterModePreference(String choice){
		GroupNewsStore.getInstance().saveNewsFilterModPreference(choice,getNewsMode(), popup);
	}

	public GroupPageCriteria getGroupFullModeCriteria() {
		return groupFullModeCriteria;
	}

	public void setGroupFullModeCriteria(GroupPageCriteria groupFullModeCriteria) {
		this.groupFullModeCriteria = groupFullModeCriteria;
	}


}
