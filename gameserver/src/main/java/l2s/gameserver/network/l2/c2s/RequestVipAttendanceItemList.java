package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class RequestVipAttendanceItemList extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		activeChar.getAttendanceRewards().sendRewardsList(true);
	}
}