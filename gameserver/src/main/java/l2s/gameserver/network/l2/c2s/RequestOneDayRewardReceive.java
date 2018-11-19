package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExOneDayReceiveRewardList;

public class RequestOneDayRewardReceive extends L2GameClientPacket
{
	private int _missionId;

	@Override
	protected void readImpl()
	{
		_missionId = readH();
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