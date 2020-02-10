package org.l2j.gameserver.engine.skill.api;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;

/**
 * @author NosBit
 */
public interface SkillCondition {

    boolean canUse(Creature caster, Skill skill, WorldObject target);
}
