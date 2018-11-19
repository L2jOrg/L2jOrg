package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.actor.instances.player.PremiumItem;

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
		writeD(_list.length);
		for(int i = 0; i < _list.length; i++)
		{
			writeD(i);
			writeD(_objectId);
			writeD(_list[i].getItemId());
			writeQ(_list[i].getItemCount());
			writeD(0);
			writeS(_list[i].getSender());
		}
	}
}