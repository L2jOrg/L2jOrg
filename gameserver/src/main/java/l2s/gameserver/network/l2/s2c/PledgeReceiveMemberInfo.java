package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.pledge.UnitMember;

public class PledgeReceiveMemberInfo extends L2GameServerPacket
{
	private UnitMember _member;

	public PledgeReceiveMemberInfo(UnitMember member)
	{
		_member = member;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_member.getPledgeType());
		writeS(_member.getName());
		writeS(_member.getTitle());
		writeD(_member.getPowerGrade());
		writeS(_member.getSubUnit().getName());
		writeS(_member.getRelatedName()); // apprentice/sponsor name if any
	}
}