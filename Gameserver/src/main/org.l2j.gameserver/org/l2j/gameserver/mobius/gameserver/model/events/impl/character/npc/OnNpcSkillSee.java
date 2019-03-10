package org.l2j.gameserver.mobius.gameserver.model.events.impl.character.npc;

import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventType;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;

/**
 * @author UnAfraid
 */
public class OnNpcSkillSee implements IBaseEvent {
    private final L2Npc _npc;
    private final L2PcInstance _caster;
    private final Skill _skill;
    private final L2Object[] _targets;
    private final boolean _isSummon;

    public OnNpcSkillSee(L2Npc npc, L2PcInstance caster, Skill skill, boolean isSummon, L2Object... targets) {
        _npc = npc;
        _caster = caster;
        _skill = skill;
        _isSummon = isSummon;
        _targets = targets;
    }

    public L2Npc getTarget() {
        return _npc;
    }

    public L2PcInstance getCaster() {
        return _caster;
    }

    public Skill getSkill() {
        return _skill;
    }

    public L2Object[] getTargets() {
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
