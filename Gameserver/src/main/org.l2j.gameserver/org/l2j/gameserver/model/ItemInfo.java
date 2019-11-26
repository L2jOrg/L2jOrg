package org.l2j.gameserver.model;

import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.buylist.Product;
import org.l2j.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Get all information from Item to generate ItemInfo.
 */
public class ItemInfo {
    private final int[] attributeDefense = { 0, 0, 0, 0, 0, 0 };
    private ItemTemplate template;
    private int objectId;
    private long count;
    private int enchant;
    private VariationInstance augmentation;
    private int locationSlot;
    private int type1;
    private int type2;
    private Collection<EnsoulOption> soulCrystalOptions;
    private Collection<EnsoulOption> soulCrystalSpecialOptions;
    private int[] enchantOption;
    private byte elemAtkType = -2;
    private int elemAtkPower = 0;

    private int time;

    private int price;

    /**
     * The action to do clientside (1=ADD, 2=MODIFY, 3=REMOVE)
     */
    private int _change;
    private boolean _available = true;
    private int _equipped;

    /**
     * Get all information from Item to generate ItemInfo.
     *
     * @param item
     */
    public ItemInfo(Item item) {
        Objects.requireNonNull(item);

        // Get the Identifier of the Item
        objectId = item.getObjectId();

        // Get the ItemTemplate of the Item
        template = item.getTemplate();

        // Get the enchant level of the Item
        enchant = item.getEnchantLevel();

        // Get the augmentation bonus
        augmentation = item.getAugmentation();

        // Get the quantity of the Item
        count = item.getCount();

        // Get custom item types (used loto, race tickets)
        type1 = item.getCustomType1();
        type2 = item.getCustomType2();

        // Verify if the Item is equipped
        _equipped = item.isEquipped() ? 1 : 0;

        // Get the action to do clientside
        switch (item.getLastChange()) {
            case Item.ADDED -> _change = 1;
            case Item.MODIFIED -> _change = 2;
            case Item.REMOVED -> _change = 3;
        }

        time = item.isTimeLimitedItem() ? (int) (item.getRemainingTime() / 1000) : -9999;
        _available = item.isAvailable();
        locationSlot = item.getLocationSlot();

        elemAtkType = item.getAttackAttributeType().getClientId();
        elemAtkPower = item.getAttackAttributePower();
        for (AttributeType type : AttributeType.ATTRIBUTE_TYPES) {
            attributeDefense[type.getClientId()] = item.getDefenceAttribute(type);
        }
        enchantOption = item.getEnchantOptions();
        soulCrystalOptions = item.getSpecialAbilities();
        soulCrystalSpecialOptions = item.getAdditionalSpecialAbilities();
    }

    public ItemInfo(Item item, int change) {
        this(item);
        _change = change;
    }

    public ItemInfo(TradeItem item) {
        if (item == null) {
            return;
        }

        // Get the Identifier of the Item
        objectId = item.getObjectId();

        // Get the ItemTemplate of the Item
        template = item.getItem();

        // Get the enchant level of the Item
        enchant = item.getEnchant();

        // Get the augmentation bonus
        if ((item.getAugmentationOption1() >= 0) && (item.getAugmentationOption2() >= 0)) {
            augmentation = new VariationInstance(0, item.getAugmentationOption1(), item.getAugmentationOption2());
        }

        // Get the quantity of the Item
        count = item.getCount();

        // Get custom item types (used loto, race tickets)
        type1 = item.getCustomType1();
        type2 = item.getCustomType2();

        // Verify if the Item is equipped
        _equipped = 0;

        // Get the action to do clientside
        _change = 0;

        time = -9999;

        locationSlot = item.getLocationSlot();

        elemAtkType = item.getAttackElementType();
        elemAtkPower = item.getAttackElementPower();
        for (byte i = 0; i < 6; i++) {
            attributeDefense[i] = item.getElementDefAttr(i);
        }

        enchantOption = item.getEnchantOptions();
        soulCrystalOptions = item.getSoulCrystalOptions();
        soulCrystalSpecialOptions = item.getSoulCrystalSpecialOptions();
    }

    public ItemInfo(Product item) {
        if (item == null) {
            return;
        }

        // Get the Identifier of the Item
        objectId = 0;

        // Get the ItemTemplate of the Item
        template = item.getTemplate();

        // Get the enchant level of the Item
        enchant = 0;

        // Get the augmentation bonus
        augmentation = null;

        // Get the quantity of the Item
        count = item.getCount();

        // Get custom item types (used loto, race tickets)
        type1 = template.getType1();
        type2 = template.getType2();

        // Verify if the Item is equipped
        _equipped = 0;

        // Get the action to do clientside
        _change = 0;

        time = -9999;

        locationSlot = 0;

        soulCrystalOptions = Collections.emptyList();
        soulCrystalSpecialOptions = Collections.emptyList();
    }

    public int getObjectId() {
        return objectId;
    }

    public ItemTemplate getTemplate() {
        return template;
    }

    public int getEnchantLevel() {
        return enchant;
    }

    public VariationInstance getAugmentation() {
        return augmentation;
    }

    public long getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }

    public int getCustomType1() {
        return type1;
    }

    public int getEquipped() {
        return _equipped;
    }

    public int getChange() {
        return _change;
    }

    public int getTime() {
        return time > 0 ? time : -9999;
    }

    public boolean isAvailable() {
        return _available;
    }

    public int getLocationSlot() {
        return locationSlot;
    }

    public int getAttackElementType() {
        return elemAtkType;
    }

    public int getAttackElementPower() {
        return elemAtkPower;
    }

    public int getAttributeDefence(AttributeType attribute) {
        return attributeDefense[attribute.getClientId()];
    }

    public int[] getEnchantOptions() {
        return enchantOption;
    }

    public Collection<EnsoulOption> getSoulCrystalOptions() {
        return soulCrystalOptions != null ? soulCrystalOptions : Collections.emptyList();
    }

    public Collection<EnsoulOption> getSoulCrystalSpecialOptions() {
        return soulCrystalSpecialOptions != null ? soulCrystalSpecialOptions : Collections.emptyList();
    }

    public int getId() {
        return template.getId();
    }

    @Override
    public String toString() {
        return template + "[objId: " + objectId + ", count: " + count + "]";
    }

    public BodyPart getBodyPart() {
        return template.getBodyPart();
    }

    public int getType2() {
        return template.getType2();
    }

    public int getDisplayId() {
        return template.getDisplayId();
    }

    public boolean isQuestItem() {
        return template.isQuestItem();
    }
}
