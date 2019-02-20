package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.List;

import org.l2j.gameserver.data.dao.MailDAO;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.mail.Mail;
import org.l2j.gameserver.network.l2.GameClient;

/**
 * @author Bonux
**/
public class ExUnReadMailCount extends L2GameServerPacket
{
	private final int _count;

	public ExUnReadMailCount(Player player)
	{
		int count = 0;
		List<Mail> mails = MailDAO.getInstance().getReceivedMailByOwnerId(player.getObjectId());
		for(Mail mail : mails)
		{
			if(mail.isUnread())
				count++;
		}
		_count = count;
	}

	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_count);
	}
}