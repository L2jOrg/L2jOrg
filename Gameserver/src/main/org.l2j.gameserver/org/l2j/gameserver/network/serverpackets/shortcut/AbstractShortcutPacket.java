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
package org.l2j.gameserver.network.serverpackets.shortcut;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public abstract class AbstractShortcutPacket extends ServerPacket {

    protected void writeShortcut(Shortcut shortcut, WritableBuffer buffer) {
        buffer.writeInt(shortcut.getType().ordinal());
        buffer.writeInt(shortcut.getClientId());
        buffer.writeByte(shortcut.isActive());

        switch (shortcut.getType()) {
            case ITEM -> writeShortcutItem(shortcut, buffer);
            case SKILL -> writeShortcutSkill(shortcut, buffer);
            case ACTION, MACRO, RECIPE, BOOKMARK -> {
                buffer.writeInt(shortcut.getShortcutId());
                buffer.writeInt(shortcut.getCharacterType());
            }
        }
    }

    protected void writeShortcutSkill(Shortcut sc, WritableBuffer buffer) {
        buffer.writeInt(sc.getShortcutId());
        buffer.writeShort(sc.getLevel());
        buffer.writeShort(sc.getSubLevel());
        buffer.writeInt(sc.getSharedReuseGroup());
        buffer.writeByte(0x00);
        buffer.writeInt(sc.getCharacterType());
    }

    protected void writeShortcutItem(Shortcut sc, WritableBuffer buffer) {
        buffer.writeInt(sc.getShortcutId());
        buffer.writeInt(sc.getCharacterType());
        buffer.writeInt(sc.getSharedReuseGroup());
        buffer.writeInt(0x00); // Remaining time
        buffer.writeInt(0x00); // Cool down time
        buffer.writeInt(0x00); // item augment effect 1
        buffer.writeInt(0x00); // item augment effect 2
        buffer.writeInt(0x00); // visual Id
    }
}
