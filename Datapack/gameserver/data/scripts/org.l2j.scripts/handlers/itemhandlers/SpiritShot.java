package handlers.itemhandlers;

import org.l2j.gameserver.enums.BroochJewel;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.Weapon;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class SpiritShot extends AbstractShot {

    @Override
    protected boolean canUse(Player player, Weapon weapon, int itemId) {
        if (isNull(player.getActiveWeaponInstance()) || weapon.getSpiritShotCount() == 0) {
            if (!player.getAutoSoulShot().contains(itemId)) {
                player.sendPacket(SystemMessageId.YOU_MAY_NOT_USE_SPIRITSHOTS);
            }
            return false;
        }
        return true;
    }

    @Override
    protected ShotType getShotType() {
        return ShotType.SPIRITSHOTS;
    }

    @Override
    protected int getConsumeCount(Weapon weapon) {
        return weapon.getSpiritShotCount();
    }

    @Override
    protected BroochJewel getModifyingJewel(Player player) {
        return player.getActiveShappireJewel();
    }

    @Override
    protected SystemMessageId getEnabledShotsMessage() {
        return SystemMessageId.YOUR_SPIRITSHOT_HAS_BEEN_ENABLED;
    }

    @Override
    protected SystemMessageId getNoEnoughShotsMessage() {
        return SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT;
    }
}
