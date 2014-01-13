package com.lighthouse.search.client.ui;

import com.google.gwt.user.client.rpc.IsSerializable;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

	
	public class ItemSuggestion implements IsSerializable, Suggestion {

	      private String str;
	      // Required for IsSerializable to work
	      public ItemSuggestion() {
	      }

	      // Convenience method for creation of a suggestion
	      public ItemSuggestion(String str) {
	         this.str = str;
	      }

	      public String getDisplayString() {
	          return str;
	      }

	      public String getReplacementString() {
	          return str;
	      }
	   }
