package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.pledge.UnitMember;

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
		writeInt(_member.getPledgeType());
		writeString(_member.getName());
		writeString(_member.getTitle());
		writeInt(_member.getPowerGrade());
		writeString(_member.getSubUnit().getName());
		writeString(_member.getRelatedName()); // apprentice/sponsor name if any
	}
}