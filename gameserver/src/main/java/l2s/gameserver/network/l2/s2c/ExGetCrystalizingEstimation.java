package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.items.ItemInstance;

/**
 * @author Bonux
 **/
public class ExGetCrystalizingEstimation extends L2GameServerPacket
{
	private final int _crystalId;
	private final long _crystalCount;

	public ExGetCrystalizingEstimation(ItemInstance item)
	{
		_crystalId = item.getGrade().getCrystalId();
		_crystalCount = item.getCrystalCountOnCrystallize();
	}

	@Override
	protected final void writeImpl()
	{
		if(_crystalId > 0 && _crystalCount > 0)
		{
			writeD(0x01);
			writeD(_crystalId);
			writeQ(_crystalCount);
			writeF(100.);
		}
		else
			writeD(0x00);
	}
}