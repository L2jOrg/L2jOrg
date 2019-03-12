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
package handlers.admincommandhandlers;

import java.util.StringTokenizer;

import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.L2Playable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.util.BuilderUtil;

public class AdminLevel implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_add_level",
		"admin_set_level"
	};
	
	@Override
	public boolean useAdminCommand(String command, L2PcInstance activeChar)
	{
		final L2Object targetChar = activeChar.getTarget();
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken(); // Get actual command
		
		String val = "";
		if (st.countTokens() >= 1)
		{
			val = st.nextToken();
		}
		
		if (actualCommand.equalsIgnoreCase("admin_add_level"))
		{
			try
			{
				if ((targetChar != null) && targetChar.isPlayable())
				{
					((L2Playable) targetChar).getStat().addLevel(Byte.parseByte(val));
				}
			}
			catch (NumberFormatException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Wrong Number Format");
			}
		}
		else if (actualCommand.equalsIgnoreCase("admin_set_level"))
		{
			final int maxLevel = ExperienceData.getInstance().getMaxLevel();
			try
			{
				if ((targetChar == null) || !targetChar.isPlayer())
				{
					activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET); // incorrect target!
					return false;
				}
				final L2PcInstance targetPlayer = (L2PcInstance) targetChar;
				
				final byte lvl = Byte.parseByte(val);
				if ((lvl >= 1) && (lvl <= maxLevel))
				{
					targetPlayer.setExp(ExperienceData.getInstance().getExpForLevel(lvl));
					targetPlayer.getStat().setLevel(lvl);
					targetPlayer.setCurrentHpMp(targetPlayer.getMaxHp(), targetPlayer.getMaxMp());
					targetPlayer.setCurrentCp(targetPlayer.getMaxCp());
					targetPlayer.broadcastUserInfo();
				}
				else
				{
					BuilderUtil.sendSysMessage(activeChar, "You must specify level between 1 and " + maxLevel + ".");
					return false;
				}
			}
			catch (NumberFormatException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "You must specify level between 1 and " + maxLevel + ".");
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
