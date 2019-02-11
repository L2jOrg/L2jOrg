package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class GameGuardQuery extends L2GameServerPacket
{
	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x00); // ? - Меняется при каждом перезаходе.
		buffer.putInt(0x00); // ? - Меняется при каждом перезаходе.
		buffer.putInt(0x00); // ? - Меняется при каждом перезаходе.
		buffer.putInt(0x00); // ? - Меняется при каждом перезаходе.
	}
}