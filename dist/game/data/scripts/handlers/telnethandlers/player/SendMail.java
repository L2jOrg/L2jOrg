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

import java.util.ArrayList;
import java.util.List;

import com.l2jmobius.gameserver.data.sql.impl.CharNameTable;
import com.l2jmobius.gameserver.enums.MailType;
import com.l2jmobius.gameserver.instancemanager.MailManager;
import com.l2jmobius.gameserver.model.entity.Message;
import com.l2jmobius.gameserver.model.holders.ItemHolder;
import com.l2jmobius.gameserver.model.itemcontainer.Mail;
import com.l2jmobius.gameserver.network.telnet.ITelnetCommand;
import com.l2jmobius.gameserver.util.Util;

import io.netty.channel.ChannelHandlerContext;

/**
 * @author Mobius
 */
public class SendMail implements ITelnetCommand
{
	@Override
	public String getCommand()
	{
		return "sendmail";
	}
	
	@Override
	public String getUsage()
	{
		return "sendmail <player name> <mail subject (use _ for spaces)> <mail message (use _ for spaces)> <item(s) (optional) e.g. 57x1000000>";
	}
	
	@Override
	public String handle(ChannelHandlerContext ctx, String[] args)
	{
		if ((args.length < 3) || args[0].isEmpty())
		{
			return null;
		}
		final int objectId = CharNameTable.getInstance().getIdByName(args[0]);
		if (objectId > 0)
		{
			final Message msg = new Message(objectId, args[1].replace("_", " "), args[2].replace("_", " "), args.length > 3 ? MailType.PRIME_SHOP_GIFT : MailType.REGULAR);
			final List<ItemHolder> itemHolders = new ArrayList<>();
			int counter = -1;
			for (String str : args)
			{
				counter++;
				if (counter < 3)
				{
					continue;
				}
				if (str.toLowerCase().contains("x"))
				{
					final String itemId = str.toLowerCase().split("x")[0];
					final String itemCount = str.toLowerCase().split("x")[1];
					if (Util.isDigit(itemId) && Util.isDigit(itemCount))
					{
						itemHolders.add(new ItemHolder(Integer.parseInt(itemId), Long.parseLong(itemCount)));
					}
				}
				else if (Util.isDigit(str))
				{
					itemHolders.add(new ItemHolder(Integer.parseInt(str), 1));
				}
			}
			if (!itemHolders.isEmpty())
			{
				final Mail attachments = msg.createAttachments();
				for (ItemHolder itemHolder : itemHolders)
				{
					attachments.addItem("Telnet-Mail", itemHolder.getId(), itemHolder.getCount(), null, null);
				}
			}
			MailManager.getInstance().sendMessage(msg);
			return "An ingame mail has been sent to " + args[0] + ".";
		}
		return "Couldn't find player with such name.";
	}
}
