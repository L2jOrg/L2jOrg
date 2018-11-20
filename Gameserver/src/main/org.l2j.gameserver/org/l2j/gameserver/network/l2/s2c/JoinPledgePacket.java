package org.l2j.gameserver.network.l2.s2c;

public class JoinPledgePacket extends L2GameServerPacket
{
	private int _pledgeId;

	public JoinPledgePacket(int pledgeId)
	{
		_pledgeId = pledgeId;
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(_pledgeId);
	}
}