package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.handler.admincommands.AdminCommandHandler;
import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class SendBypassBuildCmd extends L2GameClientPacket
{
	private String _command;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_command = readString(buffer);

		if(_command != null)
			_command = _command.trim();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();

		if(activeChar == null)
			return;

		String cmd = _command;

		if(!cmd.contains("admin_"))
			cmd = "admin_" + cmd;

		AdminCommandHandler.getInstance().useAdminCommandHandler(activeChar, cmd);
	}
}