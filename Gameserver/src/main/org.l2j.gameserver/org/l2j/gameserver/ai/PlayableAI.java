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
package org.l2j.gameserver.ai;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.world.zone.ZoneType;

import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * This class manages AI of Playable.<br>
 * PlayableAI :
 * <li>SummonAI</li>
 * <li>PlayerAI</li>
 *
 * @author JIV
 */
public abstract class PlayableAI extends CreatureAI {
    public PlayableAI(Playable playable) {
        super(playable);
    }

    @Override
    protected void onIntentionAttack(Creature target) {
        if (isPlayable(target)) {
            if (target.getActingPlayer().isProtectionBlessingAffected() && ((actor.getActingPlayer().getLevel() - target.getActingPlayer().getLevel()) >= 10) && (actor.getActingPlayer().getReputation() < 0) && !(target.isInsideZone(ZoneType.PVP))) {
                // If attacker have karma and have level >= 10 than his target and target have
                // Newbie Protection Buff,
                actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (actor.getActingPlayer().isProtectionBlessingAffected() && ((target.getActingPlayer().getLevel() - actor.getActingPlayer().getLevel()) >= 10) && (target.getActingPlayer().getReputation() < 0) && !(target.isInsideZone(ZoneType.PVP))) {
                // If target have karma and have level >= 10 than his target and actor have
                // Newbie Protection Buff,
                actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }
        }
        super.onIntentionAttack(target);
    }

    @Override
    protected void onIntentionCast(Skill skill, WorldObject target, Item item, boolean forceUse, boolean dontMove) {
        if ((isPlayable(target)) && skill.isBad()) {
            if (target.getActingPlayer().isProtectionBlessingAffected() && ((actor.getActingPlayer().getLevel() - target.getActingPlayer().getLevel()) >= 10) && (actor.getActingPlayer().getReputation() < 0) && !target.isInsideZone(ZoneType.PVP)) {
                // If attacker have karma and have level >= 10 than his target and target have
                // Newbie Protection Buff,
                actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }

            if (actor.getActingPlayer().isProtectionBlessingAffected() && ((target.getActingPlayer().getLevel() - actor.getActingPlayer().getLevel()) >= 10) && (target.getActingPlayer().getReputation() < 0) && !target.isInsideZone(ZoneType.PVP)) {
                // If target have karma and have level >= 10 than his target and actor have
                // Newbie Protection Buff,
                actor.getActingPlayer().sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
                clientActionFailed();
                return;
            }
        }
        super.onIntentionCast(skill, target, item, forceUse, dontMove);
    }
}
