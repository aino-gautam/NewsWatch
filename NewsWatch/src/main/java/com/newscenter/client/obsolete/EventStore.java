package com.newscenter.client.obsolete;

import java.util.HashSet;

import com.newscenter.client.obsolete.ItemSelectionEventListener;

public class EventStore 
{
	private static HashSet<ItemSelectionEventListener> itemSelectionEventListeners = new HashSet<ItemSelectionEventListener>();

	public HashSet<ItemSelectionEventListener> getItemSelectionEventListener() {
		return itemSelectionEventListeners;
	}

	public void setItemSelectionEventListener(
			ItemSelectionEventListener itemSelectionEventListener) {
		this.itemSelectionEventListeners.add(itemSelectionEventListener);
	}
}
