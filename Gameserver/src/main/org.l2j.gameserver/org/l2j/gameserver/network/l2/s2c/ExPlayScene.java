package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * Format: ch
 * Протокол 828: при отправке пакета клиенту ничего не происходит.
 */
public class ExPlayScene extends L2GameServerPacket
{
	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x00); //Kamael
	}
}