package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.L2ShuttleInstance;
import org.l2j.gameserver.model.shuttle.L2ShuttleStop;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author UnAfraid
 */
public class ExShuttleInfo extends IClientOutgoingPacket {
    private final L2ShuttleInstance _shuttle;
    private final List<L2ShuttleStop> _stops;

    public ExShuttleInfo(L2ShuttleInstance shuttle) {
        _shuttle = shuttle;
        _stops = shuttle.getStops();
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_SHUTTLE_INFO);

        writeInt(_shuttle.getObjectId());
        writeInt(_shuttle.getX());
        writeInt(_shuttle.getY());
        writeInt(_shuttle.getZ());
        writeInt(_shuttle.getHeading());
        writeInt(_shuttle.getId());
        writeInt(_stops.size());
        for (L2ShuttleStop stop : _stops) {
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
