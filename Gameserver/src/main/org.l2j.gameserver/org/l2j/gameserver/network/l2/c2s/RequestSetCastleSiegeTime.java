package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.CastleSiegeInfoPacket;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class RequestSetCastleSiegeTime extends L2GameClientPacket
{
	private int _id, _time;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_id = buffer.getInt();
		_time = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		Castle castle = ResidenceHolder.getInstance().getResidence(Castle.class, _id);
		if(castle == null)
			return;

		if(player.getClan().getCastle() != castle.getId())
			return;

		if((player.getClanPrivileges() & Clan.CP_CS_MANAGE_SIEGE) != Clan.CP_CS_MANAGE_SIEGE)
		{
			player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_MODIFY_THE_SIEGE_TIME);
			return;
		}

		//CastleSiegeEvent siegeEvent = castle.getSiegeEvent();

		//siegeEvent.setNextSiegeTime(_time);

		player.sendPacket(new CastleSiegeInfoPacket(castle, player));
	}
}