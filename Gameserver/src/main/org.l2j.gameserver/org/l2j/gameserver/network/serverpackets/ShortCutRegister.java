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

public final class ShortCutRegister extends ServerPacket {
    private final Shortcut shortcut;

    public ShortCutRegister(Shortcut shortcut) {
        this.shortcut = shortcut;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHORTCUT_REG);

        writeInt(shortcut.getType().ordinal());
        writeInt(shortcut.getClientId()); // C4 Client
        writeByte(0);
        switch (shortcut.getType()) {
            case ITEM -> writeShortcutItem();
            case SKILL -> writeShortcutSkill();
            case ACTION, MACRO, RECIPE, BOOKMARK -> {
                writeInt(shortcut.getShortcutId());
                writeInt(shortcut.getCharacterType());
            }
        }
    }

    private void writeShortcutSkill() {
        writeInt(shortcut.getShortcutId());
        writeShort(shortcut.getLevel());
        writeShort(shortcut.getSubLevel());
        writeInt(shortcut.getSharedReuseGroup());
        writeByte(0x00); // C5
        writeInt(shortcut.getCharacterType());
        writeInt(0x00); // TODO: Find me
        writeInt(0x00); // TODO: Find me
    }

    private void writeShortcutItem() {
        writeInt(shortcut.getShortcutId());
        writeInt(shortcut.getCharacterType());
        writeInt(shortcut.getSharedReuseGroup());
        writeInt(0x00); // Remaining time
        writeInt(0x00); // Cool down time
        writeInt(0x00); // item augment effect 1
        writeInt(0x00); // item augment effect 2
        writeInt(0x00); // unk
    }

}
