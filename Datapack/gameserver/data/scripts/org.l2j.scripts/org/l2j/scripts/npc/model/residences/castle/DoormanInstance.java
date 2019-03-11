package org.l2j.scripts.npc.model.residences.castle;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.entity.residence.Residence;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.HtmlMessage;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.utils.Location;
import org.l2j.gameserver.utils.ReflectionUtils;

/**
 * @author VISTALL
 * @date 17:42/02.04.2011
 */
public class DoormanInstance extends org.l2j.scripts.npc.model.residences.DoormanInstance
{
	private static final long serialVersionUID = 1L;

	private Location[] _locs = new Location[2];

	public DoormanInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
		for(int i = 0; i < _locs.length; i++)
		{
			String loc = template.getAIParams().getString("tele_loc" + i, null);
			if(loc != null)
				_locs[i] = Location.parseLoc(loc);
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		int cond = getCond(player);
		switch(cond)
		{
			case COND_OWNER:
				if(command.equalsIgnoreCase("openDoors"))
					for(int i : _doors)
						ReflectionUtils.getDoor(i).openMe(player, true);
				else if(command.equalsIgnoreCase("closeDoors"))
					for(int i : _doors)
						ReflectionUtils.getDoor(i).closeMe(player, true);
				else if(command.startsWith("tele"))
				{
					int id = Integer.parseInt(command.substring(4, 5));
					Location loc = _locs[id];
					if(loc != null)
						player.teleToLocation(loc);
				}
				break;
			case COND_SIEGE:
				if(command.startsWith("tele"))
				{
					int id = Integer.parseInt(command.substring(4, 5));
					Location loc = _locs[id];
					if(loc != null)
						player.teleToLocation(loc);
				}
				else
					player.sendPacket(new HtmlMessage(this, _siegeDialog));
				break;
			case COND_FAIL:
				player.sendPacket(new HtmlMessage(this, _failDialog));
				break;
		}
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		String filename = null;
		int cond = getCond(player);
		switch(cond)
		{
			case COND_OWNER:
			case COND_SIEGE:
				filename = _mainDialog;
				break;
			case COND_FAIL:
				filename = _failDialog;
				break;
		}
		player.sendPacket(new HtmlMessage(this, filename).setPlayVoice(firstTalk));
	}

	@Override
	protected int getCond(Player player)
	{
		Castle residence = getCastle();
		Clan residenceOwner = residence.getOwner();
		if(residenceOwner != null && player.getClan() == residenceOwner && (player.getClanPrivileges() & getOpenPriv()) == getOpenPriv())
		{
			if(residence.getSiegeEvent().isInProgress())
			{
				if(Config.ALLOW_USE_DOORMANS_IN_SIEGE_BY_OWNERS)
					return COND_SIEGE;
				else
					return COND_FAIL;
			}
			else
				return COND_OWNER;
		}
		else
			return COND_FAIL;
	}

	@Override
	public int getOpenPriv()
	{
		return Clan.CP_CS_ENTRY_EXIT;
	}

	@Override
	public Residence getResidence()
	{
		return getCastle();
	}
}
