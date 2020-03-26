package handlers.itemhandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class SoulShots extends AbstractShot {

    @Override
    protected boolean canUse(Player player) {
        if (isNull(player.getActiveWeaponInstance()) || !player.isAutoShotEnabled(ShotType.SOULSHOTS)) {
            player.sendPacket(SystemMessageId.CANNOT_USE_SOULSHOTS);
            return false;
        }
        return true;
    }

    @Override
    protected ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected boolean isBlessed() {
        return false;
    }

    @Override
    protected double getBonus(Player player) {
        return player.getStats().getValue(Stat.SOUL_SHOTS_BONUS, 1) * 2;
    }

    @Override
    protected SystemMessageId getEnabledShotsMessage() {
        return SystemMessageId.YOUR_SOULSHOTS_ARE_ENABLED;
    }
}
