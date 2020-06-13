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
package org.l2j.gameserver.model.actor;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.util.GameUtils;

/**
 * This class is a super-class for ControlTower and FlameTower.
 *
 * @author Zoey76
 */
public abstract class Tower extends Npc {
    public Tower(NpcTemplate template) {
        super(template);
        setIsInvul(false);
    }

    @Override
    public boolean canBeAttacked() {
        // Attackable during siege by attacker only
        return (getCastle() != null) && (getCastle().getId() > 0) && getCastle().getSiege().isInProgress();
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        // Attackable during siege by attacker only
        return GameUtils.isPlayer(attacker) && (getCastle() != null) && (getCastle().getId() > 0) && getCastle().getSiege().isInProgress() && getCastle().getSiege().checkIsAttacker(attacker.getClan());
    }

    @Override
    public void onAction(Player player, boolean interact) {
        if (!canTarget(player)) {
            return;
        }

        if (this != player.getTarget()) {
            // Set the target of the Player player
            player.setTarget(this);
        } else if (interact && isAutoAttackable(player) && (Math.abs(player.getZ() - getZ()) < 100) && GeoEngine.getInstance().canSeeTarget(player, this)) {
            // Notify the Player AI with AI_INTENTION_INTERACT
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
        }
        // Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public void onForcedAttack(Player player) {
        onAction(player);
    }
}
