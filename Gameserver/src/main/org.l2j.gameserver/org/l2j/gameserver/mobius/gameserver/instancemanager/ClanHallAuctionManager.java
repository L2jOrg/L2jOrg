/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.instancemanager;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.ClanHallData;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.clanhallauction.ClanHallAuction;
import org.l2j.gameserver.mobius.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.mobius.gameserver.model.eventengine.AbstractEventManager;
import org.l2j.gameserver.mobius.gameserver.model.eventengine.ScheduleTarget;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author Sdw
 */
public class ClanHallAuctionManager extends AbstractEventManager<AbstractEvent<?>>
{
	private static final Logger LOGGER = Logger.getLogger(ClanHallAuctionManager.class.getName());
	
	private static final Map<Integer, ClanHallAuction> AUCTIONS = new HashMap<>();
	
	protected ClanHallAuctionManager()
	{
	}
	
	@ScheduleTarget
	private void onEventStart()
	{
		LOGGER.info(getClass().getSimpleName() + ": Clan Hall Auction has started!");
		AUCTIONS.clear();
		
		//@formatter:off
		ClanHallData.getInstance().getFreeAuctionableHall()
			.forEach(c -> AUCTIONS.put(c.getResidenceId(), new ClanHallAuction(c.getResidenceId())));
		//@formatter:on
	}
	
	@ScheduleTarget
	private void onEventEnd()
	{
		AUCTIONS.values().forEach(ClanHallAuction::finalizeAuctions);
		AUCTIONS.clear();
		LOGGER.info(getClass().getSimpleName() + ": Clan Hall Auction has ended!");
	}
	
	@Override
	public void onInitialized()
	{
	}
	
	public ClanHallAuction getClanHallAuctionById(int clanHallId)
	{
		return AUCTIONS.get(clanHallId);
	}
	
	public ClanHallAuction getClanHallAuctionByClan(L2Clan clan)
	{
		//@formatter:off
		return AUCTIONS.values().stream()
			.filter(a -> a.getBids().containsKey(clan.getId()))
			.findFirst()
			.orElse(null);
		//@formatter:on
	}
	
	public boolean checkForClanBid(int clanHallId, L2Clan clan)
	{
		//@formatter:off
		return AUCTIONS.entrySet().stream()
			.filter(a -> a.getKey() != clanHallId)
			.anyMatch(a -> a.getValue().getBids().containsKey(clan.getId()));
		//@formatter:on
	}
	
	public static ClanHallAuctionManager getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final ClanHallAuctionManager INSTANCE = new ClanHallAuctionManager();
	}
}
