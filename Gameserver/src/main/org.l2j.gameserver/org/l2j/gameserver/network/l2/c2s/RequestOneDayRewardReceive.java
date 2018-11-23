package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.ExOneDayReceiveRewardList;

public class RequestOneDayRewardReceive extends L2GameClientPacket
{
	private int _missionId;

	@Override
	protected void readImpl()
	{
		_missionId = readShort();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.getDailyMissionList().complete(_missionId))
			activeChar.sendPacket(new ExOneDayReceiveRewardList(activeChar));
	}
}