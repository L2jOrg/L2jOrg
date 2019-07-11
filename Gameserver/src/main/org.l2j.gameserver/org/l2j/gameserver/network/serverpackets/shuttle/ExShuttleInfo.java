package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.model.shuttle.ShuttleStop;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.List;

/**
 * @author UnAfraid
 */
public class ExShuttleInfo extends ServerPacket {
    private final Shuttle _shuttle;
    private final List<ShuttleStop> _stops;

    public ExShuttleInfo(Shuttle shuttle) {
        _shuttle = shuttle;
        _stops = shuttle.getStops();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHUTTLE_INFO);

        writeInt(_shuttle.getObjectId());
        writeInt(_shuttle.getX());
        writeInt(_shuttle.getY());
        writeInt(_shuttle.getZ());
        writeInt(_shuttle.getHeading());
        writeInt(_shuttle.getId());
        writeInt(_stops.size());
        for (ShuttleStop stop : _stops) {
            writeInt(stop.getId());
            for (Location loc : stop.getDimensions()) {
                writeInt(loc.getX());
                writeInt(loc.getY());
                writeInt(loc.getZ());
            }
            writeInt(stop.isDoorOpen() ? 0x01 : 0x00);
            writeInt(stop.hasDoorChanged() ? 0x01 : 0x00);
        }
    }

}
