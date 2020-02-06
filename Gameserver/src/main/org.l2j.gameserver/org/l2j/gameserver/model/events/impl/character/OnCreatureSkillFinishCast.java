package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * An instantly executed event when Caster has finished using a skill.
 *
 * @author Nik
 */
public class OnCreatureSkillFinishCast implements IBaseEvent {
    private final Creature _caster;
    private final Skill _skill;
    private final boolean _simultaneously;
    private final WorldObject _target;

    public OnCreatureSkillFinishCast(Creature caster, WorldObject target, Skill skill, boolean simultaneously) {
        _caster = caster;
        _skill = skill;
        _simultaneously = simultaneously;
        _target = target;
    }

    public final Creature getCaster() {
        return _caster;
    }

    public final WorldObject getTarget() {
        return _target;
    }

    public Skill getSkill() {
        return _skill;
    }

    public boolean isSimultaneously() {
        return _simultaneously;
    }

    @Override
    public EventType getType() {
        return EventType.ON_CREATURE_SKILL_FINISH_CAST;
    }
}