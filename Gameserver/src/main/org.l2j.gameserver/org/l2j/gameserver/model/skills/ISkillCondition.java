package org.l2j.gameserver.model.skills;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.Creature;

/**
 * @author NosBit
 */
public interface ISkillCondition {
    boolean canUse(Creature caster, Skill skill, L2Object target);
}
