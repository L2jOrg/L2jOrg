package handler.voicecommands;

import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.CustomMessage;

public class Debug extends ScriptVoiceCommandHandler
{
	private final String[] COMMANDS = new String[] { "debug" };

	@Override
	public boolean useVoicedCommand(String command, Player player, String args)
	{
		if(!Config.ALT_DEBUG_ENABLED)
			return false;

		if(player.isDebug())
		{
			player.setDebug(false);
			player.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Disabled"));
		}
		else
		{
			player.setDebug(true);
			player.sendMessage(new CustomMessage("voicedcommandhandlers.Debug.Enabled"));
		}
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}
