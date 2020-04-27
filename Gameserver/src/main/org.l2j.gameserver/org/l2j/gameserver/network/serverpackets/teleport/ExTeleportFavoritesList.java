package org.l2j.gameserver.network.serverpackets.teleport;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author joeAlisson
 */
public class ExTeleportFavoritesList extends ServerPacket {

    private final boolean open;

    public ExTeleportFavoritesList(boolean open) {
        this.open = open;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_TELEPORT_FAVORITES_LIST);
        writeByte(open);
        final var teleports = client.getPlayer().getTeleportFavorites();
        writeInt(teleports.size());
        teleports.forEach(this::writeInt);

    }
}
