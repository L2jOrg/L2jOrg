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

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.skills.Skill;

/**
 * Player Can Escape condition implementation.
 *
 * @author Adry_85
 */
public class ConditionPlayerCanEscape extends Condition {
    private final boolean _val;

    public ConditionPlayerCanEscape(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        boolean canTeleport = true;
        final Player player = effector.getActingPlayer();
        if (player == null) {
            canTeleport = false;
        } else if (player.isInDuel()) {
            canTeleport = false;
        } else if (player.isControlBlocked()) {
            canTeleport = false;
        } else if (player.isCombatFlagEquipped()) {
            canTeleport = false;
        } else if (player.isFlying() || player.isFlyingMounted()) {
            canTeleport = false;
        } else if (player.isInOlympiadMode()) {
            canTeleport = false;
        } else if (player.isOnCustomEvent()) {
            canTeleport = false;
        }
        return (_val == canTeleport);
    }
}