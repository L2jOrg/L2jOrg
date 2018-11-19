package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Servitor;

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
		writeD(_summonType);
		writeD(_summonObjId);
	}
}