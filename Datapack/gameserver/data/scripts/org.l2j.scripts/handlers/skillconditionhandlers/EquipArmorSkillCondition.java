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
package handlers.skillconditionhandlers;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.w3c.dom.Node;

import java.util.Arrays;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class EquipArmorSkillCondition implements SkillCondition {

	public int armorsMask;

	private EquipArmorSkillCondition(int mask) {
		armorsMask = mask;
	}

	@Override
	public boolean canUse(Creature caster, Skill skill, WorldObject target) {
		if (!isPlayer(caster)) {
			return false;
		}
		
		final Inventory inv = caster.getInventory();
		
		// Get the itemMask of the weared chest (if exists)
		final Item chest = inv.getPaperdollItem(InventorySlot.CHEST);
		if (isNull(chest)) {
			return false;
		}

		final int chestMask = chest.getTemplate().getItemMask();
		
		// If chest armor is different from the condition one return false
		if ((armorsMask & chestMask) == 0) {
			return false;
		}
		
		// So from here, chest armor matches conditions
		
		var chestBodyPart = chest.getBodyPart();
		// return True if chest armor is a Full Armor
		if (chestBodyPart == BodyPart.FULL_ARMOR) {
			return true;
		}
		// check legs armor
		final Item legs = inv.getPaperdollItem(InventorySlot.LEGS);
		if (isNull(legs)) {
			return false;
		}
		final int legMask = legs.getTemplate().getItemMask();
		// return true if legs armor matches too
		return (armorsMask & legMask) != 0;
	}

	public static final class Factory extends SkillConditionFactory {

		@Override
		public SkillCondition create(Node xmlNode) {
			int mask = Arrays.stream(parseString(xmlNode.getAttributes(), "type").split(Util.SPACE))
					.mapToInt(s -> ArmorType.valueOf(s).mask()).reduce(0, (a, b) -> a | b);
			return new EquipArmorSkillCondition(mask);
		}

		@Override
		public String conditionName() {
			return "armor";
		}
	}
}
