package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.network.l2.c2s.RequestExSendPost;

/**
 * Запрос на отправку нового письма. Шлется в ответ на {@link RequestExSendPost}.
 */
public class ExReplyWritePost extends L2GameServerPacket
{
	public static final L2GameServerPacket STATIC_TRUE = new ExReplyWritePost(1);
	public static final L2GameServerPacket STATIC_FALSE = new ExReplyWritePost(0);

	private int _reply;

	/**
	 * @param i если 1 окно создания письма закрывается
	 */
	public ExReplyWritePost(int i)
	{
		_reply = i;
	}

	@Override
	protected void writeImpl()
	{
		writeD(_reply); // 1 - закрыть окно письма, иное - не закрывать
	}
}