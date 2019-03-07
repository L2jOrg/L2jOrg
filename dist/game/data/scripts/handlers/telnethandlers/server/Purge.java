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
package handlers.telnethandlers.server;

import com.l2jmobius.Config;
import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.gameserver.network.telnet.ITelnetCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Purge implements ITelnetCommand
{
	@Override
	public String getCommand()
	{
		return "purge";
	}
	
	@Override
	public String getUsage()
	{
		return "Purge";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		ThreadPool.purge();
		final StringBuilder sb = new StringBuilder("STATUS OF THREAD POOLS AFTER PURGE COMMAND:" + Config.EOL);
		for (String line : ThreadPool.getStats())
		{
			sb.append(line + Config.EOL);
		}
		return sb.toString();
	}
}
