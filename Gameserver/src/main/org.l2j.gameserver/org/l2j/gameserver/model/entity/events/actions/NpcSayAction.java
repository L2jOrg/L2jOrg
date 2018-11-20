package org.l2j.gameserver.model.entity.events.actions;

import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.entity.events.EventAction;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.ChatType;
import org.l2j.gameserver.network.l2.components.NpcString;
import org.l2j.gameserver.network.l2.s2c.NSPacket;

/**
 * @author VISTALL
 * @date  21:44/10.12.2010
 */
public class NpcSayAction implements EventAction
{
	private int _npcId;
	private int _range;
	private ChatType _chatType;
	private NpcString _text;

	public NpcSayAction(int npcId, int range, ChatType type, NpcString string)
	{
		_npcId = npcId;
		_range = range;
		_chatType = type;
		_text = string;
	}

	@Override
	public void call(Event event)
	{
		NpcInstance npc = GameObjectsStorage.getByNpcId(_npcId);
		if(npc == null)
			return;

		for(Player player : World.getAroundObservers(npc))
			if(_range <= 0 || player.isInRangeZ(npc, _range))
				packet(npc, player);
	}

	private void packet(NpcInstance npc, Player player)
	{
		player.sendPacket(new NSPacket(npc, _chatType, _text));
	}
}