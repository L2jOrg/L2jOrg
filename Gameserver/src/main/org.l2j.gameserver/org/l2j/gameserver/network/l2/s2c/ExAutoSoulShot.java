package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.base.SoulShotType;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExAutoSoulShot extends L2GameServerPacket
{
	private final int _itemId;
	private final int _slotId;
	private final int _type;

	public ExAutoSoulShot(int itemId, int slotId, SoulShotType type)
	{
		_itemId = itemId;
		_slotId = slotId;
		_type = type.ordinal();

	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_itemId);
		buffer.putInt(_slotId);
		buffer.putInt(_type);
	}
}