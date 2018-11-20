package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.matching.MatchingRoom;

public class ExMpccRoomInfo extends L2GameServerPacket
{
	private int _index;
	private int _memberSize;
	private int _minLevel;
	private int _maxLevel;
	private int _lootType;
	private int _locationId;
	private String _topic;

	public ExMpccRoomInfo(MatchingRoom matching)
	{
		_index = matching.getId();
		_locationId = matching.getLocationId();
		_topic = matching.getTopic();
		_minLevel = matching.getMinLevel();
		_maxLevel = matching.getMaxLevel();
		_memberSize = matching.getMaxMembersSize();
		_lootType = matching.getLootType();
	}

	@Override
	public void writeImpl()
	{
		writeInt(_index); //index
		writeInt(_memberSize); // member size 1-50
		writeInt(_minLevel); //min level
		writeInt(_maxLevel); //max level
		writeInt(_lootType); //loot type
		writeInt(_locationId); //location id as party room
		writeString(_topic); //topic
	}
}