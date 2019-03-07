/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.network.serverpackets.attendance;

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.data.xml.impl.AttendanceRewardData;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.holders.AttendanceInfoHolder;
import com.l2jmobius.gameserver.model.holders.ItemHolder;
import com.l2jmobius.gameserver.network.OutgoingPackets;
import com.l2jmobius.gameserver.network.serverpackets.IClientOutgoingPacket;

/**
 * @author Mobius
 */
public class ExVipAttendanceItemList implements IClientOutgoingPacket
{
	boolean _available;
	int _index;
	
	public ExVipAttendanceItemList(L2PcInstance player)
	{
		final AttendanceInfoHolder attendanceInfo = player.getAttendanceInfo();
		_available = attendanceInfo.isRewardAvailable();
		_index = attendanceInfo.getRewardIndex();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_VIP_ATTENDANCE_ITEM_LIST.writeId(packet);
		packet.writeC(_available ? _index + 1 : _index); // index to receive?
		packet.writeC(_index); // last received index?
		packet.writeD(0x00);
		packet.writeD(0x00);
		packet.writeC(0x01);
		packet.writeC(_available ? 0x01 : 0x00); // player can receive reward today?
		packet.writeC(250);
		packet.writeC(AttendanceRewardData.getInstance().getRewardsCount()); // reward size
		int rewardCounter = 0;
		for (ItemHolder reward : AttendanceRewardData.getInstance().getRewards())
		{
			rewardCounter++;
			packet.writeD(reward.getId());
			packet.writeQ(reward.getCount());
			packet.writeC(0x01); // is unknown?
			packet.writeC((rewardCounter % 7) == 0 ? 0x01 : 0x00); // is last in row?
		}
		packet.writeC(0x00);
		packet.writeD(0x00);
		return true;
	}
}
