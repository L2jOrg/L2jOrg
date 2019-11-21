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
import org.l2j.gameserver.model.stats.Stats;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class TwoHandedSwordBonus extends AbstractEffect
{
	private static final Condition _weaponTypeCondition = new ConditionUsingItemType(WeaponType.SWORD.mask());
	private static final Condition _slotCondition = new ConditionUsingSlotType(BodyPart.TWO_HAND.getId());
	
	private final double _pAtkAmount;
	private final StatModifierType _pAtkmode;
	
	private final double _accuracyAmount;
	private final StatModifierType _accuracyMode;
	
	public TwoHandedSwordBonus(StatsSet params)
	{
		_pAtkAmount = params.getDouble("pAtkAmount", 0);
		_pAtkmode = params.getEnum("pAtkmode", StatModifierType.class, StatModifierType.DIFF);
		
		_accuracyAmount = params.getDouble("accuracyAmount", 0);
		_accuracyMode = params.getEnum("accuracyMode", StatModifierType.class, StatModifierType.DIFF);
	}
	
	@Override
	public void pump(Creature effected, Skill skill)
	{
		if (((_weaponTypeCondition == null) || _weaponTypeCondition.test(effected, effected, skill)) && ((_slotCondition == null) || _slotCondition.test(effected, effected, skill)))
		{
			switch (_pAtkmode)
			{
				case DIFF:
				{
					effected.getStat().mergeAdd(Stats.PHYSICAL_ATTACK, _pAtkAmount);
					break;
				}
				case PER:
				{
					effected.getStat().mergeMul(Stats.PHYSICAL_ATTACK, (_pAtkAmount / 100) + 1);
					break;
				}
			}
			
			switch (_accuracyMode)
			{
				case DIFF:
				{
					effected.getStat().mergeAdd(Stats.ACCURACY_COMBAT, _accuracyAmount);
					break;
				}
				case PER:
				{
					effected.getStat().mergeMul(Stats.ACCURACY_COMBAT, (_accuracyAmount / 100) + 1);
					break;
				}
			}
		}
	}
}
