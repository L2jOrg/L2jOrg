package org.l2j.gameserver.model.skills;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.L2Character;

/**
 * @author NosBit
 */
public interface ISkillCondition {
    boolean canUse(L2Character caster, Skill skill, L2Object target);
}
