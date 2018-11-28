package handler.voicecommands;

import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.model.Player;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerInfo extends ScriptVoiceCommandHandler
{
	private final String[] COMMANDS = new String[] { "rev", "ver", "date", "time" };

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if(command.equals("rev") || command.equals("ver"))
		{
			activeChar.sendMessage("Project Version: " + GameServer.getInstance().getVersion());
			activeChar.sendMessage("Update: " + GameServer.UPDATE_NAME);
		}
		else if(command.equals("date") || command.equals("time"))
		{
			activeChar.sendMessage(DATE_FORMAT.format(new Date(System.currentTimeMillis())));
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
