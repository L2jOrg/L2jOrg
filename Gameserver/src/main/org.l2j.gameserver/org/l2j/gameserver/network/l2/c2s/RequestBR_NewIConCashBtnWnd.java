package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.network.l2.s2c.ExBR_NewIConCashBtnWnd;
import org.l2j.gameserver.network.l2.s2c.ExLightingCandleEvent;

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
		sendPacket(ExLightingCandleEvent.ENABLED);	// TODO: Посылать при наличии новинок в Итем-молле 1, если нет, то 0.
	}
}