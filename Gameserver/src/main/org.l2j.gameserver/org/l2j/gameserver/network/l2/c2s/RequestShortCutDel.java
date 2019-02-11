package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class RequestShortCutDel extends L2GameClientPacket
{
	private int _slot;
	private int _page;

	/**
	 * packet type id 0x3F
	 * format:		cd
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		int id = buffer.getInt();
		_slot = id % 12;
		_page = id / 12;
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;
		// client dont needs confirmation. this packet is just to inform the server
		activeChar.deleteShortCut(_slot, _page);
	}
}