package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.InstantZoneHolder;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.l2.GameClient;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_type);
		buffer.putInt(_members.size());
		for(PartyRoomMemberInfo member_info : _members)
		{
			buffer.putInt(member_info.objectId);
			writeString(member_info.name, buffer);
			buffer.putInt(member_info.classId);
			buffer.putInt(member_info.level);
			buffer.putInt(member_info.location);
			buffer.putInt(member_info.memberType);
			buffer.putInt(member_info.instanceReuses.size());
			for(int i : member_info.instanceReuses)
				buffer.putInt(i);
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