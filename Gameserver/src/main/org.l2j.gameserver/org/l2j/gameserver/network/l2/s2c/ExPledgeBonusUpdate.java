package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExPledgeBonusUpdate extends L2GameServerPacket
{
	private final BonusType _type;
	private final int _value;

	public static enum BonusType
	{
		ATTENDANCE,
		HUNTING
	}

	public ExPledgeBonusUpdate(BonusType type, int value)
	{
		_type = type;
		_value = value;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)_type.ordinal());
		buffer.putInt(_value);
	}
}