package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.PremiumItem;

/**
 * @author Gnacik
 * @corrected by n0nam3
 **/
public class ExGetPremiumItemListPacket extends L2GameServerPacket
{
	private final int _objectId;
	private final PremiumItem[] _list;

	public ExGetPremiumItemListPacket(Player activeChar)
	{
		_objectId = activeChar.getObjectId();
		_list = activeChar.getPremiumItemList().values();
	}

	@Override
	protected void writeImpl()
	{
		writeInt(_list.length);
		for(int i = 0; i < _list.length; i++)
		{
			writeInt(i);
			writeInt(_objectId);
			writeInt(_list[i].getItemId());
			writeLong(_list[i].getItemCount());
			writeInt(0);
			writeString(_list[i].getSender());
		}
	}
}