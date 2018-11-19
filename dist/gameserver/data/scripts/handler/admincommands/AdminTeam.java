package handler.admincommands;

import l2s.gameserver.model.Creature;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.TeamType;
import l2s.gameserver.network.l2.components.SystemMsg;

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
