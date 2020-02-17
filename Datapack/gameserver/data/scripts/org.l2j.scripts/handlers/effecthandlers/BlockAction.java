package handlers.effecthandlers;

import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.StreamUtil;
import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;

import java.util.Arrays;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Block Action effect implementation.
 * @author BiggBoss
 * @author JoeAlisson
 */
public final class BlockAction extends AbstractEffect {

	private IntSet blockedActions;
	
	private BlockAction(StatsSet params) {
		blockedActions = StreamUtil.collectToSet(Arrays.stream(params.getString("actions").split(" ")).mapToInt(Integer::parseInt));
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return isPlayer(effected);
	}
	
	@Override
	public boolean checkCondition(Object id) {
		return !(id instanceof Integer) || !blockedActions.contains((int) id);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if (blockedActions.contains(ReportTable.PARTY_ACTION_BLOCK_ID)) {
			PunishmentManager.getInstance().startPunishment(new PunishmentTask(0, effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.PARTY_BAN, 0, "block action debuff", "system", true));
		}
		
		if (blockedActions.contains(ReportTable.CHAT_BLOCK_ID)) {
			PunishmentManager.getInstance().startPunishment(new PunishmentTask(0, effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN, 0, "block action debuff", "system", true));
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		if (blockedActions.contains(ReportTable.PARTY_ACTION_BLOCK_ID)) {
			PunishmentManager.getInstance().stopPunishment(effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.PARTY_BAN);
		}

		if (blockedActions.contains(ReportTable.CHAT_BLOCK_ID)) {
			PunishmentManager.getInstance().stopPunishment(effected.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN);
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new BlockAction(data);
		}

		@Override
		public String effectName() {
			return "block-action";
		}
	}
}
