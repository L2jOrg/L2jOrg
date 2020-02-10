package handlers.skillconditionhandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.w3c.dom.Node;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class OpSweeperSkillCondition implements SkillCondition {

	private OpSweeperSkillCondition() {
	}
	
	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target)
	{
		final AtomicBoolean canSweep = new AtomicBoolean(false);
		if (caster.getActingPlayer() != null)
		{
			final Player sweeper = caster.getActingPlayer();
			if (skill != null)
			{
				skill.forEachTargetAffected(sweeper, target, o ->
				{
					if (isAttackable(o))
					{
						final Attackable a = (Attackable) o;
						if (a.isDead())
						{
							if (a.isSpoiled())
							{
								canSweep.set(a.checkSpoilOwner(sweeper, true));
								if (canSweep.get())
								{
									canSweep.set(!a.isOldCorpse(sweeper, Config.CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY, true));
								}
								if (canSweep.get())
								{
									canSweep.set(sweeper.getInventory().checkInventorySlotsAndWeight(a.getSpoilLootItems(), true, true));
								}
							}
							else
							{
								sweeper.sendPacket(SystemMessageId.SWEEPER_FAILED_TARGET_NOT_SPOILED);
							}
						}
					}
				});
			}
		}
		return canSweep.get();
	}

	public static final class Factory extends SkillConditionFactory {
		private static final OpSweeperSkillCondition INSTANCE = new OpSweeperSkillCondition();

		@Override
		public SkillCondition create(Node xmlNode) {
			return INSTANCE;
		}

		@Override
		public String conditionName() {
			return "OpSweeper";
		}
	}
}
