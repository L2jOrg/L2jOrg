package org.l2j.gameserver.mobius.gameserver.model;

import org.l2j.gameserver.mobius.gameserver.enums.AttributeType;
import org.l2j.gameserver.mobius.gameserver.model.buylist.Product;
import org.l2j.gameserver.mobius.gameserver.model.ensoul.EnsoulOption;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Item;
import org.l2j.gameserver.mobius.gameserver.model.items.L2WarehouseItem;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Get all information from L2ItemInstance to generate ItemInfo.
 */
public class ItemInfo
{
    /** Identifier of the L2ItemInstance */
    private int _objectId;

    /** The L2Item template of the L2ItemInstance */
    private L2Item _item;

    /** The level of enchant on the L2ItemInstance */
    private int _enchantLevel;

    /** The augmentation of the item */
    private VariationInstance _augmentation;

    /** The quantity of L2ItemInstance */
    private long _count;

    /** The price of the L2ItemInstance */
    private int _price;

    /** The custom L2ItemInstance types (used loto, race tickets) */
    private int _type1;
    private int _type2;

    /** If True the L2ItemInstance is equipped */
    private int _equipped;

    /** The action to do clientside (1=ADD, 2=MODIFY, 3=REMOVE) */
    private int _change;

    /** The mana of this item */
    private int _mana;
    private int _time;

    private boolean _available = true;

    private int _location;

    private byte _elemAtkType = -2;
    private int _elemAtkPower = 0;
    private final int[] _attributeDefence =
            {
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
            };

    private int[] _option;
    private Collection<EnsoulOption> _soulCrystalOptions;
    private Collection<EnsoulOption> _soulCrystalSpecialOptions;
    private int _visualId;
    private long _visualExpiration;

    /**
     * Get all information from L2ItemInstance to generate ItemInfo.
     * @param item
     */
    public ItemInfo(L2ItemInstance item)
    {
        Objects.requireNonNull(item);

        // Get the Identifier of the L2ItemInstance
        _objectId = item.getObjectId();

        // Get the L2Item of the L2ItemInstance
        _item = item.getItem();

        // Get the enchant level of the L2ItemInstance
        _enchantLevel = item.getEnchantLevel();

        // Get the augmentation bonus
        _augmentation = item.getAugmentation();

        // Get the quantity of the L2ItemInstance
        _count = item.getCount();

        // Get custom item types (used loto, race tickets)
        _type1 = item.getCustomType1();
        _type2 = item.getCustomType2();

        // Verify if the L2ItemInstance is equipped
        _equipped = item.isEquipped() ? 1 : 0;

        // Get the action to do clientside
        switch (item.getLastChange())
        {
            case L2ItemInstance.ADDED:
            {
                _change = 1;
                break;
            }
            case L2ItemInstance.MODIFIED:
            {
                _change = 2;
                break;
            }
            case L2ItemInstance.REMOVED:
            {
                _change = 3;
                break;
            }
        }

        // Get shadow item mana
        _mana = item.getMana();
        _time = item.isTimeLimitedItem() ? (int) (item.getRemainingTime() / 1000) : -9999;
        _available = item.isAvailable();
        _location = item.getLocationSlot();

        _elemAtkType = item.getAttackAttributeType().getClientId();
        _elemAtkPower = item.getAttackAttributePower();
        for (AttributeType type : AttributeType.ATTRIBUTE_TYPES)
        {
            _attributeDefence[type.getClientId()] = item.getDefenceAttribute(type);
        }
        _option = item.getEnchantOptions();
        _soulCrystalOptions = item.getSpecialAbilities();
        _soulCrystalSpecialOptions = item.getAdditionalSpecialAbilities();
        _visualId = item.getVisualId();
    }

    public ItemInfo(L2ItemInstance item, int change)
    {
        this(item);
        _change = change;
        _visualExpiration = item.getVisualLifeTime() > 0 ? (item.getVisualLifeTime() - System.currentTimeMillis()) / 1000 : 0;
    }

    public ItemInfo(TradeItem item)
    {
        if (item == null)
        {
            return;
        }

        // Get the Identifier of the L2ItemInstance
        _objectId = item.getObjectId();

        // Get the L2Item of the L2ItemInstance
        _item = item.getItem();

        // Get the enchant level of the L2ItemInstance
        _enchantLevel = item.getEnchant();

        // Get the augmentation bonus
        if ((item.getAugmentationOption1() >= 0) && (item.getAugmentationOption2() >= 0))
        {
            _augmentation = new VariationInstance(0, item.getAugmentationOption1(), item.getAugmentationOption2());
        }

        // Get the quantity of the L2ItemInstance
        _count = item.getCount();

        // Get custom item types (used loto, race tickets)
        _type1 = item.getCustomType1();
        _type2 = item.getCustomType2();

        // Verify if the L2ItemInstance is equipped
        _equipped = 0;

        // Get the action to do clientside
        _change = 0;

        // Get shadow item mana
        _mana = -1;
        _time = -9999;

        _location = item.getLocationSlot();

        _elemAtkType = item.getAttackElementType();
        _elemAtkPower = item.getAttackElementPower();
        for (byte i = 0; i < 6; i++)
        {
            _attributeDefence[i] = item.getElementDefAttr(i);
        }

        _option = item.getEnchantOptions();
        _soulCrystalOptions = item.getSoulCrystalOptions();
        _soulCrystalOptions = item.getSoulCrystalSpecialOptions();
        _visualId = item.getVisualId();
    }

    public ItemInfo(Product item)
    {
        if (item == null)
        {
            return;
        }

        // Get the Identifier of the L2ItemInstance
        _objectId = 0;

        // Get the L2Item of the L2ItemInstance
        _item = item.getItem();

        // Get the enchant level of the L2ItemInstance
        _enchantLevel = 0;

        // Get the augmentation bonus
        _augmentation = null;

        // Get the quantity of the L2ItemInstance
        _count = item.getCount();

        // Get custom item types (used loto, race tickets)
        _type1 = item.getItem().getType1();
        _type2 = item.getItem().getType2();

        // Verify if the L2ItemInstance is equipped
        _equipped = 0;

        // Get the action to do clientside
        _change = 0;

        // Get shadow item mana
        _mana = -1;
        _time = -9999;

        _location = 0;

        _soulCrystalOptions = Collections.emptyList();
        _soulCrystalSpecialOptions = Collections.emptyList();
    }

    public ItemInfo(L2WarehouseItem item)
    {
        if (item == null)
        {
            return;
        }

        // Get the Identifier of the L2ItemInstance
        _objectId = item.getObjectId();

        // Get the L2Item of the L2ItemInstance
        _item = item.getItem();

        // Get the enchant level of the L2ItemInstance
        _enchantLevel = item.getEnchantLevel();

        // Get the augmentation bonus
        _augmentation = item.getAugmentation();

        // Get the quantity of the L2ItemInstance
        _count = item.getCount();

        // Get custom item types (used loto, race tickets)
        _type1 = item.getCustomType1();
        _type2 = item.getCustomType2();

        // Verify if the L2ItemInstance is equipped
        _equipped = 0;

        // Get shadow item mana
        _mana = item.getMana();
        _time = item.getTime();
        _location = item.getLocationSlot();

        _elemAtkType = item.getAttackElementType();
        _elemAtkPower = item.getAttackElementPower();
        for (byte i = 0; i < 6; i++)
        {
            _attributeDefence[i] = item.getElementDefAttr(i);
        }
        _option = item.getEnchantOptions();
        _soulCrystalOptions = item.getSoulCrystalOptions();
        _soulCrystalOptions = item.getSoulCrystalSpecialOptions();
    }

    public int getObjectId()
    {
        return _objectId;
    }

    public L2Item getItem()
    {
        return _item;
    }

    public int getEnchantLevel()
    {
        return _enchantLevel;
    }

    public VariationInstance getAugmentation()
    {
        return _augmentation;
    }

    public long getCount()
    {
        return _count;
    }

    public int getPrice()
    {
        return _price;
    }

    public int getCustomType1()
    {
        return _type1;
    }

    public int getCustomType2()
    {
        return _type2;
    }

    public int getEquipped()
    {
        return _equipped;
    }

    public int getChange()
    {
        return _change;
    }

    public int getMana()
    {
        return _mana;
    }

    public int getTime()
    {
        return _time > 0 ? _time : _visualExpiration > 0 ? (int) _visualExpiration : -9999;
    }

    public boolean isAvailable()
    {
        return _available;
    }

    public int getLocation()
    {
        return _location;
    }

    public int getAttackElementType()
    {
        return _elemAtkType;
    }

    public int getAttackElementPower()
    {
        return _elemAtkPower;
    }

    public int getAttributeDefence(AttributeType attribute)
    {
        return _attributeDefence[attribute.getClientId()];
    }

    public int[] getEnchantOptions()
    {
        return _option;
    }

    public int getVisualId()
    {
        return _visualId;
    }

    public Collection<EnsoulOption> getSoulCrystalOptions()
    {
        return _soulCrystalOptions != null ? _soulCrystalOptions : Collections.emptyList();
    }

    public Collection<EnsoulOption> getSoulCrystalSpecialOptions()
    {
        return _soulCrystalSpecialOptions != null ? _soulCrystalSpecialOptions : Collections.emptyList();
    }

    public long getVisualExpiration()
    {
        return _visualExpiration;
    }

    @Override
    public String toString()
    {
        return String.valueOf(_item) + "[objId: " + _objectId + ", count: " + _count + "]";
    }
}
