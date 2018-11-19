package l2s.gameserver.network.authcomm.gs2as;

import l2s.gameserver.network.authcomm.SendablePacket;

public class PingResponse extends SendablePacket
{
	protected void writeImpl()
	{
		writeC(0xff);
		writeQ(System.currentTimeMillis());
	}
}