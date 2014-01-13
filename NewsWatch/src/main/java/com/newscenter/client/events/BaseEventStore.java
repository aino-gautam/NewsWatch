package com.newscenter.client.events;

import java.util.HashSet;

/**
 * This class is a store for all the event listeners. It contains a HashSet of all those classes which want
 * to listen to the particular event corresponding to the BaseEventStore.
 *
 * @param <BE> - extends BaseEvent i.e each BaseEventStore corresponds to only one type of BaseEvent
 */
public class BaseEventStore<BE extends BaseEvent> {

	private HashSet<BaseEventListener<BE>> listeners = new HashSet<BaseEventListener<BE>>();
	
	public void addEventListener(BaseEventListener<BE> baseEvtListner){
		listeners.add(baseEvtListner);
	}
	
	public void removeEventListener(BaseEventListener<BE> baseEvtListner){
		listeners.remove(baseEvtListner);
	}
	
	/**
	 * This method iterates through the set of listeners and fires the OnEvent method on each listener class
	 * @param baseEvt - a derived type of BaseEvent
	 * @return - true if the event listener who had heard that type of event doesn't want any other
	 * event listener after it to listen to this event.Otherwise false
	 */
	public boolean fireEvent(BE baseEvt){
		try{
		for ( BaseEventListener<BE> bel : listeners ){
			if (bel.onEvent(baseEvt)) 
				return true ;
		}
		return false ;
		}
		catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
}
