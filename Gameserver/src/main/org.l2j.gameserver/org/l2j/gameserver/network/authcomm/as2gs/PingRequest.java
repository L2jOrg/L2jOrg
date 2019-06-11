package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.PingResponse;

import java.nio.ByteBuffer;

public class PingRequest extends ReceivablePacket {

	@Override
	public void readImpl() {

	}

	@Override
	protected void runImpl()
	{
		AuthServerCommunication.getInstance().sendPacket(new PingResponse());
	}
}