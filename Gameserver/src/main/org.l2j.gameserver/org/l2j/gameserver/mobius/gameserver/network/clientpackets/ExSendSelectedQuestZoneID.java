package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * @author St3eT
 */
public final class ExSendSelectedQuestZoneID extends IClientIncomingPacket
{
	private int _questZoneId;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_questZoneId = packet.getInt();
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		activeChar.setQuestZoneId(_questZoneId);
	}
}
