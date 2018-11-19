package handler.voicecommands;

import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.s2c.RadarControlPacket;
import l2s.gameserver.utils.Functions;

/**
 * @Author: Abaddon
 */
public class Help extends ScriptVoiceCommandHandler
{
	private String[] COMMANDS = new String[] { "help", "exp", "whereis" };

	@Override
	public boolean useVoicedCommand(String command, Player activeChar, String args)
	{
		command = command.intern();
		if(command.equalsIgnoreCase("help"))
			return help(command, activeChar, args);
		if(command.equalsIgnoreCase("whereis"))
			return whereis(command, activeChar, args);
		if(command.equalsIgnoreCase("exp"))
			return exp(command, activeChar, args);

		return false;
	}

	private boolean exp(String command, Player activeChar, String args)
	{
		if(activeChar.getLevel() >= (activeChar.isBaseClassActive() ? Experience.getMaxLevel() : Experience.getMaxSubLevel()))
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Help.MaxLevel"));
		else
		{
			long exp = Experience.getExpForLevel(activeChar.getLevel() + 1) - activeChar.getExp();
			activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Help.ExpLeft").addNumber(exp));
		}
		return true;
	}

	private boolean whereis(String command, Player activeChar, String args)
	{
		Player friend = World.getPlayer(args);
		if(friend == null)
			return false;

		if(friend.getParty() == activeChar.getParty() || friend.getClan() == activeChar.getClan())
		{
			RadarControlPacket rc = new RadarControlPacket(0, 1, friend.getLoc());
			activeChar.sendPacket(rc);
			return true;
		}

		return false;
	}

	private boolean help(String command, Player activeChar, String args)
	{
		String dialog = HtmCache.getInstance().getHtml("command/help.htm", activeChar);
		Functions.show(dialog, activeChar);
		return true;
	}

	@Override
	public String[] getVoicedCommandList()
	{
		return COMMANDS;
	}
}