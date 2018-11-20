package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.mail.Mail;

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
	protected void writeImpl()
	{
		writeInt(_receivedBoard ? 1 : 0);
		writeInt(_mails.length);
		for(Mail mail : _mails)
		{
			writeInt(mail.getMessageId()); // postId
			writeInt(_changeId); // state
		}
	}
}