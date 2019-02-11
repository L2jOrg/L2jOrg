package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.dao.MailDAO;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.mail.Mail;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.c2s.RequestExDeleteSentPost;
import org.l2j.gameserver.network.l2.c2s.RequestExRequestSentPost;
import org.l2j.gameserver.network.l2.c2s.RequestExRequestSentPostList;

/**
 * Появляется при нажатии на кнопку "sent mail", исходящие письма
 * Ответ на {@link RequestExRequestSentPostList}
 * При нажатии на письмо в списке шлется {@link RequestExRequestSentPost}, а в ответ {@link ExReplySentPost}.
 * При нажатии на "delete" шлется {@link RequestExDeleteSentPost}.
 * @see ExShowReceivedPostList аналогичный список принятой почты
 */
public class ExShowSentPostList extends L2GameServerPacket
{
	private final List<Mail> mails;

	public ExShowSentPostList(Player cha)
	{
		mails = MailDAO.getInstance().getSentMailByOwnerId(cha.getObjectId());
		Collections.sort(mails);
	}

	// d dx[dSSddddd]
	@Override
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt((int) (System.currentTimeMillis() / 1000L));
		buffer.putInt(mails.size()); // количество писем
		for(Mail mail : mails)
		{
			buffer.putInt(mail.getMessageId()); // уникальный id письма
			writeString(mail.getTopic(), buffer); // топик
			writeString(mail.getReceiverName(), buffer); // получатель
			buffer.putInt(mail.isPayOnDelivery() ? 1 : 0); // если тут 1 то письмо требует оплаты
			buffer.putInt(mail.getExpireTime()); // время действительности письма
			buffer.putInt(mail.isUnread() ? 1 : 0); // ?
			buffer.putInt(mail.isReturnable() ? 1 : 0); // returnable
			buffer.putInt(mail.getAttachments().isEmpty() ? 0 : 1); // 1 - письмо с приложением, 0 - просто письмо
			buffer.putInt(0x00); // ???
		}
	}
}