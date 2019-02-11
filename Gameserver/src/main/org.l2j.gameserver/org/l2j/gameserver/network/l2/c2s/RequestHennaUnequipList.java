package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.HennaUnequipListPacket;

import java.nio.ByteBuffer;

public class RequestHennaUnequipList extends L2GameClientPacket
{
	private int _symbolId;

	/**
	 * format: d
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_symbolId = buffer.getInt(); //?
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		HennaUnequipListPacket he = new HennaUnequipListPacket(activeChar);
		activeChar.sendPacket(he);
	}
}