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
package org.l2j.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnNpcSkillSee implements IBaseEvent {
    private final Npc _npc;
    private final Player _caster;
    private final Skill _skill;
    private final WorldObject[] _targets;
    private final boolean _isSummon;

    public OnNpcSkillSee(Npc npc, Player caster, Skill skill, boolean isSummon, WorldObject... targets) {
        _npc = npc;
        _caster = caster;
        _skill = skill;
        _isSummon = isSummon;
        _targets = targets;
    }

    public Npc getTarget() {
        return _npc;
    }

    public Player getCaster() {
        return _caster;
    }

    public Skill getSkill() {
        return _skill;
    }

    public WorldObject[] getTargets() {
        return _targets;
    }

    public boolean isSummon() {
        return _isSummon;
    }

    @Override
    public EventType getType() {
        return EventType.ON_NPC_SKILL_SEE;
    }
}
