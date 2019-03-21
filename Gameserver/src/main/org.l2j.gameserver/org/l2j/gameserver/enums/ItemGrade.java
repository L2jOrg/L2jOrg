package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.items.type.CrystalType;

/**
 * @author UnAfraid
 */
public enum ItemGrade {
    NONE,
    D,
    C,
    B,
    A,
    S,
    R;

    public static ItemGrade valueOf(CrystalType type) {
        switch (type) {
            case NONE: {
                return NONE;
            }
            case D: {
                return D;
            }
            case C: {
                return C;
            }
            case B: {
                return B;
            }
            case A: {
                return A;
            }
            case S:
            case S80:
            case S84: {
                return S;
            }
            case R:{
                return R;
            }
        }
        return null;
    }
}
