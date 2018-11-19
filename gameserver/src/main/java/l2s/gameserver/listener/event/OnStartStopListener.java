package l2s.gameserver.listener.event;

import l2s.gameserver.listener.EventListener;
import l2s.gameserver.model.entity.events.Event;

/**
 * @author VISTALL
 * @date 7:18/10.06.2011
 */
public interface OnStartStopListener extends EventListener
{
	void onStart(Event event);

	void onStop(Event event);
}