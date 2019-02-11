package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * Format: ch S
 */
public class ExAskJoinPartyRoom extends L2GameServerPacket
{
	private String _charName;
	private String _roomName;

	public ExAskJoinPartyRoom(String charName, String roomName)
	{
		_charName = charName;
		_roomName = roomName;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_charName, buffer);
		writeString(_roomName, buffer);
	}
}