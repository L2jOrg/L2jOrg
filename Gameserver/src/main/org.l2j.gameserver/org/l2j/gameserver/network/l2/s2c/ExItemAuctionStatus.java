package org.l2j.gameserver.network.l2.s2c;

public class ExItemAuctionStatus extends L2GameServerPacket
{
	@Override
	protected final void writeImpl()
	{
		writeH(0);
		writeH(0);
		writeH(0);
		writeH(0);
		writeH(0);
		writeH(0);
		writeD(0);
		writeC(0);
	}
}