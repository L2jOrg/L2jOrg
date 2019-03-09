package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.instancemanager.DuelManager;

import java.nio.ByteBuffer;

/**
 * Format:(ch) just a trigger
 * @author -Wooden-
 */
public final class RequestDuelSurrender extends IClientIncomingPacket
{
	@Override
	public void readImpl(ByteBuffer packet)
	{
	}
	
	@Override
	public void runImpl()
	{
		DuelManager.getInstance().doSurrender(client.getActiveChar());
	}
}
