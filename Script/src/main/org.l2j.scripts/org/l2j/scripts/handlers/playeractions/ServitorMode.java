/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.handlers.playeractions;

import org.l2j.gameserver.ai.SummonAI;
import org.l2j.gameserver.data.xml.model.ActionData;
import org.l2j.gameserver.handler.IPlayerActionHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Servitor passive/defend mode player action handler.
 * @author Nik
 */
public final class ServitorMode implements IPlayerActionHandler
{
	@Override
	public void useAction(Player player, ActionData action, boolean ctrlPressed, boolean shiftPressed)
	{
		if (!player.hasServitors())
		{
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR);
			return;
		}
		
		switch (action.getOptionId())
		{
			case 1: // Passive mode
			{
				player.getServitors().values().forEach(s ->
				{
					if (s.isBetrayed())
					{
						player.sendPacket(SystemMessageId.YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
						return;
					}
					
					((SummonAI) s.getAI()).setDefending(false);
				});
				break;
			}
			case 2: // Defending mode
			{
				player.getServitors().values().forEach(s ->
				{
					if (s.isBetrayed())
					{
						player.sendPacket(SystemMessageId.YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS);
						return;
					}
					
					((SummonAI) s.getAI()).setDefending(true);
				});
			}
		}
	}

	@Override
	public boolean isSummonAction() {
		return true;
	}
}
