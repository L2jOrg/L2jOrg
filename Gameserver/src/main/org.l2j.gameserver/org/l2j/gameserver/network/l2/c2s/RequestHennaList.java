package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.HennaEquipListPacket;

public class RequestHennaList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//readInt(); - unknown
	}

	@Override
	protected void runImpl()
	{
		Player player = getClient().getActiveChar();
		if(player == null)
			return;

		player.sendPacket(new HennaEquipListPacket(player));
	}
}