package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;

import java.nio.ByteBuffer;

/**
 * @author VISTALL
 * @date 23:33/23.03.2011
 */
public class RequestGoodsInventoryInfo extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer) throws Exception
	{

	}

	@Override
	protected void runImpl() throws Exception
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;
	}
}