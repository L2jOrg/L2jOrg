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

import org.l2j.gameserver.enums.MacroType;
import org.l2j.gameserver.model.Macro;
import org.l2j.gameserver.model.MacroCmd;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

import java.util.ArrayList;
import java.util.List;

public final class RequestMakeMacro extends ClientPacket {
    private static final int MAX_MACRO_LENGTH = 12;
    private Macro _macro;
    private int _commandsLenght = 0;

    @Override
    public void readImpl() {
        final int _id = readInt();
        final String _name = readString();
        final String _desc = readString();
        final String _acronym = readString();
        final int icon = readInt();
        int count = readByte();
        if (count > MAX_MACRO_LENGTH) {
            count = MAX_MACRO_LENGTH;
        }

        final List<MacroCmd> commands = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final int entry = readByte();
            final int type = readByte(); // 1 = skill, 3 = action, 4 = shortcut
            final int d1 = readInt(); // skill or page number for shortcuts
            final int d2 = readByte();
            final String command = readString();
            _commandsLenght += command.length();
            commands.add(new MacroCmd(entry, MacroType.values()[(type < 1) || (type > 6) ? 0 : type], d1, d2, command));
        }
        _macro = new Macro(_id, icon, _name, _desc, _acronym, commands);
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }
        if (_commandsLenght > 255) {
            // Invalid macro. Refer to the Help file for instructions.
            player.sendPacket(SystemMessageId.INVALID_MACRO_REFER_TO_THE_HELP_FILE_FOR_INSTRUCTIONS);
            return;
        }
        if (player.getMacros().size() > 48) {
            // You may create up to 48 macros.
            player.sendPacket(SystemMessageId.YOU_MAY_CREATE_UP_TO_48_MACROS);
            return;
        }
        if (_macro.getName().isEmpty()) {
            // Enter the name of the macro.
            player.sendPacket(SystemMessageId.ENTER_THE_NAME_OF_THE_MACRO);
            return;
        }
        if (_macro.getDescr().length() > 32) {
            // Macro descriptions may contain up to 32 characters.
            player.sendPacket(SystemMessageId.MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS);
            return;
        }
        player.registerMacro(_macro);
    }
}
