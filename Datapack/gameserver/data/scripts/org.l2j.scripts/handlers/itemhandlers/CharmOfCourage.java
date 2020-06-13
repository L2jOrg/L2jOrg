/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.itemhandlers;

import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Charm Of Courage Handler
 * @author Zealar
 */
public class CharmOfCourage implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		
		if (!isPlayer(playable))
		{
			return false;
		}
		
		final Player activeChar = playable.getActingPlayer();
		
		int level = activeChar.getLevel();
		final int itemLevel = item.getTemplate().getCrystalType().getId();
		
		if (level < 20)
		{
			level = 0;
		}
		else if (level < 40)
		{
			level = 1;
		}
		else if (level < 52)
		{
			level = 2;
		}
		else if (level < 61)
		{
			level = 3;
		}
		else if (level < 76)
		{
			level = 4;
		}
		else
		{
			level = 5;
		}
		
		if (itemLevel < level)
		{
			final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
			sm.addItemName(item.getId());
			activeChar.sendPacket(sm);
			return false;
		}
		
		if (activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false))
		{
			activeChar.setCharmOfCourage(true);
			activeChar.sendPacket(new EtcStatusUpdate(activeChar));
			return true;
		}
		return false;
	}
}
