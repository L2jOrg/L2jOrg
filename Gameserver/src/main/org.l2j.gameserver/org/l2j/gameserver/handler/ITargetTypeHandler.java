package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.skills.targets.TargetType;

/**
 * @author Nik
 */
public interface ITargetTypeHandler {
    L2Object getTarget(Creature activeChar, L2Object selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage);

    Enum<TargetType> getTargetType();
}
