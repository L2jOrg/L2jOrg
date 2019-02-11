package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.quest.QuestState;

import java.nio.ByteBuffer;

public class RequestTutorialClientEvent extends L2GameClientPacket
{
	// format: cd
	private int _event = 0;

	/**
	 * Пакет от клиента, если вы в туториале подергали мышкой как надо - клиент пришлет его со значением 1 ну или нужным ивентом
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_event = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player player = client.getActiveChar();
		if(player == null)
			return;

		for(QuestState qs : player.getAllQuestsStates())
			qs.getQuest().notifyTutorialEvent("CE", false, String.valueOf(_event), qs);
	}
}