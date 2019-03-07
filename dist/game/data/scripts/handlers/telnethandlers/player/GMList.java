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
package handlers.telnethandlers.player;

import com.l2jmobius.Config;
import com.l2jmobius.gameserver.data.xml.impl.AdminData;
import com.l2jmobius.gameserver.network.telnet.ITelnetCommand;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author UnAfraid
 */
public class GMList implements ITelnetCommand
{
	@Override
	public String getCommand()
	{
		return "gmlist";
	}
	
	@Override
	public String getUsage()
	{
		return "GMList";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		int i = 0;
		String gms = "";
		for (String player : AdminData.getInstance().getAllGmNames(true))
		{
			gms += player + ", ";
			i++;
		}
		if (!gms.isEmpty())
		{
			gms = gms.substring(0, gms.length() - 2) + ".";
		}
		return "There are currently " + i + " GM(s) online..." + Config.EOL + gms;
	}
}
