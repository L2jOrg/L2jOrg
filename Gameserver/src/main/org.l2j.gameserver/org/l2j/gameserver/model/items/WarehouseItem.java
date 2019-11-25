package org.l2j.gameserver.model.items;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.model.items.type.ItemType;

import java.util.Collection;
import java.util.Objects;

/**
 * This class contains Item<BR>
 * Use to sort Item of :
 * <ul>
 * <li>Armor</li>
 * <li>EtcItem</li>
 * <li>Weapon</li>
 * </ul>
 *
 * @version $Revision: 1.7.2.2.2.5 $ $Date: 2005/04/06 18:25:18 $
 */
public class WarehouseItem {
    private final int[] attributeDefense = { 0, 0, 0, 0, 0, 0 };
    private final ItemTemplate template;
    private final int objectId;
    private final long count;
    private final int enchant;
    private final VariationInstance augmentation;
    private final int locationSlot;
    private final int type1;
    private final int type2;
    private final Collection<EnsoulOption> soulCrystalOptions;
    private final Collection<EnsoulOption> soulCrystalSpecialOptions;
    private final int[] enchantOptions;
    private byte elemAtkType;
    private int elemAtkPower;

    private final int time;

    private final int _owner;
    private final CrystalType _grade;

    public WarehouseItem(Item item) {
        Objects.requireNonNull(item);
        template = item.getTemplate();
        objectId = item.getObjectId();
        count = item.getCount();
        _owner = item.getOwnerId();
        locationSlot = item.getLocationSlot();
        enchant = item.getEnchantLevel();
        type1 = item.getCustomType1();
        type2 = item.getCustomType2();
        _grade = item.getTemplate().getCrystalType();
        augmentation = item.getAugmentation();
        time = item.isTimeLimitedItem() ? (int) (item.getRemainingTime() / 1000) : -1;

        elemAtkType = item.getAttackAttributeType().getClientId();
        elemAtkPower = item.getAttackAttributePower();
        for (AttributeType type : AttributeType.ATTRIBUTE_TYPES) {
            attributeDefense[type.getClientId()] = item.getDefenceAttribute(type);
        }
        enchantOptions = item.getEnchantOptions();
        soulCrystalOptions = item.getSpecialAbilities();
        soulCrystalSpecialOptions = item.getAdditionalSpecialAbilities();
    }

    /**
     * @return the item.
     */
    public ItemTemplate getItem() {
        return template;
    }

    /**
     * @return the unique objectId.
     */
    public final int getObjectId() {
        return objectId;
    }

    /**
     * @return the owner.
     */
    public final int getOwnerId() {
        return _owner;
    }

    /**
     * @return the location slot.
     */
    public final int getLocationSlot() {
        return locationSlot;
    }

    /**
     * @return the count.
     */
    public final long getCount() {
        return count;
    }

    /**
     * @return the second type.
     */
    public final int getType2() {
        return template.getType2();
    }

    /**
     * @return the second type.
     */
    public final ItemType getItemType() {
        return template.getItemType();
    }

    /**
     * @return the ItemId.
     */
    public final int getItemId() {
        return template.getId();
    }

    /**
     * @return the enchant level.
     */
    public final int getEnchantLevel() {
        return enchant;
    }

    /**
     * @return {@code true} if the item is a weapon, {@code false} otherwise.
     */
    public final boolean isWeapon() {
        return (template instanceof Weapon);
    }

    /**
     * @return {@code true} if the item is an armor, {@code false} otherwise.
     */
    public final boolean isArmor() {
        return (template instanceof Armor);
    }

    /**
     * @return the augmentation If.
     */
    public VariationInstance getAugmentation() {
        return augmentation;
    }

    /**
     * @return the name of the item
     */
    public String getName() {
        return template.getName();
    }

    public final int getCustomType1() {
        return type1;
    }

    public final int getCustomType2() {
        return type2;
    }

    public byte getAttackElementType() {
        return elemAtkType;
    }

    public int getAttackElementPower() {
        return elemAtkPower;
    }

    public int getElementDefAttr(byte i) {
        return attributeDefense[i];
    }

    public int[] getEnchantOptions() {
        return enchantOptions;
    }

    public Collection<EnsoulOption> getSoulCrystalOptions() {
        return soulCrystalOptions;
    }

    public Collection<EnsoulOption> getSoulCrystalSpecialOptions() {
        return soulCrystalSpecialOptions;
    }

    public int getTime() {
        return time;
    }

    /**
     * @return the name of the item
     */
    @Override
    public String toString() {
        return template.toString();
    }
}
