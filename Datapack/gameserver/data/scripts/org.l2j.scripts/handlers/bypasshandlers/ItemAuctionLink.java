/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.bypasshandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.instancemanager.ItemAuctionManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.auction.ItemAuction;
import org.l2j.gameserver.model.item.auction.ItemAuctionInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExItemAuctionInfoPacket;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isNpc;

public class ItemAuctionLink implements IBypassHandler
{
	private static final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss dd.MM.yyyy");
	
	private static final String[] COMMANDS =
	{
		"ItemAuction"
	};
	
	@Override
	public boolean useBypass(String command, Player player, Creature target)
	{
		if (!isNpc(target))
		{
			return false;
		}
		
		if (!Config.ALT_ITEM_AUCTION_ENABLED)
		{
			player.sendPacket(SystemMessageId.IT_IS_NOT_AN_AUCTION_PERIOD);
			return true;
		}
		
		final ItemAuctionInstance au = ItemAuctionManager.getInstance().getManagerInstance(target.getId());
		if (au == null)
		{
			return false;
		}
		
		try
		{
			final StringTokenizer st = new StringTokenizer(command);
			st.nextToken(); // bypass "ItemAuction"
			if (!st.hasMoreTokens())
			{
				return false;
			}
			
			final String cmd = st.nextToken();
			if ("show".equalsIgnoreCase(cmd))
			{
				if (!player.getFloodProtectors().getItemAuction().tryPerformAction("RequestInfoItemAuction"))
				{
					return false;
				}
				
				if (player.isItemAuctionPolling())
				{
					return false;
				}
				
				final ItemAuction currentAuction = au.getCurrentAuction();
				final ItemAuction nextAuction = au.getNextAuction();
				
				if (currentAuction == null)
				{
					player.sendPacket(SystemMessageId.IT_IS_NOT_AN_AUCTION_PERIOD);
					
					if (nextAuction != null)
					{
						player.sendMessage("The next auction will begin on the " + fmt.format(new Date(nextAuction.getStartingTime())) + ".");
					}
					return true;
				}
				
				player.sendPacket(new ExItemAuctionInfoPacket(false, currentAuction, nextAuction));
			}
			else if ("cancel".equalsIgnoreCase(cmd))
			{
				final ItemAuction[] auctions = au.getAuctionsByBidder(player.getObjectId());
				boolean returned = false;
				for (ItemAuction auction : auctions)
				{
					if (auction.cancelBid(player))
					{
						returned = true;
					}
				}
				if (!returned)
				{
					player.sendPacket(SystemMessageId.THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR);
				}
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			LOGGER.warn("Exception in " + getClass().getSimpleName(), e);
		}
		
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
