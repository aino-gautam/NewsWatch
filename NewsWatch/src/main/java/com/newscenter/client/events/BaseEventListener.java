package com.newscenter.client.events;

import java.util.EventListener;

public interface BaseEventListener<T extends BaseEvent >  extends EventListener {
	public boolean onEvent(T evt);
}
