package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.BookMark;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(0x00); // должно быть 0
		buffer.putInt(bookmarksCapacity);
		buffer.putInt(bookmarks.length);
		int slotId = 0;
		for(BookMark bookmark : bookmarks)
		{
			buffer.putInt(++slotId);
			buffer.putInt(bookmark.x);
			buffer.putInt(bookmark.y);
			buffer.putInt(bookmark.z);
			writeString(bookmark.getName(), buffer);
			buffer.putInt(bookmark.getIcon());
			writeString(bookmark.getAcronym(), buffer);
		}
	}
}