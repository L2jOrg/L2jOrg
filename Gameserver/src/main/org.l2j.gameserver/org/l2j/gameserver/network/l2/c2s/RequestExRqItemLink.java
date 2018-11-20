package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.cache.ItemInfoCache;
import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.network.l2.s2c.ActionFailPacket;
import org.l2j.gameserver.network.l2.s2c.ExRpItemLink;

public class RequestExRqItemLink extends L2GameClientPacket
{
	private int _objectId;

	@Override
	protected void readImpl()
	{
		_objectId = readD();
	}

	@Override
	protected void runImpl()
	{
		ItemInfo item;
		if((item = ItemInfoCache.getInstance().get(_objectId)) == null)
			sendPacket(ActionFailPacket.STATIC);
		else
			sendPacket(new ExRpItemLink(item));
	}
}