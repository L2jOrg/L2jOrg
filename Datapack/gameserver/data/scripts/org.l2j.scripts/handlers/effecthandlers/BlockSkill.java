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

import org.l2j.commons.util.StreamUtil;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.engine.skill.api.SkillType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSkillUse;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.item.instance.Item;

import java.util.EnumSet;
import java.util.function.Function;

import static java.util.Arrays.stream;

/**
 * Block Skills by isMagic type.
 * @author Nik
 * @author JoeAlisson
 */
public final class BlockSkill extends AbstractEffect {

    private final EnumSet<SkillType> magicTypes;

    private BlockSkill(StatsSet params) {
        magicTypes = StreamUtil.collectToEnumSet(SkillType.class, stream(params.getString("magic-types").split(Util.SPACE)).map(SkillType::valueOf));
    }

    private TerminateReturn onSkillUseEvent(OnCreatureSkillUse event) {
        if (magicTypes.contains(event.getSkill().getSkillType())) {
            return new TerminateReturn(true, true, true);
        }
        return null;
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        effected.addListener(new FunctionEventListener(effected, EventType.ON_CREATURE_SKILL_USE, (Function<OnCreatureSkillUse, AbstractEventReturn>) this::onSkillUseEvent, this));
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.removeListenerIf(EventType.ON_CREATURE_SKILL_USE, listener -> listener.getOwner() == this);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new BlockSkill(data);
        }

        @Override
        public String effectName() {
            return "block-skill";
        }
    }
}
