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
package handlers.bypasshandlers;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class Link implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"Link"
	};
	
	@Override
	public boolean useBypass(String command, Player activeChar, L2Character target)
	{
		final String htmlPath = command.substring(4).trim();
		if (htmlPath.isEmpty())
		{
			LOGGER.warn("Player " + activeChar.getName() + " sent empty link html!");
			return false;
		}
		
		if (htmlPath.contains(".."))
		{
			LOGGER.warn("Player " + activeChar.getName() + " sent invalid link html: " + htmlPath);
			return false;
		}
		
		final String content = HtmCache.getInstance().getHtm(activeChar, "data/html/" + htmlPath);
		final NpcHtmlMessage html = new NpcHtmlMessage(target != null ? target.getObjectId() : 0);
		html.setHtml(content.replace("%objectId%", String.valueOf(target != null ? target.getObjectId() : 0)));
		activeChar.sendPacket(html);
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
