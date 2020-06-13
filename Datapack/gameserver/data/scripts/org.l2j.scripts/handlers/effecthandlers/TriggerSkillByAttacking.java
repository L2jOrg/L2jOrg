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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttack;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.SkillCaster;

import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author JoeAlisson
 */
public final class TriggerSkillByAttacking extends AbstractEffect {

    private final int chance;
    private final SkillHolder skill;

    private TriggerSkillByAttacking(StatsSet data) {
        chance = data.getInt("chance", 100);
        skill = new SkillHolder(data.getInt("skill"), data.getInt("power", 1));
    }

    private void onAttackEvent(OnCreatureAttack event) {
        if (chance == 0 || skill.getSkillId() == 0 || skill.getLevel() == 0) {
            return;
        }

        if (event.getAttacker() == event.getTarget()) {
            return;
        }

        if (!Rnd.chance(chance)) {
            return;
        }

        final Skill triggerSkill = skill.getSkill();
        var target = triggerSkill.getTarget(event.getAttacker(), event.getTarget(), false, false, false);
        if(isCreature(target)) {
            final BuffInfo info = ((Creature) target).getEffectList().getBuffInfoBySkillId(triggerSkill.getId());
            if (isNull(info) || (info.getSkill().getLevel() < triggerSkill.getLevel())) {
                SkillCaster.triggerCast(event.getAttacker(), (Creature) target, triggerSkill);
            }
        }
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        effected.removeListenerIf(EventType.ON_CREATURE_ATTACK, listener -> listener.getOwner() == this);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_ATTACK, (Consumer<OnCreatureAttack>) this::onAttackEvent, this));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TriggerSkillByAttacking(data);
        }

        @Override
        public String effectName() {
            return "trigger-skill-by-attacking";
        }
    }
}
