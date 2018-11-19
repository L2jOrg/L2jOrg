package l2s.gameserver.model.entity.events.actions;

import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.entity.events.EventAction;
import l2s.gameserver.model.entity.events.Event;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.components.NpcString;
import l2s.gameserver.network.l2.s2c.NSPacket;

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