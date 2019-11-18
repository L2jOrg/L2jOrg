package org.l2j.gameserver.model.items.enchant;

import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.model.items.type.EtcItemType;
import org.l2j.gameserver.model.items.type.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author UnAfraid
 */
public abstract class AbstractEnchantItem {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractEnchantItem.class);

    private static final ItemType[] ENCHANT_TYPES = new ItemType[]
            {
                    EtcItemType.ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_AM,
                    EtcItemType.ENCHT_ATTR_ANCIENT_CRYSTAL_ENCHANT_WP,
                    EtcItemType.BLESS_ENCHT_AM,
                    EtcItemType.BLESS_ENCHT_WP,
                    EtcItemType.ENCHANT_ARMOR,
                    EtcItemType.ENCHANT_WEAPON,
                    EtcItemType.BLESSED_ENCHANT_ARMOR,
                    EtcItemType.BLESSED_ENCHANT_WEAPON,
                    EtcItemType.ENCHT_ATTR_INC_PROP_ENCHT_AM,
                    EtcItemType.ENCHT_ATTR_INC_PROP_ENCHT_WP,
                    EtcItemType.GIANT_ENCHT_ATTR_INC_PROP_ENCHT_AM,
                    EtcItemType.GIANT_ENCHT_ATTR_INC_PROP_ENCHT_WP,
                    EtcItemType.BLESSED_ENCHT_ATTR_INC_PROP_ENCHT_AM,
                    EtcItemType.BLESSED_ENCHT_ATTR_INC_PROP_ENCHT_WP,
                    EtcItemType.SOLID_ENCHANT_WEAPON,
                    EtcItemType.SOLID_ENCHANT_ARMOR,
            };

    private final int _id;
    private final CrystalType _grade;
    private final int _maxEnchantLevel;
    private final double _bonusRate;

    public AbstractEnchantItem(StatsSet set) {
        _id = set.getInt("id");
        if (getItem() == null) {
            throw new NullPointerException();
        } else if (!CommonUtil.contains(ENCHANT_TYPES, getItem().getItemType())) {
            throw new IllegalAccessError();
        }
        _grade = set.getEnum("targetGrade", CrystalType.class, CrystalType.NONE);
        _maxEnchantLevel = set.getInt("maxEnchant", 127);
        _bonusRate = set.getDouble("bonusRate", 0);
    }

    /**
     * @return id of current item
     */
    public final int getId() {
        return _id;
    }

    /**
     * @return bonus chance that would be added
     */
    public final double getBonusRate() {
        return _bonusRate;
    }

    /**
     * @return {@link ItemTemplate} current item/scroll
     */
    public final ItemTemplate getItem() {
        return ItemTable.getInstance().getTemplate(_id);
    }

    /**
     * @return grade of the item/scroll.
     */
    public final CrystalType getGrade() {
        return _grade;
    }

    /**
     * @return {@code true} if scroll is for weapon, {@code false} for armor
     */
    public abstract boolean isWeapon();

    /**
     * @return the maximum enchant level that this scroll/item can be used with
     */
    public int getMaxEnchantLevel() {
        return _maxEnchantLevel;
    }

    /**
     * @param itemToEnchant the item to be enchanted
     * @param supportItem
     * @return {@code true} if this support item can be used with the item to be enchanted, {@code false} otherwise
     */
    public boolean isValid(Item itemToEnchant, EnchantSupportItem supportItem) {
        if (itemToEnchant == null) {
            return false;
        } else if (!itemToEnchant.isEnchantable()) {
            return false;
        } else if (!isValidItemType(itemToEnchant.getTemplate().getType2())) {
            return false;
        } else if ((_maxEnchantLevel != 0) && (itemToEnchant.getEnchantLevel() >= _maxEnchantLevel)) {
            return false;
        } else if (_grade != itemToEnchant.getTemplate().getCrystalType()) {
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
