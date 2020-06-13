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
package org.l2j.gameserver.network.clientpackets.commission;

import org.l2j.gameserver.instancemanager.CommissionManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.commission.ExCloseCommission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author NosBit
 */
public class RequestCommissionRegister extends ClientPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestCommissionRegister.class);

    private int _itemObjectId;
    private long _pricePerUnit;
    private long _itemCount;
    private int _durationType; // -1 = None, 0 = 1 Day, 1 = 3 Days, 2 = 5 Days, 3 = 7 Days

    @Override
    public void readImpl() {
        _itemObjectId = readInt();
        readString(); // Item Name they use it for search we will use server side available names.
        _pricePerUnit = readLong();
        _itemCount = readLong();
        _durationType = readInt();
        // readInt(); // Unknown
        // readInt(); // Unknown
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if ((_durationType < 0) || (_durationType > 3)) {
            LOGGER.warn("Player {} sent incorrect commission duration type: {}.", player, _durationType);
            return;
        }

        if (!CommissionManager.isPlayerAllowedToInteract(player)) {
            client.sendPacket(ExCloseCommission.STATIC_PACKET);
            return;
        }

        CommissionManager.getInstance().registerItem(player, _itemObjectId, _itemCount, _pricePerUnit, (byte) ((_durationType * 2) + 1));
    }
}
