package l2s.gameserver.model.instances;

import l2s.commons.collections.MultiValueSet;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.HtmlMessage;
import l2s.gameserver.network.l2.s2c.PledgeShowInfoUpdatePacket;
import l2s.gameserver.network.l2.s2c.SystemMessage;
import l2s.gameserver.templates.npc.NpcTemplate;

public final class ClanTraderInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;

	public ClanTraderInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}

	@Override
	public void onBypassFeedback(Player player, String command)
	{
		HtmlMessage html = new HtmlMessage(this);

		if(command.equalsIgnoreCase("crp"))
		{
			if(player.getClan() != null && player.getClan().getLevel() > 4)
				html.setFile("default/" + getNpcId() + "-2.htm");
			else
				html.setFile("default/" + getNpcId() + "-1.htm");

			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		else if(command.startsWith("exchange"))
		{
			if(!player.isClanLeader())
			{
				html.setFile("default/" + getNpcId() + "-no.htm");
				html.replace("%objectId%", String.valueOf(getObjectId()));
				player.sendPacket(html);
				return;
			}

			int itemId = Integer.parseInt(command.substring(9).trim());

			int reputation = 0;
			long itemCount = 0;

			switch(itemId)
			{
				case 9911: // Blood alliance
					reputation = 500;
					itemCount = 1;
					break;
				case 9910: // 10 Blood oath
					reputation = 200;
					itemCount = 10;
					break;
				case 9912: // 100 Knight's Epaulettes
					reputation = 20;
					itemCount = 100;
					break;
			}

			if(player.getInventory().destroyItemByItemId(itemId, itemCount))
			{
				player.getClan().incReputation(reputation, false, "ClanTrader " + itemId + " from " + player.getName());
				player.getClan().broadcastToOnlineMembers(new PledgeShowInfoUpdatePacket(player.getClan()));
				player.sendPacket(new SystemMessage(SystemMessage.YOUR_CLAN_HAS_ADDED_1S_POINTS_TO_ITS_CLAN_REPUTATION_SCORE).addNumber(reputation));

				html.setFile("default/" + getNpcId() + "-ExchangeSuccess.htm");
			}
			else
				html.setFile("default/" + getNpcId() + "-ExchangeFailed.htm");

			html.replace("%objectId%", String.valueOf(getObjectId()));
			player.sendPacket(html);
		}
		else
			super.onBypassFeedback(player, command);
	}
}