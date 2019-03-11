package org.l2j.scripts.handler.voicecommands;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.Experience;

public class Delevel extends ScriptVoiceCommandHandler
{
	private final String[] COMMANDS = new String[] { "delevel" };

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if(!Config.ALLOW_DELEVEL_COMMAND)
			return false;

		if(command.equals("delevel"))
		{
			int _old_level = activeChar.getLevel();
			if(_old_level == 1)
				return false;
			Long exp_add = Experience.getExpForLevel(_old_level - 1) - activeChar.getExp();
			activeChar.addExpAndSp(exp_add, 0, true);
		}
		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}
