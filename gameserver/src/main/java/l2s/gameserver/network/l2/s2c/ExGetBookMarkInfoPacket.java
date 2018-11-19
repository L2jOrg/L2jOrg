package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.BookMark;

/**
 * dd d*[ddddSdS]
 */
public class ExGetBookMarkInfoPacket extends L2GameServerPacket
{
	private final int bookmarksCapacity;
	private final BookMark[] bookmarks;

	public ExGetBookMarkInfoPacket(Player player)
	{
		bookmarksCapacity = player.getBookMarkList().getCapacity();
		bookmarks = player.getBookMarkList().toArray();
	}

	@Override
	protected void writeImpl()
	{
		writeD(0x00); // должно быть 0
		writeD(bookmarksCapacity);
		writeD(bookmarks.length);
		int slotId = 0;
		for(BookMark bookmark : bookmarks)
		{
			writeD(++slotId);
			writeD(bookmark.x);
			writeD(bookmark.y);
			writeD(bookmark.z);
			writeS(bookmark.getName());
			writeD(bookmark.getIcon());
			writeS(bookmark.getAcronym());
		}
	}
}