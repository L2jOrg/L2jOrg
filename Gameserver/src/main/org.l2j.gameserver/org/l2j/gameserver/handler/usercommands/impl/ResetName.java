package org.l2j.gameserver.handler.usercommands.impl;

import org.l2j.gameserver.handler.usercommands.IUserCommandHandler;
import org.l2j.gameserver.model.Player;

/**
 * Support for /resetname command
 */
public class ResetName implements IUserCommandHandler
{
	private static final int[] COMMAND_IDS = { 117 };

	public boolean useUserCommand(int id, Player activeChar)
	{
		if(COMMAND_IDS[0] != id)
			return false;

		if(activeChar.getVar("oldtitle") != null)
		{
			activeChar.setTitleColor(Player.DEFAULT_TITLE_COLOR);
			activeChar.setTitle(activeChar.getVar("oldtitle"));
			activeChar.broadcastUserInfo(true);
			return true;
		}
		return false;
	}

	public final int[] getUserCommandList()
	{
		return COMMAND_IDS;
	}
}