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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;

import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * @author Sdw
 * @author JoeAlisson
 */
public class ExUserInfoEquipSlot extends AbstractMaskPacket<InventorySlot> {
    private final Player player;

    private final byte[] masks = new byte[] {
        0x00,
        0x00,
        0x00,
        0x00,
        0x00,
        0x00, // 152
        0x00, // 152
        0x00, // 152
    };

    public ExUserInfoEquipSlot(Player cha) {
        this(cha, true);
    }

    public ExUserInfoEquipSlot(Player cha, boolean addAll) {
        player = cha;

        if (addAll) {
            addComponentType(InventorySlot.values());
        }
    }

    @Override
    protected byte[] getMasks() {
        return masks;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_USER_INFO_EQUIPSLOT, buffer );

        buffer.writeInt(player.getObjectId());
        buffer.writeShort(InventorySlot.cachedValues().length); // 152
        buffer.writeBytes(masks);

        final PlayerInventory inventory = player.getInventory();
        for (var slot : getPaperdollOrder()) {
            if (containsMask(slot)) {
                final VariationInstance augment = inventory.getPaperdollAugmentation(slot);
                buffer.writeShort(22); // 10 + 4 * 3
                buffer.writeInt(inventory.getPaperdollObjectId(slot));
                buffer.writeInt(inventory.getPaperdollItemId(slot));
                buffer.writeInt(zeroIfNullOrElse(augment, VariationInstance::getOption1Id));
                buffer.writeInt(zeroIfNullOrElse(augment, VariationInstance::getOption2Id));
                buffer.writeInt(0x00); // Visual ID not used on classic
            }
        }
    }

}