package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.matching.MatchingRoom;

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
	protected final void writeImpl()
	{
		writeInt(_id); // room id
		writeInt(_maxMembers); //max members
		writeInt(_minLevel); //min level
		writeInt(_maxLevel); //max level
		writeInt(_lootDist); //loot distribution 1-Random 2-Random includ. etc
		writeInt(_location); //location
		writeString(_title); // room name
	}
}