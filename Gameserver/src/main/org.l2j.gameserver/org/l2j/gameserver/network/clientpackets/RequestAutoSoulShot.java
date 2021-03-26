/*
 * Copyright © 2019-2021 L2JOrg
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

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.network.InvalidDataPacketException;
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
    private static final int UPDATE_TYPE_DISABLE = 0;
    private static final int UPDATE_TYPE_AUTO = 1;
    
    private int itemId;
    private int updateType;
    private ShotType shotType;

    @Override
    public void readImpl() throws Exception {
        itemId = readInt();
        updateType = readInt();
        var type = readInt();

        shotType = ShotType.of(type);
        if(isNull(shotType)) {
            throw new InvalidDataPacketException("Unknown shotType " + type);
        }
    }

    @Override
    public void runImpl() {
        var player = client.getPlayer();
        if (player.isDead() || isNull(player.getActiveWeaponInstance()) || nonNull(player.getActiveRequester()) || player.getPrivateStoreType() != PrivateStoreType.NONE) {
            return;
        }

        if(updateType == UPDATE_TYPE_DISABLE) {
            player.disableAutoShot(shotType);
        } else {
            enableAutoShot(player);
        }
    }

    private void enableAutoShot(Player player) {
        var shot = player.getInventory().getItemByItemId(itemId);
        if (checkUseAutoShot(player, shot)) {
            player.enableAutoSoulShot(shotType, itemId);
        }
    }

    private boolean checkUseAutoShot(Player player, Item shot) {
        if (isNull(shot) || shot.getItemType() != EtcItemType.SOULSHOT) {
            return false;
        }

        if (player.getInventory().isBlocked(shot)) {
            player.sendPacket(getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
            return false;
        }

        if(isSummonShot(shot)) {
            if(!player.hasSummon()) {
                client.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_SERVITOR_AND_THEREFORE_CANNOT_USE_THE_AUTOMATIC_USE_FUNCTION);
                return false;
            } else if(!hasShotCount(player, shot)) {
                return false;
            }
        } else if(!isPlayerShot(shot)) {
            return false;
        }
        //@TEMP_FIX When using blessed soulshots the client doesn't update the count in the UI,
        // but in the inventory it is updated. as a work around we need to force a new ExAutoSoulShot
        return updateType != UPDATE_TYPE_AUTO || !player.tryEnableActualAutoShot(shotType);
    }

    private boolean hasShotCount(Player player, Item item) {
        var isSoulShot =  shotType == ShotType.BEAST_SOULSHOTS;
        var shotsCount = getSummonSoulShotCount(player, isSoulShot ? Summon::getSoulShotsPerHit : Summon::getSpiritShotsPerHit);
        if (shotsCount > item.getCount()) {
            var message = isSoulShot ? SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_SERVITOR : SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_SERVITOR;
            client.sendPacket(message);
            return false;
        }
        return true;
    }

    public int getSummonSoulShotCount(Player player, ToIntFunction<Summon> function) {
        return zeroIfNullOrElse(player.getPet(), function) +  player.getServitors().values().stream().mapToInt(function).sum();
    }

    private boolean isPlayerShot(Item item) {
        return switch (item.getAction()) {
            case SPIRITSHOT -> shotType == ShotType.SPIRITSHOTS;
            case SOULSHOT, FISHINGSHOT -> shotType == ShotType.SOULSHOTS;
            default -> false;
        };
    }

    private boolean isSummonShot(Item item) {
        return switch (item.getAction()) {
            case SUMMON_SOULSHOT -> shotType == ShotType.BEAST_SOULSHOTS;
            case SUMMON_SPIRITSHOT -> shotType == ShotType.BEAST_SPIRITSHOTS;
            default -> false;
        };
    }
}
