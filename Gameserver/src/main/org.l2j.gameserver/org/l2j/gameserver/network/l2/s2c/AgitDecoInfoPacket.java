package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.base.ResidenceFunctionType;
import org.l2j.gameserver.model.entity.residence.ClanHall;
import org.l2j.gameserver.model.entity.residence.ResidenceFunction;

public class AgitDecoInfoPacket extends L2GameServerPacket
{
	private final ClanHall _clanHall;

	public AgitDecoInfoPacket(ClanHall clanHall)
	{
		_clanHall = clanHall;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_clanHall.getId());

		for(ResidenceFunctionType type : ResidenceFunctionType.VALUES)
		{
			ResidenceFunction function = _clanHall.getActiveFunction(type);
			if(function != null)
				writeByte(function.getTemplate().getDepth());
			else
				writeByte(0x00);
		}
		writeInt(0);
		writeInt(0);
		writeInt(0);
		writeInt(0);
		writeInt(0);
	}
}