package org.l2j.gameserver.model.skills;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;

/**
 * @author NosBit
 */
public interface ISkillCondition {
    boolean canUse(Creature caster, Skill skill, WorldObject target);
}
