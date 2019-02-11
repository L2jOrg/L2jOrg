package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;

import org.l2j.gameserver.data.xml.holder.AttendanceRewardHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
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
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.put((byte)_indexToReceive);
		buffer.put((byte)_lastReceivedIndex);
		buffer.putInt(0x00);
		buffer.putInt(0x00);
		buffer.put((byte)0x01);
		buffer.put((byte) (!_received ? 0x01 : 0x00));
		buffer.put((byte)250);
		buffer.put((byte)_rewards.size());
		_rewards.forEach(reward ->
		{
			buffer.putInt(reward.getId());
			buffer.putLong(reward.getCount());
			buffer.put((byte) (reward.isUnknown() ? 0x01 : 0x00));
			buffer.put((byte) (reward.isBest() ? 0x01 : 0x00));
		});
		buffer.put((byte)0x00);
		buffer.putInt(0x00);
	}
}
