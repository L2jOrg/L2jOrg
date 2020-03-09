package org.l2j.gameserver.network.serverpackets.pvpbook;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExKillerLocation extends ServerPacket {

    private final Player killer;

    public ExKillerLocation(Player killer) {
        this.killer = killer;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_PVPBOOK_KILLER_LOCATION);

        writeSizedString(killer.getName());

        var location = killer.getLocation();
        writeInt(location.getX());
        writeInt(location.getY());
        writeInt(location.getZ());

    }
}
