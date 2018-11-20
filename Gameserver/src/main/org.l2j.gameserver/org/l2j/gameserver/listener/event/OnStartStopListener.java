package org.l2j.gameserver.listener.event;

import org.l2j.gameserver.listener.EventListener;
import org.l2j.gameserver.model.entity.events.Event;

/**
 * @author VISTALL
 * @date 7:18/10.06.2011
 */
public interface OnStartStopListener extends EventListener
{
	void onStart(Event event);

	void onStop(Event event);
}