package l2s.gameserver.network.l2.s2c;

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
	protected void writeImpl()
	{
		writeD(_refinerItemObjId);
		writeD(_lifestoneItemId);
		writeD(_gemstoneItemId);
		writeQ(_gemstoneCount);
		writeD(_unk);
	}
}