package org.l2j.scripts.handler.admincommands;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.TeamType;
import org.l2j.gameserver.network.l2.components.SystemMsg;

/**
 * @author VISTALL
 * @date 2:52/29.06.2011
 */
public class AdminTeam extends ScriptAdminCommand
{
	enum Commands
	{
		admin_setteam
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		TeamType team = TeamType.NONE;
		if(wordList.length >= 2)
		{
			for(TeamType t : TeamType.values())
			{
				if(wordList[1].equalsIgnoreCase(t.name()))
					team = t;
			}
		}

		GameObject object = activeChar.getTarget();
		if(object == null || !object.isCreature())
		{
			activeChar.sendPacket(SystemMsg.INVALID_TARGET);
			return false;
		}

		((Creature)object).setTeam(team);
		return true;
	}

	@Override
	public Enum[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}
