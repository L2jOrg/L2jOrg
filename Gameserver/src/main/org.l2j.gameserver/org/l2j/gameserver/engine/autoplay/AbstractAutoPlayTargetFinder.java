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
package org.l2j.gameserver.engine.autoplay;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;

import java.util.Comparator;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isGM;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
abstract class AbstractAutoPlayTargetFinder implements AutoPlayTargetFinder {

    @Override
    public boolean canBeTarget(Player player, WorldObject target, int range) {
        return !isGM(target) && !player.isTargetingDisabled() && target.isTargetable() && target.isAutoAttackable(player) && checkRespectfulMode(player, target) &&
                MathUtil.isInsideRadius3D(player, target, range) && GeoEngine.getInstance().canSeeTarget(player, target) && GeoEngine.getInstance().canMoveToTarget(player, target);
    }

    private boolean checkRespectfulMode(Player player, WorldObject target) {
        return !player.getAutoPlaySettings().isRespectfulMode() || !(target instanceof Monster monster) || canAttackRespectfully(player, monster);
    }

    private boolean canAttackRespectfully(Player player, Monster monster) {
        return isNull(monster.getTarget()) || monster.getTarget().equals(player) || monster.getAggroList().isEmpty() || checkFriendlyTarget(monster, player);
    }

    private boolean checkFriendlyTarget(Monster monster, Player player) {
        var target = monster.getTarget();
        return target == player.getPet() || (target instanceof Summon s && player.getServitors().containsValue(s))
                || (target instanceof Player targetPlayer && player.isInParty() && player.getParty().isMember(targetPlayer));
    }

    protected Creature findNextTarget(Player player, Class<? extends Creature> targetClass, int range) {
        return World.getInstance().findFirstVisibleObject(player, targetClass, range, false,
                creature -> canBeTarget(player, creature, range),
                Comparator.comparingDouble(m -> MathUtil.calculateDistanceSq3D(player, m)));
    }

    /*
     *  Used by the custom Autoplay settings.
     */
    @Override
    public boolean canBeTargetAnchored(Location location, Player player, WorldObject target, int range) {
        return !isGM(target) && !player.isTargetingDisabled() && target.isTargetable() && target.isAutoAttackable(player) && checkRespectfulMode(player, target) &&
                MathUtil.isInsideRadius3D(location, target, range) && GeoEngine.getInstance().canSeeTarget(player, target) && GeoEngine.getInstance().canMoveToTarget(player, target);
    }

    protected Creature findNextTargetAnchored(Location location, Player player, Class<? extends Creature> targetClass, int range) {
        return World.getInstance().findFirstVisibleObject(player, targetClass, (range * 2), false,
                creature -> canBeTargetAnchored(location, player, creature, range),
                Comparator.comparingDouble(m -> MathUtil.calculateDistanceSq3D(player, m)));
    }
}
