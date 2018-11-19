package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import l2s.gameserver.data.xml.holder.InstantZoneHolder;
import l2s.gameserver.instancemanager.MatchingRoomManager;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.matching.MatchingRoom;

/**
 * Format:(ch) d d [dsdddd]
 */
public class ExPartyRoomMemberPacket extends L2GameServerPacket
{
	private int _type;
	private List<PartyRoomMemberInfo> _members = Collections.emptyList();

	public ExPartyRoomMemberPacket(MatchingRoom room, Player activeChar)
	{
		_type = room.getMemberType(activeChar);
		_members = new ArrayList<PartyRoomMemberInfo>(room.getPlayers().size());
		for(Player $member : room.getPlayers())
			_members.add(new PartyRoomMemberInfo($member, room.getMemberType($member)));
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_type);
		writeD(_members.size());
		for(PartyRoomMemberInfo member_info : _members)
		{
			writeD(member_info.objectId);
			writeS(member_info.name);
			writeD(member_info.classId);
			writeD(member_info.level);
			writeD(member_info.location);
			writeD(member_info.memberType);
			writeD(member_info.instanceReuses.size());
			for(int i : member_info.instanceReuses)
				writeD(i);
		}
	}

	static class PartyRoomMemberInfo
	{
		public final int objectId, classId, level, location, memberType;
		public final String name;
		public final List<Integer> instanceReuses;

		public PartyRoomMemberInfo(Player member, int type)
		{
			objectId = member.getObjectId();
			name = member.getName();
			classId = member.getClassId().ordinal();
			level = member.getLevel();
			location = MatchingRoomManager.getInstance().getLocation(member);
			memberType = type;
			instanceReuses = InstantZoneHolder.getInstance().getLockedInstancesList(member);
		}
	}
}