package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.CustomMessage;
import l2s.gameserver.network.l2.components.SystemMsg;

public class Logout extends L2GameClientPacket
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
			activeChar.sendMessage(new CustomMessage("l2s.gameserver.network.l2.c2s.Logout.OutOfControl"));
			activeChar.sendActionFailed();
			return;
		}

		if(activeChar.isInOlympiadMode())
		{
			activeChar.sendMessage(new CustomMessage("l2s.gameserver.network.l2.c2s.Logout.Olympiad"));
			activeChar.sendActionFailed();
			return;
		}
		
		if(activeChar.isInObserverMode())
		{
			activeChar.sendMessage(new CustomMessage("l2s.gameserver.network.l2.c2s.Logout.Observer"));
			activeChar.sendActionFailed();
			return;
		}

		activeChar.kick();
	}
}