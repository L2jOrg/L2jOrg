package handlers.skillconditionhandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.ISkillCondition;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * TODO: Verify me, also should Quest items be counted?
 * @author UnAfraid
 */
public class OpEncumberedSkillCondition implements ISkillCondition {

	public final int slotsPercent;
	public final int weightPercent;
	
	public OpEncumberedSkillCondition(StatsSet params) {
		slotsPercent = params.getInt("slotsPercent");
		weightPercent = params.getInt("weightPercent");
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
}
