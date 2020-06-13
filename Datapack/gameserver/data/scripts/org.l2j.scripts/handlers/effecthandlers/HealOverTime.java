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
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.serverpackets.ExRegenMax;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isDoor;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Heal Over Time effect implementation.
 *
 * @author JoeAlisson
 */
public final class HealOverTime extends AbstractEffect {
    private final double power;

    private HealOverTime(StatsSet params) {
        power = params.getDouble("power", 0);
        setTicks(params.getInt("ticks"));
    }

    @Override
    public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected.isDead() || isDoor(effected)) {
            return false;
        }

        double hp = effected.getCurrentHp();
        final double maxhp = effected.getMaxRecoverableHp();

        // Not needed to set the HP and send update packet if player is already at max HP
        if (hp >= maxhp) {
            return false;
        }

        double power = this.power;
        if (nonNull(item) && (item.isPotion() || item.isElixir())) {
            power += effected.getStats().getValue(Stat.ADDITIONAL_POTION_HP, 0) / getTicks();
        }

        hp += power * getTicksMultiplier();
        hp = Math.min(hp, maxhp);
        effected.setCurrentHp(hp, false);
        effected.broadcastStatusUpdate(effector);
        return skill.isToggle();
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayer(effected) && (getTicks() > 0) && (skill.getAbnormalType() == AbnormalType.HP_RECOVER)) {
            double power = this.power;
            if (nonNull(item) && (item.isPotion() || item.isElixir())) {
                final double bonus = effected.getStats().getValue(Stat.ADDITIONAL_POTION_HP, 0);
                if (bonus > 0) {
                    power += bonus / getTicks();
                }
            }

            effected.sendPacket(new ExRegenMax(skill.getAbnormalTime(), getTicks(), power));
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new HealOverTime(data);
        }

        @Override
        public String effectName() {
            return "HealOverTime";
        }
    }
}
