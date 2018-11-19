package handler.voicecommands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import l2s.gameserver.GameServer;
import l2s.gameserver.model.Player;

public class ServerInfo extends ScriptVoiceCommandHandler
{
	private final String[] COMMANDS = new String[] { "rev", "ver", "date", "time" };

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String target)
	{
		if(command.equals("rev") || command.equals("ver"))
		{
			activeChar.sendMessage("Project Revision: " + GameServer.PROJECT_REVISION);
			activeChar.sendMessage("Build Revision: " + GameServer.getInstance().getVersion().getRevisionNumber());
			activeChar.sendMessage("Update: " + GameServer.UPDATE_NAME);
			activeChar.sendMessage("Build date: " + GameServer.getInstance().getVersion().getBuildDate());
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
