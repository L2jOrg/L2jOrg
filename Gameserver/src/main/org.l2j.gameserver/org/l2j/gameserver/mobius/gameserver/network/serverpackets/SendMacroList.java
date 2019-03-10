package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.enums.MacroUpdateType;
import org.l2j.gameserver.mobius.gameserver.model.Macro;
import org.l2j.gameserver.mobius.gameserver.model.MacroCmd;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class SendMacroList extends IClientOutgoingPacket {
    private final int _count;
    private final Macro _macro;
    private final MacroUpdateType _updateType;

    public SendMacroList(int count, Macro macro, MacroUpdateType updateType) {
        _count = count;
        _macro = macro;
        _updateType = updateType;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.MACRO_LIST.writeId(packet);

        packet.put((byte) _updateType.getId());
        packet.putInt(_updateType != MacroUpdateType.LIST ? _macro.getId() : 0x00); // modified, created or deleted macro's id
        packet.put((byte) _count); // count of Macros
        packet.put((byte) (_macro != null ? 1 : 0)); // unknown

        if ((_macro != null) && (_updateType != MacroUpdateType.DELETE)) {
            packet.putInt(_macro.getId()); // Macro ID
            writeString(_macro.getName(), packet); // Macro Name
            writeString(_macro.getDescr(), packet); // Desc
            writeString(_macro.getAcronym(), packet); // acronym
            packet.putInt(_macro.getIcon()); // icon

            packet.put((byte) _macro.getCommands().size()); // count

            int i = 1;
            for (MacroCmd cmd : _macro.getCommands()) {
                packet.put((byte) i++); // command count
                packet.put((byte) cmd.getType().ordinal()); // type 1 = skill, 3 = action, 4 = shortcut
                packet.putInt(cmd.getD1()); // skill id
                packet.put((byte) cmd.getD2()); // shortcut id
                writeString(cmd.getCmd(), packet); // command name
            }
        }
    }
}
