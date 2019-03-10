package org.l2j.gameserver.mobius.gameserver.handler;

import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.model.skills.targets.TargetType;

/**
 * @author Nik
 */
public interface ITargetTypeHandler {
    L2Object getTarget(L2Character activeChar, L2Object selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage);

    Enum<TargetType> getTargetType();
}
