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

import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.items.Weapon;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.ActionType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.Broadcast;

import java.util.List;

public class SpiritShot implements IItemHandler
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (!playable.isPlayer())
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final Player activeChar = playable.getActingPlayer();
		final Item weaponInst = activeChar.getActiveWeaponInstance();
		final Weapon weaponItem = activeChar.getActiveWeaponItem();
		final List<ItemSkillHolder> skills = item.getItem().getSkills(ItemSkillType.NORMAL);
		if (skills == null)
		{
			LOGGER.warn(": is missing skills!");
			return false;
		}
		
		final int itemId = item.getId();
		
		// Check if SpiritShot can be used
		if ((weaponInst == null) || (weaponItem.getSpiritShotCount() == 0))
		{
			if (!activeChar.getAutoSoulShot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_USE_SPIRITSHOTS);
			}
			return false;
		}
		
		// Check if SpiritShot is already active
		if (activeChar.isChargedShot(ShotType.SPIRITSHOTS))
		{
			return false;
		}
		
		// Check for correct grade
		final boolean gradeCheck = item.isEtcItem() && (item.getEtcItem().getDefaultAction() == ActionType.SPIRITSHOT) && (weaponInst.getItem().getCrystalType() == item.getItem().getCrystalType());
		
		if (!gradeCheck)
		{
			if (!activeChar.getAutoSoulShot().contains(itemId))
			{
				activeChar.sendPacket(SystemMessageId.YOUR_SPIRITSHOT_DOES_NOT_MATCH_THE_WEAPON_S_GRADE);
			}
			
			return false;
		}
		
		// Consume SpiritShot if player has enough of them
		if (!activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), weaponItem.getSpiritShotCount(), null, false))
		{
			if (!activeChar.disableAutoShot(itemId))
			{
				activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT);
			}
			return false;
		}
		
		// Charge Spirit shot
		activeChar.chargeShot(ShotType.SPIRITSHOTS);
		
		// Send message to client
		if (!activeChar.getAutoSoulShot().contains(item.getId()))
		{
			activeChar.sendPacket(SystemMessageId.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED);
		}
		
		// Visual effect change if player has equipped Sapphire lvl 3 or higher
		if (activeChar.getActiveShappireJewel() != null)
		{
			Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, activeChar, activeChar.getActiveShappireJewel().getEffectId(), 1, 0, 0), 600);
		}
		else
		{
			skills.forEach(holder -> Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, activeChar, holder.getSkillId(), holder.getSkillLevel(), 0, 0), 600));
			
		}
		return true;
	}
}
