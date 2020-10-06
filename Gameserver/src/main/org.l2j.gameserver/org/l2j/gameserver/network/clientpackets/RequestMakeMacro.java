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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.MacroCmdData;
import org.l2j.gameserver.data.database.data.MacroData;
import org.l2j.gameserver.enums.MacroType;
import org.l2j.gameserver.model.Macro;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

public final class RequestMakeMacro extends ClientPacket {
    private static final int MAX_MACRO_LENGTH = 12;
    private int commandsLenght = 0;
    private MacroData data;
    private List<MacroCmdData> commands;

    @Override
    public void readImpl() throws Exception {
        data = new MacroData();
        data.setId(readInt());
        data.setName(readString());
        if(data.getName().length() > 12) {
            throw new InvalidDataPacketException();
        }
        data.setDescription(readString());
        data.setAcronym(readString());
        if(data.getAcronym().length() > 4) {
            throw new InvalidDataPacketException();
        }
        data.setIcon(readInt());

        var count = Math.min(readByte(), MAX_MACRO_LENGTH);
        commands = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            MacroCmdData cmdData = new MacroCmdData();
            cmdData.setMacroId(data.getId());
            cmdData.setIndex(readByte());
            cmdData.setType(MacroType.from(readByte())); // 1 = skill, 3 = action, 4 = shortcut
            cmdData.setData1(readInt());  // skill or page number for shortcuts
            cmdData.setData2(readByte());
            cmdData.setCommand(readString());
            if(cmdData.getCommand().length() > 80) {
                throw new InvalidDataPacketException();
            }
            commandsLenght += cmdData.getCommand().length();
            commands.add(cmdData);
        }
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (isNull(player)) {
            return;
        }
        if (commandsLenght > 255) {
            player.sendPacket(SystemMessageId.INVALID_MACRO_REFER_TO_THE_HELP_FILE_FOR_INSTRUCTIONS);
            return;
        }
        if (player.getMacros().size() > 48) {
            player.sendPacket(SystemMessageId.YOU_MAY_CREATE_UP_TO_48_MACROS);
            return;
        }
        if (data.getName().isEmpty()) {
            player.sendPacket(SystemMessageId.ENTER_THE_NAME_OF_THE_MACRO);
            return;
        }
        if (data.getDescription().length() > 32) {
            player.sendPacket(SystemMessageId.MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS);
            return;
        }
        player.registerMacro(new Macro(data, commands));
    }
}
