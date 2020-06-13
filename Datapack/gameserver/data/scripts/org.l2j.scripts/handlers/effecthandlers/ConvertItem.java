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
package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.GameUtils;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.GameUtils.isWeapon;

/**
 * Convert Item effect implementation.
 * @author Zoey76
 * @author JoeAlisson
 */
public final class ConvertItem extends AbstractEffect {

	private ConvertItem() {
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {

		if (effected.isAlikeDead() || !isPlayer(effected)) {
			return;
		}
		
		final Player player = effected.getActingPlayer();

		if (player.hasItemRequest()) {
			return;
		}
		
		final Weapon weaponItem = player.getActiveWeaponItem();
		if (isNull(weaponItem)) {
			return;
		}
		
		Item wpn = player.getInventory().getPaperdollItem(InventorySlot.RIGHT_HAND);
		if (isNull(wpn)) {
			wpn = player.getInventory().getPaperdollItem(InventorySlot.LEFT_HAND);
		}
		
		if (!GameUtils.isWeapon(wpn) || wpn.isAugmented() || weaponItem.getChangeWeaponId() == 0) {
			return;
		}
		
		final int newItemId = weaponItem.getChangeWeaponId();
		if (newItemId == -1) {
			return;
		}
		
		final int enchantLevel = wpn.getEnchantLevel();
		final AttributeHolder elementals = isNull(wpn.getAttributes())  ? null : wpn.getAttackAttribute();
		var unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(wpn.getBodyPart());
		if (unequiped.size() <= 0) {
			return;
		}
		final InventoryUpdate iu = new InventoryUpdate();
		unequiped.forEach(iu::addModifiedItem);
		player.sendInventoryUpdate(iu);

		byte count = 0;
		for (Item unequippedItem : unequiped) {
			if (!isWeapon(unequippedItem)) {
				count++;
				continue;
			}
			
			final SystemMessage sm;
			if (unequippedItem.getEnchantLevel() > 0) {
				sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(unequippedItem.getEnchantLevel());
			} else {
				sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
			}
			player.sendPacket(sm.addItemName(unequippedItem));
		}
		
		if (count == unequiped.size()) {
			return;
		}
		
		final Item destroyItem = player.getInventory().destroyItem("ChangeWeapon", wpn, player, null);
		if (isNull(destroyItem)) {
			return;
		}
		
		final Item newItem = player.getInventory().addItem("ChangeWeapon", newItemId, 1, player, destroyItem);
		if (isNull(newItem)) {
			return;
		}
		
		if (nonNull(elementals)) {
			newItem.setAttribute(elementals, true);
		}
		newItem.setEnchantLevel(enchantLevel);
		player.getInventory().equipItem(newItem);
		
		final SystemMessage msg;

		if (newItem.getEnchantLevel() > 0) {
			msg = SystemMessage.getSystemMessage(SystemMessageId.EQUIPPED_S1_S2).addInt(newItem.getEnchantLevel());
		} else {
			msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
		}

		player.sendPacket(msg.addItemName(newItem));
		
		final InventoryUpdate u = new InventoryUpdate();
		u.addRemovedItem(destroyItem);
		u.addItem(newItem);
		player.sendInventoryUpdate(u);
		player.broadcastUserInfo();
	}

	public static class Factory implements SkillEffectFactory {
		private static final ConvertItem INSTANCE = new ConvertItem();

		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "ConvertItem";
		}
	}
}
