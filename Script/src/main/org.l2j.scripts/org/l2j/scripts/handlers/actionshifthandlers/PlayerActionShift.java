/*
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
package org.l2j.scripts.handlers.actionshifthandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.handler.IActionShiftHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;

public class PlayerActionShift implements IActionShiftHandler
{
	@Override
	public boolean action(Player activeChar, WorldObject target, boolean interact)
	{
		if (activeChar.isGM())
		{
			// Check if the gm already target this l2pcinstance
			if (activeChar.getTarget() != target)
			{
				// Set the target of the Player activeChar
				activeChar.setTarget(target);
			}
			
			AdminCommandHandler.getInstance().useAdminCommand(activeChar, "admin_character_info " + target.getName(), true);
		}
		return true;
	}
	
	@Override
	public InstanceType getInstanceType()
	{
		return InstanceType.L2PcInstance;
	}
}
