package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExListPartyMatchingWaitingRoom;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class RequestListPartyMatchingWaitingRoom extends L2GameClientPacket
{
	private int _minLevel, _maxLevel, _page, _classes[];

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_page = buffer.getInt();
		_minLevel = buffer.getInt();
		_maxLevel = buffer.getInt();
		int size = buffer.getInt();
		if(size > Byte.MAX_VALUE || size < 0)
			size = 0;
		_classes = new int[size];
		for(int i = 0; i < size; i++)
			_classes[i] = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		activeChar.sendPacket(new ExListPartyMatchingWaitingRoom(activeChar, _minLevel, _maxLevel, _page, _classes));
	}
}