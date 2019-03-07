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
package handlers.telnethandlers.chat;

import com.l2jmobius.gameserver.enums.ChatType;
import com.l2jmobius.gameserver.model.L2World;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.serverpackets.CreatureSay;
import com.l2jmobius.gameserver.network.telnet.ITelnetCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Msg implements ITelnetCommand
{
	@Override
	public String getCommand()
	{
		return "msg";
	}
	
	@Override
	public String getUsage()
	{
		return "Msg <player> <text>";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		if ((args.length < 2) || args[0].isEmpty())
		{
			return null;
		}
		final L2PcInstance player = L2World.getInstance().getPlayer(args[0]);
		if (player != null)
		{
			final StringBuilder sb = new StringBuilder();
			for (int i = 1; i < args.length; i++)
			{
				sb.append(args[i] + " ");
			}
			player.sendPacket(new CreatureSay(0, ChatType.WHISPER, "Telnet Priv", sb.toString()));
			return "Announcement sent!";
		}
		return "Couldn't find player with such name.";
	}
}
