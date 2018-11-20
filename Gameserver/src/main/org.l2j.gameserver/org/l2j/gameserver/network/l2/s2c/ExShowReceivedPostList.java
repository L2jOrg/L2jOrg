package org.l2j.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.dao.MailDAO;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.mail.Mail;

/**
 * Появляется при нажатии на кнопку "почта" или "received mail", входящие письма
 * <br> Ответ на {@link RequestExRequestReceivedPostList}.
 * <br> При нажатии на письмо в списке шлется {@link RequestExRequestReceivedPost} а в ответ {@link ExReplyReceivedPost}.
 * <br> При попытке удалить письмо шлется {@link RequestExDeleteReceivedPost}.
 * <br> При нажатии кнопки send mail шлется {@link RequestExPostItemList}.
 * @see ExShowSentPostList аналогичный список отправленной почты
 */
public class ExShowReceivedPostList extends L2GameServerPacket
{
	private final Mail[] _mails;

	public ExShowReceivedPostList(Player cha)
	{
		List<Mail> mails = MailDAO.getInstance().getReceivedMailByOwnerId(cha.getObjectId());
		Collections.sort(mails);
		_mails = mails.toArray(new Mail[mails.size()]);
	}

	// d dx[dSSddddddd]
	@Override
	protected void writeImpl()
	{
		writeInt((int) (System.currentTimeMillis() / 1000L));
		writeInt(_mails.length); // количество писем
		for(Mail mail : _mails)
		{
			writeInt(mail.getType().ordinal()); // тип письма

			if(mail.getType() == Mail.SenderType.SYSTEM)
				writeInt(mail.getSystemTopic());

			writeInt(mail.getMessageId()); // уникальный id письма
			writeString(mail.getTopic()); // топик
			writeString(mail.getSenderName()); // отправитель
			writeInt(mail.isPayOnDelivery() ? 1 : 0); // если тут 1 то письмо требует оплаты
			writeInt(mail.getExpireTime()); // время действительности письма
			writeInt(mail.isUnread() ? 1 : 0); // письмо не прочитано - его нельзя удалить и оно выделяется ярким цветом
			writeInt(mail.isReturnable()); // returnable
			writeInt(mail.getAttachments().isEmpty() ? 0 : 1); // 1 - письмо с приложением, 0 - просто письмо
			writeInt(mail.isReturned() ? 1 : 0);
			writeInt(mail.getReceiverId());
		}
		writeInt(100);
		writeInt(1000);
	}
}