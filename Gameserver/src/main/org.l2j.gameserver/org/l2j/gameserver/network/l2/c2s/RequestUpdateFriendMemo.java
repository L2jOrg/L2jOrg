package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class RequestUpdateFriendMemo extends L2GameClientPacket
{
	private String _name;
	private String _memo;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_name = readString(buffer);
		_memo = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		activeChar.getFriendList().updateMemo(_name, _memo);
	}
}