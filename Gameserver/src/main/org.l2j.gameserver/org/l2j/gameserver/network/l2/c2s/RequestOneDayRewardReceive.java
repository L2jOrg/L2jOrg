package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExOneDayReceiveRewardList;

import java.nio.ByteBuffer;

public class RequestOneDayRewardReceive extends L2GameClientPacket
{
	private int _missionId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_missionId = buffer.getShort();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getDailyMissionList().complete(_missionId))
			activeChar.sendPacket(new ExOneDayReceiveRewardList(activeChar));
	}
}