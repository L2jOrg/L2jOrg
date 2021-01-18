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

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.enums.MacroUpdateType;
import org.l2j.gameserver.model.Macro;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static java.util.Objects.nonNull;

public class SendMacroList extends ServerPacket {
    private final int count;
    private final Macro macro;
    private final MacroUpdateType updateType;

    public SendMacroList(int count, Macro macro, MacroUpdateType updateType) {
        this.count = count;
        this.macro = macro;
        this.updateType = updateType;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.MACRO_LIST, buffer );

        buffer.writeByte(updateType.getId());
        buffer.writeInt(updateType != MacroUpdateType.LIST ? macro.getId() : 0x00); // modified, created or deleted macro's id
        buffer.writeByte(count);
        buffer.writeByte(nonNull(macro)); // unknown

        if (nonNull(macro) && updateType != MacroUpdateType.DELETE) {
            buffer.writeInt(macro.getId());
            buffer.writeString(macro.getName());
            buffer.writeString(macro.getDescription());
            buffer.writeString(macro.getAcronym());
            buffer.writeInt(macro.getIcon());

            buffer.writeByte(macro.getCommands().size()); // count

            int i = 1;
            for (var cmd : macro.getCommands()) {
                buffer.writeByte( i++); // command count
                buffer.writeByte(cmd.getType().ordinal()); // type 1 = skill, 3 = action, 4 = shortcut
                buffer.writeInt(cmd.getData1()); // skill id
                buffer.writeByte(cmd.getData2()); // shortcut id
                buffer.writeString(cmd.getCommand()); // command name
            }
        }
    }
}
