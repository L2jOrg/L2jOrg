package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.player.PremiumItem;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

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
	protected void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_list.length);
		for(int i = 0; i < _list.length; i++)
		{
			buffer.putInt(i);
			buffer.putInt(_objectId);
			buffer.putInt(_list[i].getItemId());
			buffer.putLong(_list[i].getItemCount());
			buffer.putInt(0);
			writeString(_list[i].getSender(), buffer);
		}
	}
}