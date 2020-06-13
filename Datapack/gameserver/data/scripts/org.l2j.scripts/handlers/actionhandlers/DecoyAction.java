/*
 * Copyright © 2019 L2J Mobius
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
package handlers.actionhandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.IActionHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

public class DecoyAction implements IActionHandler
{
    @Override
    public boolean action(Player activeChar, WorldObject target, boolean interact)
    {
        // Aggression target lock effect
        if (activeChar.isLockedTarget() && (activeChar.getLockedTarget() != target))
        {
            activeChar.sendPacket(SystemMessageId.FAILED_TO_CHANGE_ENMITY);
            return false;
        }

        activeChar.setTarget(target);
        return true;
    }

    @Override
    public InstanceType getInstanceType()
    {
        return InstanceType.L2Decoy;
    }
}
