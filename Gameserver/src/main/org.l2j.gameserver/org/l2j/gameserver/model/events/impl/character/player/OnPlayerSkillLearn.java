/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * @author UnAfraid
 */
public class OnPlayerSkillLearn implements IBaseEvent {
    private final Npc _trainer;
    private final Player _activeChar;
    private final Skill _skill;
    private final AcquireSkillType _type;

    public OnPlayerSkillLearn(Npc trainer, Player activeChar, Skill skill, AcquireSkillType type) {
        _trainer = trainer;
        _activeChar = activeChar;
        _skill = skill;
        _type = type;
    }

    public Npc getTrainer() {
        return _trainer;
    }

    public Player getActiveChar() {
        return _activeChar;
    }

    public Skill getSkill() {
        return _skill;
    }

    public AcquireSkillType getAcquireType() {
        return _type;
    }

    @Override
    public EventType getType() {
        return EventType.ON_PLAYER_SKILL_LEARN;
    }
}
