package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.listener.actor.player.OnAnswerListener;
import org.l2j.gameserver.model.Player;

import io.github.joealisson.primitive.pair.IntObjectPair;

import java.nio.ByteBuffer;

public class ConfirmDlg extends L2GameClientPacket
{
	private int _answer, _requestId;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		buffer.getInt();
		_answer = buffer.getInt();
		_requestId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		IntObjectPair<OnAnswerListener> entry = activeChar.getAskListener(true);
		if(entry == null || entry.getKey() != _requestId)
			return;

		OnAnswerListener listener = entry.getValue();
		if(_answer == 1)
			listener.sayYes();
		else
			listener.sayNo();
	}
}