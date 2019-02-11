package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExPutCommissionResultForVariationMake extends L2GameServerPacket
{
	private int _gemstoneObjId, _unk1, _unk3;
	private long _gemstoneCount, _unk2;

	public ExPutCommissionResultForVariationMake(int gemstoneObjId, long count)
	{
		_gemstoneObjId = gemstoneObjId;
		_unk1 = 1;
		_gemstoneCount = count;
		_unk2 = 1;
		_unk3 = 1;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_gemstoneObjId);
		buffer.putInt(_unk1);
		buffer.putLong(_gemstoneCount);
		buffer.putLong(_unk2);
		buffer.putInt(_unk3);
	}
}