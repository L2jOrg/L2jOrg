package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.Reflection;

import java.nio.ByteBuffer;

public class RequestWithDrawalParty extends L2GameClientPacket
{
	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		Party party = activeChar.getParty();
		if(party == null)
		{
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage("Вы не можете сейчас выйти из группы."); //TODO [G1ta0] custom message
			return;
		}

		Reflection r = activeChar.getParty().getReflection();
		if(r != null && activeChar.isInCombat())
			activeChar.sendMessage("Вы не можете сейчас выйти из группы.");
		else
			activeChar.leaveParty();
	}
}