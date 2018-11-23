package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInstance;

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
			writeInt(0x01);
			writeInt(_crystalId);
			writeLong(_crystalCount);
			writeDouble(100.);
		}
		else
			writeInt(0x00);
	}
}