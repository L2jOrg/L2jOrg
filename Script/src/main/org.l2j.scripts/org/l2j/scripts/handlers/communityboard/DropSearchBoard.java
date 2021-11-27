/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.scripts.handlers.communityboard;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.enums.DropType;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.handler.IParseBoardHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.holders.DropHolder;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.settings.RateSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.*;

/**
 * @author yksdtc
 */
public class DropSearchBoard implements IParseBoardHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(DropSearchBoard.class);

	private static final String NAVIGATION_PATH = "data/html/CommunityBoard/Custom/new/navigation.html";
	private static final String[] COMMAND =
	{
		"_bbs_search_item",
		"_bbs_search_drop",
		"_bbs_npc_trace"
	};
	public static final String TD = "<td>";
	public static final String CLOSE_TD = "</td>";

	private static class CBDropHolder
	{
		final int itemId;
		final int npcId;
		final byte npcLevel;
		final long min;
		final long max;
		final double chance;
		final boolean isSpoil;
		final boolean isRaid;
		
		public CBDropHolder(NpcTemplate npcTemplate, DropHolder dropHolder)
		{
			isSpoil = dropHolder.getDropType() == DropType.SPOIL;
			itemId = dropHolder.getItemId();
			npcId = npcTemplate.getId();
			npcLevel = npcTemplate.getLevel();
			min = dropHolder.getMin();
			max = dropHolder.getMax();
			chance = dropHolder.getChance();
			isRaid = npcTemplate.getType().equals("RaidBoss") || npcTemplate.getType().equals("GrandBoss");
		}
		
		/**
		 * only for debug
		 */
		@Override
		public String toString()
		{
			return "DropHolder [itemId=" + itemId + ", npcId=" + npcId + ", npcLevel=" + npcLevel + ", min=" + min + ", max=" + max + ", chance=" + chance + ", isSpoil=" + isSpoil + "]";
		}
	}
	
	private final Map<Integer, List<CBDropHolder>> DROP_INDEX_CACHE = new HashMap<>();
	
	// nonsupport items
	private final Set<Integer> BLOCK_ID = new HashSet<>();
	{
		BLOCK_ID.add(CommonItem.ADENA);
	}
	
	public DropSearchBoard()
	{
		buildDropIndex();
	}
	
	private void buildDropIndex()
	{
		NpcData.getInstance().getTemplates(npc -> npc.getDropList(DropType.DROP) != null).forEach(npcTemplate ->
		{
			for (DropHolder dropHolder : npcTemplate.getDropList(DropType.DROP))
			{
				addToDropList(npcTemplate, dropHolder);
			}
		});
		NpcData.getInstance().getTemplates(npc -> npc.getDropList(DropType.SPOIL) != null).forEach(npcTemplate ->
		{
			for (DropHolder dropHolder : npcTemplate.getDropList(DropType.SPOIL))
			{
				addToDropList(npcTemplate, dropHolder);
			}
		});
		
		DROP_INDEX_CACHE.values().forEach(l -> l.sort(Comparator.comparingInt(d -> d.npcLevel)));
	}
	
	private void addToDropList(NpcTemplate npcTemplate, DropHolder dropHolder) {
		if (BLOCK_ID.contains(dropHolder.getItemId())) {
			return;
		}

		List<CBDropHolder> dropList = DROP_INDEX_CACHE.computeIfAbsent(dropHolder.getItemId(), k -> new ArrayList<>());
		dropList.add(new CBDropHolder(npcTemplate, dropHolder));
	}
	
	@Override
	public boolean parseCommunityBoardCommand(String command, StringTokenizer tokens, Player player) {
		final String navigation = HtmCache.getInstance().getHtm(player, NAVIGATION_PATH);
		String[] params = command.split(" ");
		String html = HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/Custom/dropsearch/main.html");
		switch (params[0]) {
			case "_bbs_search_item" -> html = searchItem(params, html);
			case "_bbs_search_drop" -> html = searchDrop(player, params, html);
			case "_bbs_npc_trace" -> npcTrace(player, params[1]);
			default -> LOGGER.warn("Unknown command: {}", params[0]);
		}
		
		if (html != null) {
			html = html.replace("%navigation%", navigation);
			CommunityBoardHandler.separateAndSend(html, player);
		}
		
		return false;
	}

	private void npcTrace(Player player, String param) {
		int npcId = Integer.parseInt(param);
		List<NpcSpawnTemplate> spawnList = SpawnsData.getInstance().getNpcSpawns(npc -> npc.getId() == npcId);
		if (spawnList.isEmpty()) {
			player.sendMessage("Cannot find any spawn. Maybe dropped by a boss or instance monster.");
		} else {
			NpcSpawnTemplate spawn = spawnList.get(Rnd.get(spawnList.size()));
			var location = spawn.getSpawnLocation();
			if(location != null) {
				player.getRadar().addMarker(location.getX(), location.getY(), location.getZ());
			} else {
				player.sendMessage("Cannot find a location in spawn " + spawn.getId());
			}
		}
	}

	private String searchDrop(Player player, String[] params, String html) {
		final DecimalFormat chanceFormat = new DecimalFormat("0.00##");
		int itemId = Integer.parseInt(params[1]);
		int page = Integer.parseInt(params[2]);
		List<CBDropHolder> list = DROP_INDEX_CACHE.get(itemId);
		int pages = list.size() / 14;
		if (pages == 0) {
			pages++;
		}

		int start = (page - 1) * 14;
		int end = Math.min(list.size() - 1, start + 14);
		StringBuilder builder = new StringBuilder();
		final double dropAmountEffectBonus = player.getStats().getValue(Stat.BONUS_DROP_AMOUNT, 1);
		final double dropRateEffectBonus = player.getStats().getValue(Stat.BONUS_DROP_RATE, 1);
		final double spoilRateEffectBonus = player.getStats().getValue(Stat.BONUS_SPOIL_RATE, 1);
		for (int index = start; index <= end; index++) {
			addDrop(chanceFormat, list, builder, dropAmountEffectBonus, dropRateEffectBonus, spoilRateEffectBonus, index);
		}

		html = html.replace("%searchResult%", builder.toString());
		builder.setLength(0);

		builder.append("<tr>");
		for (page = 1; page <= pages; page++) {
			builder.append(TD).append("<a action=\"bypass -h _bbs_search_drop ").append(itemId).append(" ").append(page).append(" $order $level\">").append(page).append("</a>").append(CLOSE_TD);
		}
		builder.append("</tr>");
		html = html.replace("%pages%", builder.toString());
		return html;
	}

	private void addDrop(DecimalFormat chanceFormat, List<CBDropHolder> list, StringBuilder builder, double dropAmountEffectBonus, double dropRateEffectBonus, double spoilRateEffectBonus, int index) {
		CBDropHolder cbDropHolder = list.get(index);
		double rateChance;
		double rateAmount;
		if (cbDropHolder.isSpoil) {
			rateChance = RateSettings.spoilChance();
			rateAmount = RateSettings.spoilAmount();
			rateChance *= spoilRateEffectBonus;
		} else {
			final ItemTemplate item = ItemEngine.getInstance().getTemplate(cbDropHolder.itemId);
			rateChance = calculateRateChance(cbDropHolder, item, dropRateEffectBonus);
			rateAmount = calculateRateAmount(dropAmountEffectBonus, cbDropHolder, item);
		}

		builder.append("<tr>");
		builder.append("<td width=30>").append(cbDropHolder.npcLevel).append(CLOSE_TD);
		builder.append("<td width=170>").append("<a action=\"bypass _bbs_npc_trace ").append(cbDropHolder.npcId).append("\">").append("&@").append(cbDropHolder.npcId).append(";").append("</a>").append(CLOSE_TD);
		builder.append("<td width=80 align=CENTER>").append(cbDropHolder.min * rateAmount).append("-").append(cbDropHolder.max * rateAmount).append(CLOSE_TD);
		builder.append("<td width=50 align=CENTER>").append(chanceFormat.format(cbDropHolder.chance * rateChance)).append("%").append(CLOSE_TD);
		builder.append("<td width=50 align=CENTER>").append(cbDropHolder.isSpoil ? "Spoil" : "Drop").append(CLOSE_TD);
		builder.append("</tr>");
	}

	private double calculateRateAmount(double dropAmountEffectBonus, CBDropHolder cbDropHolder, ItemTemplate item) {
		double rateAmount;
		rateAmount = RateSettings.dropAmountOf(cbDropHolder.itemId);

		if (rateAmount == 1) {
			if (cbDropHolder.isRaid) {
				rateAmount *= RateSettings.raidDropAmount();
			} else if (!item.hasExImmediateEffect()) {
				rateAmount *= RateSettings.deathDropAmount();
			}
		}

		// bonus drop amount effect
		rateAmount *= dropAmountEffectBonus;
		return rateAmount;
	}

	private double calculateRateChance(CBDropHolder cbDropHolder, ItemTemplate item, double dropRateEffectBonus) {
		double rateChance;
		rateChance = RateSettings.dropChanceOf(cbDropHolder.itemId);

		if (rateChance == 1) {
			if (item.hasExImmediateEffect()) {
				rateChance *= RateSettings.herbDropChance();
			} else if (cbDropHolder.isRaid) {
				rateChance *= RateSettings.raidDropChance();
			} else {
				rateChance *= RateSettings.deathDropChance();
			}
		}
		return rateChance * dropRateEffectBonus;
	}

	private String searchItem(String[] params, String html) {
		String itemName = buildItemName(params);
		String result = buildItemSearchResult(itemName);
		html = html.replace("%searchResult%", result);
		return html;
	}

	private String buildItemSearchResult(String itemName)
	{
		int limit = 0;
		Set<Integer> existInDropData = DROP_INDEX_CACHE.keySet();
		List<ItemTemplate> items = new ArrayList<>();
		for (ItemTemplate item : ItemEngine.getInstance().getAllItems())
		{
			if (item == null)
			{
				continue;
			}
			
			if (!existInDropData.contains(item.getId()))
			{
				continue;
			}
			
			if (item.getName().toLowerCase().contains(itemName.toLowerCase()))
			{
				items.add(item);
				limit++;
			}
			
			if (limit == 14)
			{
				break;
			}
		}
		
		if (items.isEmpty())
		{
			return "<tr><td width=100 align=CENTER>No Match</td></tr>";
		}
		
		int line = 0;
		
		StringBuilder builder = new StringBuilder(items.size() * 28);
		int i = 0;
		for (ItemTemplate item : items)
		{
			i++;
			if (i == 1)
			{
				line++;
				builder.append("<tr>");
			}
			
			builder.append(TD);
			builder.append("<button value=\".\" action=\"bypass _bbs_search_drop ").append(item.getId()).append(" 1 $order $level\" width=32 height=32  itemtooltip=\"").append(item.getId()).append("\">");
			builder.append(CLOSE_TD);
			builder.append("<td width=200>");
			builder.append("&#").append(item.getId()).append(";");
			builder.append(CLOSE_TD);
			
			if (i == 2)
			{
				builder.append("</tr>");
				i = 0;
			}
		}
		
		if ((i % 2) == 1)
		{
			builder.append("</tr>");
		}
		
		if (line < 7)
		{
			for (i = 0; i < (7 - line); i++)
			{
				builder.append("<tr><td height=36></td></tr>");
			}
		}
		
		return builder.toString();
	}

	private String buildItemName(String[] params)
	{
		StringJoiner joiner = new StringJoiner(" ");
		for (int i = 1; i < params.length; i++)
		{
			joiner.add(params[i]);
		}
		return joiner.toString();
	}
	
	@Override
	public String[] getCommunityBoardCommands()
	{
		return COMMAND;
	}
}
