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

public final class ShortCutRegister extends ServerPacket {
    private final Shortcut shortcut;

    public ShortCutRegister(Shortcut shortcut) {
        this.shortcut = shortcut;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.SHORTCUT_REG, buffer );

        buffer.writeInt(shortcut.getType().ordinal());
        buffer.writeInt(shortcut.getClientId());
        buffer.writeByte(shortcut.isActive());
        switch (shortcut.getType()) {
            case ITEM -> writeShortcutItem(buffer);
            case SKILL -> writeShortcutSkill(buffer);
            case ACTION, MACRO, RECIPE, BOOKMARK -> {
                buffer.writeInt(shortcut.getShortcutId());
                buffer.writeInt(shortcut.getCharacterType());
            }
        }
    }

    private void writeShortcutSkill(WritableBuffer buffer) {
        buffer.writeInt(shortcut.getShortcutId());
        buffer.writeShort(shortcut.getLevel());
        buffer.writeShort(shortcut.getSubLevel());
        buffer.writeInt(shortcut.getSharedReuseGroup());
        buffer.writeByte(0x00); // C5
        buffer.writeInt(shortcut.getCharacterType());
        buffer.writeInt(0x00); // TODO: Find me
        buffer.writeInt(0x00); // TODO: Find me
    }

    private void writeShortcutItem(WritableBuffer buffer) {
        buffer.writeInt(shortcut.getShortcutId());
        buffer.writeInt(shortcut.getCharacterType());
        buffer.writeInt(shortcut.getSharedReuseGroup());
        buffer.writeInt(0x00); // Remaining time
        buffer.writeInt(0x00); // Cool down time
        buffer.writeInt(0x00); // item augment effect 1
        buffer.writeInt(0x00); // item augment effect 2
        buffer.writeInt(0x00); // unk
    }

}
