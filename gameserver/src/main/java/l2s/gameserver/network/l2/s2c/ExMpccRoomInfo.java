package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.matching.MatchingRoom;

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
		writeD(_index); //index
		writeD(_memberSize); // member size 1-50
		writeD(_minLevel); //min level
		writeD(_maxLevel); //max level
		writeD(_lootType); //loot type
		writeD(_locationId); //location id as party room
		writeS(_topic); //topic
	}
}