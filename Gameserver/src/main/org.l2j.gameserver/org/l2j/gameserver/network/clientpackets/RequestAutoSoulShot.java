package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.ActionType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExAutoSoulShot;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * @author Unknown, UnAfraid
 * @author JoeAlisson
 */
public final class RequestAutoSoulShot extends ClientPacket {
    private int itemId;
    private boolean enable;
    private int type;

    @Override
    public void readImpl() {
        itemId = readInt();
        enable = readInt() == 1;
        type = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (isNull(player) || player.isDead() || nonNull(player.getActiveRequester()) || player.getPrivateStoreType() != PrivateStoreType.NONE) {
            return;
        }

        var item = player.getInventory().getItemByItemId(itemId);
        if (isNull(item)) {
            return;
        }

        if (enable) {
            if (!player.getInventory().canManipulate(item)) {
                player.sendPacket(getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                return;
            }

            if (isSummonShot(item.getTemplate())) {
                if (player.hasSummon()) {
                    rechargeSummonShots(player, item);
                } else {
                    client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR_AND_THEREFORE_CANNOT_USE_THE_AUTOMATIC_USE_FUNCTION);
                }
            } else if (isPlayerShot(item.getTemplate())) {
                final boolean isSoulshot = item.getEtcItem().getDefaultAction() == ActionType.SOULSHOT;
                final boolean isSpiritshot = item.getEtcItem().getDefaultAction() == ActionType.SPIRITSHOT;
                final boolean isFishingshot = item.getEtcItem().getDefaultAction() == ActionType.FISHINGSHOT;

                // Activate shots
                player.addAutoSoulShot(itemId);
                client.sendPacket(new ExAutoSoulShot(itemId, enable, type));

                // Send message
                final SystemMessage sm = getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED);
                sm.addItemName(item);
                client.sendPacket(sm);

                // Recharge player's shots
                player.rechargeShots(isSoulshot, isSpiritshot, isFishingshot);
            }
        } else {
            player.removeAutoSoulShot(itemId);
            client.sendPacket(new ExAutoSoulShot(itemId, enable, type));
            client.sendPacket(getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(item));
        }

    }

    private void rechargeSummonShots(Player player, Item item) {
        final boolean isSoulshot = item.getEtcItem().getDefaultAction() == ActionType.SUMMON_SOULSHOT;
        final boolean isSpiritshot = item.getEtcItem().getDefaultAction() == ActionType.SUMMON_SPIRITSHOT;
        if (isSoulshot) {
            int soulshotCount = 0;
            final Summon pet = player.getPet();
            if (pet != null) {
                soulshotCount += pet.getSoulShotsPerHit();
            }
            for (Summon servitor : player.getServitors().values()) {
                soulshotCount += servitor.getSoulShotsPerHit();
            }
            if (soulshotCount > item.getCount()) {
                client.sendPacket(SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_SERVITOR);
                return;
            }
        } else if (isSpiritshot) {
            int spiritshotCount = 0;
            final Summon pet = player.getPet();
            if (pet != null) {
                spiritshotCount += pet.getSpiritShotsPerHit();
            }
            for (Summon servitor : player.getServitors().values()) {
                spiritshotCount += servitor.getSpiritShotsPerHit();
            }
            if (spiritshotCount > item.getCount()) {
                client.sendPacket(SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_SERVITOR);
                return;
            }
        }

        // Activate shots
        player.addAutoSoulShot(itemId);
        client.sendPacket(new ExAutoSoulShot(itemId, enable, type));

        // Recharge summon's shots
        final Summon pet = player.getPet();
        if (pet != null) {
            // Send message
            if (!pet.isChargedShot(item.getTemplate().getDefaultAction() == ActionType.SUMMON_SOULSHOT ? ShotType.SOULSHOTS : ((item.getId() == 6647) || (item.getId() == 20334)) ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS)) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED);
                sm.addItemName(item);
                client.sendPacket(sm);
            }
            // Charge
            pet.rechargeShots(isSoulshot, isSpiritshot, false);
        }
        for (Summon summon : player.getServitors().values()) {
            // Send message
            if (!summon.isChargedShot(item.getTemplate().getDefaultAction() == ActionType.SUMMON_SOULSHOT ? ShotType.SOULSHOTS : ((item.getId() == 6647) || (item.getId() == 20334)) ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS)) {
                final SystemMessage sm = getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED);
                sm.addItemName(item);
                client.sendPacket(sm);
            }
            // Charge
            summon.rechargeShots(isSoulshot, isSpiritshot, false);
        }
    }

    private static boolean isPlayerShot(ItemTemplate item) {
        return switch (item.getDefaultAction()) {
            case SPIRITSHOT, SOULSHOT, FISHINGSHOT -> true;
            default -> false;
        };
    }

    private static boolean isSummonShot(ItemTemplate item) {
        return switch (item.getDefaultAction()) {
            case SUMMON_SPIRITSHOT, SUMMON_SOULSHOT -> true;
            default -> false;
        };
    }
}
