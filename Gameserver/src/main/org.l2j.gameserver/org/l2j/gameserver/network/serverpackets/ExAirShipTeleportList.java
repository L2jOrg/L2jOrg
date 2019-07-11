package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.VehiclePathPoint;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class ExAirShipTeleportList extends ServerPacket {
    private final int _dockId;
    private final VehiclePathPoint[][] _teleports;
    private final int[] _fuelConsumption;

    public ExAirShipTeleportList(int dockId, VehiclePathPoint[][] teleports, int[] fuelConsumption) {
        _dockId = dockId;
        _teleports = teleports;
        _fuelConsumption = fuelConsumption;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_AIR_SHIP_TELEPORT_LIST);

        writeInt(_dockId);
        if (_teleports != null) {
            writeInt(_teleports.length);

            for (int i = 0; i < _teleports.length; i++) {
                writeInt(i - 1);
                writeInt(_fuelConsumption[i]);
                final VehiclePathPoint[] path = _teleports[i];
                final VehiclePathPoint dst = path[path.length - 1];
                writeInt(dst.getX());
                writeInt(dst.getY());
                writeInt(dst.getZ());
            }
        } else {
            writeInt(0);
        }
    }

}
