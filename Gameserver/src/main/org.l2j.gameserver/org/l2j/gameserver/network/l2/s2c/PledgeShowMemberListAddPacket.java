package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.pledge.UnitMember;

public class PledgeShowMemberListAddPacket extends L2GameServerPacket
{
	private PledgePacketMember _member;

	public PledgeShowMemberListAddPacket(UnitMember member)
	{
		_member = new PledgePacketMember(member);
	}

	@Override
	protected final void writeImpl()
	{
		writeString(_member._name);
		writeInt(_member._level);
		writeInt(_member._classId);
		writeInt(_member._sex);
		writeInt(_member._race);
		writeInt(_member._online);
		writeInt(_member._pledgeType);
		writeByte(_member._attendance);
	}

	private class PledgePacketMember
	{
		private String _name;
		private int _level;
		private int _classId;
		private int _sex;
		private int _race;
		private int _online;
		private int _pledgeType;
		private int _attendance;

		public PledgePacketMember(UnitMember m)
		{
			_name = m.getName();
			_level = m.getLevel();
			_classId = m.getClassId();
			_sex = m.getSex();
			_race = 0; //TODO m.getRace()
			_online = m.isOnline() ? m.getObjectId() : 0;
			_pledgeType = m.getPledgeType();
			_attendance = m.getAttendanceType().ordinal();
		}
	}
}