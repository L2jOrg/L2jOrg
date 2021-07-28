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
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public final class ShortCutRegister extends AbstractShortcutPacket {
    private final Shortcut shortcut;

    public ShortCutRegister(Shortcut shortcut) {
        this.shortcut = shortcut;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.SHORTCUT_REG, buffer );
        writeShortcut(shortcut, buffer);
    }

    @Override
    protected void writeShortcutSkill(Shortcut shortcut, WritableBuffer buffer) {
        super.writeShortcutSkill(shortcut, buffer);
        buffer.writeInt(0x00); // TODO: Find me
        buffer.writeInt(0x00); // TODO: Find me
    }
}
