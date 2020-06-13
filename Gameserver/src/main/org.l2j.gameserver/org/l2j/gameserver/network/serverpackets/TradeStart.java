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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public final class TradeStart extends AbstractItemPacket {

    private static final byte PARTNER_INFO = 1;
    private static final byte ITEMS_INFO = 2;

    private Player partner;
    private Collection<Item> items;
    private final int type;
    private int mask = 0;

    private TradeStart(int type, Player player, Player partner) {
        this.type = type;
        this.partner = partner;

        if (nonNull(partner)) {
            if(player.isFriend(partner)) {
                mask |= 0x01;
            }

            if(player.isInSameClan(partner)) {
                mask |= 0x02;
            }

            if(player.hasMentorRelationship(partner)) {
                mask |= 0x04;
            }

            if(player.isInSameAlly(partner)) {
                mask |= 0x08;

            }
            // Does not shows level
            if (partner.isGM()) {
                mask |= 0x10;
            }
        }
    }

    private TradeStart(byte type, Player player) {
        this.type = type;
        items = player.getInventory().getAvailableItems(true, player.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && Config.GM_TRADE_RESTRICTED_ITEMS, false);
    }

    @Override
    public void writeImpl(GameClient client) throws InvalidDataPacketException {
        if ((client.getPlayer().getActiveTradeList() == null)) {
            throw new InvalidDataPacketException();
        }

        writeId(ServerPacketId.TRADE_START);
        writeByte(type);
        if (type == ITEMS_INFO) {
            writeItems();
        } else {
            writePatner();
        }
    }

    private void writePatner() {
        writeInt(partner.getObjectId());
        writeByte(mask);
        if ((mask & 0x10) == 0) {
            writeByte(partner.getLevel());
        }
    }

    private void writeItems() {
        writeInt(items.size());
        writeInt(items.size());
        for (Item item : items) {
            writeItem(item);
        }
    }

    public static TradeStart partnerInfo(Player player, Player partner) {
        return new TradeStart(PARTNER_INFO, player, partner);
    }

    public static TradeStart itemsInfo(Player player) {
        return new TradeStart(ITEMS_INFO, player);
    }
}
