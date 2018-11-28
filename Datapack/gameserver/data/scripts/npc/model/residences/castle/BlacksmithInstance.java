package npc.model.residences.castle;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.HtmlMessage;
import org.l2j.gameserver.network.l2.s2c.MyTargetSelectedPacket;
import org.l2j.gameserver.network.l2.s2c.ValidateLocationPacket;
import org.l2j.gameserver.templates.npc.NpcTemplate;

public class BlacksmithInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	protected static final int COND_ALL_FALSE = 0;
	protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
	protected static final int COND_OWNER = 2;

	public BlacksmithInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onAction(Player player, boolean shift)
	{
		if(this != player.getTarget())
		{
			player.setTarget(this);
			player.sendPacket(new ValidateLocationPacket(this));
		}
		else
		{
			player.sendPacket(new MyTargetSelectedPacket(player, this));
			if(!checkInteractionDistance(player))
			{
				player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
				player.sendActionFailed();
			}
			else
			{
				showMessageWindow(player, 0);
				player.sendActionFailed();
			}
		}
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		int condition = validateCondition(player);
		if(condition <= COND_ALL_FALSE)
			return;

		if(condition == COND_BUSY_BECAUSE_OF_SIEGE)
			return;

		if(condition == COND_OWNER)
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
				showMessageWindow(player, val);
			}
			else
				super.onBypassFeedback(player, command);
	}

	private void showMessageWindow(Player player, int val)
	{
		player.sendActionFailed();
		String filename = "castle/blacksmith/castleblacksmith-no.htm";

		int condition = validateCondition(player);
		if(condition > COND_ALL_FALSE)
			if(condition == COND_BUSY_BECAUSE_OF_SIEGE)
				filename = "castle/blacksmith/castleblacksmith-busy.htm"; // Busy because of siege
			else if(condition == COND_OWNER)
				if(val == 0)
					filename = "castle/blacksmith/castleblacksmith.htm";
				else
					filename = "castle/blacksmith/castleblacksmith-" + val + ".htm";

		HtmlMessage html = new HtmlMessage(this);
		html.setFile(filename);
		html.replace("%castleid%", Integer.toString(getCastle().getId()));
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
				else if(getCastle().getOwnerId() == player.getClanId() // Clan owns castle
						/*&& (player.getClanPrivileges() & Clan.CP_CS_MANOR_ADMIN) == Clan.CP_CS_MANOR_ADMIN*/) // has manor rights
					return COND_OWNER; // Owner
		return COND_ALL_FALSE;
	}
}