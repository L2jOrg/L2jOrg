package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.pledge.RankPrivs;

public class PledgePowerGradeList extends L2GameServerPacket
{
	private RankPrivs[] _privs;

	public PledgePowerGradeList(RankPrivs[] privs)
	{
		_privs = privs;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_privs.length);
		for(RankPrivs element : _privs)
		{
			writeInt(element.getRank());
			writeInt(element.getParty());
		}
	}
}