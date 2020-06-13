/*
 * Copyright © 2019-2020 L2JOrg
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
package handlers.itemhandlers;

import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.Broadcast;

import java.util.List;

import static java.util.Objects.isNull;

/**
 * @author -Nemesiss-
 * @author JoeAlisson
 */
public class FishShots implements IItemHandler {

	@Override
	public boolean useItem(Playable playable, Item item, boolean forceUse) {
		var player = playable.getActingPlayer();
		var weaponInst = player.getActiveWeaponInstance();
		
		if (isNull(weaponInst) || weaponInst.getItemType() != WeaponType.FISHING_ROD) {
			return false;
		}
		
		if (player.isChargedShot(ShotType.SOULSHOTS)) {
			return false;
		}

		if (item.getCount() < 1) {
			player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT);
			return false;
		}
		
		player.chargeShot(ShotType.SOULSHOTS, 1.5);
		player.destroyItemWithoutTrace("Consume", item.getObjectId(), 1, null, false);
		final WorldObject oldTarget = player.getTarget();
		player.setTarget(player);
		
		final List<ItemSkillHolder> skills = item.getSkills(ItemSkillType.NORMAL);
		
		if (skills == null) {
			LOGGER.warn("is missing skills!");
			return false;
		}
		
		skills.forEach(holder -> Broadcast.toSelfAndKnownPlayersInRadius(player, new MagicSkillUse(player, player, holder.getSkillId(), holder.getLevel(), 0, 0), 600));
		player.setTarget(oldTarget);
		return true;
	}
}
