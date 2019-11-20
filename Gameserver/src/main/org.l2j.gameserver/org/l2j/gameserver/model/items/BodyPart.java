package org.l2j.gameserver.model.items;

import io.github.joealisson.primitive.HashLongMap;
import io.github.joealisson.primitive.LongMap;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public enum BodyPart {
    GREAT_WOLF(-0x68, -1),
    BABY_PET(-0x67, -1),
    STRIDER(-0x66, -1),
    HATCHLING(-0x65, -1),
    WOLF(-0x64, -1),
    NONE(0x00, -1),
    UNDERWEAR(0x01, 0),
    RIGHT_EAR(0x02, 8),
    LEFT_EAR(0x04, 9),
    EAR(RIGHT_EAR.id | LEFT_EAR.id, 9),
    NECK(0x08, 4),
    RIGHT_FINGER(0x10, 13),
    LEFT_FINGER(0x20, 14),
    FINGER(RIGHT_FINGER.id | LEFT_FINGER.id, 13),
    HEAD(0x40, 1),
    RIGHT_HAND(0x80, 5),
    LEFT_HAND(0x100, 7),
    GLOVES(0x200, 10),
    CHEST(0x400, 6),
    LEGS(0x800, 11),
    FEET(0x1000, 12),
    BACK(0x2000, 28),
    TWO_HAND(0x4000, 5),
    FULL_ARMOR(0x8000, 6),
    HAIR(0x10000,2 ),
    ALL_DRESS(0x20000, 6),
    HAIR2(0x040000, 3),
    HAIR_ALL(0x80000, 2),
    RIGHT_BRACELET(0x100000, 16),
    LEFT_BRACELET(0x200000, 15),
    TALISMAN(0x400000, 22),
    BELT(0x10000000, 29),
    BROOCH(0x20000000, 30),
    BROOCH_JEWEL(0x40000000, 31),
    AGATHION(0x3000000000L, 17),
    ARTIFACT_BOOK(0x20000000000L, 37),
    ARTIFACT(0x40000000000L, 38)
    ;

    static LongMap<BodyPart> mapper = new HashLongMap<>();

    static {
        for (BodyPart value : values()) {
            mapper.put(value.id, value);
        }
    }


    private final long id;
    private final int paperdool;

    BodyPart(long id, int paperdool) {
        this.id = id;
        this.paperdool = paperdool;
    }

    public long getId() {
        return id;
    }

    public int getPaperdool() {
        return paperdool;
    }

    public static BodyPart fromSlot(long slot) {
        return mapper.getOrDefault(slot, NONE);
    }

    public static int slotToPaperdool(long slot) {
        return fromSlot(slot).paperdool;
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
}
