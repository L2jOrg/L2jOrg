package org.l2j.gameserver.handler.bbs;

import org.l2j.gameserver.model.Player;

public interface IBbsHandler
{
	public String[] getBypassCommands();

	public void onBypassCommand(Player player, String bypass);

	public void onWriteCommand(Player player, String bypass, String arg1, String arg2, String arg3, String arg4, String arg5);
}