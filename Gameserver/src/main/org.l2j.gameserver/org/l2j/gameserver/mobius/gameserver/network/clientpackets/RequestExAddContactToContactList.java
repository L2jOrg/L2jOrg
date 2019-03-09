package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExConfirmAddingContact;

import java.nio.ByteBuffer;

/**
 * Format: (ch)S S: Character Name
 * @author UnAfraid & mrTJO
 */
public class RequestExAddContactToContactList extends IClientIncomingPacket
{
	private String _name;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_name = readString(packet);
	}
	
	@Override
	public void runImpl()
	{
		if (!Config.ALLOW_MAIL)
		{
			return;
		}
		
		if (_name == null)
		{
			return;
		}
		
		final L2PcInstance activeChar = client.getActiveChar();
		if (activeChar == null)
		{
			return;
		}
		
		final boolean charAdded = activeChar.getContactList().add(_name);
		activeChar.sendPacket(new ExConfirmAddingContact(_name, charAdded));
	}
}
