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
import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public final class ShortCutInit extends ServerPacket {

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.INIT_SHORTCUT, buffer );

        var player = client.getPlayer();

        buffer.writeInt(player.getShortcutAmount());

        player.forEachShortcut(s -> {
            buffer.writeInt(s.getType().ordinal());
            buffer.writeInt(s.getClientId());
            buffer.writeByte(0x00); // 228

            switch (s.getType()) {
                case ITEM -> writeShortcutItem(s, buffer);
                case SKILL -> writeShortcutSkill(s, buffer);
                case ACTION, MACRO, RECIPE, BOOKMARK -> {
                    buffer.writeInt(s.getShortcutId());
                    buffer.writeInt(s.getCharacterType());
                }
            }
        });
    }

    private void writeShortcutSkill(Shortcut sc, WritableBuffer buffer) {
        buffer.writeInt(sc.getShortcutId());
        buffer.writeShort(sc.getLevel());
        buffer.writeShort(sc.getSubLevel());
        buffer.writeInt(sc.getSharedReuseGroup());
        buffer.writeByte(0x00);
        buffer.writeInt(0x01);
    }

    private void writeShortcutItem(Shortcut sc, WritableBuffer buffer) {
        buffer.writeInt(sc.getShortcutId());
        buffer.writeInt(0x01); // Enabled or not
        buffer.writeInt(sc.getSharedReuseGroup());
        buffer.writeInt(0x00);
        buffer.writeInt(0x00);
        buffer.writeInt(0x00); // Augment effect 1
        buffer.writeInt(0x00); // Augment effect 2
        buffer.writeInt(0x00); // Visual id
    }

}
