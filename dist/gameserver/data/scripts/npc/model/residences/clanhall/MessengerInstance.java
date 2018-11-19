package npc.model.residences.clanhall;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.s2c.CastleSiegeInfoPacket;
import l2s.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 15:54/07.05.2011
 * 35420
 */
public class MessengerInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	private String _siegeDialog;
	private String _ownerDialog;

	public MessengerInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);

		_siegeDialog = template.getAIParams().getString("siege_dialog");
		_ownerDialog = template.getAIParams().getString("owner_dialog");
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		ClanHall clanHall = getClanHall();
		ClanHallSiegeEvent siegeEvent = clanHall.getSiegeEvent();
		if(clanHall.getOwner() != null && clanHall.getOwner() == player.getClan())
			showChatWindow(player, _ownerDialog, firstTalk);
		else if(siegeEvent.isInProgress())
			showChatWindow(player, _siegeDialog, firstTalk);
		else
			player.sendPacket(new CastleSiegeInfoPacket(clanHall, player));
	}
}
