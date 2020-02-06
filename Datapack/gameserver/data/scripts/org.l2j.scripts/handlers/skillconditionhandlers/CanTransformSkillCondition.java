package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.isNull;

/**
 * @author Sdw
 */
public class CanTransformSkillCondition implements ISkillCondition {

	public CanTransformSkillCondition(StatsSet params) {
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		boolean canTransform = true;
		final Player player = caster.getActingPlayer();

		if (isNull(player) || player.isAlikeDead() || player.isCursedWeaponEquipped()) {
			canTransform = false;
		} else if (player.isSitting()) {
			player.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
			canTransform = false;
		} else if (player.isTransformed()) {
			player.sendPacket(SystemMessageId.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
			canTransform = false;
		} else if (player.isInWater()) {
			player.sendPacket(SystemMessageId.YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER);
			canTransform = false;
		} else if (player.isFlyingMounted() || player.isMounted()) {
			player.sendPacket(SystemMessageId.YOU_CANNOT_TRANSFORM_WHILE_RIDING_A_PET);
			canTransform = false;
		}
		return canTransform;
	}
}
