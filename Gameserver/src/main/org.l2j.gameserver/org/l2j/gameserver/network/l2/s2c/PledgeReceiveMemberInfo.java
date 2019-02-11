package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.pledge.UnitMember;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PledgeReceiveMemberInfo extends L2GameServerPacket
{
	private UnitMember _member;

	public PledgeReceiveMemberInfo(UnitMember member)
	{
		_member = member;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_member.getPledgeType());
		writeString(_member.getName(), buffer);
		writeString(_member.getTitle(), buffer);
		buffer.putInt(_member.getPowerGrade());
		writeString(_member.getSubUnit().getName(), buffer);
		writeString(_member.getRelatedName(), buffer); // apprentice/sponsor name if any
	}
}