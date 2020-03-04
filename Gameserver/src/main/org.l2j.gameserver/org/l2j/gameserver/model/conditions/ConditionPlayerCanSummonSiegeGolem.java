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
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Player Can Summon Siege Golem implementation.
 *
 * @author Adry_85
 */
public class ConditionPlayerCanSummonSiegeGolem extends Condition {
    private final boolean _val;

    public ConditionPlayerCanSummonSiegeGolem(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (!isPlayer(effector)) {
            return !_val;
        }

        final Player player = effector.getActingPlayer();
        boolean canSummonSiegeGolem = true;
        if (player.isAlikeDead() || player.isCursedWeaponEquipped() || (player.getClan() == null)) {
            canSummonSiegeGolem = false;
        }

        final Castle castle = CastleManager.getInstance().getCastle(player);
        final Fort fort = FortDataManager.getInstance().getFort(player);
        if ((castle == null) && (fort == null)) {
            canSummonSiegeGolem = false;
        }

        if (((fort != null) && (fort.getId() == 0)) || ((castle != null) && (castle.getId() == 0))) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        } else if (((castle != null) && !castle.getSiege().isInProgress()) || ((fort != null) && !fort.getSiege().isInProgress())) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        } else if ((player.getClanId() != 0) && (((castle != null) && (castle.getSiege().getAttackerClan(player.getClanId()) == null)) || ((fort != null) && (fort.getSiege().getAttackerClan(player.getClanId()) == null)))) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canSummonSiegeGolem = false;
        }
        return (_val == canSummonSiegeGolem);
    }
}
