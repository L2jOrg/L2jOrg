package npc.model.residences.castle;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.CastleSiegeInfoPacket;
import l2s.gameserver.templates.npc.NpcTemplate;

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