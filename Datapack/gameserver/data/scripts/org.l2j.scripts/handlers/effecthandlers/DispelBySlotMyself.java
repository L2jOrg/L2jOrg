package handlers.effecthandlers;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * Dispel By Slot effect implementation.
 * @author Gnacik, Zoey76, Adry_85
 */
public final class DispelBySlotMyself extends AbstractEffect {
	private final Set<AbnormalType> dispelAbnormals;
	
	private DispelBySlotMyself(StatsSet params) {
		dispelAbnormals = Arrays.stream(params.getString("abnormals").split(" ")).map(AbnormalType::valueOf).collect(Collectors.toSet());;
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.DISPEL_BY_SLOT;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (dispelAbnormals.isEmpty()) {
			return;
		}
		
		// The effectlist should already check if it has buff with this abnormal type or not.
		effected.getEffectList().stopEffects(info -> !info.getSkill().isIrreplacableBuff() && dispelAbnormals.contains(info.getSkill().getAbnormalType()), true, true);
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new DispelBySlotMyself(data);
		}

		@Override
		public String effectName() {
			return "dispel-myself";
		}
	}
}
