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

import org.l2j.gameserver.data.xml.impl.CategoryData;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.L2Playable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Mobius
 */
public class FatedSupportBox implements IItemHandler
{
	// Items
	private static final int FATED_BOX_FIGHTER = 37315;
	private static final int FATED_BOX_WIZARD = 37316;
	private static final int FATED_BOX_WARRIOR = 37317;
	private static final int FATED_BOX_ROGUE = 37318;
	private static final int FATED_BOX_ORC_FIGHTER = 37320;
	private static final int FATED_BOX_ORC_WIZARD = 37321;
	
	@Override
	public boolean useItem(L2Playable playable, L2ItemInstance item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final L2PcInstance player = playable.getActingPlayer();
		final Race race = player.getRace();
		final ClassId classId = player.getClassId();
		
		if (!player.isInventoryUnder80(false))
		{
			player.sendPacket(SystemMessageId.EMPTY_463);
			return false;
		}
		
		// Characters that have gone through their 2nd class transfer/1st liberation will be able to open the Fated Support Box at level 40.
		if ((player.getLevel() < 40) || player.isInCategory(CategoryType.FIRST_CLASS_GROUP) || (player.isInCategory(CategoryType.SECOND_CLASS_GROUP)))
		{
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item));
			return false;
		}
		
		player.getInventory().destroyItem(getClass().getSimpleName(), item, 1, player, null);
		player.sendPacket(new InventoryUpdate(item));
		
		// It will stay in your inventory after use until you reach level 84.
		if (player.getLevel() > 84)
		{
			player.sendMessage("Fated Support Box was removed because your level has exceeded the maximum requirement."); // custom message
			return true;
		}
		
		switch (race)
		{
			case HUMAN:
			case ELF:
			case DARK_ELF:
			case DWARF:
			{
				if (player.isMageClass())
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_WIZARD, 1, player, true);
				}
				else if (CategoryData.getInstance().isInCategory(CategoryType.SUB_GROUP_ROGUE, classId.getId()))
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_ROGUE, 1, player, true);
				}
				else if (CategoryData.getInstance().isInCategory(CategoryType.SUB_GROUP_KNIGHT, classId.getId()))
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_FIGHTER, 1, player, true);
				}
				else
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_WARRIOR, 1, player, true);
				}
				break;
			}
			case ORC:
			{
				if (player.isMageClass())
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_ORC_WIZARD, 1, player, true);
				}
				else
				{
					player.addItem(getClass().getSimpleName(), FATED_BOX_ORC_FIGHTER, 1, player, true);
				}
				break;
			}
		}
		return true;
	}
}
