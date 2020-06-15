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
package org.l2j.gameserver.model.item;

import io.github.joealisson.primitive.HashLongMap;
import io.github.joealisson.primitive.LongMap;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.item.instance.Item;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public enum BodyPart {
    GREAT_WOLF(-0x68, null),
    BABY_PET(-0x67, null),
    STRIDER(-0x66, null),
    HATCHLING(-0x65, null),
    WOLF(-0x64, null),
    NONE(0x00, null),
    PENDANT(0x01, InventorySlot.PENDANT),
    RIGHT_EAR(0x02, InventorySlot.RIGHT_EAR),
    LEFT_EAR(0x04, InventorySlot.LEFT_EAR),
    EAR(RIGHT_EAR.id | LEFT_EAR.id, InventorySlot.LEFT_EAR),
    NECK(0x08, InventorySlot.NECK),
    RIGHT_FINGER(0x10, InventorySlot.RIGHT_FINGER),
    LEFT_FINGER(0x20, InventorySlot.LEFT_FINGER),
    FINGER(RIGHT_FINGER.id | LEFT_FINGER.id, InventorySlot.LEFT_FINGER),
    HEAD(0x40, InventorySlot.HEAD),
    RIGHT_HAND(0x80, InventorySlot.RIGHT_HAND),
    LEFT_HAND(0x100, InventorySlot.LEFT_HAND),
    GLOVES(0x200, InventorySlot.GLOVES),
    CHEST(0x400, InventorySlot.CHEST),
    LEGS(0x800, InventorySlot.LEGS),
    FEET(0x1000, InventorySlot.FEET),
    BACK(0x2000, InventorySlot.CLOAK),
    TWO_HAND(0x4000, InventorySlot.TWO_HAND),
    FULL_ARMOR(0x8000, InventorySlot.CHEST),
    HAIR(0x10000,InventorySlot.HAIR),
    ALL_DRESS(0x20000, InventorySlot.CHEST),
    HAIR2(0x040000, InventorySlot.HAIR2),
    HAIR_ALL(0x80000, InventorySlot.HAIR),
    RIGHT_BRACELET(0x100000, InventorySlot.RIGHT_BRACELET),
    LEFT_BRACELET(0x200000, InventorySlot.LEFT_BRACELET),
    TALISMAN(0x400000, InventorySlot.TALISMAN1),
    BELT(0x10000000, InventorySlot.BELT),
    BROOCH(0x20000000, InventorySlot.BROOCH),
    BROOCH_JEWEL(0x40000000, InventorySlot.BROOCH_JEWEL1),
    AGATHION(0x3000000000L, InventorySlot.AGATHION1),
    ARTIFACT_BOOK(0x20000000000L, InventorySlot.ARTIFACT_BOOK),
    ARTIFACT(0x40000000000L, InventorySlot.ARTIFACT1)
    ;

    private static final LongMap<BodyPart> mapper = new HashLongMap<>();

    static {
        for (BodyPart value : values()) {
            mapper.put(value.id, value);
        }
    }

    private final long id;
    private final InventorySlot slot;

    BodyPart(long id, InventorySlot slot) {
        this.id = id;
        this.slot = slot;
    }

    public long getId() {
        return id;
    }

    public InventorySlot slot() {
        return slot;
    }

    public boolean isAnyOf(BodyPart... parts) {
        if(isNull(parts)) {
            return false;
        }

        for (BodyPart bodyPart : parts) {
            if(bodyPart == this) {
                return true;
            }
        }
        return false;
    }

    public static BodyPart fromSlot(long slot) {
        return mapper.getOrDefault(slot, NONE);
    }

    public static BodyPart fromEquippedPaperdoll(Item item) {
        InventorySlot slot;

        if(!item.isEquipped() || isNull(slot = InventorySlot.fromId(item.getLocationSlot()))) {
            return NONE;
        }

        return switch (item.getBodyPart()) {
            case EAR ->  slot == LEFT_EAR.slot ? LEFT_EAR : RIGHT_EAR;
            case FINGER -> slot == LEFT_FINGER.slot ? LEFT_FINGER : RIGHT_FINGER;
            default -> item.getBodyPart();
        };
    }
}
