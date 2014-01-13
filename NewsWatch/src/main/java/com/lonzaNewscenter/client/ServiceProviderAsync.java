package com.lonzaNewscenter.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ServiceProviderAsync {
	public void getInformationFromSession(AsyncCallback  callback);

	public void validateUser(AsyncCallback  callback);

	public void getisadminInformation(AsyncCallback  callback);
	
	public void getUserInformation(AsyncCallback  callback);
}