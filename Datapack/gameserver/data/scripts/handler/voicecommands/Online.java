package handler.voicecommands;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;

public class Online extends ScriptVoiceCommandHandler
{
	private final String[] COMMANDS = new String[] { "online" };

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if(!Config.ALLOW_TOTAL_ONLINE)
			return false;

		if(command.equals("online"))
		{
			int i = GameObjectsStorage.getPlayers().size();

			if(activeChar.isLangRus())
			{
				activeChar.sendMessage("На сервере играют "+i+" игроков.");
			}	
			else
			{
				activeChar.sendMessage("Right now there are "+i+" players online.");
			}
			return true;
		}
		return false;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}
