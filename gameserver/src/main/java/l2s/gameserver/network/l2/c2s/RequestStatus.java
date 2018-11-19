package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.network.l2.s2c.SendStatus;

public final class RequestStatus extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		getClient().close(new SendStatus());
	}
}