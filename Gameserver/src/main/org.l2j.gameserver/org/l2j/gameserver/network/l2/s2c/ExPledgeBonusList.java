package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.PledgeBonusUtils;

import java.nio.ByteBuffer;

public class ExPledgeBonusList extends L2GameServerPacket
{
	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer) {
		buffer.put((byte) 0x00);
		buffer.putInt(PledgeBonusUtils.ATTENDANCE_REWARDS.get(1));
		buffer.putInt(PledgeBonusUtils.ATTENDANCE_REWARDS.get(2));
		buffer.putInt(PledgeBonusUtils.ATTENDANCE_REWARDS.get(3));
		buffer.putInt(PledgeBonusUtils.ATTENDANCE_REWARDS.get(4));
		buffer.put((byte) 0x01);
		buffer.putInt(PledgeBonusUtils.HUNTING_REWARDS.get(1));
		buffer.putInt(PledgeBonusUtils.HUNTING_REWARDS.get(2));
		buffer.putInt(PledgeBonusUtils.HUNTING_REWARDS.get(3));
		buffer.putInt(PledgeBonusUtils.HUNTING_REWARDS.get(4));
	}
}