/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.admincommandhandlers;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.BuilderUtil;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @version $Revision: 1.2 $ $Date: 2004/06/27 08:12:59 $
 */
public class AdminTest implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
	{
		"admin_stats",
		"admin_skill_test"
	};
	
	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		if (command.equals("admin_stats"))
		{
			activeChar.sendMessage(ThreadPool.getInstance().getStats().toString());
		}
		else if (command.startsWith("admin_skill_test"))
		{
			try
			{
				final StringTokenizer st = new StringTokenizer(command);
				st.nextToken();
				final int id = Integer.parseInt(st.nextToken());
				if (command.startsWith("admin_skill_test"))
				{
					adminTestSkill(activeChar, id, true);
				}
				else
				{
					adminTestSkill(activeChar, id, false);
				}
			}
			catch (NumberFormatException e)
			{
				BuilderUtil.sendSysMessage(activeChar, "Command format is //skill_test <ID>");
			}
			catch (NoSuchElementException nsee)
			{
				BuilderUtil.sendSysMessage(activeChar, "Command format is //skill_test <ID>");
			}
		}
		return true;
	}
	
	/**
	 * @param activeChar
	 * @param id
	 * @param msu
	 */
	private void adminTestSkill(Player activeChar, int id, boolean msu)
	{
		Creature caster;
		final WorldObject target = activeChar.getTarget();
		if (!isCreature(target))
		{
			caster = activeChar;
		}
		else
		{
			caster = (Creature) target;
		}
		
		final Skill _skill = SkillEngine.getInstance().getSkill(id, 1);
		if (_skill != null)
		{
			caster.setTarget(activeChar);
			if (msu)
			{
				caster.broadcastPacket(new MagicSkillUse(caster, activeChar, id, 1, _skill.getHitTime(), _skill.getReuseDelay()));
			}
			else
			{
				caster.doCast(_skill);
			}
		}
	}
	
	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}
