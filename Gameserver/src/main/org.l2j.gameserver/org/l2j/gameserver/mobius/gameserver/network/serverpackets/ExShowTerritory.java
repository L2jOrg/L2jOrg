package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Note: <b>There is known issue with this packet, it cannot be removed unless game client is restarted!</b>
 *
 * @author UnAfraid
 */
public class ExShowTerritory extends IClientOutgoingPacket {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_TERRITORY.writeId(packet);

        packet.putInt(_vertices.size());
        packet.putInt(_minZ);
        packet.putInt(_maxZ);
        for (ILocational loc : _vertices) {
            packet.putInt(loc.getX());
            packet.putInt(loc.getY());
        }
    }
}
