package org.l2j.gameserver.handler.items.impl;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.s2c.ShowCalcPacket;

/**
 * @author VISTALL
 * @date 18:22/11.03.2011
 */
public class CalculatorItemHandler extends DefaultItemHandler
{
	@Override
	public boolean useItem(Playable playable, ItemInstance item, boolean ctrl)
	{
		if(!playable.isPlayer())
			return false;

		playable.sendPacket(new ShowCalcPacket(item.getItemId()));
		return true;
	}
}