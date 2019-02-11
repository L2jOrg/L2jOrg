package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ChooseInventoryItemPacket extends L2GameServerPacket
{
	private int ItemID;

	public ChooseInventoryItemPacket(int id)
	{
		ItemID = id;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(ItemID);
	}
}