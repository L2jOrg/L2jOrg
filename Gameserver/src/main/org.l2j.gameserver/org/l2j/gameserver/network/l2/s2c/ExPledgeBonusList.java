package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.utils.PledgeBonusUtils;

public class ExPledgeBonusList extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeD(PledgeBonusUtils.ATTENDANCE_REWARDS.get(1));
		writeD(PledgeBonusUtils.ATTENDANCE_REWARDS.get(2));
		writeD(PledgeBonusUtils.ATTENDANCE_REWARDS.get(3));
		writeD(PledgeBonusUtils.ATTENDANCE_REWARDS.get(4));
		writeD(PledgeBonusUtils.HUNTING_REWARDS.get(1));
		writeD(PledgeBonusUtils.HUNTING_REWARDS.get(2));
		writeD(PledgeBonusUtils.HUNTING_REWARDS.get(3));
		writeD(PledgeBonusUtils.HUNTING_REWARDS.get(4));
	}
}