package l2s.gameserver.network.authcomm.as2gs;

import l2s.gameserver.network.authcomm.AuthServerCommunication;
import l2s.gameserver.network.authcomm.ReceivablePacket;
import l2s.gameserver.network.authcomm.gs2as.PingResponse;

public class PingRequest extends ReceivablePacket
{
	@Override
	public void readImpl()
	{

	}

	@Override
	protected void runImpl()
	{
		AuthServerCommunication.getInstance().sendPacket(new PingResponse());
	}
}