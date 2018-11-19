package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.pledge.SubUnit;
import l2s.gameserver.model.pledge.UnitMember;

public class PledgeShowMemberListUpdatePacket extends L2GameServerPacket
{
	private String _name;
	private int _lvl;
	private int _classId;
	private int _sex;
	private int _isOnline;
	private int _objectId;
	private int _pledgeType;
	private int _isApprentice;
	private int _attendance;

	public PledgeShowMemberListUpdatePacket(final Player player)
	{
		_name = player.getName();
		_lvl = player.getLevel();
		_classId = player.getClassId().getId();
		_sex = player.getSex().ordinal();
		_objectId = player.getObjectId();
		_isOnline = player.isOnline() ? 1 : 0;
		_pledgeType = player.getPledgeType();
		SubUnit subUnit = player.getSubUnit();
		UnitMember member = subUnit == null ? null : subUnit.getUnitMember(_objectId);
		if(member != null)
		{
			_isApprentice = member.hasSponsor() ? 1 : 0;
			_attendance = member.getAttendanceType().ordinal();
		}
	}

	public PledgeShowMemberListUpdatePacket(final UnitMember cm)
	{
		_name = cm.getName();
		_lvl = cm.getLevel();
		_classId = cm.getClassId();
		_sex = cm.getSex();
		_objectId = cm.getObjectId();
		_isOnline = cm.isOnline() ? 1 : 0;
		_pledgeType = cm.getPledgeType();
		_isApprentice = cm.hasSponsor() ? 1 : 0;
		_attendance = cm.getAttendanceType().ordinal();
	}

	@Override
	protected final void writeImpl()
	{
		writeS(_name);
		writeD(_lvl);
		writeD(_classId);
		writeD(_sex);
		writeD(_objectId);
		writeD(_isOnline); // 1=online 0=offline
		writeD(_pledgeType);
		writeD(_isApprentice); // does a clan member have a sponsor
		writeC(_attendance);
	}
}