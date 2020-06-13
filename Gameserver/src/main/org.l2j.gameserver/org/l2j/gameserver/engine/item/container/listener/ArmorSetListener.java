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
package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.data.xml.impl.ArmorSetsData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.ArmorSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ArmorsetSkillHolder;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.skills.SkillConditionScope;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.ToIntFunction;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public final class ArmorSetListener implements PlayerInventoryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArmorSetListener.class);

    private ArmorSetListener() {
    }

    private static boolean applySkills(Player player, Item item, ArmorSet armorSet, ToIntFunction<Item> idProvider) {
        final long piecesCount = armorSet.getPiecesCount(player, idProvider);
        if (piecesCount >= armorSet.getMinimumPieces()) {
            // Applying all skills that matching the conditions
            boolean updateTimeStamp = false;
            boolean update = false;
            for (ArmorsetSkillHolder holder : armorSet.getSkills()) {
                if (holder.validateConditions(player, armorSet, idProvider)) {

                    if (player.getSkillLevel(holder.getSkillId()) >= holder.getLevel()) {
                        continue;
                    }

                    final Skill itemSkill = holder.getSkill();

                    if (itemSkill == null) {
                        LOGGER.warn("Inventory.ArmorSetListener.addSkills: Incorrect skill: " + holder);
                        continue;
                    }

                    if (itemSkill.isPassive() && !itemSkill.checkConditions(SkillConditionScope.PASSIVE, player, player))
                    {
                        continue;
                    }

                    player.addSkill(itemSkill, false);
                    if (itemSkill.isActive() && (item != null)) {
                        if (!player.hasSkillReuse(itemSkill.getReuseHashCode())) {
                            final int equipDelay = item.getEquipReuseDelay();
                            if (equipDelay > 0) {
                                player.addTimeStamp(itemSkill, equipDelay);
                                player.disableSkill(itemSkill, equipDelay);
                            }
                        }
                        updateTimeStamp = true;
                    }
                    update = true;
                }
            }
            if (updateTimeStamp) {
                player.sendPacket(new SkillCoolTime(player));
            }
            return update;
        }
        return false;
    }

    private static boolean verifyAndApply(Player player, Item item, ToIntFunction<Item> idProvider) {
        boolean update = false;
        final List<ArmorSet> armorSets = ArmorSetsData.getInstance().getSets(idProvider.applyAsInt(item));
        for (ArmorSet armorSet : armorSets) {
            if (applySkills(player, item, armorSet, idProvider)) {
                update = true;
            }
        }
        return update;
    }

    private static boolean verifyAndRemove(Player player, Item item, ToIntFunction<Item> idProvider) {
        boolean update = false;
        final List<ArmorSet> armorSets = ArmorSetsData.getInstance().getSets(idProvider.applyAsInt(item));
        for (ArmorSet armorSet : armorSets) {
            // Remove all skills that doesn't matches the conditions
            for (ArmorsetSkillHolder holder : armorSet.getSkills()) {
                if (!holder.validateConditions(player, armorSet, idProvider)) {
                    final Skill itemSkill = holder.getSkill();
                    if (itemSkill == null) {
                        LOGGER.warn("Inventory.ArmorSetListener.removeSkills: Incorrect skill: " + holder);
                        continue;
                    }

                    // Update if a skill has been removed.
                    if (player.removeSkill(itemSkill, false, itemSkill.isPassive()) != null) {
                        update = true;
                    }
                }
            }

            // Attempt to apply lower level skills if possible
            if (applySkills(player, item, armorSet, idProvider)) {
                update = true;
            }
        }

        return update;
    }

    @Override
    public void notifyEquiped(InventorySlot slot, Item item, Inventory inventory) {
        if (!isPlayer(inventory.getOwner())) {
            return;
        }

        final Player player = (Player) inventory.getOwner();
        boolean update = false;

        // Verify and apply normal set
        if (verifyAndApply(player, item, Item::getId)) {
            update = true;
        }

        if (update) {
            player.sendSkillList();
        }
    }

    @Override
    public void notifyUnequiped(InventorySlot slot, Item item, Inventory inventory) {
        if (!isPlayer(inventory.getOwner())) {
            return;
        }

        final Player player = (Player) inventory.getOwner();
        boolean remove = false;

        // verify and remove normal set bonus
        if (verifyAndRemove(player, item, Item::getId)) {
            remove = true;
        }

        if (remove) {
            player.checkItemRestriction();
            player.sendSkillList();
        }
    }

    public static ArmorSetListener provider() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ArmorSetListener INSTANCE = new ArmorSetListener();
    }
}