package org.l2j.gameserver.network.serverpackets.pvpbook;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExNewPk extends ServerPacket {

    private final Player killer;

    public ExNewPk(Player killer) {
        this.killer = killer;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_PVPBOOK_NEW_PK);
        writeSizedString(killer.getName());
    }
}
