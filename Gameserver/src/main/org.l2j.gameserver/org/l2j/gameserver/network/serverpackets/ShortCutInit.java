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

import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public final class ShortCutInit extends ServerPacket {

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.INIT_SHORTCUT);

        var player = client.getPlayer();

        writeInt(player.getShortcutAmount());

        player.forEachShortcut(s -> {
            writeInt(s.getType().ordinal());
            writeInt(s.getClientId());
            writeByte(0x00); // 228

            switch (s.getType()) {
                case ITEM -> writeShortcutItem(s);
                case SKILL -> writeShortcutSkill(s);
                case ACTION, MACRO, RECIPE, BOOKMARK -> {
                    writeInt(s.getShortcutId());
                    writeInt(s.getCharacterType());
                }
            }
        });
    }

    private void writeShortcutSkill(Shortcut sc) {
        writeInt(sc.getShortcutId());
        writeShort(sc.getLevel());
        writeShort(sc.getSubLevel());
        writeInt(sc.getSharedReuseGroup());
        writeByte(0x00);
        writeInt(0x01);
    }

    private void writeShortcutItem(Shortcut sc) {
        writeInt(sc.getShortcutId());
        writeInt(0x01); // Enabled or not
        writeInt(sc.getSharedReuseGroup());
        writeInt(0x00);
        writeInt(0x00);
        writeInt(0x00); // Augment effect 1
        writeInt(0x00); // Augment effect 2
        writeInt(0x00); // Visual id
    }

}
