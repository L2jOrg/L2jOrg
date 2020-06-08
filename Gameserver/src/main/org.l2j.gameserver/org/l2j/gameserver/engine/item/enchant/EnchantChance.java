package org.l2j.gameserver.engine.item.enchant;

import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.model.item.instance.Item;

import java.util.EnumSet;

/**
 * @author JoeAlisson
 */
public record EnchantChance(RangedChanceGroup group, EnumSet<BodyPart>slots, Boolean magicWeapon) {

    public boolean isValid(Item item) {
        if(!slots.isEmpty() && !slots.contains(item.getBodyPart())) {
            return false;
        }
        if(magicWeapon != null && item.isMagicWeapon() != magicWeapon) {
            return false;
        }
        return group.isValid(item);
    }
}
