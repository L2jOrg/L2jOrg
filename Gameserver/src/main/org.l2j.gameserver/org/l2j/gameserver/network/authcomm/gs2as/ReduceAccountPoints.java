package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.SendablePacket;

public class ReduceAccountPoints extends SendablePacket
{
	private String account;
	private int count;

	public ReduceAccountPoints(String account, int count)
	{
		this.account = account;
		this.count = count;
	}

	protected void writeImpl()
	{
		writeC(0x12);
		writeS(account);
		writeD(count);
	}
}