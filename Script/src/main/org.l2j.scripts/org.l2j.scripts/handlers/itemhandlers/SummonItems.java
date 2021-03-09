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
package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.model.PetTemplate;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.PetItemRequest;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author HorridoJoho, UnAfraid
 */
public class SummonItems extends ItemSkillsTemplate
{
	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse)
	{
		if (!isPlayer(playable))
		{
			playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
			return false;
		}
		
		final Player player = playable.getActingPlayer();
		if (!player.getFloodProtectors().getItemPetSummon().tryPerformAction("summon items") || (player.getBlockCheckerArena() != -1) || player.isInObserverMode() || player.isAllSkillsDisabled() || player.isCastingNow())
		{
			return false;
		}
		
		if (player.isSitting())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_USE_ACTIONS_AND_SKILLS_WHILE_THE_CHARACTER_IS_SITTING);
			return false;
		}
		
		if (player.hasPet() || player.isMounted())
		{
			player.sendPacket(SystemMessageId.YOU_ALREADY_HAVE_A_PET);
			return false;
		}
		
		if (player.isAttackingNow())
		{
			player.sendPacket(SystemMessageId.YOU_CANNOT_SUMMON_DURING_COMBAT);
			return false;
		}
		
		final PetTemplate petTemplate = PetDataTable.getInstance().getPetDataByItemId(item.getId());
		if ((petTemplate == null) || (petTemplate.getNpcId() == -1))
		{
			return false;
		}
		
		player.addRequest(new PetItemRequest(player, item));
		return super.useItem(playable, item, forceUse);
	}
}
