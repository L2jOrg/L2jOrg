package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.UserInfo;

import java.nio.ByteBuffer;

/**
 * Appearing Packet Handler
 * <p>
 * <p>
 * 0000: 30
 * <p>
 * <p>
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/29 23:15:33 $
 */
public final class Appearing extends IClientIncomingPacket
{
	@Override
	public void readImpl(ByteBuffer packet)
	{

	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		if (activeChar.isTeleporting())
		{
			activeChar.onTeleported();
		}
		
		client.sendPacket(new UserInfo(activeChar));
	}
}
