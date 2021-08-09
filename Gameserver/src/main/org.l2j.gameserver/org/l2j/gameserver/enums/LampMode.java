package org.l2j.gameserver.enums;

import java.util.Arrays;

public enum LampMode
{
    NORMAL,
    GREATER;

    public static LampMode getByMode(byte mode)
    {
        return Arrays.stream(values()).filter(type -> type.ordinal() == mode).findAny().orElse(NORMAL);
    }
}