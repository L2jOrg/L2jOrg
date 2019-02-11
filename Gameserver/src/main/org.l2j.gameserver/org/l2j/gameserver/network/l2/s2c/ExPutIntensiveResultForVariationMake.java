package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExPutIntensiveResultForVariationMake extends L2GameServerPacket
{
	private int _refinerItemObjId, _lifestoneItemId, _gemstoneItemId, _unk;
	private long _gemstoneCount;

	public ExPutIntensiveResultForVariationMake(int refinerItemObjId, int lifeStoneId, int gemstoneItemId, long gemstoneCount)
	{
		_refinerItemObjId = refinerItemObjId;
		_lifestoneItemId = lifeStoneId;
		_gemstoneItemId = gemstoneItemId;
		_gemstoneCount = gemstoneCount;
		_unk = 1;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_refinerItemObjId);
		buffer.putInt(_lifestoneItemId);
		buffer.putInt(_gemstoneItemId);
		buffer.putLong(_gemstoneCount);
		buffer.putInt(_unk);
	}
}