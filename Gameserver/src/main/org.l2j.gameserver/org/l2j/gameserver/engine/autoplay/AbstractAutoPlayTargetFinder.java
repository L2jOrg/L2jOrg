package org.l2j.gameserver.engine.autoplay;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;

import java.util.Comparator;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
abstract class AbstractAutoPlayTargetFinder implements AutoPlayTargetFinder {

    @Override
    public boolean canBeTarget(Player player, WorldObject target) {
        return !player.isTargetingDisabled() && target.isTargetable() && target.isAutoAttackable(player) && checkRespectfulMode(player, target) &&
                GeoEngine.getInstance().canSeeTarget(player, target) && GeoEngine.getInstance().canMoveToTarget(player, target);
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
                || (target instanceof Player targetPlayer && player.isInParty() && player.getParty().containsPlayer(targetPlayer));
    }

    protected Creature findNextTarget(Player player, Class<? extends Creature> targetClass, int range) {
        return World.getInstance().findFirstVisibleObject(player, targetClass, range, false,
                creature -> canBeTarget(player, creature),
                Comparator.comparingDouble(m -> MathUtil.calculateDistanceSq3D(player, m)));
    }
}
