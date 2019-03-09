package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.network.serverpackets.AllyCrest;

import java.nio.ByteBuffer;

/**
 * This class ...
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestAllyCrest extends IClientIncomingPacket
{
	private int _crestId;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_crestId = packet.getInt();
		packet.getInt(); // Ally ID
		packet.getInt(); // Server ID
	}
	
	@Override
	public void runImpl()
	{
		client.sendPacket(new AllyCrest(_crestId));
	}
}
