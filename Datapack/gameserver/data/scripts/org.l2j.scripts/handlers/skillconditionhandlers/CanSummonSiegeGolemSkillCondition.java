package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.network.SystemMessageId;
import org.w3c.dom.Node;

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
		if (player.isAlikeDead() || player.isCursedWeaponEquipped() || (player.getClan() == null))
		{
			canSummonSiegeGolem = false;
		}
		
		final Castle castle = CastleManager.getInstance().getCastle(player);
		final Fort fort = FortDataManager.getInstance().getFort(player);
		if ((castle == null) && (fort == null))
		{
			canSummonSiegeGolem = false;
		}
		
		if (((fort != null) && (fort.getId() == 0)) || ((castle != null) && (castle.getId() == 0)))
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			canSummonSiegeGolem = false;
		}
		else if (((castle != null) && !castle.getSiege().isInProgress()) || ((fort != null) && !fort.getSiege().isInProgress()))
		{
			player.sendPacket(SystemMessageId.INVALID_TARGET);
			canSummonSiegeGolem = false;
		}
		else if ((player.getClanId() != 0) && (((castle != null) && (castle.getSiege().getAttackerClan(player.getClanId()) == null)) || ((fort != null) && (fort.getSiege().getAttackerClan(player.getClanId()) == null))))
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
