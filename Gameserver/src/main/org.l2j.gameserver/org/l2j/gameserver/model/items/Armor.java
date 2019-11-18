package org.l2j.gameserver.model.items;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.items.type.ArmorType;

/**
 * This class is dedicated to the management of armors.
 */
public final class Armor extends ItemTemplate implements EquipableItem {
    private ArmorType type;

    /**
     * Constructor for Armor.
     *
     * @param set the StatsSet designating the set of couples (key,value) characterizing the armor.
     */
    public Armor(StatsSet set) {
        super(set);
    }

    public Armor(int id, String name, ArmorType type) {
        super(id, name);
        this.type = type;
    }

    @Override
    public void set(StatsSet set) {
        super.set(set);
        type = set.getEnum("armor_type", ArmorType.class, ArmorType.NONE);

        final long _bodyPart = getBodyPart().getId();
        if ((_bodyPart == SLOT_NECK) || ((_bodyPart & SLOT_L_EAR) != 0) || ((_bodyPart & SLOT_L_FINGER) != 0) || ((_bodyPart & SLOT_R_BRACELET) != 0) || ((_bodyPart & SLOT_L_BRACELET) != 0) || ((_bodyPart & SLOT_ARTIFACT_BOOK) != 0)) {
            _type1 = TYPE1_WEAPON_RING_EARRING_NECKLACE;
            _type2 = TYPE2_ACCESSORY;
        } else {
            if ((type == ArmorType.NONE) && (getBodyPart() == BodyPart.LEFT_HAND)) {
                type = ArmorType.SHIELD;
            }
            _type1 = TYPE1_SHIELD_ARMOR;
            _type2 = TYPE2_SHIELD_ARMOR;
        }
    }

    /**
     * @return the type of the armor.
     */
    @Override
    public ArmorType getItemType() {
        return type;
    }

    /**
     * @return the ID of the item after applying the mask.
     */
    @Override
    public final int getItemMask() {
        return type.mask();
    }

    public void setBodyPart(BodyPart bodyPart) {
        this.bodyPart = bodyPart;
    }

    public void setEnchantable(Boolean enchantable) {
        this.enchantable = enchantable;
    }

    public void setEquipReuseDelay(int equipReuseDelay) {
        this.equipReuseDelay = equipReuseDelay;
    }
}
