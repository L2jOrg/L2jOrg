/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package handlers.effecthandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.l2jmobius.Config;
import com.l2jmobius.commons.util.Rnd;
import com.l2jmobius.gameserver.model.L2ExtractableProductItem;
import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.effects.L2EffectType;
import com.l2jmobius.gameserver.model.holders.RestorationItemHolder;
import com.l2jmobius.gameserver.model.items.instance.L2ItemInstance;
import com.l2jmobius.gameserver.model.skills.Skill;
import com.l2jmobius.gameserver.network.SystemMessageId;
import com.l2jmobius.gameserver.network.serverpackets.InventoryUpdate;
import com.l2jmobius.gameserver.network.serverpackets.SystemMessage;

/**
 * Restoration Random effect implementation.<br>
 * This effect is present in item skills that "extract" new items upon usage.<br>
 * This effect has been unhardcoded in order to work on targets as well.
 * @author Zoey76, Mobius
 */
public final class RestorationRandom extends AbstractEffect
{
	private final List<L2ExtractableProductItem> _products = new ArrayList<>();
	
	public RestorationRandom(StatsSet params)
	{
		for (StatsSet group : params.getList("items", StatsSet.class))
		{
			final List<RestorationItemHolder> items = new ArrayList<>();
			for (StatsSet item : group.getList(".", StatsSet.class))
			{
				items.add(new RestorationItemHolder(item.getInt(".id"), item.getInt(".count"), item.getInt(".minEnchant", 0), item.getInt(".maxEnchant", 0)));
			}
			_products.add(new L2ExtractableProductItem(items, group.getFloat(".chance")));
		}
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(L2Character effector, L2Character effected, Skill skill, L2ItemInstance item)
	{
		final double rndNum = 100 * Rnd.nextDouble();
		double chance = 0;
		double chanceFrom = 0;
		final List<RestorationItemHolder> creationList = new ArrayList<>();
		
		// Explanation for future changes:
		// You get one chance for the current skill, then you can fall into
		// one of the "areas" like in a roulette.
		// Example: for an item like Id1,A1,30;Id2,A2,50;Id3,A3,20;
		// #---#-----#--#
		// 0--30----80-100
		// If you get chance equal 45% you fall into the second zone 30-80.
		// Meaning you get the second production list.
		// Calculate extraction
		for (L2ExtractableProductItem expi : _products)
		{
			chance = expi.getChance();
			if ((rndNum >= chanceFrom) && (rndNum <= (chance + chanceFrom)))
			{
				creationList.addAll(expi.getItems());
				break;
			}
			chanceFrom += chance;
		}
		
		final L2PcInstance player = effected.getActingPlayer();
		if (creationList.isEmpty())
		{
			player.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
			return;
		}
		
		final Map<L2ItemInstance, Long> extractedItems = new HashMap<>();
		for (RestorationItemHolder createdItem : creationList)
		{
			if ((createdItem.getId() <= 0) || (createdItem.getCount() <= 0))
			{
				continue;
			}
			
			long itemCount = (long) (createdItem.getCount() * Config.RATE_EXTRACTABLE);
			final L2ItemInstance newItem = player.addItem("Extract", createdItem.getId(), itemCount, effector, false);
			
			if (createdItem.getMaxEnchant() > 0)
			{
				newItem.setEnchantLevel(Rnd.get(createdItem.getMinEnchant(), createdItem.getMaxEnchant()));
			}
			
			if (extractedItems.get(newItem) != null)
			{
				extractedItems.put(newItem, extractedItems.get(newItem) + itemCount);
			}
			else
			{
				extractedItems.put(newItem, itemCount);
			}
		}
		
		if (!extractedItems.isEmpty())
		{
			final InventoryUpdate playerIU = new InventoryUpdate();
			for (Entry<L2ItemInstance, Long> entry : extractedItems.entrySet())
			{
				playerIU.addModifiedItem(entry.getKey());
				sendMessage(player, entry.getKey(), entry.getValue());
			}
			player.sendPacket(playerIU);
		}
	}
	
	@Override
	public L2EffectType getEffectType()
	{
		return L2EffectType.EXTRACT_ITEM;
	}
	
	private void sendMessage(L2PcInstance player, L2ItemInstance item, Long count)
	{
		final SystemMessage sm;
		if (count > 1)
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S2_S1);
			sm.addItemName(item);
			sm.addLong(count);
		}
		else if (item.getEnchantLevel() > 0)
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_A_S1_S2);
			sm.addInt(item.getEnchantLevel());
			sm.addItemName(item);
		}
		else
		{
			sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1);
			sm.addItemName(item);
		}
		player.sendPacket(sm);
	}
}
