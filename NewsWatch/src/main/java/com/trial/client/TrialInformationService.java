package com.trial.client;

import java.util.ArrayList;
import java.util.HashMap;

import com.common.client.PageResult;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.login.client.UserInformation;
import com.newscenter.client.criteria.PageCriteria;

@RemoteServiceRelativePath("trialinfoservice")
public interface TrialInformationService extends RemoteService {

	public HashMap<Integer,String> getCatalystList();
	public UserInformation getuserinformation();
	public boolean createTrialAccount(TrialAccount user,String mailformat,String subject); 
	public PageResult getTrialAccounts(PageCriteria crt);
	public PageResult getSortTrialAccounts(PageCriteria crt,String columname,String mode);
	public PageResult getSearchData(PageCriteria crt,String columname,String searchString);
	public void updateTrialDuration(ArrayList<TrialAccount> trialaccountlist);
	public void deleteTrialAccount(TrialAccount trialaccount);
	public void reinitiateTrialAccount(TrialAccount trialaccount);
}
