package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.VehiclePathPoint;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

public class ExAirShipTeleportList extends IClientOutgoingPacket {
    private final int _dockId;
    private final VehiclePathPoint[][] _teleports;
    private final int[] _fuelConsumption;

    public ExAirShipTeleportList(int dockId, VehiclePathPoint[][] teleports, int[] fuelConsumption) {
        _dockId = dockId;
        _teleports = teleports;
        _fuelConsumption = fuelConsumption;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_AIR_SHIP_TELEPORT_LIST.writeId(packet);

        packet.putInt(_dockId);
        if (_teleports != null) {
            packet.putInt(_teleports.length);

            for (int i = 0; i < _teleports.length; i++) {
                packet.putInt(i - 1);
                packet.putInt(_fuelConsumption[i]);
                final VehiclePathPoint[] path = _teleports[i];
                final VehiclePathPoint dst = path[path.length - 1];
                packet.putInt(dst.getX());
                packet.putInt(dst.getY());
                packet.putInt(dst.getZ());
            }
        } else {
            packet.putInt(0);
        }
    }
}
