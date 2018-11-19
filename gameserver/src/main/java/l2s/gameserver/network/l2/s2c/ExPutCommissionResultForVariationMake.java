package l2s.gameserver.network.l2.s2c;

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
		writeD(_gemstoneObjId);
		writeD(_unk1);
		writeQ(_gemstoneCount);
		writeQ(_unk2);
		writeD(_unk3);
	}
}