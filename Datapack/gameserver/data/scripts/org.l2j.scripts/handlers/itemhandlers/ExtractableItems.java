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
package handlers.itemhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.l2j.gameserver.Config;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.L2ExtractableProduct;
import org.l2j.gameserver.model.actor.L2Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.L2EtcItem;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Extractable Items handler.
 * @author HorridoJoho, Mobius
 */
public class ExtractableItems implements IItemHandler
{
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final Player activeChar = playable.getActingPlayer();
		final L2EtcItem etcitem = (L2EtcItem) item.getItem();
		final List<L2ExtractableProduct> exitems = etcitem.getExtractableItems();
		if (exitems == null)
		{
			LOGGER.info("No extractable data defined for " + etcitem);
			return false;
		}
		
		if (!activeChar.isInventoryUnder80(false))
		{
			activeChar.sendPacket(SystemMessageId.EMPTY_463);
			return false;
		}
		
		// destroy item
		if (!activeChar.destroyItem("Extract", item.getObjectId(), 1, activeChar, true))
		{
			return false;
		}
		
		final Map<L2ItemInstance, Long> extractedItems = new HashMap<>();
		final List<L2ItemInstance> enchantedItems = new ArrayList<>();
		if (etcitem.getExtractableCountMin() > 0)
		{
			while (extractedItems.size() < etcitem.getExtractableCountMin())
			{
				for (L2ExtractableProduct expi : exitems)
				{
					if ((etcitem.getExtractableCountMax() > 0) && (extractedItems.size() == etcitem.getExtractableCountMax()))
					{
						break;
					}
					
					if (Rnd.get(100000) <= expi.getChance())
					{
						final int min = (int) (expi.getMin() * Config.RATE_EXTRACTABLE);
						final int max = (int) (expi.getMax() * Config.RATE_EXTRACTABLE);
						
						int createItemAmount = (max == min) ? min : (Rnd.get((max - min) + 1) + min);
						if (createItemAmount == 0)
						{
							continue;
						}
						
						// Do not extract the same item.
						boolean alreadyExtracted = false;
						for (L2ItemInstance i : extractedItems.keySet())
						{
							if (i.getItem().getId() == expi.getId())
							{
								alreadyExtracted = true;
								break;
							}
						}
						if (alreadyExtracted && (exitems.size() >= etcitem.getExtractableCountMax()))
						{
							continue;
						}
						
						if (ItemTable.getInstance().getTemplate(expi.getId()).isStackable() || (createItemAmount == 1))
						{
							final L2ItemInstance newItem = activeChar.addItem("Extract", expi.getId(), createItemAmount, activeChar, false);
							if (expi.getMaxEnchant() > 0)
							{
								newItem.setEnchantLevel(Rnd.get(expi.getMinEnchant(), expi.getMaxEnchant()));
								enchantedItems.add(newItem);
							}
							addItem(extractedItems, newItem);
						}
						else
						{
							while (createItemAmount > 0)
							{
								final L2ItemInstance newItem = activeChar.addItem("Extract", expi.getId(), 1, activeChar, false);
								if (expi.getMaxEnchant() > 0)
								{
									newItem.setEnchantLevel(Rnd.get(expi.getMinEnchant(), expi.getMaxEnchant()));
									enchantedItems.add(newItem);
								}
								addItem(extractedItems, newItem);
								createItemAmount--;
							}
						}
					}
				}
			}
		}
		else
		{
			for (L2ExtractableProduct expi : exitems)
			{
				if ((etcitem.getExtractableCountMax() > 0) && (extractedItems.size() == etcitem.getExtractableCountMax()))
				{
					break;
				}
				
				if (Rnd.get(100000) <= expi.getChance())
				{
					final int min = (int) (expi.getMin() * Config.RATE_EXTRACTABLE);
					final int max = (int) (expi.getMax() * Config.RATE_EXTRACTABLE);
					
					int createItemAmount = (max == min) ? min : (Rnd.get((max - min) + 1) + min);
					if (createItemAmount == 0)
					{
						continue;
					}
					
					if (ItemTable.getInstance().getTemplate(expi.getId()).isStackable() || (createItemAmount == 1))
					{
						final L2ItemInstance newItem = activeChar.addItem("Extract", expi.getId(), createItemAmount, activeChar, false);
						if (expi.getMaxEnchant() > 0)
						{
							newItem.setEnchantLevel(Rnd.get(expi.getMinEnchant(), expi.getMaxEnchant()));
							enchantedItems.add(newItem);
						}
						addItem(extractedItems, newItem);
					}
					else
					{
						while (createItemAmount > 0)
						{
							final L2ItemInstance newItem = activeChar.addItem("Extract", expi.getId(), 1, activeChar, false);
							if (expi.getMaxEnchant() > 0)
							{
								newItem.setEnchantLevel(Rnd.get(expi.getMinEnchant(), expi.getMaxEnchant()));
								enchantedItems.add(newItem);
							}
							addItem(extractedItems, newItem);
							createItemAmount--;
						}
					}
				}
			}
		}
		
		if (extractedItems.isEmpty())
		{
			activeChar.sendPacket(SystemMessageId.THERE_WAS_NOTHING_FOUND_INSIDE);
		}
		if (!enchantedItems.isEmpty())
		{
			final InventoryUpdate playerIU = new InventoryUpdate();
			for (L2ItemInstance i : enchantedItems)
			{
				playerIU.addModifiedItem(i);
			}
			activeChar.sendPacket(playerIU);
		}
		
		for (L2ItemInstance i : extractedItems.keySet())
		{
			sendMessage(activeChar, i, extractedItems.get(i));
		}
		
		return true;
	}
	
	private void addItem(Map<L2ItemInstance, Long> extractedItems, L2ItemInstance newItem)
	{
		if (extractedItems.get(newItem) != null)
		{
			extractedItems.put(newItem, extractedItems.get(newItem) + 1);
		}
		else
		{
			extractedItems.put(newItem, 1L);
		}
	}
	
	private void sendMessage(Player player, L2ItemInstance item, Long count)
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
