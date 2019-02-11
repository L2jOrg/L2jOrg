package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.model.pledge.RankPrivs;
import org.l2j.gameserver.model.pledge.UnitMember;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PledgeReceivePowerInfo extends L2GameServerPacket
{
	private int PowerGrade, privs;
	private String member_name;

	public PledgeReceivePowerInfo(UnitMember member)
	{
		PowerGrade = member.getPowerGrade();
		member_name = member.getName();
		if(member.isClanLeader())
			privs = Clan.CP_ALL;
		else
		{
			RankPrivs temp = member.getClan().getRankPrivs(member.getPowerGrade());
			if(temp != null)
				privs = temp.getPrivs();
			else
				privs = 0;
		}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(PowerGrade);
		writeString(member_name, buffer);
		buffer.putInt(privs);
	}
}