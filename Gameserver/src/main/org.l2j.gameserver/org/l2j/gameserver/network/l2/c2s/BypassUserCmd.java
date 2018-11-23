package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.handler.usercommands.IUserCommandHandler;
import org.l2j.gameserver.handler.usercommands.UserCommandHandler;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.CustomMessage;

/**
 * format:  cd
 * Пример пакета по команде /loc:
 * AA 00 00 00 00
 */
public class BypassUserCmd extends L2GameClientPacket
{
	private int _command;

	@Override
	protected void readImpl()
	{
		_command = readInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		IUserCommandHandler handler = UserCommandHandler.getInstance().getUserCommandHandler(_command);

		if(handler == null)
			activeChar.sendMessage(new CustomMessage("common.S1NotImplemented").addString(String.valueOf(_command)));
		else
			handler.useUserCommand(_command, activeChar);
	}
}