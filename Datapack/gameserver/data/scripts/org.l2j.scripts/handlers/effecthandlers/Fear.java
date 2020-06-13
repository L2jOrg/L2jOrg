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
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Defender;
import org.l2j.gameserver.model.actor.instance.SiegeFlag;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.item.instance.Item;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.*;
import static org.l2j.gameserver.util.MathUtil.calculateAngleFrom;
import static org.l2j.gameserver.util.MathUtil.convertHeadingToDegree;

/**
 * Fear effect implementation.
 * @author littlecrow
 * @author JoeAlisson
 */
public final class Fear extends AbstractEffect {
    private static final int FEAR_RANGE = 500;

    private Fear() {
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.FEAR.getMask();
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill) {
        if (isNull(effected) || effected.isRaid()) {
            return false;
        }

        return isPlayer(effected) || isSummon(effected) || isAttackable(effected) && !(effected instanceof Defender || effected instanceof SiegeFlag || effected.getTemplate().getRace() == Race.SIEGE_WEAPON);
    }

    @Override
    public int getTicks()
    {
        return 5;
    }

    @Override
    public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item) {
        fearAction(null, effected);
        return false;
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        effected.getAI().notifyEvent(CtrlEvent.EVT_AFRAID);
        fearAction(effector, effected);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if (!isPlayer(effected)) {
            effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }

    private void fearAction(Creature effector, Creature effected) {
        final double radians = Math.toRadians((effector != null) ? calculateAngleFrom(effector, effected) : convertHeadingToDegree(effected.getHeading()));

        final int posX = (int) (effected.getX() + (FEAR_RANGE * Math.cos(radians)));
        final int posY = (int) (effected.getY() + (FEAR_RANGE * Math.sin(radians)));
        final int posZ = effected.getZ();

        final Location destination = GeoEngine.getInstance().canMoveToTargetLoc(effected.getX(), effected.getY(), effected.getZ(), posX, posY, posZ, effected.getInstanceWorld());
        effected.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, destination);
    }

    public static class Factory implements SkillEffectFactory {
        private static final Fear INSTANCE = new Fear();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "Fear";
        }
    }
}
