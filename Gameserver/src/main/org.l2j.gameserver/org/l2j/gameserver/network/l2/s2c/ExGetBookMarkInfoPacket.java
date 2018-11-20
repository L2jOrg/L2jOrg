package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.BookMark;

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
		writeInt(0x00); // должно быть 0
		writeInt(bookmarksCapacity);
		writeInt(bookmarks.length);
		int slotId = 0;
		for(BookMark bookmark : bookmarks)
		{
			writeInt(++slotId);
			writeInt(bookmark.x);
			writeInt(bookmark.y);
			writeInt(bookmark.z);
			writeString(bookmark.getName());
			writeInt(bookmark.getIcon());
			writeString(bookmark.getAcronym());
		}
	}
}