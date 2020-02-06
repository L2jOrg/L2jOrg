package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.targets.TargetType;

/**
 * @author Nik
 */
public interface ITargetTypeHandler {
    WorldObject getTarget(Creature activeChar, WorldObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage);

    Enum<TargetType> getTargetType();
}
