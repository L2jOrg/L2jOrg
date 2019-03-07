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

import com.l2jmobius.commons.network.PacketWriter;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.network.NpcStringId;
import com.l2jmobius.gameserver.network.OutgoingPackets;

import java.util.Arrays;
import java.util.List;

public class ExSendUIEvent implements IClientOutgoingPacket
{
	// UI Types
	public static int TYPE_COUNT_DOWN = 0;
	public static int TYPE_REMOVE = 1;
	public static int TYPE_ISTINA = 2;
	public static int TYPE_COUNTER = 3;
	public static int TYPE_GP_TIMER = 4;
	public static int TYPE_NORNIL = 5;
	public static int TYPE_DRACO_INCUBATION_1 = 6;
	public static int TYPE_DRACO_INCUBATION_2 = 7;
	public static int TYPE_CLAN_PROGRESS_BAR = 8;
	
	private final int _objectId;
	private final int _type;
	private final int _countUp;
	private final int _startTime;
	private final int _startTime2;
	private final int _endTime;
	private final int _endTime2;
	private final int _npcstringId;
	private List<String> _params = null;
	
	/**
	 * Remove UI
	 * @param player
	 */
	public ExSendUIEvent(L2PcInstance player)
	{
		this(player, TYPE_REMOVE, 0, 0, 0, 0, 0, -1);
	}
	
	/**
	 * @param player
	 * @param uiType
	 * @param currentPoints
	 * @param maxPoints
	 * @param npcString
	 * @param params
	 */
	public ExSendUIEvent(L2PcInstance player, int uiType, int currentPoints, int maxPoints, NpcStringId npcString, String... params)
	{
		this(player, uiType, -1, currentPoints, maxPoints, -1, -1, npcString.getId(), params);
	}
	
	/**
	 * @param player
	 * @param hide
	 * @param countUp
	 * @param startTime
	 * @param endTime
	 * @param text
	 */
	public ExSendUIEvent(L2PcInstance player, boolean hide, boolean countUp, int startTime, int endTime, String text)
	{
		this(player, hide ? 1 : 0, countUp ? 1 : 0, startTime / 60, startTime % 60, endTime / 60, endTime % 60, -1, text);
	}
	
	/**
	 * @param player
	 * @param hide
	 * @param countUp
	 * @param startTime
	 * @param endTime
	 * @param npcString
	 * @param params
	 */
	public ExSendUIEvent(L2PcInstance player, boolean hide, boolean countUp, int startTime, int endTime, NpcStringId npcString, String... params)
	{
		this(player, hide ? 1 : 0, countUp ? 1 : 0, startTime / 60, startTime % 60, endTime / 60, endTime % 60, npcString.getId(), params);
	}
	
	/**
	 * @param player
	 * @param type
	 * @param countUp
	 * @param startTime
	 * @param startTime2
	 * @param endTime
	 * @param endTime2
	 * @param npcstringId
	 * @param params
	 */
	public ExSendUIEvent(L2PcInstance player, int type, int countUp, int startTime, int startTime2, int endTime, int endTime2, int npcstringId, String... params)
	{
		_objectId = player.getObjectId();
		_type = type;
		_countUp = countUp;
		_startTime = startTime;
		_startTime2 = startTime2;
		_endTime = endTime;
		_endTime2 = endTime2;
		_npcstringId = npcstringId;
		_params = Arrays.asList(params);
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.EX_SEND_UIEVENT.writeId(packet);
		
		packet.writeD(_objectId);
		packet.writeD(_type); // 0 = show, 1 = hide (there is 2 = pause and 3 = resume also but they don't work well you can only pause count down and you cannot resume it because resume hides the counter).
		packet.writeD(0); // unknown
		packet.writeD(0); // unknown
		packet.writeS(String.valueOf(_countUp)); // 0 = count down, 1 = count up timer always disappears 10 seconds before end
		packet.writeS(String.valueOf(_startTime));
		packet.writeS(String.valueOf(_startTime2));
		packet.writeS(String.valueOf(_endTime));
		packet.writeS(String.valueOf(_endTime2));
		packet.writeD(_npcstringId);
		if (_params != null)
		{
			for (String param : _params)
			{
				packet.writeS(param);
			}
		}
		return true;
	}
}
