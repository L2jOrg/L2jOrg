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
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Root effect implementation.
 * @author mkizub
 * @author JoeAlisson
 */
public final class Root extends AbstractEffect {
    private Root() {
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.ROOTED.getMask();
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.ROOT;
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if (!isPlayer(effected)) {
            effected.getAI().notifyEvent(CtrlEvent.EVT_THINK);
        }
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isNull(effected) || effected.isRaid()) {
            return;
        }

        effected.stopMove(null);
        effected.getAI().notifyEvent(CtrlEvent.EVT_ROOTED);
    }

    public static class Factory implements SkillEffectFactory {

        private static final Root INSTANCE = new Root();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "Root";
        }
    }
}
