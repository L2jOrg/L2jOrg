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
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.EnchantItemRequest;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPutEnchantScrollItemResult;

/**
 * @author Sdw
 */
public class RequestExAddEnchantScrollItem extends ClientPacket {
    private int _scrollObjectId;
    private int _enchantObjectId;

    @Override
    public void readImpl() {
        _scrollObjectId = readInt();
        _enchantObjectId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final EnchantItemRequest request = activeChar.getRequest(EnchantItemRequest.class);
        if ((request == null) || request.isProcessing()) {
            return;
        }

        request.setEnchantingItem(_enchantObjectId);
        request.setEnchantingScroll(_scrollObjectId);

        final Item item = request.getEnchantingItem();
        final Item scroll = request.getEnchantingScroll();
        if ((item == null) || (scroll == null)) {
            // message may be custom
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            activeChar.sendPacket(new ExPutEnchantScrollItemResult(0));
            request.setEnchantingItem(Player.ID_NONE);
            request.setEnchantingScroll(Player.ID_NONE);
            return;
        }

        if (!EnchantItemEngine.getInstance().existsScroll(scroll)) {
            activeChar.sendPacket(SystemMessageId.INAPPROPRIATE_ENCHANT_CONDITIONS);
            activeChar.sendPacket(new ExPutEnchantScrollItemResult(0));
            request.setEnchantingScroll(Player.ID_NONE);
            return;
        }

        request.setTimestamp(System.currentTimeMillis());
        activeChar.sendPacket(new ExPutEnchantScrollItemResult(_scrollObjectId));
    }
}
