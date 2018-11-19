package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.World;
import l2s.gameserver.model.instances.DoorInstance;
import l2s.gameserver.network.l2.components.SystemMsg;

public class AdminDoorControl implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_open,
		admin_close,
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().Door)
			return false;

		GameObject target;

		switch(command)
		{
			case admin_open:
				if(wordList.length > 1)
					target = World.getAroundObjectById(activeChar, Integer.parseInt(wordList[1]));
				else
					target = activeChar.getTarget();

				if(target != null && target.isDoor())
					((DoorInstance) target).openMe();
				else
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);

				break;
			case admin_close:
				if(wordList.length > 1)
					target = World.getAroundObjectById(activeChar, Integer.parseInt(wordList[1]));
				else
					target = activeChar.getTarget();
				if(target != null && target.isDoor())
					((DoorInstance) target).closeMe();
				else
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
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