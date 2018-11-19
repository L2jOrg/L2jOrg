package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.network.l2.s2c.ExBR_NewIConCashBtnWnd;

/**
 * @author Bonux
**/
public class RequestBR_NewIConCashBtnWnd extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{
		//
	}

	@Override
	protected void runImpl()
	{
		sendPacket(ExBR_NewIConCashBtnWnd.HAS_UPDATES);	// TODO: Посылать при наличии новинок в Итем-молле 1, если нет, то 0.
	}
}