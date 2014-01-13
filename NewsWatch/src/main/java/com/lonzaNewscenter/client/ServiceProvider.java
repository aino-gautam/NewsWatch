package com.lonzaNewscenter.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("lonzaNewscenter")
public interface ServiceProvider extends RemoteService {
	public int getInformationFromSession();

	public boolean validateUser();

	public int getisadminInformation();
	
	public ArrayList getUserInformation();
}
