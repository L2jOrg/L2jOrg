package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Block Buff Slot effect implementation.
 * @author Zoey76
 * @author JoeAlisson
 */
public final class BlockAbnormalSlot extends AbstractEffect {

	private final Set<AbnormalType> blockAbnormalSlots;
	
	private BlockAbnormalSlot(StatsSet params) {
		blockAbnormalSlots = Arrays.stream(params.getString("abnormals").split(" ")).map(AbnormalType::valueOf).collect(Collectors.toSet());
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.getEffectList().addBlockedAbnormalTypes(blockAbnormalSlots);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.getEffectList().removeBlockedAbnormalTypes(blockAbnormalSlots);
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new BlockAbnormalSlot(data);
		}

		@Override
		public String effectName() {
			return "block-abnormal";
		}
	}
}
