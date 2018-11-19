package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;

public class AdminIP implements IAdminCommandHandler
{
	private enum Commands
	{
		admin_charip
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanBan)
			return false;

		switch(command)
		{
			case admin_charip:
				if(wordList.length != 2)
				{
					activeChar.sendMessage("Command syntax: //charip <char_name>");
					activeChar.sendMessage(" Gets character's IP.");
					break;
				}

				Player pl = World.getPlayer(wordList[1]);

				if(pl == null)
				{
					activeChar.sendMessage("Character " + wordList[1] + " not found.");
					break;
				}

				String ip_adr = pl.getIP();
				if(ip_adr.equalsIgnoreCase("<not connected>"))
				{
					activeChar.sendMessage("Character " + wordList[1] + " not found.");
					break;
				}

				activeChar.sendMessage("Character's IP: " + ip_adr);
				break;
		}
		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}