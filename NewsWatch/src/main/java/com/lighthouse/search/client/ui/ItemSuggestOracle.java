package com.lighthouse.search.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.lighthouse.search.client.service.SearchService;
import com.lighthouse.search.client.service.SearchServiceAsync;

	public class ItemSuggestOracle extends SuggestOracle {
 	
	       public boolean isDisplayStringHTML() {
	           return true;
	       }
	   	static SearchServiceAsync async = GWT.create(SearchService.class);

	       public void requestSuggestions(SuggestOracle.Request req,SuggestOracle.Callback callback) {
	    		   async.getSearchSuggestList(req, new ItemSuggestCallback(req, callback));
	    	   }

	       class ItemSuggestCallback implements AsyncCallback {

	           private SuggestOracle.Request req;
	           private SuggestOracle.Callback callback;

	           public ItemSuggestCallback(SuggestOracle.Request _req,
	                   SuggestOracle.Callback _callback) {
	               req = _req;
	               callback = _callback;
	           }

	           public void onFailure(Throwable error) {
	               callback.onSuggestionsReady(req, new SuggestOracle.Response());
	           }

	           public void onSuccess(Object retValue) {
	               callback.onSuggestionsReady(req,
	                       (SuggestOracle.Response) retValue);
	           }
	       }
	   }

