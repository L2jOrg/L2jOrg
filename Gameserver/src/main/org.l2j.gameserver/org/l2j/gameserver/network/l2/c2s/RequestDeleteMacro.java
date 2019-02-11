package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class RequestDeleteMacro extends L2GameClientPacket
{
	private int _id;

	/**
	 * format:		cd
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_id = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;
		activeChar.deleteMacro(_id);
	}
}