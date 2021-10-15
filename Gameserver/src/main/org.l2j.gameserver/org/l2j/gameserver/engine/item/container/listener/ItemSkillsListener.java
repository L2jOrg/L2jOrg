/*
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
package org.l2j.gameserver.engine.item.container.listener;

import org.l2j.gameserver.api.item.PlayerInventoryListener;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemSkillInfo;
import org.l2j.gameserver.engine.item.ItemTemplate;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.skills.SkillConditionScope;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
public final class ItemSkillsListener implements PlayerInventoryListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ItemSkillsListener.class);

    private ItemSkillsListener() {
    }

    @Override
    public void notifyUnequipped(InventorySlot slot, Item item, Inventory inventory) {
        if (!isPlayer(inventory.getOwner())) {
            return;
        }

        final Player player = (Player) inventory.getOwner();
        final ItemTemplate it = item.getTemplate();
        final AtomicBoolean update = new AtomicBoolean();
        final AtomicBoolean updateTimestamp = new AtomicBoolean();

        item.removeAugmentationBonus(player);

        player.getStats().recalculateStats(true);

        it.forEachSkill(ItemSkillType.ON_ENCHANT, holder -> {
            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }

            player.removeSkill(holder.skill(), false, holder.skill().isPassive());
            update.compareAndSet(false, true);

        });

        item.clearEnchantStats();
        item.clearSpecialAbilities();
        item.clearBlessedOptions();

        it.forEachSkill(ItemSkillType.NORMAL, holder -> {
            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }

            final Skill Skill = holder.skill();

            if (Skill != null) {
                player.removeSkill(Skill, false, Skill.isPassive());
                update.compareAndSet(false, true);
            } else {
                LOGGER.warn("Incorrect skill: {}", holder);
            }
        });

        if (item.isArmor()) {
            for (var inventoryItem : inventory.getItems()) {
                if (!inventoryItem.isEquipped() || !inventoryItem.hasSkills(ItemSkillType.NORMAL) || inventoryItem.equals(item)) {
                    continue;
                }

                inventoryItem.getTemplate().forEachSkill(ItemSkillType.NORMAL, holder ->
                {
                    InventorySlot itmSlot = InventorySlot.fromId(inventoryItem.getLocationSlot());
                    if(verifySkillActiveIfAddtionalAgathion(itmSlot, holder)) {
                        return;
                    }

                    if (player.getSkillLevel(holder.skill().getId()) != 0) {
                        return;
                    }

                    final Skill skill = holder.skill();
                    applySkillOnPlayer(item, skill, player, update, updateTimestamp);
                });
            }
        }


        inventory.forEachEquippedItem(equipped -> equipped.forEachSkill(ItemSkillType.ON_ENCHANT, holder -> {
            if (equipped.getEnchantLevel() >= holder.value()) {
                final Skill skill = holder.skill();

                if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player)) {
                    player.removeSkill(holder.skill(), false, holder.skill().isPassive());
                    update.compareAndSet(false, true);
                }
            }
        }));

        // Must check for toggle skill item conditions.
        for (Skill skill : player.getAllSkills())
        {
            if (skill.isToggle() && player.isAffectedBySkill(skill.getId()) && !skill.checkConditions(SkillConditionScope.GENERAL, player, player))
            {
                player.stopSkillEffects(true, skill.getId());
                update.compareAndSet(false, true);
            }
        }

        // Apply skill, if weapon have "skills on unequip"
        it.forEachSkill(ItemSkillType.ON_UNEQUIP, holder -> {

            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }
            holder.skill().activateSkill(player, player);
        });

        if (update.get()) {
            player.sendSkillList();
        }
        if (updateTimestamp.get()) {
            player.sendPacket(new SkillCoolTime(player));
        }

        if (item.isWeapon()) {
            player.unchargeAllShots();
        }
    }

    private void applySkillOnPlayer(Item item, Skill skill, Player player, AtomicBoolean update, AtomicBoolean updateTimestamp) {
        player.addSkill(skill, false);

        if (skill.isActive()) {
            if (!player.hasSkillReuse(skill.getReuseHashCode())) {
                final int equipDelay = item.getEquipReuseDelay();
                if (equipDelay > 0) {
                    player.addTimeStamp(skill, equipDelay);
                    player.disableSkill(skill, equipDelay);
                }
                updateTimestamp.compareAndSet(false, true);
            }
        }
        update.compareAndSet(false, true);
    }

    @Override
    public void notifyEquipped(InventorySlot slot, Item item, Inventory inventory) {
        if (!isPlayer(inventory.getOwner())) {
            return;
        }

        final Player player = (Player) inventory.getOwner();

        final AtomicBoolean update = new AtomicBoolean();
        final AtomicBoolean updateTimestamp = new AtomicBoolean();

        item.applyAugmentationBonus(player);

        player.getStats().recalculateStats(true);

        item.getTemplate().forEachSkill(ItemSkillType.ON_ENCHANT, holder -> {

            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }

            if (player.getSkillLevel(holder.skill().getId()) >= holder.skill().getLevel()) {
                return;
            }

            // Add skills bestowed from +4 armor
            applyEnchantSkill(item, player, holder, update);
        });

        // Apply enchant stats
        item.applyEnchantStats();

        // Apply SA skill
        item.applySpecialAbilities();

        // Apply blessed options
        item.applyBlessedOptions(item.getEnchantLevel());

        item.getTemplate().forEachSkill(ItemSkillType.NORMAL, holder ->
        {

            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }

            if (player.getSkillLevel(holder.skill().getId()) >= holder.skill().getLevel()) {
                return;
            }

            final Skill skill = holder.skill();
            if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player))
            {
                return;
            }
            applySkillOnPlayer(item, skill, player, update, updateTimestamp);
        });

        inventory.forEachEquippedItem(equipped -> equipped.forEachSkill(ItemSkillType.ON_ENCHANT, holder -> applyEnchantSkill(equipped, player, holder, update)));

        // Apply skill, if weapon have "skills on equip"
        item.getTemplate().forEachSkill(ItemSkillType.ON_EQUIP, holder -> {

            if(verifySkillActiveIfAddtionalAgathion(slot, holder)) {
                return;
            }

            holder.skill().activateSkill(player, player);
        });

        if (update.get()) {
            player.sendSkillList();
        }
        if (updateTimestamp.get()) {
            player.sendPacket(new SkillCoolTime(player));
        }
    }

    private void applyEnchantSkill(Item item, Player player, ItemSkillInfo holder, AtomicBoolean update) {
        if (player.getSkillLevel(holder.skill().getId()) >= holder.skill().getLevel()) {
            return;
        }

        if (item.getEnchantLevel() >= holder.value()) {
            final Skill skill = holder.skill();
            // Check passive skill conditions.
            if (skill.isPassive() && !skill.checkConditions(SkillConditionScope.PASSIVE, player, player)) {
                return;
            }
            player.addSkill(skill, false);
            update.compareAndSet(false, true);
        }
    }

    private boolean verifySkillActiveIfAddtionalAgathion(InventorySlot slot, ItemSkillInfo holder) {
        if(slot != InventorySlot.AGATHION1 &&  InventorySlot.agathions().contains(slot)) {
            var skill = holder.skill();
            return nonNull(skill) && skill.isActive();
        }
        return false;
    }

    public static ItemSkillsListener provider() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final ItemSkillsListener INSTANCE = new ItemSkillsListener();
    }
}
