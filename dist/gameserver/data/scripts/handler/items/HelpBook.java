package handler.items;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.items.ItemInstance;
import l2s.gameserver.network.l2.s2c.RadarControlPacket;
import l2s.gameserver.utils.Functions;
import l2s.gameserver.utils.Location;

public class HelpBook extends ScriptItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
			return false;

		Player activeChar = (Player) playable;
		Functions.show("help/" + item.getItemId() + ".htm", activeChar, item.getItemId());
		if(item.getItemId() == 7063)
			activeChar.sendPacket(new RadarControlPacket(0, 2, new Location(51995, -51265, -3104)));
		activeChar.sendActionFailed();
		return true;
	}
}