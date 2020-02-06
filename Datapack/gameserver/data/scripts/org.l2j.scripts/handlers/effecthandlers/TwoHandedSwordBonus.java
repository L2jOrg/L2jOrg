package handlers.effecthandlers;

import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.conditions.Condition;
import org.l2j.gameserver.model.conditions.ConditionUsingItemType;
import org.l2j.gameserver.model.conditions.ConditionUsingSlotType;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.type.WeaponType;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class TwoHandedSwordBonus extends AbstractEffect {
	private static final Condition weaponTypeCondition = new ConditionUsingItemType(WeaponType.SWORD.mask());
	private static final Condition slotCondition = new ConditionUsingSlotType(BodyPart.TWO_HAND.getId());
	
	public final double pAtkAmount;
	public final StatModifierType pAtkmode;
	
	public final double accuracyAmount;
	public final StatModifierType accuracyMode;
	
	public TwoHandedSwordBonus(StatsSet params) {
		pAtkAmount = params.getDouble("pAtkAmount", 0);
		pAtkmode = params.getEnum("pAtkmode", StatModifierType.class, StatModifierType.DIFF);
		
		accuracyAmount = params.getDouble("accuracyAmount", 0);
		accuracyMode = params.getEnum("accuracyMode", StatModifierType.class, StatModifierType.DIFF);
	}
	
	@Override
	public void pump(Creature effected, Skill skill) {
		if (weaponTypeCondition.test(effected, effected, skill) && slotCondition.test(effected, effected, skill)) {
			switch (pAtkmode) {
				case DIFF -> effected.getStats().mergeAdd(Stat.PHYSICAL_ATTACK, pAtkAmount);
				case PER -> effected.getStats().mergeMul(Stat.PHYSICAL_ATTACK, (pAtkAmount / 100) + 1);
			}

			switch (accuracyMode) {
				case DIFF -> effected.getStats().mergeAdd(Stat.ACCURACY, accuracyAmount);
				case PER -> effected.getStats().mergeMul(Stat.ACCURACY, (accuracyAmount / 100) + 1);
			}
		}
	}
}
