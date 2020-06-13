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

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.Formulas;

/**
 * HP Drain effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class HpDrain extends AbstractEffect {
    private final double power;
    private final double percentage;

    private HpDrain(StatsSet params) {
        power = params.getDouble("power", 0);
        percentage = params.getDouble("percentage", 0);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.HP_DRAIN;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effector.isAlikeDead()) {
            return;
        }

        final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
        final double damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power, effected.getMDef(), mcrit);

        double drain;
        final int cp = (int) effected.getCurrentCp();
        final int hp = (int) effected.getCurrentHp();

        if (cp > 0) {
            drain = (damage < cp) ? 0 : (damage - cp);
        } else if (damage > hp) {
            drain = hp;
        } else {
            drain = damage;
        }

        final double hpAdd = ((percentage / 100) * drain);
        final double hpFinal = effector.getCurrentHp() + hpAdd > effector.getMaxHp() ? effector.getMaxHp() : effector.getCurrentHp() + hpAdd;
        effector.setCurrentHp(hpFinal);
        effector.doAttack(damage, effected, skill, false, false, mcrit, false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new HpDrain(data);
        }

        @Override
        public String effectName() {
            return "hp-drain";
        }
    }
}