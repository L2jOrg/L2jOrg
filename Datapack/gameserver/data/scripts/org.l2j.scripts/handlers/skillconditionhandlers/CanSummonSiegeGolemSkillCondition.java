package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.SystemMessageId;
import org.w3c.dom.Node;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class CanSummonSiegeGolemSkillCondition implements SkillCondition {

	private CanSummonSiegeGolemSkillCondition() {
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (!isPlayer(caster)) {
			return false;
		}
		
		final Player player = caster.getActingPlayer();
		boolean canSummonSiegeGolem = true;
		if (player.isAlikeDead() || (player.getClan() == null))
		{
			canSummonSiegeGolem = false;
		}
		
		final Castle castle = CastleManager.getInstance().getCastle(player);
		if (isNull(castle)) {
			canSummonSiegeGolem = false;
		}
		
		if (((castle != null) && (castle.getId() == 0)))
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			canSummonSiegeGolem = false;
		}
		else if ( nonNull(castle) && !castle.getSiege().isInProgress())
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			canSummonSiegeGolem = false;
		}
		else if ( player.getClanId() != 0 && nonNull(castle) && isNull(castle.getSiege().getAttackerClan(player.getClanId())))
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			canSummonSiegeGolem = false;
		}
		
		return canSummonSiegeGolem;
	}

	public static final class Factory extends SkillConditionFactory {
		private static final CanSummonSiegeGolemSkillCondition INSTANCE = new CanSummonSiegeGolemSkillCondition();

		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "CanSummonSiegeGolem";
		}
	}
}
