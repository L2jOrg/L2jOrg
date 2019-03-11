package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.FenceState;
import org.l2j.gameserver.model.actor.instance.L2FenceInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author HoridoJoho / FBIagent
 */
public class ExColosseumFenceInfo extends IClientOutgoingPacket {
    private final int _objId;
    private final int _x;
    private final int _y;
    private final int _z;
    private final int _width;
    private final int _length;
    private final int _clientState;

    public ExColosseumFenceInfo(L2FenceInstance fence) {
        this(fence.getObjectId(), fence.getX(), fence.getY(), fence.getZ(), fence.getWidth(), fence.getLength(), fence.getState());
    }

    public ExColosseumFenceInfo(int objId, double x, double y, double z, int width, int length, FenceState state) {
        _objId = objId;
        _x = (int) x;
        _y = (int) y;
        _z = (int) z;
        _width = width;
        _length = length;
        _clientState = state.getClientId();
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_COLOSSEUM_FENCE_INFO.writeId(packet);

        packet.putInt(_objId);
        packet.putInt(_clientState);
        packet.putInt(_x);
        packet.putInt(_y);
        packet.putInt(_z);
        packet.putInt(_width);
        packet.putInt(_length);
    }
}