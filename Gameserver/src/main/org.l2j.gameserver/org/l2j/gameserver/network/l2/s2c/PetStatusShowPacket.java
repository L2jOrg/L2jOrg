package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Servitor;

public class PetStatusShowPacket extends L2GameServerPacket
{
	private int _summonType, _summonObjId;

	public PetStatusShowPacket(Servitor summon)
	{
		_summonType = summon.getServitorType();
		_summonObjId = summon.getObjectId();
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_summonType);
		writeInt(_summonObjId);
	}
}