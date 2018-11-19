package l2s.gameserver.network.l2.c2s;

import l2s.gameserver.cache.ItemInfoCache;
import l2s.gameserver.model.items.ItemInfo;
import l2s.gameserver.network.l2.s2c.ActionFailPacket;
import l2s.gameserver.network.l2.s2c.ExRpItemLink;

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