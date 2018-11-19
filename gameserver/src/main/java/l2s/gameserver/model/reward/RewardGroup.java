package l2s.gameserver.model.reward;

import l2s.commons.util.Rnd;
import l2s.gameserver.Config;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.stats.Stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @reworked by Bonux
**/
public class RewardGroup implements Cloneable
{
	private double _chance;
	private boolean _isAdena = true; // Шанс фиксирован, растет только количество
	private boolean _notRate = false; // Рейты вообще не применяются
	private List<RewardData> _items = new ArrayList<RewardData>();

	public RewardGroup(double chance)
	{
		setChance(chance);
	}

	public boolean notRate()
	{
		return _notRate;
	}

	public void setNotRate(boolean notRate)
	{
		_notRate = notRate;
	}

	public double getChance()
	{
		return _chance;
	}

	public void setChance(double chance)
	{
		_chance = Math.min(chance, RewardList.MAX_CHANCE);
	}

	public boolean isAdena()
	{
		return _isAdena;
	}

	public void setIsAdena(boolean isAdena)
	{
		_isAdena = isAdena;
	}

	public void addData(RewardData item)
	{
		if(!item.getItem().isAdena())
			_isAdena = false;
		_items.add(item);
	}

	/**
	 * Возвращает список вещей
	 */
	public List<RewardData> getItems()
	{
		return _items;
	}

	/**
	 * Возвращает полностью независимую копию группы
	 */
	@Override
	public RewardGroup clone()
	{
		RewardGroup ret = new RewardGroup(_chance);
		for(RewardData i : _items)
			ret.addData(i.clone());
		return ret;
	}

	/**
	 * Функция используется в основном механизме расчета дропа, выбирает одну/несколько вещей из группы, в зависимости от рейтов
	 */
	public List<RewardItem> roll(RewardType type, Player player, double penaltyMod, NpcInstance npc)
	{
		switch(type)
		{
			case NOT_RATED_GROUPED:
			case NOT_RATED_NOT_GROUPED:
				return rollItems(penaltyMod, 1.0);
			case EVENT_GROUPED:
				if(npc != null && npc.getReflection().isDefault() && !npc.isRaid() && (npc.getLeader() == null || !npc.getLeader().isRaid()))
					return rollItems(penaltyMod * player.getDropChanceMod() / Config.DROP_CHANCE_MODIFIER, player.getRateItems() / Config.RATE_DROP_ITEMS_BY_LVL[player.getLevel()]);
				return Collections.emptyList();
			case SWEEP:
				return rollItems(penaltyMod * player.getSpoilChanceMod(), player.getRateSpoil() * (npc != null ? npc.calcStat(Stats.SPOIL_RATE_MULTIPLIER, 1.0, player, null) : 1.0));
			case RATED_GROUPED:
				if(isAdena())
					return rollAdena(penaltyMod, player.getRateAdena() * (npc != null ? npc.calcStat(Stats.ADENA_RATE_MULTIPLIER, 1.0, player, null) : 1.0));
				return rollItems(penaltyMod * npc.getDropChanceMod(player), npc != null ? npc.getRewardRate(player) * npc.calcStat(Stats.DROP_RATE_MULTIPLIER, 1.0, player, null) : player.getRateItems());
			default:
				return Collections.emptyList();
		}
	}

	private List<RewardItem> rollAdena(double mod, double rate)
	{
		if(notRate())
		{
			mod = Math.min(mod, 1.);
			rate = 1.;
		}

		if(mod > 0 && rate > 0)
		{
			if(getChance() > Rnd.get(RewardList.MAX_CHANCE))
			{
				List<RewardItem> rolledItems = new ArrayList<RewardItem>();
				for(RewardData data : getItems())
				{
					RewardItem item = data.rollAdena(mod, rate);
					if(item != null)
						rolledItems.add(item);
				}
				if(rolledItems.isEmpty())
				{
					return Collections.emptyList();
				}
				List<RewardItem> result = new ArrayList<RewardItem>();
				for(int i = 0; i < Config.MAX_DROP_ITEMS_FROM_ONE_GROUP; i++)
				{
					RewardItem rolledItem = Rnd.get(rolledItems);
					if(rolledItems.remove(rolledItem))
						result.add(rolledItem);

					if(rolledItems.isEmpty())
						break;
				}
				return result;
			}
		}
		return Collections.emptyList();
	}

	private List<RewardItem> rollItems(double mod, double rate)
	{
		if(notRate())
		{
			mod = Math.min(mod, 1.);
			rate = 1.;
		}

		if(mod > 0 && rate > 0)
		{
			double chance = getChance() * mod;
			if(chance > RewardList.MAX_CHANCE)
			{
				mod = (chance - RewardList.MAX_CHANCE) / getChance() + 1.0;
				chance = RewardList.MAX_CHANCE;
			}
			else
				mod = 1.0;

			if(chance > 0)
			{
				int rolledCount = 0;
				int mult = (int) Math.ceil(rate);
				if(chance >= RewardList.MAX_CHANCE)
				{
					rolledCount = (int) rate;
					if(mult > rate)
					{
						if(chance * (rate - (mult - 1)) > Rnd.get(RewardList.MAX_CHANCE))
							rolledCount++;
					}
				}
				else
				{
					for(int n = 0; n < mult; n++) // TODO: Реально ли оптимизировать без цикла?
					{
						if(chance * Math.min(rate - n, 1.0) > Rnd.get(RewardList.MAX_CHANCE))
							rolledCount++;
					}
				}

				if(rolledCount > 0)
				{
					List<RewardItem> rolledItems = new ArrayList<RewardItem>();
					for(RewardData data : getItems())
					{
						RewardItem item = data.rollItem(mod, rolledCount);
						if(item != null)
							rolledItems.add(item);
					}
					if(rolledItems.isEmpty())
						return Collections.emptyList();

					List<RewardItem> result = new ArrayList<RewardItem>();
					for(int i = 0; i < Config.MAX_DROP_ITEMS_FROM_ONE_GROUP; i++)
					{
						RewardItem rolledItem = Rnd.get(rolledItems);
						if(rolledItems.remove(rolledItem))
							result.add(rolledItem);

						if(rolledItems.isEmpty())
							break;
					}
					return result;
				}
			}
		}
		return Collections.emptyList();
	}
}