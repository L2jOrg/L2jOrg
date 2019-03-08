package org.l2j.gameserver.mobius.gameserver.network.clientpackets.attributechange;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.attributechange.ExChangeAttributeFail;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public class RequestChangeAttributeCancel extends IClientIncomingPacket
{
	@Override
	public void readImpl(ByteBuffer packet) {
	}
	
	@Override
	public void runImpl()
	{
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		activeChar.sendPacket(ExChangeAttributeFail.STATIC);
	}
}