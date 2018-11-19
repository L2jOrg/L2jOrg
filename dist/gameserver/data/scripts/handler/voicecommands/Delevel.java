package handler.voicecommands;

import l2s.gameserver.Config;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.Player;

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
