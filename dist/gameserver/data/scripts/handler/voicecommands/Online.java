package handler.voicecommands;

import l2s.gameserver.Config;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.tables.FakePlayersTable;

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
			int i = 0;
			int j = 0;
			for(Player player : GameObjectsStorage.getPlayers())
			{
				i++;
				if(player.isInOfflineMode())
					j++;
			}
			i = i + FakePlayersTable.getActiveFakePlayersCount();	
			if(activeChar.isLangRus())
			{
				activeChar.sendMessage("На сервере играют "+i+" игроков.");
				activeChar.sendMessage("Из них "+j+" находятся в оффлайн торге.");
			}	
			else
			{
				activeChar.sendMessage("Right now there are "+i+" players online.");
				activeChar.sendMessage("From them "+j+" are in offline trade mode.");			
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
