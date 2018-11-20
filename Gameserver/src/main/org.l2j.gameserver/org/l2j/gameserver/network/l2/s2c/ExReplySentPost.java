package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.mail.Mail;
import org.l2j.gameserver.network.l2.c2s.RequestExCancelSentPost;
import org.l2j.gameserver.network.l2.c2s.RequestExRequestSentPost;

/**
 * Просмотр собственного отправленного письма. Шлется в ответ на {@link RequestExRequestSentPost}.
 * При нажатии на кнопку Cancel клиент шлет {@link RequestExCancelSentPost}.
 * @see ExReplyReceivedPost
 */
public class ExReplySentPost extends L2GameServerPacket
{
	private final Mail mail;

	public ExReplySentPost(Mail mail)
	{
		this.mail = mail;
	}

	// ddSSS dx[hddQdddhhhhhhhhhh] Qd
	@Override
	protected void writeImpl()
	{
		writeInt(mail.getType().ordinal());
		if(mail.getType() == Mail.SenderType.SYSTEM)
		{
			writeInt(mail.getSystemParams()[0]);
			writeInt(mail.getSystemParams()[1]);
			writeInt(mail.getSystemParams()[2]);
			writeInt(mail.getSystemParams()[3]);
			writeInt(mail.getSystemParams()[4]);
			writeInt(mail.getSystemParams()[5]);
			writeInt(mail.getSystemParams()[6]);
			writeInt(mail.getSystemParams()[7]);
			writeInt(mail.getSystemTopic());
			writeInt(mail.getSystemBody());
		}
		else if(mail.getType() == Mail.SenderType.UNKNOWN)
		{
			writeInt(3492);
			writeInt(3493);
		}

		writeInt(mail.getMessageId()); // id письма
		writeInt(mail.isPayOnDelivery() ? 1 : 0); // 1 - письмо с запросом оплаты, 0 - просто письмо

		writeString(mail.getReceiverName()); // кому
		writeString(mail.getTopic()); // топик
		writeString(mail.getBody()); // тело

		writeInt(mail.getAttachments().size()); // количество приложенных вещей
		for(ItemInstance item : mail.getAttachments())
		{
			writeItemInfo(item);
			writeInt(item.getObjectId());
		}

		writeLong(mail.getPrice()); // для писем с оплатой - цена
		writeInt(mail.getReceiverId()); // Не известно. В сниффе оффа значение 24225 (не равняется MessageId)
	}
}