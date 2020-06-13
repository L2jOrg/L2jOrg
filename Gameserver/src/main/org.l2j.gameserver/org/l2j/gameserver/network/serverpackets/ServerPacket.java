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

import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.ServerPacketId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.gameserver.enums.InventorySlot.*;

/**
 * @author KenM
 * @author JoeAlisson
 */
public abstract class ServerPacket extends WritablePacket<GameClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPacket.class);

    private final InventorySlot[] PAPERDOLL_ORDER_AUGMENT = {
        RIGHT_HAND,
        LEFT_HAND,
        TWO_HAND
    };

    public InventorySlot[] getPaperdollOrder() {
        return InventorySlot.cachedValues();
    }

    public InventorySlot[] getPaperdollOrderAugument() {
        return PAPERDOLL_ORDER_AUGMENT;
    }

    /**
     * Sends this packet to the target player, useful for lambda operations like <br>
     * {@code World.getInstance().getPlayers().forEach(packet::sendTo)}
     *
     * @param player to send the packet
     */
    public void sendTo(Player player) {
        player.sendPacket(this);
    }

    @Override
    protected boolean write(GameClient client) {
        try {
            writeImpl(client);
            return true;
        } catch (Exception e) {
            LOGGER.error("[{}] Error writing packet {} to client {}", GameServer.fullVersion, this, client);
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    public void runImpl(Player player) {

    }

    public void writeOptionalD(int value) {
        if (value >= Short.MAX_VALUE) {
            writeShort(Short.MAX_VALUE);
            writeInt(value);
        } else {
            writeShort(value);
        }
    }

    protected void writeId(ServerPacketId packet) {
        writeByte(packet.getId());
    }

    protected void writeId(ServerExPacketId exPacket) {
        writeByte(0xFE);
        writeShort(exPacket.getId());
    }

    protected abstract void writeImpl(GameClient client) throws Exception;
}
