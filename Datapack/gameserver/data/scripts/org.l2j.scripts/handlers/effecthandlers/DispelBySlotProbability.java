package handlers.effecthandlers;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.engine.skill.api.Skill;

import static java.util.Objects.isNull;

/**
 * Dispel By Slot Probability effect implementation.
 * @author Adry_85, Zoey76
 * @author JoeAlisson
 */
public final class DispelBySlotProbability extends AbstractEffect {

	public final Set<AbnormalType> dispelAbnormals;
	public final int power;
	
	public DispelBySlotProbability(StatsSet params) {
		dispelAbnormals = Arrays.stream(params.getString("abnormals").split(" ")).map(AbnormalType::valueOf).collect(Collectors.toSet());
		power = params.getInt("power", 100);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DISPEL;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (isNull(effected)) {
			return;
		}
		
		// The effectlist should already check if it has buff with this abnormal type or not.
		effected.getEffectList().stopEffects(info -> !info.getSkill().isIrreplacableBuff() && (Rnd.get(100) < power) && dispelAbnormals.contains(info.getSkill().getAbnormalType()), true, true);
	}
}
