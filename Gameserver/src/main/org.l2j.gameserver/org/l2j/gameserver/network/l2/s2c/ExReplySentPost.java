package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.mail.Mail;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.c2s.RequestExCancelSentPost;
import org.l2j.gameserver.network.l2.c2s.RequestExRequestSentPost;

import java.nio.ByteBuffer;

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
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(mail.getType().ordinal());
		if(mail.getType() == Mail.SenderType.SYSTEM)
		{
			buffer.putInt(mail.getSystemParams()[0]);
			buffer.putInt(mail.getSystemParams()[1]);
			buffer.putInt(mail.getSystemParams()[2]);
			buffer.putInt(mail.getSystemParams()[3]);
			buffer.putInt(mail.getSystemParams()[4]);
			buffer.putInt(mail.getSystemParams()[5]);
			buffer.putInt(mail.getSystemParams()[6]);
			buffer.putInt(mail.getSystemParams()[7]);
			buffer.putInt(mail.getSystemTopic());
			buffer.putInt(mail.getSystemBody());
		}
		else if(mail.getType() == Mail.SenderType.UNKNOWN)
		{
			buffer.putInt(3492);
			buffer.putInt(3493);
		}

		buffer.putInt(mail.getMessageId()); // id письма
		buffer.putInt(mail.isPayOnDelivery() ? 1 : 0); // 1 - письмо с запросом оплаты, 0 - просто письмо

		writeString(mail.getReceiverName(), buffer); // кому
		writeString(mail.getTopic(), buffer); // топик
		writeString(mail.getBody(), buffer); // тело

		buffer.putInt(mail.getAttachments().size()); // количество приложенных вещей
		for(ItemInstance item : mail.getAttachments())
		{
			writeItemInfo(buffer, item);
			buffer.putInt(item.getObjectId());
		}

		buffer.putLong(mail.getPrice()); // для писем с оплатой - цена
		buffer.putInt(mail.getReceiverId()); // Не известно. В сниффе оффа значение 24225 (не равняется MessageId)
	}
}