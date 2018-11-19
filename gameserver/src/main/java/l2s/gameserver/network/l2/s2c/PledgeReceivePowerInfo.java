package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.model.pledge.RankPrivs;
import l2s.gameserver.model.pledge.UnitMember;

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
	protected final void writeImpl()
	{
		writeD(PowerGrade);
		writeS(member_name);
		writeD(privs);
	}
}