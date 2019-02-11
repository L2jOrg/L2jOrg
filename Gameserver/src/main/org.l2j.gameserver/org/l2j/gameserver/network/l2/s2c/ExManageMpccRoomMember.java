package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 */
public class ExManageMpccRoomMember extends L2GameServerPacket
{
	public static int ADD_MEMBER = 0;
	public static int UPDATE_MEMBER = 1;
	public static int REMOVE_MEMBER = 2;

	private int _type;
	private MpccRoomMemberInfo _memberInfo;

	public ExManageMpccRoomMember(int type, MatchingRoom room, Player target)
	{
		_type = type;
		_memberInfo = (new MpccRoomMemberInfo(target, room.getMemberType(target)));
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_type);
		buffer.putInt(_memberInfo.objectId);
		writeString(_memberInfo.name, buffer);
		buffer.putInt(_memberInfo.classId);
		buffer.putInt(_memberInfo.level);
		buffer.putInt(_memberInfo.location);
		buffer.putInt(_memberInfo.memberType);
	}

	static class MpccRoomMemberInfo
	{
		public final int objectId;
		public final int classId;
		public final int level;
		public final int location;
		public final int memberType;
		public final String name;

		public MpccRoomMemberInfo(Player member, int type)
		{
			this.objectId = member.getObjectId();
			this.name = member.getName();
			this.classId = member.getClassId().ordinal();
			this.level = member.getLevel();
			this.location = MatchingRoomManager.getInstance().getLocation(member);
			this.memberType = type;
		}
	}
}