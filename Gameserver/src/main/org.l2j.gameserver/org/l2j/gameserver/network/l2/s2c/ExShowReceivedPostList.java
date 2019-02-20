package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.data.dao.MailDAO;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.mail.Mail;
import org.l2j.gameserver.network.l2.GameClient;

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
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt((int) (System.currentTimeMillis() / 1000L));
		buffer.putInt(_mails.length); // количество писем
		for(Mail mail : _mails)
		{
			buffer.putInt(mail.getType().ordinal()); // тип письма

			if(mail.getType() == Mail.SenderType.SYSTEM)
				buffer.putInt(mail.getSystemTopic());

			buffer.putInt(mail.getMessageId()); // уникальный id письма
			writeString(mail.getTopic(), buffer); // топик
			writeString(mail.getSenderName(), buffer); // отправитель
			buffer.putInt(mail.isPayOnDelivery() ? 1 : 0); // если тут 1 то письмо требует оплаты
			buffer.putInt(mail.getExpireTime()); // время действительности письма
			buffer.putInt(mail.isUnread() ? 1 : 0); // письмо не прочитано - его нельзя удалить и оно выделяется ярким цветом
			buffer.putInt(mail.isReturnable() ? 1 : 0); // returnable
			buffer.putInt(mail.getAttachments().isEmpty() ? 0 : 1); // 1 - письмо с приложением, 0 - просто письмо
			buffer.putInt(mail.isReturned() ? 1 : 0);
			buffer.putInt(mail.getReceiverId());
		}
		buffer.putInt(100);
		buffer.putInt(1000);
	}
}