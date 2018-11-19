package l2s.gameserver.network.l2.c2s;

import l2s.commons.dao.JdbcEntityState;
import l2s.gameserver.dao.MailDAO;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.mail.Mail;
import l2s.gameserver.network.l2.s2c.ExChangePostState;
import l2s.gameserver.network.l2.s2c.ExReplyReceivedPost;
import l2s.gameserver.network.l2.s2c.ExShowReceivedPostList;
import l2s.gameserver.network.l2.s2c.ExUnReadMailCount;

/**
 * Запрос информации об полученном письме. Появляется при нажатии на письмо из списка {@link ExShowReceivedPostList}.
 * @see RequestExRequestSentPost
 */
public class RequestExRequestReceivedPost extends L2GameClientPacket
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

		Mail mail = MailDAO.getInstance().getReceivedMailByMailId(activeChar.getObjectId(), postId);
		if(mail != null)
		{
			if(mail.isUnread())
			{
				mail.setUnread(false);
				mail.setJdbcState(JdbcEntityState.UPDATED);
				mail.update();
				activeChar.sendPacket(new ExChangePostState(true, Mail.READED, mail));
				activeChar.sendPacket(new ExUnReadMailCount(activeChar));
			}

			activeChar.sendPacket(new ExReplyReceivedPost(mail));
			return;
		}

		activeChar.sendPacket(new ExShowReceivedPostList(activeChar));
	}
}