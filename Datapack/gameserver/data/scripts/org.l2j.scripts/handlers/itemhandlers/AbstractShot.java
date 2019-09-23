package handlers.itemhandlers;

import org.l2j.gameserver.enums.BroochJewel;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.Weapon;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.Broadcast;

import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
public abstract class AbstractShot implements IItemHandler {

    @Override
    public boolean useItem(Playable playable, Item item, boolean forceUse) {

        if (!isPlayer(playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }

        var player = playable.getActingPlayer();
        var  weaponItem = player.getActiveWeaponItem();
        var skills = item.getItem().getSkills(ItemSkillType.NORMAL);

        if (isNullOrEmpty(skills)) {
            LOGGER.warn("item {} is missing skills!", item);
            return false;
        }

        final int itemId = item.getId();

        // Check if Soul shot can be used
        if (!canUse(player, weaponItem, itemId)) {
            return false;
        }

        if (player.isChargedShot(getShotType())) {
            return false;
        }

        if (!player.destroyItemWithoutTrace("Consume", item.getObjectId(), getConsumeCount(weaponItem), null, false)) {
            if (!player.disableAutoShot(itemId)) {
                player.sendPacket(getNoEnoughShotsMessage());
            }
            return false;
        }
        // Charge soul shot
        player.chargeShot(getShotType());

        // Send message to client
        if (!player.getAutoSoulShot().contains(item.getId()))
        {
            player.sendPacket(getEnabledShotsMessage());
        }

        var jewel = getModifyingJewel(player);
        if (jewel != null){
            Broadcast.toSelfAndKnownPlayersInRadius(player, new MagicSkillUse(player, player, jewel.getEffectId(), 1, 0, 0), 600);
        } else {
            skills.forEach(holder -> Broadcast.toSelfAndKnownPlayersInRadius(player, new MagicSkillUse(player, player, holder.getSkillId(), holder.getSkillLevel(), 0, 0), 600));
        }
        return true;
    }

    protected abstract boolean canUse(Player player, Weapon weapon, int itemId);

    protected abstract ShotType getShotType();

    protected abstract int getConsumeCount(Weapon weapon);

    protected abstract BroochJewel getModifyingJewel(Player player);

    protected abstract SystemMessageId getEnabledShotsMessage();

    protected abstract SystemMessageId getNoEnoughShotsMessage();

}
