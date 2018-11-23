package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.listener.actor.player.OnAnswerListener;
import org.l2j.gameserver.model.Player;

import org.napile.pair.primitive.IntObjectPair;

public class ConfirmDlg extends L2GameClientPacket
{
	private int _answer, _requestId;

	@Override
	protected void readImpl()
	{
		readInt();
		_answer = readInt();
		_requestId = readInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
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