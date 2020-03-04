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

import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Player Can Take Fort condition implementation.
 *
 * @author Adry_85
 */
public class ConditionPlayerCanTakeFort extends Condition {
    private final boolean _val;

    public ConditionPlayerCanTakeFort(boolean val) {
        _val = val;
    }

    @Override
    public boolean testImpl(Creature effector, Creature effected, Skill skill, ItemTemplate item) {
        if (!isPlayer(effector)) {
            return !_val;
        }

        final Player player = effector.getActingPlayer();
        boolean canTakeFort = true;
        if (player.isAlikeDead() || player.isCursedWeaponEquipped() || !player.isClanLeader()) {
            canTakeFort = false;
        }

        final Fort fort = FortDataManager.getInstance().getFort(player);
        final SystemMessage sm;
        if ((fort == null) || (fort.getId() <= 0) || !fort.getSiege().isInProgress() || (fort.getSiege().getAttackerClan(player.getClan()) == null)) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            sm.addSkillName(skill);
            player.sendPacket(sm);
            canTakeFort = false;
        } else if (fort.getFlagPole() != effected) {
            player.sendPacket(SystemMessageId.INVALID_TARGET);
            canTakeFort = false;
        } else if (!GameUtils.checkIfInRange(200, player, effected, true)) {
            player.sendPacket(SystemMessageId.THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED);
            canTakeFort = false;
        }
        return (_val == canTakeFort);
    }
}
