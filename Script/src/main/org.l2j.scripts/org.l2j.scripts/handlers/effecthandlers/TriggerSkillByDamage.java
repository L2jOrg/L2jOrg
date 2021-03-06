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
package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDamageReceived;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.skills.targets.TargetType;

import java.util.function.Consumer;

import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Trigger Skill By Damage effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class TriggerSkillByDamage extends AbstractEffect {
    private final int minDamage;
    private final int chance;
    private final SkillHolder skill;
    private final TargetType targetType;
    private final InstanceType attackerType;

    private TriggerSkillByDamage(StatsSet params) {
        minDamage = params.getInt("min-damage", 1);
        chance = params.getInt("chance", 100);
        skill = new SkillHolder(params.getInt("skill"), params.getInt("power", 1));
        targetType = params.getEnum("target", TargetType.class, TargetType.SELF);
        attackerType = params.getEnum("attacker", InstanceType.class, InstanceType.Creature);
    }

    private void onDamageReceivedEvent(OnCreatureDamageReceived event) {
        if (event.isDamageOverTime() || (chance == 0) || (skill.getLevel() == 0)) {
            return;
        }

        if (event.getAttacker() == event.getTarget()) {
            return;
        }

        if ((event.getDamage() < minDamage) || ((chance < 100) && (Rnd.get(100) > chance)) || !event.getAttacker().getInstanceType().isType(attackerType)) {
            return;
        }

        final Skill triggerSkill = skill.getSkill();
        WorldObject target = null;
        try {
            target = TargetHandler.getInstance().getHandler(targetType).getTarget(event.getTarget(), event.getAttacker(), triggerSkill, false, false, false);
        } catch (Exception e) {
            LOGGER.warn("Exception in ITargetTypeHandler.getTarget(): " + e.getMessage(), e);
        }

        if (isCreature(target)) {
            SkillCaster.triggerCast(event.getTarget(), (Creature) target, triggerSkill);
        }
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.removeListenerIf(EventType.ON_CREATURE_DAMAGE_RECEIVED, listener -> listener.getOwner() == this);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_DAMAGE_RECEIVED, (Consumer<OnCreatureDamageReceived>) this::onDamageReceivedEvent, this));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TriggerSkillByDamage(data);
        }

        @Override
        public String effectName() {
            return "trigger-skill-by-damage";
        }
    }
}
