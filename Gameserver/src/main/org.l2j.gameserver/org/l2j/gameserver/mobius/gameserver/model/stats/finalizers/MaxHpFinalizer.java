package org.l2j.gameserver.mobius.gameserver.model.stats.finalizers;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.EnchantItemHPBonusData;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.mobius.gameserver.model.stats.IStatsFunction;
import org.l2j.gameserver.mobius.gameserver.model.stats.Stats;

import java.util.Optional;

/**
 * @author UnAfraid
 */
public class MaxHpFinalizer implements IStatsFunction
{
	@Override
	public double calc(L2Character creature, Optional<Double> base, Stats stat)
	{
		throwIfPresent(base);
		
		double baseValue = creature.getTemplate().getBaseValue(stat, 0);
		if (creature.isPet())
		{
			final L2PetInstance pet = (L2PetInstance) creature;
			baseValue = pet.getPetLevelData().getPetMaxHP();
		}
		else if (creature.isPlayer())
		{
			final L2PcInstance player = creature.getActingPlayer();
			if (player != null)
			{
				baseValue = player.getTemplate().getBaseHpMax(player.getLevel());
				
				// Apply enchanted item's bonus HP
				for (L2ItemInstance item : player.getInventory().getPaperdollItems(L2ItemInstance::isEnchanted))
				{
					baseValue += EnchantItemHPBonusData.getInstance().getHPBonus(item);
				}
			}
		}
		final double conBonus = creature.getCON() > 0 ? BaseStats.CON.calcBonus(creature) : 1.;
		baseValue *= conBonus;
		return Stats.defaultValue(creature, stat, baseValue);
	}
}
