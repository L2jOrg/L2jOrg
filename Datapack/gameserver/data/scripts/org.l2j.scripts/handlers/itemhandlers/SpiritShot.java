package handlers.itemhandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class SpiritShot extends AbstractShot {

    @Override
    protected boolean canUse(Player player) {
        if (isNull(player.getActiveWeaponInstance()) || !player.isAutoShotEnabled(ShotType.SPIRITSHOTS)) {
            player.sendPacket(SystemMessageId.YOU_MAY_NOT_USE_SPIRITSHOTS);
            return false;
        }
        return true;
    }

    @Override
    protected ShotType getShotType() {
        return ShotType.SPIRITSHOTS;
    }

    @Override
    protected boolean isBlessed() {
        return false;
    }

    @Override
    protected double getBonus(Player player) {
        return player.getStats().getValue(Stat.SPIRIT_SHOTS_BONUS, 1) * 2;
    }

    @Override
    protected SystemMessageId getEnabledShotsMessage() {
        return SystemMessageId.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED;
    }
}
