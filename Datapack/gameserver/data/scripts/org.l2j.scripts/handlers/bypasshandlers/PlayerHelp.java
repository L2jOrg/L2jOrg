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

import java.util.StringTokenizer;
import java.util.logging.Level;

import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;

public class PlayerHelp implements IBypassHandler
{
	private static final String[] COMMANDS =
	{
		"player_help"
	};
	
	@Override
	public boolean useBypass(String command, L2PcInstance activeChar, L2Character target)
	{
		try
		{
			if (command.length() < 13)
			{
				return false;
			}
			
			final String path = command.substring(12);
			if (path.contains(".."))
			{
				return false;
			}
			
			final StringTokenizer st = new StringTokenizer(path);
			final String[] cmd = st.nextToken().split("#");
			
			final NpcHtmlMessage html;
			if (cmd.length > 1)
			{
				final int itemId = Integer.parseInt(cmd[1]);
				html = new NpcHtmlMessage(0, itemId);
			}
			else
			{
				html = new NpcHtmlMessage();
			}
			
			html.setFile(activeChar, "data/html/help/" + cmd[0]);
			activeChar.sendPacket(html);
		}
		catch (Exception e)
		{
			LOGGER.warn("Exception in " + getClass().getSimpleName(), e);
		}
		return true;
	}
	
	@Override
	public String[] getBypassList()
	{
		return COMMANDS;
	}
}
