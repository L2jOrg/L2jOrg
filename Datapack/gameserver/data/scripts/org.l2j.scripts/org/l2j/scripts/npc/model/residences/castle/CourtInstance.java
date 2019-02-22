package org.l2j.scripts.npc.model.residences.castle;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.HtmlMessage;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.skills.skillclasses.Call;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.utils.Location;

public class CourtInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	protected static final int COND_ALL_FALSE = 0;
	protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	protected static final int COND_OWNER = 2;

	/**
	 * @param template
	 */
	public CourtInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		int condition = validateCondition(player);
		if(condition <= COND_ALL_FALSE)
			return;
		else if(condition == COND_BUSY_BECAUSE_OF_SIEGE)
			return;
		else if((player.getClanPrivileges() & Clan.CP_CS_USE_FUNCTIONS) != Clan.CP_CS_USE_FUNCTIONS)
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		else if(condition == COND_OWNER)
		{
			if(command.startsWith("Chat"))
			{
				int val = 0;
				try
				{
					val = Integer.parseInt(command.substring(5));
				}
				catch(IndexOutOfBoundsException ioobe)
				{}
				catch(NumberFormatException nfe)
				{}
				showChatWindow(player, val, false);
			}
			else if(command.startsWith("gotoleader"))
			{
				if(player.getClan() != null)
				{
					Player clanLeader = player.getClan().getLeader().getPlayer();
					if(clanLeader == null)
						return;

					if(clanLeader.getAbnormalList().contains(3632))
					{
						if(Call.canSummonHere(clanLeader) != null)
							return;

						if(Call.canBeSummoned(player) == null)
							player.teleToLocation(Location.findAroundPosition(clanLeader, 100));
						return;
					}
					showChatWindow(player, "castle/CourtMagician/CourtMagician-nogate.htm", false);
				}
			}
			else
				super.onBypassFeedback(player, command);
		}
	}

	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
	{
		player.sendActionFailed();
		String filename = "castle/CourtMagician/CourtMagician-no.htm";

		int condition = validateCondition(player);
		if(condition > COND_ALL_FALSE)
			if(condition == COND_BUSY_BECAUSE_OF_SIEGE)
				filename = "castle/CourtMagician/CourtMagician-busy.htm"; // Busy because of siege
			else if(condition == COND_OWNER)
				if(val == 0)
					filename = "castle/CourtMagician/CourtMagician.htm";
				else
					filename = "castle/CourtMagician/CourtMagician-" + val + ".htm";

		HtmlMessage html = new HtmlMessage(this).setPlayVoice(firstTalk);
		html.setFile(filename);

		int side = 0;
		if(getCastle() != null)
			side = getCastle().getResidenceSide().ordinal();

		html.replace("<?residnce_side?>", String.valueOf(side));
		player.sendPacket(html);
	}

	protected int validateCondition(Player player)
	{
		if(player.isGM())
			return COND_OWNER;
		if(getCastle() != null && getCastle().getId() != 0)
			if(player.getClan() != null)
				if(getCastle().getSiegeEvent().isInProgress())
					return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
				else if(getCastle().getOwnerId() == player.getClanId()) // Clan owns castle
					return COND_OWNER;
		return COND_ALL_FALSE;
	}
}