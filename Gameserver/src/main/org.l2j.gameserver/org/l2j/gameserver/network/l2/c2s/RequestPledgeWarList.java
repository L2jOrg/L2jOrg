package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.s2c.PledgeReceiveWarList;

import java.nio.ByteBuffer;

public class RequestPledgeWarList extends L2GameClientPacket
{
	// format: (ch)dd
	static int _type;
	private int _page;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_page = buffer.getInt();
		_type = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		Clan clan = activeChar.getClan();
		if(clan != null)
			activeChar.sendPacket(new PledgeReceiveWarList(clan, _type, _page));
	}
}