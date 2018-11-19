package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.geodata.GeoEngine;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;

/**
 * @author Bonux
 **/
public class AdminDebug implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_cansee
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;
		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		switch(command)
		{
			case admin_cansee:
				GameObject target = activeChar.getTarget();
				if(target != null)
				{
					boolean seeActiveChar = GeoEngine.canSeeTarget(activeChar, target, activeChar.isFlying());
					boolean seeTarget = GeoEngine.canSeeTarget(target, activeChar, target.isFlying());
					activeChar.sendMessage("You" + (seeActiveChar ? "" : " DOES NOT") + " SEE TARGET, target" + (seeTarget ? "" : " DOES NOT") + " SEE YOU!");
				}
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
