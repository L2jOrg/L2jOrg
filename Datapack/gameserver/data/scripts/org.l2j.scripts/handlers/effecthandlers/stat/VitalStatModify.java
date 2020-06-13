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
package handlers.effecthandlers.stat;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author JoeAlisson
 *
 */
public class VitalStatModify extends AbstractStatEffect {

    private final boolean heal;

    private VitalStatModify(StatsSet params) {
        super(params, params.getEnum("stat", Stat.class));
        heal = params.getBoolean("heal", false);
    }

    @Override
    public void continuousInstant(Creature effector, Creature effected, Skill skill, Item item) {
        if (heal) {
            ThreadPool.schedule(() -> {
                switch (mode) {
                    case DIFF -> instantDiff(effected);
                    case PER -> instantPercent(effected);
                }
            }, 100);
        }
    }

    private void instantDiff(Creature effected) {
        switch (addStat) {
            case MAX_CP -> effected.setCurrentCp(effected.getCurrentCp() + power);
            case MAX_HP -> effected.setCurrentHp(effected.getCurrentHp() + power);
            case MAX_MP -> effected.setCurrentMp(effected.getCurrentMp() + power);
        };
    }

    private void instantPercent(Creature effected) {
        var percent = power / 100;
        switch (mulStat) {
            case MAX_CP -> effected.setCurrentCp(effected.getCurrentCp() + (effected.getMaxCp() * percent));
            case MAX_HP -> effected.setCurrentHp(effected.getCurrentHp() + (effected.getMaxHp() * percent));
            case MAX_MP -> effected.setCurrentMp(effected.getCurrentMp() + (effected.getMaxMp() * percent));
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new VitalStatModify(data);
        }

        @Override
        public String effectName() {
            return "vital-stat-modify";
        }
    }

}
