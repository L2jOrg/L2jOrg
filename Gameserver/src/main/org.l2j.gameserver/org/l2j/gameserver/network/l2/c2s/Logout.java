package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.components.SystemMsg;

import java.nio.ByteBuffer;

public class Logout extends L2GameClientPacket
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

		// Dont allow leaving if player is fighting
		if(activeChar.isInCombat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_EXIT_THE_GAME_WHILE_IN_COMBAT);
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isBlocked() && !activeChar.isFlying() && !activeChar.isInAwayingMode()) // Разрешаем выходить из игры если используется сервис HireWyvern. Вернет в начальную точку.
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.network.l2.c2s.Logout.OutOfControl"));
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.network.l2.c2s.Logout.Olympiad"));
			activeChar.sendActionFailed();
			return;
		}
		
		if(activeChar.isInObserverMode())
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.network.l2.c2s.Logout.Observer"));
			activeChar.sendActionFailed();
			return;
		}

		activeChar.kick();
	}
}