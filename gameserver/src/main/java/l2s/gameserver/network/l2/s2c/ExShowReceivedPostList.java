package l2s.gameserver.network.l2.s2c;

import java.util.Collections;
import java.util.List;

import l2s.gameserver.dao.MailDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.mail.Mail;

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
		writeD((int) (System.currentTimeMillis() / 1000L));
		writeD(_mails.length); // количество писем
		for(Mail mail : _mails)
		{
			writeD(mail.getType().ordinal()); // тип письма

			if(mail.getType() == Mail.SenderType.SYSTEM)
				writeD(mail.getSystemTopic());

			writeD(mail.getMessageId()); // уникальный id письма
			writeS(mail.getTopic()); // топик
			writeS(mail.getSenderName()); // отправитель
			writeD(mail.isPayOnDelivery() ? 1 : 0); // если тут 1 то письмо требует оплаты
			writeD(mail.getExpireTime()); // время действительности письма
			writeD(mail.isUnread() ? 1 : 0); // письмо не прочитано - его нельзя удалить и оно выделяется ярким цветом
			writeD(mail.isReturnable()); // returnable
			writeD(mail.getAttachments().isEmpty() ? 0 : 1); // 1 - письмо с приложением, 0 - просто письмо
			writeD(mail.isReturned() ? 1 : 0);
			writeD(mail.getReceiverId());
		}
		writeD(100);
		writeD(1000);
	}
}