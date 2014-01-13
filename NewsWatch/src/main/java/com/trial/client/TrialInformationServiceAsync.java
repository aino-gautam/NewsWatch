package com.trial.client;


import java.util.ArrayList;
import java.util.HashMap;
import com.common.client.PageResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.login.client.UserInformation;
import com.newscenter.client.criteria.PageCriteria;

public interface TrialInformationServiceAsync {

	void getCatalystList(AsyncCallback<HashMap<Integer, String>> callback);
	void getuserinformation(AsyncCallback<UserInformation> callback);
	void createTrialAccount(TrialAccount user, String mailformat,String subject,AsyncCallback<Boolean> callback);
	void getTrialAccounts(PageCriteria crt,	AsyncCallback<PageResult> callback);
	void getSortTrialAccounts(PageCriteria crt, String columname, String mode,AsyncCallback<PageResult> callback);
	void getSearchData(PageCriteria crt, String columname, String searchString,	AsyncCallback<PageResult> callback);
	void updateTrialDuration(ArrayList<TrialAccount> trialaccountlist,AsyncCallback<Void> callback);
	void deleteTrialAccount(TrialAccount trialaccount,AsyncCallback<Void> callback);
	void reinitiateTrialAccount(TrialAccount trialaccount,AsyncCallback<Void> callback);
}
