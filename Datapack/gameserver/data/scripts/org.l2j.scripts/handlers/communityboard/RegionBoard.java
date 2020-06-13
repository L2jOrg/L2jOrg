/*
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
package handlers.communityboard;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IWriteBoardHandler;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

import static org.l2j.commons.util.Util.isDigit;

/**
 * Region board.
 * @author Zoey76
 */
public class RegionBoard implements IWriteBoardHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegionBoard.class);

	// Region data
	// @formatter:off
	private static final int[] REGIONS = { 1049, 1052, 1053, 1057, 1060, 1059, 1248, 1247, 1056 };
	// @formatter:on
	private static final String[] COMMANDS = {
		"_bbsloc"
	};
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, StringTokenizer tokens, Player activeChar)
	{
		if (command.equals("_bbsloc"))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Region", command);
			
			final String list = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/region_list.html");
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < REGIONS.length; i++)
			{
				final Castle castle = CastleManager.getInstance().getCastleById(i + 1);
				final Clan clan = ClanTable.getInstance().getClan(castle.getOwnerId());
				String link = list.replaceAll("%region_id%", String.valueOf(i));
				link = link.replace("%region_name%", String.valueOf(REGIONS[i]));
				link = link.replace("%region_owning_clan%", (clan != null ? clan.getName() : "NPC"));
				link = link.replace("%region_owning_clan_alliance%", ((clan != null) && (clan.getAllyName() != null) ? clan.getAllyName() : ""));
				link = link.replace("%region_tax_rate%", castle.getTaxPercent(TaxType.BUY) + "%");
				sb.append(link);
			}
			
			String html = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/region.html");
			html = html.replace("%region_list%", sb.toString());
			CommunityBoardHandler.separateAndSend(html, activeChar);
		}
		else if (command.startsWith("_bbsloc;"))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Region>", command);
			
			final String id = command.replace("_bbsloc;", "");
			if (!isDigit(id))
			{
				LOGGER.warn("Player {} sent and invalid region bypass {}!", activeChar,  command);
				return false;
			}
			
			// TODO: Implement.
		}
		return true;
	}
	
	@Override
	public boolean writeCommunityBoardCommand(Player activeChar, String arg1, String arg2, String arg3, String arg4, String arg5)
	{
		// TODO: Implement.
		return false;
	}
}
