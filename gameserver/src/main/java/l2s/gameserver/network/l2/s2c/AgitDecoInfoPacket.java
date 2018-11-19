package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.base.ResidenceFunctionType;
import l2s.gameserver.model.entity.residence.ClanHall;
import l2s.gameserver.model.entity.residence.ResidenceFunction;

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
		writeD(_clanHall.getId());

		for(ResidenceFunctionType type : ResidenceFunctionType.VALUES)
		{
			ResidenceFunction function = _clanHall.getActiveFunction(type);
			if(function != null)
				writeC(function.getTemplate().getDepth());
			else
				writeC(0x00);
		}
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
	}
}