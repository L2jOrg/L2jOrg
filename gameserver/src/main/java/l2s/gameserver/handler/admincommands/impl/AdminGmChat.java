package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.ChatType;
import l2s.gameserver.network.l2.s2c.SayPacket2;
import l2s.gameserver.tables.GmListTable;

public class AdminGmChat implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_gmchat,
		admin_snoop
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanAnnounce)
			return false;

		switch(command)
		{
			case admin_gmchat:
				try
				{
					String text = fullString.replaceFirst(Commands.admin_gmchat.name(), "");
					SayPacket2 cs = new SayPacket2(0, ChatType.ALLIANCE, activeChar.getName(), text);
					GmListTable.broadcastToGMs(cs);
				}
				catch(StringIndexOutOfBoundsException e)
				{}
				break;
			case admin_snoop:
			{
				GameObject target = activeChar.getTarget();
				if(target == null)
				{
					activeChar.sendMessage("You must select a target.");
					return false;
				}
				if(!target.isPlayer())
				{
					activeChar.sendMessage("Target must be a player.");
					return false;
				}
				Player player = (Player) target;
				player.addSnooper(activeChar);
			}
		}
		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}