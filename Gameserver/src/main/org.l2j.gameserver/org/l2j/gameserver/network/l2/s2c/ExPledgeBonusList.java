package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.utils.PledgeBonusUtils;

public class ExPledgeBonusList extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeInt(PledgeBonusUtils.ATTENDANCE_REWARDS.get(1));
		writeInt(PledgeBonusUtils.ATTENDANCE_REWARDS.get(2));
		writeInt(PledgeBonusUtils.ATTENDANCE_REWARDS.get(3));
		writeInt(PledgeBonusUtils.ATTENDANCE_REWARDS.get(4));
		writeInt(PledgeBonusUtils.HUNTING_REWARDS.get(1));
		writeInt(PledgeBonusUtils.HUNTING_REWARDS.get(2));
		writeInt(PledgeBonusUtils.HUNTING_REWARDS.get(3));
		writeInt(PledgeBonusUtils.HUNTING_REWARDS.get(4));
	}
}