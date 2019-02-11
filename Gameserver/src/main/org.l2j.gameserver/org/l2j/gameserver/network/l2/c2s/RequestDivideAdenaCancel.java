package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExDivideAdenaCancel;

import java.nio.ByteBuffer;

/**
 * @author Erlandys
 */
public class RequestDivideAdenaCancel extends L2GameClientPacket
{
	private int _cancel;
	
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_cancel = buffer.get();
	}
	
	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(_cancel == 0)
		{
			activeChar.sendPacket(SystemMsg.ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED);
			activeChar.sendPacket(ExDivideAdenaCancel.STATIC);
		}
	}
}