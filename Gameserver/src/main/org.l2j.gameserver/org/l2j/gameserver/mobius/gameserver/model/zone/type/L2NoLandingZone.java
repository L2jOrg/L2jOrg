/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.zone.type;

import org.l2j.gameserver.mobius.gameserver.enums.MountType;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

/**
 * A no landing zone
 *
 * @author durgus
 */
public class L2NoLandingZone extends L2ZoneType {
    private int dismountDelay = 5;

    public L2NoLandingZone(int id) {
        super(id);
    }

    @Override
    public void setParameter(String name, String value) {
        if (name.equals("dismountDelay")) {
            dismountDelay = Integer.parseInt(value);
        } else {
            super.setParameter(name, value);
        }
    }

    @Override
    protected void onEnter(L2Character character) {
        if (character.isPlayer()) {
            character.setInsideZone(ZoneId.NO_LANDING, true);
            if (character.getActingPlayer().getMountType() == MountType.WYVERN) {
                character.sendPacket(SystemMessageId.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN_YOU_WILL_BE_DISMOUNTED_FROM_YOUR_WYVERN_IF_YOU_DO_NOT_LEAVE);
                character.getActingPlayer().enteredNoLanding(dismountDelay);
            }
        }
    }

    @Override
    protected void onExit(L2Character character) {
        if (character.isPlayer()) {
            character.setInsideZone(ZoneId.NO_LANDING, false);
            if (character.getActingPlayer().getMountType() == MountType.WYVERN) {
                character.getActingPlayer().exitedNoLanding();
            }
        }
    }
}
