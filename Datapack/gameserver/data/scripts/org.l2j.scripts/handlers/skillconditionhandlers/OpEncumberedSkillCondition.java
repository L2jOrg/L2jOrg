package handlers.skillconditionhandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.w3c.dom.Node;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * TODO: Verify me, also should Quest items be counted?
 * @author UnAfraid
 * @author JoeAlisson
 */
public class OpEncumberedSkillCondition implements SkillCondition {

	public final int slotsPercent;
	public final int weightPercent;

	private OpEncumberedSkillCondition(int slots, int weight) {
		this.slotsPercent = slots;
		this.weightPercent = weight;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (!isPlayer(caster)) {
			return false;
		}
		
		final Player player = caster.getActingPlayer();
		final int currentSlotsPercent = calcPercent(player.getInventoryLimit(), player.getInventory().getSize(item -> !item.isQuestItem()));
		final int currentWeightPercent = calcPercent(player.getMaxLoad(), player.getCurrentLoad());
		return (currentSlotsPercent >= slotsPercent) && (currentWeightPercent >= weightPercent);
	}
	
	private int calcPercent(int max, int current)
	{
		return 100 - ((current * 100) / max);
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			var attr = xmlNode.getAttributes();
			return new OpEncumberedSkillCondition(parseInt(attr, "slots"), parseInt(attr, "weight"));
		}

		@Override
		public String conditionName() {
			return "encumbered";
		}
	}
}
