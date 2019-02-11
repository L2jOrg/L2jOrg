package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.base.ResidenceFunctionType;
import org.l2j.gameserver.model.entity.residence.ClanHall;
import org.l2j.gameserver.model.entity.residence.ResidenceFunction;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class AgitDecoInfoPacket extends L2GameServerPacket
{
	private final ClanHall _clanHall;

	public AgitDecoInfoPacket(ClanHall clanHall)
	{
		_clanHall = clanHall;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_clanHall.getId());

		for(ResidenceFunctionType type : ResidenceFunctionType.VALUES)
		{
			ResidenceFunction function = _clanHall.getActiveFunction(type);
			if(function != null)
				buffer.put((byte)function.getTemplate().getDepth());
			else
				buffer.put((byte)0x00);
		}
		buffer.putInt(0);
		buffer.putInt(0);
		buffer.putInt(0);
		buffer.putInt(0);
		buffer.putInt(0);
	}
}