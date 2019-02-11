package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.pledge.RankPrivs;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class PledgePowerGradeList extends L2GameServerPacket
{
	private RankPrivs[] _privs;

	public PledgePowerGradeList(RankPrivs[] privs)
	{
		_privs = privs;
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_privs.length);
		for(RankPrivs element : _privs)
		{
			buffer.putInt(element.getRank());
			buffer.putInt(element.getParty());
		}
	}
}