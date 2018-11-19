package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.entity.Reflection;
import l2s.gameserver.network.l2.components.CustomMessage;

public class RequestWithDrawalParty extends L2GameClientPacket
{
	@Override
	protected void readImpl()
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
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