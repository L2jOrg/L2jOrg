package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * This packet shows the mouse click particle for 30 seconds on every location.
 *
 * @author NosBit
 */
public final class ExShowTrace extends IClientOutgoingPacket {
    private final List<Location> _locations = new ArrayList<>();

    public void addLocation(int x, int y, int z) {
        _locations.add(new Location(x, y, z));
    }

    public void addLocation(ILocational loc) {
        addLocation(loc.getX(), loc.getY(), loc.getZ());
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_SHOW_TRACE);

        writeShort((short) 0); // type broken in H5
        writeInt(0); // time broken in H5
        writeShort((short) _locations.size());
        for (Location loc : _locations) {
            writeInt(loc.getX());
            writeInt(loc.getY());
            writeInt(loc.getZ());
        }
    }

}
