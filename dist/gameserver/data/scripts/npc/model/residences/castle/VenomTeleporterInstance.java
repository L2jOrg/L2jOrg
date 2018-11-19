package npc.model.residences.castle;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.residence.Castle;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 21:58/23.05.2011
 * 35506
 */
public class VenomTeleporterInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public VenomTeleporterInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		Castle castle = getCastle();
		if(castle.getSiegeEvent().isInProgress())
			showChatWindow(player, "residence2/castle/rune_massymore_teleporter002.htm", false);
		else
			player.teleToLocation(12589, -49044, -3008);
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		showChatWindow(player, "residence2/castle/rune_massymore_teleporter001.htm", firstTalk);
	}
}
