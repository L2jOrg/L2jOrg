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

import java.text.DecimalFormat;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.network.telnet.ITelnetCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class Memusage implements ITelnetCommand
{
	@Override
	public String getCommand()
	{
		return "memusage";
	}
	
	@Override
	public String getUsage()
	{
		return "MemUsage";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		final double max = Runtime.getRuntime().maxMemory() / 1024; // maxMemory is the upper
		// limit the jvm can use
		final double allocated = Runtime.getRuntime().totalMemory() / 1024; // totalMemory the
		// size of the current allocation pool
		final double nonAllocated = max - allocated; // non allocated memory till jvm limit
		final double cached = Runtime.getRuntime().freeMemory() / 1024; // freeMemory the
		// unused memory in the allocation pool
		final double used = allocated - cached; // really used memory
		final double useable = max - used; // allocated, but non-used and non-allocated memory
		
		final StringBuilder sb = new StringBuilder();
		
		final DecimalFormat df = new DecimalFormat(" (0.0000'%')");
		final DecimalFormat df2 = new DecimalFormat(" # 'KB'");
		
		sb.append("+----" + Config.EOL); // ...
		sb.append("| Allowed Memory:" + df2.format(max) + Config.EOL);
		sb.append("|    |= Allocated Memory:" + df2.format(allocated) + df.format((allocated / max) * 100) + Config.EOL);
		sb.append("|    |= Non-Allocated Memory:" + df2.format(nonAllocated) + df.format((nonAllocated / max) * 100) + Config.EOL);
		sb.append("| Allocated Memory:" + df2.format(allocated) + Config.EOL);
		sb.append("|    |= Used Memory:" + df2.format(used) + df.format((used / max) * 100) + Config.EOL);
		sb.append("|    |= Unused (cached) Memory:" + df2.format(cached) + df.format((cached / max) * 100) + Config.EOL);
		sb.append("| Useable Memory:" + df2.format(useable) + df.format((useable / max) * 100) + Config.EOL); // ...
		sb.append("+----" + Config.EOL);
		return sb.toString();
	}
}
