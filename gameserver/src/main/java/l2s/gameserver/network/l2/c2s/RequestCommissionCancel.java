package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;

public class RequestCommissionCancel extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		System.out.println("RequestCommissionCancel");
	}
}
