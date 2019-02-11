package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.network.l2.s2c.CastleSiegeDefenderListPacket;

import java.nio.ByteBuffer;

public class RequestCastleSiegeDefenderList extends L2GameClientPacket
{
	private int _unitId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_unitId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _unitId);
		if(castle == null)
			return;

		player.sendPacket(new CastleSiegeDefenderListPacket(castle));
	}
}