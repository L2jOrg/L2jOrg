package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.mail.Mail;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

public class ExChangePostState extends L2GameServerPacket
{
	private boolean _receivedBoard;
	private Mail[] _mails;
	private int _changeId;

	public ExChangePostState(boolean receivedBoard, int type, Mail... n)
	{
		_receivedBoard = receivedBoard;
		_mails = n;
		_changeId = type;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_receivedBoard ? 1 : 0);
		buffer.putInt(_mails.length);
		for(Mail mail : _mails)
		{
			buffer.putInt(mail.getMessageId()); // postId
			buffer.putInt(_changeId); // state
		}
	}
}