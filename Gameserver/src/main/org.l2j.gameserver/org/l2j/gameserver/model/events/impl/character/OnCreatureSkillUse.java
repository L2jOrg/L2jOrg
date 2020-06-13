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
package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * Executed when the caster Creature tries to use a skill.
 *
 * @author UnAfraid, Nik
 */
public class OnCreatureSkillUse implements IBaseEvent {
    private final Creature _caster;
    private final Skill _skill;
    private final boolean _simultaneously;

    public OnCreatureSkillUse(Creature caster, Skill skill, boolean simultaneously) {
        _caster = caster;
        _skill = skill;
        _simultaneously = simultaneously;
    }

    public final Creature getCaster() {
        return _caster;
    }

    public Skill getSkill() {
        return _skill;
    }

    public boolean isSimultaneously() {
        return _simultaneously;
    }

    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_SKILL_USE;
    }
}