package org.l2j.gameserver.network.l2.c2s;

public class RequestPVPMatchRecord extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//System.out.println("Unimplemented packet: " + getType() + " | size: " + _buf.remaining());
	}

	@Override
	protected void runImpl()
	{}
}