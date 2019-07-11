package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExAdenaInvenCount extends ServerPacket {
    private final Player _activeChar;

    public ExAdenaInvenCount(Player cha) {
        _activeChar = cha;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_ADENA_INVEN_COUNT);

        writeLong(_activeChar.getAdena());
        writeShort((short) _activeChar.getInventory().getSize());
    }

}