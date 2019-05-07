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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHUTTLE_INFO.writeId(packet);

        packet.putInt(_shuttle.getObjectId());
        packet.putInt(_shuttle.getX());
        packet.putInt(_shuttle.getY());
        packet.putInt(_shuttle.getZ());
        packet.putInt(_shuttle.getHeading());
        packet.putInt(_shuttle.getId());
        packet.putInt(_stops.size());
        for (L2ShuttleStop stop : _stops) {
            packet.putInt(stop.getId());
            for (Location loc : stop.getDimensions()) {
                packet.putInt(loc.getX());
                packet.putInt(loc.getY());
                packet.putInt(loc.getZ());
            }
            packet.putInt(stop.isDoorOpen() ? 0x01 : 0x00);
            packet.putInt(stop.hasDoorChanged() ? 0x01 : 0x00);
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 33 + _stops.size() * 12 + _stops.stream().mapToInt(stop -> stop.getDimensions().size()).sum() * 12;
    }
}
