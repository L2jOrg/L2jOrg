package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.interfaces.ILocational;

import static org.l2j.gameserver.util.MathUtil.calculateHeadingFrom;

/**
 * @author Sdw
 */
public enum Position {
    FRONT,
    SIDE,
    BACK;

    /**
     * Position calculation based on the retail-like formulas:<br>
     * <ul>
     * <li>heading: (unsigned short) abs(heading - (unsigned short)(int)floor(atan2(toY - fromY, toX - fromX) * 65535.0 / 6.283185307179586))</li>
     * <li>side: if (heading >= 0x2000 && heading <= 0x6000 || (unsigned int)(heading - 0xA000) <= 0x4000)</li>
     * <li>front: else if ((unsigned int)(heading - 0x2000) <= 0xC000)</li>
     * <li>back: otherwise.</li>
     * </ul>
     *
     * @param from initial location
     * @param to final location
     * @return the position
     */
    public static Position getPosition(ILocational from, ILocational to) {
        final int heading = Math.abs(to.getHeading() - calculateHeadingFrom(from, to));
        if (((heading >= 0x2000) && (heading <= 0x6000)) || (Integer.toUnsignedLong(heading - 0xA000) <= 0x4000)) {
            return SIDE;
        } else if (Integer.toUnsignedLong(heading - 0x2000) <= 0xC000) {
            return FRONT;
        } else {
            return BACK;
        }
    }
}
