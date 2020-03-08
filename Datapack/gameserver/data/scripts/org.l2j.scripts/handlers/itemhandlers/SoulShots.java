package handlers.itemhandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.Weapon;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class SoulShots extends AbstractShot {

    @Override
    protected boolean canUse(Player player, Weapon weapon, int itemId) {
        if (isNull(player.getActiveWeaponInstance()) || weapon.getSoulShot() == 0) {
            if (!player.getAutoSoulShot().contains(itemId)) {
                player.sendPacket(SystemMessageId.CANNOT_USE_SOULSHOTS);
            }
            return false;
        }
        return true;
    }

    @Override
    protected ShotType getShotType() {
        return ShotType.SOULSHOTS;
    }

    @Override
    protected int getConsumeCount(Weapon weapon) {
        if (Rnd.chance(weapon.getReducedSoulShotChance())) {
            return weapon.getReducedSoulShot();
        }
        return weapon.getSoulShot();
    }

    @Override
    protected SystemMessageId getEnabledShotsMessage() {
        return SystemMessageId.YOUR_SOULSHOTS_ARE_ENABLED;
    }

    @Override
    protected SystemMessageId getNoEnoughShotsMessage() {
        return SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT;
    }
}
