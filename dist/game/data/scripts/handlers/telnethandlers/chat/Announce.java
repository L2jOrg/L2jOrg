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

import com.l2jmobius.gameserver.network.telnet.ITelnetCommand;
import com.l2jmobius.gameserver.util.Broadcast;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Announce implements ITelnetCommand
{
	@Override
	public String getCommand()
	{
		return "announce";
	}
	
	@Override
	public String getUsage()
	{
		return "Announce <text>";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		if ((args.length == 0) || args[0].isEmpty())
		{
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		for (String str : args)
		{
			sb.append(str + " ");
		}
		Broadcast.toAllOnlinePlayers(sb.toString());
		return "Announcement sent!";
	}
}
