package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient.GameClientState;
import org.l2j.gameserver.network.l2.components.CustomMessage;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ActionFailPacket;
import org.l2j.gameserver.network.l2.s2c.CharacterSelectionInfoPacket;
import org.l2j.gameserver.network.l2.s2c.RestartResponsePacket;

import java.nio.ByteBuffer;

public class RequestRestart extends L2GameClientPacket
{
	/**
	 * packet type id 0x57
	 * format:      c
     * @param buffer
     */

	@Override
	protected void readImpl(ByteBuffer buffer)
	{}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();

		if(activeChar == null)
			return;

		if(activeChar.isInObserverMode())
		{
			activeChar.sendPacket(SystemMsg.OBSERVERS_CANNOT_PARTICIPATE, RestartResponsePacket.FAIL, ActionFailPacket.STATIC);
			return;
		}

		if(activeChar.isInCombat())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_RESTART_WHILE_IN_COMBAT, RestartResponsePacket.FAIL, ActionFailPacket.STATIC);
			return;
		}

		if(activeChar.isFishing())
		{
			activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2, RestartResponsePacket.FAIL, ActionFailPacket.STATIC);
			return;
		}

		if(activeChar.isBlocked() && !activeChar.isFlying()) // Разрешаем выходить из игры если используется сервис HireWyvern. Вернет в начальную точку.
		{
			activeChar.sendMessage(new CustomMessage("org.l2j.gameserver.network.l2.c2s.RequestRestart.OutOfControl"));
			activeChar.sendPacket(RestartResponsePacket.FAIL, ActionFailPacket.STATIC);
			return;
		}
		
		if(client != null)
			client.setState(GameClientState.AUTHED);

		activeChar.restart();
		// send char list
		CharacterSelectionInfoPacket cl = new CharacterSelectionInfoPacket(client);
		sendPacket(RestartResponsePacket.OK, cl);
		client.setCharSelection(cl.getCharInfo());
	}
}