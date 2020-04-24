package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public enum UserInfoType implements IUpdateTypeComponent {
    RELATION(0x00, 4),
    BASIC_INFO(0x01, 19),
    BASE_STATS(0x02, 18),
    MAX_HPCPMP(0x03, 14),
    CURRENT_HPMPCP_EXP_SP(0x04, 38),
    ENCHANTLEVEL(0x05, 4),
    APPAREANCE(0x06, 15),
    STATUS(0x07, 6),

    STATS(0x08, 64),
    ELEMENTALS(0x09, 14),
    POSITION(0x0A, 18),
    SPEED(0x0B, 18),
    MULTIPLIER(0x0C, 18),
    COL_RADIUS_HEIGHT(0x0D, 18),
    ATK_ELEMENTAL(0x0E, 5),
    CLAN(0x0F, 32),

    SOCIAL(0x10, 30),
    VITA_FAME(0x11, 19),
    SLOTS(0x12, 12),
    MOVEMENTS(0x13, 4),
    COLOR(0x14, 10),
    INVENTORY_LIMIT(0x15, 13),
    TRUE_HERO(0x16, 9),
    SPIRITS(0x17, 26),

    RANKER(0x18, 6),
    STATS_POINTS(0x19, 16),
    STATS_ABILITIES(0x1A, 18);

    private final int mask;
    private final int blockLength;

    UserInfoType(int mask, int blockLength) {
        this.mask = mask;
        this.blockLength = blockLength;
    }

    @Override
    public final int getMask() {
        return mask;
    }

    public int getBlockLength() {
        return blockLength;
    }
}