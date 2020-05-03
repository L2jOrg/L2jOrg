package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.MacroUpdateType;
import org.l2j.gameserver.model.Macro;
import org.l2j.gameserver.model.MacroCmd;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import static java.util.Objects.nonNull;

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

        writeByte(_updateType.getId());
        writeInt(_updateType != MacroUpdateType.LIST ? _macro.getId() : 0x00); // modified, created or deleted macro's id
        writeByte(_count); // count of Macros
        writeByte(nonNull(_macro)); // unknown

        if ((_macro != null) && (_updateType != MacroUpdateType.DELETE)) {
            writeInt(_macro.getId()); // Macro ID
            writeString(_macro.getName()); // Macmacros.comro Name
            writeString(_macro.getDescr()); // Desc
            writeString(_macro.getAcronym()); // acronym
            writeInt(_macro.getIcon()); // icon

            writeByte(_macro.getCommands().size()); // count

            int i = 1;
            for (MacroCmd cmd : _macro.getCommands()) {
                writeByte( i++); // command count
                writeByte(cmd.getType().ordinal()); // type 1 = skill, 3 = action, 4 = shortcut
                writeInt(cmd.getD1()); // skill id
                writeByte(cmd.getD2()); // shortcut id
                writeString(cmd.getCmd()); // command name
            }
        }
    }
}
