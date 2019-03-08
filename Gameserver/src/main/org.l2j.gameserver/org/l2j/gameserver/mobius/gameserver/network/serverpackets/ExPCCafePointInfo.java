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
package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.commons.network.PacketWriter;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author KenM
 * @author UnAfraid
 */
public class ExPCCafePointInfo implements IClientOutgoingPacket
{
	private final int _points;
	private final int _mAddPoint;
	private final int _mPeriodType;
	private final int _remainTime;
	private final int _pointType;
	private final int _time;
	
	public ExPCCafePointInfo()
	{
		_points = 0;
		_mAddPoint = 0;
		_remainTime = 0;
		_mPeriodType = 0;
		_pointType = 0;
		_time = 0;
	}
	
	public ExPCCafePointInfo(int points, int pointsToAdd, int time)
	{
		_points = points;
		_mAddPoint = pointsToAdd;
		_mPeriodType = 1;
		_remainTime = 42; // No idea why but retail sends 42..
		_pointType = pointsToAdd < 0 ? 3 : 0; // When using points is 3
		_time = time;
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_PCCAFE_POINT_INFO.writeId(packet);
		
		packet.writeD(_points); // num points
		packet.writeD(_mAddPoint); // points inc display
		packet.writeC(_mPeriodType); // period(0=don't show window,1=acquisition,2=use points)
		packet.writeD(_remainTime); // period hours left
		packet.writeC(_pointType); // points inc display color(0=yellow, 1=cyan-blue, 2=red, all other black)
		packet.writeD(_time * 3); // value is in seconds * 3
		return true;
	}
}
