package org.l2j.gameserver.mobius.gameserver.model.events.impl.character;

import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;

/**
 * An instantly executed event when Caster has finished using a skill.
 *
 * @author Nik
 */
public class OnCreatureSkillFinishCast implements IBaseEvent {
    private final L2Character _caster;
    private final Skill _skill;
    private final boolean _simultaneously;
    private final L2Object _target;

    public OnCreatureSkillFinishCast(L2Character caster, L2Object target, Skill skill, boolean simultaneously) {
        _caster = caster;
        _skill = skill;
        _simultaneously = simultaneously;
        _target = target;
    }

    public final L2Character getCaster() {
        return _caster;
    }

    public final L2Object getTarget() {
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