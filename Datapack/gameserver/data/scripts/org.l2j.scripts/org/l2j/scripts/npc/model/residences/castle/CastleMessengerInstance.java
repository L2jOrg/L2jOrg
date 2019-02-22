package org.l2j.scripts.npc.model.residences.castle;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.s2c.CastleSiegeInfoPacket;
import org.l2j.gameserver.templates.npc.NpcTemplate;

public class CastleMessengerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public CastleMessengerInstance(int objectID, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectID, template, set);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		Castle castle = getCastle();

		if(player.isCastleLord(castle.getId()))
		{
			if(castle.getSiegeEvent() != null && castle.getSiegeEvent().isInProgress())
				showChatWindow(player, "residence2/castle/sir_tyron021.htm", firstTalk);
			else
				showChatWindow(player, "residence2/castle/sir_tyron007.htm", firstTalk);
		}
		else if(castle.getSiegeEvent() != null && castle.getSiegeEvent().isInProgress())
			showChatWindow(player, "residence2/castle/sir_tyron021.htm", firstTalk);
		else
			player.sendPacket(new CastleSiegeInfoPacket(castle, player));
	}
}