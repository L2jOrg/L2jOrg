package org.l2j.gameserver.listener.actor.player.impl;

import org.l2j.commons.lang.reference.HardReference;
import org.l2j.gameserver.data.xml.holder.EventHolder;
import org.l2j.gameserver.listener.actor.player.OnAnswerListener;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.EventType;
import org.l2j.gameserver.model.entity.events.impl.PvPEvent;

public class EventAnswerListner implements OnAnswerListener
{
	private final HardReference<Player> _playerRef;
	private final int _eventId;

	public EventAnswerListner(Player player, int eventId)
	{
		_playerRef = player.getRef();
		_eventId = eventId;
	}

	@Override
	public void sayYes()
	{
		Player player = _playerRef.get();
		if(player == null)
			return;

		PvPEvent event = EventHolder.getInstance().getEvent(EventType.CUSTOM_PVP_EVENT, _eventId);
		if(event != null && event.isRegActive())
			event.reg(player);
	}

	@Override
	public void sayNo()
	{
		//
	}
}