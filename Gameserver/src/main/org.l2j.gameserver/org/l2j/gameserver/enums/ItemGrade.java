package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.items.type.CrystalType;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public enum ItemGrade {
    NONE,
    D,
    C,
    B,
    A,
    S;

    public static ItemGrade valueOf(CrystalType type) {
        return  switch (type) {
            case D -> D;
            case C -> C;
            case B -> B;
            case A -> A;
            case S -> S;
            default -> NONE;
        };
    }
}
