package org.l2j.gameserver.handler.admincommands.impl;

import org.l2j.gameserver.handler.admincommands.IAdminCommandHandler;
import org.l2j.gameserver.model.GameObject;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.instances.DoorInstance;
import org.l2j.gameserver.network.l2.components.SystemMsg;

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