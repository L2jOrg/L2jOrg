package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.pledge.Clan;

import java.nio.ByteBuffer;

public class RequestPledgeMemberList extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;
		Clan clan = activeChar.getClan();
		if(clan != null)
		{
			activeChar.sendPacket(clan.listAll());
		}
	}
}