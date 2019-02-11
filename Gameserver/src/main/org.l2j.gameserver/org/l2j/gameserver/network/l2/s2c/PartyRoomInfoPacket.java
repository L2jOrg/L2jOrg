package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PartyRoomInfoPacket extends L2GameServerPacket
{
	private int _id;
	private int _minLevel;
	private int _maxLevel;
	private int _lootDist;
	private int _maxMembers;
	private int _location;
	private String _title;

	public PartyRoomInfoPacket(MatchingRoom room)
	{
		_id = room.getId();
		_minLevel = room.getMinLevel();
		_maxLevel = room.getMaxLevel();
		_lootDist = room.getLootType();
		_maxMembers = room.getMaxMembersSize();
		_location = room.getLocationId();
		_title = room.getTopic();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_id); // room id
		buffer.putInt(_maxMembers); //max members
		buffer.putInt(_minLevel); //min level
		buffer.putInt(_maxLevel); //max level
		buffer.putInt(_lootDist); //loot distribution 1-Random 2-Random includ. etc
		buffer.putInt(_location); //location
		writeString(_title, buffer); // room name
	}
}