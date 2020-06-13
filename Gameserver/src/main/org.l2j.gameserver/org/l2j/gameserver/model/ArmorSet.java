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
package org.l2j.gameserver.model;

import io.github.joealisson.primitive.IntSet;
import io.github.joealisson.primitive.LinkedHashIntSet;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ArmorsetSkillHolder;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.stats.BaseStats;

import java.util.*;
import java.util.function.ToIntFunction;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.enums.InventorySlot.*;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class ArmorSet {

    private static final EnumSet<InventorySlot> ARTIFACT_1_SLOTS = EnumSet.of(ARTIFACT1, ARTIFACT2, ARTIFACT3, ARTIFACT4, ARTIFACT13, ARTIFACT16, ARTIFACT19);
    private static final EnumSet<InventorySlot> ARTIFACT_2_SLOTS = EnumSet.of(ARTIFACT5, ARTIFACT6, ARTIFACT7, ARTIFACT8, ARTIFACT14, ARTIFACT17, ARTIFACT20);
    private static final EnumSet<InventorySlot> ARTIFACT_3_SLOTS = EnumSet.of(ARTIFACT9, ARTIFACT10, ARTIFACT11, ARTIFACT12, ARTIFACT15, ARTIFACT18, ARTIFACT21);

    private final int id;
    private final int minimumPieces;
    private final boolean isVisual;
    private final IntSet requiredItems = new LinkedHashIntSet();
    private final IntSet optionalItems = new LinkedHashIntSet();
    private final List<ArmorsetSkillHolder> skills = new ArrayList<>();
    private final Map<BaseStats, Double> _stats = new LinkedHashMap<>();

    public ArmorSet(int id, int minimumPieces, boolean isVisual) {
        this.id = id;
        this.minimumPieces = minimumPieces;
        this.isVisual = isVisual;
    }

    public int getId() {
        return id;
    }

    /**
     * @return the minimum amount of pieces equipped to form a set
     */
    public int getMinimumPieces() {
        return minimumPieces;
    }

    /**
     * @return {@code true} if the set is visual only, {@code} otherwise
     */
    public boolean isVisual() {
        return isVisual;
    }

    /**
     * Adds an item to the set
     *
     * @param item
     * @return {@code true} if item was successfully added, {@code false} in case it already exists
     */
    public boolean addRequiredItem(int item) {
        return requiredItems.add(item);
    }

    /**
     * @return the set of items that can form a set
     */
    public IntSet getRequiredItems() {
        return requiredItems;
    }

    /**
     * Adds an shield to the set
     *
     * @param item
     * @return {@code true} if shield was successfully added, {@code false} in case it already exists
     */
    public boolean addOptionalItem(int item) {
        return optionalItems.add(item);
    }

    /**
     * @return the set of shields
     */
    public IntSet getOptionalItems() {
        return optionalItems;
    }

    /**
     * Adds an skill to the set
     *
     * @param holder
     */
    public void addSkill(ArmorsetSkillHolder holder) {
        skills.add(holder);
    }

    /**
     * The list of skills that are activated when set reaches it's minimal equipped items condition
     *
     * @return
     */
    public List<ArmorsetSkillHolder> getSkills() {
        return skills;
    }

    /**
     * Adds stats bonus to the set activated when set reaches it's minimal equipped items condition
     *
     * @param stat
     * @param value
     */
    public void addStatsBonus(BaseStats stat, double value) {
        _stats.putIfAbsent(stat, value);
    }

    /**
     * @param stat
     * @return the stats bonus value or 0 if doesn't exists
     */
    public double getStatsBonus(BaseStats stat) {
        return _stats.getOrDefault(stat, 0d);
    }

    /**
     * @param player
     * @return true if all parts of set are enchanted to +6 or more
     */
    public int getLowestSetEnchant(Player player) {
        if (getPiecesCount(player, Item::getId) < minimumPieces) {
            return 0;
        }

        final PlayerInventory inv = player.getInventory();
        int enchantLevel = Byte.MAX_VALUE;
        for (var armorSlot : InventorySlot.armorset()) {
            final Item itemPart = inv.getPaperdollItem(armorSlot);
            if ((itemPart != null) && requiredItems.contains(itemPart.getId())) {
                if (enchantLevel > itemPart.getEnchantLevel()) {
                    enchantLevel = itemPart.getEnchantLevel();
                }
            }
        }
        if (enchantLevel == Byte.MAX_VALUE) {
            enchantLevel = 0;
        }
        return enchantLevel;
    }

    /**
     * Condition for 3 Lv. Set Effect Applied Skill
     *
     * @param player
     * @param bookSlot
     * @return total paperdoll(busy) count for 1 of 3 artifact book slots
     */
    public int getArtifactSlotMask(Player player, int bookSlot) {
        int slotMask = 0;
        var slots = switch (bookSlot) {
            case 1 -> ARTIFACT_1_SLOTS;
            case 2 -> ARTIFACT_2_SLOTS;
            case 3 -> ARTIFACT_3_SLOTS;
            default -> null;
        };

        if(nonNull(slots)) {
            final PlayerInventory inv = player.getInventory();
            for (var slot : slots) {
                if(!inv.isPaperdollSlotEmpty(slot) && requiredItems.contains(inv.getPaperdollItemId(slot))) {
                    slotMask += slot.getMask();
                }
            }
        }
        return slotMask;
    }

    public boolean hasOptionalEquipped(Player player, ToIntFunction<Item> idProvider) {
        return player.getInventory().existsEquippedItem(item -> optionalItems.contains(idProvider.applyAsInt(item)));
    }

    /**
     * @param player
     * @param idProvider
     * @return the amount of set visual items that player has equipped
     */
    public int getPiecesCount(Player player, ToIntFunction<Item> idProvider) {
        return  player.getInventory().countEquippedItems(item -> requiredItems.contains(idProvider.applyAsInt(item)));
    }
}
