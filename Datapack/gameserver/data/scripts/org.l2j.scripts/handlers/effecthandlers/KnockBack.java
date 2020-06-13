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

import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.serverpackets.FlyToLocation;
import org.l2j.gameserver.network.serverpackets.FlyToLocation.FlyType;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.MathUtil.calculateAngleFrom;
import static org.l2j.gameserver.util.MathUtil.calculateHeadingFrom;

/**
 * Check if this effect is not counted as being stunned.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class KnockBack extends AbstractEffect {
    private final int power;
    private final int speed;
    private final int delay;
    private final int animationSpeed;
    private final boolean knockDown;
    private final FlyType type;

    private KnockBack(StatsSet params) {
        power = params.getInt("power", 50);
        speed = params.getInt("speed", 0);
        delay = params.getInt("delay", 0);
        animationSpeed = params.getInt("animationSpeed", 0);
        knockDown = params.getBoolean("knock-down", false);
        type = params.getEnum("type", FlyType.class, knockDown ? FlyType.PUSH_DOWN_HORIZONTAL : FlyType.PUSH_HORIZONTAL);
    }

    @Override
    public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
    {
        return knockDown || Formulas.calcProbability(100, effector, effected, skill);
    }

    @Override
    public boolean isInstant()
    {
        return !knockDown;
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.KNOCK;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!knockDown) {
            knockBack(effector, effected);
        }
    }

    @Override
    public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item) {
        if (knockDown) {
            knockBack(effector, effected);
        }
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if (!isPlayer(effected)) {
            effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }

    private void knockBack(Creature effector, Creature effected) {
        if (isNull(effected) || effected.isRaid()) {
            return;
        }

        final double radians = Math.toRadians(calculateAngleFrom(effector, effected));
        final int x = (int) (effected.getX() + (power * Math.cos(radians)));
        final int y = (int) (effected.getY() + (power * Math.sin(radians)));
        final int z = effected.getZ();
        final Location loc = GeoEngine.getInstance().canMoveToTargetLoc(effected.getX(), effected.getY(), effected.getZ(), x, y, z, effected.getInstanceWorld());

        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        effected.broadcastPacket(new FlyToLocation(effected, loc, type, speed, delay, animationSpeed));
        if (knockDown) {
            effected.setHeading(calculateHeadingFrom(effected, effector));
        }
        effected.setXYZ(loc);
        effected.broadcastPacket(new ValidateLocation(effected));
        effected.revalidateZone(true);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new KnockBack(data);
        }

        @Override
        public String effectName() {
            return "knockback";
        }
    }
}
