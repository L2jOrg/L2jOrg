package org.l2j.gameserver.model.items.enchant;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.model.items.type.EtcItemType;
import org.l2j.gameserver.model.items.type.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.requireNonNull;

/**
 * @author UnAfraid
 */
public abstract class AbstractEnchantItem {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractEnchantItem.class);

    private static final ItemType[] ENCHANT_TYPES = new ItemType[]{
        EtcItemType.ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_AM,
        EtcItemType.ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WP,
        EtcItemType.BLESS_ENCHT_AM,
        EtcItemType.BLESS_ENCHT_WP,
        EtcItemType.ENCHANT_ARMOR,
        EtcItemType.ENCHANT_WEAPON,
        EtcItemType.BLESSED_ENCHANT_ARMOR,
        EtcItemType.BLESSED_ENCHANT_WEAPON,
        EtcItemType.IMPROVED_ENCHANT_ARMOR,
        EtcItemType.IMPROVED_ENCHANT_WEAPON,
        EtcItemType.ENCHT_ATTR_INC_PROP_ENCHT_AM,
        EtcItemType.ENCHT_ATTR_INC_PROP_ENCHT_WP,
        EtcItemType.GIANT_ENCHT_ATTR_INC_PROP_ENCHT_AM,
        EtcItemType.GIANT_ENCHT_ATTR_INC_PROP_ENCHT_WP,
        EtcItemType.BLESSED_ENCHT_ATTR_INC_PROP_ENCHT_AM,
        EtcItemType.BLESSED_ENCHT_ATTR_INC_PROP_ENCHT_WP,
        EtcItemType.SOLID_ENCHANT_WEAPON,
        EtcItemType.SOLID_ENCHANT_ARMOR,
    };

    private final int id;
    private final CrystalType grade;
    private final int maxEnchant;
    private final double bonusRate;

    public AbstractEnchantItem(int id, CrystalType grade, int maxEnchant) {
        this.id = id;
        var item = getItem();
        requireNonNull(item, "The id attribute should refer to an existent item.");

        if(!CommonUtil.contains(ENCHANT_TYPES, item.getItemType())) {
            throw new IllegalArgumentException("the item referenced by the id is not a enchant scroll");
        }

        this.grade = grade;
        this.maxEnchant = maxEnchant;
        bonusRate = 0;
    }

    /**
     * @return id of current item
     */
    public final int getId() {
        return id;
    }

    /**
     * @return bonus chance that would be added
     */
    public final double getBonusRate() {
        return bonusRate;
    }

    /**
     * @return {@link ItemTemplate} current item/scroll
     */
    public final ItemTemplate getItem() {
        return ItemEngine.getInstance().getTemplate(id);
    }

    /**
     * @return grade of the item/scroll.
     */
    public final CrystalType getGrade() {
        return grade;
    }

    /**
     * @return {@code true} if scroll is for weapon, {@code false} for armor
     */
    public abstract boolean isWeapon();

    /**
     * @return the maximum enchant level that this scroll/item can be used with
     */
    public int getMaxEnchantLevel() {
        return maxEnchant;
    }

    /**
     * @param itemToEnchant the item to be enchanted
     * @return {@code true} if this support item can be used with the item to be enchanted, {@code false} otherwise
     */
    public boolean isValid(Item itemToEnchant) {
        if (itemToEnchant == null) {
            return false;
        } else if (!itemToEnchant.isEnchantable()) {
            return false;
        } else if (!isValidItemType(itemToEnchant.getTemplate().getType2())) {
            return false;
        } else if ((maxEnchant != 0) && (itemToEnchant.getEnchantLevel() >= maxEnchant)) {
            return false;
        } else if (grade != itemToEnchant.getTemplate().getCrystalType()) {
            return false;
        }
        return true;
    }

    /**
     * @param type2
     * @return {@code true} if current type2 is valid to be enchanted, {@code false} otherwise
     */
    private boolean isValidItemType(int type2) {
        if (type2 == ItemTemplate.TYPE2_WEAPON) {
            return isWeapon();
        } else if ((type2 == ItemTemplate.TYPE2_SHIELD_ARMOR) || (type2 == ItemTemplate.TYPE2_ACCESSORY)) {
            return !isWeapon();
        }
        return false;
    }
}
