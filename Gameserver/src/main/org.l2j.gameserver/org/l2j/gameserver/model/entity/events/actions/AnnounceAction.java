package org.l2j.gameserver.model.entity.events.actions;

import org.l2j.gameserver.model.entity.events.EventAction;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.network.l2.components.SystemMsg;

/**
 * @author VISTALL
 * @date 11:12/11.03.2011
 */
public class AnnounceAction implements EventAction
{
	private final SystemMsg _msgId;
	private final int _id;
	private final int _time;

	public AnnounceAction(SystemMsg msgId, int id, int time)
	{
		_msgId = msgId;
		_id = id;
		_time = time;
	}

	@Override
	public void call(Event event)
	{
		event.announce(_msgId, _id, _time);
	}
}