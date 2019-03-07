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
package handlers.communityboard;

import com.l2jmobius.gameserver.cache.HtmCache;
import com.l2jmobius.gameserver.handler.CommunityBoardHandler;
import com.l2jmobius.gameserver.handler.IParseBoardHandler;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

/**
 * Friends board.
 * @author Zoey76
 */
public class FriendsBoard implements IParseBoardHandler
{
	private static final String[] COMMANDS =
	{
		"_friendlist",
		"_friendblocklist"
	};
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMANDS;
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, L2PcInstance activeChar)
	{
		if (command.equals("_friendlist"))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Friends List", command);
			
			final String html = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/friends_list.html");
			
			CommunityBoardHandler.separateAndSend(html, activeChar);
		}
		else if (command.equals("_friendblocklist"))
		{
			CommunityBoardHandler.getInstance().addBypass(activeChar, "Ignore list", command);
			
			final String html = HtmCache.getInstance().getHtm(activeChar, "data/html/CommunityBoard/friends_block_list.html");
			
			CommunityBoardHandler.separateAndSend(html, activeChar);
		}
		else
		{
			
		}
		return true;
	}
}
