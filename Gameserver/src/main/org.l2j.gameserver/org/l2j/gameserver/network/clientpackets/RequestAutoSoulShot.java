/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.function.ToIntFunction;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;
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
        enable = readIntAsBoolean();
        type = readInt();
    }

    @Override
    public void runImpl() {
        var player = client.getPlayer();
        if (player.isDead() || nonNull(player.getActiveRequester()) || player.getPrivateStoreType() != PrivateStoreType.NONE) {
            return;
        }

        var item = player.getInventory().getItemByItemId(itemId);
        if (isNull(item) || item.getItemType() != EtcItemType.SOULSHOT) {
            return;
        }

        ShotType shotType = ShotType.of(type);
        if(isNull(shotType)) {
            return;
        }

        if (enable) {
            if (player.getInventory().isBlocked(item)) {
                player.sendPacket(getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
                return;
            }

            if (isSummonShot(item, shotType)) {
                if (player.hasSummon()) {
                    rechargeSummonShots(player, item, shotType);
                } else {
                    client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR_AND_THEREFORE_CANNOT_USE_THE_AUTOMATIC_USE_FUNCTION);
                }
            } else if (isPlayerShot(item, shotType)) {
                player.enableAutoSoulShot(shotType, itemId);
            }
        } else {
            player.disableAutoShot(shotType);
        }
    }

    private void rechargeSummonShots(Player player, Item item, ShotType shotType) {
        var isSoulShot =  shotType == ShotType.BEAST_SOULSHOTS;
        var shotsCount = getSummonSoulShotCount(player, isSoulShot ? Summon::getSoulShotsPerHit : Summon::getSpiritShotsPerHit);
        if (shotsCount > item.getCount()) {
            var message = isSoulShot ? SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_SERVITOR : SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_SERVITOR;
            client.sendPacket(message);
            return;
        }
        player.enableAutoSoulShot(shotType, itemId);
    }

    public int getSummonSoulShotCount(Player player, ToIntFunction<Summon> function) {
        return zeroIfNullOrElse(player.getPet(), function) +  player.getServitors().values().stream().mapToInt(function).sum();
    }


    private boolean isPlayerShot(Item item, ShotType type) {
        return switch (item.getAction()) {
            case SPIRITSHOT -> type == ShotType.SPIRITSHOTS;
            case SOULSHOT, FISHINGSHOT -> type == ShotType.SOULSHOTS;
            default -> false;
        };
    }

    private boolean isSummonShot(Item item, ShotType type) {
        return switch (item.getAction()) {
            case SUMMON_SOULSHOT -> type == ShotType.BEAST_SOULSHOTS;
            case SUMMON_SPIRITSHOT -> type == ShotType.BEAST_SPIRITSHOTS;
            default -> false;
        };
    }
}
