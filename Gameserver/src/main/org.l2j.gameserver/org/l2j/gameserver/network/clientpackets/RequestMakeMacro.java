package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.MacroType;
import org.l2j.gameserver.model.Macro;
import org.l2j.gameserver.model.MacroCmd;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class RequestMakeMacro extends IClientIncomingPacket {
    private static final int MAX_MACRO_LENGTH = 12;
    private Macro _macro;
    private int _commandsLenght = 0;

    @Override
    public void readImpl(ByteBuffer packet) {
        final int _id = packet.getInt();
        final String _name = readString(packet);
        final String _desc = readString(packet);
        final String _acronym = readString(packet);
        final int icon = packet.getInt();
        int count = packet.get();
        if (count > MAX_MACRO_LENGTH) {
            count = MAX_MACRO_LENGTH;
        }

        final List<MacroCmd> commands = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            final int entry = packet.get();
            final int type = packet.get(); // 1 = skill, 3 = action, 4 = shortcut
            final int d1 = packet.getInt(); // skill or page number for shortcuts
            final int d2 = packet.get();
            final String command = readString(packet);
            _commandsLenght += command.length();
            commands.add(new MacroCmd(entry, MacroType.values()[(type < 1) || (type > 6) ? 0 : type], d1, d2, command));
        }
        _macro = new Macro(_id, icon, _name, _desc, _acronym, commands);
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }
        if (_commandsLenght > 255) {
            // Invalid macro. Refer to the Help file for instructions.
            player.sendPacket(SystemMessageId.INVALID_MACRO_REFER_TO_THE_HELP_FILE_FOR_INSTRUCTIONS);
            return;
        }
        if (player.getMacros().getAllMacroses().size() > 48) {
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
