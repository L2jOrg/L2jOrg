package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.dao.MailDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.l2.s2c.ExReplySentPost;
import l2s.gameserver.network.l2.s2c.ExShowSentPostList;

/**
 * Запрос информации об отправленном письме. Появляется при нажатии на письмо из списка {@link ExShowSentPostList}.
 * В ответ шлется {@link ExReplySentPost}.
 * @see RequestExRequestReceivedPost
 */
public class RequestExRequestSentPost extends L2GameClientPacket
{
	private int postId;

	/**
	 * format: d
	 */
	@Override
	protected void readImpl()
	{
		postId = readD(); // id письма
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = getClient().getActiveChar();
		if(activeChar == null)
			return;

		Mail mail = MailDAO.getInstance().getSentMailByMailId(activeChar.getObjectId(), postId);
		if(mail != null)
		{
			activeChar.sendPacket(new ExReplySentPost(mail));
			return;
		}

		activeChar.sendPacket(new ExShowSentPostList(activeChar));
	}
}