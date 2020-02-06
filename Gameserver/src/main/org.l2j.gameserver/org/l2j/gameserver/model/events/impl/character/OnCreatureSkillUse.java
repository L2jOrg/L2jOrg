package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.engine.skill.api.Skill;

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