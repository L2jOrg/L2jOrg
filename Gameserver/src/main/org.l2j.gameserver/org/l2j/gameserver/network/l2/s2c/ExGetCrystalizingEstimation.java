package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		if(_crystalId > 0 && _crystalCount > 0)
		{
			buffer.putInt(0x01);
			buffer.putInt(_crystalId);
			buffer.putLong(_crystalCount);
			buffer.putDouble(100.);
		}
		else
			buffer.putInt(0x00);
	}
}