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
package handlers.effecthandlers;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.serverpackets.FlyToLocation;
import org.l2j.gameserver.network.serverpackets.FlyToLocation.FlyType;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isMonster;
import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * An effect that pulls effected target back to the effector.
 * @author Nik
 * @author JoeAlisson
 */
public final class PullBack extends AbstractEffect {

    private final int speed;
    private final int delay;
    private final int animationSpeed;
    private final FlyType type;

    private PullBack(StatsSet params) {
        speed = params.getInt("power", 0);
        delay = params.getInt("delay", speed);
        animationSpeed = params.getInt("animationSpeed", 0);
        type = params.getEnum("type", FlyType.class, FlyType.WARP_FORWARD); // type 9
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
    {
        return Formulas.calcProbability(100, effector, effected, skill);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        // Prevent pulling raids.
        if (isNull(effected) || effected.isRaid()) {
            return;
        }

        // Prevent pulling debuff blocked characters.
        if (effected.isDebuffBlocked()) {
            return;
        }

        // Prevent pulling NPCs.
        if (!isPlayable(effected) && !isMonster(effected)) {
            return;
        }

        // In retail, you get debuff, but you are not even moved if there is obstacle. You are still disabled from using skills and moving though.
        if (GeoEngine.getInstance().canMoveToTarget(effected.getX(), effected.getY(), effected.getZ(), effector.getX(), effector.getY(), effector.getZ(), effector.getInstanceWorld())) {
            effected.broadcastPacket(new FlyToLocation(effected, effector, type, speed, delay, animationSpeed));
            effected.setXYZ(effector);
            effected.broadcastPacket(new ValidateLocation(effected));
            effected.revalidateZone(true);
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new PullBack(data);
        }

        @Override
        public String effectName() {
            return "pull-back";
        }
    }
}
