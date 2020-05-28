package org.l2j.gameserver.model.item.enchant;

import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.item.EnchantItemGroupsData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.model.item.type.ItemType;

import static java.util.Objects.isNull;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class EnchantScroll extends AbstractEnchantItem {
    private final boolean isForWeapon;
    private final boolean isBlessed;
    private final boolean isSafe;
    private final boolean isGiant;
    private final int group;
    private IntSet items;

    public EnchantScroll(int id, CrystalType grade, int maxEnchant, int group) {
        super(id, grade, maxEnchant);
        this.group = group;

        final ItemType type = getItem().getItemType();
        isForWeapon = (type == EtcItemType.ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WP) || (type == EtcItemType.ENCHANT_WEAPON) || (type == EtcItemType.BLESSED_ENCHANT_WEAPON) || (type == EtcItemType.GIANT_ENCHT_WP) || (type == EtcItemType.IMPROVED_ENCHANT_WEAPON);
        isBlessed = (type == EtcItemType.BLESSED_ENCHANT_ARMOR) || (type == EtcItemType.BLESSED_ENCHANT_WEAPON);
        isSafe = (type == EtcItemType.ENCHT_ATTR_CRYSTAL_ENCHANT_AM) || (type == EtcItemType.ENCHT_ATTR_CRYSTAL_ENCHANT_WP) || (type == EtcItemType.IMPROVED_ENCHANT_ARMOR) || (type == EtcItemType.IMPROVED_ENCHANT_WEAPON);
        isGiant = (type == EtcItemType.GIANT_ENCHT_AM) || (type == EtcItemType.GIANT_ENCHT_WP);
    }

    @Override
    public boolean isForWeapon() {
        return isForWeapon;
    }

    /**
     * @return {@code true} for blessed scrolls (enchanted item will remain on failure), {@code false} otherwise
     */
    public boolean isBlessed() {
        return isBlessed;
    }

    /**
     * @return {@code true} for safe-enchant scrolls (enchant level will remain on failure), {@code false} otherwise
     */
    public boolean isSafe() {
        return isSafe;
    }

    public boolean isGiant() {
        return isGiant;
    }

    /**
     * Enforces current scroll to use only those items as possible items to enchant
     *
     * @param itemId
     */
    public void addItem(int itemId) {
        if (items == null) {
            items = new HashIntSet();
        }
        items.add(itemId);
    }

    /**
     * @param itemToEnchant the item to be enchanted
     * @return {@code true} if this scroll can be used with the specified support item and the item to be enchanted, {@code false} otherwise
     */
    @Override
    public boolean canEnchant(Item itemToEnchant) {
        return super.canEnchant(itemToEnchant) && (isNull(items) || items.contains(itemToEnchant.getId()));
    }

    /**
     * @param player
     * @param enchantItem
     * @return the chance of current scroll's group.
     */
    public double getChance(Player player, Item enchantItem) {
        if (isNull(EnchantItemGroupsData.getInstance().getScrollGroup(group))) {
            LOGGER.warn("Unexistent enchant scroll group specified for enchant scroll {}", getId());
            return -1;
        }

        final EnchantItemGroup group = EnchantItemGroupsData.getInstance().getItemGroup(enchantItem.getTemplate(), this.group);
        if (group == null) {
            LOGGER.warn("Couldn't find enchant item group for scroll: {} requested by {}", getId(), player);
            return -1;
        }
        return group.getChance(enchantItem.getEnchantLevel());
    }

    /**
     * @param player
     * @param enchantItem
     * @return the total chance for success rate of this scroll
     */
    public EnchantResultType calculateSuccess(Player player, Item enchantItem) {
        if (!canEnchant(enchantItem)) {
            return EnchantResultType.ERROR;
        }

        final double chance = getChance(player, enchantItem);
        if (chance == -1) {
            return EnchantResultType.ERROR;
        }

        final double bonusRate = getBonusRate() + player.getStats().getEnchantRateBonus();
        return Rnd.chance(chance + bonusRate) ? EnchantResultType.SUCCESS : EnchantResultType.FAILURE;
    }
}
