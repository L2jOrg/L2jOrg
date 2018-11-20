package org.l2j.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.Collections;

import org.l2j.gameserver.data.xml.holder.AttendanceRewardHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.templates.item.data.AttendanceRewardData;

public class ExVipAttendanceItemList extends L2GameServerPacket
{
	private final int _indexToReceive;
	private final int _lastReceivedIndex;
	private final boolean _received;
	private final Collection<AttendanceRewardData> _rewards;

	public ExVipAttendanceItemList(Player player)
	{
		_indexToReceive = player.getAttendanceRewards().getNextRewardIndex();
		_lastReceivedIndex = player.getAttendanceRewards().getReceivedRewardIndex();
		_received = player.getAttendanceRewards().isReceived();
		_rewards = _indexToReceive > 0 ? AttendanceRewardHolder.getInstance().getRewards(player.hasPremiumAccount()) : Collections.emptyList();
	}

	@Override
	protected void writeImpl()
	{
		writeByte(_indexToReceive);
		writeByte(_lastReceivedIndex);
		writeInt(0x00);
		writeInt(0x00);
		writeByte(0x01);
		writeByte(!_received);
		writeByte(250);
		writeByte(_rewards.size());
		_rewards.forEach(reward ->
		{
			writeInt(reward.getId());
			writeLong(reward.getCount());
			writeByte(reward.isUnknown());
			writeByte(reward.isBest());
		});
		writeByte(0x00);
		writeInt(0x00);
	}
}
