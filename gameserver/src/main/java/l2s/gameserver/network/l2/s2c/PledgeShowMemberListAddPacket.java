package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.pledge.UnitMember;

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
		writeS(_member._name);
		writeD(_member._level);
		writeD(_member._classId);
		writeD(_member._sex);
		writeD(_member._race);
		writeD(_member._online);
		writeD(_member._pledgeType);
		writeC(_member._attendance);
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