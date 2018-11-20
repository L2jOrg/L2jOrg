package org.l2j.gameserver.model.entity.events.actions;

import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.EventAction;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.network.l2.components.ChatType;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.network.l2.components.SysString;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;
import org.l2j.gameserver.network.l2.s2c.SayPacket2;

/**
 * @author VISTALL
 * @date 22:25/05.01.2011
 */
public class SayAction implements EventAction
{
	private int _range;
	private ChatType _chatType;

	private String _how;
	private NpcString _text;

	private SysString _sysString;
	private SystemMsg _systemMsg;

	protected SayAction(int range, ChatType type)
	{
		_range = range;
		_chatType = type;
	}

	public SayAction(int range, ChatType type, SysString sysString, SystemMsg systemMsg)
	{
		this(range, type);
		_sysString = sysString;
		_systemMsg = systemMsg;
	}

	public SayAction(int range, ChatType type, String how, NpcString string)
	{
		this(range, type);
		_text = string;
		_how = how;
	}

	@Override
	public void call(Event event)
	{
		List<Player> players = event.broadcastPlayers(_range);
		for(Player player : players)
			packet(player);
	}

	private void packet(Player player)
	{
		if(player == null)
			return;

		L2GameServerPacket packet = null;
		if(_sysString != null)
			packet = new SayPacket2(0, _chatType, _sysString, _systemMsg);
		else
			packet = new SayPacket2(0, _chatType, _how, _text);

		player.sendPacket(packet);
	}
}