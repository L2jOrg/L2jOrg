package npc.model.residences.clanhall;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import org.l2j.gameserver.model.entity.residence.ClanHall;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.s2c.CastleSiegeInfoPacket;
import org.l2j.gameserver.templates.npc.NpcTemplate;

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
