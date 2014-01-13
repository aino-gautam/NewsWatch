package com.lighthouse.group.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.lighthouse.group.client.GroupItemStore;
import com.lighthouse.group.client.GroupNewsStore;
import com.lighthouse.group.client.criteria.GroupPageCriteria;
import com.lighthouse.group.client.domain.Group;
import com.lighthouse.group.client.event.GroupEventManager;
import com.lighthouse.login.user.client.domain.LhUser;
import com.lighthouse.login.user.client.domain.LhUserPermission;
import com.lighthouse.main.client.LhMain;
import com.lighthouse.main.client.ui.LhNewsPresenter;
import com.lighthouse.main.client.ui.LhTagPresenter;
import com.login.client.UserInformation;
import com.newscenter.client.criteria.PageCriteria;

/**
 * This class holds an instance of the NewsCenterMain for a group
 * @author Nairutee & Prachi
 *
 */
public class GroupPresenter extends Composite {
	
	private VerticalPanel baseVp;
	private Group group;
	private LhTagPresenter lhTagPresenter;
	private LhNewsPresenter lhNewsPresenter;
	private GroupItemStore groupItemStore;
	private GroupNewsStore groupNewsStore;
	private GroupPageCriteria criteria ;
	int newsFilterMode = 0;
	int subscriptionMode = 0;
	private boolean isInitialized;
	private LhUser lhUser;
	
	public GroupPresenter(){
		groupItemStore = GroupItemStore.getInstance();
		groupNewsStore = GroupNewsStore.getInstance();
		groupItemStore.setGroup(group);
		groupItemStore.setParentGroupPresenter(this);
		groupNewsStore.setGroup(group);
		
		baseVp = new VerticalPanel();
		baseVp.setWidth("100%");
		initWidget(baseVp);
		initialize();
	}
	
	/**
	 * Constructor
	 * @group - Group object associated with this instance of the GroupPresenter
	 */
	public GroupPresenter(Group group, LhUser lhUser){
		setGroup(group);
		this.lhUser = lhUser;
		criteria = new GroupPageCriteria(group.getGroupId());
		criteria.setPageSize(15);
		criteria.setStartIndex(0);
		groupItemStore = GroupItemStore.getInstance(group.getGroupId());
		groupNewsStore = GroupNewsStore.getInstance(group.getGroupId());
		groupItemStore.setGroup(group);
		groupItemStore.setParentGroupPresenter(this);
		groupNewsStore.setGroup(group);
		
       	baseVp = new VerticalPanel();
		baseVp.setWidth("100%");
		initWidget(baseVp);
		initialize();
	}
	
	public void initialize(){
		groupItemStore.setCriteria(criteria);
		groupNewsStore.setCriteria(criteria);
		setInitialized(false);
	}
	/**
	 * creates the group presenter ui
	 */
	public void createUI(){
		setInitialized(true);
		baseVp.clear();
		lhTagPresenter = new LhTagPresenter(this, lhUser);
		lhNewsPresenter = new LhNewsPresenter(this, lhUser);
		
		lhNewsPresenter.setGroupCriteria(criteria);
		lhTagPresenter.setGroupFullModeCriteria(criteria);
		
		lhNewsPresenter.setSubscriptionMode(lhUser.getIsSubscribed());
		lhNewsPresenter.setNewsMode(group.getNewsFilterMode());
		lhTagPresenter.setNewsMode(group.getNewsFilterMode());
		
		
		groupItemStore.setNewsmode(group.getNewsFilterMode());
		groupNewsStore.setNewsmode(group.getNewsFilterMode());
		
		groupItemStore.initialize();
		DOM.setStyleAttribute(lhTagPresenter.getElement(), "marginBottom", "2px");
		baseVp.add(lhTagPresenter);
		baseVp.add(lhNewsPresenter);
	}

	public void createTagPresenterUI(){
		baseVp.clear();
		lhTagPresenter = new LhTagPresenter(this, lhUser);
		lhNewsPresenter = new LhNewsPresenter(this, lhUser);
	
		lhNewsPresenter.setGroupCriteria(criteria);
		lhTagPresenter.setGroupFullModeCriteria(criteria);
		lhNewsPresenter.setSubscriptionMode(lhUser.getIsSubscribed());
		
		lhNewsPresenter.setNewsMode(group.getNewsFilterMode());
		groupItemStore.setNewsmode(group.getNewsFilterMode());
		groupNewsStore.setNewsmode(group.getNewsFilterMode());
		
		groupItemStore.initialize();
		baseVp.add(lhTagPresenter);
		baseVp.add(lhNewsPresenter);
	}
	
	/**
	 * used to reset the tag / news presenter to any particular state. currently used to close the tagpresenter
	 */
	public void resetUI(){
		lhTagPresenter.close();
	}
	
	public void resetSize(int width, int height){
		lhTagPresenter.resetSize(width, height);
		lhNewsPresenter.resetSize(width, height);
	}
	
	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public GroupItemStore getGroupItemStore() {
		return groupItemStore;
	}

	public void setGroupItemStore(GroupItemStore groupItemStore) {
		this.groupItemStore = groupItemStore;
	}

	public GroupNewsStore getGroupNewsStore() {
		return groupNewsStore;
	}


	public void setGroupNewsStore(GroupNewsStore groupNewsStore) {
		this.groupNewsStore = groupNewsStore;
	}

	public int getNewsFilterMode() {
		return newsFilterMode;
	}

	public void setNewsFilterMode(int newsFilterMode) {
		this.newsFilterMode = newsFilterMode;
	}

	public int getSubscriptionMode() {
		return subscriptionMode;
	}

	public void setSubscriptionMode(int subscriptionMode) {
		this.subscriptionMode = subscriptionMode;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	public GroupPageCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(GroupPageCriteria criteria) {
		this.criteria = criteria;
	}

	/*public LhMain getLhMainRef() {
		return lhMainRef;
	}*/

	/*public void setLhMainRef(LhMain lhMainRef) {
		this.lhMainRef = lhMainRef;
	}
*/
}
