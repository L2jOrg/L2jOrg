package handlers.itemhandlers;

import org.l2j.gameserver.enums.ShotType;

/**
 * @author JoeAlisson
 */
public class BlessedSpiritShot extends SpiritShot {

    @Override
    protected ShotType getShotType() {
        return ShotType.BLESSED_SPIRITSHOTS;
    }
}
