package org.l2j.scripts.handler.items;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.RadarControlPacket;
import org.l2j.gameserver.utils.Functions;
import org.l2j.gameserver.utils.Location;

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