package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PetStatusShowPacket extends L2GameServerPacket
{
	private int _summonType, _summonObjId;

	public PetStatusShowPacket(Servitor summon)
	{
		_summonType = summon.getServitorType();
		_summonObjId = summon.getObjectId();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_summonType);
		buffer.putInt(_summonObjId);
	}
}