package handlers.targethandlers;

import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isArtifact;

/**
 * Target siege artefact.
 * @author Nik
 */
public class HolyThing implements ITargetTypeHandler
{
	@Override
	public Enum<TargetType> getTargetType()
	{
		return TargetType.HOLYTHING;
	}
	
	@Override
	public WorldObject getTarget(Creature activeChar, WorldObject selectedTarget, Skill skill, boolean forceUse, boolean dontMove, boolean sendMessage) {
		var target = activeChar.getTarget();
		if (isArtifact(target)){
			return target;
		}
		
		if (sendMessage) {
			activeChar.sendPacket(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
		}
		return null;
	}
}
