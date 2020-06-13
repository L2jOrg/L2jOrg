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

import org.l2j.gameserver.engine.item.EnchantItemEngine;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPutEnchantTargetItemResult;

import static java.util.Objects.isNull;

/**
 * @author KenM
 * @author JoeAlisson
 */
public class RequestExTryToPutEnchantTargetItem extends ClientPacket {
    private int objectId;

    @Override
    public void readImpl() {
        objectId = readInt();
    }

    @Override
    public void runImpl() {
        final var player = client.getPlayer();
        final var request = player.getRequest(EnchantItemRequest.class);

        if (isNull(request) || request.isProcessing()) {
            return;
        }

        request.setEnchantingItem(objectId);

        final var item = request.getEnchantingItem();
        final var scroll = request.getEnchantingScroll();

        if (isNull(item) || isNull(scroll)) {
            return;
        }

        if (!EnchantItemEngine.getInstance().canEnchant(item, scroll)) {
            client.sendPacket(SystemMessageId.DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL);
            player.removeRequest(EnchantItemRequest.class);
            client.sendPacket(new ExPutEnchantTargetItemResult(0));
        } else {
            request.setTimestamp(System.currentTimeMillis());
            client.sendPacket(new ExPutEnchantTargetItemResult(objectId));
        }
    }
}
