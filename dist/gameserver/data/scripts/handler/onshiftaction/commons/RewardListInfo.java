package handler.onshiftaction.commons;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import l2s.gameserver.Config;
import l2s.gameserver.data.htm.HtmCache;
import l2s.gameserver.data.htm.HtmTemplates;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.base.Experience;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.model.instances.RaidBossInstance;
import l2s.gameserver.model.reward.RewardData;
import l2s.gameserver.model.reward.RewardGroup;
import l2s.gameserver.model.reward.RewardList;
import l2s.gameserver.model.reward.RewardType;
import l2s.gameserver.stats.Stats;
import l2s.gameserver.utils.HtmlUtils;

import org.apache.commons.lang3.StringUtils;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;

/**
 * @reworked by Bonux
**/
public abstract class RewardListInfo
{
	private static class DropInfo
	{
		public final String name;
		public final String icon;
		public final long minCount;
		public final long maxCount;
		public final double chance;

		public DropInfo(String name, String icon, long minCount, long maxCount, double chance)
		{
			this.name = name;
			this.icon = icon;
			this.minCount = minCount;
			this.maxCount = maxCount;
			this.chance = chance;
		}
	}

	private static final int MAX_ITEMS_ON_PAGE = 10;

	private static final NumberFormat PERCENT_FORMAT_5 = NumberFormat.getPercentInstance(Locale.ENGLISH);
	private static final NumberFormat PERCENT_FORMAT_10 = NumberFormat.getPercentInstance(Locale.ENGLISH);
	private static final NumberFormat PERCENT_FORMAT_15 = NumberFormat.getPercentInstance(Locale.ENGLISH);
	static
	{
		PERCENT_FORMAT_5.setMaximumFractionDigits(5);
		PERCENT_FORMAT_5.setMinimumFractionDigits(0);
		PERCENT_FORMAT_10.setMaximumFractionDigits(10);
		PERCENT_FORMAT_10.setMinimumFractionDigits(0);
		PERCENT_FORMAT_15.setMaximumFractionDigits(15);
		PERCENT_FORMAT_15.setMinimumFractionDigits(0);
	}

	public static void showInfo(Player player, NpcInstance npc, RewardType showType, int page)
	{
		final int diff = npc.calculateLevelDiffForDrop(player.isInParty() ? player.getParty().getLevel() : player.getLevel());

		double mod = Experience.penaltyModifier(diff, 9);

		final HtmTemplates tpls = HtmCache.getInstance().getTemplates("actions/rewardlist_info.htm", player);

		String html = tpls.get(0);
		html = html.replace("<?npc_name?>", HtmlUtils.htmlNpcName(npc.getNpcId()));
		html = html.replace("<?id?>", String.valueOf(npc.getNpcId()));

		StringBuilder content = new StringBuilder();
		if(mod <= 0)
			content.append(tpls.get(1));
		else if(npc.getTemplate().getRewards().isEmpty())
			content.append(tpls.get(2));
		else
		{
			List<RewardList> commonRewardLists = new ArrayList<RewardList>();
			List<RewardList> spoilRewardLists = new ArrayList<RewardList>();
			List<RewardList> eventRewardLists = new ArrayList<RewardList>();
			for(RewardList list : npc.getTemplate().getRewards())
			{
				final RewardType type = list.getType();
				switch(type)
				{
					case RATED_GROUPED:
					case NOT_RATED_GROUPED:
					case NOT_RATED_NOT_GROUPED:
						commonRewardLists.add(list);
						break;
					case SWEEP:
						spoilRewardLists.add(list);
						break;
					case EVENT_GROUPED:
						eventRewardLists.add(list);
						break;
				}
			}
			content.append(makeRewardListInfo(player, tpls, npc, commonRewardLists, RewardType.RATED_GROUPED, tpls.get(3), mod, showType == RewardType.RATED_GROUPED || showType == RewardType.NOT_RATED_GROUPED || showType == RewardType.NOT_RATED_NOT_GROUPED, page));
			content.append(makeRewardListInfo(player, tpls, npc, spoilRewardLists, RewardType.SWEEP, tpls.get(4), mod, showType == RewardType.SWEEP, page));
			content.append(makeRewardListInfo(player, tpls, npc, eventRewardLists, RewardType.EVENT_GROUPED, tpls.get(5), mod, showType == RewardType.EVENT_GROUPED, page));
		}

		html = html.replace("<?content?>", content.toString());

		npc.showChatWindow(player, html, false);
	}

	public static String makeRewardListInfo(Player player, HtmTemplates tpls, NpcInstance npc, List<RewardList> rewardLists, RewardType groupType, String groupName, double penaltyMod, boolean show, int page)
	{
		if(show)
		{
			List<DropInfo> infos = new ArrayList<DropInfo>();
			for(RewardList list : rewardLists)
			{
				if(list.isEmpty())
					continue;

				RewardType type = list.getType();
				for(RewardGroup g : list)
				{
					List<RewardData> items = new ArrayList<RewardData>();
					for(RewardData d : g.getItems())
					{
						if(!d.getItem().isHerb())
							items.add(d);
					}

					if(items.isEmpty())
						continue;

					double grate = 1.0;
					double gpmod = penaltyMod;

					if(type == RewardType.RATED_GROUPED)
					{
						if(g.isAdena())
						{
							double rateAdena = player.getRateAdena();
							if(rateAdena == 0)
								continue;

							grate = rateAdena * npc.calcStat(Stats.ADENA_RATE_MULTIPLIER, 1., player, null);
						}
						else
						{
							double rateDrop = npc.getRewardRate(player);
							if(rateDrop == 0)
								continue;

							grate = rateDrop * npc.calcStat(Stats.DROP_RATE_MULTIPLIER, 1., player, null);
							gpmod *= npc.getDropChanceMod(player);
						}
					}
					else if(type == RewardType.SWEEP)
					{
						grate = player.getRateSpoil() * npc.calcStat(Stats.SPOIL_RATE_MULTIPLIER, 1., player, null);
						gpmod *= player.getSpoilChanceMod();
					}
					else if(type == RewardType.EVENT_GROUPED)
					{
						grate = player.getRateItems() / Config.RATE_DROP_ITEMS_BY_LVL[player.getLevel()];
						gpmod *= player.getDropChanceMod() / Config.DROP_CHANCE_MODIFIER;
					}

					if(g.notRate())
					{
						gpmod = Math.min(gpmod, 1.);
						grate = 1.;
					}

					if(!player.isGM() && (gpmod == 0 || grate == 0))
						continue;

					double groupChance = g.getChance() * gpmod;
					if(groupChance > RewardList.MAX_CHANCE)
					{
						gpmod = (groupChance - RewardList.MAX_CHANCE) / g.getChance() + 1;
						groupChance = RewardList.MAX_CHANCE;
					}
					else
						gpmod = 1.;

					double groupChanceModifier = (RewardList.MAX_CHANCE - groupChance) / RewardList.MAX_CHANCE;

					// Дальше идут изжопы с шансами, для того, чтобы отображать реальный шанс выпадения предмета.
					double itemMaxChance = (double) RewardList.MAX_CHANCE / g.getItems().size() * Math.min(g.getItems().size(), Config.MAX_DROP_ITEMS_FROM_ONE_GROUP) / RewardList.MAX_CHANCE;

					int normalChancesCount = 0;
					double normalChancesSum = 0.;
					for(RewardData d : g.getItems())
					{
						double ipmod = d.notRate() ? Math.min(gpmod, 1.) : gpmod;
						double irate = d.notRate() ? 1.0 : grate;
						double chance = Math.min(RewardList.MAX_CHANCE, (d.getChance() - (d.getChance() * groupChanceModifier)) * ipmod) / RewardList.MAX_CHANCE;
						if(!g.isAdena())
							chance = getMinEventsChance(irate, chance, 1);
						if(chance < itemMaxChance)
						{
							normalChancesCount++;
							normalChancesSum += chance;
						}
					}

					// Высчитываем максимальный шанс учитывая количество предметов в группе и лимит дропа с группы.
					itemMaxChance += ((normalChancesCount * itemMaxChance) - normalChancesSum) / (g.getItems().size() - normalChancesCount);

					for(RewardData d : items)
					{
						double ipmod = d.notRate() ? Math.min(gpmod, 1.) : gpmod;
						double irate = d.notRate() ? 1.0 : grate;
						String icon = d.getItem().getIcon();
						if(icon == null || icon.equals(StringUtils.EMPTY) || icon.equalsIgnoreCase("none"))
							icon = "icon.etc_question_mark_i00";

						long minCount = Math.round(d.getMinDrop() * (g.isAdena() ? irate : 1.));
						long maxCount = Math.round(d.getMaxDrop() * (g.isAdena() ? irate : 1.));
						if(irate > 1 && !g.isAdena())
							maxCount = -maxCount;

						// Подсчитываем базовый шанс предмета учитывая шанс группы.
						double chance = Math.min(RewardList.MAX_CHANCE, (d.getChance() - (d.getChance() * groupChanceModifier)) * ipmod) / RewardList.MAX_CHANCE;

						// Учитываем рейты для минимального события дропа (шанс, что выпадет хотябы 1 раз).
						if(!g.isAdena())
							chance = getMinEventsChance(irate, chance, 1);

						// Ставим реальный максимальный шанс учитывая количество предметов в группе и лимит дропа с группы.
						if(g.getItems().size() > Config.MAX_DROP_ITEMS_FROM_ONE_GROUP)
							chance = Math.min(itemMaxChance, chance);

						infos.add(new DropInfo(HtmlUtils.htmlItemName(d.getItemId()), icon, minCount, maxCount, chance));
					}
				}
			}

			if(infos.isEmpty())
				return "";

			final int minPage = 1;
			final int maxPage = (int) Math.ceil((double) infos.size() / MAX_ITEMS_ON_PAGE);
			final int currentPage = Math.max(Math.min(maxPage, page), minPage);

			String groupBlock = tpls.get(7);

			String prevButton;
			if(currentPage == minPage || maxPage == 1)
				prevButton = tpls.get(9);
			else
			{
				prevButton = tpls.get(10);
				prevButton = prevButton.replace("<?page?>", String.valueOf(currentPage - 1));
			}

			prevButton = prevButton.replace("<?button_name?>", "«");
			groupBlock = groupBlock.replace("<?prev_button?>", prevButton);

			String nextButton;
			if(currentPage == maxPage || maxPage == 1)
				nextButton = tpls.get(9);
			else
			{
				nextButton = tpls.get(10);
				nextButton = nextButton.replace("<?page?>", String.valueOf(currentPage + 1));
			}

			nextButton = nextButton.replace("<?button_name?>", "»");
			groupBlock = groupBlock.replace("<?next_button?>", nextButton);

			groupBlock = groupBlock.replace("<?group_name?>", groupName);
			groupBlock = groupBlock.replace("<?group_type?>", String.valueOf(groupType));

			StringBuilder dropList = new StringBuilder();

			for(int i = ((currentPage - 1) * MAX_ITEMS_ON_PAGE); i < Math.min(currentPage * MAX_ITEMS_ON_PAGE, infos.size()); i++)
			{
				DropInfo info = infos.get(i);
				String dropBlock = tpls.get(8);
				if(info.minCount == Math.abs(info.maxCount))
					dropBlock = dropBlock.replace("<?drop_chances?>", tpls.get(11));
				else
					dropBlock = dropBlock.replace("<?drop_chances?>", tpls.get(12));
				dropBlock = dropBlock.replace("<?line_color?>", (((i % 2) == 0) ? "2D2D2D" : "000000"));
				dropBlock = dropBlock.replace("<?drop_icon?>", info.icon);
				dropBlock = dropBlock.replace("<?drop_item_name?>", info.name);
				dropBlock = dropBlock.replace("<?drop_count?>", String.valueOf(info.minCount) + (info.maxCount < 0 ? "+" : ""));
				dropBlock = dropBlock.replace("<?drop_min_count?>", String.valueOf(info.minCount));
				dropBlock = dropBlock.replace("<?drop_max_count?>", String.valueOf(Math.abs(info.maxCount)) + (info.maxCount < 0 ? "+" : ""));
				dropBlock = dropBlock.replace("<?drop_chance?>", getFormatedChance(info.chance));

				dropList.append(dropBlock);
			}

			groupBlock = groupBlock.replace("<?drop_list?>", dropList.toString());
			return groupBlock;
		}
		else
		{
			boolean haveDrop = false;
			for(RewardList list : rewardLists)
			{
				if(list.isEmpty())
					continue;

				for(RewardGroup g : list)
				{
					for(RewardData d : g.getItems())
					{
						if(!d.getItem().isHerb())
						{
							haveDrop = true;
							break;
						}
					}
				}
			}

			if(haveDrop)
			{
				String groupBlock = tpls.get(6);
				groupBlock = groupBlock.replace("<?group_name?>", groupName);
				groupBlock = groupBlock.replace("<?group_type?>", String.valueOf(groupType));
				return groupBlock;
			}
		}
		return "";
	}

	private static String getFormatedChance(double chance)
	{
		NumberFormat pf;
		if(chance < 0.000000000001)
			pf = PERCENT_FORMAT_15;
		else if(chance < 0.0000001)
			pf = PERCENT_FORMAT_10;
		else
			pf = PERCENT_FORMAT_5;
		return pf.format(chance);
	}

	/**
	 * Формула Бернулли: Pn(m >= k)
	 * Возвращает вероятность того, что событие с шансом 'p' произойдет за 'n' попыток минимум 'k' раз.
	**/
	private static double getMinEventsChance(double n, double p, int k)
	{
		if(n == 1)
			return p;

		n = Math.min(5000., n); // Ставим лимит, а то будет давать сильную нагрузку и не совсем корректные вычисления.

		double P = 0.;
		for(int i = 0; i < k; i++)
			P += getP(i, n, p);

		return Math.min(1., 1. - P);
	}

	private static double getP(double a, double b, double p)
	{
		return getC((int) a, (int) b) * Math.pow(p, a) * Math.pow(1. - p, b - a);
	}

	public static double getC(int a, int b)
	{
		return factorial(b).divide((factorial(b - a).multiply(factorial(a)))).doubleValue();
	}

	private static final IntObjectMap<BigInteger> FACTORIAL_CACHE = new CHashIntObjectMap<BigInteger>();

	public static BigInteger factorial(int n)
	{
		if(n == 0)
			return BigInteger.ONE;

		BigInteger ret;
		if((ret = FACTORIAL_CACHE.get(n)) != null)
			return ret;

		ret = BigInteger.ONE;
		for (int i = 1; i <= n; ++i) ret = ret.multiply(BigInteger.valueOf(i));
		/*TODO: Найти более оптимильный вариант, а то данный вариант иногда вызывает StackOverflow
		ret = BigInteger.valueOf(n).multiply(factorial(n - 1));*/
		FACTORIAL_CACHE.put(n, ret);
		return ret;
	}
}