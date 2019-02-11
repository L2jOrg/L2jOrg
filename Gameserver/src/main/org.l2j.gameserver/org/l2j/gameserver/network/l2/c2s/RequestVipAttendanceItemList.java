package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

public class RequestVipAttendanceItemList extends L2GameClientPacket
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

		activeChar.getAttendanceRewards().sendRewardsList(true);
	}
}