package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.l2.components.SystemMsg;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class RequestExManageMpccRoom extends L2GameClientPacket
{
	private int _id;
	private int _memberSize;
	private int _minLevel;
	private int _maxLevel;
	private String _topic;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_id = buffer.getInt(); // id
		_memberSize = buffer.getInt(); // member size
		_minLevel = buffer.getInt(); //min level
		_maxLevel = buffer.getInt(); //max level
		buffer.getInt(); //lootType
		_topic = readString(buffer); //topic
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		MatchingRoom room = player.getMatchingRoom();
		if(room == null || room.getId() != _id || room.getType() != MatchingRoom.CC_MATCHING)
			return;

		if(room.getLeader() != player)
			return;

		room.setTopic(_topic);
		room.setMaxMemberSize(_memberSize);
		room.setMinLevel(_minLevel);
		room.setMaxLevel(_maxLevel);
		room.broadCast(room.infoRoomPacket());

		player.sendPacket(SystemMsg.THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED);
	}
}