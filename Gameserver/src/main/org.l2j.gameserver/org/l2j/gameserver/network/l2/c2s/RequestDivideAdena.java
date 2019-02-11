package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExDivideAdenaDone;

import java.nio.ByteBuffer;

/**
 * @author Erlandys
 */
public class RequestDivideAdena extends L2GameClientPacket
{
	private long _count;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		buffer.getInt();
		_count = buffer.getLong();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		long count = activeChar.getAdena();
		if(_count > count)
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_PROCEED_AS_THERE_IS_INSUFFICIENT_ADENA);
			return;
		}

		int membersCount = activeChar.getParty().getMemberCount();
		long dividedCount = (long) Math.floor(_count / membersCount);
		activeChar.reduceAdena(membersCount * dividedCount, false);
		for(Player player : activeChar.getParty().getPartyMembers())
			player.addAdena(dividedCount, player.getObjectId() != activeChar.getObjectId());

		activeChar.sendPacket(new ExDivideAdenaDone(membersCount, _count, dividedCount, activeChar.getName()));
	}
}