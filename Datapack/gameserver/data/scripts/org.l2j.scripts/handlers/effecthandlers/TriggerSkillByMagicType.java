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
import org.l2j.commons.util.StreamUtil;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.engine.skill.api.SkillType;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillFinishCast;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.skills.targets.TargetType;

import java.util.EnumSet;
import java.util.function.Consumer;

import static java.util.Arrays.stream;
import static org.l2j.commons.util.Util.SPACE;
import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Trigger skill by isMagic type.
 * @author Nik
 * @author JoeAlisson
 */
public final class TriggerSkillByMagicType extends AbstractEffect {
    private final EnumSet<SkillType> magicTypes;
    private final int chance;
    private final SkillHolder skill;
    private final TargetType targetType;

    private TriggerSkillByMagicType(StatsSet params) {
        chance = params.getInt("chance", 100);
        magicTypes = StreamUtil.collectToEnumSet(SkillType.class, stream(params.getString("types").split(SPACE)).map(SkillType::valueOf));
        skill = new SkillHolder(params.getInt("skill", 0), params.getInt("power", 0));
        targetType = params.getEnum("target", TargetType.class, TargetType.TARGET);
    }

    private void onSkillUseEvent(OnCreatureSkillFinishCast event) {
        if (!isCreature(event.getTarget())) {
            return;
        }

        if (!magicTypes.contains(event.getSkill().getSkillType())) {
            return;
        }

        if (Rnd.chance(chance)) {
            return;
        }

        final Skill triggerSkill = skill.getSkill();

        WorldObject target = null;
        try {
            target = TargetHandler.getInstance().getHandler(targetType).getTarget(event.getCaster(), event.getTarget(), triggerSkill, false, false, false);
        } catch (Exception e) {
            LOGGER.warn("Exception in ITargetTypeHandler.getTarget(): " + e.getMessage(), e);
        }

        if (isCreature(target)) {
            SkillCaster.triggerCast(event.getCaster(), (Creature) target, triggerSkill);
        }
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (chance == 0|| this.skill.getSkillId() == 0 || this.skill.getLevel() == 0) {
            return;
        }

        effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_SKILL_FINISH_CAST, (Consumer<OnCreatureSkillFinishCast>) this::onSkillUseEvent, this));
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.removeListenerIf(EventType.ON_CREATURE_SKILL_FINISH_CAST, listener -> listener.getOwner() == this);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TriggerSkillByMagicType(data);
        }

        @Override
        public String effectName() {
            return "trigger-skill-by-magic";
        }
    }
}
