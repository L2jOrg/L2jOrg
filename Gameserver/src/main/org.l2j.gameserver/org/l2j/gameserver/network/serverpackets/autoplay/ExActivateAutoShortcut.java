package org.l2j.gameserver.network.serverpackets.autoplay;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExActivateAutoShortcut extends ServerPacket {

    private final int room;
    private final boolean activate;

    public ExActivateAutoShortcut(int room, boolean activate) {
        this.room = room;
        this.activate = activate;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_ACTIVATE_AUTO_SHORTCUT);
        writeShort(room);
        writeByte(activate);
    }
}
