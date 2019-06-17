package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

/**
 * Note: <b>There is known issue with this packet, it cannot be removed unless game client is restarted!</b>
 *
 * @author UnAfraid
 */
public class ExShowTerritory extends ServerPacket {
    private final int _minZ;
    private final int _maxZ;
    private final List<ILocational> _vertices = new ArrayList<>();

    public ExShowTerritory(int minZ, int maxZ) {
        _minZ = minZ;
        _maxZ = maxZ;
    }

    public void addVertice(ILocational loc) {
        _vertices.add(loc);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_SHOW_TERRITORY);

        writeInt(_vertices.size());
        writeInt(_minZ);
        writeInt(_maxZ);
        for (ILocational loc : _vertices) {
            writeInt(loc.getX());
            writeInt(loc.getY());
        }
    }

}
