package org.l2j.gameserver.network.l2.s2c;

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
	protected void writeImpl()
	{
		writeInt(_gemstoneObjId);
		writeInt(_unk1);
		writeLong(_gemstoneCount);
		writeLong(_unk2);
		writeInt(_unk3);
	}
}