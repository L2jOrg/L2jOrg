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
package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.model.items.L2Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * Player Can Create Outpost condition implementation.
 *
 * @author Adry_85
 */
public class ConditionPlayerCanCreateOutpost extends Condition {
    private final boolean _val;

    public ConditionPlayerCanCreateOutpost(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, L2Item item) {
        if ((effector == null) || !effector.isPlayer()) {
            return !_val;
        }

        final Player player = effector.getActingPlayer();
        boolean canCreateOutpost = true;
        if (player.isAlikeDead() || player.isCursedWeaponEquipped() || (player.getClan() == null)) {
            canCreateOutpost = false;
        }

        final Castle castle = CastleManager.getInstance().getCastle(player);
        final Fort fort = FortDataManager.getInstance().getFort(player);
        if ((castle == null) && (fort == null)) {
            canCreateOutpost = false;
        }

        if (((fort != null) && (fort.getResidenceId() == 0)) || ((castle != null) && (castle.getResidenceId() == 0))) {
            player.sendMessage("You must be on fort or castle ground to construct an outpost or flag.");
            canCreateOutpost = false;
        } else if (((fort != null) && !fort.getZone().isActive()) || ((castle != null) && !castle.getZone().isActive())) {
            player.sendMessage("You can only construct an outpost or flag on siege field.");
            canCreateOutpost = false;
        } else if (!player.isClanLeader()) {
            player.sendMessage("You must be a clan leader to construct an outpost or flag.");
            canCreateOutpost = false;
        } else if (!player.isInsideZone(ZoneId.HQ)) {
            player.sendPacket(SystemMessageId.YOU_CAN_T_BUILD_HEADQUARTERS_HERE);
            canCreateOutpost = false;
        }
        return (_val == canCreateOutpost);
    }
}
