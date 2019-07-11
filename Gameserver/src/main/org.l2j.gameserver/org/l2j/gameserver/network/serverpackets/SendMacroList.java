package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.MacroUpdateType;
import org.l2j.gameserver.model.Macro;
import org.l2j.gameserver.model.MacroCmd;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class SendMacroList extends ServerPacket {
    private final int _count;
    private final Macro _macro;
    private final MacroUpdateType _updateType;

    public SendMacroList(int count, Macro macro, MacroUpdateType updateType) {
        _count = count;
        _macro = macro;
        _updateType = updateType;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.MACRO_LIST);

        writeByte((byte) _updateType.getId());
        writeInt(_updateType != MacroUpdateType.LIST ? _macro.getId() : 0x00); // modified, created or deleted macro's id
        writeByte((byte) _count); // count of Macros
        writeByte((byte) (_macro != null ? 1 : 0)); // unknown

        if ((_macro != null) && (_updateType != MacroUpdateType.DELETE)) {
            writeInt(_macro.getId()); // Macro ID
            writeString(_macro.getName()); // Macro Name
            writeString(_macro.getDescr()); // Desc
            writeString(_macro.getAcronym()); // acronym
            writeInt(_macro.getIcon()); // icon

            writeByte((byte) _macro.getCommands().size()); // count

            int i = 1;
            for (MacroCmd cmd : _macro.getCommands()) {
                writeByte((byte) i++); // command count
                writeByte((byte) cmd.getType().ordinal()); // type 1 = skill, 3 = action, 4 = shortcut
                writeInt(cmd.getD1()); // skill id
                writeByte((byte) cmd.getD2()); // shortcut id
                writeString(cmd.getCmd()); // command name
            }
        }
    }
}
