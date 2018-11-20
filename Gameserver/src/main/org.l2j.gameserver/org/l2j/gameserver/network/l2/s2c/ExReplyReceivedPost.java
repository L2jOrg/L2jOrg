package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.mail.Mail;
import org.l2j.gameserver.network.l2.c2s.RequestExReceivePost;
import org.l2j.gameserver.network.l2.c2s.RequestExRejectPost;
import org.l2j.gameserver.network.l2.c2s.RequestExRequestReceivedPost;

/**
 * Просмотр полученного письма. Шлется в ответ на {@link RequestExRequestReceivedPost}.
 * При попытке забрать приложенные вещи клиент шлет {@link RequestExReceivePost}.
 * При возврате письма клиент шлет {@link RequestExRejectPost}.
 * @see ExReplySentPost
 */
public class ExReplyReceivedPost extends L2GameServerPacket
{
	private final Mail mail;

	public ExReplyReceivedPost(Mail mail)
	{
		this.mail = mail;
	}

	// dddSSS dx[hddQdddhhhhhhhhhh] Qdd
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

		writeInt(mail.isPayOnDelivery() ? 0x01 : 0x00); // Платное письмо или нет
		writeInt(mail.isReturned() ? 0x01 : 0x00);// unknown3

		writeString(mail.getSenderName()); // от кого
		writeString(mail.getTopic()); // топик
		writeString(mail.getBody()); // тело

		writeInt(mail.getAttachments().size()); // количество приложенных вещей
		for(ItemInstance item : mail.getAttachments())
		{
			writeItemInfo(item);
			writeInt(item.getObjectId());
		}

		writeLong(mail.getPrice()); // для писем с оплатой - цена
		writeInt(mail.isReturnable());
		writeInt(mail.getReceiverId()); // Не известно. В сниффе оффа значение 24225 (не равняется MessageId)
	}
}